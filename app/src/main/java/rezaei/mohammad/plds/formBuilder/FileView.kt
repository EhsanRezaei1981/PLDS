package rezaei.mohammad.plds.formBuilder

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.fileChooser
import kotlinx.android.synthetic.main.view_file.view.*
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.request.ChosenFile
import rezaei.mohammad.plds.data.model.request.ElementResult
import rezaei.mohammad.plds.data.model.response.FormResponse
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*


class FileView(
    context: Fragment,
    private val structure: FormResponse.DataItem,
    private val fileRequestsCallback: FileRequestsCallback,
    readOnly: Boolean = false
) : LinearLayout(context.requireContext()), FormView {

    private val fragment = WeakReference(context)

    private val onActivityResult = MutableLiveData<Intent>()
    private var selectedFile: File? = null
    private var takenPhoto: ByteArray? = null
    private var isClearImageClicked = false
    var isReadOnly: Boolean = false
        set(value) {
            btnBrowseFile.isGone = value
            btnDeleteImage.isGone = value
            field = value
        }

    init {
        isSaveEnabled = true
        View.inflate(context.requireContext(), R.layout.view_file, this)
        isReadOnly = readOnly
        setStructure()
    }


    private fun setStructure() {
        txtLabel.text = structure.label

        if (structure.value?.fileId != null || takenPhoto != null || selectedFile != null)
            showImageExistLayouts()
        else
            btnDeleteImage.isGone = true


        if (structure.dataTypeSetting?.file?.cameraIsNeeded == true) {
            btnBrowseFile.text = ""
            btnBrowseFile.icon = ContextCompat.getDrawable(context, R.drawable.ic_camera)
            btnBrowseFile.setOnClickListener { takePictureClick.invoke() }
            setupOnActivityResult()
        } else {
            txtFileExtensions.text = structure.dataTypeSetting?.file?.extensions
            btnBrowseFile.setOnClickListener { filePickerClick.invoke() }
        }
    }

    private fun showImageExistLayouts() {
        btnViewImage.isGone = false
        btnDeleteImage.isGone = isReadOnly
        isClearImageClicked = false

        btnViewImage.setOnClickListener {
            fileRequestsCallback.onPreviewImageClicked(
                structure.value?.fileId,
                structure.value?.VTFileId,
                takenPhoto?.toBase64() ?: selectedFile?.readBytes()?.toBase64()
            )
        }

        btnDeleteImage.setOnClickListener {
            isClearImageClicked = true
            it.isGone = true
            btnViewImage.isGone = true
        }
    }

    private val filePickerClick = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.requireActivity()
                    .checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ) {
                MaterialDialog(context.requireContext()).show {
                    fileChooser { dialog, file ->
                        selectedFile = file
                        showImageExistLayouts()
                        validate()
                    }
                }
            } else {
                fileRequestsCallback.requestPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            MaterialDialog(context.requireContext()).show {
                fileChooser { dialog, file ->
                    selectedFile = file
                    showImageExistLayouts()
                    validate()
                }
            }
        }

    }

    private val takePictureClick = {
        fileRequestsCallback.onPhotoTaken(onActivityResult)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.requireActivity()
                    .checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            ) {
                openCamera()
            } else {
                fileRequestsCallback.requestPermission(android.Manifest.permission.CAMERA)
            }
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(context.packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    ex.printStackTrace()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    fragment.get()?.startActivityForResult(takePictureIntent, cameraRequest)
                }
            }
        }
    }

    override fun validate(): Boolean {
        errors.clear()
        return if (structure.isMandatory == 0) {
            true
        } else {
            if (selectedFile == null && takenPhoto == null) {
                setError(context.getString(R.string.field_mandatory))
                false
            } else {
                val acceptableExtensions = structure.dataTypeSetting?.file?.extensions
                    ?.split(",")?.apply { forEach { it.trim() } }
                val maxFileSize = structure.dataTypeSetting?.file?.maxSize?.toLong() ?: 0
                val minFileSize = structure.dataTypeSetting?.file?.minSize?.toLong() ?: 0
                if (takenPhoto == null && acceptableExtensions?.contains(selectedFile!!.extension) == false) {
                    setError(context.getString(R.string.file_ext_err))
                    return false
                }
                if (takenPhoto == null && ((selectedFile!!.totalSpace < minFileSize) && (selectedFile!!.totalSpace > maxFileSize))) {
                    setError(context.getString(R.string.file_size_err))
                    return false
                }
                true
            }
        }
    }

    private val errors = mutableListOf<String>()
    private fun setError(error: String) {
        if (!errors.contains(error))
            errors.add(error)
        txtError.text = errors.joinToString(", ")
    }

    override val elementId: Int = structure.statusQueryId ?: 0

    override val result: ElementResult?
        get() = ElementResult.FileResult(
            elementId,
            when {
                selectedFile != null -> if (isClearImageClicked) ChosenFile() else ChosenFile(
                    selectedFile?.extension,
                    selectedFile?.readBytes()?.toBase64(),
                    selectedFile?.totalSpace?.toInt(),
                    selectedFile?.nameWithoutExtension
                )
                takenPhoto != null -> if (isClearImageClicked) ChosenFile() else ChosenFile(
                    "jpg",
                    takenPhoto?.toBase64(),
                    takenPhoto?.size,
                    System.currentTimeMillis().toString()
                )
                structure.value?.fileId != null -> if (isClearImageClicked) ChosenFile() else ChosenFile(
                    fileId = structure.value.fileId
                )
                else -> ChosenFile()
            },
            structure.value?.vTMTId,
            structure.value?.mTId
        )

    private fun setupOnActivityResult() {
        fragment.get()?.let { activity ->
            onActivityResult.observe(activity, Observer<Intent> {
                /*takenPhoto = ImageCompressor.compressImage(
                    File(currentPhotoPath),
                    structure.dataTypeSetting?.file?.maxSize?.toLong() ?: 0
                )*/
                takenPhoto = File(currentPhotoPath).readBytes()
                showImageExistLayouts()
                validate()
            })
        }
    }

    lateinit var currentPhotoPath: String

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    companion object {
        val cameraRequest = 2364
    }

}

interface FileRequestsCallback {
    fun requestPermission(permission: String)
    fun onPhotoTaken(result: MutableLiveData<Intent>)
    fun onPreviewImageClicked(fileId: Int?, fileVT: String?, base64: String?)
}

fun ByteArray.toBase64(): String? =
    Base64.encodeToString(this, Base64.DEFAULT)
