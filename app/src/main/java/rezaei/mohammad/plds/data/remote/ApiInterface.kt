package rezaei.mohammad.plds.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url
import rezaei.mohammad.plds.BuildConfig
import rezaei.mohammad.plds.data.model.request.DocumentStatusRequest
import rezaei.mohammad.plds.data.model.request.LoginRequest
import rezaei.mohammad.plds.data.model.response.DocumentStatusResponse
import rezaei.mohammad.plds.data.model.response.LoginResponse

interface ApiInterface {
    @POST
    suspend fun login(
        @Url url: String = BuildConfig.LoginUrl,
        @Body loginRequest: LoginRequest
    ): LoginResponse?

    @POST("Tracking/RetrieveDocumentStatus")
    suspend fun retrieveDocumentStatus(@Body documentStatusRequest: DocumentStatusRequest): DocumentStatusResponse?
}