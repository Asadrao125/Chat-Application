package com.asad.chatapplication.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.asad.chatapplication.R
import com.asad.chatapplication.activities.Chat
import com.asad.chatapplication.activities.ViewImage
import com.asad.chatapplication.models.ChatModel
import com.asad.chatapplication.utils.StaticFunctions.Companion.OpenFile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso

class ChatAdapter(
    var context: Chat,
    var list: ArrayList<ChatModel>,
    var username: String,
    var profilePicUrl: String
) :
    RecyclerView.Adapter<ChatAdapter.MyViewHolder>() {
    val MSG_TYPE_LEFT: Int = 0
    val MSG_TYPE_RIGHT: Int = 1
    var fuser: FirebaseUser? = null
    var selectedPosition = -1

    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View?
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_right, parent, false)
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_left, parent, false)
        }
        return MyViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(@NonNull holder: MyViewHolder, position: Int) {
        val chatModel: ChatModel = list.get(position)
        holder.tvMessage.text = chatModel.message
        holder.tvName.text = username
        holder.tvTime.text = chatModel.time
        Picasso.get().load(profilePicUrl).placeholder(R.drawable.ic_gallery).into(holder.imgProfile)

        if (selectedPosition == position) {
            holder.imageVoiceType.setImageResource(R.drawable.ic_stop)
        } else {
            holder.imageVoiceType.setImageResource(R.drawable.ic_play)
        }

        if (chatModel.imageUrl.isEmpty()) {
            holder.cv.visibility = View.GONE
        } else {
            Picasso.get().load(chatModel.imageUrl).placeholder(R.drawable.ic_gallery).into(holder.imageViewChat)
            holder.cv.visibility = View.VISIBLE
        }

        if (chatModel.voiceMessage.isEmpty()) {
            holder.voiceLayout!!.visibility = View.GONE
        } else {
            holder.voiceLayout!!.visibility = View.VISIBLE
        }

        if (!chatModel.fileUrl.isEmpty() || !chatModel.imageUrl.isEmpty() || !chatModel.voiceMessage.isEmpty()) {
            holder.tvMessage.visibility = View.GONE
        } else {
            holder.tvMessage.visibility = View.VISIBLE
        }

        if (chatModel.messageStatus.equals("Seen")) {
            holder.imgSeen.setImageResource(R.drawable.ic_seen)
        } else {
            holder.imgSeen.setImageResource(R.drawable.ic_delivered)
        }

        if (chatModel.messageReaction.equals("No")) {
            holder.imgReaction.visibility = View.GONE
        } else {
            holder.imgReaction.visibility = View.VISIBLE
            if (chatModel.messageReaction.equals("LIKE")) {
                holder.imgReaction.setImageResource(R.drawable.like)
            } else if (chatModel.messageReaction.equals("HEART")) {
                holder.imgReaction.setImageResource(R.drawable.heart)
            } else if (chatModel.messageReaction.equals("SMILE")) {
                holder.imgReaction.setImageResource(R.drawable.happy)
            } else if (chatModel.messageReaction.equals("SHOCK")) {
                holder.imgReaction.setImageResource(R.drawable.shock)
            } else if (chatModel.messageReaction.equals("SAD")) {
                holder.imgReaction.setImageResource(R.drawable.sad)
            } else if (chatModel.messageReaction.equals("ANGRY")) {
                holder.imgReaction.setImageResource(R.drawable.angry)
            }
        }

        if (!chatModel.fileUrl.isEmpty()) {
            val url = chatModel.fileUrl
            if (url.contains("jpg") || url.contains("png") || url.contains("jpeg")) {
                Picasso.get().load(chatModel.fileUrl).placeholder(R.drawable.ic_gallery)
                    .into(holder.imageViewChat)
                holder.cv.visibility = View.VISIBLE
                holder.fileLayout?.visibility = View.GONE
            } else {
                holder.cv.visibility = View.GONE
                holder.fileLayout?.visibility = View.VISIBLE
                holder.tvFileTextView?.visibility = View.VISIBLE
                setFileText(chatModel.fileUrl, holder)
            }
        } else {
            holder.fileLayout?.visibility = View.GONE
            holder.tvFileTextView?.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val transition = Pair.create<View?, String?>(holder.imageViewChat, "transition")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(context, transition)
            if (!chatModel.imageUrl.isEmpty()) {
                val intent = Intent(context, ViewImage::class.java)
                intent.putExtra("imageUrl", chatModel.imageUrl)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent, options.toBundle())
            } else if (!chatModel.fileUrl.isEmpty()) {
                val url = chatModel.fileUrl
                if (url.contains("jpg") || url.contains("png") || url.contains("jpeg")) {
                    val intent = Intent(context, ViewImage::class.java)
                    intent.putExtra("imageUrl", chatModel.fileUrl)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent, options.toBundle())
                } else {
                    val uri: Uri = Uri.parse(chatModel.fileUrl)
                    OpenFile(chatModel.fileUrl, uri, context)
                }
            }
        }
    }

    private fun setFileText(ext: String, holder: MyViewHolder) {
        var fileName = "File Attached"
        var drawable = R.drawable.ic_file
        if (ext.contains("pdf")) {
            fileName = "Pdf"
            drawable = R.drawable.ic_pdf
        } else if (ext.contains("docx")) {
            fileName = "Docx"
            drawable = R.drawable.ic_docx
        } else if (ext.contains("mp4")) {
            fileName = "Video"
            drawable = R.drawable.ic_play
        } else if (ext.contains("jpg")) {
            fileName = "Image"
            drawable = R.drawable.ic_gallery
        } else if (ext.contains("mp3")) {
            fileName = "Audio"
            drawable = R.drawable.ic_audio
        }

        holder.tvFileTextView?.setText(fileName + " File Attached")
        holder.imageFileType.setImageResource(drawable)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvMessage: TextView
        var tvName: TextView
        var tvTime: TextView
        var fileLayout: LinearLayout? = null
        var voiceLayout: LinearLayout? = null
        var tvFileTextView: TextView? = null
        var imageViewChat: ImageView
        var imageFileType: ImageView
        var imageVoiceType: ImageView
        var tvVoiceTextView: TextView
        var cv: CardView
        var imgSeen: ImageView
        var imgProfile: ImageView
        var imgReaction: ImageView

        init {
            tvMessage = itemView.findViewById(R.id.tvMessage)
            tvName = itemView.findViewById(R.id.tvName)
            tvTime = itemView.findViewById(R.id.tvTime)
            imageViewChat = itemView.findViewById(R.id.imageViewChat)
            imageVoiceType = itemView.findViewById(R.id.imageVoiceType)
            imageFileType = itemView.findViewById(R.id.imageFileType)
            cv = itemView.findViewById(R.id.cv)
            fileLayout = itemView.findViewById(R.id.fileLayout)
            voiceLayout = itemView.findViewById(R.id.voiceLayout)
            tvFileTextView = itemView.findViewById(R.id.tvFileTextView)
            imgSeen = itemView.findViewById(R.id.imgSeen)
            imgProfile = itemView.findViewById(R.id.imgProfile)
            imgReaction = itemView.findViewById(R.id.imgReaction)
            tvVoiceTextView = itemView.findViewById(R.id.tvVoiceTextView)
        }
    }

    override fun getItemViewType(position: Int): Int {
        fuser = FirebaseAuth.getInstance().currentUser
        return if (list.get(position).senderId.equals(fuser?.getUid())) {
            MSG_TYPE_RIGHT
        } else {
            MSG_TYPE_LEFT
        }
    }
}