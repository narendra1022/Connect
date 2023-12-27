package com.example.chatapp.Fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.chatapp.MainActivity
import com.example.chatapp.PostData
import com.example.chatapp.R
import com.example.chatapp.Util.show
import com.example.chatapp.databinding.FragmentVideoBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Player.REPEAT_MODE_ONE
import com.google.android.exoplayer2.Player.RepeatMode
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.Date
import java.util.UUID

class VideoFragment : Fragment() {

    private lateinit var binding: FragmentVideoBinding
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var firebaseAuth: FirebaseAuth
    private var uri: Uri? = null
    private var nm: String? = null
    private var lo: String? = null
    private var post: String? = "narebxa"
    private var check: String? = ""
    private var prof: String? = null
    private lateinit var player: ExoPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideoBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.postImg.setOnClickListener {
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
            intent.type = "video/*"
            startActivityForResult(intent, 1)

            FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().uid!!).get().addOnSuccessListener {
                    nm = it.get("name") as String?
                    lo = it.get("userLocation") as String?
                    prof = it.get("profilePicUrl") as String?
                }

        }

        binding.upload.setOnClickListener {
            if (check.equals("success")) {
                Toast.makeText(requireContext(), "Posted", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_videoFragment_to_homeFragment)
            } else {
                Toast.makeText(requireContext(), "Nothing Posted", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_videoFragment_to_homeFragment)
            }
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (data != null) {
            if (data.data != null) {
                uri = data.data!!

                player = ExoPlayer.Builder(binding.root.context).build()
                binding.postImg.player = player

                player.addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)
                        Toast.makeText(
                            binding.root.context,
                            "Can't play the video",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_BUFFERING -> {
                                binding.pb.visibility = View.VISIBLE
                            }

                            Player.STATE_READY, Player.STATE_ENDED -> {
                                binding.pb.visibility = View.GONE
                            }
                        }
                    }
                })

                player.seekTo(0)
                player.repeatMode = Player.REPEAT_MODE_ONE

                val dataSourceFactory = DefaultDataSource.Factory(requireContext())
                var mediaSource =
                    ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(
                        com.google.android.exoplayer2.MediaItem.fromUri(
                            Uri.parse(
                                data.data!!.toString()
                            )
                        )
                    )

                player.setMediaSource(mediaSource)
                player.prepare()
                player.play()


            }
        }

        val sReference =
            FirebaseStorage.getInstance().reference.child("Images").child("video_folder").child(
                Date().time.toString()
            )

        uri?.let { it1 ->
            sReference.putFile(it1).addOnCompleteListener {
                if (it.isSuccessful) {
                    sReference.downloadUrl.addOnSuccessListener { task ->

                        post = task.toString()
                        val ca = binding.cap.text.toString()

                        val data = PostData(
                            UUID.randomUUID().toString(),
                            ca,
                            post,
                            nm,
                            prof,
                            lo,
                            "post",
                            firebaseAuth.currentUser?.uid!!.toString(),
                            0
                        )

                        FirebaseFirestore.getInstance().collection("videos")
                            .add(data).addOnSuccessListener { doc ->
                                val data = doc.id
                                check = "success"

                            }

                    }
                }
            }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
    }


}

