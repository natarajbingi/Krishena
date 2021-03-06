package com.krishe.govern.views.reports

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.krishe.govern.R
import com.krishe.govern.databinding.ReportsFragmentBinding
import com.krishe.govern.utils.BaseFragment
import com.krishe.govern.utils.KrisheUtils
import com.krishe.govern.views.newReport.NewReportActivity
import com.krishe.govern.views.newReport.NewReportModelReq
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import org.json.JSONObject
import java.util.*

/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
class ReportsFragment : BaseFragment(), ReportsICallBack, OnReportItemClickListener,
    DatePickerDialog.OnDateSetListener {

    lateinit var binding: ReportsFragmentBinding
    private lateinit var viewModel: ReportsViewModel
    val adapter = ReportsStatusAdapter(this)
    var imgDate = 0
    var paramJson = JSONObject()
    lateinit var ctx: Context

    companion object {
        fun newInstance() = ReportsFragment()
        const val TAG = "ReportsFragment"
    }

    override fun onAttach(context: Context) {
        this.ctx = context
        super.onAttach(context)
    }

    val regContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == 1) {
                viewModel.getReportsCall(paramJson)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.reports_fragment, container, false)
        viewModel = ViewModelProvider(this).get(ReportsViewModel::class.java)
        binding.reportModel = viewModel
        viewModel.onViewAvailable(this)

        binding.reportsRecycleView.adapter = adapter
        // adapter.data = data
        // if (!data.isEmpty())
        //   adapter.submitList(data)

        //context?.let { KrisheUtils.setSpinnerItems(it,binding.reportsSpinner,spinnerArray) }
        // Spinner ItemClickListener
        binding.reportsSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                }

                override fun onNothingSelected(p0: AdapterView<*>?) {

                }
            };

        paramJson.put("userID", sessions.getUserString(KrisheUtils.userID))

        viewModel.getReportsCall(paramJson)
        viewModel.getData().observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })

        binding.SorryTryAgainBut.setOnClickListener {
            viewModel.getReportsCall(paramJson)
        }
        binding.imgDateClickFrom.setOnClickListener {
            imgDate = 0
            datePickerDialog()
        }

        binding.imgDateClickTo.setOnClickListener {
            imgDate = 1
            datePickerDialog()
        }

        binding.filterGet.setOnClickListener {
            paramJson = JSONObject()
            paramJson.put(KrisheUtils.userID, sessions.getUserString(KrisheUtils.userID))

            val fromDate = binding.dateFromFilter.text.toString().replace("From: ", "")
            val toDate = binding.dateToFilter.text.toString().replace("To: ", "")
            val reportTypeName = binding.reportsSpinner.selectedItem.toString()
            if (fromDate.isNotEmpty()) {
                paramJson.put("fromDate", fromDate)
            }
            if (toDate.isNotEmpty()) {
                paramJson.put("toDate", toDate)
            }
            if (reportTypeName != "Select" && reportTypeName.isNotEmpty()) {
                paramJson.put("reportTypeName", reportTypeName)
            }
            viewModel.getReportsCall(paramJson)
        }

        return binding.root
    }

    private fun datePickerDialog() {
        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
            this@ReportsFragment,
            now[Calendar.YEAR],  // Initial year selection
            now[Calendar.MONTH],  // Initial month selection
            now[Calendar.DAY_OF_MONTH] // Inital day selection
        )
        dpd.maxDate = now
        dpd.show(parentFragmentManager, "Datepickerdialog")
    }

    override fun onError(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        binding.reportsRecycleView.visibility = View.GONE
        binding.SorryTryAgainLayout.visibility = View.VISIBLE
        binding.SorryTryAgainText.text = msg
    }

    override fun onPrHide() {
        progressBar.hide()
    }

    override fun onPrShow() {
        progressBar.show()
    }

    override fun onSuccess(list: List<ReportsItemModel>) {
        binding.reportsRecycleView.visibility = View.VISIBLE
        binding.SorryTryAgainLayout.visibility = View.GONE
    }

    override fun onRemoveSuccess(msg: String) {
        KrisheUtils.alertDialogShowOK(ctx, msg, R.mipmap.ic_launcher, { dialog, which ->
            if (msg != "no record found")
                viewModel.getReportsCall(paramJson)
        })
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val date = year.toString() +
                "-" + KrisheUtils.oneDigToTwo(monthOfYear + 1) +
                "-" + KrisheUtils.oneDigToTwo(dayOfMonth)

        when (imgDate) {
            0 -> {
                binding.dateFromFilter.text = "From: $date"
            }
            1 -> {
                binding.dateToFilter.text = "To: $date"
            }
        }

    }

    override fun onItemClick(item: NewReportModelReq, position: Int) {
        //  "Go View page"

        val intent = Intent(context, NewReportActivity::class.java)
        intent.putExtra("dateModel", item)
        intent.putExtra("from", "listAdapter")

        regContract.launch(intent)
    }

    override fun onItemLongClick(item: NewReportModelReq, position: Int) {
        // want to delete?
        val jParam = JSONObject()
        jParam.put("id", item.id)
        jParam.put("userID", sessions.getUserString(KrisheUtils.userID))
        confirmDeleteDialog("Are you sure want to delete ?", jParam)
    }

    private fun confirmDeleteDialog(msg: String, jParam: JSONObject) {

        KrisheUtils.alertDialogShowYesNo(
            ctx,
            msg,
            { dialog, which -> viewModel.deleteReportsCall(jParam) })
    }
}