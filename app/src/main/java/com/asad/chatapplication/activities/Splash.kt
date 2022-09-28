package com.asad.chatapplication.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.asad.chatapplication.R

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)

        supportActionBar?.hide()
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        val backgroundImage: ImageView = findViewById(R.id.splashScreenImage)
        val textView: TextView = findViewById(R.id.textView)

        val slideAnimation2 = AnimationUtils.loadAnimation(this, R.anim.top_to_bottom)
        backgroundImage.startAnimation(slideAnimation2)

        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.side_slide)
        textView.startAnimation(slideAnimation)

        Handler().postDelayed({
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
}