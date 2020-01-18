package rezaei.mohammad.plds.data.model.response

import com.google.gson.annotations.SerializedName

open class ErrorHandling(

    @field:SerializedName("ErrorMustBeSeenByUser")
    val errorMustBeSeenByUser: Boolean? = null,

    @field:SerializedName("ErrorLevel")
    val errorLevel: String? = null,

    @field:SerializedName("ErrorCode")
    val errorCode: Int? = null,

    @field:SerializedName("ErrorMessage")
    val errorMessage: String? = null,

    @field:SerializedName("IsSuccessful")
    val isSuccessful: Boolean? = null
)