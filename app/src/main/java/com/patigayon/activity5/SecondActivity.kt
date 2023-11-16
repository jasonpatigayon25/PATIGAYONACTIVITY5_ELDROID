package com.patigayon.activity5

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationServices
import com.patigayon.activity5.databinding.ActivitySecondBinding
import androidx.core.content.ContextCompat

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding

    private val startCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            // val imageBitmap = result.data?.extras?.get("data") as Bitmap
            // TODO: Something
            Toast.makeText(this, "Picture taken successfully!", Toast.LENGTH_LONG).show()
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        when {
            isGranted -> performActionBasedOnPermission(currentPermissionRequest)
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

    private fun performActionBasedOnPermission(permission: String?) {
        when (permission) {
            Manifest.permission.CAMERA -> takePicture()
            Manifest.permission.ACCESS_FINE_LOCATION -> getLocation()
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> performStorageOperation()
            else -> Toast.makeText(this, "No action defined for this permission", Toast.LENGTH_SHORT).show()
        }
    }

    private fun takePicture() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startCamera.launch(cameraIntent)
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener { location ->
                location?.let {
                    Toast.makeText(this, "Lat: ${it.latitude}, Lon: ${it.longitude}", Toast.LENGTH_LONG).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "Location permission is not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performStorageOperation() {
        Toast.makeText(this, "Performing action with storage permission", Toast.LENGTH_SHORT).show()
    }
}
