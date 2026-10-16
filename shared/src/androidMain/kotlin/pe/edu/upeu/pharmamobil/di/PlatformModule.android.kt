
package pe.edu.upeu.pharmamobil.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.platform.CompartidorAndroid
import pe.edu.upeu.pharmamobil.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobil.data.local.AlmacenSeguroAndroid

actual val platformModule: Module = module {

    single<HttpClientEngine> {
        OkHttp.create()
    }

    single(named("urlBase")) {
        "http://172.20.10.3:8080/api/v1/"
    }

    single<Compartidor> {
        CompartidorAndroid(androidContext())
    }

    // Almacenamiento seguro del token JWT
    single<AlmacenSeguro> {
        AlmacenSeguroAndroid(androidContext())
    }
}
