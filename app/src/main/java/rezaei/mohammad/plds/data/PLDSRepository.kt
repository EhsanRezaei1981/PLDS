package rezaei.mohammad.plds.data

import rezaei.mohammad.plds.data.model.local.CheckInResponseEntity
import rezaei.mohammad.plds.data.model.local.Document
import rezaei.mohammad.plds.data.model.local.DocumentType
import rezaei.mohammad.plds.data.model.request.*
import rezaei.mohammad.plds.data.model.response.*

interface RemoteRepository {
    suspend fun login(userName: String, password: String): ApiResult<LoginResponse>
    suspend fun retrieveDocumentStatus(documentRefNo: String?): ApiResult<DocumentStatusResponse>
    suspend fun getDynamicFieldsUnsuccessful(getDynamicFieldsRequest: GetDynamicFieldsRequest): ApiResult<FormResponse>
    suspend fun getDynamicFieldsSuccessful(getDynamicFieldsRequest: GetDynamicFieldsRequest): ApiResult<FormResponse>
    suspend fun sendDynamicFieldResponse(formResult: FormResult.DocumentProgress): ApiResult<BaseResponse<Unit>>
    suspend fun getCourts(unit: Unit): ApiResult<CourtResponse>
    suspend fun getSheriffs(unit: Unit): ApiResult<SheriffResponse>
    suspend fun getCommonIssues(document: DocumentsInfoItem): ApiResult<CommonIssuesResponse>
    suspend fun getDocumentBaseInfo(documentStatusRequest: DocumentStatusRequest): ApiResult<DocumentBaseInfoResponse>
    suspend fun getDocumentStatusHistory(documentStatusHistoryRequest: DocumentBaseInfoResponse.Data): ApiResult<DocumentStatusHistoryResponse>
    suspend fun getRespondedFields(respondedFieldsRequest: RespondedFieldsRequest): ApiResult<FormResponse>
    suspend fun getStatusSuccesses(respondedFieldsRequest: RespondedFieldsRequest): ApiResult<FormResponse>
    suspend fun getFileByMainLegalInfo(getFileRequest: GetFileRequest): ApiResult<GetFileResponse>
    suspend fun updateRespondedFields(updateRespondedFieldsRequest: FormResult.RespondedFields): ApiResult<BaseResponse<Unit>>
    suspend fun checkIn(checkInRequest: CheckInRequest): ApiResult<CheckInResponse>
    suspend fun checkOut(checkOutRequest: CheckOutRequest): ApiResult<BaseResponse<Unit>>
    suspend fun userTracking(userTrackRequest: UserTrackRequest): ApiResult<BaseResponse<Unit>>
    suspend fun getDocumentListOnLocation(getDocumentsOnLocationRequest: GetDocumentsOnLocationRequest): ApiResult<DocumentOnLocationResponse>

}

interface LocalRepository {

    suspend fun saveUser(user: LoginResponse.User)

    suspend fun getUser(): LoginResponse.User

    suspend fun deleteUser(user: LoginResponse.User)

    suspend fun insertDocument(document: Document): Boolean

    suspend fun getAllDocument(documentType: DocumentType): List<Document>

    suspend fun deleteDocument(document: Document)

    suspend fun deleteAllDocs(documents: List<Document>)

    suspend fun insertCheckInResponse(checkInResponseEntity: CheckInResponseEntity)

    suspend fun getCheckInResponse(): CheckInResponseEntity?

    suspend fun deleteCheckInResponse(checkInResponseEntity: CheckInResponseEntity)

    suspend fun deleteAllCheckInResponse()

}