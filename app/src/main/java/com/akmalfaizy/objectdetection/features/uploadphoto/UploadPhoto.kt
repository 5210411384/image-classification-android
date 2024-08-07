package com.akmalfaizy.objectdetection.features.uploadphoto

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.akmalfaizy.objectdetection.R
import com.akmalfaizy.objectdetection.features.takephoto.ResultTakePhoto
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler

class UploadPhoto : AppCompatActivity() {

    private lateinit var btnUploadPhoto : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_photo)

        btnUploadPhoto = findViewById(R.id.btnUploadPhoto)
        btnUploadPhoto.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // Start the Intent
            // Start the Intent
            startActivityForResult(galleryIntent, 1)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                // Mengambil gambar dari URI yang diberikan.
                val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                // Sekarang Anda punya `imageBitmap` yang merupakan gambar dari galeri.
                // Anda bisa melanjutkan untuk menganalisis gambar tersebut.
                analyzeImage(imageBitmap)
            }
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
                val intent = Intent(this, ResultUploadPhoto::class.java)
                intent.putExtra("data", searchQuery)
                startActivity(intent)
                Log.d(ContentValues.TAG, "Search Query NF $searchQuery")
            }
            .addOnFailureListener { e ->
                // Displaying an error message.
                Toast.makeText(this@UploadPhoto, "Fail to detect image..", Toast.LENGTH_SHORT).show()
            }
    }
}
