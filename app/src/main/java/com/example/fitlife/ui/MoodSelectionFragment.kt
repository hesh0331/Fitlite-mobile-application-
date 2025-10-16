package com.example.fitlife.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fitlife.R
import com.example.fitlife.data.Prefs
import com.example.fitlife.databinding.FragmentMoodSelectionBinding
import com.example.fitlife.model.MoodEntry
import com.example.fitlife.model.MoodLevel
import java.util.*

class MoodSelectionFragment : Fragment() {
    
    private var _binding: FragmentMoodSelectionBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefs: Prefs
    private lateinit var moodViewModel: MoodViewModel
    private var selectedMood: String = ""
    private var selectedEmoji: String = ""
    private var selectedLevel: Int = 3
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        prefs = Prefs(requireContext())
        moodViewModel = ViewModelProvider(requireActivity()).get(MoodViewModel::class.java)
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        // Mood cards
        binding.cardHappy.setOnClickListener { showMoodForm("Happy", "😊") }
        binding.cardExcited.setOnClickListener { showMoodForm("Excited", "🤩") }
        binding.cardSad.setOnClickListener { showMoodForm("Sad", "😞") }
        binding.cardAngry.setOnClickListener { showMoodForm("Angry", "😡") }
        binding.cardCalm.setOnClickListener { showMoodForm("Calm", "😌") }
        binding.cardSurprised.setOnClickListener { showMoodForm("Surprised", "😮") }
        binding.cardConfused.setOnClickListener { showMoodForm("Confused", "😕") }
        binding.cardSleepy.setOnClickListener { showMoodForm("Sleepy", "😴") }
        binding.cardGrateful.setOnClickListener { showMoodForm("Grateful", "🥰") }
    }
    
    private fun showMoodForm(mood: String, emoji: String) {
        selectedMood = mood
        selectedEmoji = emoji
        
        // Navigate to mood report with selected mood
        val moodFragment = MoodFragment()
        val bundle = Bundle()
        bundle.putString("selected_mood", mood)
        bundle.putString("selected_emoji", emoji)
        moodFragment.arguments = bundle
        
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, moodFragment)
            .commit()
    }
    
    private fun selectLevel(level: Int, btn1: Button, btn2: Button, btn3: Button, btn4: Button, btn5: Button, levelText: TextView, descriptionText: TextView) {
        selectedLevel = level
        
        // Reset all buttons
        btn1.setBackgroundResource(R.drawable.level_button_circular_unselected)
        btn1.setTextColor(resources.getColor(R.color.text_secondary_light, null))
        btn2.setBackgroundResource(R.drawable.level_button_circular_unselected)
        btn2.setTextColor(resources.getColor(R.color.text_secondary_light, null))
        btn3.setBackgroundResource(R.drawable.level_button_circular_unselected)
        btn3.setTextColor(resources.getColor(R.color.text_secondary_light, null))
        btn4.setBackgroundResource(R.drawable.level_button_circular_unselected)
        btn4.setTextColor(resources.getColor(R.color.text_secondary_light, null))
        btn5.setBackgroundResource(R.drawable.level_button_circular_unselected)
        btn5.setTextColor(resources.getColor(R.color.text_secondary_light, null))
        
        // Select current button
        when (level) {
            1 -> {
                btn1.setBackgroundResource(R.drawable.level_button_circular_selected)
                btn1.setTextColor(resources.getColor(R.color.white, null))
            }
            2 -> {
                btn2.setBackgroundResource(R.drawable.level_button_circular_selected)
                btn2.setTextColor(resources.getColor(R.color.white, null))
            }
            3 -> {
                btn3.setBackgroundResource(R.drawable.level_button_circular_selected)
                btn3.setTextColor(resources.getColor(R.color.white, null))
            }
            4 -> {
                btn4.setBackgroundResource(R.drawable.level_button_circular_selected)
                btn4.setTextColor(resources.getColor(R.color.white, null))
            }
            5 -> {
                btn5.setBackgroundResource(R.drawable.level_button_circular_selected)
                btn5.setTextColor(resources.getColor(R.color.white, null))
            }
        }
        
        updateLevelDisplay(level, levelText, descriptionText)
    }
    
    private fun updateLevelDisplay(level: Int, levelText: TextView, descriptionText: TextView) {
        levelText.text = "Level $level"
        
        val description = when (level) {
            1 -> "Very Low"
            2 -> "Low"
            3 -> "Moderate"
            4 -> "High"
            5 -> "Very High"
            else -> "Moderate"
        }
        descriptionText.text = description
    }
    
    private fun saveMoodEntry(reason: String, dialog: Dialog) {
        val moodLevel = when (selectedLevel) {
            1 -> MoodLevel.VERY_SAD
            2 -> MoodLevel.SAD
            3 -> MoodLevel.NEUTRAL
            4 -> MoodLevel.HAPPY
            5 -> MoodLevel.VERY_HAPPY
            else -> MoodLevel.NEUTRAL
        }
        
        val moodEntry = MoodEntry(
            id = UUID.randomUUID().toString(),
            date = Date(),
            moodLevel = moodLevel,
            energyLevel = com.example.fitlife.model.EnergyLevel.MODERATE,
            stressLevel = com.example.fitlife.model.StressLevel.MODERATE,
            sleepQuality = com.example.fitlife.model.SleepQuality.GOOD,
            notes = reason,
            tags = listOf(selectedMood),
            activities = emptyList(),
            weather = "",
            createdAt = Date(),
            updatedAt = Date()
        )
        
        val existingMoods = prefs.getMoodEntries().toMutableList()
        existingMoods.add(moodEntry)
        prefs.saveMoodEntries(existingMoods)
        moodViewModel.addMood(moodEntry)
        
        dialog.dismiss()
        
        // Navigate to mood report
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, MoodFragment())
            .commit()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
