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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun WelcomeCarousel( onAceitar: () -> Unit ){
    val paginas = listOf("welcome", "passo1", "passo2", "termos")
    var paginaAtual by remember { mutableStateOf(0) }
    var aceitouTermos by remember { mutableStateOf(false) }

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
                            painter = painterResource(id = br.edu.puc.superid.R.drawable.imagem_default),
                            contentDescription = null,
                            modifier = Modifier.size(300.dp)
                        )
                        Text("Seja bem-vindo!\n", style = MaterialTheme.typography.headlineMedium)
                        Text(
                            "Lorem ipsum dolor sit amet. Rem erf rfrfr numquam commodi 33 temporibus voluptas non Quis voluptates. Et nihil quaerat non natus illum hic expedita numquam ut accusamus beatae.",
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                "passo1" -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = br.edu.puc.superid.R.drawable.imagem_default),
                            contentDescription = null,
                            modifier = Modifier.size(300.dp).padding(16.dp)
                        )
                        Text("Passo 1\n", style = MaterialTheme.typography.headlineMedium)
                        Text(
                            "Lorem ipsum dolor sit amet. Rem erf rfrfr numquam commodi 33 temporibus voluptas non Quis voluptates. Et nihil quaerat non natus illum hic expedita numquam ut accusamus beatae.",
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                "passo2" -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = br.edu.puc.superid.R.drawable.imagem_default),
                            contentDescription = null,
                            modifier = Modifier.size(300.dp).padding(16.dp)
                        )
                        Text("Passo 2\n", style = MaterialTheme.typography.headlineMedium)
                        Text(
                            "Lorem ipsum dolor sit amet. Rem erf rfrfr numquam commodi 33 temporibus voluptas non Quis voluptates. Et nihil quaerat non natus illum hic expedita numquam ut accusamus beatae.",
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                "termos" -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("\n\nTermos e Condições\n", style = MaterialTheme.typography.headlineMedium)
                        Text(
                            "Ao utilizar o aplicativo SuperID, voce concorda com os seguintes termos:\n" +
                                    "O usuário é reponsável pels informacoes fornecidas no cadastro e pelo uso adequado da senha mestre.\n\n" +
                                    "Todas as senhas e dados armazenados sao criptografados, mas o SuperID nao se responsabiliza por perdas decorrentes de acessos nao autorizados causados por negligencia do usuário.\n\n" +
                                    "A funcionalidade de login via QR Code está disponivel apenas para plataformas compativeis e parceiras.\n\n" +
                                    "O usuário concorda em nao utilizar o SuperID para atividades ilegais ou que violem direitos de terceiros.\n\n" +
                                    "Ao prosseguir, voce confirma que leu, compreendeu e aceita integralmente os termos de uso.\n",
                            fontSize = 18.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
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
                }) {
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
                enabled = paginaAtual < paginas.lastIndex || aceitouTermos
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