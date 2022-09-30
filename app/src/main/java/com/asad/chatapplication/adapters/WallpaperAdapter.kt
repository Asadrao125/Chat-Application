package com.asad.chatapplication.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.asad.chatapplication.R
import com.asad.chatapplication.activities.Home
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import java.io.File

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

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val path = list.get(position)
        Glide.with(context).load(path)/*.placeholder(R.drawable.ic_launcher_background)*/
            .into(holder.selectedImage)

        if (selectedPosition == position) {
            holder.parentLayout.setBackgroundResource(R.drawable.wallpapper_selector_border)
        } else {
            holder.parentLayout.setBackgroundResource(R.drawable.wallpapper_selector_trans)
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