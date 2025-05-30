function mostrarTela(id) {
    document.querySelectorAll('.tela').forEach(div => div.classList.remove('ativa'));
    document.getElementById(id).classList.add('ativa');
}

let qrTimeout = null;
let contadorInterval = null;
const tempoTotal = 60;

let loginToken = null; 
let verificarInterval = null;

async function abrirQRCode() {
    document.getElementById('qrModal').style.display = 'flex';

    
    if (qrTimeout) clearTimeout(qrTimeout);
    if (contadorInterval) clearInterval(contadorInterval);
    if (verificarInterval) clearInterval(verificarInterval);

    let tempoRestante = tempoTotal;
    atualizarContador(tempoRestante);

    try {
        const resposta = await fetch('https://us-central1-SEU-PROJETO.cloudfunctions.net/performAuth', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                apiKey: 'SUA_API_KEY_AQUI',
                siteUrl: 'www.seusiteparceiro.com.br'
            })
        });

        const dados = await resposta.json();

        if (resposta.ok) {
            loginToken = dados.loginToken;

          
            document.getElementById('qrcode').innerHTML = `
                <img src="${dados.qrCodeImage}" alt="QR Code">
            `;


            iniciarVerificacao();

        } else {
            alert('Erro ao gerar QR Code: ' + dados.error);
            fecharQRCode();
            return;
        }
    } catch (erro) {
        alert('Erro na comunicação com servidor');
        fecharQRCode();
        return;
    }


    contadorInterval = setInterval(() => {
        tempoRestante--;
        atualizarContador(tempoRestante);

        if (tempoRestante <= 0) {
            clearInterval(contadorInterval);
        }
    }, 1000);


    qrTimeout = setTimeout(() => {
        fecharQRCode();
        alert('Login não realizado. Código expirou.');
        qrTimeout = null;
        clearInterval(contadorInterval);
        atualizarContador('');
    }, tempoTotal * 1000);
}

function atualizarContador(texto) {
    document.getElementById('contador').textContent =
        typeof texto === 'number' ? `Autenticando em ${texto}s...` : texto;
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
    if (verificarInterval) {
        clearInterval(verificarInterval);
        verificarInterval = null;
    }

    document.getElementById('qrcode').innerHTML = '';
    atualizarContador('');
    loginToken = null;
}

function cancelarQRCode() {
    fecharQRCode();
}

function iniciarVerificacao() {
    verificarInterval = setInterval(async () => {
        if (!loginToken) return;

        try {
            const resposta = await fetch('https://us-central1-SEU-PROJETO.cloudfunctions.net/getLoginStatus', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ loginToken })
            });

            const dados = await resposta.json();

            if (resposta.ok) {
                if (dados.status === 'authenticated') {
                    clearInterval(verificarInterval);
                    alert(`Login realizado com sucesso! UID: ${dados.uid}`);
                    fecharQRCode();

                }
            } else {
                console.log('Erro ao verificar login:', dados.error);
            }
        } catch (erro) {
            console.log('Erro na requisição de status');
        }
    }, 2000);
}
