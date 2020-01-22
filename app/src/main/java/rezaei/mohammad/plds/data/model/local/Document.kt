package rezaei.mohammad.plds.data.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "document")
data class Document(
    @PrimaryKey
    var docRefNo: String
)