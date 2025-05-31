const firebaseConfig = {
  apiKey: "AIzaSyCYDMIvrsYn8kl3UMY3CbqTUYGd8wYYgqU",
  authDomain: "superid-d1bf3.firebaseapp.com",
  projectId: "superid-d1bf3",
  storageBucket: "superid-d1bf3.appspot.com",
  messagingSenderId: "230927964369",
  appId: "1:230927964369:web:f16acc1fb1c89bf52d4df0",
  measurementId: "G-LE6XDTV9GK"
};

firebase.initializeApp(firebaseConfig);

const auth = firebase.auth();
const firestore = firebase.firestore();


function mostrarTela(id) {
    document.querySelectorAll('.tela').forEach(div => div.classList.remove('ativa'));
    document.getElementById(id).classList.add('ativa');
}

let qrTimeout = null;
let contadorInterval = null;
const tempoTotal = 60;

function abrirQRCode() {
    auth.signInAnonymously()
    .then(userCredential => {
        return userCredential.user.getIdToken();
    })
    .then(token => {
        // Exibe o modal e gera o QR Code com o token
        document.getElementById('qrModal').style.display = 'flex';

        // Limpar QRCode anterior
        document.getElementById("qrcode").innerHTML = "";

        // Gerar QR Code com o token
        new QRCode(document.getElementById("qrcode"), token);

        // Iniciar contadores e timeout conforme seu código atual
        if (qrTimeout) clearTimeout(qrTimeout);
        if (contadorInterval) clearInterval(contadorInterval);

        let tempoRestante = tempoTotal;
        atualizarContador(tempoRestante);

        contadorInterval = setInterval(() => {
          tempoRestante--;
          atualizarContador(tempoRestante);

          if (tempoRestante <= 0) {
            clearInterval(contadorInterval);
          }
        }, 1000);

        qrTimeout = setTimeout(() => {
            fecharQRCode();
            alert('Login não realizado pois o código não foi escaneado');
            qrTimeout = null;
            clearInterval(contadorInterval);
            atualizarContador(''); 
        }, tempoTotal * 1000);

    })
    .catch(error => {
        console.error("Erro ao gerar token:", error);
        alert("Erro ao iniciar autenticação");
    });
}

const admin = require('firebase-admin');
admin.initializeApp({
  credential: admin.credential.applicationDefault()
});
const db = admin.firestore();

async function validarTokenEGravar(token) {
  try {
    const decodedToken = await admin.auth().verifyIdToken(token);
    const uid = decodedToken.uid;

    // Grava no Firestore que o usuário está autenticado
    await db.collection('logins').doc(uid).set({
      loginAt: admin.firestore.FieldValue.serverTimestamp()
    });

    return { success: true, uid };
  } catch (error) {
    return { success: false, error };
  }
}

function atualizarContador(texto) {
    document.getElementById('contador').textContent = typeof texto === 'number' ? `Autenticando em ${texto}s...` : texto;
}

function fecharQRCode() {
    document.getElementById('qrModal').style.display = 'none';

    if (qrTimeout) {
        clearTimeout(qrTimeout);
        qrTimeout = null;
    }
    if (contadorInterval) {
        clearInterval(contadorInterval);
        contadorInterval = null;
    }

    atualizarContador('');
}

function cancelarQRCode() {
    fecharQRCode();

    if (qrTimeout) {
        clearTimeout(qrTimeout);
        qrTimeout = null;
    }
    if (contadorInterval) {
        clearInterval(contadorInterval);
        contadorInterval = null;
    }

    atualizarContador('');
}

var qrcode = new QRCode("qrcode");



