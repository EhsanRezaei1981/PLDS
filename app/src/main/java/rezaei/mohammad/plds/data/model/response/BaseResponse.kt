package rezaei.mohammad.plds.data.model.response

import com.google.gson.annotations.SerializedName

open class BaseResponse<T> {

    @field:SerializedName("ErrorHandling")
    val errorHandling: ErrorHandling? = null

    @field:SerializedName("Data")
    val data: T? = null
}