package rezaei.mohammad.plds.data.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rezaei.mohammad.plds.data.RemoteRepository
import rezaei.mohammad.plds.data.Result
import rezaei.mohammad.plds.data.model.request.DocumentStatusRequest
import rezaei.mohammad.plds.data.model.request.LoginRequest
import rezaei.mohammad.plds.data.model.response.BaseResponse
import rezaei.mohammad.plds.data.model.response.DocumentStatusResponse
import rezaei.mohammad.plds.data.model.response.ErrorHandling
import rezaei.mohammad.plds.data.model.response.LoginResponse

class RemoteRepository(
    private val apiInterface: ApiInterface,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RemoteRepository {

    override suspend fun login(userName: String, password: String): Result<LoginResponse> =
        withContext(ioDispatcher) {
            return@withContext parseResult(
                apiInterface.login(
                    loginRequest = LoginRequest(
                        username = userName,
                        password = password
                    )
                )
            )
        }

    override suspend fun retrieveDocumentStatus(documentRefNo: String?): Result<DocumentStatusResponse> =
        withContext(ioDispatcher) {
            return@withContext parseResult(
                apiInterface.retrieveDocumentStatus(DocumentStatusRequest(documentRefNo))
            )
        }

    private fun <T : BaseResponse<*>> parseResult(result: T?): Result<T> {
        return try {
            if (result != null)
                if (result.errorHandling?.isSuccessful == true)
                    Result.Success(result)
                else
                    Result.Error(result.errorHandling)
            else
                Result.Error(
                    ErrorHandling(
                        errorMessage = "Bad response",
                        errorMustBeSeenByUser = true
                    )
                )
        } catch (e: Exception) {
            Result.Error(
                ErrorHandling(
                    errorMessage = e.message,
                    errorMustBeSeenByUser = false
                )
            )
        }
    }
}