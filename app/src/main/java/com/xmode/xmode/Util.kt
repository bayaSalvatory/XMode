package com.xmode.xmode

import android.content.Context
import android.widget.Toast

object Util {

    /**
     * Use this to disaplay a toast message.
     *
     * @param [message] string of the message you want to display as  a toast.
     */
    fun makeAToast(context: Context, message: String) {
        Toast.makeText(
            context,
            message,
            Toast.LENGTH_LONG
        ).show()
    }
}