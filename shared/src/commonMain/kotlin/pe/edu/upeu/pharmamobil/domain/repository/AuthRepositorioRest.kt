
package pe.edu.upeu.pharmamobil.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.Serializable
import pe.edu.upeu.pharmamobil.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobil.domain.model.Credenciales
import pe.edu.upeu.pharmamobil.domain.model.Sesion
import pe.edu.upeu.pharmamobil.domain.repository.AuthRepository

@Serializable
private data class LoginRequest(
    val username: String,
    val password: String
)

@Serializable
private data class LoginResponse(
    val token: String,
    val tipo: String = "Bearer",
    val expiraEnSegundos: Long = 3600,
    val username: String = "",
    val nombreCompleto: String = "",
    val roles: Set<String> = emptySet(),
    val permisos: Set<String> = emptySet()
)

class AuthRepositorioRest(
    private val cliente: HttpClient,
    private val urlBase: String,
    private val almacen: AlmacenSeguro
) : AuthRepository {

    private var sesionActual: Sesion? = null

    override suspend fun iniciarSesion(
        credenciales: Credenciales
    ): Sesion {

        val respuesta: LoginResponse = cliente.post(
            "${urlBase.trimEnd('/')}/auth/login"
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequest(
                    username  = credenciales.usuario,
                    password  = credenciales.clave
                )
            )
        }.body()

        val sesion = Sesion(
            token = respuesta.token,
            usuario = respuesta.username.ifBlank {
                credenciales.usuario
            },
            nombreCompleto = respuesta.nombreCompleto,
            permisos = respuesta.permisos
        )

        almacen.guardarToken(sesion.token)
        sesionActual = sesion

        return sesion
    }

    override suspend fun sesionGuardada(): Sesion? {
        if (almacen.leerToken() == null) return null
        return sesionActual
    }

    override suspend fun cerrarSesion() {
        almacen.borrarToken()
        sesionActual = null
    }
}
