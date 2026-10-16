
package pe.edu.upeu.pharmamobil.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobil.domain.model.Credenciales
import pe.edu.upeu.pharmamobil.domain.repository.AuthRepository

data class LoginEstado(
    val cargando: Boolean = false,
    val autenticado: Boolean = false,
    val error: String? = null
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _estado = MutableStateFlow(LoginEstado())
    val estado = _estado.asStateFlow()

    fun iniciarSesion(usuario: String, clave: String) {
        if (usuario.isBlank() || clave.isBlank()) {
            _estado.value = LoginEstado(
                error = "Completa usuario y contraseña"
            )
            return
        }

        viewModelScope.launch {
            _estado.value = LoginEstado(cargando = true)

            try {
                authRepository.iniciarSesion(
                    Credenciales(usuario.trim(), clave)
                )
                _estado.value = LoginEstado(autenticado = true)
            } catch (e: Exception) {
                e.printStackTrace()
                _estado.value = LoginEstado(
                    error = "No se pudo iniciar sesión. Verifica tus datos y la conexión."
                )
            }
        }
    }
}
