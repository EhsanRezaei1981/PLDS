package rezaei.mohammad.plds.data.model.request

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GetDocumentsOnLocationRequest(
    @field:SerializedName("LocationId")
    var locationId: Int? = null,

    @field:SerializedName("VTLocationId")
    var vTLocationId: String? = null,

    @field:SerializedName("VTLocation")
    var vTLocation: String? = null,

    @field:SerializedName("UTPId")
    var uTPId: Int? = null,

    @field:SerializedName("VTUTPId")
    var vTUTPId: String? = null,

    @field:SerializedName("LocationType")
    var locationType: String? = null

) : Parcelable