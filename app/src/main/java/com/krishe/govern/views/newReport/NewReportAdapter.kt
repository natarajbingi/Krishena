package com.krishe.govern.views.newReport

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krishe.govern.R
import com.krishe.govern.databinding.ImgaeLayoutBinding
import com.krishe.govern.views.reports.ReportsFragment
import java.io.File

class NewReportAdapter(val mListener: OnItemClickListener) :
    ListAdapter<NameImageModel, NewReportAdapter.NewReportViewHolder>(NewReportDiffCallback()) {
    /*var data = listOf<NameImageModel>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }*/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewReportViewHolder {
        val binding = ImgaeLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewReportViewHolder(binding)

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: NewReportViewHolder, position: Int) {
        val item = getItem(position)//data[position]

        holder.bindNewReport(item)
    }

    inner class NewReportViewHolder(private val itemBinding: ImgaeLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bindNewReport(item: NameImageModel) {
            itemBinding.nameImgModel = item
            itemBinding.locationPositionName.text = item.imgName
            Log.e(ReportsFragment.TAG, "bindNewReport: ${item.toString()} " )
            if (item.imgPath.isNotEmpty()) {
                itemBinding.locationPosiRemove.visibility = View.VISIBLE
                val f = File(item.imgPath)
                Glide.with(itemView.context)
                    .load(f)
                    .centerCrop()
                    .placeholder(R.drawable.loader)
                    .error(R.drawable.blankimge)
                    .into(itemBinding.imgView)
            } else {
                itemBinding.locationPosiRemove.visibility = View.GONE
                itemBinding.imgView.setImageDrawable(itemView.context.getDrawable(R.drawable.image_upload))
                /*Glide.with(itemView.context)
                    .load(f)
                    .centerCrop()
                    .placeholder(R.drawable.loader)
                    .error(R.drawable.blankimge)
                    .into(itemBinding.imgView)*/
            }

            itemBinding.executePendingBindings()

            itemBinding.imgView.setOnClickListener {
                if (item.isEditable)
                    mListener.onItemClick(item, adapterPosition)
            }
            itemBinding.locationPosiRemove.setOnClickListener {
                if (item.isEditable)
                    mListener.onItemRemove(item, adapterPosition)
            }

        }
    }

    // override fun getItemCount(): Int = data.size // ListAdapter will handle

    class NewReportDiffCallback : DiffUtil.ItemCallback<NameImageModel>() {
        override fun areItemsTheSame(oldItem: NameImageModel, newItem: NameImageModel): Boolean {
            return oldItem.imgPosition == newItem.imgPosition
        }

        override fun areContentsTheSame(oldItem: NameImageModel, newItem: NameImageModel): Boolean {
            return oldItem == newItem
        }
    }

}

interface OnItemClickListener {
    fun onItemClick(item: NameImageModel, position: Int)

    fun onItemRemove(item: NameImageModel, position: Int)
}
