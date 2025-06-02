package br.edu.puc.superid

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import br.edu.puc.superid.permissions.TelaSolicitaPermissaoCamera
import br.edu.puc.superid.ui.theme.SuperIdTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import java.lang.reflect.Modifier


class SignInWithoutPass : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperIdTheme {
                TelaSolicitaPermissaoCamera(
                    onPermissionGranted = { startBarcodeScanner() }
                )
            }
        }
    }
    // Função para iniciar o scanner de código de barras
    private fun startBarcodeScanner() {
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE
            )
            .build()
        val scanner = GmsBarcodeScanning.getClient(this, options)

        // Inicia o escaneamento
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val loginToken = barcode.rawValue

                // Se o token do QR Code não estiver vazio
                if (!loginToken.isNullOrEmpty()) {
                    updateLoginDocument(loginToken)
                    showSuccessPopup(loginToken)
                } else {
                    showFailurePopup("QR Code inválido: Token vazio")
                }
            }
            .addOnCanceledListener {
                showFailurePopup("Escaneamento de QR Code cancelado.")
            }
            .addOnFailureListener { e ->
                Log.e("QRCODE_SCANNER", "Erro ao escanear QR Code: ${e.message}")
                showFailurePopup("Erro ao escanear QR Code!")
            }
    }

    // Função para atualizar o documento no Firestore com o login via QR Code
    private fun updateLoginDocument(loginToken: String) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser

        // Se o usuário não estiver autenticado
        if (currentUser == null) {
            showFailurePopup("Usuário não autenticado no aplicativo.")
            return
        }

        val userUid = currentUser.uid
        val loginDocRef = db.collection("login").document(loginToken)

        val updates = hashMapOf<String, Any>(
            "user" to userUid,
            "loggedInAt" to FieldValue.serverTimestamp()
        )

        loginDocRef.update(updates)
            .addOnSuccessListener {
                Log.d("FIRESTORE", "Documento de login atualizado com sucesso para token: $loginToken com UID: $userUid")
                showSuccessPopup("Login realizado com sucesso via QR Code!")
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE", "Erro ao atualizar documento de login: ${e.message}", e)
                showFailurePopup("Erro ao finalizar login via QR Code!")
            }
    }

    // Exibe um popup de sucesso
    private fun showSuccessPopup(message: String) {
        setContent {
            SuperIdTheme {
                PopUpScreen(message, Icons.Default.CheckCircle) {
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }
    // Exibe um popup de erro
    private fun showFailurePopup(message: String) {
        setContent {
            SuperIdTheme {
                PopUpScreen(
                    message = message,
                    icon = Icons.Default.Cancel,
                ) {
                    setResult(RESULT_CANCELED)
                    finish()
                }
            }
        }
    }
}
// Composable popup
@Composable
fun PopUpScreen(
    message: String,
    icon: ImageVector,onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Icon(
                icon,
                contentDescription = null,
            )
        },
        text = {
            Text(message)
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}
