package rezaei.mohammad.plds.data.local

import androidx.room.*
import rezaei.mohammad.plds.data.model.local.CheckInResponseEntity
import rezaei.mohammad.plds.data.model.local.Document
import rezaei.mohammad.plds.data.model.local.DocumentType
import rezaei.mohammad.plds.data.model.response.LoginResponse

@Dao
interface PLDSDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUser(user: LoginResponse.User)

    @Query("SELECT * FROM USER LIMIT 1")
    suspend fun getUser(): LoginResponse.User

    @Delete
    suspend fun deleteUser(user: LoginResponse.User)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertDocument(document: Document)

    @Query("SELECT * FROM document WHERE documentType = :documentType ORDER BY id Desc")
    suspend fun getAllDocument(documentType: DocumentType): List<Document>

    @Delete
    suspend fun deleteDocument(document: Document)

    @Delete
    suspend fun deleteAllDocs(documents: List<Document>)

    @Insert
    suspend fun insertCheckInResponse(checkInResponseEntity: CheckInResponseEntity)

    @Query("SELECT * FROM checkinresponseentity LIMIT 1")
    suspend fun getCheckInResponse(): CheckInResponseEntity?

    @Delete
    suspend fun deleteCheckInResponse(checkInResponseEntity: CheckInResponseEntity)

    @Query("DELETE FROM checkinresponseentity")
    suspend fun deleteAllCheckInResponse()

}