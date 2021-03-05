package rezaei.mohammad.plds.data.model.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetFileRequest(

    @field:SerializedName("VTDocumentId")
    val vTDocumentId: String? = null,

    @field:SerializedName("DocumentId")
    val documentId: Int? = null,

    @field:SerializedName("VTFileId")
    var vTFileId: String? = null,

    @field:SerializedName("FileId")
    var fileId: Int? = null,

    @field:SerializedName("VTServiceId")
    val vTServiceId: String? = null,

    @field:SerializedName("ServiceId")
    val serviceId: Int? = null
) : Parcelable
