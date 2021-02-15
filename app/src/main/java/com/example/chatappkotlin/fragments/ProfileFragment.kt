package com.example.chatappkotlin.fragments

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.chatappkotlin.R
import com.example.chatappkotlin.database.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.dialog_layout.view.*
import kotlinx.android.synthetic.main.fragment_profile.view.*

class ProfileFragment : Fragment() {

    private var userModel = UserModel()

    private lateinit var dialog: AlertDialog

    var firebaseAuth: FirebaseAuth? = null
    var databaseReference: DatabaseReference? = null
    var storageReference: StorageReference? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        activity!!.bottomChip.setItemSelected(R.id.btnProfile)

        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        storageReference = FirebaseStorage.getInstance().reference

        getUserData(view!!)

        clickOfData(view)

        return view
    }

    private fun clickOfData(view: View) {
        view!!.imgEdit.setOnClickListener {
            getEditDialog()
        }

        view!!.deleteCard.setOnClickListener {
            //firebaseAuth!!.signOut()

        }
    }

    private fun getEditDialog() {
        val alertDialog = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null, false)
        alertDialog.setView(view)

        // set Data
        view.edtName.setText(userModel.name)
        view.edtStatus.setText(userModel.status)

        view.btnEdit.setOnClickListener {

            // get Data
            val name = view.edtName.text.toString()
            val status = view.edtStatus.text.toString()

            if (name.isNotEmpty() && status.isNotEmpty()) {
                updateNameAndStatus(name, status)
                update()
                dialog.dismiss()
            }
        }
        dialog = alertDialog.create()
        dialog.show()

    }

    private fun updateNameAndStatus(name: String, status: String) {
        Toast.makeText(context, "edit is successfully", Toast.LENGTH_SHORT).show()

        val map = mapOf(
            "name" to name,
            "status" to status
        )

        databaseReference!!.child(firebaseAuth!!.uid!!).updateChildren(map)

        userModel.name = map.get("name")!!
        userModel.status = map.get("status")!!
        Hawk.put("CurrentUser", userModel)

    }

    private fun update() {
        userModel = Hawk.get("CurrentUser")

        view!!.txtProfileName.text = userModel.name
        view!!.txtProfileFName.text = userModel.name
        view!!.txtProfileStatus.text = userModel.status
    }

    private fun getUserData(view: View) {

        userModel = Hawk.get("CurrentUser")

        view.txtProfileName.text = userModel.name
        view.txtProfileStatus.text = userModel.status
        view.txtProfileFName.text = userModel.name
        view.txtProfileNumber.text = userModel.number
        val image: Uri = Uri.parse(userModel.image)
        Glide.with(view.context).load(image).into(view.imgProfile)

    }

}