package com.example.chatappkotlin.database.model


data class UserModel(
    var name: String = "",
    var status: String = "",
    var image: String = "",
    var number: String = "",
    var uID: String = "",
    val online: String = "offline",
    val typing: String = "false"
)



