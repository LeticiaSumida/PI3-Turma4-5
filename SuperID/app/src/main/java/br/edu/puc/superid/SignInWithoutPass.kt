package br.edu.puc.superid

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import br.edu.puc.superid.permissions.TelaSolicitaPermissaoCamera
import br.edu.puc.superid.ui.theme.SuperIdTheme
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

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

    private fun startBarcodeScanner() {
        val options = GmsBarcodeScannerOptions.Builder().build()
        val scanner = GmsBarcodeScanning.getClient(this, options)

        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val loginToken = barcode.rawValue
                if (!loginToken.isNullOrEmpty()) {
                    updateLoginDocument(loginToken)
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


    private fun updateLoginDocument(loginToken: String) {
        val db = Firebase.firestore
        val currentUser = Firebase.auth.currentUser

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

    private fun showSuccessPopup(message: String) {
//        setContent {
//            SuperIdTheme {
//                PopUpScreen(message, Icons.Default.CheckCircle, MaterialTheme.colorScheme.primary) {
//                    setResult(RESULT_OK)
                    finish()
//                }
//            }
//        }
    }

    private fun showFailurePopup(message: String) {
//        setContent {
//            SuperIdTheme {
//                AutoDismissPopup(
//                    message = message,
//                    icon = Icons.Default.Cancel,
//                    iconColor = MaterialTheme.colorScheme.error
//                ) {
//                    setResult(RESULT_CANCELED)
                    finish()
//                }
//            }
//        }
    }
}
