package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.auth.providers.BearerTokens
import pe.edu.upeu.pharmamobil.data.local.AlmacenSeguro


fun crearHttpClient(
    engine: HttpClientEngine,
    almacenSeguro: AlmacenSeguro
): HttpClient {
    return HttpClient(engine) {
        expectSuccess = true

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
                encodeDefaults = true
            })
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    println("KTOR_LOG: $message")
                }
            }
            level = LogLevel.HEADERS
            sanitizeHeader { header ->
                header == HttpHeaders.Authorization
            }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 15_000L
            connectTimeoutMillis = 15_000L
            socketTimeoutMillis = 15_000L
        }

        install(DefaultRequest) {
            header(HttpHeaders.ContentType, "application/json")
        }

        install(Auth) {
            bearer {
                loadTokens {
                    almacenSeguro.leerToken()?.let { token ->
                        BearerTokens(token, "")
                    }
                }


                sendWithoutRequest { request ->
                    request.url.encodedPathSegments
                        .takeLast(2) != listOf("auth", "login")
                }

            }
        }
    }
}