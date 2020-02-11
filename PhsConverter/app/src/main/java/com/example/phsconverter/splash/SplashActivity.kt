package com.example.phsconverter.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import butterknife.BindView
import butterknife.ButterKnife
import com.example.phsconverter.R
import com.example.phsconverter.main.MainActivity

class SplashScreen : AppCompatActivity() {
    @BindView(R.id.tv_app_name)
    internal var tvAppName: TextView? = null

    @BindView(R.id.iv_logo)
    internal var ivLogo: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.exitTransition = null
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        ButterKnife.bind(this)
        Handler().postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, 1500)
    }
}