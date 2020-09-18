package rezaei.mohammad.plds.data.model.request

import com.google.gson.annotations.SerializedName

sealed class ElementResult {

    data class FileResult(
        @field:SerializedName("Id")
        val id: Int? = null,
        @field:SerializedName("File")
        val chosenFile: ChosenFile? = null,
        @field:SerializedName("VTMTId")
        val vTMTId: String? = null,
        @field:SerializedName("MTId")
        val mTId: Int? = null
    ) : ElementResult()

    data class ListResult(
        @field:SerializedName("Id")
        val id: Int? = null,
        @field:SerializedName("Item")
        val listItem: ListItem? = null,
        @field:SerializedName("VTMTId")
        val vTMTId: String? = null,
        @field:SerializedName("MTId")
        val mTId: Int? = null,
        @Transient
        val gps: Gps? = null
    ) : ElementResult()

    data class StringResult(
        @field:SerializedName("Id")
        val id: Int? = null,
        @field:SerializedName("Reply")
        val reply: String? = null,
        @field:SerializedName("VTMTId")
        val vTMTId: String? = null,
        @field:SerializedName("MTId")
        val mTId: Int? = null
    ) : ElementResult()

    data class IssueResult(
        @field:SerializedName("Comment")
        val comment: String? = null,
        @field:SerializedName("Date")
        var date: String? = null,
        @field:SerializedName("CommonIssueId")
        val cmmonIssueId: Int? = null,
        @field:SerializedName("SelectedIssueDescription")
        val selectedIssueDescription: String? = null,
        @field:SerializedName("CustomAction")
        val customAction: CustomAction? = null,
        @Transient
        val gps: Gps? = null
    ) : ElementResult()

}

data class ChosenFile(
    @field:SerializedName("Extension")
    val extension: String? = null,

    @field:SerializedName("Base64")
    val base64: String? = null,

    @field:SerializedName("Size")
    val size: Int? = null,

    @field:SerializedName("Filename")
    val filename: String? = null,

    @field:SerializedName("FileId")
    val fileId: Int? = null
)

data class ListItem(

    @field:SerializedName("Comment")
    val comment: String? = null,

    @field:SerializedName("CustomAction")
    val customAction: CustomAction? = null,

    @field:SerializedName("Text")
    val text: String? = null,

    @field:SerializedName("Id")
    val id: Int? = null
)

data class CustomAction(
    @field:SerializedName("Data")
    val data: Data? = null
)

data class Data(

    @field:SerializedName("SheriffOfficeId")
    val sheriffOfficeId: Int? = null,

    @field:SerializedName("CourtName")
    val courtName: String? = null,

    @field:SerializedName("CourtId")
    val courtId: Int? = null,

    @field:SerializedName("SheriffAreaName")
    val sheriffAreaName: String? = null
)

data class Item(

    @field:SerializedName("Text")
    val text: String? = null,

    @field:SerializedName("Id")
    val id: String? = null
)