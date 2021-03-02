package rezaei.mohammad.plds.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
class FormResponse : BaseResponse<List<FormResponse.DataItem>>(), Parcelable {
    @Parcelize
    data class DataItem(

        @field:SerializedName("IsMandatory")
        val isMandatory: Int? = null,

        @field:SerializedName("Label")
        val label: String? = null,

        @field:SerializedName("DataType")
        val dataType: String? = null,

        @field:SerializedName("List")
        val list: List<ListItem>? = null,

        @field:SerializedName("StatusQueryId", alternate = ["CommonIssueId", "StatusSuccessId"])
        val statusQueryId: Int? = null,

        @field:SerializedName("DataTypeSetting")
        val dataTypeSetting: DataTypeSetting? = null,

        @field:SerializedName("VT")
        val vT: String? = null,

        @field:SerializedName("Value")
        val value: Value? = null,

        @field:SerializedName("CommonIssue")
        val commonIssue: CommonIssue? = null,

        @field:SerializedName("StatusQuery")
        val statusQuery: List<DataItem>? = null,

        @field:SerializedName("Date")
        val date: String? = null,

        @field:SerializedName("DocumentStatusQueryId")
        val documentStatusQueryId: Int? = null,

        @field:SerializedName("LastUpdateDateTime")
        val lastUpdateDateTime: String? = null

    ) : Parcelable {
        @IgnoredOnParcel
        @Transient
        var localText: LocalText? = null
    }

    @Parcelize
    data class CommonIssue(

        @field:SerializedName("CommentValue")
        val commentValue: String? = null,

        @field:SerializedName("CommonIssue")
        val commonIssue: String? = null,

        @field:SerializedName("CommonIssueId")
        val commonIssueId: Int? = null
    ) : Parcelable

    @Parcelize
    data class ListItem(

        @field:SerializedName("Description")
        val description: String? = null,

        @field:SerializedName("CustomActionCode")
        val customActionCode: String? = null,

        @field:SerializedName("CommentIsNeeded")
        val commentIsNeeded: Int? = null,

        @field:SerializedName(
            "StatusQueryIssueId",
            alternate = ["CommonIssueId", "StatusSuccessListId"]
        )
        val listId: Int? = null,

        @field:SerializedName("GPSIsNeeded")
        val gPSIsNeeded: Int? = null,

        @field:SerializedName("IgnoredStatusQueryJson")
        val ignoredStatusQueryJson: List<IgnoredStatusQueryJsonItem>? = null
    ) : Parcelable

    @Parcelize
    data class IgnoredStatusQueryJsonItem(

        @field:SerializedName("StatusQueryId")
        val statusQueryId: Int? = null
    ) : Parcelable

    @Parcelize
    data class DataTypeSetting(

        @field:SerializedName("File")
        val file: File? = null,

        @field:SerializedName("TodayDateNeeded")
        val todayDateNeeded: Boolean = false
    ) : Parcelable

    @Parcelize
    data class File(

        @field:SerializedName("MinSize")
        val minSize: String? = null,

        @field:SerializedName("CameraIsNeeded")
        val cameraIsNeeded: Boolean? = null,

        @field:SerializedName("IsFileBrowserNeeded")
        val isFileBrowserNeeded: Boolean? = null,

        @field:SerializedName("Extensions")
        val extensions: String? = null,

        @field:SerializedName("MaxSize")
        val maxSize: String? = null
    ) : Parcelable

    @Parcelize
    data class Value(

        @field:SerializedName("Extension")
        val extension: String? = null,

        @field:SerializedName("VTMTId")
        val vTMTId: String? = null,

        @field:SerializedName("MTId")
        val mTId: Int? = null,

        @field:SerializedName("FileId")
        val fileId: Int? = null,

        @field:SerializedName("VTFileId")
        val VTFileId: String? = null,

        @field:SerializedName("Reply")
        val reply: String? = null,

        @field:SerializedName("Id")
        val listSelectedId: Int? = null,

        @field:SerializedName("Text")
        val listSelectedText: String? = null,

        @field:SerializedName("Comment")
        val listComment: String? = null
    ) : Parcelable

    data class LocalText(
        val text: String?,
        val isEditable: Boolean,
        val onEditClick: (() -> Unit)? = null
    )
}
