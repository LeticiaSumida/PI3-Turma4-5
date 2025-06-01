package br.edu.puc.superid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.puc.superid.CriptoAES.criptografar
import br.edu.puc.superid.ui.checarVerificado
import br.edu.puc.superid.ui.theme.SuperIdTheme
import br.edu.puc.superid.ui.theme.branco
import br.edu.puc.superid.ui.theme.cinzaclaro
import br.edu.puc.superid.ui.theme.cinzaescuro
import br.edu.puc.superid.ui.theme.roxo
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay


private lateinit var auth: FirebaseAuth
private val TAG = "PasswordActivityLOG"

class PasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            SuperIdTheme {
                telaCadastroSenha()
            }
        }
    }

    @Composable
    fun telaCadastroSenha() {


        var login by remember { mutableStateOf("") }
        var senha by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var categoriaSelecionada by remember { mutableStateOf("Categoria") }
        var categorias = remember { mutableStateListOf<String>() }
        var mostrarDialog by remember { mutableStateOf(false) }
        var verificado by remember { mutableStateOf(false) }
        var carregando by remember { mutableStateOf(true) }
        var expanded2 by remember { mutableStateOf(false) }
        var home by remember { mutableStateOf(false) }
        var context = LocalContext.current
        var erroCadastro by remember { mutableStateOf(false) }



        LaunchedEffect(Unit) {
            carregarCategorias(categorias)
            checarVerificado { resultado ->
                verificado = resultado
                carregando = false}
            delay(2000)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(roxo)
        )
        Row(
            modifier = Modifier.padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { finish() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBackIosNew,
                    contentDescription = "Voltar",
                    tint = branco,
                )
            }
            Text("SuperID", color = branco)
            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = { expanded2 = !expanded2 },

                ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Sign-Out",
                    tint = branco,
                )
            }
            DropdownMenu(
                expanded = expanded2,
                onDismissRequest = { expanded2 = false },
                offset = DpOffset(x = (200).dp, y = 0.dp),

                ) {
                DropdownMenuItem(

                    text = {
                        Text(
                            "Home",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },
                    leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                    onClick = {
                        home = true
                    }
                )
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            Icons.AutoMirrored.Outlined.Logout,
                            contentDescription = null
                        )
                    },
                    text = {
                        Text(
                            "Logout",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },

                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(context, SignInActivity::class.java)
                        context.startActivity(intent)
                        finish()
                    }
                )

                if (verificado == false) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "Reenviar verificação",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Refresh,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            val user = Firebase.auth.currentUser
                            user?.sendEmailVerification()
                            Toast.makeText(
                                context,
                                "Email Reenviado com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                } else {
                    DropdownMenuItem(

                        text = {
                            Text(
                                "Esqueci minha senha",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        },
                        leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                        onClick = {
                            val user = Firebase.auth.currentUser
                            var email = user!!.email
                            email = email.toString()
                            Firebase.auth.sendPasswordResetEmail(email)
                            Toast.makeText(
                                context,
                                "Email de redefinição de senha enviado com sucesso",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    )

                }
            }
        }


        Column(
            modifier = Modifier


                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            Text(
                "Nova Senha:",
                modifier = Modifier,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            UnderlineTextField(
                value = login,
                onValueChange = { login = it },
                label = "Login"
            )

            UnderlineTextField(
                value = senha,
                onValueChange = { senha = it },
                label = "Senha",

                )

            UnderlineTextField(
                value = desc,
                onValueChange = { desc = it },
                label = "Descrição"

            )

            DropDownCategoria(
                categorias = categorias,
                categoriaSelecionada = categoriaSelecionada,
                onCategoriaSelecionadaChange = { categoriaSelecionada = it }
            )

            HorizontalDivider(
                thickness = 0.5.dp,
                modifier = Modifier.padding(horizontal = 33.dp),
                color = Color.Gray
            )
            if (erroCadastro) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 30.dp, top = 4.dp, end = 30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Aviso",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Preencha a senha e selecione uma categoria antes de cadastrar.",
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1f),
                        textAlign = TextAlign.Start
                    )
                }
            }

            TextButton(
                onClick = {
                    if (senha.isBlank() || categoriaSelecionada == "Categoria") {
                        erroCadastro = true
                    } else {
                        erroCadastro = false
                        addFirestoreSenha(login, senha, desc, categoriaSelecionada) {
                            mostrarDialog = true
                        }
                    }

                    Log.d(TAG, "Categoria selecionada: $categoriaSelecionada")
                },
                colors = ButtonDefaults.buttonColors(containerColor = roxo),
                shape = RoundedCornerShape(16),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 12.dp)
                    .padding(top = 50.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16),
                        ambientColor = cinzaclaro,
                        spotColor = cinzaescuro

                    )
            ) {
                Text(
                    "Cadastrar",
                    color = branco
                )
            }
            if (mostrarDialog) {
                AlertDialog(
                    onDismissRequest = { mostrarDialog = false },
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
                                text = "Sua senha foi cadastrada com sucesso!",
                                color = branco,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 24.dp),
                                textAlign = TextAlign.Center,
                                lineHeight = 36.sp
                            )

                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier
                                    .size(200.dp)
                                    .padding(bottom = 24.dp)
                            )

                            TextButton(
                                onClick = {
                                    mostrarDialog = false
                                    finish()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = branco),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Voltar para Minhas senhas",
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
}


    @Composable
    fun DropDownCategoria(
        categorias: List<String>,
        categoriaSelecionada: String,
        onCategoriaSelecionadaChange: (String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }

        TextButton(
            modifier = Modifier
                .padding(top = 10.dp)
                .padding(horizontal = 33.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(10),

            onClick = { expanded = !expanded }
        ) {
            if (expanded) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }) {
                    categorias.forEach { categoria ->
                        DropdownMenuItem(
                            text = { Text(categoria) },
                            onClick = {
                                onCategoriaSelecionadaChange(categoria)
                                expanded = !expanded
                            }

                        )
                    }
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    categoriaSelecionada,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(vertical = 10.dp),
                    color = cinzaclaro,
                    fontSize = 17.sp
                )
            }
        }
    }

fun addFirestoreSenha(login: String, senha: String, desc: String, categoria: String,onSuccess: () -> Unit) {
    val db = Firebase.firestore
    val user = Firebase.auth.currentUser
    val uid = user!!.uid
    val deletavel = true
    val senhaCripto = criptografar(senha)

    var senhaDb = hashMapOf(
        "Login" to login,
        "Senha" to senhaCripto,
        "Descrição" to desc,
        "Categoria" to categoria,
        "deletavel" to deletavel
    )

    db.collection("Usuario").document(uid).collection("senhas").add(senhaDb)
        .addOnSuccessListener { documentReference ->
            Log.d(TAG, "Documento adicionado com ID: $uid")
            onSuccess()
        }
        .addOnFailureListener { e ->
            Log.w("Firestore", "Erro ao adicionar documento", e)
        }
}

fun carregarCategorias(categorias: SnapshotStateList<String>) {
    val db = Firebase.firestore
    val user = Firebase.auth.currentUser
    val uid = user!!.uid

    db.collection("Usuario").document(uid).collection("categorias")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                val categoria = document.getString("nome")
                if (categoria != null) {
                    categorias.add(categoria)
                }
            }
        }
        .addOnFailureListener { exception ->
            Log.w("Categoria", "Erro ao buscar categorias", exception)
        }
}


    @Composable
    fun UnderlineTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
    ) {
        if (label == "Senha") {
            TextField(
                value = value,
                onValueChange = onValueChange,
                label = { Text(label, fontWeight = FontWeight.ExtraBold) },
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
                visualTransformation = PasswordVisualTransformation()
            )
        } else {
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
            )
        }
    }



