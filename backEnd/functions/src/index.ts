import * as functions from "firebase-functions";
import * as admin from "firebase-admin";
import { v4 as uuidv4 } from "uuid";
import * as QRCode from "qrcode";

admin.initializeApp();
const db = admin.firestore();

export const performAuth = functions.https.onRequest(async (req, res) => {
  try {
    const { apiKey, siteUrl } = req.body;

    if (!apiKey || !siteUrl) {
      res.status(400).json({ error: "apiKey e siteUrl são obrigatórios" });
      return;
    }

    const partnerRef = db.collection("partners").doc(siteUrl);
    const partnerSnap = await partnerRef.get();

    if (!partnerSnap.exists) {
      res.status(403).json({ error: "Site não autorizado" });
      return;
    }

    const partnerData = partnerSnap.data();

    if (partnerData?.apiKey !== apiKey) {
      res.status(403).json({ error: "API Key inválida" });
      return;
    }

    const loginToken =
      uuidv4().replace(/-/g, "") + uuidv4().replace(/-/g, "") +
      uuidv4().replace(/-/g, "") + uuidv4().replace(/-/g, "");

    await db.collection("login").doc(loginToken).set({
      apiKey,
      siteUrl,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      loginToken,
      status: "pending",
    });

    const qrCodeDataURL = await QRCode.toDataURL(loginToken);

    res.status(200).json({
      loginToken,
      qrCodeImage: qrCodeDataURL,
    });

  } catch (error) {
    console.error("Erro no performAuth:", error);
    res.status(500).json({ error: "Erro interno no servidor" });
  }
});
