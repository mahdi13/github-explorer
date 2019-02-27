package com.perfect.githubexplorer

import androidx.appcompat.widget.SearchView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.rule.ActivityTestRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.*
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import android.widget.EditText
import android.widget.TextView
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.espresso.intent.rule.IntentsTestRule
import com.perfect.githubexplorer.ui.RepositoryViewHolder
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import com.google.android.material.chip.Chip
import org.hamcrest.Matcher


@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityInstrumentedTest {

    @get:Rule
    var activityRule: IntentsTestRule<MainActivity> = IntentsTestRule(MainActivity::class.java)

    @Test
    fun testRepositoryItemClick() {

        onView(anyOf(instanceOf(EditText::class.java)))
            .perform(clearText(), typeText("Abc"), pressImeActionButton())

        Thread.sleep(3000)

        onView(withId(R.id.list))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RepositoryViewHolder>(3, click()))


        intended(hasComponent(RepositoryActivity::class.java.name))
        intended(hasExtraWithKey("id"))

    }

    @Test
    fun testRepositoryUserItemClick() {

        onView(anyOf(instanceOf(EditText::class.java)))
            .perform(clearText(), typeText("Abc"), pressImeActionButton())

        Thread.sleep(3000)

        onView(withId(R.id.list))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    3,
                    object : ViewAction {
                        override fun getDescription(): String? = null
                        override fun getConstraints(): Matcher<View>? = null

                        override fun perform(uiController: UiController, view: View) {
                            view.findViewById<Chip>(R.id.user).performClick()
                        }

                    })
            )

        intended(hasComponent(UserProfileActivity::class.java.name))
        intended(hasExtraWithKey("username"))

    }
}
