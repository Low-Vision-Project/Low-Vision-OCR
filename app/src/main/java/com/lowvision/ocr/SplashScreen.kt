package com.lowvision.ocr

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hide the action bar
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash_screen)

        // Animation For Splash Screen
        splash_screen_icon.alpha = 0f
        splash_screen_icon.animate().setDuration(1000).alpha(1f).withEndAction {
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}