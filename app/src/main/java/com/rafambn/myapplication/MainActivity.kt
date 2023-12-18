package com.rafambn.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rafambn.frameprogressbar.FrameProgressBar
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var zoom: FrameProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        zoom = findViewById(R.id.aaaa)
        zoom.setNumberFrames(100)

       MainScope().launch {
           zoom.setNumberFrames(50)
       }
    }
}