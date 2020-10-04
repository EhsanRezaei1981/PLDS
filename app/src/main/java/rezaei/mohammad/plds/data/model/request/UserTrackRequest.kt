package rezaei.mohammad.plds.data.model.request

import com.google.gson.annotations.SerializedName

data class UserTrackRequest(

    @field:SerializedName("UTPId")
    val uTPId: Int? = null,

    @field:SerializedName("VTUTPId")
    val vTUTPId: String? = null,

    @field:SerializedName("GPS")
    val gPS: Gps? = null
)
