package com.asad.chatapplication.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.asad.chatapplication.R
import com.asad.chatapplication.models.StoryModal
import com.squareup.picasso.Picasso

class StoryAdapter(var context: Context, var list: ArrayList<StoryModal>) :
    RecyclerView.Adapter<StoryAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view: View =
            LayoutInflater.from(context).inflate(R.layout.item_story, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val storyModal: StoryModal = list.get(position)
        holder.tvUsername.setText(storyModal.storyUploaderName)
        holder.tvTime.setText(storyModal.storyTime)
        Picasso.get().load(storyModal.storyImageUrl).placeholder(R.drawable.ic_gallery)
            .into(holder.imgStatus)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvUsername: TextView
        var tvTime: TextView
        var imgStatus: ImageView

        init {
            tvUsername = itemView.findViewById(R.id.tvUsername)
            tvTime = itemView.findViewById(R.id.tvTime)
            imgStatus = itemView.findViewById(R.id.imgStatus)
        }
    }
}