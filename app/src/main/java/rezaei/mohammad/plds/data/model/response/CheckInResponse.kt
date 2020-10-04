package rezaei.mohammad.plds.data.model.response

import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

class CheckInResponse : BaseResponse<CheckInResponse.Data>() {

    data class TrackingConfig(

        @field:SerializedName("TrackingInterval")
        val trackingInterval: Int? = null
    )

    @Parcelize
    @Entity(tableName = "Location")
    data class LocationItem(

        @field:SerializedName("LocationId")
        val locationId: Int? = null,

        @field:SerializedName("LocationType")
        val locationType: String? = null,

        @field:SerializedName("LocationName")
        val locationName: String? = null


    ) : Parcelable {
        override fun toString(): String {
            return "$locationId,$locationName,$locationType"
        }
    }

    data class Data(

        @field:SerializedName("TrackingConfig")
        val trackingConfig: TrackingConfig? = null,

        @field:SerializedName("CheckInPart")
        val checkInPart: String? = null,

        @field:SerializedName("LocationId")
        val locationId: Int? = null,

        @field:SerializedName("VTLocation")
        val vTLocation: String? = null,

        @field:SerializedName("UTPId")
        val uTPId: Int? = null,

        @field:SerializedName("VTUTPId")
        val vTUTPId: String? = null,

        @field:SerializedName("LocationType")
        val locationType: String? = null,

        @field:SerializedName("VTLocationId")
        val vTLocationId: String? = null,

        @field:SerializedName("LocationName")
        val locationName: String? = null,

        @field:SerializedName("Locations")
        val locations: List<LocationItem>? = null
    )
}
