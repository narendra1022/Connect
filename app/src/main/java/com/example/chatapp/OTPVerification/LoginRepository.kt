package com.example.chatapp.OTPVerification

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth

class KrishiTechsLoginRepository {
    fun sendOtpToMobileNumber(
        firebaseAuth: FirebaseAuth,
        givenMobileNumber: String,
        parentActivity: Activity
    ) =
        LoginMethodFirebase.sendOtpToMobileNumber(firebaseAuth, givenMobileNumber, parentActivity)

    fun getLoginStates() = LoginMethodFirebase.loginStates
    fun resetLoginStates() = LoginMethodFirebase.resetLoginStates()

    fun verifyOtp(enteredOtp: String) = LoginMethodFirebase.verifyUserWithOTP(enteredOtp)
}