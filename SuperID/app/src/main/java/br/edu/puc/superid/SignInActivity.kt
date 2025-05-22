package br.edu.puc.superid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.puc.superid.ui.HomePage
import br.edu.puc.superid.ui.MessageType
import br.edu.puc.superid.ui.ModalTextField
import br.edu.puc.superid.ui.theme.SuperIdTheme
import br.edu.puc.superid.ui.theme.roxo
import br.edu.puc.superid.ui.theme.roxoclaro
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

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

    @Composable
    fun TelaLogin() {
        var email by remember { mutableStateOf("") }
        var senha by remember { mutableStateOf("") }
        var erroMensagem by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }
        val context = LocalContext.current
        var esqueciSenhaModal by remember { mutableStateOf(false) }
        var senhaVisibilidade by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(roxoclaro, roxo) // Gradiente roxo escuro
                    )
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.4f),
                    radius = size.minDimension / 1.7f,
                    center = Offset(x = size.width * 0.2f, y = size.height * 0.2f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.5f),
                    radius = size.minDimension / 1.5f,
                    center = Offset(x = size.width * 0.7f, y = size.height * 0.9f)
                )
            }}

        Column(
            modifier = Modifier
                .padding(24.dp)
                .padding(top = 50.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
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

            TextField(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 12.dp)
                    .fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") }
            )

            TextField(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 12.dp)
                    .fillMaxWidth(),
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha Mestre") },
                visualTransformation = if (senhaVisibilidade) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (senhaVisibilidade)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff

                    IconButton(onClick = { senhaVisibilidade = !senhaVisibilidade }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                }
            )


            if (erroMensagem != null) {
                Text(
                    text = erroMensagem!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xff000000)
                    ),

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
                                setContent {
                                    HomePage()
                                }
                            } else {
                                erroMensagem = "Email ou senha incorretos."
                            }
                        }
                        checarVerificacao{verificado, erro ->
                            if(erro != null){
                                Log.d(TAG, "Erro ao verificar email $erro")
                            } else {
                                if(verificado){
                                    Log.d(TAG, "Email esta verificado")
                                }
                                else{
                                    Log.d(TAG, "Email nao esta verificado")
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                            .padding(top = 10.dp)
                            .padding(horizontal = 12.dp)
                ) {
                    Text("Logar")

                }
                Row(){
                    Text("Esqueci minha ")
                    Text("senha",
                        color = roxo,
                        modifier = Modifier
                            .clickable(
                            onClick = { esqueciSenhaModal = true
                            },

                        ))

                    if (esqueciSenhaModal){
                        ModalTextField(
                            type = MessageType.EMAIL,
                            titulo =  "Esqueceu sua senha?",
                            mensagem= "Digite seu email e enviaremos um link para redefinir sua senha mestre",
                            caminhoBotao2= {esqueciSenhaModal = false},
                            textoBotao1= "Enviar link de redefinição",
                            textoBotao2= "Cancelar",
                            onDismiss= { esqueciSenhaModal = false })
                    }
                }
            }

            Text("Ainda não tem conta? Cadastre-se",
                modifier = Modifier.clickable(
                    onClick = {
                        val intent = Intent(context, SignUpActivity::class.java)
                        context.startActivity(intent)
                        finish()
                    }
                ))
        }
    }

    fun checarVerificacao(callback: (Boolean, String?) -> Unit){
        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            user.reload().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(user.isEmailVerified, null)
                } else {
                    callback(false, task.exception?.message)
                }
            }
        } else {
            callback(false, "Usuário não autenticado")
        }
    }



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
}