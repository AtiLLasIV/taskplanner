package com.hwmipt.planner.ui.tasks

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hwmipt.planner.storage.TaskModel
import com.hwmipt.planner.viewmodel.TasksViewModel

class TasksListFragment(
    private val isDone: Boolean,
    private val selectedTags: Set<String>,
    private val onToggleDone: (TaskModel) -> Unit,
    private val onClick: (TaskModel) -> Unit = {}
) : Fragment() {

    private lateinit var adapter: TasksAdapter
    private lateinit var viewModel: TasksViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val recyclerView = RecyclerView(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = TasksAdapter(onClick, onToggleDone)
        recyclerView.adapter = adapter

        return recyclerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[TasksViewModel::class.java]

        viewModel.tasks.observe(viewLifecycleOwner) { allTasks ->
            val prefs = requireContext().getSharedPreferences("app_settings", android.content.Context.MODE_PRIVATE)
            val sortByUrgency = prefs.getBoolean("urgency_sort", false)

            val filtered = allTasks
                .filter { it.isDone == isDone }
                .filter { selectedTags.isEmpty() || it.tags.any { tag -> tag in selectedTags } }
                .let { list ->
                    if (sortByUrgency) list.sortedByDescending { it.urgency } else list
                }

            adapter.submitList(filtered)
        }
    }
}