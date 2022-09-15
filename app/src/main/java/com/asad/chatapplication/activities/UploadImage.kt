package com.asad.chatapplication.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.asad.chatapplication.R
import com.asad.chatapplication.utils.StaticFunctions
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import java.util.concurrent.TimeUnit

class UploadImage : AppCompatActivity() {
    var mAuth: FirebaseAuth? = null
    var edtPhone: EditText? = null
    var edtOTP: EditText? = null
    var verifyOTPBtn: Button? = null
    var generateOTPBtn: Button? = null
    var verificationId: String? = null
    var mCallBack: OnVerificationStateChangedCallbacks? = null

    override
    fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)

        mAuth = FirebaseAuth.getInstance()
        edtPhone = findViewById<EditText>(R.id.idEdtPhoneNumber)
        edtOTP = findViewById<EditText>(R.id.idEdtOtp)
        verifyOTPBtn = findViewById<Button>(R.id.idBtnVerify)
        generateOTPBtn = findViewById<Button>(R.id.idBtnGetOtp)

        mCallBack = object : OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                verificationId = s
            }

            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
                if (code != null) {
                    edtOTP!!.setText(code)
                    verifyCode(code)
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("onVerificationFailed", "onVerificationFailed: " + e.message)
            }
        }

        generateOTPBtn?.setOnClickListener {
            val number: String = edtPhone?.text.toString().trim()
            if (TextUtils.isEmpty(number)) {
                StaticFunctions.ShowToast(this, "Please enter a number")
            } else {
                val phone = "+92" + number
                sendVerificationCode(phone)
            }
        }

        verifyOTPBtn?.setOnClickListener {
            val otp: String = edtOTP?.text.toString().trim()
            if (TextUtils.isEmpty(otp)) {
                StaticFunctions.ShowToast(this, "Please enter OTP")
            } else {
                verifyCode(otp)
            }
        }

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth!!.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val i = Intent(this@UploadImage, Register::class.java)
                    startActivity(i)
                    finish()
                } else {
                    StaticFunctions.ShowToast(this, task.exception?.localizedMessage.toString())
                }
            }
    }

    private fun sendVerificationCode(number: String) {
        val options: PhoneAuthOptions = PhoneAuthOptions.newBuilder(mAuth!!)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallBack!!)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithCredential(credential)
    }
}