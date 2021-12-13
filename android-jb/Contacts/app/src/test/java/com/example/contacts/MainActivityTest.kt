package com.example.contacts

import android.app.Activity
import android.content.pm.ActivityInfo
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(sdk = [Config.OLDEST_SDK])
@RunWith(RobolectricTestRunner::class)
class MainActivityTest {
    private var activity: Activity? = null
    private var firstContact = Contact("FirstName", "12345678901")
    private var secondContact = Contact("SecondName", "88005553535")
    private var thirdContact = Contact("ThirdName", "87541344224")
    private var contacts = listOf<Contact>(
        firstContact,
        secondContact,
        thirdContact)

    @Before
    fun setUp() {
        activity = Robolectric.buildActivity(MainActivity::class.java).create().get()
        (activity as MainActivity).contacts = contacts
        val recyclerView = activity?.findViewById<RecyclerView>(R.id.ContactsRecyclerView)
        if (recyclerView != null) {
            (recyclerView.adapter as ContactAdapter).update(contacts)
        }
    }

    @Test
    fun activityNotNull() {
        assertNotNull(activity)
    }

    @Test
    fun recyclerNotNull() {
        assertNotNull(activity?.findViewById<RecyclerView>(R.id.ContactsRecyclerView))
    }

    @Test
    fun searchBarNotNull() {
        assertNotNull(activity?.findViewById(R.id.search_edit_text))
    }

    @Test
    fun searchFirstByName() {
        setSearchText("First")
        checkRecycler(listOf(firstContact))
    }

    @Test
    fun searchSecondByNumber() {
        setSearchText("8800")
        checkRecycler(listOf(secondContact))
    }

    @Test
    fun searchThirdInUpperCase() {
        setSearchText("THIRD")
        checkRecycler(listOf(thirdContact))
    }


    @Test
    fun searchEmptyName() {
        setSearchText("")
        checkRecycler(contacts)
    }

    @Test
    fun searchNotExistingPerson() {
        setSearchText("NotExisting")
        checkRecycler(listOf())
    }

    @Test
    fun activityRotation() {
        setSearchText("123")
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        assertEquals("123", currentSearchText())
    }

    @Test
    fun veryLongSearch() {
        var str = ""
        for (i in 1..1000) {
            str += "a"
        }
        setSearchText(str)
    }

    private fun setSearchText(str: String) {
        activity?.findViewById<EditText>(R.id.search_edit_text)?.setText(str)
    }

    private fun currentSearchText() : String {
        val text = activity?.findViewById<EditText>(R.id.search_edit_text)
        if (text != null) {
            return text.text.toString()
        }
        return ""
    }

    private fun checkRecycler(list : List<Contact>) {
        val recyclerView = activity?.findViewById<RecyclerView>(R.id.ContactsRecyclerView)
        if (recyclerView != null) {
            assertEquals(list, (recyclerView.adapter as ContactAdapter).contacts)
        }
    }
}