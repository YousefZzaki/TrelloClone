package com.yz.trelloclone.activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.yz.trelloclone.activities.BaseActivity.Companion.TAG
import com.yz.trelloclone.databinding.ActivitySplashScreenBinding
import com.yz.trelloclone.firebase.Firestore

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private var binding: ActivitySplashScreenBinding? = null

    private var currentUser = Firestore().getUserUID()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        Handler().postDelayed({

            if (currentUser.isEmpty())
                startActivity(Intent(this, IntroActivity::class.java))
            else
                startActivity(Intent(this, MainActivity::class.java))
            finish()
            Log.e(TAG, currentUser)
        }, 2500)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}