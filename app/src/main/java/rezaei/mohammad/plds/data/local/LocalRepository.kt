package rezaei.mohammad.plds.data.local

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rezaei.mohammad.plds.data.LocalRepository
import rezaei.mohammad.plds.data.model.local.Document
import rezaei.mohammad.plds.data.model.local.DocumentType
import rezaei.mohammad.plds.data.model.response.LoginResponse

class LocalRepository(
    private val pldsDao: PLDSDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : LocalRepository {

    override suspend fun saveUser(user: LoginResponse.User) {
        withContext(ioDispatcher) {
            pldsDao.saveUser(user)
        }
    }

    override suspend fun getUser(): LoginResponse.User {
        return withContext(ioDispatcher) {
            return@withContext pldsDao.getUser()
        }
    }

    override suspend fun deleteUser(user: LoginResponse.User) {
        withContext(ioDispatcher) {
            pldsDao.deleteUser(user)
        }
    }

    override suspend fun insertDocument(document: Document): Boolean {
        return withContext(ioDispatcher) {
            try {
                pldsDao.insertDocument(document)
                return@withContext true
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext false
            }
        }
    }

    override suspend fun getAllDocument(documentType: DocumentType): List<Document> {
        return withContext(ioDispatcher) {
            return@withContext pldsDao.getAllDocument(documentType)
        }
    }

    override suspend fun deleteDocument(document: Document) {
        withContext(ioDispatcher) {
            pldsDao.deleteDocument(document)
        }
    }

    override suspend fun deleteAllDocs(documents: List<Document>) {
        withContext(ioDispatcher) {
            pldsDao.deleteAllDocs(documents)
        }
    }
}