package rezaei.mohammad.plds

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import rezaei.mohammad.plds.di.Module

class PLDSapp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PLDSapp)
            modules(Module.pldsModule)
        }
    }
}