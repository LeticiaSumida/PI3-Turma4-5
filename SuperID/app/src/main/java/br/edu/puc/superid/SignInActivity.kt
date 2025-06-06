package br.edu.puc.superid

import android.content.Intent
import androidx.compose.ui.text.TextStyle
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.puc.superid.ui.HomePage
import br.edu.puc.superid.ui.MessageType
import br.edu.puc.superid.ui.ModalTextField
import br.edu.puc.superid.ui.theme.SuperIdTheme
import br.edu.puc.superid.ui.theme.branco
import br.edu.puc.superid.ui.theme.cinzaclaro
import br.edu.puc.superid.ui.theme.cinzaescuro
import br.edu.puc.superid.ui.theme.roxo
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

private val TAG = "SignInActivityLOG"

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperIdTheme {
                TelaLogin()
            }
        }
    }

    // Função Composable que define a tela de login
    @Composable
    fun TelaLogin() {
        var email by remember { mutableStateOf("") }
        var senha by remember { mutableStateOf("") }
        var erroMensagem by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }
        var showSuccessDialog by remember { mutableStateOf(false) }
        var esqueciSenhaModal by remember { mutableStateOf(false) }

        val context = LocalContext.current

        Column(
            modifier = Modifier
                .padding(24.dp)
                .padding(top = 50.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo/Cadeado
            Image(
                painter = painterResource(id = R.drawable.cadeado3),
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .padding(start = 40.dp)
            )
            Text(
                "Login",
                modifier = Modifier,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            UnderlineTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email",
            )

            UnderlineTextField(
                value = senha,
                onValueChange = { senha = it },
                label = "Senha Mestre"
            )

            // Mensagem de erro
            if (erroMensagem != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 30.dp, top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Aviso",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = erroMensagem!!,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
            }

            // Indicador de carregamento
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                Button(
                    onClick = {
                        erroMensagem = null
                        if (email.isBlank() || senha.isBlank()) {
                            erroMensagem = "Preencha todos os campos."
                            return@Button
                        }
                        if (senha.length < 6) {
                            erroMensagem = "A senha precisa ter no mínimo 6 caracteres."
                            return@Button
                        }
                        if (!isValidEmail(email)) {
                            erroMensagem = "Formato de email inválido."
                            return@Button
                        }

                        isLoading = true

                        loginAuth(email, senha) { login ->
                            isLoading = false
                            if (login) {
                                showSuccessDialog = true
                            } else {
                                erroMensagem = "Email ou senha incorretos."
                            }
                        }

                        checarVerificacao { verificado, erro ->
                            if (erro != null) {
                                Log.d(TAG, "Erro ao verificar email $erro")
                            } else {
                                if (verificado) {
                                    Log.d(TAG, "Email está verificado")
                                } else {
                                    Log.d(TAG, "Email não está verificado")
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = roxo),
                    shape = RoundedCornerShape(16),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 12.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(16),
                            ambientColor = cinzaclaro,
                            spotColor = cinzaescuro
                        )
                ) {
                    Text("Logar", color = branco)
                }

                Row {
                    Text("Esqueci minha ")
                    Text(
                        "senha",
                        color = roxo,
                        modifier = Modifier.clickable { esqueciSenhaModal = true }
                    )

                    if (esqueciSenhaModal) {
                        ModalTextField(
                            type = MessageType.EMAIL,
                            titulo = "Esqueceu sua senha?",
                            mensagem = "Digite seu email e enviaremos um link para redefinir sua senha mestre",
                            caminhoBotao2 = { esqueciSenhaModal = false },
                            textoBotao1 = "Enviar link de redefinição",
                            textoBotao2 = "Cancelar",
                            onDismiss = { esqueciSenhaModal = false }
                        )
                    }
                }

                Row {
                    Text("Ainda não tem conta? ")
                    Text(
                        "Cadastre-se",
                        color = roxo,
                        modifier = Modifier.clickable {
                            val intent = Intent(context, SignUpActivity::class.java)
                            context.startActivity(intent)
                            (context as? ComponentActivity)?.finish()
                        }
                    )
                }
            }

            // Dialog de sucesso no login
            if (showSuccessDialog) {
                androidx.compose.material3.AlertDialog(
                    onDismissRequest = { showSuccessDialog = false },
                    confirmButton = {},
                    title = null,
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Login realizado com sucesso!",
                                color = branco,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 24.dp),
                                lineHeight = 36.sp,
                                textAlign = TextAlign.Center
                            )

                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(200.dp)
                                    .padding(bottom = 24.dp)
                            )

                            Button(
                                onClick = {
                                    showSuccessDialog = false
                                    (context as? ComponentActivity)?.setContent {
                                        HomePage()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = branco),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Ir para a página inicial",
                                    color = roxo,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    containerColor = roxo,
                    shape = RoundedCornerShape(20.dp),
                    tonalElevation = 8.dp
                )
            }
        }
    }

    // Função para checar se o email foi verificado
    fun checarVerificacao(callback: (Boolean, String?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val db = Firebase.firestore


            val uid = user!!.uid
            user.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    var isverified = user.isEmailVerified
                    if (isverified) {
                        db.collection("Usuario").document(uid).update("emailVerificado", true)
                            .addOnSuccessListener {
                                Log.d(
                                    TAG, "Campo emailVerificado atualizado com sucesso no Firestore"
                                )
                            }.addOnFailureListener { e ->
                                Log.w(TAG, "Erro ao atualizar emailVerificado no Firestore", e)
                            }
                    } else {
                        callback(isverified, null)
                    }

                } else {
                    callback(false, task.exception?.message)
                }
            }

    }

    // Validação básica de email
    fun isValidEmail(email: String?): Boolean {
        if (email == null) return false

        val EMAIL_PATTERN =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
        val pattern = Regex(EMAIL_PATTERN)
        return pattern.matches(email)
    }

    fun loginAuth(email: String, senha: String, onResult: (Boolean) -> Unit) {
        Firebase.auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Logado com sucesso")
                onResult(true)
            } else {

                Log.w(TAG, "Não foi possivel logar", task.exception)
                onResult(false)
            }
        }
    }


    @Composable
    fun UnderlineTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String
    ) {
        var senhaVisibilidade by remember { mutableStateOf(false) }

        val textStyle = TextStyle(color = Color.DarkGray) // Define a cor da fonte aqui

        if (label == "Senha Mestre") {
            TextField(
                value = value,
                onValueChange = onValueChange,
                label = {
                    Text(label, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
                },
                textStyle = textStyle,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.DarkGray,
                    unfocusedTextColor = Color.DarkGray,
                    disabledTextColor = Color.Gray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Gray,
                    unfocusedIndicatorColor = Color.Gray,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 30.dp),
                visualTransformation = if (senhaVisibilidade) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image =
                        if (senhaVisibilidade) Icons.Default.Visibility else Icons.Default.VisibilityOff
                    IconButton(onClick = { senhaVisibilidade = !senhaVisibilidade }) {
                        Icon(imageVector = image, contentDescription = null, tint = Color.DarkGray)
                    }
                }
            )
        } else {
            TextField(
                value = value,
                onValueChange = onValueChange,
                label = {
                    Text(label, color = Color.DarkGray, fontWeight = FontWeight.SemiBold)
                },
                textStyle = textStyle,
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.DarkGray,
                    unfocusedTextColor = Color.DarkGray,
                    disabledTextColor = Color.Gray,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Gray,
                    unfocusedIndicatorColor = Color.Gray,
                    disabledIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 30.dp)
            )
        }
    }
}