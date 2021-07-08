package rezaei.mohammad.plds.formBuilder

import rezaei.mohammad.plds.data.model.request.ElementResult

interface FormView {
    fun validate(): Boolean
    val elementId: Int
    val result: ElementResult?
    var valueIndex: Int
}
