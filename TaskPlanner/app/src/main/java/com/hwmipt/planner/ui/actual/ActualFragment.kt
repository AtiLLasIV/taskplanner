package com.hwmipt.planner.ui.actual

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hwmipt.planner.R
import com.hwmipt.planner.storage.TaskModel
import com.hwmipt.planner.ui.edit.EditTaskFragment
import com.hwmipt.planner.ui.tasks.TagAdapter
import com.hwmipt.planner.viewmodel.TasksViewModel
import java.util.*

class ActualFragment : Fragment() {

    private lateinit var viewModel: TasksViewModel
    private lateinit var recycler: RecyclerView
    private lateinit var tagRecycler: RecyclerView
    private lateinit var tagAdapter: TagAdapter
    private lateinit var selectedTags: MutableSet<String>
    private lateinit var adapter: ActualAdapter
    private var filtersEnabled = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_actual, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity())[TasksViewModel::class.java]
        selectedTags = viewModel.selectedTags

        recycler = view.findViewById(R.id.actual_task_list)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = ActualAdapter(
            onMarkDone = { task -> viewModel.markTaskDone(task) },
            onClick = { task -> openEditor(task) }
        )
        recycler.adapter = adapter

        tagRecycler = view.findViewById(R.id.tag_filter_list)
        tagRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val prefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        filtersEnabled = !prefs.getBoolean("hide_tags", false)
        tagRecycler.visibility = if (filtersEnabled) View.VISIBLE else View.GONE

        val fab = view.findViewById<View>(R.id.fab_add_task)
        fab.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, EditTaskFragment())
                .addToBackStack(null)
                .commit()
        }

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            if (filtersEnabled) {
                setupTags(tasks)
            }
            applyTagFilter(tasks)
        }
    }

    private fun setupTags(tasks: List<TaskModel>) {
        val allTags = tasks.flatMap { it.tags }.toSet().toList()
        tagAdapter = TagAdapter(allTags, selectedTags) {
            viewModel.tasks.value?.let { applyTagFilter(it) }
        }
        tagRecycler.adapter = tagAdapter
    }

    private fun applyTagFilter(tasks: List<TaskModel>) {
        val now = Calendar.getInstance()
        val today = normalizeDay(now.time)

        now.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrow = normalizeDay(now.time)

        val filtered = if (!filtersEnabled || selectedTags.isEmpty()) {
            tasks.filter { !it.isDone }
        } else {
            tasks.filter { !it.isDone && it.tags.any { tag -> tag in selectedTags } }
        }

        val prefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val sortByUrgency = prefs.getBoolean("urgency_sort", false)

        val todayTasks = filtered
            .filter { normalizeDay(Date(it.deadline)) == today }
            .let { if (sortByUrgency) it.sortedByDescending { it.urgency } else it }

        val tomorrowTasks = filtered
            .filter { normalizeDay(Date(it.deadline)) == tomorrow }
            .let { if (sortByUrgency) it.sortedByDescending { it.urgency } else it }

        val list = mutableListOf<ActualItem>()
        list += ActualItem.Title(getString(R.string.actual_today))
        list += if (todayTasks.isEmpty()) listOf(ActualItem.Empty) else todayTasks.map { ActualItem.Task(it) }

        list += ActualItem.Title(getString(R.string.actual_tomorrow))
        list += if (tomorrowTasks.isEmpty()) listOf(ActualItem.Empty) else tomorrowTasks.map { ActualItem.Task(it) }

        adapter.submitList(list)
    }

    private fun normalizeDay(date: Date): String {
        val cal = Calendar.getInstance().apply { time = date }
        val y = cal.get(Calendar.YEAR)
        val m = cal.get(Calendar.MONTH)
        val d = cal.get(Calendar.DAY_OF_MONTH)
        return "$y-$m-$d"
    }

    private fun openEditor(task: TaskModel) {
        val fragment = EditTaskFragment().apply {
            arguments = Bundle().apply {
                putParcelable("task", task)
            }
        }
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}