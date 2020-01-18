package rezaei.mohammad.plds.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import rezaei.mohammad.plds.data.model.request.LoginRequest
import rezaei.mohammad.plds.data.model.response.LoginResponse

interface ApiInterface {
    @POST("WebCore/Account/Authenticate")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse?
}