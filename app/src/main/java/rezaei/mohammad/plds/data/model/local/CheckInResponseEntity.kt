package rezaei.mohammad.plds.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CheckInResponseEntity(
    var trackingInterval: Int?,
    var checkInPart: String?,
    @PrimaryKey
    var locationId: Int?,
    var vTLocation: String?,
    var uTPId: Int?,
    var vTUTPId: String?,
    var locationType: String?,
    var vTLocationId: String?,
    var locationName: String?
)