package rezaei.mohammad.plds.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import rezaei.mohammad.plds.data.preference.PreferenceManager

class AuthInterceptor(private val preferenceManager: PreferenceManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
        builder.addHeader("Authorization", "Bearer ${preferenceManager.authToken}")
        val newRequest = builder.build()
        return chain.proceed(newRequest)
    }
}