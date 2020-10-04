package rezaei.mohammad.plds.data.model.request

import com.google.gson.annotations.SerializedName

data class CheckOutRequest(

    @field:SerializedName("LocationId")
    val locationId: Int? = null,

    @field:SerializedName("CheckOutPart")
    val checkOutPart: String? = null,

    @field:SerializedName("VTLocation")
    val vTLocation: String? = null,

    @field:SerializedName("UTPId")
    val uTPId: Int? = null,

    @field:SerializedName("VTUTPId")
    val vTUTPId: String? = null,

    @field:SerializedName("GPS")
    val gPS: Gps? = null,

    @field:SerializedName("LocationType")
    val locationType: String? = null,

    @field:SerializedName("LocationName")
    val locationName: String? = null
)
