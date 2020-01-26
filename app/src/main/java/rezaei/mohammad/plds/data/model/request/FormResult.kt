package rezaei.mohammad.plds.data.model.request

import com.google.gson.annotations.SerializedName

data class FormResult(

    @field:SerializedName("DocumentsInfo")
    var documentsInfo: List<DocumentsInfoItem?>? = null,

    @field:SerializedName("Unsuccessful")
    var unsuccessful: Result? = null,

    @field:SerializedName("Successful")
    var successful: Result? = null,

    @field:SerializedName("GPS")
    var gPS: Gps? = null,

    @field:SerializedName("ResponseType")
    var responseType: String? = null
)

data class Result(

    @field:SerializedName("Elements")
    val elements: MutableList<ElementResult?>? = null
)

data class Gps(

    @field:SerializedName("X")
    val X: Double? = null,

    @field:SerializedName("Y")
    val Y: Double? = null
)

data class DocumentsInfoItem(

    @field:SerializedName("DocumentReferenceNo")
    val documentReferenceNo: String? = null
)