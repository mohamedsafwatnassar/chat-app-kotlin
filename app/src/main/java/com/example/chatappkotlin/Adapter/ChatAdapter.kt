package com.example.chatappkotlin.Adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatappkotlin.R
import com.example.chatappkotlin.database.dao.ChatDao
import com.example.chatappkotlin.database.model.ChatModel
import com.example.chatappkotlin.database.model.UserModel
import com.example.chatappkotlin.fragments.MessageFragment
import com.example.chatmekotlin.Util.AppUtil
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.chat_item_layout.view.*

class ChatAdapter(private var chatList: ArrayList<ChatModel>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

    private lateinit var appUtil: AppUtil

    private var allChats: ArrayList<ChatModel> = chatList

    inner class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {

            appUtil = AppUtil()
            itemView.setOnClickListener {
                val position = adapterPosition

                val clickedChat = allChats[position]

                //Log.d("ChatModel", clickedChat.chatID!!)

                val bundle = Bundle()
                bundle.putString("chatId", clickedChat.chatID)

                val userModel = UserModel()
                userModel.uID = clickedChat.userid!!
                userModel.name = clickedChat.name!!
                userModel.image = clickedChat.image!!

                Hawk.put("ClickedUser", userModel)

                val fragment = MessageFragment()
                fragment.arguments = bundle

                val activity = it.context as AppCompatActivity
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.dashboardContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.chat_item_layout,
                parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = allChats[position]

        Glide.with(holder.itemView.context).load(chat.image)
            .into(holder.itemView.imgContactUserInfo)

        holder.itemView.txtChatName.text = chat.name
        holder.itemView.txtChatLastMessage.text = chat.lastMessage

        if (chat.type.equals("text")){
            holder.itemView.txtChatLastMessage.text = chat.lastMessage
        }else if (chat.type.equals("image")){
            holder.itemView.txtChatLastMessage.text = "photo"
        }

        val date = appUtil.getTimeAgo(chat.date!!.toLong())

        holder.itemView.txtChatDate.text = date


    }

    override fun getItemCount(): Int {
        return allChats.size
    }
}
