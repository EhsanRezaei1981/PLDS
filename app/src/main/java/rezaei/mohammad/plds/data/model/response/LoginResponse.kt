package rezaei.mohammad.plds.data.model.response

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

class LoginResponse : BaseResponse<LoginResponse.User>() {
    @Entity(tableName = "user")
    data class User(

        @field:SerializedName("LastEventLoginPlatformCode")
        val lastEventLoginPlatformCode: String? = null,

        @field:SerializedName("LastEventLoginIpAddress")
        val lastEventLoginIpAddress: String? = null,

        @PrimaryKey
        @field:SerializedName("UserId")
        val userId: Int? = null,

        @field:SerializedName("FirstName")
        val firstName: String? = null,

        @field:SerializedName("EventLoginSpecificLoginToken")
        val eventLoginSpecificLoginToken: String? = null,

        @field:SerializedName("Sex")
        val sex: String? = null,

        @field:SerializedName("JAToken")
        val jAToken: String? = null,

        @field:SerializedName("EventLoginId")
        val eventLoginId: Int? = null,

        @field:SerializedName("LastEventLoginDateTime")
        val lastEventLoginDateTime: String? = null,

        @field:SerializedName("LastEventLoginPlatformName")
        val lastEventLoginPlatformName: String? = null,

        @field:SerializedName("SystemUrl")
        val systemUrl: String? = null
    )
}