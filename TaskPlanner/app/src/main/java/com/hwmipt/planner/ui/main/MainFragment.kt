package com.hwmipt.planner.ui.main

import android.os.Bundle
import android.view.*
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.hwmipt.planner.R
import com.hwmipt.planner.ui.about.AboutFragment
import com.hwmipt.planner.ui.actual.ActualFragment
import com.hwmipt.planner.ui.settings.SettingsFragment
import com.hwmipt.planner.ui.tasks.TasksFragment

class MainFragment : Fragment() {
    private var currentTag: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tasksButton = view.findViewById<ImageButton>(R.id.tab_tasks)
        val actualButton = view.findViewById<ImageButton>(R.id.tab_actual)
        val settingsButton = view.findViewById<ImageButton>(R.id.tab_settings)
        val aboutButton = view.findViewById<ImageButton>(R.id.btn_about)

        tasksButton.setOnClickListener {
            showFragment(TasksFragment(), "tasks")
        }
        actualButton.setOnClickListener {
            showFragment(ActualFragment(), "actual")
        }
        settingsButton.setOnClickListener {
            showFragment(SettingsFragment(), "settings")
        }
        aboutButton.setOnClickListener {
            childFragmentManager.beginTransaction()
                .replace(R.id.main_fragment_container, AboutFragment())
                .addToBackStack(null)
                .commit()
        }

        if (savedInstanceState == null) {
            showFragment(TasksFragment(), "tasks")
        }
    }

    private fun showFragment(fragment: Fragment, tag: String) {
        if (tag == currentTag) return
        currentTag = tag
        updateTabSelection(tag)

        childFragmentManager.beginTransaction()
            .replace(R.id.main_fragment_container, fragment, tag)
            .commit()
    }

    private fun updateTabSelection(selected: String) {
        val activeColor = ContextCompat.getColor(requireContext(), R.color.extra_light_blue)
        val transparent = 0x00000000

        val tasksButton = view?.findViewById<ImageButton>(R.id.tab_tasks)
        val actualButton = view?.findViewById<ImageButton>(R.id.tab_actual)
        val settingsButton = view?.findViewById<ImageButton>(R.id.tab_settings)

        tasksButton?.setBackgroundColor(if (selected == "tasks") activeColor else transparent)
        actualButton?.setBackgroundColor(if (selected == "actual") activeColor else transparent)
        settingsButton?.setBackgroundColor(if (selected == "settings") activeColor else transparent)
    }
}