function mostrarTela(id) {
    document.querySelectorAll('.tela').forEach(div => div.classList.remove('ativa'));
    document.getElementById(id).classList.add('ativa');
}

let qrTimeout = null;
let contadorInterval = null;
const tempoTotal = 60;

function abrirQRCode() {
    document.getElementById('qrModal').style.display = 'flex';

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



