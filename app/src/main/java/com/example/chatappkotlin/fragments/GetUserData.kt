package com.example.chatappkotlin.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import androidx.fragment.app.Fragment
import com.example.chatappkotlin.R
import com.example.chatappkotlin.activity.DashBoardActivity
import com.example.chatappkotlin.database.dao.UserDao
import com.example.chatappkotlin.database.model.UserModel
import com.example.chatmekotlin.Constants.AppConstants
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.orhanobut.hawk.Hawk
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.fragment_get_user_data.view.*


class GetUserData : Fragment() {

    var username: String? = null
    var userStatus: String? = null
    var imageUri: String? = null
    var userImage: Uri? = null

    var userDao: UserDao? = UserDao()
    var firebaseAuth: FirebaseAuth? = null
    var storageReference: StorageReference? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_get_user_data, container, false)

        firebaseAuth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        setData(view)

        return view
    }

    private fun setData(view: View?) {
        view!!.imgPickImage.setOnClickListener {
            if (checkStoragePermission()) {
                pickImage()
            } else {
                storageRequestPermission()
            }
        }

        view.btnDataDone.setOnClickListener {
            if (checkData()) {
                uploadData(username!!, userStatus!!, userImage!!)
            }
        }
    }

    // upload user name, user status and user image in realtime Database
    private fun uploadData(username: String, userStatus: String, userImage: Uri) = kotlin.run {

        storageReference!!.child(firebaseAuth!!.uid + AppConstants.PATH)
            .putFile(userImage).addOnSuccessListener {
                val task = it.storage.downloadUrl
                task.addOnCompleteListener { uri ->
                    imageUri = uri.result.toString()

                    val map = mapOf(
                        "name" to username,
                        "status" to userStatus,
                        "image" to imageUri,
                    )

                    userDao!!.updateChildrenUser(map, firebaseAuth, OnCompleteListener {
                        userDao!!.getUser(firebaseAuth).addValueEventListener(object :
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val user = snapshot.getValue(UserModel::class.java)
                                Hawk.put("CurrentUser", user)
                                //Log.d("GetUSer", "" + user!!.uID)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context, "" + error.message, Toast.LENGTH_SHORT).show()
                                Log.e("DatabaseError",error.message.toString())
                            }
                        })
                    })
                    startActivity(Intent(context, DashBoardActivity::class.java))
                    activity!!.finish()
                }
            }
    }

    // check if take that permission of gallery or No take
    private fun checkStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context!!,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    // take permission of gallery
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            10001 ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    pickImage()
                else Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    userImage = result.uri
                    view!!.imgUser.setImageURI(userImage)
                }
            }
        }
    }

    // pick image from gallery and make crop this select image
    private fun pickImage() {
        CropImage.activity()
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(context!!, this)
    }

    private fun checkData(): Boolean {

        username = view!!.edtUserName.text.toString().trim()
        userStatus = view!!.edtUserStatus.text.toString().trim()

        if (username!!.isEmpty()) {
            view!!.edtUserName.error = "Filed is required"
            return false
        } else if (userStatus!!.isEmpty()) {
            view!!.edtUserStatus.error = "Filed is required"
            return false
        } else if (userImage!! == null) {
            Toast.makeText(context, "Image required", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun storageRequestPermission() = ActivityCompat.requestPermissions(
        activity!!, arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ), 10001
    )



}