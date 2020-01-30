package rezaei.mohammad.plds.views.qrReader


import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_qr_reader.*
import me.dm7.barcodescanner.zbar.BarcodeFormat
import me.dm7.barcodescanner.zbar.ZBarScannerView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import rezaei.mohammad.plds.R
import rezaei.mohammad.plds.data.model.local.QRResponse
import rezaei.mohammad.plds.data.model.response.ErrorHandling
import rezaei.mohammad.plds.util.setActivityTitle
import rezaei.mohammad.plds.util.snack
import rezaei.mohammad.plds.views.main.GlobalViewModel

class QrReaderFragment : Fragment(), ZBarScannerView.ResultHandler {

    private var scannerView: ZBarScannerView? = null
    private val globalViewModel: GlobalViewModel by sharedViewModel()
    private var isCodeDetected = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qr_reader, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setActivityTitle("Scan document QR code")
        checkPermission()
    }

    private fun checkPermission() {
        requestPermissions(arrayOf(android.Manifest.permission.CAMERA), 123)
    }

    private fun startQrCodeScanner() {
        scannerView = ZBarScannerView(requireContext()).apply {
            setResultHandler(this@QrReaderFragment)
            setAutoFocus(true)
            setFormats(arrayListOf(BarcodeFormat.QRCODE))
            startCamera()
        }
        cameraView.addView(scannerView)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123)
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED })
                startQrCodeScanner()
            else
                findNavController().popBackStack()
    }

    override fun onResume() {
        super.onResume()
        scannerView?.startCamera()
    }

    override fun onPause() {
        scannerView?.stopCamera()
        super.onPause()
    }


    override fun handleResult(rawResult: me.dm7.barcodescanner.zbar.Result?) {
        if (!isCodeDetected) {
            parseResult(rawResult)
            Handler().postDelayed({ scannerView?.resumeCameraPreview(this) }, 500)
        }
    }

    private fun parseResult(rawResult: me.dm7.barcodescanner.zbar.Result?) {
        val docRefNo = getDocRefNo(rawResult?.contents)
        if (docRefNo != null) {
            isCodeDetected = true
            globalViewModel.docRefNo.value = docRefNo
            findNavController().popBackStack()
        } else {
            view?.snack(ErrorHandling(errorMessage = "Document reference No not detected."))
        }
    }

    private fun getDocRefNo(rawText: String?): String? {
        return try {
            val result = Gson().fromJson<QRResponse>(rawText, QRResponse::class.java)
            result.docRefNo
        } catch (e: Exception) {
            null
        }
    }

}
