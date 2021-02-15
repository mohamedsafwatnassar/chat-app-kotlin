package com.example.chatappkotlin.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatappkotlin.Adapter.ChatAdapter
import com.example.chatappkotlin.R
import com.example.chatappkotlin.database.model.UserModel
import com.example.chatappkotlin.viewModel.ChatViewModel
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.fragment_chat.view.*

class ChatFragment : Fragment() {

    private lateinit var chatAdapter: ChatAdapter

    var user: UserModel? = UserModel()

    private lateinit var chatViewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // initialize ViewModel
        //chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        chatViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application)
            .create(ChatViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        activity!!.bottomChip.setItemSelected(R.id.btnChat)

        user = Hawk.get("CurrentUser")

        // get data from view model
        subscribeToLiveData()

        return view
    }

    private fun subscribeToLiveData() {
        chatViewModel.chatMutable.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                view!!.recyclerViewChat?.apply {
                    layoutManager = LinearLayoutManager(context)
                    chatAdapter = ChatAdapter(it)
                    setHasFixedSize(true)
                    adapter = chatAdapter
                }
            }
        })


    }


}