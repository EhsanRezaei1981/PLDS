package rezaei.mohammad.plds.data.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rezaei.mohammad.plds.data.RemoteRepository
import rezaei.mohammad.plds.data.Result
import rezaei.mohammad.plds.data.model.request.*
import rezaei.mohammad.plds.data.model.response.*

class RemoteRepository(
    private val apiInterface: ApiInterface,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RemoteRepository {

    private val networkError = Result.Error(
        ErrorHandling(
            errorMessage = "Network or server error",
            errorMustBeSeenByUser = true,
            isSuccessful = false
        )
    )

    override suspend fun login(userName: String, password: String): Result<LoginResponse> =
        try {
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
        } catch (e: Exception) {
            networkError
        }

    override suspend fun retrieveDocumentStatus(documentRefNo: String?): Result<DocumentStatusResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.retrieveDocumentStatus(DocumentStatusRequest(documentRefNo))
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun getDynamicFieldsUnsuccessful(getDynamicFieldsRequest: GetDynamicFieldsRequest): Result<FormResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.getDynamicFieldsUnsuccessful(getDynamicFieldsRequest)
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun getDynamicFieldsSuccessful(getDynamicFieldsRequest: GetDynamicFieldsRequest): Result<FormResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.getDynamicFieldsSuccessful(getDynamicFieldsRequest)
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun sendDynamicFieldResponse(formResult: FormResult): Result<BaseResponse<Unit>> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.sendDynamicFieldResponse(formResult)
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun getCourts(unit: Unit): Result<CourtResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.getCourts(unit)
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun getSheriffs(unit: Unit): Result<SheriffResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.getSheriffs(unit)
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun getCommonIssues(document: DocumentsInfoItem): Result<CommonIssuesResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.getCommonIssues(document)
                )
            }
        } catch (e: Exception) {
            networkError
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