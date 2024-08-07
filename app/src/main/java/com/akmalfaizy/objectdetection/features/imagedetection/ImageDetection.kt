package com.akmalfaizy.objectdetection.features.imagedetection

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.akmalfaizy.objectdetection.R
import com.akmalfaizy.objectdetection.ml.SsdMobilenetV11Metadata1
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ImageDetection : AppCompatActivity() {

    private lateinit var saveImgBtn : Button

    private val paint = Paint()
    private lateinit var btnSelectImage: Button
    private lateinit var imageView: ImageView
    private lateinit var bitmap: Bitmap
    private var colors = listOf<Int>(
        Color.BLUE, Color.GREEN, Color.RED, Color.CYAN, Color.GRAY, Color.BLACK,
        Color.DKGRAY, Color.MAGENTA, Color.YELLOW, Color.RED)
    private lateinit var labels: List<String>
    private lateinit var model: SsdMobilenetV11Metadata1
    private val imageProcessor: ImageProcessor = ImageProcessor.Builder().add(ResizeOp(300, 300, ResizeOp.ResizeMethod.BILINEAR)).build()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_detection)

        labels = FileUtil.loadLabels(this, "labels.txt")
        model = SsdMobilenetV11Metadata1.newInstance(this)

        paint.color = Color.BLUE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 5.0f
//        paint.textSize = paint.textSize*3

        Log.d("labels", labels.toString())

        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"

        btnSelectImage = findViewById(R.id.btnSelectImage)
        imageView = findViewById(R.id.imaegView)
        saveImgBtn = findViewById(R.id.btnSaveImage)

        btnSelectImage.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            // Start the Intent
            // Start the Intent
            startActivityForResult(galleryIntent, 1)
        }
        saveImgBtn.setOnClickListener {
            if (::bitmap.isInitialized) {
                saveImageToGallery()
            }
        }
    }

    fun getPath(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = managedQuery(uri, projection, null, null, null)
        val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "IMAGE PATH: ${data?.data?.path}" )
        if (data == null || data?.data == null || data?.data?.path == null) return
        if(requestCode == 1){
            val uri = data!!.data!!

            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)

            val photoPath = getPath(uri)
            val ei = ExifInterface(photoPath!!)
            val orientation: Int = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
            )

            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> bitmap = rotateImage(bitmap, 90F)
                ExifInterface.ORIENTATION_ROTATE_180 -> bitmap = rotateImage(bitmap, 180F)
                ExifInterface.ORIENTATION_ROTATE_270 -> bitmap = rotateImage(bitmap, 270F)
                ExifInterface.ORIENTATION_NORMAL -> bitmap = bitmap
            }

            get_predictions()

            imageView.visibility = View.VISIBLE
            saveImgBtn.visibility = View.VISIBLE
        }
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        model.close()
    }

    fun get_predictions(){

        var image = TensorImage.fromBitmap(bitmap)
        image = imageProcessor.process(image)
        val outputs = model.process(image)
        val locations = outputs.locationsAsTensorBuffer.floatArray
        val classes = outputs.classesAsTensorBuffer.floatArray
        val scores = outputs.scoresAsTensorBuffer.floatArray
        val numberOfDetections = outputs.numberOfDetectionsAsTensorBuffer.floatArray

        val mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutable)
        val h = mutable.height
        val w = mutable.width

        paint.textSize = h/15f
        paint.strokeWidth = h/85f
        scores.forEachIndexed { index, fl ->
            if(fl > 0.5){
                var x = index
                x *= 4
                paint.color = colors[index]
                paint.style = Paint.Style.STROKE
                canvas.drawRect(RectF(locations[x + 1] *w, locations[x] *h, locations[x+3] *w, locations[x + 2] *h), paint)
                paint.style = Paint.Style.FILL
                canvas.drawText(labels[classes[index].toInt()] + " " + fl.toString(), locations[x+1] *w, locations[x] *h, paint)
            }
        }

        imageView.setImageBitmap(mutable)
    }
    private fun saveImageToGallery() {
        val screenshotBitmap = Bitmap.createBitmap(
            imageView.width,
            imageView.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(screenshotBitmap)
        imageView.draw(canvas)

        val filename = "Screenshot_${
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                Date()
            )}.jpg"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val imageFile = File(storageDir, filename)

        val fos: OutputStream = FileOutputStream(imageFile)
        screenshotBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()

        // Menambahkan gambar ke MediaStore
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val resolver = contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        // Menyalin data gambar ke MediaStore
        val inputStream = FileInputStream(imageFile)
        val outputStream = resolver.openOutputStream(imageUri!!)
        inputStream.copyTo(outputStream!!)
        inputStream.close()
        outputStream!!.close()

        // Memberi tahu MediaStore bahwa operasi penambahan sudah selesai
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUri, contentValues, null, null)
        }

        // Memberitahu media scanner untuk memindai file yang baru ditambahkan
        sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri))
    }

}
