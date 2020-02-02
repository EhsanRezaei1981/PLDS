package rezaei.mohammad.plds.data.model.local

import androidx.room.*

@Entity(tableName = "document", indices = [Index("docRefNo", "documentType", unique = true)])
data class Document(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "docRefNo")
    var docRefNo: String,
    var documentType: DocumentType
) {
    @Ignore
    var positionInList: String? = null
}

enum class DocumentType { ReportIssue, CheckProgress }