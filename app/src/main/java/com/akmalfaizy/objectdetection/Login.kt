package com.akmalfaizy.objectdetection

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Suppress("DEPRECATION")
class Login : AppCompatActivity() {

    private lateinit var TextRegist : TextView
    private lateinit var BtnLogin : Button

    private lateinit var EdEmail : EditText
    private lateinit var EdPassword : EditText
    private lateinit var auth : FirebaseAuth

    companion object{
        const val RC_SIGN_IN = 9001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        EdEmail = findViewById(R.id.EdEmailLogin)
        EdPassword = findViewById(R.id.EdPasswordLogin)
        auth = FirebaseAuth.getInstance()
        BtnLogin = findViewById(R.id.btn_login)
        TextRegist = findViewById(R.id.regist_text)


        TextRegist.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
        BtnLogin.setOnClickListener {
            if (!validateUsername() or !validatePassword()) {
            } else {
                checkUser()
            }
        }
    }
    fun validateUsername(): Boolean {
        val `val` = EdEmail!!.text.toString()
        return if (`val`.isEmpty()) {
            EdEmail!!.error = "Username cannot be empty"
            false
        } else {
            EdEmail!!.error = null
            true
        }
    }

    fun validatePassword(): Boolean {
        val `val` = EdPassword!!.text.toString()
        return if (`val`.isEmpty()) {
            EdPassword!!.error = "Password cannot be empty"
            false
        } else {
            EdPassword!!.error = null
            true
        }
    }
    fun checkUser() {
        val userUsername = EdEmail!!.text.toString().trim { it <= ' ' }
        val userPassword = EdPassword!!.text.toString().trim { it <= ' ' }
        val reference = FirebaseDatabase.getInstance().getReference("users")
        val checkUserDatabase = reference.orderByChild("username").equalTo(userUsername)
        checkUserDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    EdEmail!!.error = null
                    val passwordFromDB = snapshot.child(userUsername).child("password").getValue(
                        String::class.java
                    )
                    if (passwordFromDB == userPassword) {
                        EdEmail!!.error = null
                        val nameFromDB = snapshot.child(userUsername).child("name").getValue(
                            String::class.java
                        )
                        val emailFromDB = snapshot.child(userUsername).child("email").getValue(
                            String::class.java
                        )
                        val usernameFromDB =
                            snapshot.child(userUsername).child("username").getValue(
                                String::class.java
                            )
                        val intent = Intent(this@Login, MainActivity::class.java)
                        intent.putExtra("name", nameFromDB)
                        intent.putExtra("email", emailFromDB)
                        intent.putExtra("username", usernameFromDB)
                        intent.putExtra("password", passwordFromDB)
                        startActivity(intent)
                    } else {
                        EdEmail!!.error = "Invalid Credentials"
                        EdPassword!!.requestFocus()
                    }
                } else {
                    EdEmail!!.error = "User does not exist"
                    EdEmail!!.requestFocus()
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}