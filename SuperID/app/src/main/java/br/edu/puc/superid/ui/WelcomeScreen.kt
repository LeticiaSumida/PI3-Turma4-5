package br.edu.puc.superid.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.R
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


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
            targetState = paginas[paginaAtual],
            transitionSpec = {
                (slideInHorizontally { fullWidth -> fullWidth } + fadeIn()).togetherWith(
                    slideOutHorizontally { fullWidth -> -fullWidth } + fadeOut())
            },
            label = "Transição de Página"
        ) { pagina ->
            when (pagina) {
                "welcome" -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = br.edu.puc.superid.R.drawable.aaaaaa),
                            contentDescription = null,
                            modifier = Modifier.size(300.dp)
                        )
                        Text("Seja bem-vindo!", style = MaterialTheme.typography.headlineMedium)
                        Text(
                            "Estamos felizes em ter você aqui.",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(top = 0.dp)
                        )
                    }
                }

                "passo1" -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = br.edu.puc.superid.R.drawable.aaaaaa),
                            contentDescription = null,
                            modifier = Modifier.size(300.dp).padding(16.dp)
                        )
                        Text("Passo 1", style = MaterialTheme.typography.headlineMedium)
                        Text("Navegue facilmente pelo app.", style = MaterialTheme.typography.bodyLarge)
                    }
                }

                "passo2" -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = br.edu.puc.superid.R.drawable.aaaaaa),
                            contentDescription = null,
                            modifier = Modifier.size(300.dp).padding(16.dp)
                        )
                        Text("Passo 2", style = MaterialTheme.typography.headlineMedium)
                        Text("Gerencie suas tarefas rapidamente.", style = MaterialTheme.typography.bodyLarge)
                    }
                }

                "termos" -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("\n\nTermos e Condições\n", style = MaterialTheme.typography.headlineMedium)
                        Text(
                            "Ao utilizar o aplicativo SuperID, voce concorda com os seguintes termos:\n\n" +
                                    "O usuário é reponsável pels informacoes fornecidas no cadastro e pelo uso adequado da senha mestre.\n\n" +
                                    "Todas as senhas e dados armazenados sao criptografados, mas o SuperID nao se responsabiliza por perdas decorrentes de acessos nao autorizados causados por negligencia do usuário.\n\n" +
                                    "A funcionalidade de login via QR Code está disponivel apenas para plataformas compativeis e parceiras.\n\n" +
                                    "O usuário concorda em nao utilizar o SuperID para atividades ilegais ou que violem direitos de terceiros.\n\n" +
                                    "Ao prosseguir, voce confirma que leu, compreendeu e aceita integralmente os termos de uso.\n",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = aceitouTermos, onCheckedChange = { aceitouTermos = it })
                            Text("Li e aceito os termos.")
                        }
                    }
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
            modifier = Modifier.align(Alignment.End),
            enabled = paginaAtual < paginas.lastIndex || aceitouTermos
        ) {
            Text(if (paginaAtual == paginas.lastIndex) "Finalizar" else "Próximo")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TutorialScreenPreview() {
    WelcomeCarousel(onAceitar = {})
}