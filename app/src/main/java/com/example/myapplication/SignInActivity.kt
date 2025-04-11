package com.example.myapplication

import android.content.Intent
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx. compose. ui. text. input. PasswordVisualTransformation
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.auth
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.auth.userProfileChangeRequest

private val TAG=  "SignInActivityLOG"

class SignInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                TelaLogin()
            }
        }
    }

    @Composable
    fun TelaLogin() {
        var email by remember { mutableStateOf("")}
        var senha by remember { mutableStateOf("")}
        val context = LocalContext.current


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
            Text("Login",
                modifier = Modifier,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold

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
                label = { Text("Senha") },
                visualTransformation = PasswordVisualTransformation()
            )

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff000000)
                ),

                onClick = {
                    loginAuth(email, senha) { login -> atualizarBD(email)
                    if(login){
                        atualizarBD(email)
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    } else{}}


                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 12.dp)
            ){
                Text("Logar")
            }
        }
    }


    fun loginAuth(email: String, senha: String, onResult: (Boolean) -> Unit){
        Firebase.auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "Logado com sucesso")
                onResult(true)
            } else {

                Log.w(TAG, "Não foi possivel logar", task.exception)
                onResult(false)


            }
        }
    }


   fun atualizarBD(email: String) {
       val novouid = Firebase.auth.uid
       val db = Firebase.firestore

       db.collection("Usuario")
           //chatgpt
           .whereEqualTo("email", email)
           .get()
           .addOnSuccessListener { result ->
               if (!result.isEmpty) {
                   val document = result.documents[0]
                   val docId = document.id


                   db.collection("Usuario").document(docId).update("uid", novouid)
                       .addOnSuccessListener {
                           Log.d(
                               TAG,
                               "Documento atualizado com sucesso"
                           )
                       }
                       .addOnFailureListener { e -> Log.w(TAG, "Erro ao atualizar o documento", e) }

               }

           }
   }}



