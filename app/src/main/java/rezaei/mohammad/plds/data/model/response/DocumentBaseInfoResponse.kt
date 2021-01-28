package rezaei.mohammad.plds.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class DocumentBaseInfoResponse : BaseResponse<DocumentBaseInfoResponse.Data>() {
    @Parcelize
    data class Data(

        @field:SerializedName("ServiceId")
        val serviceId: Int? = null,

        @field:SerializedName("DocumentId")
        val documentId: Int? = null,

        @field:SerializedName("VTServiceId")
        val vTServiceId: String? = null,

        @field:SerializedName("VTDocumentId")
        val vTDocumentId: String? = null
    ) : Parcelable
}
