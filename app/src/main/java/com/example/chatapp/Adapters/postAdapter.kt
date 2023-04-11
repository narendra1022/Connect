package com.example.chatapp.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.PostData
import com.example.chatapp.databinding.PostviewBinding

class postAdapter :RecyclerView.Adapter<postAdapter.viewHolder>() {

    var onItemClick: ((PostData) -> Unit)? = null

    inner class viewHolder(val binding: PostviewBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(product: PostData) {
            binding.apply {
                Glide.with(itemView).load(product.profilePicUrl).into(pic)
                name.text = product.name
                city.text=product.userLocation
                Glide.with(itemView).load(product.postUrl).into(post)
                capt.text=product.caption
            }
        }

    }
    private val diffCallback = object : DiffUtil.ItemCallback<PostData>() {
        override fun areItemsTheSame(oldItem: PostData, newItem: PostData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PostData, newItem: PostData): Boolean {
            return oldItem.name == newItem.name
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        return viewHolder(
            PostviewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val product = differ.currentList[position]
        val data=
            holder.bind(product)

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(product)
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}