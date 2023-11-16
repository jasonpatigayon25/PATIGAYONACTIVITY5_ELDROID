package com.patigayon.activity5

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.patigayon.activity5.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding

    private val startCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap

            Toast.makeText(this, "Picture taken successfully!", Toast.LENGTH_LONG).show()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        when {
            isGranted -> {
                when (currentPermissionRequest) {
                    Manifest.permission.CAMERA -> takePicture()
                    Manifest.permission.ACCESS_FINE_LOCATION -> getLocation()
                    Manifest.permission.WRITE_EXTERNAL_STORAGE -> performStorageOperation()
                }
            }
            else -> Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private var currentPermissionRequest: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonTakePicture.setOnClickListener {
            currentPermissionRequest = Manifest.permission.CAMERA
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.buttonGetLocation.setOnClickListener {
            currentPermissionRequest = Manifest.permission.ACCESS_FINE_LOCATION
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        binding.buttonReadStorage.setOnClickListener {
            currentPermissionRequest = Manifest.permission.WRITE_EXTERNAL_STORAGE
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun takePicture() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startCamera.launch(cameraIntent)
    }

    private fun getLocation() {
        val locationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Location permission is not granted", Toast.LENGTH_SHORT).show()
            return
        }
        locationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                Toast.makeText(this, "Lat: ${it.latitude}, Lon: ${it.longitude}", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to get location", Toast.LENGTH_LONG).show()
        }
    }

    private fun performStorageOperation() {
        Toast.makeText(this, "Performing action with storage permission", Toast.LENGTH_SHORT).show()
    }

    private fun requestPermission(permission: String, rationale: String, requestCode: Int) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage(rationale)
                .setPositiveButton("OK") { _, _ ->
                    ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
                }
                .setNegativeButton("Cancel", null)
                .create()
                .show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            permissions.forEachIndexed { index, permission ->
                if (grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    when (permission) {
                        Manifest.permission.CAMERA -> takePicture()
                        Manifest.permission.ACCESS_FINE_LOCATION -> getLocation()
                        Manifest.permission.WRITE_EXTERNAL_STORAGE -> performStorageOperation()
                    }
                } else {
                    Toast.makeText(this, "Permission denied: $permission", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

}
