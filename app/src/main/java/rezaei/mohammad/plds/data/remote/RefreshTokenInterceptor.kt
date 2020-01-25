package rezaei.mohammad.plds.data.remote

import ir.co.yalda.android.yalda360.data.prefs.Prefs
import ir.co.yalda.android.yalda360.network.ApiService
import ir.co.yalda.android.yalda360.network.request.LoginRequest
import ir.co.yalda.android.yalda360.ui.login.LoginError
import okhttp3.Interceptor
import okhttp3.Response

class RefreshTokenInterceptor(
    private var prefs: Lazy<Prefs>,
    private var apiClient: Lazy<ApiService>
) : Interceptor {
    var isRefreshing = false
    override fun intercept(chain: Interceptor.Chain): Response? {
        val response = chain.proceed(chain.request())
        val isAuthorize = response?.code() != 401
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
        val loginRequestBody = createLoginRequest(prefs.value.username, prefs.value.password, true)
        val call = apiClient.value.userLogin(loginRequestBody)
        val response = call.execute()
        return when {
            response.isSuccessful -> if (response.body()?.errorCode != LoginError.FAILED_LOGIN.code) {

                val accessToken =
                    response.body()?.outputJson?.find { item -> item?.name.equals("accessToken") }
                        ?.value

                accessToken?.let {
                    prefs.value.accessToken = it
                }
                true

            } else {
                false
            }
            else -> false
        }
    }

    private fun createLoginRequest(
        username: String?,
        password: String?,
        confirm: Boolean
    ): LoginRequest {
        val loginRequestBody = LoginRequest()
        loginRequestBody.userName = username
        loginRequestBody.password = password
        loginRequestBody.confirm = confirm
        loginRequestBody.platformEnName = "android"
        return loginRequestBody

    }

}