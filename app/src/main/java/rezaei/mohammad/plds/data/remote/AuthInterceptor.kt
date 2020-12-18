package rezaei.mohammad.plds.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import rezaei.mohammad.plds.data.preference.PreferenceManager

class AuthInterceptor(private val preferenceManager: PreferenceManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder().addHeader(
            "Authorization",
            "Bearer ${preferenceManager.authToken}"
        )
        // execute request
        val response = chain.proceed(request.build())

        //save token from requests
        val token = response.header("JAToken")
        token?.let { preferenceManager.authToken = it }

        return response
    }
}