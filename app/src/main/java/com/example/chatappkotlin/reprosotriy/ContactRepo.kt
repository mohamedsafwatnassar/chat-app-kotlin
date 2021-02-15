package com.example.chatappkotlin.reprosotriy

import android.content.Context
import android.provider.ContactsContract
import androidx.lifecycle.MutableLiveData
import com.example.chatappkotlin.database.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.orhanobut.hawk.Hawk

class ContactRepo {

    var appContacts: MutableLiveData<ArrayList<UserModel>> = MutableLiveData()

    var appList: ArrayList<UserModel>? = null
    var mobileList: ArrayList<UserModel>? = null
    var userModel: UserModel? = null
    var phoneNumber: String? = null

    fun getMobileContact(context: Context) {

        val projection = arrayOf(
            ContactsContract.Data.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )

        val contentResolver = context!!.contentResolver
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            projection,
            null,
            null,
            null
        )

        if (cursor != null) {
            mobileList = ArrayList()
            while (cursor.moveToNext()) {

                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                var number =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                number = number.replace("\\s".toRegex(), "")
                val num = number.elementAt(0).toString()
                if (num == "0")
                    number = number.replaceFirst("(?:0)+".toRegex(), "+20")
                val userModel = UserModel()
                userModel.name = name
                userModel.number = number

                mobileList!!.add(userModel)

            }
            cursor.close()
            getAppContact(mobileList!!)
        }

    }

    fun getAppContact(mobileContact: ArrayList<UserModel>) {

        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        val query = databaseReference.orderByChild("number")

        userModel = Hawk.get("CurrentUser")
        phoneNumber = userModel!!.number

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    appList = ArrayList()

                    for (data in snapshot.children) {
                        val number = data.child("number").value.toString()
                        for (mobileModel in mobileContact) {
                            if (mobileModel.number == number && number != phoneNumber) {
                                val user = data.getValue(UserModel::class.java)
                                appList!!.add(user!!)
                            }
                        }
                    }

                    appContacts.value = appList
                }
            }

            override fun onCancelled(error: DatabaseError) {
                //TODO("Not yet implemented")
            }
        })

    }
}