package com.example.chatapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.Adapters.VideosAdapter
import com.example.chatapp.Adapters.postAdapter
import com.example.chatapp.PostData
import com.example.chatapp.R
import com.example.chatapp.Resource
import com.example.chatapp.ViewModels.VideoViewModel
import com.example.chatapp.ViewModels.postViewModal
import com.example.chatapp.databinding.FragmentReelBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

class ReelFragment : Fragment() {


    private lateinit var spad: VideosAdapter
    private lateinit var binding: FragmentReelBinding
    private val viewmodel by viewModels<VideoViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentReelBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    }

    private fun SetupSpecialProductRv() {

        spad = VideosAdapter()
        binding.videos.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            val adapter = spad
            binding.videos.adapter = adapter
        }
    }


}