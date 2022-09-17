package com.asad.chatapplication.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.asad.chatapplication.R
import com.asad.chatapplication.utils.StaticFunctions
import com.asad.chatapplication.utils.StaticFunctions.Companion.ShowToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
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

        /*mAuth = FirebaseAuth.getInstance()
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
        }*/

        val btn: Button = findViewById(R.id.btn)
        btn.setOnLongClickListener {
            val location = IntArray(2)
            btn.getLocationOnScreen(location)
            val p = Point()
            p.x = location[0]
            p.y = location[1]
            showStatusPopup(this@UploadImage, p)
            true
        }
    }

    @SuppressLint("NewApi")
    private fun showStatusPopup(context: Activity, p: Point) {
        val viewGroup = context.findViewById<View>(R.id.llSortChangePopup)
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout: View = layoutInflater.inflate(R.layout.popup_menu_layout, null)

        val changeStatusPopUp = PopupWindow(context)
        changeStatusPopUp.setContentView(layout)
        changeStatusPopUp.setWidth(LinearLayout.LayoutParams.MATCH_PARENT)
        changeStatusPopUp.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT)
        changeStatusPopUp.setFocusable(true)
        val OFFSET_X = 30
        val OFFSET_Y = -250
        changeStatusPopUp.setBackgroundDrawable(BitmapDrawable())
        changeStatusPopUp.showAtLocation(layout, Gravity.NO_GRAVITY, p.x + OFFSET_X, p.y + OFFSET_Y)

        val imglike: ImageView = layout.findViewById(R.id.imgLike)
        val imgHeart: ImageView = layout.findViewById(R.id.imgHeart)
        val imgSmile: ImageView = layout.findViewById(R.id.imgSmile)
        val imgShock: ImageView = layout.findViewById(R.id.imgShock)
        val imgSad: ImageView = layout.findViewById(R.id.imgSad)
        val imgAngry: ImageView = layout.findViewById(R.id.imgAngry)

        imglike.setOnClickListener {
            ShowToast(this, "Like")
            changeStatusPopUp.dismiss()
        }

        imgHeart.setOnClickListener {
            ShowToast(this, "Heart")
            changeStatusPopUp.dismiss()
        }

        imgSmile.setOnClickListener {
            ShowToast(this, "Smile")
            changeStatusPopUp.dismiss()
        }

        imgShock.setOnClickListener {
            ShowToast(this, "Shock")
            changeStatusPopUp.dismiss()
        }

        imgSad.setOnClickListener {
            ShowToast(this, "Sad")
            changeStatusPopUp.dismiss()
        }

        imgAngry.setOnClickListener {
            ShowToast(this, "Angry")
            changeStatusPopUp.dismiss()
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