package rezaei.mohammad.plds.data.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rezaei.mohammad.plds.data.RemoteRepository
import rezaei.mohammad.plds.data.Result
import rezaei.mohammad.plds.data.model.request.DocumentStatusRequest
import rezaei.mohammad.plds.data.model.request.FormResult
import rezaei.mohammad.plds.data.model.request.GetDynamicFieldsRequest
import rezaei.mohammad.plds.data.model.request.LoginRequest
import rezaei.mohammad.plds.data.model.response.*

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

    override suspend fun getDynamicFieldsUnsuccessful(getDynamicFieldsRequest: GetDynamicFieldsRequest): Result<FormResponse> =
        withContext(ioDispatcher) {
            return@withContext parseResult(
                apiInterface.getDynamicFieldsUnsuccessful(getDynamicFieldsRequest)
            )
        }

    override suspend fun getDynamicFieldsSuccessful(getDynamicFieldsRequest: GetDynamicFieldsRequest): Result<FormResponse> =
        withContext(ioDispatcher) {
            return@withContext parseResult(
                apiInterface.getDynamicFieldsSuccessful(getDynamicFieldsRequest)
            )
        }

    override suspend fun sendDynamicFieldResponse(formResult: FormResult): Result<BaseResponse<Unit>> =
        withContext(ioDispatcher) {
            return@withContext parseResult(
                apiInterface.sendDynamicFieldResponse(formResult)
            )
        }

    override suspend fun getCourts(unit: Unit): Result<CourtResponse> =
        withContext(ioDispatcher) {
            return@withContext parseResult(
                apiInterface.getCourts(unit)
            )
        }

    override suspend fun getSheriffs(unit: Unit): Result<SheriffResponse> =
        withContext(ioDispatcher) {
            return@withContext parseResult(
                apiInterface.getSheriffs(unit)
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