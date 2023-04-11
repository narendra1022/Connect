package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.chatapp.Fragments.HomeFragment
import com.example.chatapp.Fragments.SendOTPFragment
import com.example.chatapp.Fragments.messageFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val navHostFragment=supportFragmentManager.findFragmentById(R.id.nav) as NavHostFragment
//        navController=navHostFragment.navController


        inflateFragment()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

    }

    private fun inflateFragment() {
        val firebaseAuthInst: FirebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuthInst.currentUser == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main, SendOTPFragment()).commit()
        } else {
            supportFragmentManager.beginTransaction()
                .add(R.id.main, messageFragment()).commit()
        }
    }

    companion object {
        const val READ_PERMISSION = 101
    }
}