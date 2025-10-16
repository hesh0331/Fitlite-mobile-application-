package com.example.fitlife.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import com.example.fitlife.R
import com.example.fitlife.databinding.DialogHabitBinding
import com.example.fitlife.model.Habit
import com.example.fitlife.model.HabitCategory
import com.example.fitlife.model.HabitFrequency
import com.google.android.material.button.MaterialButton
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class HabitDialogFragment : DialogFragment() {

    private var _binding: DialogHabitBinding? = null
    private val binding get() = _binding!!

    private var habit: Habit? = null
    private var onHabitSaved: ((Habit) -> Unit)? = null

    companion object {
        private const val ARG_HABIT = "habit"

        fun newInstance(habit: Habit? = null): HabitDialogFragment {
            val fragment = HabitDialogFragment()
            if (habit != null) {
                val args = Bundle()
                args.putSerializable(ARG_HABIT, habit)
                fragment.arguments = args
            }
            return fragment
        }
    }

    override fun onStart() {
        super.onStart()
        // Make this dialog fullscreen
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            habit = it.getSerializable(ARG_HABIT) as? Habit
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogHabitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()
        populateFields()
        setupClickListeners()
    }

    private fun setupSpinners() {
        // Category spinner
        val categoryAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            HabitCategory.values().map { it.name.replace("_", " ") }
        )
        binding.spinnerCategory.setAdapter(categoryAdapter)

        // Frequency spinner
        val frequencyAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            HabitFrequency.values().map { it.name.replace("_", " ") }
        )
        binding.spinnerFrequency.setAdapter(frequencyAdapter)
    }

    private fun populateFields() {
        habit?.let { existingHabit ->
            binding.editHabitName.setText(existingHabit.name)
            binding.editHabitDescription.setText(existingHabit.description)
            binding.spinnerCategory.setText(existingHabit.category.name.replace("_", " "), false)
            binding.spinnerFrequency.setText(existingHabit.frequency.name.replace("_", " "), false)
            binding.editTargetValue.setText(existingHabit.targetValue.toString())
            binding.editUnit.setText(existingHabit.unit)
        }
    }

    private fun setupClickListeners() {
        view?.findViewById<MaterialToolbar>(R.id.toolbarHabit)?.setNavigationOnClickListener {
            dismiss()
        }
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            saveHabit()
        }
    }

    private fun saveHabit() {
        val name = binding.editHabitName.text.toString().trim()
        val description = binding.editHabitDescription.text.toString().trim()
        val categoryText = binding.spinnerCategory.text.toString()
        val frequencyText = binding.spinnerFrequency.text.toString()
        val targetValueText = binding.editTargetValue.text.toString()
        val unit = binding.editUnit.text.toString().trim()

        // No validation: fall back to safe defaults
        val targetValue = targetValueText.toIntOrNull() ?: 1
        val categoryEnum = if (categoryText.isEmpty()) HabitCategory.GENERAL
            else HabitCategory.valueOf(categoryText.replace(" ", "_").uppercase())
        val frequencyEnum = if (frequencyText.isEmpty()) HabitFrequency.DAILY
            else HabitFrequency.valueOf(frequencyText.replace(" ", "_").uppercase())

        // Create or update habit
        val habitToSave = habit?.copy(
            name = name,
            description = description,
            category = categoryEnum,
            frequency = frequencyEnum,
            targetValue = targetValue,
            unit = unit.ifEmpty { "times" },
            updatedAt = Date()
        ) ?: Habit(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            category = categoryEnum,
            frequency = frequencyEnum,
            targetValue = targetValue,
            unit = unit.ifEmpty { "times" },
            createdAt = Date(),
            updatedAt = Date()
        )

        onHabitSaved?.invoke(habitToSave)
        dismiss()
    }

    fun setOnHabitSavedListener(listener: (Habit) -> Unit) {
        onHabitSaved = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
