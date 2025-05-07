package br.edu.puc.superid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.puc.superid.ui.theme.SuperIdTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import androidx.compose.ui.platform.LocalContext

private lateinit var auth: FirebaseAuth
private val TAG=  "CategoryActivityLOG"
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
        var categoria by remember { mutableStateOf("") }
        var erroCategoria by remember { mutableStateOf(false) }
        var categorias = remember { mutableStateListOf<String>() }
        Column(
            modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Text(
                "Nova Categoria:",
                modifier = Modifier,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            TextField(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 12.dp)
                    .fillMaxWidth(),
                value = categoria,
                onValueChange = { categoria = it },
                label = { Text("categoria") }
            )
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff000000)
                ),

                onClick = {
                    var categoria = categoria.uppercase()
                    val user = Firebase.auth.currentUser
                    val uid = user!!.uid
                    checarCategoria(categoria, uid) { categoriaexistente ->
                        if (categoriaexistente) {
                            Log.w(TAG, "Categoria ja cadastrada")
                            erroCategoria = true
                        } else {
                            addFirestoreCategoria(categoria)
                            Log.d(TAG, "Categoria criada com sucesso")
                            categorias.add(categoria)
                            categoria = ""

                        }
                    }

                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 12.dp)
            ) {
                Text("Cadastrar")
            }
            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff000000)
                ),

                onClick = {
                    val intent = Intent(context, CategoriesScreenActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 12.dp)
            ) {
                Text("Ver categorias")
            }

            }

        }
    }

    fun addFirestoreCategoria(categoria: String) {
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        val uid = user?.uid


        var categoriaDb = hashMapOf(
            "categoria" to categoria,
            "uid" to uid
        )

        db.collection("Categoria")
            .add(categoriaDb)
            .addOnSuccessListener { documentReference ->
                Log.d("${TAG}", "Documento adicionado com ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro ao adicionar documento", e)
            }
    }

    fun checarCategoria(categoria: String, uid:String, callback: (Boolean) -> Unit) {
        val db = Firebase.firestore

        db.collection("Categoria")
            .whereEqualTo("categoria", categoria)
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { documents ->
                callback(!documents.isEmpty) // se achou algo, retorna true
            }
            .addOnFailureListener {
                callback(false) // erro na consulta, assume que não achou
            }
    }





