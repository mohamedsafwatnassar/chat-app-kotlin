package com.example.chatmekotlin.Adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatappkotlin.R
import com.example.chatappkotlin.database.model.UserModel
import com.example.chatappkotlin.fragments.MessageFragment
import com.example.chatappkotlin.fragments.UserInfoFragment
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.contact_item_layout.view.*
import java.util.*
import kotlin.collections.ArrayList

class ContactAdapter(private var appContacts:ArrayList<UserModel>) :
    RecyclerView.Adapter<ContactAdapter.ContactViewHolder>(), Filterable {

    private var allContact: ArrayList<UserModel> = appContacts

    inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {

            itemView.imgContact.setOnClickListener {
                val position = adapterPosition
                val currentUser = allContact[position]

                val bundle = Bundle()
                bundle.putString("userId", currentUser.uID)

                val toFragment: Fragment = UserInfoFragment()
                toFragment.arguments = bundle

                val activity = it.context as AppCompatActivity
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.dashboardContainer, toFragment)
                    .addToBackStack(null)
                    .commit()
            }

            itemView.setOnClickListener {
                val position = adapterPosition
                val currentUser = allContact[position]

                Hawk.put("ClickedUser", currentUser)

                val activity = it.context as AppCompatActivity
                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.dashboardContainer, MessageFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.contact_item_layout, parent,false))
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {

        val user = allContact[position]

        holder.itemView.txtContactName.text = user.name
        holder.itemView.txtContactStatus.text = user.status

        Glide.with(holder.itemView.context).load(user.image).into(holder.itemView.imgContactUserInfo)

    }

    override fun getItemCount(): Int {
        return allContact.size
    }

    override fun getFilter(): Filter {

        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val searchContent = constraint.toString()
                if (searchContent.isEmpty())
                    allContact = appContacts
                else {
                    val filterContact = ArrayList<UserModel>()
                    for (user in allContact) {
                        if (user.name!!.toLowerCase(Locale.ROOT).trim()
                                .contains(searchContent.toLowerCase(Locale.ROOT).trim())
                        )
                            filterContact.add(user)
                    }
                    allContact = filterContact
                }

                val filterResults = FilterResults()
                filterResults.values = allContact
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                allContact = results?.values as ArrayList<UserModel>
                notifyDataSetChanged()

            }
        }
    }
}