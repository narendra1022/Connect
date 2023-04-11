package com.example.chatapp.Adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.Fragments.chatFragment
import com.example.chatapp.R
import com.example.chatapp.UserData
import com.example.chatapp.databinding.ProfilesBinding

class profileAdapter :RecyclerView.Adapter<profileAdapter.viewHolder>() {

    var onItemClick: ((UserData) -> Unit)? = null

    inner class viewHolder(val binding: ProfilesBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(product: UserData) {
            binding.apply {
                Glide.with(itemView).load(product.profilePicUrl).into(pic)
                name.text = product.name
                city.text=product.userLocation

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

        val data= holder.bind(product)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(product)
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}