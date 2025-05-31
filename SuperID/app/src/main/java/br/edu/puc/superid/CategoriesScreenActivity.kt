package br.edu.puc.superid

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.edu.puc.superid.ui.HomePage
import br.edu.puc.superid.ui.MessageType
import br.edu.puc.superid.ui.checarVerificado
import br.edu.puc.superid.ui.theme.SuperIdTheme
import br.edu.puc.superid.ui.theme.branco
import br.edu.puc.superid.ui.theme.cinzaescuro
import br.edu.puc.superid.ui.theme.preto
import br.edu.puc.superid.ui.theme.roxo
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.delay

private lateinit var auth: FirebaseAuth

class CategoriesScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        setContent {
            SuperIdTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val scrollState = rememberScrollState()
                    CategoriaNaTela(scrollState)
                }
            }
        }
    }

    data class ContaSenha(
        var login: String,
        var senha: String
    )

    @Composable
    fun CategoriaNaTela(scrollState: ScrollState) {
        val categorias = remember { mutableStateListOf<String>() }
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current
        val systemUiController = rememberSystemUiController()
        var expanded by remember { mutableStateOf(false) }
        var expanded2 by remember { mutableStateOf(false) }
        var home by remember { mutableStateOf(false) }
        var carregando by remember {mutableStateOf(true)}
        var verificado by remember {mutableStateOf(false)}

        LaunchedEffect(Unit) {
            checarVerificado { resultado ->
                verificado = resultado
                carregando = false}
            delay(2000)

        }
        SideEffect {
            systemUiController.setStatusBarColor(
                color = Color.Black,
                darkIcons = false
            )
        }

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    categorias.clear()
                    CategoriasConta(categorias)
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .background(roxo)
            )
            Row(
                modifier = Modifier.padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { expanded2 = !expanded2 }
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Sign-Out",
                        tint = branco,
                    )
                }
                DropdownMenu(
                    expanded = expanded2,
                    onDismissRequest = { expanded2 = false }
                ) {
                    DropdownMenuItem(

                        text = { Text("Home") },
                        leadingIcon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                        onClick = {
                            home = true
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Logout") },
                        leadingIcon = {
                            Icon(
                                Icons.AutoMirrored.Outlined.Logout,
                                contentDescription = null
                            )
                        },
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            val intent = Intent(context, SignInActivity::class.java)
                            context.startActivity(intent)
                            finish()
                        }
                    )

                    if (verificado == false) {
                        DropdownMenuItem(
                            text = { Text("Reenviar verificação") },
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
                        DropdownMenuItem(

                            text = { Text("Esqueci minha senha") },
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

                Text("SuperID", color = branco)
            }




            if (home) {
                HomePage()
                finish()
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp + 16.dp)
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(100.dp))
                Text(
                    text = "Minhas senhas",
                    fontSize = 33.sp,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(50.dp))
                Text(
                    text = "Categorias",
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    modifier = Modifier.align(Alignment.Start)
                )
                categorias.forEach { cat ->
                    expandableCard(
                        categoria = cat,
                        onCategoriaRemovida = { categorias.remove(cat) }
                    )

                }
            }

            val transition = updateTransition(targetState = expanded, label = "transition")
            val rotation by transition.animateFloat(label = "rotation") { if (it) 315f else 0f }

            FloatingActionButton(
                onClick = { expanded = !expanded },
                containerColor = roxo,
                contentColor = branco,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-24).dp, y = (-50).dp)
                    .size(45.dp)
                    .rotate(rotation),

                ) {
                Icon(Icons.Filled.Add, "Floating action button.")
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + slideInVertically { fullHeight -> fullHeight / 3 } + scaleIn(),
                exit = fadeOut() + slideOutVertically { fullHeight -> fullHeight / 3 } + scaleOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(y = (-115).dp)
                    .padding(vertical = 2.dp)
                    .padding(end = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                ) {
                    ExtendedFloatingActionButton(
                        modifier = Modifier
                            .padding(start = 10.dp),
                        onClick = {
                            val intent = Intent(context, CategoryActivity::class.java)
                            context.startActivity(intent)
                        },
                        containerColor = roxo,
                        contentColor = branco
                    ) {
                        Text("Nova categoria")
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    ExtendedFloatingActionButton(
                        modifier = Modifier
                            .padding(start = 30.dp),
                        onClick = {
                            val intent = Intent(context, PasswordActivity::class.java)
                            context.startActivity(intent)
                        },
                        containerColor = roxo,
                        contentColor = branco
                    ) {
                        Text("Nova senha")
                    }
                }
            }
        }
    }

    @Composable
    fun expandableCard(categoria: String, onCategoriaRemovida: () -> Unit) {
        val context = LocalContext.current
        var expandedState by remember { mutableStateOf(false) }
        val rotationState by animateFloatAsState(
            targetValue = if (expandedState) 180f else 0f
        )
        var mostrarDialog by remember { mutableStateOf(false) }


        val senhas = remember { mutableStateListOf<ContaSenha>() }

        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    senhas.clear()
                    senhasConta(senhas, categoria)
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        Card(
            modifier = Modifier

                .padding(top = 30.dp)

                .fillMaxWidth()
                .animateContentSize(
                    animationSpec = tween(
                        delayMillis = 100,
                        easing = LinearOutSlowInEasing
                    )
                )
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(16),
                    ambientColor = cinzaescuro, // Roxo mais claro para a sombra
                    spotColor = preto

                )
                .clickable {
                    expandedState = !expandedState // Alterna o estado ao clicar
                },
            colors = CardDefaults.cardColors(
                containerColor = roxo,
            ),
            shape = RoundedCornerShape(16)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)

            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        modifier = Modifier
                            .rotate(rotationState),

                        onClick = {
                            expandedState = !expandedState
                        }

                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expandir",
                            tint = branco
                        )
                    }
                    Text(
                        modifier = Modifier,
                        fontSize = 22.sp,
                        color = branco,
                        text = categoria,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (!categoria.equals("Sites Web", ignoreCase = true)) {
                        IconButton(
                            modifier = Modifier,
                            onClick = {
                                mostrarDialog = true
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Deletar categoria",
                                tint = branco
                            )
                        }
                    }


                }
                if (expandedState) {
                    Spacer(modifier = Modifier.height(16.dp))
                    senhas.forEach { conta ->
                        mostrarSenhas(conta, senhas)
                        HorizontalDivider(
                            Modifier
                                .padding(horizontal = 33.dp)
                                .padding(bottom = 10.dp),
                            color = Color.LightGray
                        )
                    }
                }
                if (mostrarDialog) {
                    AlertDialog(
                        onDismissRequest = { mostrarDialog = false },
                        title = null,
                        text = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Deseja mesmo deletar essa categoria?",
                                    color = branco,
                                    textAlign = TextAlign.Center,
                                    fontSize = 28.sp,
                                    lineHeight = 36.sp
                                )
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "Ícone de deletar",
                                    tint = branco,
                                    modifier = Modifier
                                        .size(200.dp)
                                        .padding(bottom = 20.dp)
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                Column {
                                    Button(
                                        onClick = {
                                            removerCategoriaFirestore(
                                                categoria = categoria,
                                                onSucesso = {
                                                    onCategoriaRemovida()
                                                    mostrarDialog = false
                                                }
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = branco),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(bottom = 12.dp)
                                    ) {
                                        Text("Sim", color = roxo, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            mostrarDialog = false
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = roxo),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth()
                                            .height(35.dp)
                                            .border(
                                                width = 2.dp,
                                                color = Color.White,
                                                shape = RoundedCornerShape(12.dp),
                                            )
                                    ) {
                                        Text("Não", color = branco, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        },
                        confirmButton = {},
                        containerColor = roxo,
                        shape = RoundedCornerShape(20.dp),
                        tonalElevation = 8.dp
                    )
                }

            }

        }
    }


    @Composable
    fun mostrarSenhas(
        conta: ContaSenha,
        senhas: SnapshotStateList<ContaSenha>


    ) {

        var textlogin = conta.login
        var text = conta.senha
        var textMasked = "*".repeat(text.length)
        var checked by remember { mutableStateOf(false) }
        var alterarSenha by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .padding(3.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(5f)) {
                    Text(
                        if (checked) text else textMasked,
                        modifier = Modifier,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        textlogin,
                        modifier = Modifier,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }


                IconButton(
                    modifier = Modifier,


                    onClick = {
                        checked = !checked
                    }

                ) {
                    Icon(imageVector = Icons.Filled.VisibilityOff, contentDescription = "Ver senha", tint = branco)
                }
                IconButton(
                    modifier = Modifier,


                    onClick = {
                        alterarSenha = true
                    }

                )
                {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar senha", tint=branco)
                }
                IconButton(
                    modifier = Modifier,

                    onClick = {
                        removerFirestoreSenha(
                            conta.senha, conta.login,
                            onSuccess = {
                                senhas.remove(conta)
                                Log.d("UI", "Senha deletada com sucesso")

                            },
                            onFailure = { e ->
                                Log.e("UI", "Erro ao deletar senha: ${e.message}")
                            })
                    }

                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Deletar senha",tint=branco)
                }
                if (alterarSenha) {
                    ModalTextField(
                        type = MessageType.PASSWORD,
                        titulo = "Quer alterar esta senha?",
                        mensagem = conta.login,
                        textoBotao1 = "Alterar",
                        textoBotao2 = "Cancelar",
                        onConfirm = { novaSenha ->
                            editarFirestoreSenha(
                                conta.login, conta.senha, novaSenha,
                                onSuccess = {
                                    conta.senha = novaSenha
                                    alterarSenha = false
                                },
                                onFailure = {
                                    Log.e("Erro", "Falha ao editar senha")
                                    alterarSenha = false
                                }
                            )
                        },
                        onDismiss = { alterarSenha = false }
                    )
                }
            }

        }
    }


    fun senhasConta(senhas: SnapshotStateList<ContaSenha>, categoria: String) {
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        val uid = user!!.uid

        db.collection("Usuario").document(uid).collection("senhas")
            .whereEqualTo("Categoria", categoria)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val senha = document.getString("Senha")
                    val login = document.getString("Login")

                    if (senha != null && login != null) {
                        senhas.add(ContaSenha(login, senha))
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Categoria", "Erro ao buscar categorias", exception)
            }
    }


    fun CategoriasConta(categorias: SnapshotStateList<String>) {
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        val uid = user!!.uid


        db.collection("Usuario").document(uid).collection("categorias")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val categoria = document.getString("nome")
                    if (categoria != null) {
                        categorias.add(categoria)
                    }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Categoria", "Erro ao buscar categorias", exception)
            }
    }


    fun removerFirestoreSenha(

        senha: String,
        login: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}

    ) {
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        val uid = user!!.uid


        db.collection("Usuario").document(uid).collection("senhas")
            .whereEqualTo("Senha", senha)
            .whereEqualTo("Login", login)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.d("Firestore", "Senha deletada com sucesso.")
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Erro ao deletar senha", e)
                            onFailure(e)
                        }
                }


            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Erro ao buscar a senha", e)
                onFailure(e)
            }
    }

    fun removerCategoriaFirestore(
        categoria: String,
        onSucesso: () -> Unit,

        ) {
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser
        val uid = user!!.uid


        db.collection("Usuario").document(uid).collection("categorias").whereEqualTo("nome", categoria).whereEqualTo("deletavel", true)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.delete()
                        .addOnSuccessListener {
                            Log.d("Firestore", "Categoria deletada com sucesso.")
                            onSucesso()
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Erro ao deletar Categoria ", e)
                        }
                }

            }.addOnFailureListener { e ->
                Log.w("Firestore", "Erro ao buscar a categoria", e)

            }
    }




    fun editarFirestoreSenha(
        login: String,
        senhaAntiga: String,
        novaSenha: String,
        onSuccess: () -> Unit = {},
        onFailure: (Exception) -> Unit = {}
    ) {
        val db = Firebase.firestore
        val user = Firebase.auth.currentUser ?: return
        val uid = user.uid

        db.collection("Usuario").document(uid).collection("senhas")
            .whereEqualTo("Login", login)
            .whereEqualTo("Senha", senhaAntiga)
            .get()
            .addOnSuccessListener { querySnapshot ->
                for (document in querySnapshot) {
                    document.reference.update("Senha", novaSenha)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Senha atualizada com sucesso")
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Erro ao atualizar senha", e)
                            onFailure(e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao buscar senha para editar", e)
                onFailure(e)
            }
    }

    @Composable
    fun ModalTextField(
        type: MessageType,
        titulo: String,
        mensagem: String,
        textoBotao1: String,
        textoBotao2: String,
        onConfirm: (String) -> Unit,
        onDismiss: () -> Unit = {}
    ) {
        var password by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = onDismiss,

            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = titulo)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = mensagem,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                    if (type == MessageType.PASSWORD) {
                        TextField(
                            modifier = Modifier
                                .padding(vertical = 10.dp, horizontal = 12.dp)
                                .fillMaxWidth(),
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Nova Senha") },
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }

                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),

                    ) {
                    Button(
                        modifier = Modifier.weight(2f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xff000000)
                        ), onClick = { onConfirm(password) }) {
                        Text(text = textoBotao1)
                    }
                    Button(
                        modifier = Modifier.weight(2f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xff000000)
                        ), onClick = { onDismiss() }) {
                        Text(text = textoBotao2)
                    }
                }

            }
        )
    }
}