package rezaei.mohammad.plds.data.model.response

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.room.Entity
import androidx.room.Ignore
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

        @field:SerializedName("UserImageBase64")
        val userImage: String? = null,

        @field:SerializedName("EventLoginId")
        val eventLoginId: Int? = null,

        @field:SerializedName("LastEventLoginDateTime")
        val lastEventLoginDateTime: String? = null,

        @field:SerializedName("LastEventLoginPlatformName")
        val lastEventLoginPlatformName: String? = null,

        @field:SerializedName("SystemUrl")
        val systemUrl: String? = null
    ) {
        @Transient
        @Ignore
        var avatar: Bitmap? = null
            get() {
                userImage?.let {
                    val decodedString: ByteArray? = Base64.decode(
                        userImage.substring(
                            userImage.indexOf(",")
                            , userImage.length
                        ), Base64.DEFAULT
                    )
                    decodedString?.let {
                        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    }
                }
                return null
            }
    }
}