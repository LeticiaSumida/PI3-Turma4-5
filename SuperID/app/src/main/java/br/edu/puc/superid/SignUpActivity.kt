package br.edu.puc.superid

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.puc.superid.ui.theme.SuperIdTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.activity.ComponentActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import android.util.Log
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.auth
import androidx.compose.foundation.Image
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.TextButton
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation

private lateinit var auth: FirebaseAuth
private val TAG=  "SignUpActivityLOG"
class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            SuperIdTheme {
                TelaCadastro()
            }
        }
    }
    @Composable
    fun TelaCadastro(){
        var nome by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("")}
        var senha by remember { mutableStateOf("")}
        var erroSenha by remember { mutableStateOf(false) }
        var erroEmail by remember { mutableStateOf(false) }



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

                    checarEmail(email){ emailexistente ->
                        if (emailexistente){
                            Log.w(TAG,"Usuario com email cadastrado" )
                            erroEmail = true
                            }
                        else {
                            addAuth(email, senha)
                            val uid = auth.currentUser?.uid!!
                            addFirestore(nome, email, senha, uid)
                            Log.d(TAG, "Usuario criado com sucesso")
                    }}
                } else{
                    erroSenha = true
                    Log.w(TAG,"Sua senha precisa ter mais de 6 caracteres" )
                }},
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 12.dp)
            ){
                Text("Cadastrar")
                if (erroEmail){
                    msgErroEmail(
                        onDismiss = { erroEmail = false },
                        onConfirm = { erroEmail = false }
                    )
                if (erroSenha){
                    msgErroSenha(
                        onDismiss = { erroSenha = false },
                        onConfirm = { erroSenha = false }
                    )
                }

                }
            }
        }
        }

    @Composable
    fun msgErroSenha(
        onDismiss: () -> Unit,
        onConfirm: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Erro") },
            text = { Text("A senha precisa ter mais de 6 caracteres.") },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("OK")
                }
            }
        )
    }

    @Composable
    fun msgErroEmail(
        onDismiss: () -> Unit,
        onConfirm: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Erro") },
            text = { Text("O email ja esta cadastrado") },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("OK")
                }
            }
        )
    }


    fun checarEmail(email: String, callback: (Boolean) -> Unit) {
        val db = Firebase.firestore

        db.collection("Usuario")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                callback(!documents.isEmpty) // se achou algo, retorna true
            }
            .addOnFailureListener {
                callback(false) // erro na consulta, assume que não achou
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
                Log.d("$TAG", "Documento adicionado com ID: ${documentReference.id}")
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


