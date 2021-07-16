package com.arjental.dealdone.userinterface

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.arjental.dealdone.DealDoneApplication
import com.arjental.dealdone.R
import com.arjental.dealdone.Translator
import com.arjental.dealdone.di.factories.viewmodelfactory.ViewModelFactory
import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItemPriorities
import com.arjental.dealdone.viewmodels.EditTaskViewModel
import java.text.DateFormat
import java.util.*
import javax.inject.Inject

private const val TAG = "NewTaskFragment"
private const val DIALOG_DATE = "DialogDate"
private const val REQUEST_DATE = 0

class NewTaskFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var evm: EditTaskViewModel

    private lateinit var descriptionEditText: EditText
    private lateinit var closeButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private lateinit var saveButton: Button
    private lateinit var dateSwitch: SwitchCompat
    private lateinit var deadlineTextView: TextView
    private lateinit var importanceTextView: TextView
    private lateinit var importancePreviewTextView: TextView

    @Inject lateinit var translator: Translator

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as DealDoneApplication).appComponent.inject(this)
        evm = ViewModelProvider(requireActivity(), viewModelFactory).get(EditTaskViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (translator.editedTask.value != null) evm.collectTaskFromTranslator() else evm.createNewTask()
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.new_task_fragment, container, false)

        descriptionEditText = view.findViewById(R.id.new_task_edit_text)
        closeButton = view.findViewById(R.id.new_task_close)
        saveButton = view.findViewById(R.id.new_task_save)
        dateSwitch = view.findViewById(R.id.new_task_time_switch)
        deadlineTextView = view.findViewById(R.id.new_task_deadline_date)
        importanceTextView = view.findViewById(R.id.new_task_importance)
        importancePreviewTextView = view.findViewById(R.id.new_task_importance_show)
        deleteButton = view.findViewById(R.id.delete_button_edit_task)

        descriptionEditText.setText(evm.newTask.value?.text)
        deadlineTextView.text =
            if (evm.newTask.value?.deadline != null) {
                val dateTimeFormat: DateFormat =
                    DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
                dateTimeFormat.format(Date(evm.newTask.value?.deadline!!))
            } else getString(
                R.string.deadline_not_set
            )
        importancePreviewTextView.text = when (evm.newTask.value?.priority) {
            TaskItemPriorities.NONE -> {
                getString(R.string.importance_none)
            }
            TaskItemPriorities.MEDIUM -> {
                getString(R.string.importance_low)
            }
            TaskItemPriorities.HIGH -> {
                importancePreviewTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.color_light_red, requireContext().theme))
                getString(R.string.importance_high)
            }
            else -> throw IllegalStateException("Illegal priority of Task")
        }

        closeButton.setOnClickListener {
            translator.editedTask.value = null
            evm.newTask.value = null
            NavHostFragment.findNavController(this).popBackStack()
        }

        saveButton.setOnClickListener {
            evm.newTask.value?.text = descriptionEditText.text.toString()
            evm.newTask.value?.updateDate = evm.time()
            translator.editedTask.value = evm.newTask.value?.copy()
            evm.updateOrAddTask()
            evm.newTask.value = null
            NavHostFragment.findNavController(this).popBackStack()
        }

        deleteButton.setOnClickListener {
            if (evm.newTask.value?.state == ItemState.NEW) {
                evm.newTask.value = null
            } else {
                translator.editedTask.value = evm.newTask.value?.copy(state = ItemState.DELETED)
                evm.deleteTask()
                evm.newTask.value = null
                NavHostFragment.findNavController(this).popBackStack()
            }
        }

        dateSwitch.isChecked = evm.newTask.value?.deadline != null
        dateSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                DatePickerFragment.newInstance(
                    if (evm.newTask.value?.deadline == null) {
                        Calendar.getInstance().time
                    } else {
                        Date(evm.newTask.value?.deadline!!)
                    }
                ).apply {
                    show(this@NewTaskFragment.childFragmentManager, DIALOG_DATE)
                }
            } else {
                evm.newTask.value?.deadline = null
                deadlineTextView.text = getString(R.string.deadline_not_set)
            }
        }

        importanceTextView.setOnClickListener {
            val popup = PopupMenu(requireContext(), it)
            popup.inflate(R.menu.new_task_importance_menu)
            popup.show()
            popup.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.low_prior_menu -> {
                        evm.newTask.value?.priority = TaskItemPriorities.NONE
                        importancePreviewTextView.text = getString(R.string.importance_none)
                        importancePreviewTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.label_light_tertiary, requireContext().theme))
                        true
                    }
                    R.id.medium_prior_menu -> {
                        evm.newTask.value?.priority = TaskItemPriorities.MEDIUM
                        importancePreviewTextView.text = getString(R.string.importance_low)
                        importancePreviewTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.label_light_tertiary, requireContext().theme))
                        true
                    }
                    else -> {
                        evm.newTask.value?.priority = TaskItemPriorities.HIGH
                        importancePreviewTextView.text = getString(R.string.importance_high)
                        importancePreviewTextView.setTextColor(ResourcesCompat.getColor(resources, R.color.color_light_red, requireContext().theme))
                        true
                    }
                }
            }

        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        translator.timeSelectedFromCalendar.observe(viewLifecycleOwner, {

            if (it != null) {
                evm.newTask.value?.deadline = it.time

                val dateTimeFormat: DateFormat =
                    DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
                deadlineTextView.text = dateTimeFormat.format(Date(it.time))
                translator.timeSelectedFromCalendar.value = null
            } else {
                if (evm.newTask.value?.deadline == null) {
                    dateSwitch.isChecked = false
                }
            }


        })

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {

        super.onResume()
    }

}