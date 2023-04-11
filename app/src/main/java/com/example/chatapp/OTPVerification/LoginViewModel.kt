package com.example.chatapp.OTPVerification

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    private val myRepository = KrishiTechsLoginRepository()

    fun sendOtpToMobileNumber(
        firebaseAuth: FirebaseAuth,
        givenMobileNumber: String,
        parentActivity: Activity
    ) = myRepository.sendOtpToMobileNumber(firebaseAuth, givenMobileNumber, parentActivity)

    fun signInStates() = myRepository.getLoginStates()
    fun resetSignInStates() = myRepository.resetLoginStates()
    fun verifyWithOtp(enteredOtp: String) = myRepository.verifyOtp(enteredOtp)
}