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
Object.defineProperty(exports, "__esModule", { value: true });
exports.generateCustomToken = void 0;
const https_1 = require("firebase-functions/v2/https");
const logger = __importStar(require("firebase-functions/logger"));
const admin = __importStar(require("firebase-admin"));
admin.initializeApp();
exports.generateCustomToken = (0, https_1.onRequest)(async (req, res) => {
    const uid = req.body.uid;
    logger.info('Request to generate token', { uid });
    if (!uid) {
        res.status(400).json({ error: 'UID is required' });
        return;
    }
    try {
        // Verificar se o usuário existe
        let userRecord;
        try {
            userRecord = await admin.auth().getUser(uid);
            logger.info('User exists', { uid });
        }
        catch (error) {
            if (error.code === 'auth/user-not-found') {
                logger.info('User not found. Creating user...', { uid });
                // Cria o usuário se não existir
                userRecord = await admin.auth().createUser({
                    uid: uid
                    // Você pode adicionar mais dados aqui, como email, nome, etc.
                });
                logger.info('User created successfully', { uid });
            }
            else {
                throw error; // Outros erros são propagados
            }
        }
        // Gerar o token personalizado
        const customToken = await admin.auth().createCustomToken(userRecord.uid);
        logger.info('Token generated successfully', { uid });
        res.status(200).json({ token: customToken });
    }
    catch (error) {
        logger.error('Error generating token', error);
        res.status(500).json({ error: 'Error generating token' });
    }
});
//# sourceMappingURL=index.js.map