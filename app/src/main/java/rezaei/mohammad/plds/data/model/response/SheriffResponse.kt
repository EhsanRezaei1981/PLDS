package rezaei.mohammad.plds.data.model.response

import com.google.gson.annotations.SerializedName

class SheriffResponse : BaseResponse<List<SheriffResponse.Sheriff>>() {
    data class Sheriff(

        @field:SerializedName("CourtName")
        val courtName: String? = null,

        @field:SerializedName("SheriffOfficeId")
        val sheriffOfficeId: Int? = null,

        @field:SerializedName("CourtId")
        val courtId: Int? = null,

        @field:SerializedName("SheriffAreaName")
        val sheriffAreaName: String? = null,

        @field:SerializedName("SheriffLinkId")
        val sheriffLinkId: Int? = null
    )
}
