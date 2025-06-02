import { initializeApp } from "https://www.gstatic.com/firebasejs/10.7.1/firebase-app.js";;
import { 
  getFunctions, 
  httpsCallable 
} from "https://www.gstatic.com/firebasejs/10.7.1/firebase-functions.js";

const firebaseConfig = {
  apiKey: "AIzaSyCYDMIvrsYn8kl3UMY3CbqTUYGd8wYYgqU",
  authDomain: "superid-d1bf3.firebaseapp.com",
  projectId: "superid-d1bf3",
  storageBucket: "superid-d1bf3.firebasestorage.app",
  messagingSenderId: "230927964369",
  appId: "1:230927964369:web:f16acc1fb1c89bf52d4df0",
  measurementId: "G-LE6XDTV9GK"
};


function elemtId(s){ 
  return document.getElementById(s)
}
const app = initializeApp(firebaseConfig);
const functions = getFunctions(app);


async function performAuth() {
  const fn = httpsCallable(functions, "performAuth");
    try {

        const apikey = "Teste do SuperID"
        const result = await fn({ APIkey: apikey });

        const { qrcode, token } = result.data;
        elemtId("qrImg").src = qrcode;
        if(elemtId("qrcode").style.display !== "block"){
          elemtId("qrcode").style.display = "block"
        }
        console.log("Token gerado:", token);
      } catch (err) {
        console.error("Erro na chamada performAuth:", err);
        alert("Falha ao gerar QR: " + err.message);
      }
}

    // 5) Vincula ao clique do botão







// Função para gerar QR Code
async function generateNewQRCode() {
  const fn = httpsCallable(functions, "performAuth");
  try {
    const apikey = 'Teste do SuperID'
    const result = await fn({ APIkey: apikey });

    const { qrcode, token } = result.data;
    elemtId("qrImg").src = qrcode;
    if(elemtId("qrcode").style.display !== "block"){
      elemtId("qrcode").style.display = "block"
    }
    console.log("Token gerado:", token);

    // Iniciar verificação de status
    startStatusPolling(token);

  } catch (err) {
        console.error("Erro na chamada performAuth:", err);
        alert("Falha ao gerar QR: " + err.message);
    }
}

// Função de polling de status
let interval; 
function startStatusPolling(a) {
  const b = httpsCallable(functions, "getLoginStatus");
  
  interval = setInterval(async () => {
    try {
      const result = await b({ loginToken: a });
      
      if (result.data.status === "completed") {
        clearInterval(interval);
        document.getElementById("qrcode").style.display = "none";
        alert(`Usuário autenticado: ${result.data.user?.email}`);
      }
      console.log(result);
    } catch (error) {
      clearInterval(interval);
      console.error("Erro na chamada StartStatus/getLoginStatus:", error);
      generateNewQRCode();
    }
  }, 5000); // 5 segundos
}

// Event Listener para o botão
//    elemtId("authBtn")
//    .addEventListener("click", performAuth);

    elemtId("authBtn").addEventListener("click", () =>
      {
        generateNewQRCode()
      });

document.getElementById("generate-btn")?.addEventListener("click", generateNewQRCode);

