package rezaei.mohammad.plds.data.remote

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Interceptor
import okhttp3.Response
import rezaei.mohammad.plds.data.Result
import rezaei.mohammad.plds.data.preference.PreferenceManager

class RefreshTokenInterceptor(
    private val prefs: Lazy<PreferenceManager>,
    private val remoteRepository: Lazy<RemoteRepository>
) : Interceptor {
    private var isRefreshing = false
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val isAuthorize = response.code != 401
        if (!isAuthorize && !isRefreshing) {
            isRefreshing = true
            return if (login())
                chain.proceed(chain.request())
            else
                response

        }
        return response
    }

    private fun login(): Boolean {
        var result = false
        GlobalScope.launch {
            val response = remoteRepository.value.login(
                prefs.value.username ?: "",
                prefs.value.password ?: ""
            )
            (response as? Result.Success)?.let {
                it.response.data?.jAToken?.let {
                    prefs.value.authToken = it
                    result = true
                }
            }
        }
        return result
    }

}