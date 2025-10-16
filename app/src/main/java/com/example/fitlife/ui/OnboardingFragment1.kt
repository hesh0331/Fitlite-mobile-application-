package com.example.fitlife.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitlife.R

class OnboardingFragment1 : Fragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_1, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cbHydration = view.findViewById<CheckBox>(R.id.cbHydration)
        val cbSteps = view.findViewById<CheckBox>(R.id.cbSteps)
        val cbSleep = view.findViewById<CheckBox>(R.id.cbSleep)
        val cbWorkout = view.findViewById<CheckBox>(R.id.cbWorkout)
        val cbMindfulness = view.findViewById<CheckBox>(R.id.cbMindfulness)
        // No internal Continue button; rely on Activity's Next button
        view.setOnClickListener {
            val selected = mutableListOf<String>()
            if (cbHydration?.isChecked == true) selected.add("Hydration")
            if (cbSteps?.isChecked == true) selected.add("Steps")
            if (cbSleep?.isChecked == true) selected.add("Sleep")
            if (cbWorkout?.isChecked == true) selected.add("Workout")
            if (cbMindfulness?.isChecked == true) selected.add("Mindfulness")

            if (selected.isEmpty()) {
                Toast.makeText(requireContext(), getString(R.string.select_at_least_one_habit), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(requireContext(), getString(R.string.selected_habits_format, selected.joinToString(", ")), Toast.LENGTH_LONG).show()

            // TODO: Persist the selection
        }
    }
}




