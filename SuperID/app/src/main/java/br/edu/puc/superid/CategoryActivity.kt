package br.edu.puc.superid

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.puc.superid.ui.theme.SuperIdTheme
import br.edu.puc.superid.ui.theme.branco
import br.edu.puc.superid.ui.theme.cinzaclaro
import br.edu.puc.superid.ui.theme.cinzaescuro
import br.edu.puc.superid.ui.theme.roxo
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

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
        var categorias = remember { mutableStateListOf<String>() }
        var mostrarDialog by remember { mutableStateOf(false) }
        var erroInterno by remember { mutableStateOf(false) }



        Column(
            modifier = Modifier


                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            Text(
                "Nova categoria:",
                modifier = Modifier
                    .padding(bottom = 30.dp),
                fontSize = 28.sp
            )
            UnderlineTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = "Categoria"

            )

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

            Button(
                onClick = {
                    erroCategoria = false
                    erroInterno = false

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
            val context = LocalContext.current
            val activity = context as? Activity

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
                                onClick = { mostrarDialog = false
                                    activity?.finish()},
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
                        ambientColor = cinzaclaro, // Roxo mais claro para a sombra
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





