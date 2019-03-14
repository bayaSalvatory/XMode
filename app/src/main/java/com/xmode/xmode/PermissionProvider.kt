package com.xmode.xmode

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat

object PermissionProvider {

    const val ALL_PERMISSIONS_RESULT = 1011

    private var permissionsToRequest: ArrayList<String>? = null
    private val permissions = ArrayList<String>()

    val permissionsRejected = ArrayList<String>()

    /**
     * Use this function to request permission from the system
     *
     * @param [permissions] list if permission you want request access for.
     */
    fun requestPermission(activity: Activity, permissions: Array<String>) {
        PermissionProvider.permissions.addAll(permissions)
        permissionsToRequest =
            permissionsToRequest(
                activity,
                PermissionProvider.permissions
            )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest!!.size > 0) {
                activity.requestPermissions(
                    permissionsToRequest!!.toArray(arrayOfNulls(permissionsToRequest!!.size)),
                    ALL_PERMISSIONS_RESULT
                )
            }
        }
    }

    /**
     * Use this to check if certain permission are granted already.
     *
     * @param [permissions] list of permissions to check it's access status.
     */
    fun checkHasPermission(context: Context, permissions: Array<String>): Boolean {
        permissions.forEach {
            if (!checkHasPermission(context, it)) {
                return false
            }
        }
        return true
    }

    private fun checkHasPermission(context: Context, permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun permissionsToRequest(context: Context, wantedPermissions: ArrayList<String>): ArrayList<String> {
        val result = ArrayList<String>()
        wantedPermissions.forEach {
            if (!checkHasPermission(context, it)) {
                result.add(it)
            }
        }
        return result
    }

    /**
     * This is a callback to notify permission access process is completed and ready to
     * obtain results.
     */
    fun onPermissionResult(context: Context) {
        for (perm in permissionsToRequest!!) {
            if (!checkHasPermission(context, perm)) {
                permissionsRejected.add(perm)
            }
        }
    }

    /**
     * Checks to see whether all the asked permission where granted.
     */
    fun isAllPermissionGranted(): Boolean {
        return permissionsRejected.size <= 0
    }

    /**
     * Use this to try asking again all permission for access that were rejected.
     */
    fun requestRejectedPerm(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission(
                activity,
                permissionsRejected.toArray(
                    arrayOfNulls(
                        permissionsRejected.size
                    )
                )
            )
        }
    }


}