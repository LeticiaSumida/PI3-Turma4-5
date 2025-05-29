package br.edu.puc.superid

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
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


        LaunchedEffect(Unit) {
            carregarCategorias(categorias)
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

            TextButton(
                onClick = {
                    addFirestoreSenha(login, senha, desc, categoriaSelecionada)
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

    fun addFirestoreSenha(login: String, senha: String, desc: String, categoria: String) {
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
                Log.d(TAG, "Documento adicionado com ID: $uid")
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

    @Preview
    @Composable
    fun telaSenhaPreview() {
        telaCadastroSenha()
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
}


