package rezaei.mohammad.plds.data.model.response

import android.os.Parcelable
import androidx.room.Entity
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import rezaei.mohammad.plds.data.model.local.CheckInResponseEntity

class CheckInResponse : BaseResponse<CheckInResponse.Data>() {

    data class TrackingConfig(

        @field:SerializedName("TrackingInterval")
        val trackingInterval: Long? = null,
        @field:SerializedName("DistanceInterval")
        val DistanceInterval: Long? = null,
        @field:SerializedName("AcceptableAccuracy")
        val acceptableAccuracy: Float? = null,
        @field:SerializedName("AcceptableTimePeriod")
        val acceptableTimePeriod: Long? = null
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


    ) : Parcelable

    data class Data(

        @field:SerializedName("TrackingConfig")
        var trackingConfig: TrackingConfig? = null,

        @field:SerializedName("CheckInPart")
        var checkInPart: String? = null,

        @field:SerializedName("LocationId")
        var locationId: Int? = null,

        @field:SerializedName("VTLocation")
        var vTLocation: String? = null,

        @field:SerializedName("UTPId")
        var uTPId: Int? = null,

        @field:SerializedName("VTUTPId")
        var vTUTPId: String? = null,

        @field:SerializedName("LocationType")
        var locationType: String? = null,

        @field:SerializedName("VTLocationId")
        var vTLocationId: String? = null,

        @field:SerializedName("LocationName")
        var locationName: String? = null,

        @field:SerializedName("Locations")
        val locations: List<LocationItem>? = null
    ) {
        fun toLocal() = CheckInResponseEntity(
            trackingConfig?.trackingInterval,
            checkInPart,
            locationId,
            vTLocation,
            uTPId,
            vTUTPId,
            locationType,
            vTLocationId,
            locationName
        )

        fun fromLocal(checkInResponseEntity: CheckInResponseEntity) {
            trackingConfig = TrackingConfig(
                checkInResponseEntity.trackingInterval
            )
            checkInPart = checkInResponseEntity.checkInPart
            locationId = checkInResponseEntity.locationId
            vTLocation = checkInResponseEntity.vTLocation
            uTPId = checkInResponseEntity.uTPId
            vTUTPId = checkInResponseEntity.vTUTPId
            locationType = checkInResponseEntity.locationType
            vTLocationId = checkInResponseEntity.vTLocationId
            locationName = checkInResponseEntity.locationName
        }
    }
}