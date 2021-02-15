package com.example.chatappkotlin.database

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RealtimeDatabase {
    private val User_Ref: String = "Users"
    private val Chat_Ref: String = "Chat"
    private val ChatList_Ref: String = "ChatList"

    var database: FirebaseDatabase? = null

    fun getInstance(): FirebaseDatabase {
        database = FirebaseDatabase.getInstance()

        if (database == null){
            database = FirebaseDatabase.getInstance();
        }
        return database!!
    }

    fun getUserRef(): DatabaseReference {
        return getInstance().reference.child(User_Ref)
    }

    fun getChatRef(): DatabaseReference {
        return getInstance().reference.child(Chat_Ref)
    }

    fun getChatListRef(): DatabaseReference {
        return getInstance().reference.child(ChatList_Ref)
    }
}
