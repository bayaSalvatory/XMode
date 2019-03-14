package com.xmode.xmode.jobscheduler

import android.app.IntentService
import android.content.Intent
import com.xmode.xmode.location.LocationProvider

class XModeAlarmService : IntentService("Alarm") {
    override fun onHandleIntent(p0: Intent?) {
        LocationProvider.publish(applicationContext)
    }
}