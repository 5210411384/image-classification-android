package com.akmalfaizy.objectdetection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class Register : AppCompatActivity() {

    private lateinit var TextLogin : TextView
    private lateinit var BtnRegist : Button

    private lateinit var EdNameRegist : EditText
    private lateinit var EdEmailRegist : EditText
    private lateinit var EdUsernameRegist : EditText
    private lateinit var EdPasswordRegist : EditText

    private lateinit var database :FirebaseDatabase
    private lateinit var reference : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        EdNameRegist = findViewById(R.id.EdNameRegist)
        EdEmailRegist = findViewById(R.id.EdEmailRegist)
        EdUsernameRegist = findViewById(R.id.EdUsernameRegist)
        EdPasswordRegist = findViewById(R.id.EdPasswordRegist)
        TextLogin = findViewById(R.id.text_login)
        BtnRegist = findViewById(R.id.btn_regist)

        BtnRegist.setOnClickListener {
            database = FirebaseDatabase.getInstance()
            reference = database.getReference("users")
            val name = EdNameRegist.text.toString()
            val email = EdEmailRegist.text.toString()
            val username = EdUsernameRegist.text.toString()
            val password = EdPasswordRegist.text.toString()
            val helperClass = HelperClass(name, email, username, password)
            reference.child(username).setValue(helperClass)
            Toast.makeText(this@Register, "You have signup successfully!", Toast.LENGTH_SHORT)
                .show()

            // Mengirim data pengguna ke AccountFragment
            val fragment = AccountFragment()
            val args = Bundle()
            args.putString("name", name)
            args.putString("username", username)
            fragment.arguments = args

            // Membuka AccountFragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit()
        }

        TextLogin.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}