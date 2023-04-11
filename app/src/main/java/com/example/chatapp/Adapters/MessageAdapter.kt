package com.example.chatapp.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.chatapp.Message
import com.example.chatapp.R
import com.example.chatapp.databinding.ProfilesBinding
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList: ArrayList<Message>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_SENT = 2;
    val ITEM_RECEIVE = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        if (viewType == 1) {

            val view:View = LayoutInflater.from(context).inflate(R.layout.reciever, parent, false)
            return recieveViewHolder(view)

        } else {

            val view:View = LayoutInflater.from(context).inflate(R.layout.send, parent, false)
            return sentViewHolder(view)

        }

    }

    override fun getItemViewType(position: Int): Int {

        val currentmsg = messageList[position]
        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentmsg.senderId)) {
            return ITEM_SENT
        } else {
            return ITEM_RECEIVE
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentmsg = messageList[position]
        if (holder.javaClass == sentViewHolder::class.java) {
            val viewHolder = holder as sentViewHolder
            holder.sentmsg.text = currentmsg.message
        } else {
            val viewHolder = holder as recieveViewHolder
            holder.receivemsg.text = currentmsg.message
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }


    class sentViewHolder(itemView: View) : ViewHolder(itemView) {
        val sentmsg = itemView.findViewById<TextView>(R.id.sent)
    }

    class recieveViewHolder(itemView: View) : ViewHolder(itemView) {
        val receivemsg = itemView.findViewById<TextView>(R.id.receive)
    }
}