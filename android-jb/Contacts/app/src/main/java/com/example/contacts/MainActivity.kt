package com.example.contacts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        const val CONTACTSFOUND = "CONTACTSFOUND"
    }

    var contacts = listOf<Contact>()
    var contactsFound = false
    var myRequestId = 123

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                myRequestId)
        }

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED) {
            contacts = this.fetchAllContacts().sortedBy { it.name }
            if (!contactsFound) {
                toastContacts()
                contactsFound = true
            }
            val viewManager = LinearLayoutManager(this)
            ContactsRecyclerView.apply {
                layoutManager = viewManager
                adapter = ContactAdapter(contacts) {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${it.phoneNumber}"))
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                }
            }
            ContactsRecyclerView.setHasFixedSize(true)
        }

        val search = findViewById<EditText>(R.id.search_edit_text)
        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateRecyclerView(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            myRequestId -> {
                if (!(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(this,
                        R.string.no_contacts,
                        Toast.LENGTH_LONG).show()
                } else {
                    contacts = this.fetchAllContacts().sortedBy { it.name }
                    if (!contactsFound) {
                        toastContacts()
                        contactsFound = true
                    }
                    val viewManager = LinearLayoutManager(this)
                    ContactsRecyclerView.apply {
                        layoutManager = viewManager
                        adapter = ContactAdapter(contacts) {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${it.phoneNumber}"))
                            if (intent.resolveActivity(packageManager) != null) {
                                startActivity(intent)
                            }
                        }
                    }
                    ContactsRecyclerView.setHasFixedSize(true)
                }
                return
            }
        }
    }

    fun updateRecyclerView(searchStr : String) {
        val list = mutableListOf<Contact>()
        contacts.forEach {
            if (contactContains(searchStr, it)) {
                list.add(it)
            }
        }
        (ContactsRecyclerView.adapter as ContactAdapter).update(list)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(CONTACTSFOUND, contactsFound)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        contactsFound = savedInstanceState.getBoolean(CONTACTSFOUND)
    }

    fun toastContacts() {
        Toast.makeText(
            this,
            resources.getQuantityString(
                R.plurals.num_of_contacts,
                contacts.size,
                contacts.size
            ),
            Toast.LENGTH_LONG
        ).show()
    }
}