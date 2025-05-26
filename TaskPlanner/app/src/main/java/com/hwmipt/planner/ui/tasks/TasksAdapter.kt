package com.hwmipt.planner.ui.tasks

import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hwmipt.planner.R
import com.hwmipt.planner.storage.TaskModel

class TasksAdapter(
    private val onClick: (TaskModel) -> Unit,
    private val onToggleDone: (TaskModel) -> Unit
) : ListAdapter<TaskModel, TasksAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TaskModel>() {
            override fun areItemsTheSame(old: TaskModel, new: TaskModel) = old.id == new.id
            override fun areContentsTheSame(old: TaskModel, new: TaskModel) = old == new
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.task_title)
        val checkbox: CheckBox = view.findViewById(R.id.task_done_checkbox)

        fun bind(task: TaskModel) {
            title.text = task.title

            checkbox.setOnCheckedChangeListener(null)
            checkbox.isChecked = task.isDone
            checkbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != task.isDone) {
                    onToggleDone(task.copy(isDone = isChecked))
                }
            }

            itemView.setOnClickListener {
                onClick(task)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}