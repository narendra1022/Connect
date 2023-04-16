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
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.example.chatapp.Adapters.profileAdapter
import com.example.chatapp.R
import com.example.chatapp.Resource
import com.example.chatapp.ViewModels.profileViewmodel
import com.example.chatapp.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.collectLatest

class MessagingFragment : Fragment() {

    private lateinit var spad:profileAdapter
    private lateinit var binding: FragmentHomeBinding
    private val viewmodel by viewModels<profileViewmodel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentHomeBinding.inflate(layoutInflater)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseFirestore.getInstance().collection("Users").document(currentUser?.uid!!)
        userRef.update("lastSeen" ,System.currentTimeMillis())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        SetupSpecialProductRv()

        spad.onItemClick={

            val b=Bundle()
            b.putString("name",it.name)
            b.putString("photo",it.profilePicUrl)
            b.putString("uid",it.uid)
            val fragment = chatFragment()
            fragment.arguments = b

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main, fragment)
                .setCustomAnimations(R.anim.fragment_slide_in, R.anim.fragment_slide_out)
                .addToBackStack(null)
                .commit()


        }

        lifecycleScope.launchWhenStarted {
            viewmodel.data.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        binding.pb1.visibility=View.VISIBLE
                    }
                    is Resource.Success -> {
                        if(FirebaseAuth.getInstance().uid.toString() != FirebaseAuth.getInstance().currentUser.toString() ) {
                            spad.differ.submitList(it.data)
                        }
                        binding.pb1.visibility=View.INVISIBLE
                    }
                    is Resource.Error -> {
                        binding.pb1.visibility=View.INVISIBLE
                        Toast.makeText(requireContext(), "Error", Toast.LENGTH_SHORT).show()
                    }
                    else ->Unit

                }
            }
        }
    }

    private fun SetupSpecialProductRv() {

        spad= profileAdapter()
        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.recyclerView)
        binding.recyclerView.apply {
            layoutManager=
                LinearLayoutManager(requireContext())
            val adapter=spad
            binding.recyclerView.adapter=adapter
        }

    }


    override fun onResume() {
        super.onResume()
    }

}