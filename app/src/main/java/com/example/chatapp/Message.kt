package com.example.chatapp

import android.text.Editable

class Message {
    var message: String? = null
    var senderId: String? = null
    constructor (){}
    constructor (message: String, senderId: String?) {
        this.message = message
        this.senderId = senderId
    }
}
