package rezaei.mohammad.plds.data

import rezaei.mohammad.plds.data.model.local.Document
import rezaei.mohammad.plds.data.model.response.DocumentStatusResponse
import rezaei.mohammad.plds.data.model.response.LoginResponse

interface RemoteRepository {
    suspend fun login(userName: String, password: String): Result<LoginResponse>
    suspend fun retrieveDocumentStatus(documentRefNo: String?): Result<DocumentStatusResponse>
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