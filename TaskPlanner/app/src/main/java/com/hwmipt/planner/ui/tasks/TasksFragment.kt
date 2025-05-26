package com.hwmipt.planner.ui.tasks

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.hwmipt.planner.R
import com.hwmipt.planner.ui.edit.EditTaskFragment
import com.hwmipt.planner.viewmodel.TasksViewModel
import com.hwmipt.planner.storage.TaskModel

class TasksFragment : Fragment() {

    private lateinit var viewModel: TasksViewModel
    private lateinit var tagRecycler: RecyclerView
    private lateinit var tagAdapter: TagAdapter
    private lateinit var selectedTags: MutableSet<String>
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var pagerAdapter: TasksPagerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity())[TasksViewModel::class.java]
        selectedTags = viewModel.selectedTags

        tagRecycler = view.findViewById(R.id.tag_filter_list)
        tagRecycler.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val prefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val hideFilters = prefs.getBoolean("hide_tags", false)
        tagRecycler.visibility = if (hideFilters) View.GONE else View.VISIBLE

        tabLayout = view.findViewById(R.id.tab_layout)
        viewPager = view.findViewById(R.id.view_pager)

        pagerAdapter = TasksPagerAdapter(
            this,
            selectedTags,
            onToggleDone = { viewModel.markTaskDone(it) },
            onClick = { openEditor(it) }
        )
        viewPager.adapter = pagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            tab.text = if (pos == 0) getString(R.string.tab_planned) else getString(R.string.tab_done)
        }.attach()

        val fab = view.findViewById<View>(R.id.fab_add_task)
        fab.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, EditTaskFragment())
                .addToBackStack(null)
                .commit()
        }

        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            setupTags(tasks)
        }
    }

    private fun setupTags(tasks: List<TaskModel>) {
        val allTags = tasks.flatMap { it.tags }.toSet().toList()

        tagAdapter = TagAdapter(allTags, selectedTags) {
            pagerAdapter = TasksPagerAdapter(
                this,
                selectedTags,
                onToggleDone = { viewModel.updateTask(it) },
                onClick = { openEditor(it) }
            )
            viewPager.adapter = pagerAdapter

            TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
                tab.text = if (pos == 0) getString(R.string.tab_planned) else getString(R.string.tab_done)
            }.attach()
        }

        tagRecycler.adapter = tagAdapter
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