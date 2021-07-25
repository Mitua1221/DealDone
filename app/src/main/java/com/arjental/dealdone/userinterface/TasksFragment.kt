package com.arjental.dealdone.userinterface

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.arjental.dealdone.DealDoneApplication
import com.arjental.dealdone.R
import com.arjental.dealdone.Translator
import com.arjental.dealdone.exceptions.ExceptionsHandler
import com.arjental.dealdone.recycler.delegates.*
import com.arjental.dealdone.recycler.delegates.interfaces.Delegate
import com.arjental.dealdone.models.ItemState
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.recycler.SwipeToDeleteCallback
import com.arjental.dealdone.recycler.TaskListDiffUtil
import com.arjental.dealdone.viewmodels.TasksListFragmentViewModel
import com.arjental.dealdone.viewmodels.ViewModelFactory
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject
import kotlin.math.abs

private const val TAG = "TasksFragment"

class TasksFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    lateinit var tvm: TasksListFragmentViewModel

    private lateinit var tasksRecyclerView: RecyclerView

    @Inject
    lateinit var translator: Translator

    @Inject
    lateinit var exceptionHandler: ExceptionsHandler

    private val taskAdapter by lazy {
        TasksAdapterDelegates(
            delegates = listOf(
                TaskItemDelegate(requireContext(), tvm),
                TaskItemDelegateWithTime(requireContext(), tvm),
                NewItemDelegate(requireContext()),
                TopDeviderDelegate(requireContext()),
                BottomDeviderDelegate(requireContext())
            )
        )
    }

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var collapsingToolbar: CollapsingToolbarLayout
    private lateinit var addButton: FloatingActionButton
    private lateinit var textviewToolbar: TextView
    private lateinit var appBarLayout: AppBarLayout
    private lateinit var hideImageButton: ImageButton
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as DealDoneApplication).appComponent.inject(this)
        tvm = ViewModelProvider(
            requireActivity(),
            viewModelFactory
        ).get(TasksListFragmentViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.tasks_list_fragment, container, false)

        toolbar = view.findViewById(R.id.deals_fragment_toolbar)
        hideImageButton = view.findViewById(R.id.image_button_visibility_deals)
        collapsingToolbar = view.findViewById(R.id.collapsing_toolbar_deals_fragment)
        textviewToolbar = view.findViewById(R.id.textview_toolbar_deals_fragment)
        swipeRefresh = view.findViewById(R.id.swipe_refresh)
        addButton = view.findViewById(R.id.add_new_task_button)
        tasksRecyclerView = view?.findViewById(R.id.task_list_recycler) as RecyclerView

        swipeRefresh.apply {
            setColorSchemeColors(
                ResourcesCompat.getColor(resources, R.color.color_light_blue, requireContext().theme),
                ResourcesCompat.getColor(resources, R.color.color_light_green, requireContext().theme),
                ResourcesCompat.getColor(resources, R.color.color_light_red, requireContext().theme),
            )
            setProgressBackgroundColorSchemeResource(
                R.color.back_light_primary
            )
        }

        textviewToolbar.text =
            resources.getString(R.string.my_tasks_done, tvm.unsolvedQuality().toString())

        appBarLayout = view.findViewById(R.id.appbarlayout_deals_fragment)

        swipeRefresh.setOnRefreshListener {
            tvm.updateTasks()
        }

        appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            when {
                abs(verticalOffset) >= appBarLayout.totalScrollRange -> {} // collapsed
                verticalOffset == 0 -> { // fully expand
                    textviewToolbar.visibility = View.VISIBLE
                }
                else -> { // scolling
                    if (textviewToolbar.visibility == View.VISIBLE) textviewToolbar.visibility =
                        View.GONE
                }
            }
        })

        collapsingToolbar.expandedTitleMarginStart = collapsingToolbar.expandedTitleMarginStart * 2

        collapsingToolbar.setCollapsedTitleTextAppearance(R.style.deals_bar_title_collapsed)
        collapsingToolbar.setExpandedTitleTextAppearance(R.style.deals_bar_title_expand)

        toolbar.title = getString(R.string.my_tasks)

        setupRecycleView()

        addButton.setOnClickListener {
            findNavController().navigate(R.id.action_dealsFragment_to_newTaskFragment)
        }

        toolbar.setOnClickListener {
            tasksRecyclerView.smoothScrollToPosition(0)
        }

        hideImageButton.setOnClickListener {
            if (tvm.isHidden) {
                hideImageButton.setImageResource(R.drawable.ic_visibility_on)
                tvm.setSortedListToPaste(false)
                tvm.isHidden = false
            } else {
                hideImageButton.setImageResource(R.drawable.ic_visibility_off)
                tvm.setSortedListToPaste(true)
                tvm.isHidden = true
                true
            }
        }

        toolbar.setOnClickListener {
            if (tvm.isHidden) {
                hideImageButton.setImageResource(R.drawable.ic_visibility_on)
                tvm.setSortedListToPaste(false)
                tvm.isHidden = false
            } else {
                hideImageButton.setImageResource(R.drawable.ic_visibility_off)
                tvm.setSortedListToPaste(true)
                tvm.isHidden = true
                true
            }
        }

        collapsingToolbar.setOnClickListener {
            if (tvm.isHidden) {
                hideImageButton.setImageResource(R.drawable.ic_visibility_on)
                tvm.setSortedListToPaste(false)
                tvm.isHidden = false
            } else {
                hideImageButton.setImageResource(R.drawable.ic_visibility_off)
                tvm.setSortedListToPaste(true)
                tvm.isHidden = true
                true
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvm.qualitySolvedChange.observe(viewLifecycleOwner, {
            textviewToolbar.text =
                resources.getString(R.string.my_tasks_done, tvm.unsolvedQuality().toString())
        })
        tvm.qualitySolvedChange.value = true

        tvm.tasksList.observe(viewLifecycleOwner, {
            if (tvm.isHidden) {
                tvm.setSortedListToPaste(true)
            } else {
                tvm.setSortedListToPaste(false)
            }
        })

        tvm.pasteList.observe(viewLifecycleOwner, {
            swipeRefresh.isRefreshing = false
            taskAdapter.setData(it)
        })

    }

    override fun onResume() {
        if (translator.editedTask.value != null) {
            when (translator.editedTask.value!!.state) {
                ItemState.DELETED -> {
                    tvm.deleteElement(translator.editedTask.value!!.copy())
                    translator.editedTask.value = null
                }
                ItemState.NEW -> {
                    tvm.addElement(translator.editedTask.value!!.copy())
                    translator.editedTask.value = null
                }
                ItemState.EXIST -> {
                    tvm.changeElement(translator.editedTask.value!!.copy())
                    translator.editedTask.value = null
                }
                else -> {
                    val item = translator.editedTask.value!!.copy()
                    translator.editedTask.value = null
                    exceptionHandler.illegalStateOfTask(TAG, item, item.state)
                }
            }
        }
        super.onResume()
    }

    private inner class TasksAdapterDelegates(
        private val delegates: List<Delegate>
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        override fun getItemViewType(position: Int): Int =
            delegates.indexOfFirst { delegate -> delegate.forItem(tvm.recyclerList[position]) }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            delegates[viewType].getViewHolder(parent)

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            delegates[getItemViewType(position)].bindViewHolder(holder, tvm.recyclerList[position])
        }

        fun removeAt(position: Int) {
            notifyItemRemoved(position)
            tvm.deleteTask(tvm.recyclerList[position])
            tvm.deleteElement(tvm.recyclerList[position])
            tvm.qualitySolvedChange.value = true
        }

        fun doneAt(position: Int) {
            notifyItemChanged(position)
            if (tvm.recyclerList[position].isSolved) {
                tvm.recyclerList[position].isSolved = false
                tvm.changeDone(tvm.recyclerList[position], false)
            } else {
                tvm.recyclerList[position].isSolved = true
                tvm.changeDone(tvm.recyclerList[position], true)
            }
            tvm.qualitySolvedChange.value = true
        }

        override fun getItemCount(): Int = tvm.recyclerList.size

        fun setData(newList: List<TaskItem>) {
            val oldList = tvm.recyclerList
            val diffUtil = TaskListDiffUtil(newList, oldList)
            val diffResults = DiffUtil.calculateDiff(diffUtil)
            tvm.recyclerList = newList
            diffResults.dispatchUpdatesTo(this)
        }
    }

    private fun setupRecycleView() {
        tasksRecyclerView.adapter = taskAdapter
        tasksRecyclerView.layoutManager = LinearLayoutManager(context)

        val swipeHandler = object : SwipeToDeleteCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = tasksRecyclerView.adapter as TasksAdapterDelegates
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        adapter.removeAt(viewHolder.absoluteAdapterPosition)
                    }
                    ItemTouchHelper.RIGHT -> {
                        adapter.doneAt(viewHolder.absoluteAdapterPosition)
                    }
                }


            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(tasksRecyclerView)

    }


}