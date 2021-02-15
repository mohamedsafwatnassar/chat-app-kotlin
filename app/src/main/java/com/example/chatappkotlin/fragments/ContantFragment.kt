package com.example.chatappkotlin.fragments

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatappkotlin.R
import com.example.chatappkotlin.database.model.UserModel
import com.example.chatappkotlin.viewModel.ChatViewModel
import com.example.chatappkotlin.viewModel.ContactViewModel
import com.example.chatmekotlin.Adapter.ContactAdapter
import com.example.chatmekotlin.Constants.AppConstants
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.activity_dash_board.*
import kotlinx.android.synthetic.main.fragment_contact.view.*

class ContantFragment : Fragment() {

    private var contactViewModel: ContactViewModel? = null
    private var contactAdapter: ContactAdapter? = null
    var contatcts: ArrayList<UserModel>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contact, container, false)

        activity!!.bottomChip.setItemSelected(R.id.btnContact)

        // initialize ViewModel
        contactViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(activity!!.application)
            .create(ContactViewModel::class.java)

        // get data from view model
        subscribeToLiveData(view)

        // save contact in case no Internet
        cacheContact(view)

        // search to contact user
        searchContact(view)

        return view
    }

    private fun subscribeToLiveData(view: View) {
        // get user contact in case allow permission and save user phone in phone contact of you
        contactViewModel!!.contactUser.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                Hawk.put("Contacts", it)
                view.recyclerViewContact?.apply {
                    layoutManager = LinearLayoutManager(context)
                    setHasFixedSize(true)
                    contactAdapter = ContactAdapter(it)
                    adapter = contactAdapter
                }
            }
        })

    }

    // search about specific contact
    private fun searchContact(view: View) {
        view.contactSearchView!!.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (contactAdapter != null)
                    contactAdapter!!.filter.filter(newText)
                return false
            }
        })
    }

    // save contact in case no found internet
    private fun cacheContact(view: View) {
        contatcts = ArrayList()
        contatcts = Hawk.get("Contacts")
        if (contatcts != null) {
            view.recyclerViewContact?.apply {
                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)
                contactAdapter = ContactAdapter(contatcts!!)
                adapter = contactAdapter
            }
        }
    }

    // take permission of contact
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            AppConstants.CONTACT_PERMISSION -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    contactViewModel!!.fetchUserData()
                } else {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


}