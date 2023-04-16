package com.example.chatapp.Fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import com.example.chatapp.OTPVerification.FirebaseLoginResponseStates
import com.example.chatapp.OTPVerification.LoginViewModel
import com.example.chatapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class SendOTPFragment : Fragment() {

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var parentView: View
    private lateinit var editTextSignUp: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_send_o_t_p, container, false)
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView?.visibility = View.GONE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViewsAndVariables()
    }

    private fun initializeViewsAndVariables() {
        firebaseAuth = FirebaseAuth.getInstance()
        parentView = requireView()

        editTextSignUp = parentView.findViewById(R.id.phone_num_et_signup)
        val buttonGetOtp: Button = parentView.findViewById(R.id.get_otp_signup_user)
        val progressIndicator: ProgressBar = parentView.findViewById(R.id.sendOtp_progressIndicator)


        val myViewModel = LoginViewModel()
        buttonGetOtp.setOnClickListener {
            // sending OTP to valid Mobile Numbers
            val enteredMobileNumber = editTextSignUp.text.toString()

            if (isValidMobileNumber(enteredMobileNumber)) {
                buttonGetOtp.text = ""; progressIndicator.visibility = View.VISIBLE
                myViewModel.sendOtpToMobileNumber(
                    firebaseAuth,
                    "+91$enteredMobileNumber",
                    requireActivity()
                )
            } else {
                Snackbar.make(parentView, "Invalid Mobile Number", Snackbar.LENGTH_LONG).show()
            }
        }

        // observing the Login States sent by the ViewModel
        myViewModel.signInStates().observe(this.viewLifecycleOwner) {
            if (it is FirebaseLoginResponseStates.CodeSentLoginState) {
                myViewModel.resetSignInStates()

                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.fragment_slide_in, R.anim.fragment_slide_out)
                    .replace(R.id.main, OTPVerifyFragment(
                        requireContext(),
                        "+91${editTextSignUp.text}"
                    ))
                    .commit()

            } else if (it is FirebaseLoginResponseStates.ErrorLoginState) {
                myViewModel.resetSignInStates()
                buttonGetOtp.text = "Get OTP"; progressIndicator.visibility = View.INVISIBLE
                Snackbar.make(
                    parentView,
                    "Error : ${it.errorMessage}",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun isValidMobileNumber(enteredMobileNumber: String): Boolean {
        return enteredMobileNumber.length == 10 && (enteredMobileNumber[0] in '6'..'9')
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView?.visibility = View.VISIBLE
    }


}