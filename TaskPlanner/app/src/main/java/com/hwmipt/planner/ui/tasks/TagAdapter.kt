package com.hwmipt.planner.ui.tasks

import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hwmipt.planner.R

class TagAdapter(
    private val tags: List<String>,
    private val selectedTags: MutableSet<String>,
    private val onSelectionChanged: () -> Unit
) : RecyclerView.Adapter<TagAdapter.TagViewHolder>() {

    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tagText: TextView = itemView.findViewById(R.id.tag_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tag, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        val tag = tags[position]
        holder.tagText.text = tag

        val selected = tag in selectedTags
        holder.tagText.setBackgroundResource(
            if (selected) R.drawable.tag_background_selected else R.drawable.tag_background
        )

        holder.tagText.setOnClickListener {
            if (selected) selectedTags.remove(tag) else selectedTags.add(tag)
            notifyItemChanged(position)
            onSelectionChanged()
        }
    }

    override fun getItemCount(): Int = tags.size
}