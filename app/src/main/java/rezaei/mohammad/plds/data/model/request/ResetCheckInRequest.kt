package rezaei.mohammad.plds.data.model.request

import com.google.gson.annotations.SerializedName

data class ResetCheckInRequest(
    @SerializedName("GPS")
    val gps: Gps
)