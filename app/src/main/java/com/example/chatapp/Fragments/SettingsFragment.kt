package com.example.chatapp.Fragments

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapp.Adapters.ViewpagerAdapter
import com.example.chatapp.Adapters.profilepostsAdapter
import com.example.chatapp.MainActivity
import com.example.chatapp.PostData
import com.example.chatapp.R
import com.example.chatapp.Resource
import com.example.chatapp.ViewModels.profilepostsViewmodel
import com.example.chatapp.databinding.FragmentSettingsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import java.util.Date

class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding
    private var uri: Uri? = null
    private val viewmodel by viewModels<profilepostsViewmodel>()
    private lateinit var spad: profilepostsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater)


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

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun SetupSpecialProductRv() {
        spad = profilepostsAdapter()
        binding.recyclerViewPost.apply {
            layoutManager =
                LinearLayoutManager(requireContext())
            val adapter = spad
            binding.recyclerViewPost.adapter = adapter
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        FirebaseFirestore.getInstance().collection("Users")
            .document(FirebaseAuth.getInstance().currentUser?.uid!!).get().addOnSuccessListener {
                val name = it.getString("name")
                val pic = it.getString("profilePicUrl")
                binding.nm.text = name
                Glide.with(requireContext()).load(pic).into(binding.iv)
            }.addOnFailureListener {

            }

        binding.editName.setOnClickListener {
            dialog()
        }

        binding.sav.setOnClickListener {
            val t=parentFragmentManager.beginTransaction()
            t.add(R.id.main,SavedFragment())
                .addToBackStack("change")
                .commit()
        }


    }

    private fun dialog() {

        val mDialog = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val mDialogView = inflater.inflate(R.layout.edit, null)
        mDialog.setView(mDialogView)
        val addEmailbtn: Button = mDialogView.findViewById(R.id.addBt)
        val addEmail: TextView = mDialogView.findViewById(R.id.addEmai)
        val pic: ImageView = mDialogView.findViewById(R.id.phot)
        val alertDialog = mDialog.create()
        alertDialog.show()

        addEmailbtn.setOnClickListener {

            val mail = addEmail.text.toString()
            if (mail.length.equals(0)) {
                Toast.makeText(requireContext(), "Enter name", Toast.LENGTH_SHORT).show()
            } else {
                FirebaseFirestore.getInstance().collection("Users")
                    .document(FirebaseAuth.getInstance().uid!!)
                    .update("name", mail).addOnSuccessListener {
                        Toast.makeText(
                            requireContext(),
                            "Name updated Succesfully",
                            Toast.LENGTH_SHORT
                        )
                            .show()

                        FirebaseFirestore.getInstance().collection("Users")
                            .document(FirebaseAuth.getInstance().uid!!)
                            .get()
                            .addOnSuccessListener {

                                binding.nm.text = it.getString("name")

                            }

                        alertDialog.dismiss()
                    }
                    .addOnFailureListener {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT)
                            .show()
                    }
            }
        }

        pic.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    MainActivity.READ_PERMISSION
                )
            }

            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (data.data != null) {
                uri = data.data!!
                binding.iv.setImageURI(data.data!!)
            }
        }

        Toast.makeText(
            requireContext(),
            "Profile pic updated",
            Toast.LENGTH_SHORT
        ).show()

        val sReference =
            FirebaseStorage.getInstance().reference.child("Images").child("user_folder").child(
                Date().time.toString()
            )

        uri?.let { it1 ->
            sReference.putFile(it1).addOnCompleteListener {
                if (it.isSuccessful) {
                    sReference.downloadUrl.addOnSuccessListener { task ->
                        FirebaseFirestore.getInstance().collection("Users")
                            .document(FirebaseAuth.getInstance().uid!!)
                            .update("profilePicUrl", task).addOnSuccessListener {

                            }
                    }
                }
            }

                .addOnFailureListener {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }

}