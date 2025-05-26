package com.hwmipt.planner.ui.actual

import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hwmipt.planner.R
import com.hwmipt.planner.storage.TaskModel

sealed class ActualItem {
    data class Title(val text: String) : ActualItem()
    data class Task(val task: TaskModel) : ActualItem()
    object Empty : ActualItem()
}

class ActualAdapter(
    private val onMarkDone: (TaskModel) -> Unit,
    private val onClick: (TaskModel) -> Unit
) : ListAdapter<ActualItem, RecyclerView.ViewHolder>(DIFF) {

    companion object {
        private const val TYPE_TITLE = 0
        private const val TYPE_TASK = 1
        private const val TYPE_EMPTY = 2

        private val DIFF = object : DiffUtil.ItemCallback<ActualItem>() {
            override fun areItemsTheSame(old: ActualItem, new: ActualItem): Boolean {
                return when {
                    old is ActualItem.Title && new is ActualItem.Title -> old.text == new.text
                    old is ActualItem.Task && new is ActualItem.Task -> old.task.id == new.task.id
                    old is ActualItem.Empty && new is ActualItem.Empty -> true
                    else -> false
                }
            }

            override fun areContentsTheSame(old: ActualItem, new: ActualItem): Boolean {
                return old == new
            }
        }
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is ActualItem.Title -> TYPE_TITLE
        is ActualItem.Task -> TYPE_TASK
        is ActualItem.Empty -> TYPE_EMPTY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_TITLE -> TitleViewHolder(inflater.inflate(R.layout.item_title, parent, false))
            TYPE_TASK -> TaskViewHolder(inflater.inflate(R.layout.item_task, parent, false))
            TYPE_EMPTY -> EmptyViewHolder(inflater.inflate(R.layout.item_empty, parent, false))
            else -> throw IllegalArgumentException("Unknown type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ActualItem.Title -> (holder as TitleViewHolder).bind(item)
            is ActualItem.Task -> (holder as TaskViewHolder).bind(item.task)
            is ActualItem.Empty -> {}
        }
    }

    inner class TitleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: ActualItem.Title) {
            (itemView as TextView).text = item.text
        }
    }

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.findViewById<TextView>(R.id.task_title)
        private val doneCheckbox = view.findViewById<CheckBox>(R.id.task_done_checkbox)

        fun bind(task: TaskModel) {
            title.text = task.title

            doneCheckbox.setOnCheckedChangeListener(null)
            doneCheckbox.isChecked = task.isDone
            doneCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked != task.isDone) {
                    onMarkDone(task.copy(isDone = isChecked))
                }
            }

            itemView.setOnClickListener {
                onClick(task)
            }
        }
    }

    inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}