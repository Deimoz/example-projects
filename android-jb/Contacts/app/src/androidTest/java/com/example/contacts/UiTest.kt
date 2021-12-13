package com.example.contacts

import android.widget.EditText
import androidx.test.InstrumentationRegistry.getInstrumentation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UiTest {
    @get:Rule
    public var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun normalBehavior() {
        val device: UiDevice = UiDevice.getInstance(getInstrumentation())
        device.setOrientationLeft()
        val searchBarText = device.findObject(UiSelector().className("android.widget.EditText"))
        searchBarText.text = "123"
        device.setOrientationNatural()
        device.waitForIdle(1000)
        device.pressHome()
    }


}