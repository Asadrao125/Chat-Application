package com.asad.chatapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asad.chatapplication.R
import com.bumptech.glide.Glide


class WallpaperAdapter(var context: Context, var list: ArrayList<Int>) :
    RecyclerView.Adapter<WallpaperAdapter.MyViewHolder>() {
    var selectedPosition = -1

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_image, parent, false)
        return MyViewHolder(view)
    }

    @SuppressLint("NewApi")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val path = list.get(position)
        Glide.with(context).load(path).into(holder.selectedImage)

        if (selectedPosition == position) {
            holder.parentLayout.alpha = 0.6F
            holder.selectedImage.setForeground(context.getDrawable(R.drawable.wallpapper_selector_border))
        } else {
            holder.parentLayout.alpha = 1F
            holder.selectedImage.setForeground(context.getDrawable(R.drawable.wallpapper_selector_trans))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var selectedImage: ImageView
        var parentLayout: LinearLayout

        init {
            selectedImage = itemView.findViewById(R.id.selectedImage)
            parentLayout = itemView.findViewById(R.id.parentLayout)
        }
    }
}