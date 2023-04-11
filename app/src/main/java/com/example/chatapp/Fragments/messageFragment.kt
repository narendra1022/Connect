package com.example.chatapp.Fragments

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.chatapp.Adapters.postAdapter
import com.example.chatapp.PostData
import com.example.chatapp.R
import com.example.chatapp.Resource
import com.example.chatapp.ViewModels.postViewModal
import com.example.chatapp.databinding.FragmentMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class messageFragment : Fragment() {

    private lateinit var spad: postAdapter
    private lateinit var binding: FragmentMessageBinding
    private val viewmodel by viewModels<postViewModal>()
    private val firebase = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMessageBinding.inflate(layoutInflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val _data= MutableStateFlow<Resource<List<PostData>>>(Resource.unspecified())
        val data: StateFlow<Resource<List<PostData>>> = _data

        SetupSpecialProductRv()

        binding.msgs.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.your_frame_layo,HomeFragment()).addToBackStack(null).commit()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            postAdapter().notifyDataSetChanged()
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.pic.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.your_frame_layo,PostFragment()).addToBackStack(null).commit()
        }

        spad.onItemClick = {

        }

        lifecycleScope.launchWhenStarted {
            viewmodel.data.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.pb1.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        spad.differ.submitList(it.data)

                        binding.pb1.visibility = View.INVISIBLE
                    }
                    is Resource.Error -> {
                        binding.pb1.visibility = View.INVISIBLE
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit

                }
            }
        }
    }

    private fun SetupSpecialProductRv() {

        spad = postAdapter()
        binding.recyclerView.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val adapter = spad
            binding.recyclerView.adapter = adapter
        }

    }


    override fun onResume() {
        super.onResume()
    }

}