package com.xmode.xmode.main

import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.xmode.xmode.PermissionProvider
import com.xmode.xmode.R
import com.xmode.xmode.Util
import com.xmode.xmode.jobscheduler.XModeJobsScheduler
import com.xmode.xmode.location.LocationProvider


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setClickListeners()
        setLocationUpdateListener()
    }

    private fun setClickListeners() {
        val textView = findViewById<TextView>(R.id.id_text_view)
        val ss = SpannableString(textView.text)

        val span1 = object : ClickableSpan() {
            override fun onClick(p0: View) {
                Toast.makeText(baseContext, "Task 1 completed", Toast.LENGTH_LONG).show()
            }
        }

        val span2 = object : ClickableSpan() {
            override fun onClick(p0: View) {
                LocationProvider.requestLocationUpdate(this@MainActivity)
            }
        }

        ss.setSpan(span1, 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(span2, 6, 12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = ss
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setLocationUpdateListener() {
        LocationProvider.addLocationUpdateListener(object : LocationProvider.OnLocationUpdateListener {
            override fun onUpdated(location: Location?) {
                runOnUiThread {
                    Util.makeAToast(baseContext, "Your Location is, $location")
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        LocationProvider.requestLocationUpdate(this)
        XModeJobsScheduler.scheduleRepeatingJob(applicationContext)
    }

    override fun onPause() {
        super.onPause()
        // stop location updates
        LocationProvider.stop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PermissionProvider.ALL_PERMISSIONS_RESULT -> {
                PermissionProvider.onPermissionResult(this)
                val permissionsRejected = PermissionProvider.permissionsRejected
                if (PermissionProvider.isAllPermissionGranted()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected[0])) {
                            showPermissionDialog()
                            return
                        }
                    }
                } else {
                    LocationProvider.requestLocationUpdate(this)
                }
            }
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.dialog_msg_permission))
            .setPositiveButton(
                getString(R.string.ok)
            ) { _, _ ->
                PermissionProvider.requestRejectedPerm(this)
            }.setNegativeButton(getString(R.string.cancel), null).create().show()
    }

}
