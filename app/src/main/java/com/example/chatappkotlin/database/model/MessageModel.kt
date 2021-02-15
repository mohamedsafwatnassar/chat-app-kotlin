package com.example.chatappkotlin.database.model

class MessageModel (
    var senderId: String = "",
    var receiverId: String = "",
    var message: String? = "",
    var date: String = "",
    var type: String = ""
)