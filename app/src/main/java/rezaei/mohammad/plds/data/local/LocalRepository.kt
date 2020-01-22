package rezaei.mohammad.plds.data.local

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rezaei.mohammad.plds.data.LocalRepository
import rezaei.mohammad.plds.data.model.local.Document
import rezaei.mohammad.plds.data.model.response.LoginResponse

class LocalRepository(
    private val pldsDao: PLDSDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LocalRepository {

    override suspend fun saveUser(user: LoginResponse.User) {
        return withContext(ioDispatcher) {
            return@withContext pldsDao.saveUser(user)
        }
    }

    override suspend fun getUser(): LoginResponse.User {
        return withContext(ioDispatcher) {
            return@withContext getUser()
        }
    }

    override suspend fun deleteUser(user: LoginResponse.User) {
        return withContext(ioDispatcher) {
            return@withContext deleteUser(user)
        }
    }

    override suspend fun insertDocument(document: Document) {
        return withContext(ioDispatcher) {
            return@withContext insertDocument(document)
        }
    }

    override suspend fun getAllDocument(): List<Document> {
        return withContext(ioDispatcher) {
            return@withContext getAllDocument()
        }
    }

    override suspend fun deleteDocument(document: Document) {
        return withContext(ioDispatcher) {
            return@withContext deleteDocument(document)
        }
    }

    override suspend fun deleteAllDocs(vararg document: Document) {
        return withContext(ioDispatcher) {
            return@withContext deleteAllDocs(*document)
        }
    }
}