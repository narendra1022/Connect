package com.example.chatapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chatapp.Adapters.postAdapter
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentCommentBinding

class CommentFragment : Fragment(), postAdapter.OnItemClickListener {

    private lateinit var binding: FragmentCommentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentCommentBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment

        val adapter = postAdapter()
        adapter.onItemClickListener = this

        return binding.root
    }

    override fun onItemClick(data: String) {


        binding.data.text=data

    }

}