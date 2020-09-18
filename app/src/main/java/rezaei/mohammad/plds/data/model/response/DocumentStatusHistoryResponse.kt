package rezaei.mohammad.plds.data.model.response

import com.google.gson.annotations.SerializedName
import rezaei.mohammad.plds.data.model.request.Gps

class DocumentStatusHistoryResponse : BaseResponse<List<DocumentStatusHistoryResponse.Data>>() {
    data class Data(

        @field:SerializedName("VT")
        val vT: String? = null,

        @field:SerializedName("GPS")
        val gPS: Gps? = null,

        @field:SerializedName("Stage")
        val stage: String? = null,

        @field:SerializedName("Comment")
        val comment: String? = null,

        @field:SerializedName("StatusId")
        val statusId: Int? = null,

        @field:SerializedName("IsSuccess")
        val isSuccess: Int? = null,

        @field:SerializedName("EndDateTime")
        val endDateTime: String? = null,

        @field:SerializedName("GPSIsNeeded")
        val gPSIsNeeded: Int? = null,

        @field:SerializedName("StartDateTime")
        val startDateTime: String? = null,

        @field:SerializedName("CreateDateTime")
        val createDateTime: String? = null,

        @field:SerializedName("CustomActionId")
        val customActionId: Int? = null,

        @field:SerializedName("DocumentStatusId")
        val documentStatusId: Int? = null,

        @field:SerializedName("StatusDescription")
        val statusDescription: String? = null,

        @field:SerializedName("UnsuccessfulJsonData")
        val unsuccessfulJsonData: List<Unit>? = null
    ) {
        val date: String?
            get() = if (endDateTime?.isNotEmpty() == true)
                "${startDateTime ?: ""} | $endDateTime"
            else
                startDateTime ?: ""

    }
}

