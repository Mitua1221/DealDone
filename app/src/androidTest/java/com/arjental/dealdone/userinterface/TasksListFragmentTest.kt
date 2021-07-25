package com.arjental.dealdone.userinterface

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.arjental.dealdone.R
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify


class TasksListFragmentTest {

    private val mockNavController = mock<NavController>()

    @Test
    fun press_on_floatingButton_on_tasksListFragment_and_open_editTaskFragment () {

        val firstScenario = launchFragmentInContainer<TasksFragment>(themeResId = R.style.Theme_DealDone)

        firstScenario.onFragment { fragment ->
            Navigation.setViewNavController(fragment.requireView(), mockNavController)

        }

        onView(ViewMatchers.withId(R.id.add_new_task_button)).perform(ViewActions.click())
        verify(mockNavController).navigate(R.id.action_dealsFragment_to_newTaskFragment)
    }

}