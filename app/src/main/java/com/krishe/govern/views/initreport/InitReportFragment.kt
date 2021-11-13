package com.krishe.govern.views.initreport

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.krishe.govern.MainActivity
import com.krishe.govern.R
import com.krishe.govern.camera.MainFragment
import com.krishe.govern.databinding.InitReportsFragmentBinding
import com.krishe.govern.utils.BaseFragment
import com.krishe.govern.utils.GetCurrentGPSLocation
import com.krishe.govern.utils.KrisheUtils
import com.krishe.govern.utils.KrisheUtils.Companion.checkSinglePermission
import com.krishe.govern.utils.Sessions
import com.krishe.govern.views.home.CommunicationCallBack
import com.krishe.govern.views.newReport.NewReportActivity
import com.krishe.govern.views.newReport.NewReportModelReq
import com.krishe.govern.views.reports.ReportsFragment.Companion.TAG
import org.json.JSONObject


class InitReportFragment : BaseFragment(), CommunicationCallBack {

    lateinit var binding: InitReportsFragmentBinding
    private lateinit var viewModel: InitReportViewModel
    private lateinit var activity: Activity
    var implementID = ""
    private lateinit var ctx: Context
    var latitude = 0.0
    var longitude = 0.0
    lateinit var newReportModelReq : NewReportModelReq

    companion object {
        var newReportModelReqData: MutableLiveData<NewReportModelReq> = MutableLiveData()
        fun newInstance() = InitReportFragment()
    }

    override fun onAttach(context : Context) {
        super.onAttach(context )
        ctx = context
        sessions = Sessions(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.init_reports_fragment,container,false)
        viewModel = ViewModelProvider(this).get(InitReportViewModel::class.java)
        activity = requireActivity()
        binding.initReportModel = viewModel


        context?.registerReceiver(gpsReceiver, IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        // f0r QR c0de scanner result
        binding.scanQrCode.setOnClickListener {
            /*val string = "{\"id\": 483,\"implement_type\": \"Laser Leveller\",\"category\": \"Krish-e\", \"price_per_acre\": 480, \"price_per_day\": 3200,\"price_per_hour\": 400,\"implement_category\": \"Specialised Land Preparation\",\"implement_code\": \"LPLLLBRH001K\",\"dealership_name\": \"Vinayak Auto\",\"dealer_phone\": 9771494767 }"
            scannerToInit(string)*/

            val fragment = MainFragment()
            fragment.onViewAvailable(this)
            MainActivity.fragmentSetter.postValue(fragment)
           /* val action = InitReportFragmentDirections.actionInitReportFragmentToMainFragment()
            it.findNavController().navigate(action)*/
        }

        /*Log.d("error", "getExternalStorageDirectory "+Environment.getExternalStorageDirectory());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.d("error", "getStorageDirectory "+Environment.getStorageDirectory())
        };
        Log.d("error", "getExternalStorageState "+Environment.getExternalStorageState());
        Log.d("error", "getExternalFilesDir "+ (getActivity()?.getExternalFilesDir("KrishE-World") ?:"nt avaibe" ));*/

        binding.refreshImplementButton.setOnClickListener {
            KrisheUtils.toastAction(activity,"will refresh soon with API")
        }

        binding.addImageNxt.setOnClickListener {
            validate()
        }

        val locationSwitch = sessions.getUserString(KrisheUtils.locationSwitch)
        if(locationSwitch != null) binding.locationSwitch.isChecked = locationSwitch != "NO"

        if (binding.locationSwitch.isChecked){
            checkLocationPermissionAPI29()
            getLocationLatLong()
        }

        binding.locationSwitch.setOnCheckedChangeListener { compoundButton, b ->
            val value = if (b) {
                checkLocationPermissionAPI29()
                getLocationLatLong()
                "YES"
            } else {
                latitude = 0.0
                longitude = 0.0
                "NO"
            }
            sessions.setUserString(value, KrisheUtils.locationSwitch)
        }

        viewModel._scannerToInit.observe(viewLifecycleOwner, {
            implementID = it
            KrisheUtils.toastAction(ctx ,implementID)
            Log.d("_scannerToInit", "$implementID ")

        })

        viewModel._data.observe(viewLifecycleOwner, Observer {

            Log.e("data", " "+it?.get(0)?.implement_code)
        })

        return binding.root
    }

    fun validate() {
       // val implement = binding.implementListSpinner.selectedItem.toString()
        val reportType = binding.reportTypeSpinner.selectedItem.toString()

        if (implementID.isEmpty()){
            KrisheUtils.toastAction(activity,"Scan Implement from QR code")
            return
        }/* implement == "Select" && else { - elect Implement from drop down / s
            implementID = implement
        }*/

        if (reportType == "Select"){
            KrisheUtils.toastAction(activity,"please Select Report Type")
            return
        }
        if (binding.locationSwitch.isChecked){
            if (latitude <= 0){
                KrisheUtils.toastAction(activity,"Location is mandatory, please enable Location to proceed")
                return
            }
        }


        newReportModelReq.reportTypeID = reportType
        newReportModelReq.reportTypeName = reportType
        newReportModelReq.userID = sessions.getUserString("userID").toString()

        val intent = Intent(this.context, NewReportActivity::class.java)
        newReportModelReqData.postValue(newReportModelReq)

        intent.putExtra("dateModel", newReportModelReq)
        intent.putExtra("from", "fragment")

        regContract.launch(intent)
        //startActivity(intent)
    }

    val regContract = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result->
        if(result.resultCode == 1){
            implementID = ""
            binding.reportTypeSpinner.setSelection(0)
        }
    }
    fun getLocationLatLong(){
        val gps = GetCurrentGPSLocation(activity)

        // check if GPS enabled
        if (gps.canGetLocation()) {
              latitude = gps.latitude
              longitude = gps.longitude
              Log.d("GetLocationLatLong", "$latitude $longitude")

            if (latitude <= 0){
                // set Location image dark
                getLocationLatLong()
            } else {
                return
            }

        } else {
            // can't get location GPS or Network is not enabled Ask user to enable GPS/network in settings
            gps.showSettingsAlert()
        }

    }

    private val gpsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                //Do your stuff on GPS status change
                if (KrisheUtils.isLocationEnabled(activity)){
                    if (binding.locationSwitch.isChecked){
                        getLocationLatLong()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (binding.locationSwitch.isChecked){
            getLocationLatLong()
        }
    }
    // f0r QR c0de scanner result
    override fun scannerToInit(msg: String ){
        Log.e("implementID", " $msg")

        val msgJsonObj = JSONObject(msg)
        implementID = msgJsonObj.optString("id")
        newReportModelReq = NewReportModelReq(msgJsonObj.optString("id"))
        newReportModelReq.implementName = msgJsonObj.optString("implement_type")
        newReportModelReq.ownerShip = msgJsonObj.optString("dealership_name")
        newReportModelReq.latitude = latitude.toString()
        newReportModelReq.longitude = longitude.toString()
        KrisheUtils.toastAction(ctx ,newReportModelReq.implementName)
    }

    private fun checkLocationPermissionAPI29() {
        if (checkSinglePermission(activity,Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkSinglePermission(activity,Manifest.permission.ACCESS_COARSE_LOCATION) &&
            checkSinglePermission(activity,Manifest.permission.ACCESS_BACKGROUND_LOCATION)) return
        Log.e(TAG, "checkLocationPermissionAPI29: came" )

        val permList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.e(TAG, "checkLocationPermissionAPI29: >=Q" )
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        } else {
            Log.e(TAG, "checkLocationPermissionAPI29: <=Q" )
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
        ActivityCompat.requestPermissions(activity, permList, 50);
//        requestPermissions(permList, 50)

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }
}