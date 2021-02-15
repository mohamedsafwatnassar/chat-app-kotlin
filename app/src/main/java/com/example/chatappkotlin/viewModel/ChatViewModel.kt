package com.example.chatappkotlin.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.chatappkotlin.database.dao.UserDao
import com.example.chatappkotlin.database.model.ChatModel
import com.example.chatappkotlin.database.model.MessageModel
import com.example.chatappkotlin.database.model.UserModel
import com.example.chatappkotlin.reprosotriy.ChatRepo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.orhanobut.hawk.Hawk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private var context: Context? = null
    private var repo : ChatRepo? = ChatRepo()
    private var chatUser: UserModel? = null
    private var currentUser = UserModel()
    var userDao: UserDao = UserDao()

    var messsageMutable = MutableLiveData<ArrayList<MessageModel>>()
    var chatMutable = MutableLiveData<ArrayList<ChatModel>>()

    var onlineStatus = MutableLiveData<String>()
    var typingStatus = MutableLiveData<String?>()

    init {

        chatUser = Hawk.get("ClickedUser")
        currentUser = Hawk.get("CurrentUser")

        context = getApplication<Application>().applicationContext

        onlineStatus = MutableLiveData(null)
        typingStatus = MutableLiveData(null)

        messsageMutable = MutableLiveData(null)
        chatMutable = MutableLiveData(null)

        GlobalScope.launch(Dispatchers.IO) {
            if (chatUser != null){
                repo!!.checkChat(chatUser!!.uID)
            }
        }

        messsageMutable = repo!!.allMessage
        chatMutable = repo!!.allChat
    }

    fun sendMessage(message: String, type: String) {
        GlobalScope.launch(Dispatchers.Main) {
            repo!!.sendMessage(message, type)
        }
    }

    fun getToken(message: String, type: String) {
        GlobalScope.launch(Dispatchers.Main) {
            repo!!.getToken(message, context!!, type)
        }
    }

    fun getUserStatus(){
        userDao.getUser(chatUser!!.uID).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(UserModel::class.java)
                    onlineStatus.value = user!!.online
                    typingStatus.value = user.typing
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun typingStatus(typing: String){

        val map = mapOf(
            "typing" to typing,
        )

        userDao.typingStatus(currentUser.uID, map)
    }

    fun readMessages(chatId : String){
        repo!!.readMessage(chatId)
    }


}