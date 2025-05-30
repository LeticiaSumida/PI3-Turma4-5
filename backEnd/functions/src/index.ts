import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import * as QRCode from "qrcode";
import * as crypto from "crypto";

admin.initializeApp();
const db = admin.firestore();

/**
 * Gerar token aleatório seguro
 */
function generateToken(length: number): string {
  return crypto.randomBytes(length).toString("hex").slice(0, length);
}

/**
 * 🔥 performAuth
 * - Gera QRCode com loginToken
 */
export const performAuth = functions.https.onCall(async (data, context) => {
  const { apiKey, siteUrl } = data;

  if (!apiKey || !siteUrl) {
    throw new functions.https.HttpsError(
      "invalid-argument",
      "apiKey e siteUrl são obrigatórios."
    );
  }

  if (!/^www\.[a-z0-9\-\.]+\.[a-z]{2,}$/.test(siteUrl)) {
    throw new functions.https.HttpsError(
      "invalid-argument",
      "siteUrl inválido. Deve começar com www e sem subdomínios ou barras."
    );
  }

  const partnerRef = db.collection("partners").doc(siteUrl);
  const partnerSnap = await partnerRef.get();

  if (!partnerSnap.exists) {
    throw new functions.https.HttpsError(
      "permission-denied",
      "Parceiro não cadastrado."
    );
  }

  const partnerData = partnerSnap.data();
  if (partnerData?.apiKey !== apiKey) {
    throw new functions.https.HttpsError(
      "permission-denied",
      "API Key inválida."
    );
  }

  const loginToken = generateToken(256);
  const now = admin.firestore.Timestamp.now();

  await db.collection("login").doc(loginToken).set({
    apiKey,
    siteUrl,
    loginToken,
    createdAt: now,
    attempts: 0,
  });

  const qrCodeDataURL = await QRCode.toDataURL(loginToken);

  return {
    qrCodeBase64: qrCodeDataURL,
    loginToken,
  };
});

/**
 * 🔑 confirmLogin
 * - O app SuperID confirma o login
 */
export const confirmLogin = functions.https.onCall(async (data, context) => {
  const { loginToken } = data;

  const uid = context.auth?.uid;
  if (!uid) {
    throw new functions.https.HttpsError(
      "unauthenticated",
      "Usuário não autenticado."
    );
  }

  if (!loginToken) {
    throw new functions.https.HttpsError(
      "invalid-argument",
      "loginToken é obrigatório."
    );
  }

  const loginRef = db.collection("login").doc(loginToken);
  const loginSnap = await loginRef.get();

  if (!loginSnap.exists) {
    throw new functions.https.HttpsError(
      "not-found",
      "Token inválido ou expirado."
    );
  }

  const loginData = loginSnap.data();
  const now = admin.firestore.Timestamp.now();
  const diffSeconds = now.seconds - loginData!.createdAt.seconds;

  if (diffSeconds > 60) {
    await loginRef.delete();
    throw new functions.https.HttpsError(
      "deadline-exceeded",
      "Token expirado."
    );
  }

  await loginRef.update({
    user: uid,
    confirmedAt: now,
  });

  return { message: "Login confirmado com sucesso." };
});

/**
 * 🔍 getLoginStatus
 * - O site verifica se o login foi concluído
 */
export const getLoginStatus = functions.https.onCall(async (data, context) => {
  const { loginToken } = data;

  if (!loginToken) {
    throw new functions.https.HttpsError(
      "invalid-argument",
      "loginToken é obrigatório."
    );
  }

  const loginRef = db.collection("login").doc(loginToken);
  const loginSnap = await loginRef.get();

  if (!loginSnap.exists) {
    throw new functions.https.HttpsError(
      "not-found",
      "Token inválido ou expirado."
    );
  }

  const loginData = loginSnap.data();
  const now = admin.firestore.Timestamp.now();
  const diffSeconds = now.seconds - loginData!.createdAt.seconds;

  if (diffSeconds > 60) {
    await loginRef.delete();
    throw new functions.https.HttpsError(
      "deadline-exceeded",
      "Token expirado."
    );
  }

  const attempts = (loginData?.attempts || 0) + 1;
  if (attempts >= 3) {
    await loginRef.delete();
    throw new functions.https.HttpsError(
      "resource-exhausted",
      "Número máximo de tentativas excedido."
    );
  }

  await loginRef.update({ attempts });

  if (loginData?.user) {
    await loginRef.delete();
    return {
      status: "authenticated",
      user: loginData.user,
    };
  } else {
    return {
      status: "pending",
    };
  }
});
