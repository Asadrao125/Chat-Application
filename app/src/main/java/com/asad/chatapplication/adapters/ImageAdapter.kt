package com.asad.chatapplication.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.asad.chatapplication.R
import com.asad.chatapplication.activities.Home
import com.squareup.picasso.Picasso
import java.io.File

class ImageAdapter(var context: Context, var list: ArrayList<Uri>) :
    RecyclerView.Adapter<ImageAdapter.MyViewHolder>() {

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
        Log.d("path#", "onBindViewHolder: " + path)
        holder.selectedImage.setImageURI(path)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var selectedImage: ImageView

        init {
            selectedImage = itemView.findViewById(R.id.selectedImage)
        }
    }
}