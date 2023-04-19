package com.example.chatapp.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
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
import com.example.chatapp.databinding.VideoviewBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class VideosAdapter : RecyclerView.Adapter<VideosAdapter.viewHolder>() {

    inner class viewHolder(val binding: VideoviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: PostData) {

            binding.share.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Post")
                shareIntent.putExtra(Intent.EXTRA_TEXT,product.postUrl)
                itemView.context.startActivity(Intent.createChooser(shareIntent, "Share via"))
            }

            binding.apply {
                Glide.with(itemView).load(product.profilePicUrl).into(pic)
                name.text = product.name
                city.text = product.userLocation
                post.setVideoPath(product.postUrl)
                post.start()
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
            VideoviewBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val product = differ.currentList[position]
        val data = holder.bind(product)


        val hashMa: HashMap<String, Any> = HashMap()

        hashMa["name"] = product.name.toString()
        hashMa["userLocation"] = product.userLocation.toString()
        hashMa["profilePicUrl"] = product.profilePicUrl.toString()
        hashMa["uid"] = FirebaseAuth.getInstance().currentUser?.uid!!.toString()
        hashMa["category"] = "saves"
        hashMa["likes"] = product.likes!!
        hashMa["postUrl"] = product.postUrl.toString()

        holder.binding.postsLikeCount.text = product.likes.toString()

        holder.binding.like.setOnClickListener {

            val collectionRef = FirebaseFirestore.getInstance().collection("videos")
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

            val collectionRef = FirebaseFirestore.getInstance().collection("videos")
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
            postRef.add(hashMa)
                .addOnSuccessListener {
                    holder.binding.save.visibility = View.GONE
                    holder.binding.saved.visibility = View.VISIBLE
                }
        }


        holder.binding.saved.setOnClickListener {

            val collectionRef = FirebaseFirestore.getInstance().collection("saved")
            val query = collectionRef.whereEqualTo("postUrl", product.postUrl.toString())
            query.get().addOnSuccessListener { querySnapshot ->
                val documentSnapshot = querySnapshot.documents.firstOrNull()
                documentSnapshot?.let { document ->
                    val documentRef = collectionRef.document(document.id)
                    documentRef.delete()

                    holder.binding.saved.visibility = View.GONE
                    holder.binding.save.visibility = View.VISIBLE

                }
            }
        }


        holder.binding.comment.setOnClickListener {

            val collectionRef = FirebaseFirestore.getInstance().collection("posts")
            val query = collectionRef.whereEqualTo("postUrl", product.postUrl.toString())
            query.get().addOnSuccessListener { querySnapshot ->
                val documentSnapshot = querySnapshot.documents.firstOrNull()
                documentSnapshot?.let { document ->
                    val documentRef = collectionRef.document(document.id)
                }
            }


            holder.binding.comment.setOnClickListener {
//                onItemClick?.invoke(product)
            }


        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}
