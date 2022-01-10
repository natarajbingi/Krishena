package com.krishe.govern.views.newReport

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.krishe.govern.R
import com.krishe.govern.databinding.ImgaeLayoutBinding
import com.krishe.govern.networks.imgAzure.ImageManager
import com.krishe.govern.views.reports.ReportsFragment
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * Created by Nataraj Bingi on Oct 24, 2021
 */
class NewReportAdapter(val mListener: OnItemClickListener) :
    ListAdapter<NameImageModel, NewReportAdapter.NewReportViewHolder>(NewReportDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewReportViewHolder {
        val binding = ImgaeLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewReportViewHolder(binding)

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: NewReportViewHolder, position: Int) {
        val item = getItem(position)//data[position]

        holder.bindNewReport(item)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    inner class NewReportViewHolder(private val itemBinding: ImgaeLayoutBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        fun bindNewReport(item: NameImageModel) {
            itemBinding.nameImgModel = item
            itemBinding.locationPositionName.text = item.imgName
            Log.e(ReportsFragment.TAG, "bindNewReport: ${item.toString()} " )
            if (item.imgPath.isNotEmpty()) {
                itemBinding.locationPosiRemove.visibility = View.VISIBLE
                if (item.imgPath.contains("/")) {
                    glideImgLoad(item.imgPath,itemBinding.imgView,0)
                } else  {
                    glideImgLoad(item.imgPath,itemBinding.imgView,2)
                    loadImgViewDwnAzure(item.imgPath, itemBinding.imgView)
                }
            } else {
                itemBinding.locationPosiRemove.visibility = View.GONE
                itemBinding.imgView.setImageDrawable(itemView.context.getDrawable(R.drawable.image_upload))
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
            itemBinding.imgView.setOnLongClickListener {

                //if (item.isEditable){
                    val bitmap : Bitmap = itemBinding.imgView.drawable.toBitmap()
                    mListener.onItemZoom(bitmap)
                //}
                return@setOnLongClickListener true
            }

        }
    }

    fun glideImgLoad(imgPath: String, imgView: ImageView, imgType:Int) {
        val imgPathFile = when(imgType){
            0 -> File(imgPath)
            else -> imgPath
        }
        try {
            Glide.with(imgView.context)
                .load(imgPathFile)
                //.centerCrop()
                .placeholder(R.drawable.loader)
                .error(R.drawable.blankimge)
                .into(imgView)
        } catch (e:Exception){
            e.printStackTrace()
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

    fun loadImgViewDwnAzure(image: String, imageView: ImageView) {
        val imageStream = ByteArrayOutputStream()

        val handler = Handler()

        val th = Thread {
            try {
                val imageLength: Long = 0
                ImageManager.GetImage(image, imageStream, imageLength)
                handler.post {
                    val buffer: ByteArray = imageStream.toByteArray()
                    val bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.size)
                    //imageView.setImageBitmap(bitmap)
                    try {
                        Glide.with(imageView.context)
                            .load(bitmap)
                            //.centerCrop()
                            .placeholder(R.drawable.loader)
                            .error(R.drawable.blankimge)
                            .into(imageView)
                    } catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            } catch (ex: java.lang.Exception) {
                val exceptionMessage = ex.message
                handler.post {
                    Log.e("TAG", "$exceptionMessage")
                }
            }
        }
        th.start()
    }
}

