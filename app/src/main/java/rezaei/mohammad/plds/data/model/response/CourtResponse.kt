package rezaei.mohammad.plds.data.model.response

import com.google.gson.annotations.SerializedName

class CourtResponse : BaseResponse<List<CourtResponse.Court>>() {

    data class Court(

        @field:SerializedName("CourtName")
        val courtName: String? = null,

        @field:SerializedName("CourtType")
        val courtType: String? = null,

        @field:SerializedName("CourtId")
        val courtId: Int? = null,

        @field:SerializedName("CourtTypeId")
        val courtTypeId: Int? = null
    )
}
