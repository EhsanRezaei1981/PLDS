package rezaei.mohammad.plds.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
class FormResponse : BaseResponse<List<FormResponse.DataItem>>(), Parcelable {
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
        val dataTypeSetting: DataTypeSetting? = null
    )

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
    )

    data class IgnoredStatusQueryJsonItem(

        @field:SerializedName("StatusQueryId")
        val statusQueryId: Int? = null
    )

    data class DataTypeSetting(

        @field:SerializedName("File")
        val file: File? = null
    )

    data class File(

        @field:SerializedName("MinSize")
        val minSize: String? = null,

        @field:SerializedName("CameraIsNeeded")
        val cameraIsNeeded: Boolean? = null,

        @field:SerializedName("Extensions")
        val extensions: String? = null,

        @field:SerializedName("MaxSize")
        val maxSize: String? = null
    )
}
