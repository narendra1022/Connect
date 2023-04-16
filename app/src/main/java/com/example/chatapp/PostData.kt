package com.example.chatapp

data class PostData(
    var postid: String = "null",
    var caption: String? = "null",
    var postUrl: String? = "null",
    var name: String? = "null",
    var profilePicUrl: String? = "null",
    var userLocation: String? = "null",
    var post:String?="null",
    var uid:String?=null,
    var likes: Int? =null
)
