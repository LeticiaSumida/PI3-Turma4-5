package br.edu.puc.superid

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.puc.superid.ui.theme.SuperIdTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.auth
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import br.edu.puc.superid.ModalTextField
import com.google.firebase.auth.FirebaseAuth
import org.mindrot.jbcrypt.BCrypt
import br.edu.puc.superid.ui.theme.roxo
import br.edu.puc.superid.ui.theme.roxoclaro

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
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Image(
                painter = painterResource(id = R.drawable.cadastro),
                contentDescription = null,
                modifier = Modifier.size(250.dp)

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
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation()
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

                        verificarCredenciais(email, senha) { credenciaisValidas ->
                            isLoading = false
                            if (credenciaisValidas) {
                                loginAuth(email, senha) { login ->
                                    if (login) {
                                        val intent = Intent(context, CategoriesScreenActivity::class.java)
                                        context.startActivity(intent)
                                    } else {
                                        erroMensagem = "Erro na autenticação."
                                    }
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

    @Preview
    @Composable
    fun previewtelalogin(){
        TelaLogin()
    }

    fun isValidEmail(email: String?): Boolean {
        if (email == null) return false

        val EMAIL_PATTERN =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$"
        val pattern = Regex(EMAIL_PATTERN)
        return pattern.matches(email)
    }

    fun verificarCredenciais(email: String, senha: String, callback: (Boolean) -> Unit) {
        val db = Firebase.firestore

        db.collection("Usuario")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val doc = result.documents[0]
                    val senhaHash = doc.getString("senha")
                    if (senhaHash != null && BCrypt.checkpw(senha, senhaHash)) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                } else {
                    callback(false)
                }
            }
            .addOnFailureListener {
                callback(false)
            }
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