package com.krishe.govern.views.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.krishe.govern.MainActivity
import com.krishe.govern.R
import com.krishe.govern.databinding.HomeFragmentBinding
import com.krishe.govern.models.ImplementsDataRes
import com.krishe.govern.utils.BaseFragment
import com.krishe.govern.utils.KrisheUtils
import com.krishe.govern.views.initreport.InitReportFragment
import com.krishe.govern.views.reports.ReportsFragment
import org.json.JSONObject


class HomeFragment : BaseFragment(), View.OnClickListener, InitIReportCallBack {

    lateinit var binding: HomeFragmentBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var ctx: Context
    private var jsonObject: JSONObject = JSONObject()
    companion object {
        fun newInstance() = HomeFragment()
    }

    override fun onAttach(context : Context) {
        super.onAttach(context )
        ctx = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.home_fragment,container,false)
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        binding.homeModel = viewModel

        viewModel.onViewAvailable(this, sessions)

        checkListSetUp()
        checkStatisticYMtSetUp()

        binding.reportLayout.setOnClickListener(this)
        binding.newQrCodeLayout.setOnClickListener(this)

        binding.statsGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                when (checkedId){
                    R.id.statMontly -> {
                        setUpMontlyYr(0)
                        context?.let { KrisheUtils.toastAction(it,"Monthly") }
                    }
                    R.id.statFY -> {
                        setUpMontlyYr(1)
                        context?.let { KrisheUtils.toastAction(it,"This FY") }
                    }
                }
            }

        })

        return binding.root
    }

    private fun setUpMontlyYr(i: Int) {

        if (i==0) {
            binding.reportsSubmitted.text = jsonObject.optString("dataMSubmitted")
            binding.approvedByAdmin.text = jsonObject.optString("dataMApproved")
            binding.implementsUnderActive.text = jsonObject.optString("dataMFollowUp")
        } else {
            binding.reportsSubmitted.text = jsonObject.optString("dataYSubmitted")
            binding.approvedByAdmin.text = jsonObject.optString("dataYApproved")
            binding.implementsUnderActive.text = jsonObject.optString("dataYFollowUp")
        }

    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.reportLayout -> {
                MainActivity.fragmentSetter.postValue(ReportsFragment())
//                val action = HomeFragmentDirections.actionHomeFragmentToReportsFragment()
//                v.findNavController().navigate(action)
            }
            R.id.newQrCodeLayout -> {
                MainActivity.fragmentSetter.postValue(InitReportFragment())
//                val action = HomeFragmentDirections.actionHomeFragmentToInitReportFragment()
//                v.findNavController().navigate(action)
            }
            else -> {
//                val action = HomeFragmentDirections.actionHomeFragmentToReportsFragment()
//                v?.findNavController()?.navigate(action)
            }
        }
    }

    override fun onError(msg: String) {
        KrisheUtils.toastAction(ctx,msg)
    }

    override fun onPrHide() {
        progressBar.hide()
    }

    override fun onPrShow() {
        progressBar.show()
    }

    override fun onSuccessImplementList(list: ImplementsDataRes) {
        sessions.setUserString(KrisheUtils.dateTime("nope"), KrisheUtils.oldProcessingDate)

    }

    override fun onSuccessStatistics(msg: String) {
        jsonObject = JSONObject(msg)
        setUpMontlyYr(0)
    }

    private fun checkListSetUp() {
        sessions.setUserString("10",KrisheUtils.userID)

        val checkDate: String? = sessions.getUserString( KrisheUtils.oldProcessingDate)
        if (checkDate == null || !KrisheUtils.dateTime("nope").equals(checkDate)) {
            if (KrisheUtils.isOnline(ctx)) {
                viewModel.onGetImplementList()
            } else {
                KrisheUtils.toastAction(ctx, getString(R.string.no_internet))
            }
        } else {
            val resObj = sessions.getUserObj(KrisheUtils.implementListSession, ImplementsDataRes::class.java) as ImplementsDataRes
            viewModel.setImplementList(resObj.data)
        }
    }
    private fun checkStatisticYMtSetUp() {
            if (KrisheUtils.isOnline(ctx)) {
                val paramJson = JSONObject()
                paramJson.put("userID", sessions.getUserString(KrisheUtils.userID))
                paramJson.put("fromDate", KrisheUtils.dateFLYMTime("FDM"))
                paramJson.put("toDate", KrisheUtils.dateFLYMTime("LDM"))
                paramJson.put("fromYDate", KrisheUtils.dateFLYMTime("FDY"))
                paramJson.put("toYDate", KrisheUtils.dateFLYMTime("LDY"))
                viewModel.getStatisticImplement(paramJson)
            } else {
                KrisheUtils.toastAction(ctx, getString(R.string.no_internet))
            }
    }

}