package rezaei.mohammad.plds.data.model.request

import com.google.gson.annotations.SerializedName

class CommonIssueRequest(
    @field:SerializedName("DocumentsInfo")
    var documentList: List<DocumentsInfoItem>?
)