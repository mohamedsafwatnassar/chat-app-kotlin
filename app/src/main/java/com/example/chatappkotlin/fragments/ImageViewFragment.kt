package com.example.chatappkotlin.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.chatappkotlin.R
import com.example.chatappkotlin.database.model.UserModel
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.fragment_image_view.view.*

class ImageViewFragment : Fragment() {

    private var clickedUser: UserModel? = Hawk.get("ClickedUser")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_image_view, container, false)

        val image: Uri = Uri.parse(clickedUser!!.image)
        Glide.with(view!!.context).load(image).into(view.imageUser)

        return view
    }

}