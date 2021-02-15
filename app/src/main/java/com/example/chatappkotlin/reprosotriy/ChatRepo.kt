package com.example.chatappkotlin.reprosotriy

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.chatappkotlin.database.dao.ChatDao
import com.example.chatappkotlin.database.model.ChatListModel
import com.example.chatappkotlin.database.model.ChatModel
import com.example.chatappkotlin.database.model.MessageModel
import com.example.chatappkotlin.database.model.UserModel
import com.example.chatmekotlin.Constants.AppConstants
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.orhanobut.hawk.Hawk
import org.json.JSONObject

class ChatRepo {

    private var currentUser: UserModel? = UserModel()
    private var chatUser: UserModel = UserModel()

    private var chatId: String? = null

    var chatDao: ChatDao = ChatDao()
    var messageList: ArrayList<MessageModel>? = null
    var chatModels: ArrayList<ChatModel>? = null
    var allMessage: MutableLiveData<ArrayList<MessageModel>> = MutableLiveData(null)
    var allChat: MutableLiveData<ArrayList<ChatModel>> = MutableLiveData(null)


    init {
        currentUser = Hawk.get("CurrentUser")
        //chatUser = Hawk.get("ClickedUser")

        readChat()
    }

    fun checkChat(chatUser: String) {

        // This Check if they send Message To Each Before or not
        chatDao.CheckChat(chatUser, currentUser!!.uID!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (ds in snapshot.children) {
                            val member = ds.child("member").value.toString()
                            if (chatUser == member) {
                                chatId = ds.key
                                readMessage(chatId!!)
                                break
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }

    fun sendMessage(message: String, typeMessage: String) {

        if (chatId == null) // Means they didn't Send Message To Each before
            createNewConversation(message, typeMessage)
        else { // Means they have conversation before

            updateChat(message, typeMessage)
            updateChatList(message, typeMessage)

        }
    }

    fun createNewConversation(message: String, typeMessage: String) {

        // Create New id For Chat
        chatId = chatDao.getChatId()

        createChatList(chatId!!, message, typeMessage)

        createChat(chatId!!, message, typeMessage)
    }

    fun createChat(chatId: String, message: String, typeMessage: String) {
        chatUser = Hawk.get("ClickedUser")

        var messageModel: MessageModel = MessageModel()
        if (typeMessage == "text") {
            messageModel = MessageModel(currentUser!!.uID, chatUser.uID, message,
                System.currentTimeMillis().toString(), typeMessage)
        } else if (typeMessage == "image"){
            messageModel = MessageModel(currentUser!!.uID, chatUser.uID, message,
                System.currentTimeMillis().toString(), typeMessage)
        }

        // Create Chat To Start Conversation && Save Messages
        chatDao.CreateChat(chatId, messageModel)
    }

    fun createChatList(chatid: String, message: String, typeMessage: String) {
        chatUser = Hawk.get("ClickedUser")
        // Create Child To Add Users && Chatid of conversation for each Member

        var chatListModel : ChatListModel = ChatListModel()
        if (typeMessage == "text") {
            chatListModel =
                ChatListModel(chatid, message, System.currentTimeMillis().toString(),
                    chatUser.uID, typeMessage)
        }else if (typeMessage == "image") {
            chatListModel =
                ChatListModel(chatid, message, System.currentTimeMillis().toString(),
                    chatUser.uID, typeMessage)
        }

        chatDao.CreateChatList(currentUser!!.uID, chatid, chatListModel)

        chatListModel.member = currentUser!!.uID

        chatDao.CreateChatList(chatUser.uID, chatid, chatListModel)

    }

    fun updateChatList(message: String, typeMessage: String) {
        chatUser = Hawk.get("ClickedUser")
        // update LastMessage , Date in the ChatList

        var map : Map<String, String> = HashMap()

        if (typeMessage == "text"){
            map = mapOf(
                "lastMessage" to message,
                "date" to System.currentTimeMillis().toString(),
                "type" to typeMessage
            )
        }else if (typeMessage == "image"){
            map = mapOf(
                "lastMessage" to message,
                "date" to System.currentTimeMillis().toString(),
                "type" to typeMessage
            )
        }

        chatDao.updateLastMessage(currentUser!!.uID, chatId, map)

        chatDao.updateLastMessage(chatUser.uID, chatId, map)
    }

    fun updateChat(message: String, typeMessage: String) {
        chatUser = Hawk.get("ClickedUser")

        // Add new Message in the Conversation to Chat
        var messageModel: MessageModel = MessageModel()
        if (typeMessage == "text") {
            messageModel = MessageModel(currentUser!!.uID, chatUser.uID, message,
                System.currentTimeMillis().toString(), typeMessage)
        } else if (typeMessage == "image") {
            messageModel = MessageModel(currentUser!!.uID, chatUser.uID, message,
                System.currentTimeMillis().toString(), typeMessage)
        }

        chatDao.CreateChat(chatId, messageModel)
    }

    fun readMessage(chatId: String) {
        chatDao.readMessage(chatId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    messageList = ArrayList()
                    for (ds in snapshot.children) {
                        val messages = ds.getValue(MessageModel::class.java)
                        messageList!!.add(messages!!)
                    }
                    allMessage.value = messageList
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun readChat() {
        chatDao.getChatListRef(currentUser!!.uID!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        chatModels = ArrayList()
                        for (ds in snapshot.children) {
                            val chatListModel = ds.getValue(ChatListModel::class.java)

                            getUser(chatListModel!!.member, chatListModel)
                        }
                        chatModels = ArrayList()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //TODO("Not yet implemented")
                }

            })
    }

    private fun getUser(member: String, chatListModel: ChatListModel?) {
        chatDao.getMemberUser(member).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(UserModel::class.java)
                    val chatModel = ChatModel(
                        chatListModel!!.chatId,
                        user!!.name,
                        chatListModel.lastMessage,
                        user.image,
                        chatListModel.date,
                        user.online,
                        user.uID,
                        chatListModel.type
                    )
                    chatModels!!.add(chatModel)
                }

                allChat.value = chatModels
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getToken(message: String, context: Context, type: String) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(chatUser.uID)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val token = snapshot.child("token").value.toString()

                    val to = JSONObject()
                    val data = JSONObject()

                    data.put("hisId", currentUser!!.uID)
                    data.put("hisImage", currentUser!!.image)
                    data.put("title", currentUser!!.name)
                    if (type == "text"){
                        data.put("message", message)
                    }else if (type == "image"){
                        data.put("message", "photo")
                    }

                    //data.put("chatId", chatId) // not found chatId

                    to.put("to", token)
                    to.put("data", data)
                    sendNotification(to, context)

                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun sendNotification(to: JSONObject, context: Context) {

        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            AppConstants.NOTIFICATION_URL,
            to,
            Response.Listener { response: JSONObject ->

                Log.d("TAG", "onResponse: $response")
            },
            Response.ErrorListener {

                Log.d("TAG", "onError: $it")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map: MutableMap<String, String> = HashMap()

                map["Authorization"] = "key=" + AppConstants.SERVER_KEY
                map["Content-type"] = "application/json"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }

        val requestQueue = Volley.newRequestQueue(context)
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        requestQueue.add(request)

    }

}