package com.example.chatappkotlin.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chatappkotlin.R
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.android.synthetic.main.fragment_get_user_number.view.*
import java.util.concurrent.TimeUnit

class GetUserNumber : Fragment() {

    private var number: String? = null
    private var code: String? = null
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_get_user_number, container, false)

        view.btnGenerateOTP.setOnClickListener {
            if (checkNumber()) {
                val phoneNumber = view.countryCodePicker.selectedCountryCodeWithPlus + number
                sendCode(phoneNumber)
            }
        }

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                Log.e("sms", credential.smsCode.toString())
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                    Log.e("xx", e.message.toString())
                } else if (e is FirebaseTooManyRequestsException) {
                    Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                    Log.e("xxx", e.message.toString())
                } else {
                    Toast.makeText(context, "" + e.message, Toast.LENGTH_SHORT).show()
                    Log.e("x", e.message.toString())
                }
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                code = p0
                activity!!.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_container, VerifyNumber.newInstance(code!!))
                    .commit()
            }
        }
        return view
    }

    private fun sendCode(phoneNumber: String) {
        PhoneAuthProvider.getInstance()
            .verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, activity!!, callbacks)
    }

    private fun checkNumber(): Boolean {
        number = view!!.edtNumber.text.toString().trim()

        if (number!!.isEmpty()) {
            view!!.edtNumber.error = "Field is required"
            return false
        } else if (number!!.length < 10) {
            view!!.edtNumber.error = "Number should be 10 in length"
            return false
        } else return true
    }

}