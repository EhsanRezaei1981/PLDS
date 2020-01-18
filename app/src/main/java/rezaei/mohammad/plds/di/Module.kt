package rezaei.mohammad.plds.di

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import rezaei.mohammad.plds.data.preference.PreferenceManager
import rezaei.mohammad.plds.data.remote.ApiInterface
import rezaei.mohammad.plds.data.remote.RemoteRepository
import rezaei.mohammad.plds.views.login.LoginViewModel

object Module {
    val pldsModule = module {

        //ok http
        single {
            OkHttpClient.Builder().apply {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }.build()
        }

        //retrofit
        single {
            Retrofit.Builder()
                .baseUrl("https://JarvisUAT.Prosource.co.za/")
                .client(get())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiInterface::class.java)
        }

        //remote repository
        single {
            RemoteRepository(get())
        }

        //pref manager
        single { PreferenceManager(androidContext()) }

        viewModel { LoginViewModel(get(), get()) }
    }
}