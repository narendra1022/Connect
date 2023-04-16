package com.example.chatapp.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.PostData
import com.example.chatapp.PostsOperationsInterface
import com.example.chatapp.R
import com.example.chatapp.databinding.PostviewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class postAdapter : RecyclerView.Adapter<postAdapter.viewHolder>() {

    var onItemClick: ((PostData) -> Unit)? = null
    var onItemClick2: ((PostData) -> Unit)? = null
    var onItemClickListener: OnItemClickListener? = null


    inner class viewHolder(val binding: PostviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: PostData) {
            binding.apply {
                Glide.with(itemView).load(product.profilePicUrl).into(pic)
                name.text = product.name
                city.text = product.userLocation
                Glide.with(itemView).load(product.postUrl).into(post)
                capt.text = product.caption
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
        val data = holder.bind(product)

        val hashMap: HashMap<String, String> = HashMap()

        hashMap["name"] = product.name.toString()
        hashMap["userLocation"] = product.userLocation.toString()
        hashMap["profilePicUrl"] = product.profilePicUrl.toString()
        hashMap["uid"] = product.uid.toString()
        hashMap["category"] = "saves"
        hashMap["postUrl"] = product.postUrl.toString()

        holder.binding.postsLikeCount.text = product.likes.toString()

        holder.binding.like.setOnClickListener {

            val collectionRef = FirebaseFirestore.getInstance().collection("posts")
            val query = collectionRef.whereEqualTo("postUrl", product.postUrl.toString())
            query.get().addOnSuccessListener { querySnapshot ->
                val documentSnapshot = querySnapshot.documents.firstOrNull()
                documentSnapshot?.let { document ->
                    val documentRef = collectionRef.document(document.id)
                    documentRef.update("likes", product.likes?.plus(1))
                    holder.binding.postsLikeCount.text =
                        (holder.binding.postsLikeCount.text.toString().toInt() + 1).toString()
                    holder.binding.like.setImageResource(R.drawable.ic_like_post_filled)
                    holder.binding.like.visibility = View.GONE
                    holder.binding.undoLike.visibility = View.VISIBLE

                }
            }
        }

        holder.binding.undoLike.setOnClickListener {

            val collectionRef = FirebaseFirestore.getInstance().collection("posts")
            val query = collectionRef.whereEqualTo("postUrl", product.postUrl.toString())
            query.get().addOnSuccessListener { querySnapshot ->
                val documentSnapshot = querySnapshot.documents.firstOrNull()
                documentSnapshot?.let { document ->
                    val documentRef = collectionRef.document(document.id)
                    documentRef.update("likes", product.likes?.minus(1))
                    holder.binding.postsLikeCount.text =
                        (holder.binding.postsLikeCount.text.toString().toInt() - 1).toString()
                    holder.binding.like.setImageResource(R.drawable.ic_like_post)
                    holder.binding.undoLike.visibility = View.GONE
                    holder.binding.like.visibility = View.VISIBLE

                }
            }
        }

        holder.binding.save.setOnClickListener {
            val postRef = FirebaseFirestore.getInstance()
                .collection("saved")
            postRef.add(hashMap)
                .addOnSuccessListener {
                }
        }


        holder.binding.comment.setOnClickListener {

            val collectionRef = FirebaseFirestore.getInstance().collection("posts")
            val query = collectionRef.whereEqualTo("postUrl", product.postUrl.toString())
            query.get().addOnSuccessListener { querySnapshot ->
                val documentSnapshot = querySnapshot.documents.firstOrNull()
                documentSnapshot?.let { document ->
                    val documentRef = collectionRef.document(document.id)
                    onItemClickListener?.onItemClick(documentRef.toString())
                }
            }


            holder.binding.comment.setOnClickListener {
                onItemClick?.invoke(product)
            }

            holder.binding.share.setOnClickListener {
                onItemClick2?.invoke(product)
            }

        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    interface OnItemClickListener {
        fun onItemClick(data: String)
    }


}
