package com.akmalfaizy.objectdetection

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.akmalfaizy.objectdetection.features.imagedetection.ImageDetection
import com.akmalfaizy.objectdetection.features.realtimedetection.RealtimeDetection
import com.akmalfaizy.objectdetection.features.takephoto.TakePhoto
import com.akmalfaizy.objectdetection.features.uploadphoto.UploadPhoto
import com.google.firebase.auth.FirebaseAuth
import java.util.Calendar
import java.util.Calendar.HOUR_OF_DAY

class DashboardFragment : Fragment() {

    private lateinit var auth : FirebaseAuth
    
    private lateinit var greetingsSay : TextView

    private lateinit var realtimeDetection : LinearLayout
    private lateinit var imageDetection : LinearLayout
    private lateinit var takePhoto : LinearLayout
    private lateinit var uploadPhoto : LinearLayout

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        realtimeDetection = view.findViewById(R.id.RealtimeDetection)
        imageDetection = view.findViewById(R.id.ImageDetection)
        takePhoto = view.findViewById(R.id.TakePhoto)
        uploadPhoto = view.findViewById(R.id.UploadPhoto)

        auth = FirebaseAuth.getInstance()

        greetingsSay = view.findViewById(R.id.greetings)
        val kalender = Calendar.getInstance()

        when (kalender.get(HOUR_OF_DAY)) {
            in 12..16 -> {
                greetingsSay.text = "Good Afternoon"
            }
            in 17..20 -> {
                greetingsSay.text = "Good Evening"
            }
            in 21..23 -> {
                greetingsSay.text = "Good Night"
            }
            else -> {
                greetingsSay.text = "Good Morning"
            }
        }
        realtimeDetection.setOnClickListener {
            activity?.let {
                val keIntent = Intent (it, RealtimeDetection::class.java)
                it.startActivity(keIntent)
            }
        }
        imageDetection.setOnClickListener {
            activity?.let {
                val keIntent = Intent (it, ImageDetection::class.java)
                it.startActivity(keIntent)
            }
        }
        takePhoto.setOnClickListener {
            activity?.let {
                val keIntent = Intent (it, TakePhoto::class.java)
                it.startActivity(keIntent)
            }
        }
        uploadPhoto.setOnClickListener {
        activity?.let {
            val keIntent = Intent (it, UploadPhoto::class.java)
            it.startActivity(keIntent)
        }
    }
    return view
    }
}

