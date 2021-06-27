package com.arjental.dealdone.userinterface

import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arjental.dealdone.R
import com.arjental.dealdone.models.TaskItem
import com.arjental.dealdone.viewmodels.TasksFagmentViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

private const val TAG = "DealsFragment"

class TasksFragment : Fragment() {

    private lateinit var tasksRecyclerView: RecyclerView
    private val tasksAdapter by lazy { TasksAdapter() }
    private val tvm: TasksFagmentViewModel by lazy {
        ViewModelProvider(requireActivity()).get(TasksFagmentViewModel::class.java)
    }

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var addButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.deals_fragment, container, false)

        tasksRecyclerView = view?.findViewById(R.id.task_list_recycler) as RecyclerView
        setupRecycleView()

        toolbar = view.findViewById(R.id.deals_fragment_toolbar)
        toolbar.inflateMenu(R.menu.deals_fragment_toolbar_menu)
        toolbar.title = "asdasd"
        toolbar.subtitle = "mymy"

        addButton = view.findViewById(R.id.add_new_task_button)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addButton.setOnClickListener {
            findNavController().navigate(R.id.action_dealsFragment_to_newTaskFragment)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.deals_fragment_toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private inner class TasksAdapter : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {

        inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

//            val titleTextView: TextView = itemView.findViewById(R.id.stock_title)

            init {
                itemView.setOnClickListener {
//                    val ticker = stockListViewModel.stockList[adapterPosition]
//                    val action =
//                        StockListFragmentDirections.actionStockListFragmentToStockDetailsFragment(
//                            ticker
//                        )
//                    findNavController().navigate(action)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
            return TaskViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.tasks_recycler_layout,
                    parent,
                    false
                )
            )

        }

        override fun getItemCount(): Int {
            return tvm.recyclerList?.size ?: 0
        }

        override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
//            holder.titleTextView.text = stockListViewModel.stockList[position].title
//            holder.fullTitleTextView.text = stockListViewModel.stockList[position].fullTitle

        }

//        //Обновление только изменных элементов
//        fun setData(newList: List<Stock>) {
//
//            loadingSpinKit.isVisible = false
//            val oldList = stockListViewModel.stockList
//            val diffUtil = StockListDiffUtil(newList, oldList)
//            val diffResults = DiffUtil.calculateDiff(diffUtil)
//            stockListViewModel.stockList = newList
//            diffResults.dispatchUpdatesTo(this)
//        }
//
//        fun updateView(int: Int) {
//            notifyItemChanged(int)
//        }

        //Обновление всех элементов
        //Список избранного лагает из-за этого мептода, DiffUtil вызывает ошибку
        fun setDataWithRefresh(newList: List<TaskItem>) {
            tvm.recyclerList = newList
            notifyDataSetChanged()
        }



    }

    private fun setupRecycleView() {
        tasksRecyclerView.adapter = tasksAdapter
        tasksRecyclerView.layoutManager = LinearLayoutManager(context)
    }



}