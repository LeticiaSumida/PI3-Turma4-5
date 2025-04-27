package br.edu.puc.superid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import br.edu.puc.superid.ui.theme.SuperIdTheme
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.FirebaseApp


class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        enableEdgeToEdge()
        setContent {
            SuperIdTheme {
                PrimeiraTela()
            }
        }
    }

    @Composable
    fun PrimeiraTela(){
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ){
            Button(
                onClick = {
                    val intent = Intent(context, SignUpActivity::class.java)
                    context.startActivity(intent)


                },
                modifier = Modifier
                .fillMaxWidth()
            ){
                Text("Cadastrar")
            }
            Button(
                onClick = {
                    val intent = Intent(context, SignInActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Text("Login")
            }
        }
    }
}