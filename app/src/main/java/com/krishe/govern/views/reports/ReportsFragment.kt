package com.krishe.govern.views.reports

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.krishe.govern.R
import com.krishe.govern.databinding.ReportsFragmentBinding
import com.krishe.govern.utils.BaseFragment
import com.krishe.govern.utils.KrisheUtils
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*

class ReportsFragment : BaseFragment() ,ReportsICallBack, DatePickerDialog.OnDateSetListener{

//    var spinnerArray = arrayOf("Dumbell", "Punching Bag", "Yoga Ball", "Skipping Rope")
    lateinit var binding: ReportsFragmentBinding
    private lateinit var viewModel: ReportsViewModel
    val adapter = ReportsStatusAdapter(ReportsStatusAdapter.ReportsAdapterListener {  })
    var imgDate = 0

    val data = listOf<ReportsItemModel>(
        ReportsItemModel(1,"Rotavator WLX", "21-Mar-2021", "Krishe", "Submitted"),
        ReportsItemModel(2,"Rotavator 2 WLX", "11-Mar-2021", "Krishe", "Follow-up"),
        ReportsItemModel(3,"Rotavator 3 WLX", "31-Jul-2021", "Krishe", "Approved"),
        ReportsItemModel(4,"Rotavator 4 WLX", "11-Mar-2021", "Krishe", "Follow-up"),
        ReportsItemModel(5,"Rotavator 5 WLX", "31-Jul-2021", "Krishe", "Approved"),
        ReportsItemModel(6,"Rotavator 6 WLX", "11-Mar-2021", "Krishe", "Follow-up"),
        ReportsItemModel(7,"Rotavator 7 WLX", "21-Mar-2021", "Krishe", "Submitted"),
    )
    companion object {
        fun newInstance() = ReportsFragment()
        const val TAG = "ReportsFragment"
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.reports_fragment, container, false)
        viewModel = ViewModelProvider(this).get(ReportsViewModel::class.java)
        binding.reportModel = viewModel
        viewModel.onViewAvailable(this)

        binding.reportsRecycleView.adapter = adapter
        // adapter.data = data
        adapter.submitList(data)

        //context?.let { KrisheUtils.setSpinnerItems(it,binding.reportsSpinner,spinnerArray) }
        // Spinner ItemClickListener
        binding.reportsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        };
        //viewModel.getReportsCall()
        viewModel.getData().observe(viewLifecycleOwner, Observer<List<ReportsItemModel>>(){
            Log.e(TAG, "onCreateView: ${it[0].reportName}" )
            adapter.submitList(it)
        })

        binding.imgDateClickFrom.setOnClickListener {
            imgDate = 0
            val now = Calendar.getInstance()
            val dpd = DatePickerDialog.newInstance(
                this@ReportsFragment,
                now[Calendar.YEAR],  // Initial year selection
                now[Calendar.MONTH],  // Initial month selection
                now[Calendar.DAY_OF_MONTH] // Inital day selection
            )
            dpd.maxDate = now
            dpd.show(parentFragmentManager,"Datepickerdialog")

        }

        binding.imgDateClickTo.setOnClickListener {
            imgDate = 1
            val now = Calendar.getInstance()
            val dpd = DatePickerDialog.newInstance(
                this@ReportsFragment,
                now[Calendar.YEAR],  // Initial year selection
                now[Calendar.MONTH],  // Initial month selection
                now[Calendar.DAY_OF_MONTH] // Inital day selection
            )
            dpd.maxDate = now
            dpd.show(parentFragmentManager,"Datepickerdialog")

        }

        return binding.root
    }

    override fun onError(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onPrHide() {
        progressBar.hide()
    }

    override fun onPrShow() {
        progressBar.show()
    }

    override fun onSuccess(list: List<ReportsItemModel>) {
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val date = year.toString() +
                "-" + KrisheUtils.oneDigToTwo(monthOfYear + 1) +
                "-" + KrisheUtils.oneDigToTwo(dayOfMonth)

        when (imgDate) {
            0 -> {
                binding.dateFromFilter.setText(date)
            }
            1 -> {
                binding.dateToFilter.setText(date)
            }
        }

    }
}