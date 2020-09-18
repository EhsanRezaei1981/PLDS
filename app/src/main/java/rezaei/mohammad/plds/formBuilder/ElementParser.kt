package rezaei.mohammad.plds.formBuilder

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.transition.TransitionManager
import rezaei.mohammad.plds.data.model.request.ElementResult
import rezaei.mohammad.plds.data.model.request.FormResult
import rezaei.mohammad.plds.data.model.request.Result
import rezaei.mohammad.plds.data.model.response.CourtResponse
import rezaei.mohammad.plds.data.model.response.FormResponse
import rezaei.mohammad.plds.data.model.response.SheriffResponse

/**
 * Created by Rezaei_M on 3/6/2018.
 */
class ElementParser(
    private val fragment: Fragment,
    elementList: List<FormResponse.DataItem>?,
    private val containerView: ViewGroup,
    private val elementsActivityRequestCallback: ElementsActivityRequestCallback?,
    private val justReadOnly: Boolean = false
) {


    init {
        containerView.removeAllViews()
        TransitionManager.beginDelayedTransition(containerView)

        elementList?.forEach {
            when (it.dataType) {
                "String" -> {
                    createString(it)
                }
                "Date" -> {
                    createDate(it)
                }
                "File" -> {
                    createFile(it)
                }
                "List" -> {
                    createDropDown(it)
                }
            }
        }
    }

    fun isItemsValid(): Boolean {
        val listValidation = mutableListOf(true)
        for (x in 0..containerView.childCount) {
            val element = containerView.getChildAt(x)
            if (element is FormView)
                if (element.visibility == View.VISIBLE)
                    listValidation.add(element.validate())
        }
        return listValidation.all { it }
    }

    fun getResult(formResult: FormResult): FormResult {
        val result = mutableListOf<ElementResult?>()
        for (x in 0..containerView.childCount) {
            val element = containerView.getChildAt(x)
            if (element is FormView)
                if (element.visibility == View.VISIBLE) {
                    result.add(element.result)
                    (element.result as? ElementResult.ListResult)?.let {
                        if (it.gps != null)
                            formResult.gps = it.gps
                    }
                    (element.result as? ElementResult.IssueResult)?.let {
                        if (it.gps != null)
                            formResult.gps = it.gps
                    }

                }
        }
        if (formResult is FormResult.DocumentProgress)
            when (formResult.responseType) {
                "Unsuccessful" -> {
                    formResult.unsuccessful = Result(result)
                }
                "Successful" -> {
                    formResult.successful = Result(result)
                }
                else -> {
                    formResult.reportIssue = result[1].also {
                        (it as ElementResult.IssueResult).date =
                            (result[0] as ElementResult.StringResult).reply
                    }
                }
            }
        else
            (formResult as? FormResult.RespondedFields)?.elements = result
        return formResult
    }

    private fun hideElement(elementIds: List<Int?>?) {
        elementIds?.let {
            for (x in 0..containerView.childCount) {
                val element = containerView.getChildAt(x)
                if (element is FormView)
                    if (elementIds.contains(element.elementId)) {
                        element.visibility = View.GONE
                    }

            }
        } ?: kotlin.run {
            for (x in 0..containerView.childCount) {
                val element = containerView.getChildAt(x)
                if (element is FormView) {
                    element.visibility = View.VISIBLE
                }

            }
        }
    }

    private fun createString(structure: FormResponse.DataItem) {
        val param = ViewGroup.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val component = TextInputView(fragment.requireContext(), structure, justReadOnly)
        component.layoutParams = param
        component.isReadOnly = justReadOnly
        containerView.addView(component)
    }

    private fun createDropDown(structure: FormResponse.DataItem) {
        val param = ViewGroup.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val component = ListView(fragment, structure, object : OnListItemSelectedCallback {
            override fun onItemSelected(ignoreViewIds: List<Int?>?) {
                hideElement(ignoreViewIds)
            }

            override fun courtListNeeded(courtList: MutableLiveData<List<CourtResponse.Court>>) {
                elementsActivityRequestCallback?.courtListNeeded(courtList)
            }

            override fun sheriffListNeeded(sheriffList: MutableLiveData<List<SheriffResponse.Sheriff>>) {
                elementsActivityRequestCallback?.sheriffListNeeded(sheriffList)
            }
        }, justReadOnly)

        component.layoutParams = param
        containerView.addView(component)
    }

    private fun createDate(structure: FormResponse.DataItem) {
        val param = ViewGroup.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val component = DatePicker(fragment.requireContext(), structure, justReadOnly)
        component.layoutParams = param
        containerView.addView(component)
    }

    private fun createFile(structure: FormResponse.DataItem) {
        val param = ViewGroup.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val component = FileView(fragment, structure, object : FileRequestsCallback {
            override fun requestPermission(permission: String) {
                elementsActivityRequestCallback?.requestPermission(permission)
            }

            override fun onPhotoTaken(result: MutableLiveData<Intent>) {
                elementsActivityRequestCallback?.onPhotoTaken(result)
            }

            override fun onPreviewImageClicked(fileId: Int?, fileVT: String?, base64: String?) {
                elementsActivityRequestCallback?.onPreviewImageClicked(fileId, fileVT, base64)
            }
        }, justReadOnly)
        component.layoutParams = param
        containerView.addView(component)
    }
}

interface ElementsActivityRequestCallback {
    fun requestPermission(permission: String)
    fun onPhotoTaken(result: MutableLiveData<Intent>)
    fun courtListNeeded(courtList: MutableLiveData<List<CourtResponse.Court>>)
    fun sheriffListNeeded(sheriffList: MutableLiveData<List<SheriffResponse.Sheriff>>)
    fun onPreviewImageClicked(fileId: Int?, fileVT: String?, base64: String?)
}