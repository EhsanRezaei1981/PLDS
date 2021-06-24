package rezaei.mohammad.plds.formBuilder

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.transition.TransitionManager
import kotlinx.android.synthetic.main.view_title_text.view.*
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.request.*
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
                "Text" -> {
                    createText(it)
                }
                "String" -> {
                    createTextInput(it)
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

    fun getResult(
        formResult: FormResult,
        documentStatusQueryId: Int? = null,
        vt: String? = null
    ): FormResult {
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
        when (formResult) {
            is FormResult.DocumentProgress -> when (formResult.responseType) {
                "Unsuccessful" -> {
                    formResult.unsuccessful = Unsuccessful(
                        elements = result,
                        documentStatusQueryId = documentStatusQueryId,
                        vT = vt
                    )
                }
                "Successful" -> {
                    // Remove defendant from successful list and add it to defendant object
                    val defendantResult = result.firstOrNull { it is ElementResult.DefendantResult } as? ElementResult.DefendantResult
                    result.remove(defendantResult)

                    formResult.successful = Successful(
                        elements = result,
                        documentStatusId = documentStatusQueryId,
                        vT = vt,
                        documentDefendant = defendantResult?.documentDefendant
                    )
                }
                else -> {
                    formResult.reportIssue = result[1].also {
                        (it as ElementResult.IssueResult).date =
                            (result[0] as ElementResult.StringResult).reply
                        it.chosenFile = (result[2] as ElementResult.FileResult).chosenFile
                        it.documentStatusQueryId = documentStatusQueryId
                        it.vT = vt
                    }
                }
            }
            is FormResult.CommonAction -> {
                formResult.date = (result[0] as ElementResult.StringResult).reply
                formResult.commonActionId = (result[1] as ElementResult.ListResult).listItem?.id
                formResult.chosenFile = (result[2] as ElementResult.FileResult).chosenFile
                formResult.comment = (result[3] as ElementResult.StringResult).reply
            }
        }

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

    private fun createText(structure: FormResponse.DataItem) {
        if (structure.localText == null)
            return
        val localText = structure.localText
        val component = LayoutInflater.from(fragment.requireContext())
            .inflate(R.layout.view_title_text, containerView, false) as LinearLayoutCompat
        component.id = component.hashCode()
        component.txt_title.text = localText?.text
        component.btn_edit.isVisible = localText?.isEditable ?: false
        component.btn_edit.setOnClickListener { localText?.onEditClick?.invoke() }
        containerView.addView(component)
    }

    private fun createTextInput(structure: FormResponse.DataItem) {
        val param = ViewGroup.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val component = TextInputView(fragment.requireContext(), structure, justReadOnly)
        component.id = component.hashCode()
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
        component.id = component.hashCode()
        component.layoutParams = param
        containerView.addView(component)
    }

    private fun createDate(structure: FormResponse.DataItem) {
        val param = ViewGroup.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        val component = DatePicker(fragment.requireContext(), structure, justReadOnly)
        component.id = component.hashCode()
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

            override fun onImageSelected(result: MutableLiveData<Intent>) {
                elementsActivityRequestCallback?.onImageSelected(result)
            }

            override fun onPreviewImageClicked(fileId: Int?, fileVT: String?, base64: String?) {
                elementsActivityRequestCallback?.onPreviewImageClicked(fileId, fileVT, base64)
            }
        }, justReadOnly)
        component.id = component.hashCode()
        component.layoutParams = param
        containerView.addView(component)
    }
}

interface ElementsActivityRequestCallback {
    fun requestPermission(permission: String)
    fun onImageSelected(result: MutableLiveData<Intent>)
    fun courtListNeeded(courtList: MutableLiveData<List<CourtResponse.Court>>)
    fun sheriffListNeeded(sheriffList: MutableLiveData<List<SheriffResponse.Sheriff>>)
    fun onPreviewImageClicked(fileId: Int?, fileVT: String?, base64: String?)
}
