package rezaei.mohammad.plds.data

import rezaei.mohammad.plds.data.model.response.DocumentStatusResponse
import rezaei.mohammad.plds.data.model.response.LoginResponse

interface PLDSRepository {
    suspend fun login(userName: String, password: String): Result<LoginResponse>
    suspend fun retrieveDocumentStatus(documentRefNo: String?): Result<DocumentStatusResponse>
}