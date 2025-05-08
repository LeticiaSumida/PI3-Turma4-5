package br.edu.puc.superid

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MessageDialog(
    type: MessageType,
    titulo: String,
    mensagem: String,
    caminhoBotao1: () -> Unit,
    caminhoBotao2: () -> Unit,
    textoBotao1: String,
    textoBotao2: String,
    onDismiss: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = titulo)
                Spacer(modifier = Modifier.height(8.dp))
                val imageRes = when (type) {
                    MessageType.SUCCESS -> R.drawable.check
                    MessageType.ERROR -> R.drawable.erro
                    MessageType.EMAIL -> R.drawable.email
                }
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = type.name,
                    modifier = Modifier.size(100.dp)
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = mensagem,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Button(
                    onClick = caminhoBotao1,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff000000)
                    ),
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = textoBotao1)
                }
                Button(
                    onClick = caminhoBotao2,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff000000)
                    ),
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(text = textoBotao2)
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

// Enum para representar os tipos de mensagem
enum class MessageType {
    SUCCESS,
    ERROR,
    EMAIL
}

@Preview(showBackground = true)
@Composable
fun PreviewMessageDialogSuccess() {
    MessageDialog(
        type = MessageType.EMAIL,
        titulo = "Cadastro realizado com sucesso !!",
        mensagem = "Enviamos um email de verificação para você \n" +
        "Verifique sua caixa de entrada e clique no link para validar sua conta.",
        caminhoBotao1 = {},
        caminhoBotao2 = {},
        textoBotao1 = "Ir para o login",
        textoBotao2 = "Realizar outro cadastro"
    )
}
