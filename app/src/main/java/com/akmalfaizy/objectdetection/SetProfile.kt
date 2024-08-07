package com.akmalfaizy.objectdetection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SetProfile : AppCompatActivity() {

    private lateinit var CancelEdit : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_profile)

        CancelEdit = findViewById(R.id.btn_cancel_edit)
        CancelEdit.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}