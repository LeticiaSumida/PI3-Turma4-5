package br.edu.puc.superid

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import br.edu.puc.superid.permissions.CameraXScreen
import br.edu.puc.superid.ui.theme.SuperIdTheme

class SignInWithoutPass : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuperIdTheme {
                TelaSolicitaPermissaoCamera()
            }
        }
    }


    @Composable
    fun TelaSolicitaPermissaoCamera() {
        val context = LocalContext.current
        var permissaoConcedida by remember { mutableStateOf(false) }

        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            permissaoConcedida = granted
            if (!granted) {
                Toast.makeText(context, "Permissão negada", Toast.LENGTH_SHORT).show()
            }
        }

        LaunchedEffect(Unit) {
            launcher.launch(Manifest.permission.CAMERA)
        }

        if (permissaoConcedida) {
            CameraXScreen(activity = this@SignInWithoutPass)
        }
    }
}