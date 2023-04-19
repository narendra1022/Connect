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

        val t=parentFragmentManager.beginTransaction()
        t.add(R.id.main,HomeFragment())
            .commit()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}