package br.edu.puc.superid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.puc.superid.ui.theme.SuperIdTheme
import br.edu.puc.superid.ui.theme.branco
import br.edu.puc.superid.ui.theme.roxo
import br.edu.puc.superid.ui.theme.roxoclaro
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import java.nio.file.Files.size

private lateinit var auth: FirebaseAuth

class CategoriesScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            SuperIdTheme {
                CategoriaNaTela()

            }
        }
    }


    @Composable
    fun CategoriaNaTela() {
        val categorias = remember { mutableStateListOf<String>() }
        val context = LocalContext.current



        LaunchedEffect(Unit) {
            CategoriasConta(categorias)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)

        ) {
            Column(
                modifier = Modifier
                    .padding(top = 30.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                Spacer(modifier = Modifier.height(16.dp))
                categorias.forEach { cat ->
                    expandableCard(cat)
                }

            }
            Button(
                onClick = {
                    val intent = Intent(context, CategoryActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(90)),

                ) {
                Text("+")
            }
        }
    }

    @Composable
    fun expandableCard(categoria: String) {
        val context = LocalContext.current
        var expandedState by remember { mutableStateOf(true) }
        val rotationState by animateFloatAsState(
            targetValue = if (expandedState) 180f else 0f
        )
        val senhas = remember { mutableStateListOf<String>() }


        LaunchedEffect(Unit) {
            senhasConta(senhas, categoria)
        }
        Card(
            modifier = Modifier

                .padding(top = 10.dp)
                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = tween(
                        delayMillis = 100,
                        easing = LinearOutSlowInEasing
                    )
                )
                .clickable {
                    expandedState = !expandedState // Alterna o estado ao clicar
                },
            colors = CardDefaults.cardColors(
                containerColor = roxoclaro,
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)

            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        modifier = Modifier
                            .rotate(rotationState),

                        onClick = {
                            expandedState = !expandedState
                        }

                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expandir",
                            tint = branco
                        )
                    }
                    Text(
                        modifier = Modifier,
                        fontSize = 22.sp,
                        color = branco,
                        text = categoria,
                        overflow = TextOverflow.Ellipsis
                    )


                }

                if (expandedState) {
                    Spacer(modifier = Modifier.height(16.dp))
                    senhas.forEach { senha ->
                        mostrarSenhas(senha)
                        HorizontalDivider(Modifier.padding(horizontal = 33.dp),
                            color = Color.LightGray)
                    }

                    TextButton(
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .fillMaxWidth()
                            .height(40.dp)
                            .background(
                                color = roxoclaro,


                            ),

                        onClick = {
                            val intent = Intent(context, PasswordActivity::class.java)
                            context.startActivity(intent)
                        }
                    ) {
                        Text(
                            "+ Adicionar Senha",

                            color = branco,
                            fontWeight = FontWeight.W600

                        )
                    }
                }
            }

        }
    }

    @Preview
    @Composable
    fun previewCat(){
        expandableCard("Teste")
    }

    @Composable
    fun mostrarSenhas(
        senha: String
    ) {


        var text = senha
        var textMasked = "*".repeat(text.length)
        var checked by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
        ) {
            Row(modifier = Modifier
                .padding(horizontal = 30.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (checked) text else textMasked,
                    modifier = Modifier
                        .weight(6f),
                    fontSize = 20.sp
                )
                IconButton(
                    modifier = Modifier
                        .weight(8f),

                    onClick = {
                        checked = !checked
                    }

                ) {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Ver senha")
                }
            }

        }
    }


    fun senhasConta(senhas: SnapshotStateList<String>, categoria: String) {
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        val uid = user!!.uid

        db.collection("Usuario").document(uid).collection("senhas")
            .whereEqualTo("Categoria", categoria)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val senha = document.getString("Senha")
                    if (senha != null) {
                        senhas.add(senha)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Categoria", "Erro ao buscar categorias", exception)
            }
    }


    fun CategoriasConta(categorias: SnapshotStateList<String>) {
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

}
