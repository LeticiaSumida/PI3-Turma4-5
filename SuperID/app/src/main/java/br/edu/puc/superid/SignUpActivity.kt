package br.edu.puc.superid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.puc.superid.ui.MessageDialog
import br.edu.puc.superid.ui.MessageType
import br.edu.puc.superid.ui.WelcomeCarousel
import br.edu.puc.superid.ui.theme.SuperIdTheme
import br.edu.puc.superid.ui.theme.branco
import br.edu.puc.superid.ui.theme.cinzaclaro
import br.edu.puc.superid.ui.theme.cinzaescuro
import br.edu.puc.superid.ui.theme.roxo
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
        val prefs = getSharedPreferences("tutorial_prefs", MODE_PRIVATE)
        val tutorialVisto = prefs.getBoolean("tutorial_visto", false)

        enableEdgeToEdge()
        setContent {
            SuperIdTheme {
                var telaAtual by remember { mutableStateOf(if (tutorialVisto) "home" else "tutorial") }

                when (telaAtual) {
                    "tutorial" -> WelcomeCarousel(
                        onAceitar = {
                            prefs.edit().putBoolean("tutorial_visto", true).apply()
                            telaAtual = "home"
                        }
                    )
                    "home" -> TelaCadastro()
                }
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
        var senhaVisibilidade by remember { mutableStateOf(false) }

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
                modifier = Modifier.size(250.dp)
            )

            Text(
                "Cadastre-se",
                modifier = Modifier,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            UnderlineTextField(

                value = nome,
                onValueChange = { nome = it },
                label = "Nome"
            )

            UnderlineTextField(

                value = email,
                onValueChange = { email = it },
                label = "Email"
            )

            UnderlineTextField(
                value = senha,
                onValueChange = { senha = it },
                label = "Senha Mestre",
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
                                        addFirestore(nome, email, uid)
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
                    colors = ButtonDefaults.buttonColors(containerColor = roxo),
                    shape = RoundedCornerShape(16),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 12.dp)
                        .shadow(
                            elevation = 9.dp,
                            shape = RoundedCornerShape(16),
                            ambientColor = cinzaclaro, // Roxo mais claro para a sombra
                            spotColor = cinzaescuro

                        )
                ) {
                    Text("Cadastrar", color = branco)
                }
            }
            Row(){
            Text("Já tem uma conta? ")
            Text("Faça Login",
                color = roxo,
                modifier = Modifier.clickable(
                    onClick = {
                        val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
                    }

                ))}

            if (showSuccessDialog) {
                MessageDialog(
                    type = MessageType.SUCCESS,
                    titulo = "Cadastro realizado",
                    mensagem = "Seu cadastro foi concluído com sucesso!",
                    textoBotao = "Ir para Login",
                    caminhoBotao = {
                        showSuccessDialog = false

                        val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                        startActivity(intent)
                        finish()
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

    fun addFirestore(nome: String, email: String, uid: String) {
        val db = Firebase.firestore
        val usuario = FirebaseAuth.getInstance().currentUser

        val user = hashMapOf(
            "nome" to nome,
            "email" to email,
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
    @Composable
    fun UnderlineTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String
    ) {
        var senhaVisibilidade by remember { mutableStateOf(false) }

        if (label == "Senha Mestre"){
            TextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.LightGray,
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
                    val image = if (senhaVisibilidade)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff

                    IconButton(onClick = { senhaVisibilidade = !senhaVisibilidade }) {
                        Icon(imageVector = image, contentDescription = null)
                    }
                }
            )
        }
        else{
            TextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label) },
                colors = TextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.LightGray,
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
            )}

    }

}