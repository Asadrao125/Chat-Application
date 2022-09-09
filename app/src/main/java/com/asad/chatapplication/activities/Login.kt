package com.asad.chatapplication.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asad.chatapplication.R
import com.asad.chatapplication.utils.Dialog_CustomProgress
import com.asad.chatapplication.utils.StaticFunctions.Companion.ShowToast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.rengwuxian.materialedittext.MaterialEditText

class Login : AppCompatActivity() {
    var etEmail: MaterialEditText? = null
    var etPassword: MaterialEditText? = null
    var btnLogin: Button? = null
    var layoutCreateAccount: LinearLayout? = null
    var mAuth: FirebaseAuth? = null
    var customProgressDialog: Dialog_CustomProgress? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        setTitle("Login")

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        layoutCreateAccount = findViewById(R.id.layoutCreateAccount)
        mAuth = FirebaseAuth.getInstance()
        customProgressDialog = Dialog_CustomProgress(this)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin?.setOnClickListener {
            val email = etEmail?.text.toString().trim()
            val password = etPassword?.text.toString().trim()
            if (!email.isEmpty() && !password.isEmpty()) {
                login(email, password)
            } else {
                Toast.makeText(this, "Please enter email or password", Toast.LENGTH_SHORT).show()
            }
        }

        layoutCreateAccount?.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
    }

    private fun login(email: String, password: String) {
        customProgressDialog?.show()
        mAuth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(OnCompleteListener {
                customProgressDialog?.hide()
                if (it.isSuccessful) {
                    val intent = Intent(this, Home::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    ShowToast(this, it.exception?.localizedMessage!!)
                }
            })
    }
}