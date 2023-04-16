package com.example.chatapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.Adapters.profilepostsAdapter
import com.example.chatapp.PostData
import com.example.chatapp.R
import com.example.chatapp.Resource
import com.example.chatapp.ViewModels.profilepostsViewmodel
import com.example.chatapp.databinding.FragmentPostListBinding
import com.example.chatapp.databinding.FragmentSettingsBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

class PostListFragment : Fragment() {

    private lateinit var binding: FragmentPostListBinding
    private val viewmodel by viewModels<profilepostsViewmodel>()
    private lateinit var spad: profilepostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostListBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        val _data = MutableStateFlow<Resource<List<PostData>>>(Resource.unspecified())
        val data: StateFlow<Resource<List<PostData>>> = _data

        SetupSpecialProductRv()


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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun SetupSpecialProductRv() {
        spad = profilepostsAdapter()
        binding.recyclerViewPosts.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            val adapter = spad
            binding.recyclerViewPosts.adapter = adapter
        }
    }

}