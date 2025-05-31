"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || (function () {
    var ownKeys = function(o) {
        ownKeys = Object.getOwnPropertyNames || function (o) {
            var ar = [];
            for (var k in o) if (Object.prototype.hasOwnProperty.call(o, k)) ar[ar.length] = k;
            return ar;
        };
        return ownKeys(o);
    };
    return function (mod) {
        if (mod && mod.__esModule) return mod;
        var result = {};
        if (mod != null) for (var k = ownKeys(mod), i = 0; i < k.length; i++) if (k[i] !== "default") __createBinding(result, mod, k[i]);
        __setModuleDefault(result, mod);
        return result;
    };
})();
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.getLoginStatus = exports.confirmLogin = exports.performAuth = void 0;
const functions = __importStar(require("firebase-functions"));
const admin = __importStar(require("firebase-admin"));
const qrcode_1 = __importDefault(require("qrcode"));
const crypto = __importStar(require("crypto"));
admin.initializeApp();
const db = admin.firestore();
const TOKEN_EXPIRATION = 180; // segundos
const MAX_ATTEMPTS = 3;
/**
 * Gerar token aleatório seguro
 */
function generateToken(length) {
    return crypto.randomBytes(length).toString("hex").slice(0, length);
}
/**
 *  performAuth
 */
exports.performAuth = functions.https.onCall(async (data) => {
    const { apiKey, siteUrl } = data;
    if (!apiKey || !siteUrl) {
        throw new functions.https.HttpsError("invalid-argument", "apiKey e siteUrl são obrigatórios.");
    }
    if (!/^[a-z0-9\-\.]+\.[a-z]{2,}$/.test(siteUrl)) {
        throw new functions.https.HttpsError("invalid-argument", "siteUrl inválido. Deve ser um domínio válido.");
    }
    const partnerRef = db.collection("partners").doc(siteUrl);
    const partnerSnap = await partnerRef.get();
    if (!partnerSnap.exists) {
        throw new functions.https.HttpsError("permission-denied", "Parceiro não cadastrado.");
    }
    const partnerData = partnerSnap.data();
    if ((partnerData === null || partnerData === void 0 ? void 0 : partnerData.apiKey) !== apiKey) {
        throw new functions.https.HttpsError("permission-denied", "API Key inválida.");
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
    const qrCodeDataURL = await qrcode_1.default.toDataURL(loginToken);
    return {
        qrCodeBase64: qrCodeDataURL,
        loginToken,
    };
});
/**
 *  confirmLogin
 */
exports.confirmLogin = functions.https.onCall(async (data, context) => {
    var _a;
    const { loginToken } = data;
    const uid = (_a = context.auth) === null || _a === void 0 ? void 0 : _a.uid;
    if (!uid) {
        throw new functions.https.HttpsError("unauthenticated", "Usuário não autenticado.");
    }
    if (!loginToken) {
        throw new functions.https.HttpsError("invalid-argument", "loginToken é obrigatório.");
    }
    const loginRef = db.collection("login").doc(loginToken);
    const loginSnap = await loginRef.get();
    if (!loginSnap.exists) {
        throw new functions.https.HttpsError("not-found", "Token inválido ou expirado.");
    }
    const loginData = loginSnap.data();
    const now = admin.firestore.Timestamp.now();
    const diffSeconds = now.seconds - loginData.createdAt.seconds;
    if (diffSeconds > TOKEN_EXPIRATION) {
        await loginRef.delete();
        throw new functions.https.HttpsError("deadline-exceeded", "Token expirado.");
    }
    await loginRef.update({
        user: uid,
        confirmedAt: now,
    });
    return { message: "Login confirmado com sucesso." };
});
/**
 *  getLoginStatus
 */
exports.getLoginStatus = functions.https.onCall(async (data) => {
    const { loginToken } = data;
    if (!loginToken) {
        throw new functions.https.HttpsError("invalid-argument", "loginToken é obrigatório.");
    }
    const loginRef = db.collection("login").doc(loginToken);
    const loginSnap = await loginRef.get();
    if (!loginSnap.exists) {
        throw new functions.https.HttpsError("not-found", "Token inválido ou expirado.");
    }
    const loginData = loginSnap.data();
    const now = admin.firestore.Timestamp.now();
    const diffSeconds = now.seconds - loginData.createdAt.seconds;
    if (diffSeconds > TOKEN_EXPIRATION) {
        await loginRef.delete();
        throw new functions.https.HttpsError("deadline-exceeded", "Token expirado.");
    }
    const attempts = ((loginData === null || loginData === void 0 ? void 0 : loginData.attempts) || 0) + 1;
    if (attempts >= MAX_ATTEMPTS) {
        await loginRef.delete();
        throw new functions.https.HttpsError("resource-exhausted", "Número máximo de tentativas excedido.");
    }
    await loginRef.update({ attempts });
    if (loginData === null || loginData === void 0 ? void 0 : loginData.user) {
        await loginRef.delete();
        return {
            status: "authenticated",
            user: loginData.user,
        };
    }
    else {
        return {
            status: "pending",
        };
    }
});
//# sourceMappingURL=index.js.map