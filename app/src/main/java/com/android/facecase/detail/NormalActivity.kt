package com.android.facecase.detail

import android.os.Bundle
import com.android.facecase.R

class NormalActivity : FaceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal)
    }
}