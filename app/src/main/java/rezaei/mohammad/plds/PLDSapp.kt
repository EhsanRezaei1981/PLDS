package rezaei.mohammad.plds

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import rezaei.mohammad.plds.data.model.response.LoginResponse
import rezaei.mohammad.plds.di.Module

class PLDSapp : Application() {

    companion object {
        var currentUser: LoginResponse.Data? = null
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PLDSapp)
            modules(Module.pldsModule)
        }
    }
}