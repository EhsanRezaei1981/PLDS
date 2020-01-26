package rezaei.mohammad.plds.data.model.request

import com.google.gson.annotations.SerializedName

data class GetDynamicFieldsRequest(

    @field:SerializedName("DocumentStatusId")
    val documentStatusId: Int? = null,

    @field:SerializedName("VT")
    val vT: String? = null
)