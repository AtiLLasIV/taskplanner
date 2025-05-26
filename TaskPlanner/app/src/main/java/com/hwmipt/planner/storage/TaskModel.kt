package com.hwmipt.planner.storage
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class TaskModel (
    val id: Int,
    val title: String,
    val description: String,
    val isDone: Boolean,
    val deadline: Long,
    val urgency: Int,
    val tags: List<String>,
    ) : Parcelable
