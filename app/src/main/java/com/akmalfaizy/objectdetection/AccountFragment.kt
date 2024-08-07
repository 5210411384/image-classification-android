package com.akmalfaizy.objectdetection

import android.app.PendingIntent.OnFinished
import android.app.PendingIntent.readPendingIntentOrNullFromParcel
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import org.w3c.dom.Text
import kotlin.system.exitProcess

class AccountFragment : Fragment() {

    private lateinit var SettingProfil : Button
    private lateinit var Logout : Button
    private lateinit var tvName : TextView
    private lateinit var tvUsername : TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        auth = FirebaseAuth.getInstance()
        SettingProfil = view.findViewById(R.id.btn_set_profil)
        Logout = view.findViewById(R.id.btn_logout)
        tvName = view.findViewById(R.id.profileName)
        tvUsername = view.findViewById(R.id.profileUsername)

        val name = arguments?.getString("name")
        val username = arguments?.getString("username")
        Log.d("AccountFragment", "Name: $name, Username: $username")

        tvName.text
        tvUsername.text

        SettingProfil.setOnClickListener {
            activity?.let {
                val keIntent = Intent (it, SetProfile::class.java)
                it.startActivity(keIntent)
            }
        }
        Logout.setOnClickListener {
            auth.signOut()
            activity?.let {
                val keIntent = Intent(it, ChooseLogin::class.java)
                it.startActivity(keIntent)
                it.finish()
            }
        }
    return view
    }
}

