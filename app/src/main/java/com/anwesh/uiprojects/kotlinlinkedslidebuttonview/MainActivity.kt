package com.anwesh.uiprojects.kotlinlinkedslidebuttonview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import com.anwesh.uiprojects.linkedslidebuttonview.LinkedSlideButtonView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view : LinkedSlideButtonView = LinkedSlideButtonView.create(this)
        view.addOnCompleteListener {
            Toast.makeText(this, "animation ${it} completed", Toast.LENGTH_SHORT).show()
        }
        fullScreen()
    }
}

fun MainActivity.fullScreen() {
    supportActionBar?.hide()
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}