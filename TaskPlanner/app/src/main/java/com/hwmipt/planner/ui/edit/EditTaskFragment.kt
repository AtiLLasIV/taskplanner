package com.hwmipt.planner.ui.edit

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hwmipt.planner.R
import com.hwmipt.planner.storage.TaskModel
import com.hwmipt.planner.viewmodel.TasksViewModel
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context


class EditTaskFragment : Fragment() {
    private lateinit var viewModel: TasksViewModel
    private var currentTask: TaskModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_edit_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(requireActivity())[TasksViewModel::class.java]

        val inputTitle = view.findViewById<EditText>(R.id.input_title)
        val inputDescription = view.findViewById<EditText>(R.id.input_description)
        val inputUrgency = view.findViewById<EditText>(R.id.input_urgency)

        val inputTags = view.findViewById<EditText>(R.id.input_tags)
        val prefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        val hideFilters = prefs.getBoolean("hide_tags", false)
        if (hideFilters) {
            inputTags.visibility = View.GONE
        }

        val inputDeadline = view.findViewById<EditText>(R.id.input_deadline)
        val btnSave = view.findViewById<Button>(R.id.btn_save)
        val btnDelete = view.findViewById<Button>(R.id.btn_delete)

        currentTask = arguments?.getParcelable("task")
        if (currentTask != null) {
            inputTitle.setText(currentTask?.title)
            inputDescription.setText(currentTask?.description)
            inputUrgency.setText(currentTask?.urgency.toString())
            inputTags.setText(currentTask?.tags?.joinToString(","))
            inputDeadline.setText(formatDate(currentTask?.deadline ?: 0))

            btnDelete.visibility = View.VISIBLE
        } else {
            btnDelete.visibility = View.GONE
        }

        btnSave.setOnClickListener {
            val title = inputTitle.text.toString().trim()
            val description = inputDescription.text.toString().trim()
            val urgency = inputUrgency.text.toString().toIntOrNull() ?: 0

            val tags = if (inputTags.visibility == View.VISIBLE) {
                inputTags.text.toString().split(",").map { it.trim() }.filter { it.isNotBlank() }
            } else {
                emptyList()
            }

            val deadlineStr = inputDeadline.text.toString().trim()
            val deadline = parseDate(deadlineStr)


            val task = TaskModel(
                id = currentTask?.id ?: 0,
                title = title,
                description = description,
                isDone = currentTask?.isDone ?: false,
                deadline = deadline,
                urgency = urgency,
                tags = tags
            )
            if (currentTask != null) {
                viewModel.updateTask(task)
            } else {
                viewModel.addTask(task)
            }

            parentFragmentManager.popBackStack()
        }

        btnDelete.setOnClickListener {
            currentTask?.let {
                viewModel.deleteTask(it)
            }
            parentFragmentManager.popBackStack()
        }
    }

    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun parseDate(dateStr: String): Long {
        return try {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            sdf.parse(dateStr)?.time ?: System.currentTimeMillis()
        } catch (e: Exception) {
            System.currentTimeMillis()
        }
    }
}