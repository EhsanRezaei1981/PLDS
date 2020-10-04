package rezaei.mohammad.plds.data.remote

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url
import rezaei.mohammad.plds.data.model.request.*
import rezaei.mohammad.plds.data.model.response.*

interface ApiInterface {
    @POST
    suspend fun login(
        @Url url: String,
        @Body loginRequest: LoginRequest
    ): LoginResponse?

    @POST("Tracking/RetrieveDocumentStatus")
    suspend fun retrieveDocumentStatus(@Body documentStatusRequest: DocumentStatusRequest): DocumentStatusResponse?

    @POST("Tracking/GetStatusQueries")
    suspend fun getDynamicFieldsUnsuccessful(@Body getDynamicFieldsRequest: GetDynamicFieldsRequest): FormResponse?

    @POST("Tracking/GetStatusSuccesses")
    suspend fun getDynamicFieldsSuccessful(@Body getDynamicFieldsRequest: GetDynamicFieldsRequest): FormResponse?

    @POST("Tracking/DocumentStatusCreateResponse")
    suspend fun sendDynamicFieldResponse(@Body formResult: FormResult.DocumentProgress): BaseResponse<Unit>?

    @POST("Tracking/GetCourts")
    suspend fun getCourts(@Body unit: Unit): CourtResponse?

    @POST("Tracking/GetSheriffOffices")
    suspend fun getSheriffs(@Body unit: Unit): SheriffResponse?

    @POST("Tracking/RetrieveCommonIssues")
    suspend fun getCommonIssues(@Body commonIssueRequest: DocumentsInfoItem): CommonIssuesResponse?

    @POST("Tracking/GetDocumentBaseInfo")
    suspend fun getDocumentBaseInfo(@Body documentStatusRequest: DocumentStatusRequest): DocumentBaseInfoResponse?

    @POST("Tracking/GetDocumentStatusHistory")
    suspend fun getDocumentStatusHistory(@Body documentStatusHistoryRequest: DocumentBaseInfoResponse.Data): DocumentStatusHistoryResponse?

    @POST("Tracking/GetRespondedFields")
    suspend fun getRespondedFields(@Body respondedFieldsRequest: RespondedFieldsRequest): FormResponse?

    @POST("Tracking/GetStatusSuccesses")
    suspend fun getStatusSuccesses(@Body respondedFieldsRequest: RespondedFieldsRequest): FormResponse?

    @POST("Tracking/GetFileByMainLegalInfo")
    suspend fun getFileByMainLegalInfo(@Body getFileRequest: GetFileRequest): GetFileResponse?

    @POST("Tracking/UpdateRespondedFields")
    suspend fun updateRespondedFields(@Body updateRespondedFieldsRequest: FormResult.RespondedFields): BaseResponse<Unit>?

    @POST("Tracking/CheckIn")
    suspend fun checkIn(@Body checkInRequest: CheckInRequest): CheckInResponse?

    @POST("Tracking/CheckOut")
    suspend fun checkOut(@Body checkOutRequest: CheckOutRequest): BaseResponse<Unit>?

    @POST("Tracking/UserTracking")
    suspend fun userTracking(@Body userTrackRequest: UserTrackRequest): BaseResponse<Unit>?


}