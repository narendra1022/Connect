package com.example.chatapp.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapp.PostData
import com.example.chatapp.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SavedListsViewModel @Inject constructor():ViewModel() {

    private val firebase = Firebase.firestore

    val _data= MutableStateFlow<Resource<List<PostData>>>(Resource.unspecified())
    val data: StateFlow<Resource<List<PostData>>> = _data


    init {
        fetchdata()
    }

    private fun fetchdata() {

        viewModelScope.launch {
            _data.emit (Resource.Loading())
        }

        firebase.collection("saved")
            .whereEqualTo("uid",FirebaseAuth.getInstance().currentUser?.uid!!).get().addOnSuccessListener { result ->
                val ProductList=result.toObjects(PostData::class.java)
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