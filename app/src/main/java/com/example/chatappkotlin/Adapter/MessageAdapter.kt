package com.example.chatappkotlin.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatappkotlin.R
import com.example.chatappkotlin.database.model.MessageModel
import com.example.chatappkotlin.database.model.UserModel
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.left_item_layout.view.*
import kotlinx.android.synthetic.main.right_item_layout.view.*
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(private var messages: ArrayList<MessageModel>?) :
    RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    var currentUser: UserModel = Hawk.get("CurrentUser")
    var clickedUser: UserModel = Hawk.get("ClickedUser")

    private var allMessage: ArrayList<MessageModel>? = messages

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageViewHolder {
        var view: View? = null
        if (viewType == 0) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.right_item_layout, parent, false)

        } else if (viewType == 1) {
            view = LayoutInflater.from(parent.context)
                .inflate(R.layout.left_item_layout, parent, false)
        }
        return MessageViewHolder(view!!)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        val message = allMessage!![position]

        if (viewType == 0) {

            Glide.with(holder.itemView.context).load(currentUser.image)
                .into(holder.itemView.imgMessageImageR)

            holder.itemView.messageDateR.text = toDateString(message.date.toLong())

            if (message.type.equals("text")) {
                holder.itemView.sendImageR.visibility = View.GONE
                holder.itemView.txtMessageR.text = message.message
            } else {
                holder.itemView.txtMessageR.visibility = View.GONE
                Glide.with(holder.itemView.context).load(message.message)
                    .into(holder.itemView.sendImageR)
            }

        } else if (viewType == 1) {

            Glide.with(holder.itemView.context).load(clickedUser.image)
                .into(holder.itemView.imgMessageImageL)

            holder.itemView.messageDateL.text = toDateString(message.date.toLong())

            if (message.type.equals("text")) {
                holder.itemView.sendImageL.visibility = View.GONE
                holder.itemView.txtMessageL.text = message.message

            } else if (message.type.equals("image")) {
                holder.itemView.txtMessageL.visibility = View.GONE
                Glide.with(holder.itemView.context).load(message.message)
                    .into(holder.itemView.sendImageL)
            }
        }
    }

    fun toDateString(time: Long): String {
        val formatter = SimpleDateFormat("hh.mm aa")

        // Create a calendar object that will convert the date and time value in milliseconds to date.

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = time
        return formatter.format(calendar.time)
    }

    override fun getItemViewType(position: Int): Int {
        val message = messages!![position]

        if (message.senderId.equals(currentUser.uID))
            return 0
        else
            return 1
    }

    override fun getItemCount(): Int {
        return allMessage!!.size
    }
}