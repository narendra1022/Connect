package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.os.postDelayed
import com.google.android.material.bottomnavigation.BottomNavigationView

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


//        val splashImage = findViewById<ImageView>(R.id.splash_image)
//        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
//        splashImage.startAnimation(fadeInAnimation)
        startActivity(Intent(this, MainActivity::class.java))

//        Handler().postDelayed(
//            {
//                startActivity(Intent(this, MainActivity::class.java))
//                finish()
//            }, 3500
//        )
    }


}