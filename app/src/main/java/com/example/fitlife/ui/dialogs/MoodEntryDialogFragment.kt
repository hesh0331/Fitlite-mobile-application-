package com.example.fitlife.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.GridLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.fitlife.R
import com.example.fitlife.databinding.DialogMoodBinding
import com.example.fitlife.model.EnergyLevel
import com.example.fitlife.model.MoodEntry
import com.example.fitlife.model.MoodLevel
import com.example.fitlife.model.StressLevel
import java.util.*

class MoodEntryDialogFragment : DialogFragment() {

    private var _binding: DialogMoodBinding? = null
    private val binding get() = _binding!!

    private var selectedMoodLevel: MoodLevel = MoodLevel.NEUTRAL
    private var selectedEnergyLevel: EnergyLevel = EnergyLevel.MODERATE
    private var selectedStressLevel: StressLevel = StressLevel.MODERATE
    private var onMoodSaved: ((MoodEntry) -> Unit)? = null

    companion object {
        fun newInstance(): MoodEntryDialogFragment {
            return MoodEntryDialogFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMoodChips()
        setupSliders()
        setupClickListeners()
        updateSelectedMoodDisplay()
    }

    private fun setupMoodChips() {
        val container = binding.moodsContainer
        container.removeAllViews()
        MoodLevel.values().forEach { moodLevel ->
            val chip = TextView(requireContext()).apply {
                text = "${moodLevel.emoji}  ${moodLevel.name.replace("_", " ")}"
                setPadding(24, 16, 24, 16)
                setTextColor(resources.getColor(R.color.text_primary_light, null))
                background = if (moodLevel == selectedMoodLevel) resources.getDrawable(R.drawable.bg_chip_selected, null) else resources.getDrawable(R.drawable.bg_chip, null)
                setOnClickListener { v ->
                    selectedMoodLevel = moodLevel
                    updateSelectedMoodDisplay()
                    setupMoodChips()
                    v.animate().scaleX(1.05f).scaleY(1.05f).setDuration(100).withEndAction {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(100).start()
                    }.start()
                }
            }
            val lp = android.widget.LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.setMargins(8, 0, 8, 0)
            chip.layoutParams = lp
            container.addView(chip)
        }
    }

    private fun setupSliders() {
        binding.seekEnergy.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                selectedEnergyLevel = when (progress) {
                    0 -> EnergyLevel.VERY_LOW
                    1 -> EnergyLevel.LOW
                    2 -> EnergyLevel.MODERATE
                    3 -> EnergyLevel.HIGH
                    else -> EnergyLevel.VERY_HIGH
                }
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })

        binding.seekStress.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                selectedStressLevel = when (progress) {
                    0 -> StressLevel.VERY_LOW
                    1 -> StressLevel.LOW
                    2 -> StressLevel.MODERATE
                    3 -> StressLevel.HIGH
                    else -> StressLevel.VERY_HIGH
                }
            }
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })
    }

    // Spinners removed; using sliders instead

    private fun setupClickListeners() {
        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnSave.setOnClickListener {
            saveMoodEntry()
        }
    }

    private fun updateSelectedMoodDisplay() {
        binding.textSelectedEmoji.text = selectedMoodLevel.emoji
        binding.textSelectedMood.text = selectedMoodLevel.name.replace("_", " ")
    }

    private fun saveMoodEntry() {
        val notes = binding.editMoodNotes.text.toString().trim()

        val moodEntry = MoodEntry(
            id = UUID.randomUUID().toString(),
            date = Date(),
            moodLevel = selectedMoodLevel,
            energyLevel = selectedEnergyLevel,
            stressLevel = selectedStressLevel,
            notes = notes,
            createdAt = Date(),
            updatedAt = Date()
        )

        onMoodSaved?.invoke(moodEntry)
        dismiss()
    }

    fun setOnMoodSavedListener(listener: (MoodEntry) -> Unit) {
        onMoodSaved = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
