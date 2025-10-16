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

class OnboardingFragment3 : Fragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_onboarding_3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val foodFruits = view.findViewById<CheckBox>(R.id.foodFruits)
        val foodVegetables = view.findViewById<CheckBox>(R.id.foodVegetables)
        val foodProtein = view.findViewById<CheckBox>(R.id.foodProtein)
        val foodWholeGrains = view.findViewById<CheckBox>(R.id.foodWholeGrains)
        // Limited to four foods

        val rgDiet = view.findViewById<RadioGroup>(R.id.rgDietPreference)
        val rbVeg = view.findViewById<RadioButton>(R.id.rbVeg)
        val rbNonVeg = view.findViewById<RadioButton>(R.id.rbNonVeg)
        val rbVegan = view.findViewById<RadioButton>(R.id.rbVegan)

        // Use Activity's global Next/Get Started button; simply compute summary on demand if needed
        view.setOnClickListener {
            val foods = mutableListOf<String>()
            if (foodFruits?.isChecked == true) foods.add(getString(R.string.food_fruits))
            if (foodVegetables?.isChecked == true) foods.add(getString(R.string.food_vegetables))
            if (foodProtein?.isChecked == true) foods.add(getString(R.string.food_protein))
            if (foodWholeGrains?.isChecked == true) foods.add(getString(R.string.food_whole_grains))

            val diet = when {
                rbVeg?.isChecked == true -> getString(R.string.diet_veg)
                rbNonVeg?.isChecked == true -> getString(R.string.diet_non_veg)
                rbVegan?.isChecked == true -> getString(R.string.diet_vegan)
                else -> null
            }

            val summary = buildString {
                append(getString(R.string.selected_foods_format, if (foods.isEmpty()) getString(R.string.none_label) else foods.joinToString(", ")))
                append("\n")
                append(getString(R.string.selected_diet_format, diet ?: getString(R.string.none_label)))
            }
            Toast.makeText(requireContext(), summary, Toast.LENGTH_LONG).show()

            // TODO: Persist and navigate to main/home
        }
    }
}




