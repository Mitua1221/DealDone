package com.arjental.dealdone.userinterface

import android.util.Log
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.arjental.dealdone.R
import com.arjental.dealdone.Translator
import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.models.TaskItemPriorities
import com.arjental.dealdone.viewmodels.EditTaskFragmentViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.Test
import org.mockito.kotlin.mock
import java.util.*
import org.mockito.kotlin.verify

class EditTaskFragmentTest {

    private val mockNavController : NavController = mock()
    private val translator = mockk<Translator>()
    private val emvmockk = mockk<EditTaskFragmentViewModel>()

    var editedTask: MutableLiveData<TaskItem> = MutableLiveData(null)
    val timeSelectedFromCalendar: MutableLiveData<Date?> = MutableLiveData(null)

    @Test
    fun add_new_task_ang_get_back_to_tasksfragment () {

        every { translator.editedTask } returns editedTask
        every { translator.timeSelectedFromCalendar } returns timeSelectedFromCalendar
        every { emvmockk.createNewTask() } returns sup()
        every { emvmockk.newTask } returns editedTask
        every { emvmockk.time() } returns time()
        every { emvmockk.updateOrAddTask() } returns Unit

        val firstScenario =
            launchFragmentInContainer<EditTaskFragment>(themeResId = R.style.Theme_DealDone,
            initialState = Lifecycle.State.CREATED)

        firstScenario.onFragment { fragment ->
            fragment.translator = translator
            fragment.evm = emvmockk
            firstScenario.moveToState(Lifecycle.State.STARTED)
            Navigation.setViewNavController(fragment.requireView(), mockNavController)

        }

        onView(ViewMatchers.withId(R.id.new_task_save)).perform(ViewActions.click())
        verify { emvmockk.createNewTask() }
        verify { emvmockk.time() }
        verify { emvmockk.updateOrAddTask() }
        verify(mockNavController).popBackStack()

    }

    fun sup() {
        runBlocking {
            withContext(Dispatchers.Main) {
                editedTask = MutableLiveData( TaskItem(
                    id = UUID.randomUUID(),
                    isSolved = false,
                    text = "UUUUUUUUUUU",
                    priority = TaskItemPriorities.NONE,
                    deadline = null,
                    state = ItemState.NEW,
                    createDate = time(),
                    updateDate = time(),
                ))
            }
        }
        return Unit
    }

    fun time() = Calendar.getInstance().time.time


}