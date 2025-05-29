package br.edu.puc.superid.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.ButtonDefaults
import br.edu.puc.superid.ui.theme.branco
import br.edu.puc.superid.ui.theme.roxo


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WelcomeCarousel( onAceitar: () -> Unit ){
    val paginas = listOf("welcome", "passo1", "passo2", "termos")
    var paginaAtual by remember { mutableStateOf(0) }
    var aceitouTermos by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(roxo)

    )
    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically){
        Text("SuperID", color = branco,
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AnimatedContent(
            targetState = paginaAtual,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally { width -> width } + fadeIn() with
                            slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() with
                            slideOutHorizontally { width -> width } + fadeOut()
                }.using(SizeTransform(clip = false))
            }
        ) { index ->
            val pagina = paginas[index]
            when (pagina) {
                "welcome" -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = br.edu.puc.superid.R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier.size(300.dp)
                        )
                        Text("Seja bem-vindo!\n", style = MaterialTheme.typography.headlineMedium)
                        Text(
                            "Você acaba de dar o primeiro passo para simplificar e proteger sua vida digital!\n\nCom o Super ID, você pode armazenar todas as suas senhas com segurança, acessá-las facilmente sempre que precisar e manter suas informações organizadas em um só lugar.\n\nSe esquecer uma senha, basta consultar o Super ID em poucos toques, sem precisar redefinir tudo novamente. A proposta é oferecer praticidade sem abrir mão da segurança.",
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                "passo1" -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = br.edu.puc.superid.R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier.size(250.dp).padding(16.dp)
                        )
                        Text(
                            "Comece sua jornada com o SuperID!\n",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "1. Para começar a usar o SuperID, crie sua conta.\n\n2. Preencha seu nome, e-mail e defina uma senha mestre — ela será usada sempre que quiser acessar o app.\n\n3. Após o cadastro, verifique seu e-mail e confirme sua conta. Esse passo é importante para desbloquear todas as funcionalidades.\n\n4. Com tudo pronto, basta fazer login usando sua senha mestre.\n\n5. Agora vamos te mostrar como o app funciona!",
                                fontSize = 18.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Justify
                            )
                        }
                    }
                }

                "passo2" -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = br.edu.puc.superid.R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier.size(300.dp).padding(16.dp)
                        )
                        Text("Como funciona?\n", style = MaterialTheme.typography.headlineMedium)
                        Text(
                            "1. No app, você pode salvar senhas de sites e aplicativos que usa com frequência\n\n2. Cada senha é armazenada com segurança e pode ser organizada em categorias para facilitar sua consulta.\n\n3. Você também pode entrar em sites escaneando um QR Code com o app, sem digitar senha.\n\n4. Pronto! Após aceitar os termos de uso, você já está pronto para iniciar seu cadastro!",
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Justify
                        )
                    }
                }

                "termos" -> {
                    val scrollState = rememberScrollState()
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("\n\nTermos e Condições\n", style = MaterialTheme.typography.headlineMedium)
                        Text(
                            "Ao utilizar o aplicativo SuperID, você concorda com os seguintes termos:\n\n" +
                                    "•O usuário é reponsável pelas informações fornecidas no cadastro e pelo uso adequado da senha mestre.\n\n" +
                                    "•Todas as senhas e dados armazenados são criptografados, mas o SuperID não se responsabiliza por perdas decorrentes de acessos não autorizados causados por negligência do usuário.\n\n" +
                                    "•A funcionalidade de login via QR Code está disponivel apenas para plataformas compatíveis e parceiras.\n\n" +
                                    "•O usuário concorda em não utilizar o SuperID para atividades ilegais ou que violem direitos de terceiros.\n\n" +
                                    "•Ao prosseguir, você confirma que leu, compreendeu e aceita integralmente os termos de uso.\n",
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth()
                                .verticalScroll(scrollState),
                            textAlign = TextAlign.Justify
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = aceitouTermos, onCheckedChange = { aceitouTermos = it })
                            Text("Li e aceito os termos.")
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 25.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (paginaAtual != 0) {
                Button(onClick = {
                    if (paginaAtual > 0) paginaAtual--
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF5847AA),
                    contentColor = Color.White
                )) {
                    Text("Voltar")
                }
            } else {
                Spacer(modifier = Modifier.width(80.dp))
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(paginas.size) { index ->
                    val isSelected = paginaAtual == index
                    val animatedSize by animateDpAsState(targetValue = if (isSelected) 14.dp else 10.dp)
                    val animatedColor by animateColorAsState(
                        targetValue = if (isSelected) Color.Black else Color.LightGray
                    )

                    Box(
                        modifier = Modifier
                            .size(animatedSize)
                            .clip(CircleShape)
                            .background(animatedColor)
                    )

                    if (index != paginas.lastIndex) {
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                }
            }

            Button(
                onClick = {
                    if (paginaAtual < paginas.lastIndex) {
                        paginaAtual++
                    } else if (aceitouTermos) {
                        onAceitar()
                    }
                },
                enabled = paginaAtual < paginas.lastIndex || aceitouTermos,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5847AA),
                    contentColor = Color.White,
                )
            ) {
                Text(if (paginaAtual == paginas.lastIndex) "Finalizar" else "Próximo")
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun TutorialScreenPreview() {
    WelcomeCarousel(onAceitar = {})
}