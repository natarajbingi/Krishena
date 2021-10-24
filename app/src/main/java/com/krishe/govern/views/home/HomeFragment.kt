package com.krishe.govern.views.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.krishe.govern.MainActivity
import com.krishe.govern.R
import com.krishe.govern.databinding.HomeFragmentBinding
import com.krishe.govern.models.ImplementsDataRes
import com.krishe.govern.utils.BaseFragment
import com.krishe.govern.utils.KrisheUtils
import com.krishe.govern.views.initreport.InitReportFragment
import com.krishe.govern.views.reports.ReportsFragment


class HomeFragment : BaseFragment(), View.OnClickListener, InitIReportCallBack {

    lateinit var binding: HomeFragmentBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var ctx: Context
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

        binding.reportLayout.setOnClickListener(this)
        binding.newQrCodeLayout.setOnClickListener(this)

        binding.statsGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener{
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                when (checkedId){
                    R.id.statMontly -> context?.let { KrisheUtils.toastAction(it,"statMontly") }
                    R.id.statFY -> context?.let { KrisheUtils.toastAction(it,"statFY") }
                }
            }

        })

        return binding.root
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
        sessions.setUserString(KrisheUtils.DATETIME("nope"), KrisheUtils.oldProcessingDate)

    }

    private fun checkListSetUp() {
        val checkDate: String? = sessions.getUserString( KrisheUtils.oldProcessingDate)
        if (checkDate == null || !KrisheUtils.DATETIME("nope").equals(checkDate)) {
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

    /*
    *  val name = "Jenny Jones"
            val age = 25
            val bundle = Bundle()
            bundle.putString("name",name)
            bundle.putInt("age",age)
            val fragment = FirstFragment()
            fragment.arguments = bundle

            // Call the extension function for fragment transaction
            context.replaceFragment(fragment)
    * */
}