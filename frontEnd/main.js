function mostrarTela(id) {
    document.querySelectorAll('.tela').forEach(div => div.classList.remove('ativa'));
    document.getElementById(id).classList.add('ativa');
}

