package com.practicalconnectlab.app.utils

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.practicalconnectlab.app.R
import java.util.*

class CustomDialog(context: Context?) : Dialog(
    context!!
) {
    fun showDialog() {
        show()
    }

    init {
        Objects.requireNonNull(window)?.decorView?.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        window!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    context!!,
                    R.color.blackTransparent
                )
            )
        )
        //        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window!!.setLayout(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.WRAP_CONTENT
        )
        window!!.setGravity(Gravity.BOTTOM)
        setCancelable(true)
        window!!.attributes.windowAnimations = R.style.DialogAnimation
    }
}