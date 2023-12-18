package com.example.chatapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentVideoOrPhotoBinding


class VideoOrPhotoFragment : Fragment() {

    private lateinit var binding: FragmentVideoOrPhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentVideoOrPhotoBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.upload.setOnClickListener {
            findNavController().navigate(R.id.action_videoOrPhotoFragment_to_postFragment)
        }

        binding.uploa.setOnClickListener {
           findNavController().navigate(R.id.action_videoOrPhotoFragment_to_videoFragment)
        }
    }


    interface DataTransfer {
        var data: String
    }

}