package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.actions.FloatAction
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.chatapp.Fragments.SendOTPFragment
import com.example.chatapp.Fragments.HomeFragment
import com.example.chatapp.Fragments.MessagingFragment
import com.example.chatapp.Fragments.PostFragment
import com.example.chatapp.Fragments.SettingsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        inflateFragment()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val post = findViewById<FloatingActionButton>(R.id.fab)
        post.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .addToBackStack("change")
                .add(R.id.main, PostFragment()).commit()

        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    supportFragmentManager.beginTransaction()
                        .add(R.id.main, HomeFragment()).commit()
                    true
                }

                R.id.navigation_message -> {
                    // Handle click on Dashboard button
                    supportFragmentManager.beginTransaction()
                        .add(R.id.main, MessagingFragment()).commit()
                    true
                }

                R.id.navigation_profile -> {
                    // Handle click on Notifications button
                    supportFragmentManager.beginTransaction()
                        .add(R.id.main, SettingsFragment()).commit()
                    true
                }

                else -> false
            }
        }


    }

    private fun inflateFragment() {
        val firebaseAuthInst: FirebaseAuth = FirebaseAuth.getInstance()
        if (firebaseAuthInst.currentUser == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main, SendOTPFragment()).commit()
        } else {
            supportFragmentManager.beginTransaction()
                .add(R.id.main, HomeFragment()).commit()
        }
    }

    companion object {
        const val READ_PERMISSION = 101
    }

    override fun onDestroy() {
        super.onDestroy()

        val post = findViewById<FloatingActionButton>(R.id.fab)

    }

}