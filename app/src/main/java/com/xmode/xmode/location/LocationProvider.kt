package com.xmode.xmode.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.Log
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationListener
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.xmode.xmode.PermissionProvider
import com.xmode.xmode.Util

object LocationProvider : LocationListener {

    private const val TAG = "XModeLocationProvider"
    private var googleApiClient: GoogleApiClient? = null
    private var locationRequest: LocationRequest? = null

    private const val PLAY_SERVICES_RESOLUTION_REQUEST = 9000
    private const val FASTEST_INTERVAL: Long = 5000 // = 5 seconds
    private val locationUpdateListeners = HashSet<OnLocationUpdateListener>()

    interface OnLocationUpdateListener {
        fun onUpdated(location: Location?)
    }

    /**
     * Use this to terminate all location update process.
     */
    fun stop() {
        // stop location updates
        if (googleApiClient != null && googleApiClient!!.isConnected) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this)
            googleApiClient!!.disconnect()
        }
    }

    /**
     * Use this to request a new single location update
     */
    fun requestLocationUpdate(context: Context) {
        if (!checkHasPermissionLocation(context)) {
            return
        }
        if (!checkPlayServices(context)) {
            Util.makeAToast(context, "You need to install Google Play Services to use the App properly")
            return
        }
        connectGoogleClient(context)
    }

    private fun checkHasPermissionLocation(context: Context): Boolean {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (!PermissionProvider.checkHasPermission(
                context,
                permissions
            )
        ) {
            PermissionProvider.requestPermission(context as Activity, permissions)
            Util.makeAToast(context, "You need to enable permissions to display location !")
            return false
        }
        return true
    }

    /**
     * Use this to register your module to receive location updates.
     */
    fun addLocationUpdateListener(locationUpdateListener: OnLocationUpdateListener) {
        locationUpdateListeners.add(locationUpdateListener)
    }

    private fun connectGoogleClient(context: Context) {
        googleApiClient = GoogleApiClient.Builder(context).addApi(LocationServices.API).addConnectionCallbacks(object :
            GoogleApiClient.ConnectionCallbacks {
            override fun onConnected(p0: Bundle?) {
                requestLocationUpdates()
            }

            override fun onConnectionSuspended(p0: Int) {
                Log.i(TAG, "onConnectionSuspended")
            }

        })
            .addOnConnectionFailedListener {
                Log.i(TAG, "Google client failed to connect")
            }.build()
        googleApiClient!!.connect()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        locationRequest = LocationRequest()
        locationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest!!.fastestInterval =
            FASTEST_INTERVAL
        LocationServices.FusedLocationApi.requestLocationUpdates(
            googleApiClient,
            locationRequest, this
        )
    }

    override fun onLocationChanged(p0: Location?) {
        notifyAllListeners(p0)
    }

    private fun checkPlayServices(context: Context): Boolean {
        val apiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = apiAvailability.isGooglePlayServicesAvailable(context)

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(
                    context as Activity, resultCode,
                    PLAY_SERVICES_RESOLUTION_REQUEST
                )
            }
            return false
        }
        return true
    }

    /**
     * Use this to push the location to all the subscribers.
     */
    fun publish(context: Context) {
        if (googleApiClient == null) {
            return
        }
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (!PermissionProvider.checkHasPermission(
                context,
                permissions
            )
        ) {
            PermissionProvider.requestPermission(context as Activity, permissions)
            Util.makeAToast(context, "You need to enable permissions to display location !")
            return
        }

        // Todo Save this in Shared preference or SQLite database
        val location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        notifyAllListeners(location)
    }

    private fun notifyAllListeners(location: Location?) {
        locationUpdateListeners.forEach {
            it.onUpdated(location)
        }
    }


}