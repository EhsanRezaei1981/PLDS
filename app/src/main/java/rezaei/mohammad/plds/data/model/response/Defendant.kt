package rezaei.mohammad.plds.data.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Defendant(

	@field:SerializedName("PatronName")
	val patronName: String? = null,

	@field:SerializedName("PatronType")
	val patronType: String? = null,

	@field:SerializedName("DocumentLegalDefendantId")
	val documentLegalDefendantId: Int? = null,

	@field:SerializedName("VT")
	val vT: String? = null,

	@field:SerializedName("HasValue")
	val hasValue: Int? = null
): Parcelable
