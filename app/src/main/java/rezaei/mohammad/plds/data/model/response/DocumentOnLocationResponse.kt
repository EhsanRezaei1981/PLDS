package rezaei.mohammad.plds.data.model.response

import com.google.gson.annotations.SerializedName

class DocumentOnLocationResponse : BaseResponse<List<DocumentOnLocationResponse.Data>>() {
    data class Data(

        @field:SerializedName("ProcessDescription")
        val processDescription: String? = null,

        @field:SerializedName("Action")
        val action: String? = null,

        @field:SerializedName("ClientName")
        val clientName: String? = null,

        @field:SerializedName("ActionDescription")
        val actionDescription: String? = null,

        @field:SerializedName("DocumentReferenceNo")
        val documentReferenceNo: String? = null,

        @field:SerializedName("DaysPassed")
        val daysPassed: String? = null
    )
}


