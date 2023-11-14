package com.patigayon.activity5

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SecondActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }

    private val startCamera = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {

            val imageBitmap = result.data?.extras?.getParcelable<Bitmap>("data")
            Toast.makeText(this, "Picture taken successfully!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        findViewById<Button>(R.id.button_take_picture).setOnClickListener {
            handlePermission(Manifest.permission.CAMERA)
        }

        findViewById<Button>(R.id.button_get_location).setOnClickListener {
            handlePermission(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        findViewById<Button>(R.id.button_write_storage).setOnClickListener {
            handlePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun handlePermission(permission: String) {
        if (hasPermission(permission)) {
            performActionBasedOnPermission(permission)
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(permission)) {
                    explainPermissionBeforeRequesting(permission)
                }
                requestPermissions(arrayOf(permission), PERMISSION_REQUEST_CODE)
            } else {
                Toast.makeText(this, "OS Version does not support this feature", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun explainPermissionBeforeRequesting(permission: String) {

        val message = when (permission) {
            Manifest.permission.CAMERA -> "Camera access is required to take pictures."
            Manifest.permission.ACCESS_FINE_LOCATION -> "Location access is required to retrieve your current position."
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> "Write access is required to save data on your device."
            else -> "Additional permissions required."
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun performActionBasedOnPermission(permission: String) {
        when (permission) {
            Manifest.permission.CAMERA -> {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startCamera.launch(cameraIntent)
            }
            Manifest.permission.ACCESS_FINE_LOCATION -> {
                if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    try {
                        val locationClient = LocationServices.getFusedLocationProviderClient(this)
                        locationClient.lastLocation.addOnSuccessListener { location: Location? ->
                            location?.let {
                                Toast.makeText(this, "Lat: ${it.latitude}, Lon: ${it.longitude}", Toast.LENGTH_LONG).show()
                            } ?: Toast.makeText(this, "Location is not available", Toast.LENGTH_LONG).show()
                        }.addOnFailureListener {
                            Toast.makeText(this, "Failed to get location", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: SecurityException) {
                        Toast.makeText(this, "Location permission not granted", Toast.LENGTH_LONG).show()
                    }
                }
            }
            Manifest.permission.WRITE_EXTERNAL_STORAGE -> {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                    val externalStorageDir = Environment.getExternalStorageDirectory()
                    val file = File(externalStorageDir, "myFile.txt")
                    try {
                        val fileOutputStream = FileOutputStream(file)
                        fileOutputStream.use { stream ->
                            stream.write("Data to write to file".toByteArray())
                        }
                        Toast.makeText(this, "File written to storage", Toast.LENGTH_LONG).show()
                    } catch (e: IOException) {
                        Toast.makeText(this, "Error writing file: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "External storage not available", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                grantResults.forEachIndexed { index, result ->
                    val permission = permissions[index]
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        performActionBasedOnPermission(permission)
                    } else {
                        Toast.makeText(this, "Permission denied for: $permission", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
