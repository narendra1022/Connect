package com.example.chatapp.Util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hide(){
    val bn=(activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    bn.visibility= View.GONE
}
fun Fragment.show(){
    val bn=(activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottomNavigationView)
    bn.visibility= View.VISIBLE
}