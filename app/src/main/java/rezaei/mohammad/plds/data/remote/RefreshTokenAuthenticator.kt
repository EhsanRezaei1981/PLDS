package rezaei.mohammad.plds.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import rezaei.mohammad.plds.data.Result
import rezaei.mohammad.plds.data.preference.PreferenceManager

class RefreshTokenAuthenticator(
    private val prefs: Lazy<PreferenceManager>,
    private val remoteRepository: Lazy<RemoteRepository>
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            login()
            return@runBlocking response.request.newBuilder()
                .header("Authorization", "Bearer ${prefs.value.authToken}")
                .build()
        }

    }

    private suspend fun login(): Boolean {
        var result = false
        return withContext(Dispatchers.IO) {
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
            return@withContext result
        }
    }

}