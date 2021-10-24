package  com.krishe.govern.views.reports

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.krishe.govern.R
import com.krishe.govern.databinding.ReportItemBinding
import com.krishe.govern.utils.KrisheUtils
import com.krishe.govern.views.newReport.NewReportActivity
import com.krishe.govern.views.newReport.NewReportModelReq

//class ReportsStatusAdapter : RecyclerView.Adapter<ReportsStatusAdapter.ReportsViewHolder>() {
class ReportsStatusAdapter(val listener: ReportsAdapterListener) :
    ListAdapter<ReportsItemModel, ReportsStatusAdapter.ReportsViewHolder>(ReportsDiffCallback()) {
    /*var data = listOf<ReportsItemModel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }*/
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportsViewHolder {
        return ReportsViewHolder.reportsViewHolder(this, parent)

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: ReportsViewHolder, position: Int) {
        val item = getItem(position)//data[position]
        holder.bindReport(item, listener)
    }

    //    class ReportsViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
    class ReportsViewHolder private constructor(val itemBinding: ReportItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        var boolean = true

        @SuppressLint("UseCompatLoadingForDrawables")
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bindReport(item: ReportsItemModel, listener: ReportsAdapterListener) {
            itemBinding.reportName.text = item.reportName
            itemBinding.reportDate.text = item.reportDate
            itemBinding.typeName.text = item.typeName
            itemBinding.reportStatusIcon.background = when (item.reportStatus) {
                "Submitted" -> itemView.context.getDrawable(R.drawable.custom_round_orange)
                "Follow-up" -> itemView.context.getDrawable(R.drawable.custom_round_purple)
                "Approved" -> itemView.context.getDrawable(R.drawable.custom_round_green)
                else -> itemView.context.getDrawable(R.drawable.custom_round_green)
            }

            itemView.setOnClickListener {
                if (boolean) {
                    itemBinding.statusGraphicLayout.visibility = View.VISIBLE
                    itemBinding.reportStatusIcon.visibility = View.INVISIBLE
                } else {
                    itemBinding.statusGraphicLayout.visibility = View.GONE
                    itemBinding.reportStatusIcon.visibility = View.VISIBLE
                }
                boolean = !boolean
            }

            itemBinding.viewReportClick.setOnClickListener {
                KrisheUtils.toastAction(itemView.context, "G0 Next page")

                val newReportModelReq = NewReportModelReq(item.typeName)
                newReportModelReq.implementID = item.reportName
                newReportModelReq.implementName = item.reportStatus
                newReportModelReq.reportTypeID = item.reportName
                newReportModelReq.reportTypeName = item.reportStatus
                newReportModelReq.ownerShip = item.reportStatus
                newReportModelReq.latitude = 0.0
                newReportModelReq.longitude = 0.0

                val intent = Intent(itemView.context, NewReportActivity::class.java)
                /*intent.putExtra("implementListID", "implementListID")
                intent.putExtra("reportType", "reportType")
                intent.putExtra("latitude", "latitude")
                intent.putExtra("longitude", "longitude")
                intent.putExtra("from", "recycle")*/
                intent.putExtra("dateModel", newReportModelReq)
                intent.putExtra("from", "listAdapter")
                itemView.context.startActivity(intent)
            }
        }

        companion object {
            fun reportsViewHolder(
                reportsStatusAdapter: ReportsStatusAdapter,
                parent: ViewGroup
            ): ReportsViewHolder {
                reportsStatusAdapter.context = parent.context
                //val view = LayoutInflater.from(parent.context).inflate(R.layout.report_item, parent, false)
                val binding =
                    ReportItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ReportsViewHolder(binding)
            }
        }
    }

    // override fun getItemCount(): Int = data.size

    class ReportsDiffCallback : DiffUtil.ItemCallback<ReportsItemModel>() {
        override fun areItemsTheSame(
            oldItem: ReportsItemModel,
            newItem: ReportsItemModel
        ): Boolean {
            return oldItem.reportId == newItem.reportId
        }

        override fun areContentsTheSame(
            oldItem: ReportsItemModel,
            newItem: ReportsItemModel
        ): Boolean {
            return oldItem == newItem
        }
    }

    class ReportsAdapterListener(val clickListener: (item: ReportsItemModel) -> Unit) {
        // fun onClickLayout(item: ReportsItemModel) = clickListener(item)
        fun onClick(item: ReportsItemModel) = clickListener(item)
    }

    /*@SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun ReportsViewHolder.bindReport(item: ReportsItemModel) {
        reportDate.text = item.reportDate
        reportName.text = item.reportName
        typeName.text = item.typeName
        reportStatusIcon.background = when (item.reportStatus) {
            "Submitted" -> context.getDrawable(R.drawable.custom_round_orange)
            "Follow-up" -> context.getDrawable(R.drawable.custom_round_purple)
            "Approved" -> context.getDrawable(R.drawable.custom_round_green)
            else -> context.getDrawable(R.drawable.custom_round_green)
        }

        itemView.setOnClickListener {
            if (boolean) {
                statusGraphicLayout.visibility = View.VISIBLE
                reportStatusIcon.visibility = View.INVISIBLE
            } else {
                statusGraphicLayout.visibility = View.GONE
                reportStatusIcon.visibility = View.VISIBLE
            }
            boolean = !boolean
        }

        viewReportClick.setOnClickListener {
            Toast.makeText(context, "G0 Next page", Toast.LENGTH_SHORT).show()
        }
    }*/

}