package com.example.chatapp.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.Resource
import com.example.chatapp.UserData
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class profileViewmodel @Inject constructor() :ViewModel() {

    private val firebase = Firebase.firestore

    val _data= MutableStateFlow<Resource<List<UserData>>>(Resource.unspecified())
    val data: StateFlow<Resource<List<UserData>>> = _data


    init {
        fetchdata()
    }

    private fun fetchdata() {

        viewModelScope.launch {
            _data.emit (Resource.Loading())
        }

        firebase.collection("Users")
            .whereEqualTo("category","profiles").get().addOnSuccessListener { result ->
                val ProductList=result.toObjects(UserData::class.java)

                viewModelScope.launch {
                    _data.emit(Resource.Success(ProductList))
                }

            }

            .addOnFailureListener {
                viewModelScope.launch {
                    _data.emit(Resource.Error(it.message.toString()))
                }
            }
    }




}