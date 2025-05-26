package com.hwmipt.planner.ui.tasks

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.hwmipt.planner.storage.TaskModel

class TasksPagerAdapter(
    fragment: Fragment,
    private val selectedTags: Set<String>,
    private val onToggleDone: (TaskModel) -> Unit,
    private val onClick: (TaskModel) -> Unit
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        val isDone = (position == 1)
        return TasksListFragment(isDone, selectedTags, onToggleDone, onClick)
    }
}