package com.anwesh.uiprojects.kotlinlinkedslidebuttonview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.linkedslidebuttonview.LinkedSlideButtonView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LinkedSlideButtonView.create(this)
    }
}
