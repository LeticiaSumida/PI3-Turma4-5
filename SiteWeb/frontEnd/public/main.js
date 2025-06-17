// firebaseConfig
const firebaseConfig = {
  apiKey: "AIzaSyCYDMIvrsYn8kl3UMY3CbqTUYGd8wYYgqU",
  authDomain: "superid-d1bf3.firebaseapp.com",
  projectId: "superid-d1bf3",
  storageBucket: "superid-d1bf3.appspot.com",
  messagingSenderId: "230927964369",
  appId: "1:230927964369:web:f16acc1fb1c89bf52d4df0",
  measurementId: "G-LE6XDTV9GK"
};


// Inicializa Firebase
firebase.initializeApp(firebaseConfig);
const db = firebase.firestore();

async function getApiKey() {
  const docRef = db.collection('partners').doc('superid-d1bf3.web.app');
  const docSnap = await docRef.get();
  const apiKey = docSnap.get('apiKey'); // ou: docSnap.data().apiKey
  return apiKey
}

function gerarStringAleatoria(tamanho) {
  const caracteres = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let resultado = '';
  for (let i = 0; i < tamanho; i++) {
    resultado += caracteres.charAt(Math.floor(Math.random() * caracteres.length));
  }
  return resultado;
}

let stringAleatoria = ''; // mover para escopo global

function gerarQRCode() {
  const modal = document.getElementById('qrModal');
  const qrContainer = document.getElementById('qrcode');
  const statusTexto = document.getElementById('statusTexto');

  qrContainer.innerHTML = '';
  statusTexto.textContent = 'Gerando QR Code...';

  modal.style.display = 'block';

  stringAleatoria = gerarStringAleatoria(256); // agora só é gerada aqui

  new QRCode(qrContainer, {
    text: stringAleatoria,
    width: 200,
    height: 200,
    colorDark: "#000000",
    colorLight: "#ffffff",
    correctLevel: QRCode.CorrectLevel.H
  });

  statusTexto.textContent = 'Escaneie com o app SuperID';

  // chama getLogin apenas após gerar QR
  getLogin();
  iniciarTimerExclusao(); 
  verificarLoginPeriodicamente();
}

function cancelarQRCode() {
  const modal = document.getElementById('qrModal');
  modal.style.display = 'none';
}

async function getLogin() {
   
  try {
    const snapshot = await db.collection('login').get();
    const loginList = snapshot.docs.map(doc => doc.data());
    console.log("Lista de login:", loginList);
    const apiKey = await getApiKey();
    const data = {
      apiKey: apiKey,
      loggedInAt: ''
    };

    const res = await db.collection('login').doc(stringAleatoria).set(data);

    console.log('Added document with ID: ', stringAleatoria);
    return loginList;
  } catch (error) {
    console.error("Erro ao buscar logins:", error);
  }
}

let contadorInterval; // precisa estar no escopo global

function iniciarTimerExclusao() {
  const contador = document.getElementById("contador");
  let segundosRestantes = 60;

  contador.textContent = `Expira em ${segundosRestantes}s`;

  contadorInterval = setInterval(() => {
    segundosRestantes--;
    if (segundosRestantes >= 0) {
      contador.textContent = `Expira em ${segundosRestantes}s`;
    }
  }, 1000);

  // Espera 20 segundos e faz a verificação
  setTimeout(async () => {
    clearInterval(contadorInterval); // para o contador

    try {
      const docRef = db.collection('login').doc(stringAleatoria);
      const docSnap = await docRef.get();

      if (docSnap.exists) {
        const data = docSnap.data();
        if (!data.loggedInAt) {
          await docRef.delete();
          console.log('Documento excluído por inatividade:', stringAleatoria);
          cancelarQRCode();
        } else {
          console.log('Documento foi atualizado, não será excluído.');
          window.location.href = "loginRealizado.html";
        }
      }
    } catch (error) {
      console.error("Erro ao verificar/excluir documento:", error);
    }
  }, 60000); // 20 segundos
}


let loginCheckInterval; // global para poder limpar depois

function verificarLoginPeriodicamente() {
  loginCheckInterval = setInterval(async () => {
    try {
      const docRef = db.collection('login').doc(stringAleatoria);
      const docSnap = await docRef.get();

      if (docSnap.exists) {
        const data = docSnap.data();

        if (data.loggedInAt) {
          console.log('Login realizado:', stringAleatoria);
          clearInterval(loginCheckInterval); // Para de verificar
          window.location.href = "loginRealizado.html";
          window.alert("Login realizado com sucesso!")
        } else {
          console.log('Ainda aguardando login:', stringAleatoria);
        }
      }
    } catch (error) {
      console.error("Erro ao verificar login:", error);
    }
  }, 3000); // verifica a cada 3 segundos
}