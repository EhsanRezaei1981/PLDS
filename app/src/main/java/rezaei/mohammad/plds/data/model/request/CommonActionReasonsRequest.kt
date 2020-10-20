package rezaei.mohammad.plds.data.model.request

import com.google.gson.annotations.SerializedName

data class CommonActionReasonsRequest(

    @field:SerializedName("LocationType")
    val locationType: String? = null
)
