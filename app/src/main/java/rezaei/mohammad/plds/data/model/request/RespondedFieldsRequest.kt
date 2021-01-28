package rezaei.mohammad.plds.data.model.request

import com.google.gson.annotations.SerializedName

data class RespondedFieldsRequest(

    @field:SerializedName("DocumentStatusId")
    val documentStatusId: Int? = null,

    @field:SerializedName("VT")
    val vT: String? = null,

    @field:SerializedName("Type")
    val type: String? = null,

    @field:SerializedName("ValueMustBeChecked")
    val valueMustBeChecked: Boolean? = null,

    @field:SerializedName("DocumentStatusQueryId")
    val documentStatusQueryId: Int? = null
)