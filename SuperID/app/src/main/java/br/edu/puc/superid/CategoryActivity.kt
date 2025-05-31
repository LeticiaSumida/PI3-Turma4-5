package br.edu.puc.superid

import android.app.Activity
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
private val TAG = "CategoryActivityLOG"

class CategoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            SuperIdTheme {
                TelaCadastroCategoria()
            }
        }
    }

    @Composable
    fun TelaCadastroCategoria() {
        val context = LocalContext.current
        val activity = context as? Activity
        var categoria by remember { mutableStateOf("") }
        var erroCategoria by remember { mutableStateOf(false) }
        var erroCategoriaVazia by remember { mutableStateOf(false) } // novo estado para categoria vazia
        var categorias = remember { mutableStateListOf<String>() }
        var mostrarDialog by remember { mutableStateOf(false) }
        var erroInterno by remember { mutableStateOf(false) }
        var verificado by remember { mutableStateOf(false) }
        var carregando by remember { mutableStateOf(true) }
        var expanded2 by remember { mutableStateOf(false) }
        var home by remember { mutableStateOf(false) }


        LaunchedEffect(Unit) {
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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Nova categoria:",
                modifier = Modifier.padding(bottom = 30.dp),
                fontSize = 28.sp
            )
            UnderlineTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = "Categoria"
            )

            // Aviso se categoria já cadastrada
            if (erroCategoria) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Aviso",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Categoria já cadastrada.",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Aviso se categoria vazia
            if (erroCategoriaVazia) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Aviso",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Não é possível cadastrar uma categoria sem nome.",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Button(
                onClick = {
                    erroCategoria = false
                    erroInterno = false
                    erroCategoriaVazia = false

                    if (categoria.trim().isEmpty()) {
                        erroCategoriaVazia = true
                        return@Button
                    }

                    val user = Firebase.auth.currentUser
                    val uid = user?.uid

                    if (uid == null) {
                        erroInterno = true
                        return@Button
                    }

                    checarCategoria(categoria) { categoriaexistente ->
                        if (categoriaexistente) {
                            erroCategoria = true
                        } else {
                            try {
                                addFirestoreCategoria(categoria)
                                categorias.add(categoria)
                                categoria = ""
                                mostrarDialog = true
                            } catch (e: Exception) {
                                erroInterno = true
                            }
                        }
                    }
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
                Text("Cadastrar", color = branco)
            }

            // ... resto do código de AlertDialog e botões permanece igual

            if (mostrarDialog) {
                AlertDialog(
                    onDismissRequest = { mostrarDialog = false },
                    title = null,
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Categoria cadastrada com sucesso!",
                                color = branco,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 24.dp),
                                lineHeight = 36.sp
                            )
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Sucesso",
                                tint = Color.White,
                                modifier = Modifier
                                    .size(200.dp)
                                    .padding(bottom = 16.dp)
                            )
                            Button(
                                onClick = {
                                    mostrarDialog = false
                                    activity?.finish()
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
                    tonalElevation = 8.dp,
                    confirmButton = {}
                )
            }

            if (erroInterno) {
                AlertDialog(
                    onDismissRequest = { erroInterno = false },
                    title = { Text("Erro interno") },
                    text = { Text("Ocorreu um erro inesperado ao cadastrar a categoria.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                erroInterno = false
                                activity?.finish()
                            }
                        ) {
                            Text("Voltar para Minhas senhas")
                        }
                    }
                )
            }

            Button(
                onClick = {
                    val intent = Intent(context, CategoriesScreenActivity::class.java)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = branco),
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
                Text(
                    "Ver categorias",
                    color = roxo
                )
            }
        }
    }



    fun addFirestoreCategoria(categoria: String) {
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        val uid = user!!.uid
        val deletavel = true


        var categoriaDb = hashMapOf(
            "nome" to categoria,
            "deletavel" to deletavel
        )

        db.collection("Usuario").document(uid).collection("categorias")
            .add(categoriaDb)
            .addOnSuccessListener { documentReference ->
                Log.d("${TAG}", "Documento adicionado com ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro ao adicionar documento", e)
            }
    }

    fun checarCategoria(categoria: String, callback: (Boolean) -> Unit) {
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        val uid = user!!.uid

        db.collection("Usuario").document(uid).collection("categorias")
            .whereEqualTo("nome", categoria)

            .get()
            .addOnSuccessListener { documents ->
                callback(!documents.isEmpty) // se achou algo, retorna true
            }
            .addOnFailureListener {
                callback(false) // erro na consulta, assume que não achou
            }
    }

    @Composable
    fun UnderlineTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String = "Categoria"
    ) {

        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label, fontWeight = FontWeight.Bold) },
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





