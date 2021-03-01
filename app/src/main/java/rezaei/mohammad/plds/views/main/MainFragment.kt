package rezaei.mohammad.plds.views.main

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.getInputField
import com.afollestad.materialdialogs.input.getInputLayout
import com.afollestad.materialdialogs.input.input
import kotlinx.android.synthetic.main.fragment_main.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import rezaei.mohammad.plds.BuildConfig
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.response.ErrorHandling
import rezaei.mohammad.plds.util.setActivityTitle
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.util.tryNavigate
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    companion object {
        val cameraRequest = 8754
    }

    private val globalViewModel: GlobalViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActivityTitle(getString(R.string.app_name))
        btnUpdateDocProgress.setOnClickListener {
            navigateToGetDocRef()
        }

        btnReportIssueGeneral.setOnClickListener {
            navigateToCommonAction()
        }

        btnReportIssueDocument.setOnClickListener {
            navigateToReportIssuePerDocument()
        }

        btnManageDocument.setOnClickListener {
            navigateToManageDoc()
        }

        btnTakePicture.setOnClickListener {
            checkPermissions()
        }

        setVersionName()
    }

    private fun navigateToGetDocRef() {
        val action = if (isCheckedIn())
            MainFragmentDirections.actionMainActivityFragmentToGetDocReferenceFragment()
        else
            MainFragmentDirections.actionMainActivityFragmentToCheckInFragment(
                null,
                chekinPartName = "UpdateDocumentProgress"
            )
        findNavController().tryNavigate(action)
    }

    private fun navigateToReportIssuePerDocument() {
        val action = if (isCheckedIn())
            MainFragmentDirections.actionMainActivityFragmentToReportIssueFragment()
        else
            MainFragmentDirections.actionMainActivityFragmentToCheckInFragment(
                null,
                chekinPartName = "ReportIssuePerDocument"
            )
        findNavController().tryNavigate(action)
    }

    private fun navigateToCommonAction() {
        val action = if (isCheckedIn())
            MainFragmentDirections.actionMainActivityFragmentToReportIssueInGeneralFragment()
        else
            MainFragmentDirections.actionMainActivityFragmentToCheckInFragment(
                null,
                chekinPartName = "ReportIssueInGeneral"
            )
        findNavController().tryNavigate(action)
    }

    private fun navigateToManageDoc() {
        val action = MainFragmentDirections.actionMainActivityFragmentToManageDocumentFragment()
        findNavController().tryNavigate(action)
    }

    private fun isCheckedIn(): Boolean {
        val checkInService = (requireActivity() as MainActivity).checkInService
        return checkInService != null && checkInService.isCheckedIn
    }

    private fun setVersionName() {
        txtVersion.text = "v${BuildConfig.VERSION_NAME}"
    }

    private fun checkPermissions() {
        requestPermissions(
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 123
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123)
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED })
                openCamera()
            else
                btnTakePicture.snack(
                    ErrorHandling(
                        errorMessage = getString(R.string.permission_denied)
                    ),
                    getString(R.string.grnat_permission),
                    { checkPermissions() }
                )
    }

    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
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
                        requireContext(),
                        "${requireContext().packageName}.provider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(
                        takePictureIntent,
                        cameraRequest
                    )
                }
            }
        }
    }

    private lateinit var currentPhotoPath: String

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir =
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(
            storageDir,
            "$timeStamp.jpg"
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == cameraRequest) {
            openRenameImageDialog()
        }
    }

    private fun openRenameImageDialog() {
        val fileName = File(currentPhotoPath).nameWithoutExtension

        MaterialDialog(requireContext()).show {
            title(R.string.choose_name_for_image)
            input(
                hintRes = R.string.image_name,
                prefill = fileName,
                allowEmpty = false,
                waitForPositiveButton = true
            )
            cancelOnTouchOutside(false)
            cancelable(false)
            noAutoDismiss()
            positiveButton(R.string.save_image) {
                val newPath =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val newName = it.getInputField().text.toString() + ".jpg"
                val newFile = File(newPath, newName)
                when {
                    newFile.exists() -> it.getInputLayout().error =
                        getString(R.string.image_exist_error)
                    else -> {
                        try {
                            File(currentPhotoPath).copyTo(newFile)
                            Toast.makeText(
                                requireContext(),
                                R.string.image_saved_successfully,
                                Toast.LENGTH_LONG
                            ).show()
                            File(currentPhotoPath).delete()
                            deleteLastPhotoTaken()
                            MediaScannerConnection.scanFile(
                                requireContext(),
                                arrayOf(newFile.absolutePath),
                                null,
                                null
                            )
                            it.dismiss()
                        } catch (e: Exception) {
                            Toast.makeText(
                                requireContext(),
                                getString(R.string.save_image_failed) + "\n" + e.message,
                                Toast.LENGTH_LONG
                            ).show()
                            it.dismiss()
                        }
                    }
                }
            }
            negativeButton(R.string.cancel) {
                File(currentPhotoPath).delete()
                deleteLastPhotoTaken()
                it.dismiss()
            }
        }
    }

    private fun deleteLastPhotoTaken() {
        val projection = arrayOf(
            MediaStore.Images.ImageColumns.DATA
        )
        val cursor: Cursor? = requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
            null, null, MediaStore.Images.Media.DATE_MODIFIED
        )
        if (cursor != null) {
            cursor.moveToFirst()
            while (cursor.moveToNext()) {
                val imagePath =
                    cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                if (File(imagePath).nameWithoutExtension == File(currentPhotoPath).nameWithoutExtension) {
                    File(imagePath).delete()
                    break
                }
            }
        }
        cursor?.close()
    }

}
