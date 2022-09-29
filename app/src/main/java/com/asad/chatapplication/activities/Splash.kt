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
import androidx.appcompat.app.AppCompatDelegate
import com.asad.chatapplication.R
import com.asad.chatapplication.utils.DataProccessor

class Splash : AppCompatActivity() {
    var dataProcessor: DataProccessor? = null

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

        dataProcessor = DataProccessor(this)

        val slideAnimation2 = AnimationUtils.loadAnimation(this, R.anim.top_to_bottom)
        backgroundImage.startAnimation(slideAnimation2)

        val slideAnimation = AnimationUtils.loadAnimation(this, R.anim.side_slide)
        textView.startAnimation(slideAnimation)

        if (dataProcessor?.getStr("Theme").equals("Dark")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else if (dataProcessor?.getStr("Theme").equals("Light")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        Handler().postDelayed({
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }
}