package com.hwmipt.planner.storage

import android.content.ContentValues

class TaskRepository(private val taskHelper: TaskHelper) {

    fun getAllTasks(): List<TaskModel> {
        val db = taskHelper.readableDatabase
        val tasks = mutableListOf<TaskModel>()

        db.query(TaskHelper.TABLE_NAME, null, null, null, null, null, null).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(TaskHelper.COL_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(TaskHelper.COL_TITLE))
                val desc = cursor.getString(cursor.getColumnIndexOrThrow(TaskHelper.COL_DESCRIPTION))
                val isDone = cursor.getInt(cursor.getColumnIndexOrThrow(TaskHelper.COL_IS_DONE)) == 1
                val deadline = cursor.getLong(cursor.getColumnIndexOrThrow(TaskHelper.COL_DEADLINE))
                val urgency = cursor.getInt(cursor.getColumnIndexOrThrow(TaskHelper.COL_URGENCY))
                val tagsStr = cursor.getString(cursor.getColumnIndexOrThrow(TaskHelper.COL_TAGS)) ?: ""
                val tags = tagsStr.split(",").filter { it.isNotBlank() }

                tasks.add(TaskModel(id, title, desc, isDone, deadline, urgency, tags))
            }
        }

        return tasks
    }

    fun insertTask(task: TaskModel) {
        val db = taskHelper.writableDatabase
        db.insert(TaskHelper.TABLE_NAME, null, toContentValues(task))
    }

    fun updateTask(task: TaskModel) {
        val db = taskHelper.writableDatabase
        db.update(
            TaskHelper.TABLE_NAME,
            toContentValues(task),
            "${TaskHelper.COL_ID} = ?",
            arrayOf(task.id.toString())
        )
    }

    fun deleteTask(task: TaskModel) {
        val db = taskHelper.writableDatabase
        db.delete(
            TaskHelper.TABLE_NAME,
            "${TaskHelper.COL_ID} = ?",
            arrayOf(task.id.toString())
        )
    }

    private fun toContentValues(task: TaskModel): ContentValues {
        return ContentValues().apply {
            put(TaskHelper.COL_TITLE, task.title)
            put(TaskHelper.COL_DESCRIPTION, task.description)
            put(TaskHelper.COL_IS_DONE, if (task.isDone) 1 else 0)
            put(TaskHelper.COL_DEADLINE, task.deadline)
            put(TaskHelper.COL_URGENCY, task.urgency)
            put(TaskHelper.COL_TAGS, task.tags.joinToString(","))
        }
    }
}