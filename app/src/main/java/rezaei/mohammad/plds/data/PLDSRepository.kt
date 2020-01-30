package rezaei.mohammad.plds.data

import rezaei.mohammad.plds.data.model.local.Document
import rezaei.mohammad.plds.data.model.request.DocumentsInfoItem
import rezaei.mohammad.plds.data.model.request.FormResult
import rezaei.mohammad.plds.data.model.request.GetDynamicFieldsRequest
import rezaei.mohammad.plds.data.model.response.*

interface RemoteRepository {
    suspend fun login(userName: String, password: String): Result<LoginResponse>
    suspend fun retrieveDocumentStatus(documentRefNo: String?): Result<DocumentStatusResponse>
    suspend fun getDynamicFieldsUnsuccessful(getDynamicFieldsRequest: GetDynamicFieldsRequest): Result<FormResponse>
    suspend fun getDynamicFieldsSuccessful(getDynamicFieldsRequest: GetDynamicFieldsRequest): Result<FormResponse>
    suspend fun sendDynamicFieldResponse(formResult: FormResult): Result<BaseResponse<Unit>>
    suspend fun getCourts(unit: Unit): Result<CourtResponse>
    suspend fun getSheriffs(unit: Unit): Result<SheriffResponse>
    suspend fun getCommonIssues(document: DocumentsInfoItem): Result<CommonIssuesResponse>

}

interface LocalRepository {

    suspend fun saveUser(user: LoginResponse.User)

    suspend fun getUser(): LoginResponse.User

    suspend fun deleteUser(user: LoginResponse.User)

    suspend fun insertDocument(document: Document): Boolean

    suspend fun getAllDocument(): List<Document>

    suspend fun deleteDocument(document: Document)

    suspend fun deleteAllDocs(documents: List<Document>)

}