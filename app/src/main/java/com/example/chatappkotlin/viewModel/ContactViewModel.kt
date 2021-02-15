package com.example.chatappkotlin.viewModel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.chatappkotlin.database.model.UserModel
import com.example.chatappkotlin.reprosotriy.ContactRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

open class ContactViewModel(application: Application) : AndroidViewModel(application) {

    private var context: Context? = null

    private var contactRepo = ContactRepo()
    var contactUser = MutableLiveData<ArrayList<UserModel>>()

    init {
        contactRepo = ContactRepo()

        context = getApplication<Application>().applicationContext

        contactUser = MutableLiveData(null)

        fetchUserData()

    }

    fun fetchUserData() {
        GlobalScope.launch(Dispatchers.IO) {
            contactRepo.getMobileContact(context!!)
        }
        contactUser = contactRepo.appContacts
    }

}