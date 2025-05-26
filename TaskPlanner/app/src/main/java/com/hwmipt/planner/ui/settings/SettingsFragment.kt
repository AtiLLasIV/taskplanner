package com.hwmipt.planner.ui.settings

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hwmipt.planner.R
import com.hwmipt.planner.viewmodel.TasksViewModel

class SettingsFragment : Fragment() {

    private lateinit var checkHideFilters: CheckBox
    private lateinit var checkHideAds: CheckBox
    private lateinit var checkSortUrgency: CheckBox
    private lateinit var viewModel: TasksViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val prefs = requireContext().getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        viewModel = ViewModelProvider(requireActivity())[TasksViewModel::class.java]

        checkHideFilters = view.findViewById(R.id.checkbox_hide_tags)
        checkHideAds = view.findViewById(R.id.checkbox_hide_ad)
        checkSortUrgency = view.findViewById(R.id.checkbox_urgency_sort)

        checkHideFilters.isChecked = prefs.getBoolean("hide_tags", false)
        checkHideAds.isChecked = prefs.getBoolean("hide_ads", false)
        checkSortUrgency.isChecked = prefs.getBoolean("urgency_sort", false)

        checkHideFilters.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                viewModel.selectedTags.clear()
                viewModel.refreshTasks()
            }
            prefs.edit().putBoolean("hide_tags", isChecked).apply()
        }

        checkHideAds.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("hide_ads", isChecked).apply()
        }

        checkSortUrgency.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("urgency_sort", isChecked).apply()
        }
    }
}