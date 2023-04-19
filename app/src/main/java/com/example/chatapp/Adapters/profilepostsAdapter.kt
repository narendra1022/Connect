package com.example.chatapp.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.PostData
import com.example.chatapp.databinding.PostlistBinding

class profilepostsAdapter:RecyclerView.Adapter <profilepostsAdapter.viewHolder>(){

    inner class viewHolder( val binding: PostlistBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(data :PostData){
            binding.apply {
                Glide.with(itemView).load(data.postUrl).into(postss)
                li.text=data.likes.toString()+" likes"
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): profilepostsAdapter.viewHolder {
        return viewHolder(
            PostlistBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: profilepostsAdapter.viewHolder, position: Int) {
        val product = differ.currentList[position]
        val data = holder.bind(product)

    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }



}