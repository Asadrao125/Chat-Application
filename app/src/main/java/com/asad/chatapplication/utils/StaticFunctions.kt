package com.asad.chatapplication.utils

import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.view.Display
import android.webkit.MimeTypeMap
import android.widget.Toast
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class StaticFunctions {
    companion object {
        fun OpenFile(url: String, uri: Uri, context: Context) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                if (url.contains(".doc") || url.toString().contains(".docx")) {
                    intent.setDataAndType(uri, "application/msword")
                } else if (url.contains(".pdf")) {
                    intent.setDataAndType(uri, "application/pdf")
                } else if (url.contains(".ppt") || url.toString().contains(".pptx")) {
                    intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
                } else if (url.contains(".xls") || url.toString().contains(".xlsx")) {
                    intent.setDataAndType(uri, "application/vnd.ms-excel")
                } else if (url.contains(".zip")) {
                    intent.setDataAndType(uri, "application/zip")
                } else if (url.contains(".rar")) {
                    intent.setDataAndType(uri, "application/x-rar-compressed")
                } else if (url.contains(".rtf")) {
                    intent.setDataAndType(uri, "application/rtf")
                } else if (url.contains(".wav") || url.toString().contains(".mp3")) {
                    intent.setDataAndType(uri, "audio/x-wav")
                } else if (url.contains(".gif")) {
                    intent.setDataAndType(uri, "image/gif")
                } else if (url.contains(".jpg") || url
                        .contains(".jpeg") || url.contains(".png")
                ) {
                    intent.setDataAndType(uri, "image/jpeg")
                } else if (url.contains(".txt")) {
                    intent.setDataAndType(uri, "text/plain")
                } else if (url.contains(".3gp") || url.contains(".mpg") ||
                    url.contains(".mpeg") || url
                        .contains(".mpe") || url.contains(".mp4") || url
                        .contains(".avi")
                ) {
                    intent.setDataAndType(uri, "video/*")
                } else {
                    intent.setDataAndType(uri, "*/*")
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(
                    context,
                    "No application found which can open the file",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        fun GetMimeType(uri: Uri, context: Context): String? {
            val extension: String?
            extension = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                val mime = MimeTypeMap.getSingleton()
                mime.getExtensionFromMimeType(context.getContentResolver().getType(uri))
            } else {
                MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(File(uri.path)).toString())
            }
            return extension
        }

        fun GetCurrentDateAndTime(): String {
            val df: DateFormat = SimpleDateFormat("EEE, dd MMM, hh:mm aa")
            val date: String = df.format(Calendar.getInstance().getTime())
            return date
        }

        fun GetCurrentTime(): String {
            val time: String = SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(Date())
            return time
        }

        fun ShowToast(context: Context, msg: String) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }

        fun GetRoundedCornerBitmap(bitmap: Bitmap): Bitmap? {
            val pixels = 100;
            val output = Bitmap.createBitmap(
                bitmap.width, bitmap
                    .height, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(output)
            val color = -0xbdbdbe
            val paint = Paint()
            val rect = Rect(0, 0, bitmap.width, bitmap.height)
            val rectF = RectF(rect)
            val roundPx = pixels.toFloat()
            paint.setAntiAlias(true)
            canvas.drawARGB(0, 0, 0, 0)
            paint.setColor(color)
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint)
            paint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))
            canvas.drawBitmap(bitmap, rect, rect, paint)
            return output
        }
    }
}