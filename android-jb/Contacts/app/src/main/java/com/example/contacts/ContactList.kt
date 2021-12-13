package com.example.contacts

import android.content.Context
import android.os.Parcelable
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.contact_form.view.*

@Parcelize
data class Contact(val name: String, val phoneNumber: String) : Parcelable

fun contactContains(searchStr: String, contact: Contact) : Boolean {
    return contact.name.contains(searchStr, ignoreCase = true) || contact.phoneNumber.contains(searchStr, ignoreCase = true)
}

class ContactAdapter(var contacts: List<Contact>, val onClick: (Contact) -> Unit) : RecyclerView.Adapter<ContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ContactViewHolder {
        val holder = ContactViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.contact_form, parent, false)
        )
        holder.root.setOnClickListener {
            onClick(contacts[holder.adapterPosition])
        }
        return holder
    }

    override fun getItemCount() = contacts.size

    fun update(newContacts : List<Contact>) {
        contacts = newContacts
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        holder: ContactViewHolder,
        position: Int
    ) = holder.bind(contacts[position])

    inner class ContactViewHolder(val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(contact: Contact) {
            with(root) {
                name.text = contact.name
                number.text = contact.phoneNumber
            }
        }
    }
}

fun Context.fetchAllContacts(): List<Contact> {
    contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        .use { cursor ->
            if (cursor == null) return emptyList()
            val builder = ArrayList<Contact>()
            while (cursor.moveToNext()) {
                val name =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)) ?: "N/A"
                val phoneNumber =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) ?: "N/A"

                builder.add(Contact(name, phoneNumber))
            }
            return builder
        }
}