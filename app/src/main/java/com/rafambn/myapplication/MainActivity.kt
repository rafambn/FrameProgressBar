package com.rafambn.myapplication

import android.os.Bundle
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.rafambn.frameprogressbar.FrameProgressBar

class MainActivity : AppCompatActivity() {

    private lateinit var zoom: FrameProgressBar
    private lateinit var zoom2: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        zoom = findViewById(R.id.aaaa)
        zoom.setMarkersWidth(2)
    }
}