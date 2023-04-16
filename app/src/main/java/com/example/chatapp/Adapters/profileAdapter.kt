package com.example.chatapp.Adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.Fragments.chatFragment
import com.example.chatapp.R
import com.example.chatapp.UserData
import com.example.chatapp.databinding.ProfilesBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class profileAdapter : RecyclerView.Adapter<profileAdapter.viewHolder>() {

    var onItemClick: ((UserData) -> Unit)? = null

    inner class viewHolder(val binding: ProfilesBinding) : RecyclerView.ViewHolder(binding.root) {


        fun bind(product: UserData) {

//            if (FirebaseAuth.getInstance().currentUser?.uid!!.toString() != (product.uid.toString())) {
                binding.apply {

                    Glide.with(itemView).load(product.profilePicUrl).into(pic)

                    FirebaseFirestore.getInstance().collection("Users")
                        .document(product.uid.toString())
                        .get().addOnSuccessListener {
                            val time = it.get("lastSeen")
                            name.text =product.name
                            val t = getLastSeenDisplayText(time as Long)
                            city.text = t
                            
                            if (t=="Online"){
                                binding.active.visibility=View.VISIBLE
                            }

                        }
                }
//            }
        }
    }

    fun getLastSeenDisplayText(lastSeen: Long): String {
        val currentTime = System.currentTimeMillis()

        val elapsedMillis = currentTime - lastSeen

        return when {
            elapsedMillis < TimeUnit.MINUTES.toMillis(1) -> "Online"
            elapsedMillis < TimeUnit.HOURS.toMillis(1) -> {
                val elapsedMinutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMillis)
                "Last seen $elapsedMinutes minutes ago"
            }

            elapsedMillis < TimeUnit.DAYS.toMillis(1) -> {
                val elapsedHours = TimeUnit.MILLISECONDS.toHours(elapsedMillis)
                "Last seen $elapsedHours hours ago"
            }

            else -> {
                val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                "Last seen on ${dateFormatter.format(Date(lastSeen))}"
            }
        }
    }


    private val diffCallback = object : DiffUtil.ItemCallback<UserData>() {
        override fun areItemsTheSame(oldItem: UserData, newItem: UserData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: UserData, newItem: UserData): Boolean {
            return oldItem.name == newItem.name
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(
            ProfilesBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val product = differ.currentList[position]

//        if (FirebaseAuth.getInstance().currentUser?.uid!!.toString() != (product.uid.toString())) {
            holder.bind(product)
//        } else {
//            return//        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(product)
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}