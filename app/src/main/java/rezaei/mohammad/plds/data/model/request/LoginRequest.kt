package rezaei.mohammad.plds.data.model.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(

    @field:SerializedName("PlatformKey")
    private val platformKey: String? = "Smartphone",

    @field:SerializedName("Username")
    val username: String? = null,

    @field:SerializedName("SystemKey")
    private val systemKey: String? = "PLDS",

    @field:SerializedName("Password")
    val password: String? = null,

    @field:SerializedName("DeviceInfo")
    val deviceInfo: String? = null
)