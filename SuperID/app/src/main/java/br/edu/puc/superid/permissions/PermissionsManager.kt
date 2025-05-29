package br.edu.puc.superid.permissions

import android.Manifest
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import br.edu.puc.superid.SignInWithoutPass

@Composable
fun CameraXScreen(activity: Activity) {
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    )

    LaunchedEffect(lensFacing) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview
            )
        } catch (e: Exception) {
            Log.e("CameraX", "Erro ao iniciar preview", e)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK)
                        CameraSelector.LENS_FACING_FRONT
                    else
                        CameraSelector.LENS_FACING_BACK
                }
            ) {
                Text("Trocar")
            }
        }

        Button(
            onClick = { activity.finish() },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text("Voltar")
        }
    }
}

@Composable
fun TelaSolicitaPermissaoCamera(
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    var permissaoConcedida by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissaoConcedida = granted
        if (granted) {
            onPermissionGranted()
        } else {
            Toast.makeText(context, "Permissão negada", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.CAMERA)
    }
}