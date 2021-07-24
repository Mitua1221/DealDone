package com.arjental.dealdone.userinterface

import android.util.Log
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import com.arjental.dealdone.R
import io.mockk.mockk
import org.junit.Assert.*
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify


class TasksFragmentTest {

    private val mockNavController : NavController = mock()
    private val log = mockk<Log>()

    @Test
    fun press_on_floatingbutton_on_tasksfragment_and_open_newtaskfragment () {

        val firstScenario = launchFragmentInContainer<TasksFragment>(themeResId = R.style.Theme_DealDone)

        firstScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)

        }

        onView(ViewMatchers.withId(R.id.add_new_task_button)).perform(ViewActions.click())
        verify(mockNavController).navigate(R.id.action_dealsFragment_to_newTaskFragment)
    }

}