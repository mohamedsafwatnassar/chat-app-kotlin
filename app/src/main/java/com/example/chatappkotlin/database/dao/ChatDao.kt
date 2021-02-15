package com.example.chatappkotlin.database.dao

import com.example.chatappkotlin.database.RealtimeDatabase
import com.example.chatappkotlin.database.model.ChatListModel
import com.example.chatappkotlin.database.model.MessageModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Query

class ChatDao {

    var realtimeDatabase: RealtimeDatabase = RealtimeDatabase()

    fun CheckChat(userid: String? , myid : String?): Query {
        val reference = realtimeDatabase.getChatListRef()
            .child(myid!!)
        reference.keepSynced(true)
        return reference.orderByChild("member").equalTo(userid)
    }

    fun getChatId(): String? {
        val reference = realtimeDatabase.getChatListRef()

        val chatId = reference.push().key

        return chatId
    }

    fun CreateChatList(myId: String?, chatId: String?, chatListModel: ChatListModel) {
        realtimeDatabase.getChatListRef().child(myId!!)
            .child(chatId!!)
            .setValue(chatListModel)
    }

    fun CreateChat(chatId: String?, messageModel: MessageModel){
        realtimeDatabase.getChatRef().child(chatId!!)
            .push()
            .setValue(messageModel)
    }

    fun createChatImage(chatId: String?, messageModel: MessageModel?){
        realtimeDatabase.getChatRef().child(chatId!!)
            .push()
            .setValue(messageModel)
    }

    fun updateLastMessage(myId: String?, chatId: String?, map: Map<String, String>) {
        realtimeDatabase.getChatListRef()
            .child(myId!!)
            .child(chatId!!)
            .updateChildren(map)
    }

    fun casheChat(chatId: String?, myId: String?, message: String){
        realtimeDatabase.getChatRef()
            .child(chatId!!)
            .child(myId!!)
            .setValue(message)
    }

    fun readMessage(chatId : String?): DatabaseReference {

        val reference = realtimeDatabase.getChatRef()
            .child(chatId!!)

        reference.keepSynced(true)
        return reference
    }

    fun getChatListRef(myId: String?): DatabaseReference {
        val reference = realtimeDatabase.getChatListRef()
            .child(myId!!)
        reference.keepSynced(true)
        return reference
    }

    fun getMemberUser(member: String?): DatabaseReference {
        val reference = realtimeDatabase.getUserRef()
            .child(member!!)
        reference.keepSynced(true)
        return reference
    }
}