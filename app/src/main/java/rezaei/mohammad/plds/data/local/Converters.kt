package rezaei.mohammad.plds.data.local

import androidx.room.TypeConverter
import rezaei.mohammad.plds.data.model.local.DocumentType

object Converters {
    @TypeConverter
    @JvmStatic
    fun docTypeToString(documentType: DocumentType): String =
        documentType.name

    @TypeConverter
    @JvmStatic
    fun stringToDocType(documentType: String): DocumentType =
        DocumentType.valueOf(documentType)
}