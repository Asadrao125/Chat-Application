package com.asad.chatapplication.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.transition.Fade
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.asad.chatapplication.R
import com.jsibbold.zoomage.ZoomageView
import com.squareup.picasso.Picasso

class ViewImage : AppCompatActivity() {
    var imageUrl: String? = null
    var imageView: ZoomageView? = null

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_image)

        getSupportActionBar()?.hide()
        imageUrl = intent.getStringExtra("imageUrl")
        imageView = findViewById(R.id.imageView)
        Picasso.get().load(imageUrl).placeholder(R.drawable.ic_gallery).into(imageView)
    }
}