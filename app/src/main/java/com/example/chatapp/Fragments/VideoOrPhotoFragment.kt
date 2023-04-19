package com.example.chatapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            val t = parentFragmentManager.beginTransaction()
            t.add(R.id.main, PostFragment())
                .addToBackStack("change")
                .commit()
        }

        binding.uploa.setOnClickListener {
            val t = parentFragmentManager.beginTransaction()
            t.add(R.id.main, VideoFragment())
                .addToBackStack("change")
                .commit()
        }
    }


    interface DataTransfer {
        var data: String
    }

}