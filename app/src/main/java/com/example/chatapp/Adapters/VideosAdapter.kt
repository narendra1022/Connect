package com.example.viewpager2withexoplayer

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.PostData
import com.example.chatapp.R
import com.example.chatapp.Util.ExoPlayerItem
import com.example.chatapp.databinding.VideoviewBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class VideoAdapter(
    var context: Context,
    var videoPreparedListener: OnVideoPreparedListener
) : RecyclerView.Adapter<VideoAdapter.VideoViewHolder>() {

    class VideoViewHolder(
        val binding: VideoviewBinding,
        var context: Context,
        var videoPreparedListener: OnVideoPreparedListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var exoPlayer: ExoPlayer
        private lateinit var mediaSource: MediaSource

        fun bind(product: PostData) {

            binding.share.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Post")
                shareIntent.putExtra(Intent.EXTRA_TEXT, product.postUrl)
                itemView.context.startActivity(Intent.createChooser(shareIntent, "Share via"))
            }


            binding.apply {
                Glide.with(itemView).load(product.profilePicUrl).into(pic)
                name.text = product.name
                city.text = product.userLocation
                capt.text = product.caption

            }


            exoPlayer = ExoPlayer.Builder(context).build()

            exoPlayer.addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    Log.e("VideoAdapter", "ExoPlayer error: ${error.message}", error)
                    Toast.makeText(context, "Can't play this video", Toast.LENGTH_SHORT).show()
                }

                override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                    if (playbackState == Player.STATE_BUFFERING) {
                        binding.progressbar.visibility = View.VISIBLE
                        binding.exoPlayerView.visibility = View.GONE
                    } else if (playbackState == Player.STATE_READY) {
                        binding.progressbar.visibility = View.GONE
                        binding.exoPlayerView.visibility = View.VISIBLE
                    }
                }
            })

            binding.exoPlayerView.player = exoPlayer

            exoPlayer.seekTo(0)
            exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

            val dataSourceFactory = DefaultDataSource.Factory(context)

            Log.d("VideoAdapter", "Media Source URL: ${product.postUrl}")

            mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                MediaItem.fromUri(
                    Uri.parse(
                        product.postUrl
                    )
                )
            )

            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()

            if (absoluteAdapterPosition == 0) {
                exoPlayer.playWhenReady = true
                exoPlayer.play()
            }

            videoPreparedListener.onVideoPrepared(ExoPlayerItem(exoPlayer, absoluteAdapterPosition))
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = VideoviewBinding.inflate(LayoutInflater.from(context), parent, false)
        return VideoViewHolder(view, context, videoPreparedListener)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {

        val product = differ.currentList[position]
        holder.bind(product)


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

    interface OnVideoPreparedListener {
        fun onVideoPrepared(exoPlayerItem: ExoPlayerItem)
    }
}


//
//}
