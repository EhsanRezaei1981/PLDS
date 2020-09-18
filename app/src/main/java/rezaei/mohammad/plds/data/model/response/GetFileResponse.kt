package rezaei.mohammad.plds.data.model.response

import com.google.gson.annotations.SerializedName

class GetFileResponse : BaseResponse<GetFileResponse.Data>() {
    data class Data(

        @field:SerializedName("Extension")
        val extension: String? = null,

        @field:SerializedName("Base64")
        val base64: String? = null,

        @field:SerializedName("Description")
        val description: String? = null,

        @field:SerializedName("Size")
        val size: Int? = null,

        @field:SerializedName("Filename")
        val filename: String? = null
    )
}


