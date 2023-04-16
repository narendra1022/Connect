package com.example.chatapp.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapp.Adapters.postAdapter
import com.example.chatapp.PostData
import com.example.chatapp.PostsOperationsInterface
import com.example.chatapp.R
import com.example.chatapp.Resource
import com.example.chatapp.ViewModels.postViewModal
import com.example.chatapp.databinding.FragmentMessageBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest

class HomeFragment : Fragment() {

    private lateinit var spad: postAdapter
    private lateinit var binding: FragmentMessageBinding
    private val viewmodel by viewModels<postViewModal>()
    private val firebase = Firebase.firestore
    private var id: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMessageBinding.inflate(layoutInflater)


        val currentUser = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseFirestore.getInstance().collection("Users").document(currentUser?.uid!!)
        userRef.update("lastSeen" ,System.currentTimeMillis())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val _data = MutableStateFlow<Resource<List<PostData>>>(Resource.unspecified())
        val data: StateFlow<Resource<List<PostData>>> = _data

        SetupSpecialProductRv()


        binding.swipeRefreshLayout.setOnRefreshListener {
            postAdapter().notifyDataSetChanged()
            binding.swipeRefreshLayout.isRefreshing = false
        }


//
//        binding.pic.setOnClickListener {
//
//            val t = parentFragmentManager.beginTransaction()
//            t.replace(R.id.nav_host_fragment, PostFragment())
//                .addToBackStack("change")
//                .commit()
//
////            val action = HomeFragmentDirections.actionHomeFragmentToPostFragment()
////            findNavController().navigate(action)
////
//
////            findNavController().navigate(R.id.action_homeFragment_to_postFragment)
//        }

        spad.onItemClick = {

            val postUrl = it.postUrl.toString()

            val mDialog = AlertDialog.Builder(requireContext())
            val inflater = layoutInflater
            val mDialogView = inflater.inflate(R.layout.edit_name, null)
            mDialog.setView(mDialogView)
            val addEmailbtn: Button = mDialogView.findViewById(R.id.addBtn)
            val addEmail: TextView = mDialogView.findViewById(R.id.addEmail)
            mDialog.setTitle("Edit name")
            val alertDialog = mDialog.create()
            alertDialog.show()

            addEmailbtn.setOnClickListener {

                val mail = addEmail.text.toString()
                if (mail.length.equals(0)) {
                    Toast.makeText(requireContext(), "Post Comment", Toast.LENGTH_SHORT).show()
                } else {

                    FirebaseFirestore.getInstance().collection("Users")
                        .document(FirebaseAuth.getInstance().uid!!)
                        .update("name", mail).addOnSuccessListener {
                            Toast.makeText(
                                requireContext(),
                                "Comment Added Successfully",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            alertDialog.dismiss()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                                .show()
                        }
                }


            }
        }

        spad.onItemClick2 = {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = "text/plain"
            shareIntent.putExtra(Intent.EXTRA_TEXT, it.postUrl)
            startActivity(Intent.createChooser(shareIntent, "Share post"))
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

    inner class PostOperationsInterfaceImp : PostsOperationsInterface {
        override fun editPost(givenPostId: String) {
            id = givenPostId
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