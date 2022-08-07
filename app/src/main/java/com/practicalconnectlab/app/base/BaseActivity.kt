package com.practicalconnectlab.app.base

import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity


abstract class BaseActivity : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            if (currentFocus != null) {
                inputMethodManager.hideSoftInputFromWindow(
                    currentFocus!!.windowToken, 0
                )
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputMethodManager.isActive) {
            if (currentFocus != null) {
                inputMethodManager.hideSoftInputFromWindow(
                    currentFocus!!.windowToken, 0
                )
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}