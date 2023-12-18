package com.example.chatapp.Fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Adapter
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.chatapp.Adapters.MessageAdapter
import com.example.chatapp.Message
import com.example.chatapp.R
import com.example.chatapp.Resource
import com.example.chatapp.UserData
import com.example.chatapp.databinding.FragmentChatBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class chatFragment : Fragment() {

    private lateinit var binding: FragmentChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messaList: ArrayList<Message>
    private lateinit var ref: FirebaseDatabase


    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navbarr)
        bottomNavigationView?.visibility = View.GONE

        binding = FragmentChatBinding.inflate(layoutInflater)

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userRef = FirebaseFirestore.getInstance().collection("Users").document(currentUser?.uid!!)
        userRef.update("lastSeen" ,System.currentTimeMillis())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val value = arguments?.getString("name")
        val photo = arguments?.getString("photo")
        val uid = arguments?.getString("uid")

        ref = FirebaseDatabase.getInstance()
        messaList = arrayListOf()
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        messageAdapter = MessageAdapter(requireContext(), messaList)
        binding.view.layoutManager = LinearLayoutManager(requireContext(),)
        binding.view.adapter = messageAdapter




        binding.apply {
            nm.text = value
            Glide.with(requireContext())
                .load(photo)
                .into(iv)
        }

        val senderUID = FirebaseAuth.getInstance().currentUser?.uid
        senderRoom = uid + senderUID
        receiverRoom = senderUID + uid

        binding.sendMsg.setOnClickListener {

            val msg = binding.msg.text
            val obj = Message(msg.toString(), senderUID)

            ref.reference.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(obj).addOnSuccessListener {
                    ref.reference.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(obj)
                }

            binding.msg.setText("")

        }


        ref.reference.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    messaList.clear()

                    for (data in snapshot.children) {
                        val msg = data.getValue(Message::class.java)
                        messaList.add(msg!!)
                    }

                    messageAdapter.notifyDataSetChanged()

                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_SHORT).show()
                }


            })


    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        if (nextAnim != 0) {
            return AnimationUtils.loadAnimation(context, nextAnim)
        }

        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    override fun onDestroy() {
        super.onDestroy()

        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.navbarr)
        bottomNavigationView?.visibility = View.VISIBLE

    }


}