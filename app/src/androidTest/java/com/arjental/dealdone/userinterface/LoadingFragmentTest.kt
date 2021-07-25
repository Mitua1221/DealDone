package com.arjental.dealdone.userinterface

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arjental.dealdone.R
import com.arjental.dealdone.Translator
import com.arjental.dealdone.models.TaskItem
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify


@RunWith(AndroidJUnit4::class)
class LoadingFragmentTest {

    val mockOfTranslator = mockk<Translator>()

    val flow = MutableSharedFlow<List<TaskItem>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    var testScope =
        CoroutineScope(Dispatchers.IO + CoroutineName("ActualizationScope"))

    private val mockNavController : NavController = mock()

    @Test
    fun loadingFragment_navigating_to_dealsFragment_after_emit_in_actualTaskList() {

        every { mockOfTranslator.actualTaskList } returns flow

        val firstScenario = launchFragmentInContainer<LoadingFragment>(initialState = Lifecycle.State.CREATED)

        firstScenario.onFragment { fragment ->
            fragment.translator = mockOfTranslator
            fragment.waitForLoadingScope = testScope
            firstScenario.moveToState(Lifecycle.State.STARTED)
            Navigation.setViewNavController(fragment.requireView(), mockNavController)

            runBlocking {
                flow.emit(emptyList())
                delay(30)
            }
        }

        onView(withId(R.id.spin_kit)).check(matches(isDisplayed()))
        verify(mockNavController).navigate(R.id.action_loadingFragment_to_dealsFragment)

    }

}