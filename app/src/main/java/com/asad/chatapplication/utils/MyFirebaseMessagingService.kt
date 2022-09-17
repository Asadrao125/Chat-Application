package com.asad.chatapplication.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Build
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.asad.chatapplication.R
import com.asad.chatapplication.activities.Home
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var params: Map<String, String>? = null

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        params = remoteMessage.data

        if (params!!.size != 0) {
            val title = params!!.get("title")
            val body = params!!.get("body")
            val senderId = params!!.get("senderId")
            val recieverId = params!!.get("recieverId")
            val profilePicUrl = params!!.get("profilePicUrl")
            val messageType = params!!.get("messageType")
            val fileUrl = params!!.get("fileUrl")

            if (messageType.equals("1")) {
                showNotificationSimple(title!!, body!!, profilePicUrl!!)
            } else if (messageType.equals("2")) {
                val imageUrl = params!!.get("imageUrl")
                showNotificationWithAttachment(title!!, body!!, profilePicUrl!!, imageUrl!!)
            } else if (messageType.equals("3")) {
                if (fileUrl!!.contains("jpg") || fileUrl.contains("png")
                    || fileUrl.contains("jpeg")
                ) {
                    showNotificationWithAttachment(title!!, body!!, profilePicUrl!!, fileUrl)
                } else {
                    showNotificationSimple(title!!, body!!, profilePicUrl!!)
                }
            }
        }
    }

    fun showNotificationWithAttachment(
        title: String, body: String, profilePicUrl: String, attachmentUrl: String
    ) {
        val collapsedView = RemoteViews(packageName, R.layout.notification_collapsed)
        val expandedView = RemoteViews(packageName, R.layout.notification_expanded)

        collapsedView.setTextViewText(R.id.tvCollapsedTitle, title)
        collapsedView.setTextViewText(R.id.tvCollapsedMessage, body)

        val clickIntent = Intent(this, Home::class.java)
        val clickPendingIntent = PendingIntent.getActivity(this, 0, clickIntent, 0)

        collapsedView.setImageViewBitmap(
            R.id.imgProfileCollapsed,
            getRoundedCornerBitmap(profilePicUrl)
        )

        expandedView.setImageViewBitmap(
            R.id.imgProfileExpanded,
            getRoundedCornerBitmap(profilePicUrl)
        )

        expandedView.setImageViewBitmap(
            R.id.imgExpanded,
            getRoundedCornerBitmap(attachmentUrl)
        )

        expandedView.setTextViewText(R.id.tvExpandedTitle, title)
        expandedView.setTextViewText(R.id.tvExpandedMessage, body)
        expandedView.setOnClickPendingIntent(R.id.imgExpanded, clickPendingIntent)

        val channelId = getString(R.string.default_notification_channel_id)
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_message)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(expandedView)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setContentIntent(clickPendingIntent)
                .setAutoCancel(true)
                .setNumber(3)

        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            builder.setChannelId(createNotificationChannel())
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random(100).nextInt(), builder.build())
    }

    fun showNotificationSimple(title: String, body: String, profilePicUrl: String) {
        val collapsedView = RemoteViews(packageName, R.layout.notification_collapsed)

        collapsedView.setTextViewText(R.id.tvCollapsedTitle, title)
        collapsedView.setTextViewText(R.id.tvCollapsedMessage, body)
        collapsedView.setImageViewResource(
            R.id.imgProfileCollapsed,
            R.drawable.ic_launcher_background
        )
        val clickIntent = Intent(this, Home::class.java)
        val clickPendingIntent = PendingIntent.getActivity(this, 0, clickIntent, 0)

        collapsedView.setImageViewBitmap(
            R.id.imgProfileCollapsed,
            getRoundedCornerBitmap(profilePicUrl)
        )

        val channelId = getString(R.string.default_notification_channel_id)
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_message)
                .setCustomContentView(collapsedView)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setContentIntent(clickPendingIntent)
                .setAutoCancel(true)
                .setNumber(5)

        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            builder.setChannelId(createNotificationChannel())
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(Random(100).nextInt(), builder.build())
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String {
        val channelId = "demo"
        val channelName = "My demo"
        val mChannel =
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_NONE)
        mChannel.importance = NotificationManager.IMPORTANCE_HIGH
            mChannel.setShowBadge(false)
        val mNotificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.createNotificationChannel(mChannel)
        return channelId
    }

    fun getRoundedCornerBitmap(imageUrl: String): Bitmap? {
        val bitmap: Bitmap = Glide.with(applicationContext)
            .asBitmap()
            .load(imageUrl)
            .error(R.drawable.ic_user)
            .submit(512, 512)
            .get()
        return StaticFunctions.GetRoundedCornerBitmap(bitmap)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}