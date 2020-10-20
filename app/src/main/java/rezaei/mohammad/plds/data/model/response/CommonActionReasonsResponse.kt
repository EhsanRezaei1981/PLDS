package rezaei.mohammad.plds.data.model.response

import com.google.gson.annotations.SerializedName

class CommonActionReasonsResponse : BaseResponse<List<CommonActionReasonsResponse.Data>>() {
    data class Data(
        @field:SerializedName("SequenceNo")
        val sequenceNo: Int? = null,

        @field:SerializedName("Description")
        val description: String? = null,

        @field:SerializedName("VT")
        val vT: String? = null,

        @field:SerializedName("CommonActionId")
        val commonActionId: Int? = null
    )
}
