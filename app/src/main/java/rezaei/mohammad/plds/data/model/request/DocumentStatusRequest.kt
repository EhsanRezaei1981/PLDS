package rezaei.mohammad.plds.data.model.request

import com.google.gson.annotations.SerializedName

data class DocumentStatusRequest(

    @field:SerializedName("DocumentReferenceNo")
    val documentReferenceNo: String? = null
)