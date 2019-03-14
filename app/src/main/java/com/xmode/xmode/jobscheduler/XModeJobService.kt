package com.xmode.xmode.jobscheduler

import android.app.job.JobParameters
import android.app.job.JobService
import android.os.Build
import android.support.annotation.RequiresApi
import com.xmode.xmode.location.LocationProvider

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class XModeJobService : JobService() {

    override fun onStartJob(p0: JobParameters?): Boolean {
        XModeJobsScheduler.scheduleRepeatingJob(applicationContext) // Reschedule.
        LocationProvider.publish(applicationContext)
        return true // Indicates separate thread process needs to occur.
    }

    override fun onStopJob(p0: JobParameters?): Boolean {
        return true // This flag restarts the job after being completed or fail.
    }


}