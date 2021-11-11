package com.krishe.govern.views.newReport

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import com.krishe.govern.R
import com.krishe.govern.databinding.ActivityNewReportBinding
import com.krishe.govern.utils.BaseActivity
import com.krishe.govern.utils.KrisheUtils
import com.krishe.govern.views.initreport.InitReportFragment.Companion.newReportModelReqData
import com.krishe.govern.views.reports.ReportsStatusAdapter.ReportsViewHolder.Companion.nameImageModelObj
import kotlinx.android.synthetic.main.activity_new_report.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import pl.aprilapps.easyphotopicker.DefaultCallback
import pl.aprilapps.easyphotopicker.MediaFile
import pl.aprilapps.easyphotopicker.MediaSource

class NewReportActivity : BaseActivity(), OnItemClickListener, NewReportCallBack {

    private lateinit var viewModel: NewReportViewModel
    lateinit var binding: ActivityNewReportBinding
    private lateinit var adapter: NewReportAdapter
    private lateinit var newReportModelReq: NewReportModelReq

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

    private val activityScope = CoroutineScope(Dispatchers.Default)

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

        if (from == "fragment") {
            currentStep = stepOne
            binding.backButton.text = "Back"
        } else {
            currentStep = stepThree
            binding.backButton.text = "Edit"

            if (newReportModelReq.nameImageModel != null && newReportModelReq.nameImageModel.isNotEmpty()) {
                viewModel.defaultData.clear()
                viewModel.defaultData.addAll(nameImageModelObj(newReportModelReq.nameImageModel))
            }
        }

        adapter = NewReportAdapter(this)
        binding.stepOneImagesLayout.adapter = adapter
        adapter.submitList(viewModel.defaultData)

        binding.implementName.text = newReportModelReq.implementName
        binding.implementId.text = newReportModelReq.implementID
        binding.implementType.text = newReportModelReq.reportTypeName
        binding.ownerShip.text = newReportModelReq.ownerShip

        binding.reportCommentsEditext.setText(newReportModelReq.reportComment)

        when (newReportModelReq.currentImplementStatus) {
            "Servicing Required" -> binding.radioButton2.isChecked = true
            "Major Damage" -> binding.radioButton3.isChecked = true
            "Replacement required" -> binding.radioButton4.isChecked = true
            "All OK" -> binding.radioButton5.isChecked = true
        }

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

                    val radioCheck = binding.currentImplementStatusGrp.checkedRadioButtonId
                    if (radioCheck == -1 || binding.reportCommentsEditext.text.toString()
                            .isEmpty()
                    ) {
                        KrisheUtils.toastAction(this, "Implement State & Comments are Mandatory")
                    } else {
                        currentStep = stepThree
                        val rb = findViewById<View>(radioCheck) as RadioButton
                        newReportModelReq.currentImplementStatus = rb.text.toString()
                        newReportModelReq.reportComment =
                            binding.reportCommentsEditext.text.toString()
                        setUpCurrentStepView()
                    }

                } else
                // todo stepThree
                    if (currentStep == stepThree) {
                        if (KrisheUtils.isOnline(this)) {
                            if (testU) {
                                viewModel.imageUploadSet(this)
                            }
                            runOnUiThread {
                                showPbar(this)
                            }
                            countAzrCheck() // Thread.sleep time to upload images to azure
                            val g = Gson()
                            val toJsonStr = g.toJson(viewModel.defaultData)
                            newReportModelReq.nameImageModel = toJsonStr
                            testU = false
                            if (from == "fragment") {
                                newReportModelReq.userID = "10" //sessions.getUserString("userID").toString()
                                viewModel.addImplement(newReportModelReq)
                            } else {
                                viewModel.updateImplement(newReportModelReq)
                                KrisheUtils.toastAction(this, "updateImplement is in progress" )
                            }
                            setUpCurrentStepView()
                        } else {
                            KrisheUtils.toastAction(this, getString(R.string.no_internet))
                        }
                    }
        }

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
            onBackPressMethod()
        }

        setUpCurrentStepView()
    }

    private fun countAzrCheck() {
        Thread.sleep(10000)
        if (from != "fragment" && viewModel.countAzr == 0) {
            viewModel.countAzr = viewModel.count
        }
        if (viewModel.countAzr == viewModel.defaultData.size) {
            testU = false
            return
        } else countAzrCheck()
    }

    private fun setUpCurrentStepView() {
        when (currentStep) {
            stepOne -> {
                binding.stepOneImagesLayout.visibility = View.VISIBLE
                binding.ifShowStepOneImagesLayout.visibility = View.VISIBLE
                binding.stepTwoImagesLayout.visibility = View.GONE
                binding.forwardDecisionButton.text = "Add Details"
            }
            stepTwo -> {
                binding.stepOneImagesLayout.visibility = View.GONE
                binding.ifShowStepOneImagesLayout.visibility = View.GONE
                binding.stepTwoImagesLayout.visibility = View.VISIBLE
                binding.forwardDecisionButton.text = "Preview Report"
            }
            stepThree -> {
                binding.stepOneImagesLayout.visibility = View.VISIBLE
                binding.stepTwoImagesLayout.visibility = View.VISIBLE
                binding.forwardDecisionButton.text = "Submit Report"

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
        // binding.currentImplementStatusGrp.isEnabled = isEnabled
        binding.reportCommentsEditext.isEnabled = isEnabled
        adapter.submitList(viewModel.defaultData)

        newReportModelReqData.postValue(newReportModelReq)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        easyImage!!.handleActivityResult(
            requestCode,
            resultCode,
            data,
            this,
            object : DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {

                    adapterItem?.imgPath = imageFiles[0].file.absoluteFile.toString()
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
                    /*uploadFile(
                        photos[0].getFile(),
                        Constants.CreateFileNameWithWith_Height(500, 600, selectedType))
                     Glide.with(this@NewReportActivity)
                        .load(photos[0].file)
                        //.centerCrop()
                        //.placeholder(R.drawable.loading_spinner)
                        .into(binding.testImg)*/;

                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                    //Some error handling
                    error.printStackTrace()
                    KrisheUtils.alertDialogShow(
                        this@NewReportActivity,
                        "There is a problem landing image, please try again",
                        resources.getString(R.string.app_name), R.mipmap.ic_launcher
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
            AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.app_name)
                .setMessage("Are you sure want to Re-Take the IMAGE ?")
                .setPositiveButton(getString(R.string.yes),
                    DialogInterface.OnClickListener { dialog, which ->
                        // if (direct) {
                        selectImage(this)
                        // } else {
                        //     askNameForImgAlertBox()
                        // }
                    })
                .setNegativeButton(getString(R.string.no), null)
                .show()
        } else {
            // if (direct)
            selectImage(this)
            // else
            //    askNameForImgAlertBox()
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
        AlertDialog.Builder(this)
            .setIcon(R.mipmap.ic_launcher)
            .setTitle(R.string.app_name)
            .setMessage("Are you sure want to Remove the IMAGE?")
            .setPositiveButton(getString(R.string.yes),
                DialogInterface.OnClickListener { dialog, which ->

                    if (position in 0..4) {
                        item.imgPath = ""
                        viewModel.defaultData[position] = item
                    } else {
                        viewModel.defaultData.removeAt(position)
                    }
                    adapter.submitList(viewModel.defaultData)
                    adapter.notifyDataSetChanged()
                    ifShowStepOneImagesLayout()

                })
            .setNegativeButton(getString(R.string.no), null)
            .show()

    }

    private fun onBackPressMethod() {
        when (currentStep) {
            stepThree -> {
                if ( binding.backButton.text == "Edit") {
                    currentStep = stepOne
                    binding.backButton.text = "Back"
                } else
                    currentStep = stepTwo
                setUpCurrentStepView()
            }
            stepTwo -> {
                currentStep = stepOne
                setUpCurrentStepView()
            }
            stepOne -> {
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
        runOnUiThread {
            alertDialogShow(msg, resources.getString(R.string.app_name), R.mipmap.ic_launcher)
        }
    }

    fun alertDialogShow(message: String, title: String, icon: Int) {
        val alertDialog = android.app.AlertDialog.Builder(this)
        alertDialog.setIcon(icon)
        alertDialog.setTitle(title)
        alertDialog.setMessage(message)
        alertDialog.setNegativeButton(
            "OK"
        ) { dialog, which ->
            dialog.dismiss()
            finish()
        }
        val alert = alertDialog.create()

        // show it
        alert.show()
    }

}