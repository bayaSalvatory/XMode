package com.xmode.xmode.jobscheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.*
import android.os.Build
import android.support.annotation.RequiresApi
import com.xmode.xmode.BuildConfig

object XModeJobsScheduler {

    private val xModeBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            scheduleRepeatingJob(p0!!)
        }
    }

    /**
     * Use this schedule periodic jobs callbacks.
     */
    fun scheduleRepeatingJob(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scheduleJob(context)
        } else {
            scheduleAlarm(context)
        }
        registerBootBroadcastReceiver(context, true)
    }

    private fun registerBootBroadcastReceiver(context: Context, isRegister: Boolean) {
        try {
            if (isRegister) {
                context.registerReceiver(xModeBroadcastReceiver, IntentFilter(Intent.ACTION_BOOT_COMPLETED))
            } else {
                context.unregisterReceiver(xModeBroadcastReceiver)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    private fun scheduleAlarm(context: Context) {
        val launchIntent = Intent(context, XModeAlarmService::class.java)
        val alarmIntent = PendingIntent.getService(context, 0, launchIntent, 0)
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        manager.setRepeating(
            AlarmManager.RTC,
            System.currentTimeMillis(),
            BuildConfig.INTERVAL,
            alarmIntent
        )
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun scheduleJob(context: Context) {
        val serviceComponent = ComponentName(context, XModeJobService::class.java)
        val jobInfoBuilder = JobInfo.Builder(1, serviceComponent)
        jobInfoBuilder.setMinimumLatency(BuildConfig.INTERVAL)  // Wait At-least.
        val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.schedule(jobInfoBuilder.build())
    }
}