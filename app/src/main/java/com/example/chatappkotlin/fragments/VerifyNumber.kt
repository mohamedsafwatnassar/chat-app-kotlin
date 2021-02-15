package com.example.chatappkotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chatappkotlin.R
import com.example.chatappkotlin.database.dao.UserDao
import com.example.chatappkotlin.database.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.fragment_verifiy_number.view.*


class VerifyNumber : Fragment() {

    private lateinit var pin: String
    private var code: String? = null

    var userDao: UserDao? = UserDao()

    private var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            code = it.getString("Code")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_verifiy_number, container, false)

        firebaseAuth = FirebaseAuth.getInstance()

        view.btnVerify.setOnClickListener {
            if (checkPin()) {
                val credential = PhoneAuthProvider.getCredential(code!!, pin)
                signInUser(credential)
            }
        }

        return view
    }

    private fun signInUser(credential: PhoneAuthCredential) {

        firebaseAuth!!.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = UserModel(
                    "", "", "",
                    firebaseAuth!!.currentUser!!.phoneNumber!!, firebaseAuth!!.uid!!
                )

                userDao!!.addUser(user)
                Hawk.put("CurrentUser", user)
                activity!!.supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.main_container, GetUserData())
                    .commit()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment VerifyNumber.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(code: String) = VerifyNumber().apply {
            arguments = Bundle().apply {
                putString("Code", code)
            }
        }
    }

    private fun checkPin(): Boolean {
        pin = view!!.otp_text_view.text.toString()
        if (pin.isEmpty()) {
            view!!.otp_text_view.error = "Filed is required"
            return false
        } else if (pin.length < 6) {
            view!!.otp_text_view.error = "Enter valid pin"
            return false
        } else return true
    }

}
