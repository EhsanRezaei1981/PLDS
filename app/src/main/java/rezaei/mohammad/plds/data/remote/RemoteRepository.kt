package rezaei.mohammad.plds.data.remote

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rezaei.mohammad.plds.PLDSapp
import rezaei.mohammad.plds.data.ApiResult
import rezaei.mohammad.plds.data.RemoteRepository
import rezaei.mohammad.plds.data.model.request.*
import rezaei.mohammad.plds.data.model.response.*
import rezaei.mohammad.plds.data.preference.PreferenceManager

class RemoteRepository(
    private val apiInterface: ApiInterface,
    private val pref: PreferenceManager,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : RemoteRepository {

    private val networkError = ApiResult.Error(
        ErrorHandling(
            errorMessage = "Network or server error",
            errorMustBeSeenByUser = true,
            isSuccessful = false
        )
    )

    override suspend fun login(userName: String, password: String): ApiResult<LoginResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.login(
                        loginRequest = LoginRequest(
                            username = userName,
                            password = password,
                            deviceInfo = PLDSapp.userAgent
                        ),
                        url = pref.getActiveEnvironment().first.trimEnd(
                            '/',
                            '\\'
                        ) + "/WebCore/Account/Authenticate"
                    )
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun retrieveDocumentStatus(documentRefNo: String?): ApiResult<DocumentStatusResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.retrieveDocumentStatus(DocumentStatusRequest(documentRefNo))
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun getDynamicFieldsUnsuccessful(getDynamicFieldsRequest: GetDynamicFieldsRequest): ApiResult<FormResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.getDynamicFieldsUnsuccessful(getDynamicFieldsRequest)
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun getDynamicFieldsSuccessful(getDynamicFieldsRequest: GetDynamicFieldsRequest): ApiResult<FormResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.getDynamicFieldsSuccessful(getDynamicFieldsRequest)
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun sendDynamicFieldResponse(formResult: FormResult.DocumentProgress): ApiResult<BaseResponse<Unit>> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.sendDynamicFieldResponse(formResult)
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun getCourts(unit: Unit): ApiResult<CourtResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.getCourts(unit)
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun getSheriffs(unit: Unit): ApiResult<SheriffResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.getSheriffs(unit)
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun getCommonIssues(document: DocumentsInfoItem): ApiResult<CommonIssuesResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.getCommonIssues(document)
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun getDocumentBaseInfo(documentStatusRequest: DocumentStatusRequest): ApiResult<DocumentBaseInfoResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.getDocumentBaseInfo(
                        documentStatusRequest
                    )
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun getDocumentStatusHistory(documentStatusHistoryRequest: DocumentBaseInfoResponse.Data): ApiResult<DocumentStatusHistoryResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.getDocumentStatusHistory(
                        documentStatusHistoryRequest
                    )
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun getRespondedFields(respondedFieldsRequest: RespondedFieldsRequest): ApiResult<FormResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.getRespondedFields(
                        respondedFieldsRequest
                    )
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun getStatusSuccesses(respondedFieldsRequest: RespondedFieldsRequest): ApiResult<FormResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.getStatusSuccesses(
                        respondedFieldsRequest
                    )
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun getFileByMainLegalInfo(getFileRequest: GetFileRequest): ApiResult<GetFileResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(apiInterface.getFileByMainLegalInfo(getFileRequest))
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun updateRespondedFields(updateRespondedFieldsRequest: FormResult.RespondedFields): ApiResult<BaseResponse<Unit>> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.updateRespondedFields(
                        updateRespondedFieldsRequest
                    )
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun checkIn(checkInRequest: CheckInRequest): ApiResult<CheckInResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.checkIn(
                        checkInRequest
                    )
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun checkOut(checkOutRequest: CheckOutRequest): ApiResult<BaseResponse<Unit>> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.checkOut(
                        checkOutRequest
                    )
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun userTracking(userTrackRequest: UserTrackRequest): ApiResult<BaseResponse<Unit>> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.userTracking(
                        userTrackRequest
                    )
                )
            }
        } catch (e: Exception) {
            networkError
        }

    override suspend fun getDocumentListOnLocation(getDocumentsOnLocationRequest: GetDocumentsOnLocationRequest): ApiResult<DocumentOnLocationResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.getDocumentListOnLocation(getDocumentsOnLocationRequest)
                )
            }
        } catch (e: java.lang.Exception) {
            networkError
        }

    override suspend fun getCommonActionReasons(commonActionReasonsRequest: CommonActionReasonsRequest): ApiResult<CommonActionReasonsResponse> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.getCommonActionReasons(commonActionReasonsRequest)
                )
            }
        } catch (e: java.lang.Exception) {
            networkError
        }

    override suspend fun resetCheckInOutOperation(resetCheckInRequest: ResetCheckInRequest): ApiResult<BaseResponse<Unit>> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.resetCheckInOutOperation(resetCheckInRequest)
                )
            }
        } catch (e: java.lang.Exception) {
            networkError
        }

    override suspend fun submitCommonActionForm(commonActionResult: FormResult.CommonAction): ApiResult<BaseResponse<Unit>> =
        try {
            withContext(ioDispatcher) {
                return@withContext parseResult(
                    apiInterface.submitCommonActionForm(commonActionResult)
                )
            }
        } catch (e: java.lang.Exception) {
            networkError
        }

    private fun <T : BaseResponse<*>> parseResult(result: T?): ApiResult<T> {
        return try {
            if (result != null)
                if (result.data != null || result.errorHandling?.isSuccessful == true)
                    ApiResult.Success(result)
                else
                    ApiResult.Error(result.errorHandling)
            else
                ApiResult.Error(
                    ErrorHandling(
                        errorMessage = "Bad response",
                        errorMustBeSeenByUser = true
                    )
                )
        } catch (e: Exception) {
            ApiResult.Error(
                ErrorHandling(
                    errorMessage = e.message,
                    errorMustBeSeenByUser = false
                )
            )
        }
    }
}