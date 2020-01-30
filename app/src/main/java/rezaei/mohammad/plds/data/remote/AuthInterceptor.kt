package rezaei.mohammad.plds.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import rezaei.mohammad.plds.data.preference.PreferenceManager

class AuthInterceptor(private val preferenceManager: PreferenceManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()

        //add token to each request
        builder.header("Authorization", "Bearer ${preferenceManager.authToken}")

        //save token from requests
        val token = request.header("jatoken")
        token?.let { preferenceManager.authToken = it }

        val newRequest = builder.build()
        return chain.proceed(newRequest)
    }
}