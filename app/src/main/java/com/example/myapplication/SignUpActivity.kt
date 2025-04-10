package com.example.myapplication

import android.os.Bundle
import android.widget.EditText
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.ui.theme.MyApplicationTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx. compose. material3.Button
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import android.util.Log
import androidx.compose.runtime.rememberCoroutineScope
import androidx. compose. ui. text. input. PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.size

private lateinit var auth: FirebaseAuth
private val TAG=  "SignUpActivityLOG"
class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            MyApplicationTheme {
                TelaCadastro()
            }
        }
    }
    @Composable
    fun TelaCadastro(){
        var nome by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("")}
        var senha by remember { mutableStateOf("")}




        Column( modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth(),
             horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(
                painter = painterResource(id = R.drawable.cadastro),
                contentDescription = null,
                modifier = Modifier.size(250.dp)

            )
            Text("Cadastre-se",
                modifier = Modifier,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold

            )
            TextField(modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 12.dp)
                .fillMaxWidth(),
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") }
            )

            TextField(modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 12.dp)
                .fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") }
            )


            TextField(modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 12.dp)
                .fillMaxWidth(),
                value = senha,
                onValueChange = { senha = it },
                label = { Text("Senha Mestre") },
                visualTransformation = PasswordVisualTransformation()

            )


            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff000000)
                ),
            onClick = {
                if (senha.length >= 6) {

                    addAuth(email, senha)
                    val uid = auth.currentUser?.uid!!
                    addFirestore(nome, email, senha, uid)


                    Log.d(TAG, "Usuario criado com sucesso")
                } else{
                    Log.w(TAG,"Sua senha precisa ter mais de 6 caracteres" )
                }},
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp)
            ){
                Text("Cadastrar")
            }
        }
        }



    fun addFirestore(nome: String, email: String, senha: String, uid: String){

        val db = Firebase.firestore
        val uid = "Ainda nao fez login"
        val user = hashMapOf(
            "nome" to nome,
            "email" to email,
            "senha" to senha,
            "uid" to uid,
        )

        db.collection("Usuario")
            .add(user)
            .addOnSuccessListener { documentReference ->
                Log.d("${TAG}", "Documento adicionado com ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w( "Firestore", "Erro ao adicionar documento", e)
            }
    }

    fun addAuth(email: String, senha: String){
        Firebase.auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "Usuario criado no auth")
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "Erro ao criar usuario no auth", task.exception)

            }
        }
    }






}


