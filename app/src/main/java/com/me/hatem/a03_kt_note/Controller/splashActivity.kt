package com.me.hatem.a03_kt_note.Controller

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.me.hatem.a03_kt_note.R

class splashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed(
                Runnable {
                    val toMain = Intent(this, MainActivity::class.java)
                    startActivity(toMain)
                },
                1000
        )
    }
}
