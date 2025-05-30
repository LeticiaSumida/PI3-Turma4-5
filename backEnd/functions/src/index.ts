import { onRequest } from 'firebase-functions/v2/https';
import * as logger from 'firebase-functions/logger';
import * as admin from 'firebase-admin';

admin.initializeApp();

export const generateCustomToken = onRequest(async (req, res) => {
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
    } catch (error: any) {
      if (error.code === 'auth/user-not-found') {
        logger.info('User not found. Creating user...', { uid });

        // Cria o usuário se não existir
        userRecord = await admin.auth().createUser({
          uid: uid
          // Você pode adicionar mais dados aqui, como email, nome, etc.
        });

        logger.info('User created successfully', { uid });
      } else {
        throw error; // Outros erros são propagados
      }
    }

    // Gerar o token personalizado
    const customToken = await admin.auth().createCustomToken(userRecord.uid);

    logger.info('Token generated successfully', { uid });

    res.status(200).json({ token: customToken });
  } catch (error) {
    logger.error('Error generating token', error);
    res.status(500).json({ error: 'Error generating token' });
  }
});
