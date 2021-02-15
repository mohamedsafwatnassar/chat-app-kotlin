package com.example.chatappkotlin.fragments

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.chatappkotlin.R
import com.example.chatappkotlin.database.dao.UserDao
import com.example.chatappkotlin.database.model.UserModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.fragment_user_info.view.*

class UserInfoFragment : Fragment() {

    var userDao: UserDao = UserDao()
    private var userId: String? = null

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_user_info, container, false)

        activity!!.bottomChip.visibility = View.GONE

        userId = arguments!!.getString("userId")

        getUserData(userId)

        return view
    }

    private fun getUserData(userId: String?) {

        userDao.getUser(userId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userModel = snapshot.getValue(UserModel::class.java)

                        view!!.txtProfileName.text = userModel!!.name
                        view!!.txtProfileStatus.text = userModel.status
                        view!!.txtProfileFName.text = userModel.name
                        view!!.txtProfileNumber.text = userModel.number
                        val image: Uri = Uri.parse(userModel.image)
                        Glide.with(view!!.context).load(image).into(view!!.imgProfile)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            })
    }


}