package rezaei.mohammad.plds.data.model.request

import com.google.gson.annotations.SerializedName

data class CheckInRequest(

    @field:SerializedName("CheckInPart")
    val checkInPart: String? = null,

    @field:SerializedName("LocationId")
    val locationId: Int? = null,

    @field:SerializedName("GPS")
    val gPS: Gps? = null,

    @field:SerializedName("LocationType")
    val locationType: String? = null,

    @field:SerializedName("LocationName")
    val locationName: String? = null


)
