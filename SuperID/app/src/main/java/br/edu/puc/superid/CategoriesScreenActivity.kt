package br.edu.puc.superid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puc.superid.ui.theme.SuperIdTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

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
                    Text(text = cat)
                }

            }
            Button(
                onClick = { val intent = Intent(context, CategoryActivity::class.java)
                    context.startActivity(intent) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(RoundedCornerShape(50)),

            ) {
                Text("+")
            }
        }
    }

    fun CategoriasConta(categorias: SnapshotStateList<String>){
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        val uid = user?.uid


        db.collection("Categoria")
            .whereEqualTo("uid", uid)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val categoria = document.getString("categoria")
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
