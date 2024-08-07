package com.akmalfaizy.objectdetection.features.takephoto

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.akmalfaizy.objectdetection.R
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler

@Suppress("DEPRECATION")
open class TakePhoto : AppCompatActivity() {
    private val REQUEST_IMAGE_CAPTURE = 1

    private lateinit var imgTakePhoto: ImageView
    private lateinit var btnTakePhoto: Button
    private lateinit var imageBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_photo)

        imgTakePhoto = findViewById(R.id.ImageTakePhoto)
        btnTakePhoto = findViewById(R.id.btnTakePhoto)

        btnTakePhoto.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // inside on activity result method we are
        // sending the image URI to ResultActivity.
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Mendapatkan foto yang diambil dalam bentuk Bitmap.
            val imageBitmap = data?.extras?.get("data") as Bitmap

            // Sekarang Anda punya `imageBitmap` yang merupakan foto yang diambil.
            // Anda bisa melanjutkan untuk menganalisis gambar tersebut.
            analyzeImage(imageBitmap)
        }
    }

    private fun analyzeImage(imageBitmap: Bitmap) {
        // Inside the label image method, we are calling a Firebase Vision image
        // and passing our image bitmap to it.
        val image: FirebaseVisionImage = FirebaseVisionImage.fromBitmap(imageBitmap)

        // On below line, we are creating a labeler for our image bitmap and
        // creating a variable for our Firebase Vision image labeler.
        val labeler: FirebaseVisionImageLabeler = FirebaseVision.getInstance().onDeviceImageLabeler

        // Calling a method to process an image and adding on success listener method to it.
        labeler.processImage(image)
            .addOnSuccessListener { firebaseVisionImageLabels ->
                val searchQuery = firebaseVisionImageLabels[0].text
                val intent = Intent(this, ResultTakePhoto::class.java)
                intent.putExtra("data", searchQuery)
                startActivity(intent)
                Log.d(TAG, "Search Query NF $searchQuery")
            }
            .addOnFailureListener { e ->
                // Displaying an error message.
                Toast.makeText(this@TakePhoto, "Fail to detect image..", Toast.LENGTH_SHORT).show()
            }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }
}
