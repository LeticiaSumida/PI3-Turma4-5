function mostrarTela(id) {
    document.querySelectorAll('.tela').forEach(div => div.classList.remove('ativa'));
    document.getElementById(id).classList.add('ativa');
}

function abrirQRCode() {
    document.getElementById('qrModal').style.display = 'flex';

    // Simula a autenticação no Firebase após 5 segundos
    setTimeout(() => {
        fecharQRCode();
        alert('Login realizado com sucesso via SuperID!');
        mostrarTela('telaLojaLogado');
    }, 5000);
}

function fecharQRCode() {
    document.getElementById('qrModal').style.display = 'none';
}


var qrcode = new QRCode("qrcode");

function makeCode () {    
  var elText = document.getElementById("text");
  
  if (!elText.value) {
    alert("Input a text");
    elText.focus();
    return;
  }
  
  qrcode.makeCode(elText.value);
}

makeCode();

$("#text").
  on("blur", function () {
    makeCode();
  }).
  on("keydown", function (e) {
    if (e.keyCode == 13) {
      makeCode();
    }
  });
