package com.example.chatapp.OTPVerification

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginMethodFirebase {

    companion object {
        var loginStates: MutableLiveData<FirebaseLoginResponseStates> = MutableLiveData()
        private lateinit var thisToken: PhoneAuthProvider.ForceResendingToken
        private lateinit var thisVerificationId: String

        fun resetLoginStates() {
            loginStates = MutableLiveData()
        }

        fun sendOtpToMobileNumber(
            firebaseAuth: FirebaseAuth,
            givenMobileNumber: String,
            parentActivity: Activity
        ) {
            loginStates.postValue(FirebaseLoginResponseStates.ProcessingState())

            val options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(givenMobileNumber)       // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(parentActivity)                 // Activity (for callback binding)
                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }

        private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                // this function is left empty
            }

            override fun onVerificationFailed(e: FirebaseException) {
                loginStates.postValue(FirebaseLoginResponseStates.ErrorLoginState(e.message.toString()))
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                thisVerificationId = verificationId
                thisToken = token
                loginStates.postValue(FirebaseLoginResponseStates.CodeSentLoginState())
            }
        }

        fun verifyUserWithOTP(enteredOTP: String) {
            val credentials = PhoneAuthProvider.getCredential(thisVerificationId, enteredOTP)

            FirebaseAuth.getInstance().signInWithCredential(credentials)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        loginStates.postValue(FirebaseLoginResponseStates.LoginSuccessState())
                    } else {
                        loginStates.postValue(FirebaseLoginResponseStates.ErrorLoginState(task.exception?.message.toString()))
                    }
                }
        }
    }
}