package com.practicalconnectlab.app.ui

import android.os.Bundle
import com.practicalconnectlab.app.R
import com.practicalconnectlab.app.base.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}