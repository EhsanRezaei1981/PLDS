package rezaei.mohammad.plds.data.model.local

import com.google.gson.annotations.SerializedName

data class QRResponse(

    @field:SerializedName("PriorityType")
    val priorityType: String? = null,

    @field:SerializedName("DocRefNo")
    val docRefNo: String? = null
)