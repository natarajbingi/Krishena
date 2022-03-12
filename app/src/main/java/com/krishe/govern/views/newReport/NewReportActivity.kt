package com.krishe.govern.views.newReport

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.krishe.govern.R
import com.krishe.govern.databinding.ActivityNewReportBinding
import com.krishe.govern.utils.BaseActivity
import com.krishe.govern.utils.FileUtil
import com.krishe.govern.utils.KrisheUtils
import com.krishe.govern.views.initreport.InitReportFragment.Companion.newReportModelReqData
import com.krishe.govern.views.reports.ReportsStatusAdapter.ReportsViewHolder.Companion.nameImageModelObj
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_new_report.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource
import java.io.File
import java.io.IOException

/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
class NewReportActivity : BaseActivity(), OnItemClickListener, NewReportCallBack {

    private lateinit var viewModel: NewReportViewModel
    lateinit var binding: ActivityNewReportBinding
    private lateinit var adapter: NewReportAdapter
    private lateinit var newReportModelReq: NewReportModelReq
    private lateinit var currentLinearLayout: LinearLayout
    private var currentLinearLayoutVal= 0

    var from: String = ""
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    var adapterPosition = -1
    var adapterItem: NameImageModel? = null

    var isNew: Boolean = false
    var direct: Boolean = false
    var testU: Boolean = true

    private val stepOne = 1
    private val stepTwo = 2
    private val stepThree = 3
    private var currentStep = 0

//    private val activityScope = CoroutineScope(Dispatchers.Default)
    private var actualImage: File? = null
    private var compressedImage: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        newReportModelReq = intent.getParcelableExtra<NewReportModelReq>("dateModel")!!
        from = intent.getStringExtra("from").toString()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_new_report)
        viewModel = ViewModelProvider(this).get(NewReportViewModel::class.java)
        binding.newReportModel = viewModel

        viewModel.onViewAvailable(this)
        onSetEasyImg(false, applicationContext)
        initSetUp()
    }

    private fun initSetUp() {

        currentLinearLayout = when (newReportModelReq.reportTypeName) {
            "Implement Condition Report" -> {
                currentLinearLayoutVal = 1
                binding.monthlyMainRepoLayout
            }
            "Pre-season Checklist" -> {
                currentLinearLayoutVal = 2
                binding.preSeasonalRepoLayout
            }
            "Breakdown report" -> {
                currentLinearLayoutVal = 3
                binding.BreakdownRepoLayout
            }
            else -> binding.monthlyMainRepoLayout
        }

        if (from == "fragment") {
            currentStep = stepOne
            binding.backButton.text = "Back"
        } else {
            currentStep = stepThree
            binding.backButton.text = "Edit"

            if (newReportModelReq.nameImageModel != null && newReportModelReq.nameImageModel.isNotEmpty()) {
                val arr = newReportModelReq.implementName.split("_")
                if (arr.size > 1) {
                    newReportModelReq.implementName = arr[0]
                    newReportModelReq.center = arr[1]
                }
                viewModel.defaultData.clear()
                viewModel.defaultData.addAll(nameImageModelObj(newReportModelReq.nameImageModel))
            }

            setUpSecSet()
        }

        adapter = NewReportAdapter(this)
        binding.stepOneImagesLayout.adapter = adapter
        adapter.submitList(viewModel.defaultData)

        binding.implementName.text = newReportModelReq.implementName
        binding.implementId.text = newReportModelReq.implementID
        binding.implementIdCenter.text = newReportModelReq.center
        binding.implementType.text = newReportModelReq.reportTypeName
        binding.ownerShip.text = newReportModelReq.ownerShip

        binding.forwardDecisionButton.setOnClickListener {

            // todo stepOne
            if (currentStep == stepOne) {
                var firstFiveImgCount = 0
                for (i in viewModel.defaultData) {
                    if (i.imgPosition < 5 && i.imgPath.isNotEmpty()) {
                        firstFiveImgCount++
                    }
                }

                if (firstFiveImgCount >= 5) {
                    currentStep = stepTwo
                    setUpCurrentStepView()
                } else {
                    KrisheUtils.toastAction(this, "Minimum first 5 Images are Mandatory")
                }

            } else
            // todo stepTwo
                if (currentStep == stepTwo) {
                    if (!validateSet()) {
                        currentStep = stepThree
                        setUpCurrentStepView()
                    }

                } else
                // todo stepThree
                    if (currentStep == stepThree) {
                        if (KrisheUtils.isOnline(this)) {
                            if (from == "fragment")
                                confirmAlertDialog("Are you sure want to Submit the Implement?")
                            else
                                confirmAlertDialog("Are you sure want to update the Implement?")
                        } else {
                            KrisheUtils.toastAction(this, getString(R.string.no_internet))
                        }
                    }
        }

        viewModel.countAzrVV.observe(this, Observer {

            if (from != "fragment" && viewModel.countAzr == 0) {
                viewModel.countAzr = viewModel.count
            }
            if (viewModel.countAzr == viewModel.defaultData.size) {
                testU = false

                val g = Gson()
                val toJsonStr = g.toJson(viewModel.defaultData)
                newReportModelReq.implementName = newReportModelReq.implementName + "_" + newReportModelReq.center
                newReportModelReq.nameImageModel = toJsonStr
                testU = false
                if (from == "fragment") {
                    newReportModelReq.userID = sessions.getUserString(KrisheUtils.userID).toString()
                    viewModel.addImplement(newReportModelReq)
                } else {
                    viewModel.updateImplement(newReportModelReq)
                    //KrisheUtils.toastAction(this, "updateImplement is in progress" )
                }
                setUpCurrentStepView()
            }


        })

        // Other images to add
        binding.imgAddView.setOnClickListener {
            var firstFiveImgCount = 0
            for (i in viewModel.defaultData) {
                if (i.imgPosition < 5 && i.imgPath.isNotEmpty()) {
                    firstFiveImgCount++
                }
            }
            if (firstFiveImgCount >= 5) {
                isNew = true
                direct = false

                adapterPosition = viewModel.defaultData.size
                adapterItem = NameImageModel(adapterPosition, "", "", true)

                selectImage(this)
                //askNameForImgAlertBox()
            } else {
                KrisheUtils.toastAction(this, "Add Minimum first 5 Images to add further images")
            }
        }

        // back button click
        binding.backButton.setOnClickListener {
            if (binding.backButton.text == "Edit") {
                binding.backButton.text = "Back"
                currentStep = stepOne
                setUpCurrentStepView()
            } else
                onBackPressMethod()
        }

        setUpCurrentStepView()
    }

    private fun setUpSecSet() {
        when (currentLinearLayoutVal) {
            1 -> {
                binding.reportCommentsEditext.setText(newReportModelReq.reportComment)
                when (newReportModelReq.currentImplementStatus) {
                    getString(R.string.implement_is_perfectly_okay) -> binding.radioButton1.isChecked = true
                    getString(R.string.implement_is_okay_but_schedule_service_is_due) -> binding.radioButton2.isChecked = true
                    getString(R.string.implement_is_okay_but_requires_minor_repairs) -> binding.radioButton3.isChecked = true
                    getString(R.string.implement_is_not_okay_breakdown_major_repairs_and_parts_are_needed) -> binding.radioButton4.isChecked = true
                    getString(R.string.implement_is_not_okay_can_be_scrapped) -> binding.radioButton5.isChecked = true
                    getString(R.string.other_please_enter_your_remarks_in_the_box_below) -> binding.radioButton6.isChecked = true
                }
            }
            2 -> {
                val strRB = newReportModelReq.currentImplementStatus.split("_")
                val strRBCmt = newReportModelReq.reportComment.split("_")
                binding.preSeasonalServiceComment.setText(strRBCmt[0])
                binding.preSeasonalReadinessComment.setText(strRBCmt[1])
                when (strRB[0]) {
                    getString(R.string.yes) -> binding.radiopreSeasonalServiceButton1.isChecked = true
                    getString(R.string.no) -> binding.radiopreSeasonalServiceButton2.isChecked = true
                    getString(R.string.other_please_enter_your_remarks_in_the_box_below) -> binding.radiopreSeasonalServiceButton3.isChecked = true
                }
                when (strRB[1]) {
                    getString(R.string.yes) -> binding.radiopreSeasonalReadinessButton1.isChecked = true
                    getString(R.string.no) -> binding.radiopreSeasonalReadinessButton2.isChecked = true
                    getString(R.string.other_please_enter_your_remarks_in_the_box_below) -> binding.radiopreSeasonalReadinessButton3.isChecked = true
                }
            }
            3 -> {
                binding.reportBreakdownEditext.setText(newReportModelReq.reportComment)
                when (newReportModelReq.currentImplementStatus) {
                    getString(R.string.minor_can_be_resolved_in_field_by_local_technicians_in_0_to_1_day) -> binding.radioBreakdownButton1.isChecked = true
                    getString(R.string.moderate_can_be_resolved_by_the_dealer_service_team_in_0_to_2_days) -> binding.radioBreakdownButton2.isChecked = true
                    getString(R.string.major_immediate_intervention_of_m_amp_m_team_is_needed) -> binding.radioBreakdownButton3.isChecked = true
                    getString(R.string.other_please_enter_your_remarks_in_the_box_below) -> binding.radioBreakdownButton4.isChecked = true
                }
            }
        }
    }

    private fun validateSet(): Boolean {
         when (currentLinearLayoutVal) {
            1 -> {
                val radioCheck = binding.currentImplementStatusGrp.checkedRadioButtonId
                return if (radioCheck == -1 ) {
                    KrisheUtils.toastAction(this, "Implement State is Mandatory")
                    true
                } else if (radioCheck == R.id.radioButton6 && binding.reportCommentsEditext.text.toString().isEmpty()) {
                    KrisheUtils.toastAction(this, "Comments are Mandatory")
                    true
                } else {
                    val rb = findViewById<View>(radioCheck) as RadioButton
                    newReportModelReq.currentImplementStatus = rb.text.toString()
                    newReportModelReq.reportComment = binding.reportCommentsEditext.text.toString()
                    false
                }
            }
            2 -> {
                val radioCheck1 = binding.preSeasonalServiceGrp.checkedRadioButtonId
                val radioCheck2 = binding.preSeasonalReadinessGrp.checkedRadioButtonId
                return if (radioCheck1 == -1 || radioCheck2 == -1){
                    KrisheUtils.toastAction(this, "Schedule Service & pre seasonal readiness")
                    true
                } else if (radioCheck1 == R.id.radiopreSeasonalServiceButton3 && binding.preSeasonalServiceComment.text.toString().isEmpty()) {
                    KrisheUtils.toastAction(this, "Schedule Service comments are Mandatory")
                    true
                }  else if (radioCheck2 == R.id.radiopreSeasonalReadinessButton3 && binding.preSeasonalReadinessComment.text.toString().isEmpty()) {
                    KrisheUtils.toastAction(this, "pre seasonal readiness comments are Mandatory")
                    true
                }  else {
                    val rb = findViewById<View>(radioCheck1) as RadioButton
                    val rb2 = findViewById<View>(radioCheck2) as RadioButton
                    val strRdBtn = rb.text.toString() +"_"+ rb2.text.toString()
                    val strRdCmt = binding.preSeasonalServiceComment.text.toString() +"_"+ binding.preSeasonalReadinessComment.text.toString()
                    newReportModelReq.currentImplementStatus = strRdBtn
                    newReportModelReq.reportComment = strRdCmt
                    false
                }

            }
            3 -> {
                val radioCheck = binding.BreakdownGrp.checkedRadioButtonId
                return if ( radioCheck == -1 ){
                    KrisheUtils.toastAction(this, "Nature of Breakdown is Mandatory")
                    true
                } else if (radioCheck == R.id.radioBreakdownButton4 && binding.reportBreakdownEditext.text.toString().isEmpty()) {
                    KrisheUtils.toastAction(this, "Comments are Mandatory")
                    true
                }  else {
                    val rb = findViewById<View>(radioCheck) as RadioButton
                    newReportModelReq.currentImplementStatus = rb.text.toString()
                    newReportModelReq.reportComment = binding.reportBreakdownEditext.text.toString()
                    false
                }
            }
            else -> {
                KrisheUtils.toastAction(this, "Something wrong, try again.")
                return true
            }
        }

    }

    private fun setUpCurrentStepView() {
        when (currentStep) {
            stepOne -> {
                binding.stepOneImagesLayout.visibility = View.VISIBLE
                binding.ifShowStepOneImagesLayout.visibility = View.VISIBLE
                binding.forwardDecisionButton.text = "Add Details"
                binding.stepTwoImagesLayout.visibility = View.GONE
                currentLinearLayout.visibility = View.GONE
            }
            stepTwo -> {
                binding.stepOneImagesLayout.visibility = View.GONE
                binding.ifShowStepOneImagesLayout.visibility = View.GONE
                binding.forwardDecisionButton.text = "Preview Report"
                binding.stepTwoImagesLayout.visibility = View.VISIBLE
                currentLinearLayout.visibility = View.VISIBLE
            }
            stepThree -> {
                binding.stepOneImagesLayout.visibility = View.VISIBLE
                binding.forwardDecisionButton.text = "Submit Report"
                binding.stepTwoImagesLayout.visibility = View.VISIBLE
                currentLinearLayout.visibility = View.VISIBLE

            }
        }

        setObserverReqData(currentStep != stepThree)
    }

    private fun setObserverReqData(isEnabled: Boolean) {

        for (itm in viewModel.defaultData.indices) {
            viewModel.defaultData[itm].isEditable = isEnabled
        }
        for (i in 0 until binding.currentImplementStatusGrp.childCount) {
            (binding.currentImplementStatusGrp.getChildAt(i) as RadioButton).isEnabled = isEnabled
        }
        for (i in 0 until binding.preSeasonalServiceGrp.childCount) {
            (binding.preSeasonalServiceGrp.getChildAt(i) as RadioButton).isEnabled = isEnabled
        }
        for (i in 0 until binding.preSeasonalReadinessGrp.childCount) {
            (binding.preSeasonalReadinessGrp.getChildAt(i) as RadioButton).isEnabled = isEnabled
        }
        for (i in 0 until binding.BreakdownGrp.childCount) {
            (binding.BreakdownGrp.getChildAt(i) as RadioButton).isEnabled = isEnabled
        }
        // binding.currentImplementStatusGrp.isEnabled = isEnabled
        binding.reportCommentsEditext.isEnabled = isEnabled
        binding.preSeasonalServiceComment.isEnabled = isEnabled
        binding.preSeasonalReadinessComment.isEnabled = isEnabled
        binding.reportBreakdownEditext.isEnabled = isEnabled
        adapter.submitList(viewModel.defaultData)

        newReportModelReqData.postValue(newReportModelReq)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage?.handleActivityResult(
            requestCode,
            resultCode,
            data,
            this,
            object : DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {

                    adapterItem?.imgPath = imageFiles[0].file.absoluteFile.toString()
//                    Log.e("TAG", "onMediaFilesPicked: imgPath ${adapterItem?.imgPath}" )
//                    val imageFileObj = ImageFile(imageFiles[0].file.toUri(),adapterItem?.imgPath.toString())
//                    val imgPathStr = imageFileObj.copyFileStream(this@NewReportActivity, imageFiles[0].file.toUri())
//                    adapterItem?.imgPath = imgPathStr
//                    Log.e("TAG", "onMediaFilesPicked: imgPathStr ${imgPathStr}" )
                    try {
                        actualImage = FileUtil.from(this@NewReportActivity, imageFiles[0].file.toUri())
                        actualImage?.let { imageFile ->
                            lifecycleScope.launch {
                                // Default compression
                                compressedImage = Compressor.compress(this@NewReportActivity, imageFile)
                                //setCompressedImage()
                                adapterItem?.imgPath = compressedImage?.absolutePath.toString()
                            }
                        } ?: Log.e("TAG","Please choose an image!")
                    } catch (e: IOException) {
                        //showError("Failed to read picture data!")
                        e.printStackTrace()
                    }

                    if (isNew) {
                        viewModel.defaultData.add(adapterItem!!)
                    } else {
                        viewModel.defaultData.set(adapterPosition, adapterItem!!)
                    }
                    if (direct) {
                        adapter.submitList(viewModel.defaultData)
                        adapter.notifyDataSetChanged()
                        isNew = false
                        ifShowStepOneImagesLayout()
                    } else {
                        askNameForImgAlertBox()
                    }

                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                    //Some error handling
                    error.printStackTrace()
                    KrisheUtils.alertDialogShowOK(
                        this@NewReportActivity,
                        "There is a problem landing image, please try again",
                        R.mipmap.ic_launcher,{ dialog, which ->
                            dialog.dismiss()
                        }
                    )
                }

                override fun onCanceled(source: MediaSource) {
                    //Not necessary to remove any files manually anymore
                }
            })
    }

    //Image capture recyclerView Items click.
    override fun onItemClick(item: NameImageModel, position: Int) {
        KrisheUtils.toastAction(baseContext, "${item.imgName} onItemClick Image")
        adapterItem = item
        adapterPosition = position
        if (position in 0..4) {
            direct = true
            callAlertDialog(item.imgPath, true)
        } else {
            direct = false
            callAlertDialog(item.imgPath, false)
        }

        ifShowStepOneImagesLayout()
    }

    private fun callAlertDialog(imgPath: String, direct: Boolean) {
        if (imgPath.isNotEmpty()) {
            KrisheUtils.alertDialogShowYesNo(this,"Are you sure want to Re-Take the IMAGE ?",
                { dialog, which ->
                    // if (direct) {
                    selectImage(this)
                     /*} else {
                         askNameForImgAlertBox()
                     }*/
                } )
        } else {
            // if (direct)
            selectImage(this)
            // else
            //    askNameForImgAlertBox()
        }
    }

    private fun confirmAlertDialog(msg: String) {
        KrisheUtils.alertDialogShowYesNo(this,msg
        ) { dialog, which ->
            binding.forwardDecisionButton.isEnabled = false
            viewModel.imageUploadSet(this)
        }
    }

    private fun askNameForImgAlertBox() {
        val builder = MaterialAlertDialogBuilder(this)
        // dialog title
        builder.setTitle("Enter the Image name.")
        // dialog message view
        val constraintLayout = viewModel.getEditTextLayout(this)
        builder.setView(constraintLayout)
        val textInputLayout =
            constraintLayout.findViewWithTag<TextInputLayout>("textInputLayoutTag")
        val textInputEditText =
            constraintLayout.findViewWithTag<TextInputEditText>("textInputEditTextTag")
        if (!isNew) textInputEditText.setText(adapterItem!!.imgName)

        // alert dialog positive button
        builder.setPositiveButton("Continue") { dialog, which ->
            val name = textInputEditText.text.toString()
            if (name.isNotEmpty()) {
                /*if (isNew) {
                    adapterPosition = defaultData.size
                    adapterItem = NameImageModel(adapterPosition, "", name, true)
                } else {*/
                adapterItem!!.imgName = name
                //   }
                textInputLayout.error = ""
                //selectImage(this)
                adapter.submitList(viewModel.defaultData)
                adapter.notifyDataSetChanged()

                isNew = false
                direct = false
                ifShowStepOneImagesLayout()
            } else {
                textInputLayout.error = "Name is required."
            }

        }
        // alert dialog other buttons
        builder.setNegativeButton("No", null)
        builder.setNeutralButton("Cancel", null)
        // set dialog non cancelable
        builder.setCancelable(false)
        // finally, create the alert dialog and show it
        val dialog = builder.create()
        dialog.show()
        // initially disable the positive button
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
        // edit text text change listener
        textInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.isNullOrBlank()) {
                    textInputLayout.error = "Name is required."
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .isEnabled = false
                } else {
                    textInputLayout.error = ""
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .isEnabled = true
                }
            }
        })

    }

    // Remove Image if with in 5 items else remove item.
    override fun onItemRemove(item: NameImageModel, position: Int) {
        KrisheUtils.alertDialogShowYesNo(this,"Are you sure want to Remove the IMAGE?",
            { dialog, which ->

                if (position in 0..4) {
                    item.imgPath = ""
                    viewModel.defaultData[position] = item
                } else {
                    viewModel.defaultData.removeAt(position)
                }
                adapter.submitList(viewModel.defaultData)
                adapter.notifyDataSetChanged()
                ifShowStepOneImagesLayout()

            } )
    }

    override fun onItemZoom(bitmap : Bitmap) {
        KrisheUtils.popUpImg(this,null,"Selected Image","",bitmap,"bitMap")
    }

    private fun onBackPressMethod() {
        when (currentStep) {
            stepThree -> {
                if (binding.backButton.text == "Edit") {
                    setResult(0)
                    super.onBackPressed()
                } else {
                    currentStep = stepTwo
                    setUpCurrentStepView()
                }
            }
            stepTwo -> {
                currentStep = stepOne
                setUpCurrentStepView()
            }
            stepOne -> {
                setResult(0)
                super.onBackPressed()
            }
        }
    }

    fun ifShowStepOneImagesLayout() {
        if (viewModel.defaultData.size >= 9) {
            binding.ifShowStepOneImagesLayout.visibility = View.GONE
        } else {
            binding.ifShowStepOneImagesLayout.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        onBackPressMethod()
    }

    override fun onError(msg: String) {
        binding.forwardDecisionButton.isEnabled = true
        hidePbar()
    }

    override fun onPrHide() {
        hidePbar()
    }

    override fun onPrShow() {
        showPbar(this)
    }

    override fun onSuccess(msg: String) {
        hidePbar()
        binding.forwardDecisionButton.isEnabled = true
        runOnUiThread {
            KrisheUtils.alertDialogShowOK(this,msg,R.mipmap.ic_launcher,
                { dialog, which ->
                    dialog.dismiss()
                    setResult(1)
                    finish()
                } )
        }
    }


}