package br.edu.puc.superid

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

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
                    else -> R.drawable.check
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
enum class MessageType {
    SUCCESS,
    ERROR,
    EMAIL,
    PASSWORD
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

@Composable
fun ModalTextField(type: MessageType,
                   titulo: String,
                   mensagem: String,

                   caminhoBotao2: () -> Unit,
                   textoBotao1: String,
                   textoBotao2: String,
                   onDismiss: () -> Unit = {}) {

    var (email2, setEmail) = androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf("")
    }
    var (password, setPassword) = androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf("")
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(text = titulo)
                Spacer(modifier = Modifier.height(8.dp))
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
                when (type) {
                    MessageType.PASSWORD -> {
                        TextField(
                            modifier = Modifier
                                .padding(vertical = 10.dp, horizontal = 12.dp)
                                .fillMaxWidth(),
                            value = password,
                            onValueChange = setPassword,
                            label = { Text("Senha Mestre") } ,
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }
                    MessageType.EMAIL ->{
                        TextField(
                            modifier = Modifier
                                .padding(vertical = 10.dp, horizontal = 12.dp)
                                .fillMaxWidth(),
                            value = email2,
                            onValueChange = setEmail,
                            label = { Text("Email") }
                        )
                        Button(
                            onClick = { esqueciSenha(email2) },
                            colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xff000000)
                            ),
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text(text = textoBotao1)
                        }
                    }


                    else -> {Text("")}
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

fun esqueciSenha(email:String){
    val TAG = "EsqueciSenha"
    Firebase.auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG,"Email enviado.")

            }
            else{
                Log.w(TAG,"Email inválido")
            }
        }
}