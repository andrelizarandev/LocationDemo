package com.example.kotlintesting

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kotlintesting.databinding.ActivityMainBinding
import com.example.kotlintesting.services.LocationService

class MainActivity : AppCompatActivity() {

	private lateinit var binding:ActivityMainBinding
	private var isServiceRunning = false

	// Permissions
	private val coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
	private val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION

	@RequiresApi(Build.VERSION_CODES.O)
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)
		checkPermissions()
		binding.btnStartService.setOnClickListener{ startStopService() }
	}

	@RequiresApi(Build.VERSION_CODES.O)
	private fun startStopService() {
		val intent = Intent(this, LocationService::class.java)
		if (!isServiceRunning) startForegroundService(intent)
		else stopService(intent)
		isServiceRunning = !isServiceRunning
	}

	private fun checkPermissions () {
		val coarseLocationPermission = ContextCompat.checkSelfPermission(this, coarseLocation) != PackageManager.PERMISSION_GRANTED
		val fineLocationPermission = ContextCompat.checkSelfPermission(this, fineLocation) != PackageManager.PERMISSION_GRANTED
		if (coarseLocationPermission || fineLocationPermission) requirePermissions()
	}

	private fun requirePermissions () {
		ActivityCompat.requestPermissions(this, arrayOf(coarseLocation, fineLocation), MY_PERMISSIONS_REQUEST_LOCATION)
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED) return
		when (requestCode) {
			MY_PERMISSIONS_REQUEST_LOCATION -> {}
		}
	}

	companion object {
		private const val MY_PERMISSIONS_REQUEST_LOCATION = 99
	}

}

