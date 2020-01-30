package rezaei.mohammad.plds.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url
import rezaei.mohammad.plds.BuildConfig
import rezaei.mohammad.plds.data.model.request.*
import rezaei.mohammad.plds.data.model.response.*

interface ApiInterface {
    @POST
    suspend fun login(
        @Url url: String = BuildConfig.LoginUrl,
        @Body loginRequest: LoginRequest
    ): LoginResponse?

    @POST("Tracking/RetrieveDocumentStatus")
    suspend fun retrieveDocumentStatus(@Body documentStatusRequest: DocumentStatusRequest): DocumentStatusResponse?

    @POST("Tracking/GetStatusQueries")
    suspend fun getDynamicFieldsUnsuccessful(@Body getDynamicFieldsRequest: GetDynamicFieldsRequest): FormResponse?

    @POST("Tracking/GetStatusSuccesses")
    suspend fun getDynamicFieldsSuccessful(@Body getDynamicFieldsRequest: GetDynamicFieldsRequest): FormResponse?

    @POST("Tracking/DocumentStatusCreateResponse")
    suspend fun sendDynamicFieldResponse(@Body formResult: FormResult): BaseResponse<Unit>?

    @POST("Tracking/GetCourts")
    suspend fun getCourts(@Body unit: Unit): CourtResponse?

    @POST("Tracking/GetSheriffOffices")
    suspend fun getSheriffs(@Body unit: Unit): SheriffResponse?

    @POST("Tracking/RetrieveCommonIssues")
    suspend fun getCommonIssues(@Body commonIssueRequest: DocumentsInfoItem): CommonIssuesResponse?

}