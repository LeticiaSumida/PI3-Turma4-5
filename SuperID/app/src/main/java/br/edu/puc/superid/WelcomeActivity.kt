package br.edu.puc.superid

import android.R.id.bold
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.puc.superid.ui.theme.SuperIdTheme
import br.edu.puc.superid.ui.theme.branco
import br.edu.puc.superid.ui.theme.cinzaclaro
import br.edu.puc.superid.ui.theme.cinzaescuro
import br.edu.puc.superid.ui.theme.fontFamily
import br.edu.puc.superid.ui.theme.fontName
import br.edu.puc.superid.ui.theme.roxo
import br.edu.puc.superid.ui.theme.roxoclaro
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(roxoclaro, roxo) // Gradiente roxo escuro
                    )
                )
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.White.copy(alpha = 0.4f),
                    radius = size.minDimension / 1.7f,
                    center = Offset(x = size.width * 0.2f, y = size.height * 0.2f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.5f),
                    radius = size.minDimension / 1.5f,
                    center = Offset(x = size.width * 0.7f, y = size.height * 0.9f)
                )
            }}
        Column(
            modifier = Modifier
                .padding(24.dp)
                .padding(top = 50.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(

                painter = painterResource(id = R.drawable.cadeado3),
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .padding(start = 40.dp)


            )
            Text("SuperID",
                modifier = Modifier
                    .padding(bottom = 40.dp),
                fontSize = 60.sp,
                color = branco,
                fontFamily = fontFamily,

            )
            Button(
                onClick = {
                    val intent = Intent(context, SignUpActivity::class.java)
                    context.startActivity(intent)

                },
                colors = ButtonDefaults.buttonColors(containerColor = branco),
                shape = RoundedCornerShape(10),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 12.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(10),
                        ambientColor = cinzaclaro, // Roxo mais claro para a sombra
                        spotColor = cinzaescuro

                    )
            ){
                Text("Cadastrar", color = roxo)
            }
            Button(
                onClick = {
                    val intent = Intent(context, SignInActivity::class.java)
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = branco),
                shape = RoundedCornerShape(10),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp, horizontal = 12.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(10),
                        ambientColor = cinzaclaro, // Roxo mais claro para a sombra
                        spotColor = cinzaescuro

                    )
            ){
                Text("Login", color = roxo)
            }
        }
    }

    @Preview
    @Composable
    fun previewprimeiratela(){
        PrimeiraTela()
    }
}