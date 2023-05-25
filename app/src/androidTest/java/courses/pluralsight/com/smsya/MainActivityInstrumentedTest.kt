package courses.pluralsight.com.smsya

import android.app.Activity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityInstrumentedTest {
    @Rule @JvmField
    val mMainActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun showAllAdressbooks_success(){
        //make sure the first fragment is beign displayed
        onView(withId(R.id.textView)).check(matches(isDisplayed()))

        //click the contacts button
        onView(withId(R.id.btn_contacts)).perform(click())

        //check if the addressbooks recylerview is displayed
        onView(withId(R.id.contactListsRecyclerView)).check(matches(isDisplayed()))

    }
}