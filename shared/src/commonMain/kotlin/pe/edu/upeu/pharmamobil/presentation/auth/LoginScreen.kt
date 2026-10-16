
package pe.edu.upeu.pharmamobil.presentation.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    alIniciarSesion: () -> Unit,
    viewModel: LoginViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    var usuario by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

    LaunchedEffect(estado.autenticado) {
        if (estado.autenticado) alIniciarSesion()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("PharmaMobil", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(8.dp))
        Text("Iniciar sesión")
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = clave,
            onValueChange = { clave = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))

        Button(
            onClick = { viewModel.iniciarSesion(usuario, clave) },
            enabled = !estado.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (estado.cargando) "Ingresando..." else "Ingresar")
        }

        if (estado.cargando) {
            Spacer(Modifier.height(12.dp))
            CircularProgressIndicator()
        }

        estado.error?.let { mensaje ->
            Spacer(Modifier.height(12.dp))
            Text(mensaje, color = MaterialTheme.colorScheme.error)
        }
    }
}
