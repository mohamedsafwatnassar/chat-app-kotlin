package com.example.chatappkotlin.database.dao

import com.example.chatappkotlin.database.RealtimeDatabase
import com.example.chatappkotlin.database.model.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class UserDao {

    var realtimeDatabase: RealtimeDatabase =  RealtimeDatabase()

    fun addUser(user: UserModel) {
        realtimeDatabase.getUserRef()
            .child(user.uID)
            .setValue(user)
    }

    fun updateChildrenUser(
        map: Map<String, String?>, firebaseAuth: FirebaseAuth?,
        onCompleteListener: OnCompleteListener<Void>
    ) {
        realtimeDatabase.getUserRef()
            .child(firebaseAuth!!.uid!!)
            .updateChildren(map)
            .addOnCompleteListener(onCompleteListener)
    }

    fun getUser(auth : FirebaseAuth? ) : DatabaseReference {
        val reference = realtimeDatabase.getUserRef().child(auth!!.uid!!)

        reference.keepSynced(true)
        return reference
    }

    fun typingStatus(myId: String, map: Map<String, String>){
        realtimeDatabase.getUserRef().child(myId)

            .updateChildren(map)
    }

    fun updateOnlineStatus(myId: String, map: Map<String, String>){
        realtimeDatabase.getUserRef().child(myId)

            .updateChildren(map)
    }

    fun getUser(hisId: String) : DatabaseReference {
        val reference = realtimeDatabase.getUserRef().child(hisId)

        reference.keepSynced(true)
        return reference
    }

    fun createToken(hisId: String, map: Map<String, String?>) {
        realtimeDatabase.getUserRef().child(hisId)
            .updateChildren(map)

    }
}