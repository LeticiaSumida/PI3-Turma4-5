package br.edu.puc.superid

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.puc.superid.ui.theme.SuperIdTheme
import br.edu.puc.superid.ui.theme.roxo
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

private lateinit var auth: FirebaseAuth
private val TAG=  "PasswordActivityLOG"
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
        val context = LocalContext.current

        var login by remember { mutableStateOf("") }
        var senha by remember { mutableStateOf("") }
        var desc by remember { mutableStateOf("") }
        var categoriaSelecionada by remember { mutableStateOf("Categoria") }
        var categorias = remember { mutableStateListOf<String>() }


        LaunchedEffect(Unit) {
            carregarCategorias(categorias)
        }


        Column(
            modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Text(
                "Nova Senha:",
                modifier = Modifier,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = roxo
            )
            TextField(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 12.dp)
                    .fillMaxWidth(),
                value = login,
                onValueChange = { login = it },
                label = { Text("Login") }
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
            TextField(
                modifier = Modifier
                    .padding(vertical = 10.dp, horizontal = 12.dp)
                    .fillMaxWidth(),
                value = desc,
                onValueChange = { desc = it },
                label = { Text("Descrição") }
            )
            DropDownCategoria(categorias = categorias,
                categoriaSelecionada = categoriaSelecionada,
                onCategoriaSelecionadaChange = { categoriaSelecionada = it })
            Button(
                onClick = {addFirestoreSenha(login, senha, desc, categoriaSelecionada)
                    Log.d(TAG, "Categoria selecionada: $categoriaSelecionada")},
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 12.dp)
            ) {
                Text("Cadastrar")
            }


        }

    }
}
@Composable
fun DropDownCategoria(categorias: List<String>,
                      categoriaSelecionada: String,
                      onCategoriaSelecionadaChange: (String) -> Unit){
    var expanded by remember { mutableStateOf(false)}


    TextButton(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = {expanded = !expanded}
    ){
        if(expanded){
            DropdownMenu(expanded = expanded,
                onDismissRequest = { expanded = false }) {
                categorias.forEach { categoria ->
                    DropdownMenuItem(
                    text = { Text(categoria) },
                    onClick = {onCategoriaSelecionadaChange(categoria)
                    expanded=!expanded}

                )  }}
        }
        Text(categoriaSelecionada)
    }

}

fun addFirestoreSenha(login: String, senha: String, desc:String, categoria: String) {
    val db = Firebase.firestore
    val user = Firebase.auth.currentUser
    val uid = user!!.uid
    val deletavel = true


    var senhaDb = hashMapOf(
        "Login" to login,
        "Senha" to senha,
        "Descrição" to desc,
        "Categoria" to categoria,
        "deletavel" to deletavel
    )

    db.collection("Usuario").document(uid).collection("senhas").add(senhaDb)
        .addOnSuccessListener { documentReference ->
            Log.d(TAG, "Documento adicionado com ID: $uid")}
        .addOnFailureListener { e ->
                    Log.w("Firestore", "Erro ao adicionar documento", e)
                }




}

fun carregarCategorias(categorias: SnapshotStateList<String>){
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






