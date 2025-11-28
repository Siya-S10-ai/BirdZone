package com.example.birdzone

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.birdzone.Model.Observation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ObservationsActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null

    private lateinit var editTextSpecies: EditText
    private lateinit var editTextLocation: EditText
    private lateinit var editTextNotes: EditText
    private lateinit var textViewDate: TextView
    private lateinit var textViewTime: TextView
    private lateinit var buttonSaveObservation: Button

    private lateinit var birdImageView: ImageView
    private lateinit var openCameraButton: Button
    private lateinit var imageUri: Uri
    private val CAMERA_REQUEST_CODE = 100

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) openCamera()
        else Toast.makeText(this, "Camera permission is required.", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_observations)

        editTextSpecies = findViewById(R.id.editTextSpecies)
        editTextLocation = findViewById(R.id.editTextLocation)
        editTextNotes = findViewById(R.id.editTextNotes)
        textViewDate = findViewById(R.id.editTextDate)
        textViewTime = findViewById(R.id.editTextTime)
        buttonSaveObservation = findViewById(R.id.buttonSave)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        birdImageView = findViewById(R.id.birdImageView)
        openCameraButton = findViewById(R.id.openCameraButton)

        openCameraButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) openCamera()
            else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (checkLocationPermission()) fetchLastLocation()
        displayCurrentDateTime()

        buttonSaveObservation.setOnClickListener {
            saveObservationLocally()
        }
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageFile = File.createTempFile("bird_photo", ".jpg", getExternalFilesDir(null))
        imageUri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", imageFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            birdImageView.setImageURI(imageUri)
            saveImageLocally()
        }
    }

    private fun saveImageLocally() {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
            val filename = "bird_photo_${System.currentTimeMillis()}.jpg"

            // Save image to a public directory for Gallery visibility
            val savedImageUri = MediaStore.Images.Media.insertImage(
                contentResolver,
                bitmap,
                filename,
                "Bird observation image"
            )

            // Show feedback to the user
            if (savedImageUri != null) {
                Toast.makeText(this, "Image saved to Gallery.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to save image to Gallery.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving image: $e", Toast.LENGTH_SHORT).show()
        }
    }


    private fun displayCurrentDateTime() {
        val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        textViewDate.text = currentDate
        textViewTime.text = currentTime
    }

    private fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    private fun fetchLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                currentLocation = it
                val latitude = it.latitude
                val longitude = it.longitude
                editTextLocation.setText("$latitude, $longitude")
            }
        }
    }

    private fun saveObservationLocally() {
        val species = editTextSpecies.text.toString()
        val date = textViewDate.text.toString()
        val time = textViewTime.text.toString()
        val location = editTextLocation.text.toString()
        val notes = editTextNotes.text.toString()

        if (species.isNotEmpty() && location.isNotEmpty()) {
            val observation = Observation(species, date, time, location, notes)

            Toast.makeText(this, "Observation saved locally.", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SavedObservationsActivity::class.java)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Species and Location fields are required.", Toast.LENGTH_SHORT).show()
        }
    }
}
