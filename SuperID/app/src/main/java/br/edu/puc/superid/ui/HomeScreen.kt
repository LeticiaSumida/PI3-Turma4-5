package br.edu.puc.superid.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.puc.superid.CategoriesScreenActivity
import br.edu.puc.superid.R
import br.edu.puc.superid.SignInActivity
import br.edu.puc.superid.SignInWithoutPass
import br.edu.puc.superid.SignUpActivity
import br.edu.puc.superid.ui.theme.branco
import br.edu.puc.superid.ui.theme.roxo
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay

//Composable que representa a tela inicial do aplicativo.
//Exibe opções de login sem senha, gerenciamento de senhas e acesso a configurações da conta.
@Composable
fun HomePage(){
    var context = LocalContext.current
    var mostrarDialogoSenha by remember { mutableStateOf(false) }
    var verificado by remember {mutableStateOf(false)}
    var expanded2 by remember { mutableStateOf(false)}

    var carregando by remember { mutableStateOf(true) }

    // LaunchedEffect usado para verificar o status de verificação do e-mail do usuário.
    LaunchedEffect(Unit) {
        checarVerificado { resultado ->
            verificado = resultado
            carregando = false}
        delay(2000)

    }

    // Layout da barra superior com ícones e título.
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(roxo)
    )
    Row(modifier = Modifier.padding(vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically){
        IconButton(onClick = { expanded2 = !expanded2}
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Sign-Out",
                tint = branco,
            )
        }

        // Menu suspenso que aparece ao clicar no ícone da conta.
        DropdownMenu(
            expanded = expanded2,
            onDismissRequest = { expanded2 = false }
        ) {

            // Item do menu: Logout.
            DropdownMenuItem(
                text = { Text("Logout") },
                leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null) },
                onClick = { FirebaseAuth.getInstance().signOut()
                    val intent = Intent(context, SignInActivity::class.java)
                    context.startActivity(intent)

                }
            )

            // Item do menu : Reenviar verificação
            if (verificado == false) {
                DropdownMenuItem(
                    text = {
                        Text(
                            "Reenviar verificação",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Refresh,
                            contentDescription = null
                        )
                    },
                    onClick = {
                        val user = Firebase.auth.currentUser
                        user?.sendEmailVerification()
                        Toast.makeText(
                            context,
                            "Email Reenviado com sucesso",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            } else {

                // Item do menu: Esqueci minha senha
                DropdownMenuItem(

                    text = {
                        Text(
                            "Esqueci minha senha",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    },
                    leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                    onClick = {
                        val user = Firebase.auth.currentUser
                        var email = user!!.email
                        email = email.toString()
                        Firebase.auth.sendPasswordResetEmail(email)
                        Toast.makeText(
                            context,
                            "Email de redefinição de senha enviado com sucesso",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                )

            }

        }

        // Título "SuperID" na barra superior.
        Text("SuperID", color = branco)

    }

        //Conteúdo Principal da Página
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Exibe um aviso se o e-mail do usuário não estiver verificado.
            if (!verificado) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Aviso",
                        tint = Color(0xFF000000),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Verifique seu email para usar o login sem senha",
                        color = Color(0xFF000000),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Imagem do logotipo.
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
            )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "SuperID",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(48.dp))

        //Botão "Login sem senha"
        Button(
            enabled = verificado,
            onClick = {
                if (verificado){mostrarDialogoSenha = true } },
            colors = ButtonDefaults.buttonColors(containerColor = roxo),
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .width(30.dp)
                ,
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
            Spacer(modifier = Modifier.height(12.dp))

        // Botão "Minhas Senhas"
        Button(
            onClick = {
                val intent = Intent(context, CategoriesScreenActivity::class.java)
                context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = roxo),
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

    // Modal de Senha Mestre
    if (mostrarDialogoSenha) {
        ModalTextField(
            type = MessageType.PASSWORD,
            titulo = "Confirme sua \n Senha Mestre",
            mensagem = "",
            caminhoBotao2 = {
                mostrarDialogoSenha = false
            },
            textoBotao1 = "Confirmar",
            textoBotao2 = "Cancelar",
            onDismiss = { mostrarDialogoSenha = false }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomePagePreview() {
    HomePage()
}

//Função assíncrona para verificar se o e-mail do usuário atual está verificado no Firestore.
fun checarVerificado(callback: (Boolean) -> Unit){
    val db = Firebase.firestore
    val user = Firebase.auth.currentUser
    val uid = user!!.uid
    db.collection("Usuario").document(uid).get()
        .addOnSuccessListener { document ->
            if (document != null){
                val verificado = document.getBoolean("emailVerificado") ?: false
                callback(verificado)
            } else{
                callback(false)
            }
        }
        .addOnFailureListener {  e ->
            callback(false)
        }}