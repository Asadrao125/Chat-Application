package com.asad.chatapplication.activities

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.asad.chatapplication.R

class UploadImage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)

        findViewById<Button>(R.id.btnShowNotification).setOnClickListener {

        }
    }
}