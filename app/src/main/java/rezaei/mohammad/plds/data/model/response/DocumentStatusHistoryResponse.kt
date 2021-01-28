package rezaei.mohammad.plds.data.model.response

import com.google.gson.annotations.SerializedName
import rezaei.mohammad.plds.data.model.request.Gps

class DocumentStatusHistoryResponse : BaseResponse<List<DocumentStatusHistoryResponse.Data>>() {
    data class Data(
        @field:SerializedName("Comment")
        val comment: String? = null,

        @field:SerializedName("IsSuccess")
        val isSuccess: Int? = null,

        @field:SerializedName("DocumentStatusId")
        val documentStatusId: Int? = null,

        @field:SerializedName("LastUpdateDateTime")
        val lastUpdateDateTime: String? = null,

        @field:SerializedName("GPSIsNeeded")
        val gPSIsNeeded: Int? = null,

        @field:SerializedName("CreateDateTime")
        val createDateTime: String? = null,

        @field:SerializedName("GPS")
        val gPS: Gps? = null,

        @field:SerializedName("UnsuccessfulJsonData")
        val unsuccessfulJsonData: List<UnsuccessfulJsonData>? = null,

        @field:SerializedName("StartDateTime")
        val startDateTime: String? = null,

        @field:SerializedName("EndDateTime")
        val endDateTime: String? = null,

        @field:SerializedName("StatusDescription")
        val statusDescription: String? = null,

        @field:SerializedName("CustomActionId")
        val customActionId: Int? = null,

        @field:SerializedName("Stage")
        val stage: String? = null,

        @field:SerializedName("StatusId")
        val statusId: Int? = null,

        @field:SerializedName("VT")
        val vT: String? = null
    ) {
        @Transient
        var date: String? = null
            get() = if (endDateTime.isNullOrEmpty().not())
                "${startDateTime ?: ""} | $endDateTime"
            else
                startDateTime ?: ""

    }

    data class UnsuccessfulJsonData(

        @field:SerializedName("CommentValue")
        val commentValue: String? = null,

        @field:SerializedName("ReasonDecsription")
        val reasonDecsription: String? = null,

        @field:SerializedName("CreateDateTime")
        val createDateTime: String? = null,

        @field:SerializedName("DocumentStatusQueryId")
        val documentStatusQueryId: Int? = null,

        @field:SerializedName("CommonIssueId")
        val commonIssueId: Int? = null,

        @field:SerializedName("GPS")
        val gPS: String? = null,

        @field:SerializedName("Date")
        val date: String? = null
    )
}

