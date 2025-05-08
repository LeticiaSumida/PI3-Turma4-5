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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.auth
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.google.firebase.auth.FirebaseAuth
import org.mindrot.jbcrypt.BCrypt

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
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 12.dp)
                ) {
                    Text("Logar")
                }
            }
        }
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