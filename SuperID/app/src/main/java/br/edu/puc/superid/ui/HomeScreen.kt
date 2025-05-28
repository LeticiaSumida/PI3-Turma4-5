package br.edu.puc.superid.ui

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.puc.superid.CategoriesScreenActivity
import br.edu.puc.superid.R
import br.edu.puc.superid.SignInWithoutPass

@Composable
fun HomePage() {
    var context = LocalContext.current;

    var mostrarDialogoSenha by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .size(180.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SuperID",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = { mostrarDialogoSenha = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6441A5)),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .width(30.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.qrcode),
                    contentDescription = "QR Code",
                    modifier = Modifier
                        .size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Login sem senha",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Acesse sites parceiros",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val intent = Intent(context, CategoriesScreenActivity::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6441A5)),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cadeado),
                    contentDescription = null,
                    modifier = Modifier
                        .size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Minhas Senhas",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Gerencia suas senhas com segurança",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }

    if (mostrarDialogoSenha) {
        ConfirmarSenhaEIrParaCamera(
            onSenhaConfirmada = {
                mostrarDialogoSenha = false
                context.startActivity(Intent(context, SignInWithoutPass::class.java))
            },
            onCancelar = { mostrarDialogoSenha = false }
        )
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomePagePreview() {
    HomePage()
}