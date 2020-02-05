package rezaei.mohammad.plds.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class DocumentStatusResponse : BaseResponse<DocumentStatusResponse.Data>() {
    @Parcelize
    data class Data(

        @field:SerializedName("DocumentStatusId")
        val documentStatusId: Int? = null,

        @field:SerializedName("ProcessStepStage")
        val processStepStage: Int? = null,

        @field:SerializedName("StatusDescription")
        val statusDescription: String? = null,

        @field:SerializedName("DocumentReferenceNo")
        val documentReferenceNo: String? = null,

        @field:SerializedName("Title")
        val title: String? = null,

        @field:SerializedName("IsAbleToAcceptMultipleDocuments")
        val isAbleToAcceptMultipleDocuments: Int? = null,

        @field:SerializedName("Question")
        val question: String? = null,

        @field:SerializedName("VT")
        val vT: String? = null,

        @field:SerializedName("GPSIsNeeded")
        val gpsIsNeeded: Int? = null
    ) : Parcelable
}