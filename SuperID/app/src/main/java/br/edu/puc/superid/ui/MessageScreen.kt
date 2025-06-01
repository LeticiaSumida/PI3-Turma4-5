package br.edu.puc.superid.ui

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.puc.superid.R
import br.edu.puc.superid.SignInWithoutPass
import br.edu.puc.superid.ui.theme.branco
import br.edu.puc.superid.ui.theme.cinzaclaro
import br.edu.puc.superid.ui.theme.cinzaescuro
import br.edu.puc.superid.ui.theme.preto
import br.edu.puc.superid.ui.theme.roxo
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import androidx.compose.material3.IconButton


private val TAG = "MODALSCREEN"

//Definie os tipos de mensagem que podem ser exibidos no modal
enum class MessageType {
    SUCCESS,
    ERROR,
    EMAIL,
    PASSWORD
}

// Composable de modal de aviso de acordo com tipo
@Composable
fun MessageDialog(
    type: MessageType,
    titulo: String,
    mensagem: String,
    caminhoBotao: () -> Unit,
    textoBotao: String,
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
                    else -> R.drawable.imagem_default
                }

                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = type.name,
                    modifier = Modifier.size(100.dp)
                )

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
                Button(
                    onClick = caminhoBotao,
                    colors = ButtonDefaults.buttonColors(containerColor = branco),
                    shape = RoundedCornerShape(16),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 12.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16),
                            ambientColor = cinzaclaro, // Roxo mais claro para a sombra
                            spotColor = cinzaescuro

                        )
                )

                {
                    Text(text = textoBotao, color = preto)
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewMessageDialogSuccess() {
    MessageDialog(
        type = MessageType.EMAIL,
        titulo = "Cadastro realizado com sucesso !!",
        mensagem = "Enviamos um email de verificação para você \n" +
                "Verifique sua caixa de entrada e clique no link para validar sua conta.",
        caminhoBotao = {},
        textoBotao = "Ir para o login",
    )
}
// Composable para exibir Modal com TextField
@Composable
fun ModalTextField(
    type: MessageType,
    titulo: String,
    mensagem: String,
    caminhoBotao2: () -> Unit,
    textoBotao1: String,
    textoBotao2: String,
    onDismiss: () -> Unit = {}
) {
    var context = LocalContext.current
    var erroBoolean by remember { mutableStateOf(false) }
    var sucessBoolean by remember { mutableStateOf(false) }
    var mensagemErro = remember { mutableStateOf<String?>(null) }
    var shouldNavigate by remember { mutableStateOf(false) }
    var (email2, setEmail) = androidx.compose.runtime.remember { mutableStateOf("") }
    var (password, setPassword) = androidx.compose.runtime.remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

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
                        var senhaVisivel by remember { mutableStateOf(false) }

                        TextField(
                            modifier = Modifier
                                .padding(vertical = 10.dp, horizontal = 12.dp)
                                .fillMaxWidth(),
                            value = password,
                            onValueChange = setPassword,
                            label = { Text("Senha Mestre",color = Color.Black) },
                            visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { senhaVisivel = !senhaVisivel }) {
                                    Icon(
                                        imageVector = if (senhaVisivel) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = if (senhaVisivel) "Ocultar senha" else "Mostrar senha"
                                    )
                                }
                            }
                        )

                        mensagemErro.value?.let { mensagem ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Aviso",
                                    tint = Color.Red,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = mensagem,
                                    color = Color.Red,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }

                        // Botão para reautenticar com senha mestre
                        Button(
                            onClick = {
                                val user = FirebaseAuth.getInstance().currentUser
                                val email = user?.email.orEmpty()

                                if (user != null && email.isNotBlank() && password.isNotBlank()) {
                                    val credential =
                                        EmailAuthProvider.getCredential(email, password)

                                    user.reauthenticate(credential)
                                        .addOnCompleteListener { task ->
                                            if (task.isSuccessful) {
                                                mensagemErro.value = null
                                                shouldNavigate = true
                                            } else {
                                                mensagemErro.value = "Senha Mestre Inválida"
                                            }
                                        }
                                } else {
                                    mensagemErro.value = "Preencha o campo acima"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = roxo),
                            shape = RoundedCornerShape(16),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .padding(horizontal = 12.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(16),
                                    ambientColor = cinzaclaro,
                                    spotColor = cinzaescuro
                                )
                        ) {
                            Text(text = textoBotao1)
                        }

                        if (shouldNavigate) {
                            LaunchedEffect(Unit) {
                                context.startActivity(
                                    Intent(
                                        context,
                                        SignInWithoutPass::class.java
                                    )
                                )
                            }
                        }
                    }

                    MessageType.EMAIL -> {
                        TextField(
                            modifier = Modifier
                                .padding(vertical = 10.dp, horizontal = 12.dp)
                                .fillMaxWidth(),
                            value = email2,
                            onValueChange = setEmail,
                            label = { Text("Email", color = Color.Black) }
                        )

                        mensagemErro.value?.let { mensagem ->
                            Text(
                                text = mensagem,
                                color = Color.Red,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .align(Alignment.CenterHorizontally)
                            )
                        }

                        // Botão para enviar email de redefinição de senha
                        Button(
                            onClick = {
                                if (email2.isNotBlank()) {
                                    if (isValidEmail(email2)) {
                                        esqueciSenha(email2) { sucesso, mensagem ->
                                            if (sucesso) {
                                                mensagemErro.value = mensagem
                                                sucessBoolean = true

                                            } else {
                                                mensagemErro.value = mensagem
                                                erroBoolean = true
                                            }
                                        }
                                    } else {
                                        Log.d(TAG, "Formato invalido")
                                        mensagemErro.value = "Formato de Email Inválido"
                                    }
                                } else {
                                    mensagemErro.value = "Preencha o campo acima"
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = roxo),
                            shape = RoundedCornerShape(16),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .padding(horizontal = 12.dp)
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(16),
                                    ambientColor = cinzaclaro, // Roxo mais claro para a sombra
                                    spotColor = cinzaescuro

                                )
                        ) {
                            Text(text = textoBotao1, color = branco)
                        }
                        if (erroBoolean) {
                            MessageDialog(
                                type = MessageType.ERROR,
                                titulo = "Email nao enviado",
                                mensagem = mensagemErro.value.toString(),
                                caminhoBotao = {
                                    erroBoolean = false
                                    onDismiss()
                                               },
                                textoBotao = "Fechar",
                                onDismiss = {
                                    erroBoolean = false
                                    onDismiss()
                                }
                            )
                        }
                        if (sucessBoolean) {
                            MessageDialog(
                                type = MessageType.SUCCESS,
                                titulo = "Email enviado com sucesso",
                                mensagem = mensagemErro.value.toString(),
                                caminhoBotao = {
                                    sucessBoolean = false
                                    onDismiss()
                                               },
                                textoBotao = "Fechar",
                                onDismiss = {
                                    sucessBoolean = false
                                    onDismiss()
                                }
                            )
                        }
                    }

                    else -> {
                        Text("TYPE INVALIDO")
                    }
                }

                Button(
                    onClick = caminhoBotao2,
                    colors = ButtonDefaults.buttonColors(containerColor = branco),
                    shape = RoundedCornerShape(16),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 12.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16),
                            ambientColor = cinzaclaro, // Roxo mais claro para a sombra
                            spotColor = cinzaescuro
                        )
                ) {
                    Text(text = textoBotao2, color = roxo)
                }
            }
        },
        confirmButton = {},
        dismissButton = {}
    )
}
// Função que consulta no Firestore se o email existe e envia um email de redefinição
fun esqueciSenha(
    email: String,
    callback: (Boolean, String) -> Unit
) {

    val db = Firebase.firestore

    db.collection("Usuario")
        .whereEqualTo("email", email)
        .get()
        .addOnSuccessListener { result ->
            if (!result.isEmpty) {
                val document = result.documents[0]
                val emailVerificado = document.getBoolean("emailVerificado") ?: false

                if (emailVerificado) {
                    Firebase.auth.sendPasswordResetEmail(email)
                        .addOnSuccessListener {
                            Log.d(TAG, "Email enviado.")
                            callback(true, "Email de redefinição enviado com sucesso.")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Falha ao enviar email de redefinição", e)
                            callback(false, "Falha ao enviar email de redefinição: ${e.message}")
                        }

                } else {
                    Log.w(TAG, "Email não verificado. Não é possível enviar redefinição.")
                    callback(
                        false,
                        "Email não verificado. Verifique seu email antes de redefinir a senha."
                    )

                }
            } else {
                Log.w(TAG, "Email nao esta no banco") // Result = null
                callback(false, "Email não encontrado no sistema.")

            }
        }
        .addOnFailureListener { e ->
            Log.w(TAG, "Erro ao consultar o banco")
            callback(
                false,
                "Erro ao consultar o banco: ${e.message}"
            )//Nao conseguiu consultar o banco

        }

}
// Função para validar se o formato do email é válido
fun isValidEmail(email: String?): Boolean {
    if (email == null) return false

    val EMAIL_PATTERN =
        "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
    val pattern = Regex(EMAIL_PATTERN)
    return pattern.matches(email)
}
