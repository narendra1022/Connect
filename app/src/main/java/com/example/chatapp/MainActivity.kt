package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.service.controls.actions.FloatAction
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.chatapp.Fragments.SendOTPFragment
import com.example.chatapp.Fragments.HomeFragment
import com.example.chatapp.Fragments.MessagingFragment
import com.example.chatapp.Fragments.PostFragment
import com.example.chatapp.Fragments.PostListFragment
import com.example.chatapp.Fragments.ReelFragment
import com.example.chatapp.Fragments.SettingsFragment
import com.example.chatapp.Fragments.VideoOrPhotoFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val firebaseAuthInst: FirebaseAuth = FirebaseAuth.getInstance()
//        if (firebaseAuthInst.currentUser == null) {
//            supportFragmentManager.beginTransaction()
//                .add(R.id.main, SendOTPFragment()).commit()
//        } else {
//            supportFragmentManager.beginTransaction()
//                .add(R.id.main, HomeFragment()).commit()
//        }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView)
        windowInsetsController?.isAppearanceLightNavigationBars = true
        windowInsetsController?.isAppearanceLightStatusBars = true

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav) as NavHostFragment
        navController = navHostFragment.navController
        val b = findViewById<BottomNavigationView>(R.id.navbarr)
        NavigationUI.setupWithNavController(b, navController)

        b.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED

    }

    companion object {
        const val READ_PERMISSION = 101
    }

}