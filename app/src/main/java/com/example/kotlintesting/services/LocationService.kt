package com.example.kotlintesting.services

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.kotlintesting.Constants
import com.example.kotlintesting.MainActivity
import com.google.android.gms.location.*

class LocationService: Service()  {

	private lateinit var fusedLocationClient: FusedLocationProviderClient
	private lateinit var locationCallback: LocationCallback
	private var latitude = 0.0
	private var longitude = 0.0

	@RequiresApi(Build.VERSION_CODES.S)
	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		createNotificationChannel()
		showNotification()
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
		startFusedLocation()
		return super.onStartCommand(intent, flags, startId)
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private fun createNotificationChannel() {
		val serviceChannel = NotificationChannel(
			Constants.CHANNEL_ID,
			Constants.CHANNEL_NAME,
			NotificationManager.IMPORTANCE_DEFAULT
		)
		val manager = getSystemService(NotificationManager::class.java)
		manager.createNotificationChannel(serviceChannel)
		val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notificationManager.createNotificationChannel(serviceChannel)
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private fun showNotification () {
		var title = "Servicio de Localización"
		val notificationIntent = Intent(this, MainActivity::class.java)
		val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
		val notification = NotificationCompat
			.Builder(this, Constants.CHANNEL_ID)
			.setContentIntent(pendingIntent)
		  .setAutoCancel(true)
			.setContentText(title)
			.setContentTitle(title)
			.build()
		startForeground(Constants.LOCATION_NOTIFICATION_ID, notification)
	}

	private fun getCurrentLocation (): LocationCallback {
		locationCallback = object : LocationCallback() {
			override fun onLocationResult(locationResult: LocationResult) {
				for (location in locationResult.locations){
					latitude = location.latitude
					longitude = location.longitude
				}
			}
		}
		return locationCallback
	}

	@RequiresApi(Build.VERSION_CODES.S)
	@SuppressLint("MissingPermission")
	private fun startFusedLocation () {
		fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
		val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5L).build()
		val looper = Looper.getMainLooper()
		fusedLocationClient.requestLocationUpdates(locationRequest, getCurrentLocation(), looper)
	}

	override fun onBind(p0: Intent?): IBinder? {
		return null
	}

	override fun onDestroy() {
		fusedLocationClient.removeLocationUpdates(locationCallback)
	}

}