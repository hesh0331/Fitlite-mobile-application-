package com.example.fitlife.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fitlife.R

class OnboardingFragment2 : Fragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rgMoods = view.findViewById<RadioGroup>(R.id.rgMoods)
        val rbLoving = view.findViewById<RadioButton>(R.id.rbLoving)
        val rbSad = view.findViewById<RadioButton>(R.id.rbSad)
        val rbRomance = view.findViewById<RadioButton>(R.id.rbRomance)

        val reasonFood = view.findViewById<CheckBox>(R.id.reasonFood)
        val reasonWork = view.findViewById<CheckBox>(R.id.reasonWork)
        val reasonSleep = view.findViewById<CheckBox>(R.id.reasonSleep)
        val reasonExercise = view.findViewById<CheckBox>(R.id.reasonExercise)
        val reasonFamily = view.findViewById<CheckBox>(R.id.reasonFamily)
        val reasonWeather = view.findViewById<CheckBox>(R.id.reasonWeather)
        val reasonLover = view.findViewById<CheckBox>(R.id.reasonLover)

        // No internal Continue button; rely on Activity navigation
        view.setOnClickListener {
            val selectedMood = when {
                rbLoving?.isChecked == true -> getString(R.string.mood_loving)
                rbSad?.isChecked == true -> getString(R.string.mood_sad)
                rbRomance?.isChecked == true -> getString(R.string.mood_romance)
                else -> null
            }

            if (selectedMood == null) {
                Toast.makeText(requireContext(), getString(R.string.select_a_mood), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val reasons = mutableListOf<String>()
            if (reasonFood?.isChecked == true) reasons.add(getString(R.string.reason_food))
            if (reasonWork?.isChecked == true) reasons.add(getString(R.string.reason_work))
            if (reasonSleep?.isChecked == true) reasons.add(getString(R.string.reason_sleep))
            if (reasonExercise?.isChecked == true) reasons.add(getString(R.string.reason_exercise))
            if (reasonFamily?.isChecked == true) reasons.add(getString(R.string.reason_family))
            if (reasonWeather?.isChecked == true) reasons.add(getString(R.string.reason_weather))
            if (reasonLover?.isChecked == true) reasons.add(getString(R.string.reason_lover))

            val summary = getString(R.string.selected_mood_format, selectedMood) +
                if (reasons.isNotEmpty()) ": " + reasons.joinToString(", ") else ""
            Toast.makeText(requireContext(), summary, Toast.LENGTH_LONG).show()

            // TODO: Persist selections and/or navigate to next screen
        }
    }
}




