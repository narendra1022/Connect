package com.example.chatapp.Fragments

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chatapp.MainActivity
import com.example.chatapp.OTPVerification.FirebaseLoginResponseStates
import com.example.chatapp.OTPVerification.LoginViewModel
import com.example.chatapp.R
import com.example.chatapp.UserData
import com.example.chatapp.Util.hide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OTPVerifyFragment(
    private val appContext: Context,
    private val enteredMobileNumber: String
) : Fragment() {
    private lateinit var parentView: View
    private lateinit var editTextOtp1: EditText
    private lateinit var editTextOtp2: EditText
    private lateinit var editTextOtp3: EditText
    private lateinit var editTextOtp4: EditText
    private lateinit var editTextOtp5: EditText
    private lateinit var editTextOtp6: EditText
    private lateinit var countDownTimer: CountDownTimer

    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mContext: Context

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_o_t_pverification, container, false)

        hide()



    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val textViewMobileNumber: TextView = view.findViewById(R.id.phoneTVSignupVerification)
        textViewMobileNumber.text = enteredMobileNumber
        initializeAllViewsAndVars()


    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (nextAnim != 0) {
            return AnimationUtils.loadAnimation(context, nextAnim)
        }
        return super.onCreateAnimation(transit, enter, nextAnim)
    }


    private fun initializeAllViewsAndVars() {
        // initializing all the Views and Variables
        parentView = requireView()
        editTextOtp1 = parentView.findViewById(R.id.optET1); editTextOtp2 =
            parentView.findViewById(R.id.optET2)
        editTextOtp3 = parentView.findViewById(R.id.optET3); editTextOtp4 =
            parentView.findViewById(R.id.optET4)
        editTextOtp5 = parentView.findViewById(R.id.optET5); editTextOtp6 =
            parentView.findViewById(R.id.optET6)
        val timerTV: TextView = parentView.findViewById(R.id.otpTimerTVSignupVerification)
        val buttonVerifyOtp: Button = parentView.findViewById(R.id.verify_otp_signup_user)
        val myLoginViewModel = LoginViewModel()
        firebaseDatabase = FirebaseDatabase.getInstance(); firebaseAuth = FirebaseAuth.getInstance()

        // setting up the OTP edit text and OTP Timer
        setUpOtpView()
        buttonVerifyOtp.setOnClickListener {
            // verifying the entered OTP by the user.
            val enteredOTP = editTextOtp1.text.toString() + editTextOtp2.text.toString() +
                    editTextOtp3.text.toString() + editTextOtp4.text.toString() +
                    editTextOtp5.text.toString() + editTextOtp6.text.toString()

            myLoginViewModel.verifyWithOtp(enteredOTP)
        }

        myLoginViewModel.signInStates().observe(this.viewLifecycleOwner) {
            if (it is FirebaseLoginResponseStates.LoginSuccessState) {
                countDownTimer.cancel()
                verifyCurrentUser()
            } else if (it is FirebaseLoginResponseStates.ErrorLoginState) {
                Snackbar.make(parentView, "Invalid OTP", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
        setUpOTPTimer(timerTV)
    }

    private fun verifyCurrentUser() {
        firebaseAuth.currentUser?.let { firebaseUser ->
            firebaseDatabase.reference.child("users").child(firebaseUser.uid).get()
                .addOnCompleteListener {
                    if (it.isSuccessful && it.result.value == null) {
                        addUserDataToFirebase()
                        addUserToLocalStorage(UserData(contactNo = enteredMobileNumber))
                        redirectToLocationAccessFragment()
                    } else {
                        val fetchedData = it.result.getValue(UserData::class.java) as UserData
                        addUserToLocalStorage(fetchedData)
                        redirectToLocationAccessFragment()
                    }
                }
        }
    }

    private fun redirectToLocationAccessFragment() {
        findNavController().navigate(R.id.action_OTPVerifyFragment_to_profileFragment)
    }

    private fun addUserToLocalStorage(userData: UserData) {
        val sharedPrefEditor = mContext.getSharedPreferences(
            resources.getString(R.string.shared_pref_key),
            Context.MODE_PRIVATE
        ).edit()

        sharedPrefEditor.putString("name", userData.name).apply()
        sharedPrefEditor.putString("contactNo", userData.contactNo).apply()
        sharedPrefEditor.putString("userLocation", userData.userLocation).apply()
        sharedPrefEditor.putString("profilePicUrl", userData.profilePicUrl).apply()
    }

    // function for creating user in database
    private fun addUserDataToFirebase() {

        val hashMap: HashMap<String, String> = HashMap()
        val userUid = firebaseAuth.currentUser!!.uid

        // creating New User Data to be uploaded at Firebase
        hashMap["name"] = ""
        hashMap["contactNo"] = enteredMobileNumber
        hashMap["userLocation"] = ""
        hashMap["profilePicUrl"] = ""
        hashMap["uid"] = FirebaseAuth.getInstance().uid!!
        hashMap["category"] = "profiles"
        hashMap["lastSeen"] = ""


        CoroutineScope(Dispatchers.IO).launch {
            // uploading New User Data on Firebase

            firebaseDatabase.reference.child("users")
                .child(userUid)
                .setValue(hashMap).addOnSuccessListener {
                    Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(mContext, it.toString(), Toast.LENGTH_SHORT).show()
                }
            FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().uid!!)
                .set(hashMap).addOnSuccessListener {
                    Toast.makeText(mContext, "", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(mContext, it.toString(), Toast.LENGTH_SHORT).show()
                }

            FirebaseFirestore.getInstance().collection("Users")
                .document(FirebaseAuth.getInstance().uid!!)
                .set(hashMap).addOnSuccessListener {
                    Toast.makeText(mContext, "success", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(mContext, it.toString(), Toast.LENGTH_SHORT).show()

                }

        }

    }

    // requesting focus for first OTP EditText and
    // assigning each OTP EditText a Text Watcher
    private fun setUpOtpView() {
        editTextOtp1.isEnabled = true
        editTextOtp1.addTextChangedListener(OtpTextWatcher(editTextOtp1))
        editTextOtp2.addTextChangedListener(OtpTextWatcher(editTextOtp2))
        editTextOtp3.addTextChangedListener(OtpTextWatcher(editTextOtp3))
        editTextOtp4.addTextChangedListener(OtpTextWatcher(editTextOtp4))
        editTextOtp5.addTextChangedListener(OtpTextWatcher(editTextOtp5))
        editTextOtp6.addTextChangedListener(OtpTextWatcher(editTextOtp6))
    }

    // A TextWatcher to auto jump over each EditText
    // while entering OTP by the user
    inner class OtpTextWatcher(private val givenView: View) : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // nothing to do here
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // nothing to do here
        }

        override fun afterTextChanged(p0: Editable?) {
            // checking if the current entered Text is Empty or not
            // in each EditText and requesting focus accordingly
            val enteredText = p0.toString()
            when (givenView.id) {
                R.id.optET1 -> if (enteredText.length == 1) editTextOtp2.requestFocus()
                R.id.optET2 -> if (enteredText.length == 1) editTextOtp3.requestFocus() else if (enteredText.isEmpty()) editTextOtp1.requestFocus()
                R.id.optET3 -> if (enteredText.length == 1) editTextOtp4.requestFocus() else if (enteredText.isEmpty()) editTextOtp2.requestFocus()
                R.id.optET4 -> if (enteredText.length == 1) editTextOtp5.requestFocus() else if (enteredText.isEmpty()) editTextOtp3.requestFocus()
                R.id.optET5 -> if (enteredText.length == 1) editTextOtp6.requestFocus() else if (enteredText.isEmpty()) editTextOtp4.requestFocus()
                R.id.optET6 -> if (enteredText.isEmpty()) editTextOtp5.requestFocus()
            }
        }
    }

    // Setting up the OTP timer
    private fun setUpOTPTimer(timerText: TextView) {
        countDownTimer = object : CountDownTimer(50000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text =
                    resources.getString(R.string.otp_countdownText, millisUntilFinished / 1000)
            }

            override fun onFinish() {
                timerText.text = requireContext().getString(R.string.otp_countdownFinishText)
                timerText.setTextColor(resources.getColor(R.color.purple_500))
            }
        }.start()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onDestroyView() {
        super.onDestroyView()

        countDownTimer.cancel()
    }


}