package br.edu.puc.superid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.puc.superid.ui.theme.SuperIdTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import org.mindrot.jbcrypt.BCrypt

private lateinit var auth: FirebaseAuth
private const val TAG = "SignUpActivityLOG"

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            SuperIdTheme {
                TelaCadastro()
            }
        }
    }

    @Composable
    fun TelaCadastro() {
        var nome by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var senha by remember { mutableStateOf("") }
        var erroMensagem by remember { mutableStateOf<String?>(null) }
        var isLoading by remember { mutableStateOf(false) }
        var showSuccessDialog by remember { mutableStateOf(false) }

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
                "Cadastre-se",
                modifier = Modifier,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            TextField(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 12.dp)
                    .fillMaxWidth(),
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") }
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
                        if (nome.isBlank() || email.isBlank() || senha.isBlank()) {
                            erroMensagem = "Preencha todos os campos."
                            return@Button
                        }
                        if (!isValidEmail(email)) {
                            erroMensagem = "Formato de email inválido."
                            return@Button
                        }
                        if (senha.length < 6) {
                            erroMensagem = "A senha precisa ter no mínimo 6 caracteres."
                            return@Button
                        }
                        isLoading = true
                        checarEmail(email) { emailExistente ->
                            if (emailExistente) {
                                erroMensagem = "O email já está cadastrado."
                                isLoading = false
                            } else {
                                addAuth(email, senha) { sucessoAuth ->
                                    if (sucessoAuth) {
                                        val uid = auth.currentUser?.uid ?: ""
                                        addFirestore(nome, email, senha, uid)
                                        isLoading = false
                                        Log.d(TAG, "Usuário criado com sucesso")
                                        showSuccessDialog = true
                                    } else {
                                        erroMensagem = "Erro ao criar usuário."
                                        isLoading = false
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 12.dp)
                ) {
                    Text("Cadastrar")
                }
            }
            if (showSuccessDialog) {
                MessageDialog(
                    type = MessageType.SUCCESS,
                    titulo = "Cadastro realizado",
                    mensagem = "Seu cadastro foi concluído com sucesso!",
                    textoBotao1 = "Ir para Login",
                    textoBotao2 = "Fechar",
                    caminhoBotao1 = {
                        showSuccessDialog = false

                        val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    caminhoBotao2 = {
                        showSuccessDialog = false
                    },
                    onDismiss = {
                        showSuccessDialog = false
                    }
                )
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

    fun checarEmail(email: String, callback: (Boolean) -> Unit) {
        val db = Firebase.firestore

        db.collection("Usuario")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                callback(!documents.isEmpty) // se achou algo, retorna true
            }
            .addOnFailureListener {
                callback(false) // erro na consulta, assume que não achou
            }
    }

    fun addFirestore(nome: String, email: String, senha: String, uid: String) {
        val db = Firebase.firestore
        val senhaCrypto = hashPassword(senha)
        val usuario = FirebaseAuth.getInstance().currentUser

        val user = hashMapOf(
            "nome" to nome,
            "email" to email,
            "senha" to senhaCrypto,
            "uid" to uid,
            "emailVerificado" to usuario?.isEmailVerified
        )

        db.collection("Usuario")
            .document(uid)
            .set(user)
            .addOnSuccessListener { documentReference ->


                val categoriasPadrao = listOf( // O rf-2 solicita 3 categorias padrões sendo a Sites Web impossível de deletar
                    mapOf("nome" to "Sites Web", "deletavel" to false),
                    mapOf("nome" to "Aplicativos", "deletavel" to true),
                    mapOf("nome" to "Teclados de Acesso Físico", "deletavel" to true)
                )

                for (categoria in categoriasPadrao) {
                    db.collection("Usuario").document(uid).collection("categorias").add(categoria) //Coleção Usuario.IdDoUsuario.SubColeção Categoria
                }

                //As senhas serão adicionadas manualmente futuramente, quando é criado um usuário apenas as categorias são obrigatórias



                Log.d(TAG, "Documento adicionado com ID: $uid")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro ao adicionar documento", e)
            }
    }

    fun addAuth(email: String, senha: String, callback: (Boolean) -> Unit) {
        Firebase.auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = Firebase.auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { emailTask ->
                            if (emailTask.isSuccessful) {
                                Log.d(TAG, "E-mail de verificação enviado.")
                            } else {
                                Log.w(TAG, "Falha ao enviar e-mail de verificação", emailTask.exception)
                            }
                        }

                    Log.d(TAG, "Usuário criado no auth")
                    callback(true)
                } else {
                    Log.w(TAG, "Erro ao criar usuário no auth", task.exception)
                    callback(false)
                }
            }
    }

    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())

    }
}