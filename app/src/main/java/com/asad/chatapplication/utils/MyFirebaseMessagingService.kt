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
import com.asad.chatapplication.activities.Chat
import com.asad.chatapplication.activities.Home
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.*

class MyFirebaseMessagingService : FirebaseMessagingService() {
    var params: Map<String, String>? = null
    var title: String = ""
    var body: String = ""
    var senderId: String = ""
    var recieverId: String = ""
    var profilePicUrl: String = ""
    var messageType: String = ""
    var fileUrl: String = ""
    var recieverName: String = ""
    var token: String = ""

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        params = remoteMessage.data

        if (params!!.size != 0) {
            title = params!!.get("title")!!
            body = params!!.get("body")!!
            senderId = params!!.get("senderId")!!
            recieverId = params!!.get("recieverId")!!
            profilePicUrl = params!!.get("profilePicUrl")!!
            messageType = params!!.get("messageType")!!
            recieverName = params!!.get("recieverName")!!
            token = params!!.get("token")!!

            if (messageType.equals("1")) {
                showNotificationSimple(title, body, profilePicUrl)
            } else if (messageType.equals("2")) {
                val imageUrl = params!!.get("imageUrl")
                showNotificationWithAttachment(title, body, profilePicUrl, imageUrl!!)
            } else if (messageType.equals("3")) {
                fileUrl = params!!.get("fileUrl")!!
                if (fileUrl.contains("jpg") || fileUrl.contains("png") || fileUrl.contains("jpeg")) {
                    showNotificationWithAttachment(title, body, profilePicUrl, fileUrl)
                } else {
                    showNotificationSimple(title, body, profilePicUrl)
                }
            } else if (messageType.equals("4")) {
                showNotificationSimple(title, body, profilePicUrl)
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

        val clickIntent = Intent(this, Chat::class.java)
        clickIntent.putExtra("senderId", senderId)
        clickIntent.putExtra("recieverId", recieverId)
        clickIntent.putExtra("username", recieverName)
        clickIntent.putExtra("fcmToken", token)
        clickIntent.putExtra("senderName", title)
        clickIntent.putExtra("profilePic", profilePicUrl)
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

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
        val clickIntent = Intent(this, Chat::class.java)
        clickIntent.putExtra("senderId", senderId)
        clickIntent.putExtra("recieverId", recieverId)
        clickIntent.putExtra("username", recieverName)
        clickIntent.putExtra("fcmToken", token)
        clickIntent.putExtra("senderName", title)
        clickIntent.putExtra("profilePic", profilePicUrl)
        clickIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

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