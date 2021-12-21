package com.krishe.govern.camera

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.krishe.govern.R
import com.krishe.govern.utils.BaseFragment
import com.krishe.govern.utils.KrisheUtils
import com.krishe.govern.views.home.CommunicationCallBack

class MainFragment : BaseFragment() {
    private lateinit var codeScanner: CodeScanner
    private lateinit var activity: Activity
    private lateinit var scannerView: CodeScannerView
    private lateinit var viewModel: MainViewModel
    var checkCameraPermission: Boolean = false
    var checkSuccessScan: Boolean = false

    //    private val callBackArgs: MainFragmentArgs by navArgs()
    private lateinit var callBack: CommunicationCallBack


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    fun onViewAvailable(v: CommunicationCallBack) {
        callBack = v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        scannerView = view.findViewById<CodeScannerView>(R.id.scanner_view)
        activity = requireActivity()

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        checkCameraPermission =
            KrisheUtils.checkSinglePermission(activity, Manifest.permission.CAMERA)
        if (!checkCameraPermission) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.CAMERA),
                50
            );
        } else {
            initVal(scannerView)
        }
    }

    private fun initVal(scannerView: CodeScannerView) {
        codeScanner = CodeScanner(activity, scannerView)
        codeScanner.decodeCallback = DecodeCallback {
            activity.runOnUiThread {
                checkSuccessScan = true
                callBack.scannerToInit(it.text)
//               viewModel._scannerToInit.postValue(it.text)
                parentFragmentManager.popBackStack()
                // Toast.makeText(activity, it.text, Toast.LENGTH_LONG).show()
            }
        }
        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 50 && grantResults[0] != -1) {
            initVal(scannerView)
        } else {
            KrisheUtils.toastAction(activity, "please provide Permission to proceed")
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.CAMERA),
                50
            );
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkCameraPermission) {
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        if (checkCameraPermission) {
            codeScanner.releaseResources()
        }
        super.onPause()
    }

    override fun onDestroy() {
        if (!checkSuccessScan) {
            callBack.scannerToInit("Failed or Cancelled scanning")
        }
        super.onDestroy()
    }
}
