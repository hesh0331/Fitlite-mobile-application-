package com.example.fitlife.ui

/**
 * MoodFragment
 *
 * Purpose: Displays the mood entry form (emoji, level, reason, date),
 * persists entries via Prefs, and visualizes recent moods in a weekly
 * bar chart. Also shows a calendar below the form to pick the entry date.
 */

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fitlife.R
import com.example.fitlife.data.Prefs
import com.example.fitlife.databinding.FragmentMoodBinding
import com.example.fitlife.model.MoodEntry
import com.example.fitlife.model.MoodLevel
import com.example.fitlife.ui.dialogs.MoodEntryDialogFragment
import com.google.android.material.datepicker.MaterialDatePicker
import android.widget.CalendarView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import java.util.UUID

class MoodFragment : Fragment() {
    
    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefs: Prefs
    private var selectedMood: String = "Calm"
    private var selectedEmoji: String = "😌"
    private var selectedLevel: Int = 3
    private var selectedDate: Date = Date()
    private var weeklyChart: BarChart? = null
    private var calendarView: CalendarView? = null
    private lateinit var moodViewModel: MoodViewModel
    
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        prefs = Prefs(requireContext())
        // If a mood was passed as arguments from the selection screen, apply it
        arguments?.getString("selected_mood")?.let { passedMood ->
            selectedMood = passedMood
        }
        arguments?.getString("selected_emoji")?.let { passedEmoji ->
            selectedEmoji = passedEmoji
        }
        setupClickListeners()
        setupMoodForm()
        setupWeeklyChart()
        setupCalendar()
        updateDateDisplay()

        // Init shared ViewModel from activity (no KTX needed)
        moodViewModel = ViewModelProvider(requireActivity()).get(MoodViewModel::class.java)
        // Observe entries to refresh chart/calendar immediately after submit
        moodViewModel.entries.observe(viewLifecycleOwner) {
            updateWeeklyChart()
        }
    }

    private fun setupClickListeners() {
        // Date picker
        binding.layoutDateSelector.setOnClickListener {
                    showDatePicker()
        }
        
        // Mood form buttons
        binding.btnBack.setOnClickListener {
            // Clear form and reset
            clearForm()
        }
        
        binding.btnSubmit.setOnClickListener {
            saveMoodEntry()
            showSuccessToast()
            navigateToMoodSelection()
        }
        
        // Emotion cards removed in UI
        
        // Mood level slider
        binding.moodLevelSlider.setOnSeekBarChangeListener(object : android.widget.SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: android.widget.SeekBar?, progress: Int, fromUser: Boolean) {
                selectedLevel = progress + 1 // Convert 0-4 to 1-5
                updateLevelDisplay(selectedLevel)
            }
            
            override fun onStartTrackingTouch(seekBar: android.widget.SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: android.widget.SeekBar?) {}
        })
        
    }
    
    private fun setupMoodForm() {
        // Ensure form is visible
        binding.layoutMoodForm.visibility = View.VISIBLE
        
        // Set selected mood
        binding.textSelectedEmoji.text = selectedEmoji
        binding.textSelectedMood.text = selectedMood
        
        // Initialize slider
        binding.moodLevelSlider.progress = selectedLevel - 1 // Convert 1-5 to 0-4
        updateLevelDisplay(selectedLevel)
        
        // Initialize date
        updateDateDisplay()
    }
    
    private fun selectMood(mood: String, emoji: String) {
        selectedMood = mood
        selectedEmoji = emoji
        
        // Update UI
        binding.textSelectedEmoji.text = selectedEmoji
        binding.textSelectedMood.text = selectedMood

        // Scroll back to the form so user sees the selection applied
        binding.root.post {
            binding.root.smoothScrollTo(0, binding.layoutMoodForm.top)
        }
    }
    
    private fun clearForm() {
        // Reset form to default values
        selectedMood = "Calm"
        selectedEmoji = "😌"
        selectedLevel = 3
        selectedDate = Date()
        
        // Update UI
        binding.textSelectedEmoji.text = selectedEmoji
        binding.textSelectedMood.text = selectedMood
        binding.moodLevelSlider.progress = selectedLevel - 1
        binding.editReason.setText("")
        updateLevelDisplay(selectedLevel)
        updateDateDisplay()
    }
    
    
    private fun updateLevelDisplay(level: Int) {
        binding.textSelectedLevel.text = "Level $level"
        
        val description = when (level) {
            1 -> "Very Low"
            2 -> "Low"
            3 -> "Moderate"
            4 -> "High"
            5 -> "Very High"
            else -> "Moderate"
        }
        binding.textLevelDescription.text = description
    }
    
    private fun saveMoodEntry() {
        val moodLevel = when (selectedLevel) {
            1 -> MoodLevel.VERY_SAD
            2 -> MoodLevel.SAD
            3 -> MoodLevel.NEUTRAL
            4 -> MoodLevel.HAPPY
            5 -> MoodLevel.VERY_HAPPY
            else -> MoodLevel.NEUTRAL
        }
        
        val reason = binding.editReason.text.toString()
        
        val moodEntry = MoodEntry(
            id = UUID.randomUUID().toString(),
            date = selectedDate,
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
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(selectedDate.time)
            .build()

        datePicker.addOnPositiveButtonClickListener { selectedDateInUtc ->
            // Update the selected date
            selectedDate = Date(selectedDateInUtc)
            updateDateDisplay()
        }

        datePicker.show(parentFragmentManager, "MoodDatePicker")
    }
    
    private fun updateDateDisplay() {
        val formatter = SimpleDateFormat("MMMM, dd", Locale.getDefault())
        binding.textSelectedDate.text = formatter.format(selectedDate)
    }
    
    private fun showSuccessToast() {
        android.widget.Toast.makeText(
            requireContext(),
            "Mood entry saved successfully! 😊",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    }
    
    private fun navigateToMoodSelection() {
        // Clear the form to show fresh state
        clearForm()
        
        // Scroll to top of form after save
        binding.root.post {
            binding.root.smoothScrollTo(0, binding.layoutMoodForm.top)
        }

        // Refresh chart after save
        updateWeeklyChart()
        // Jump calendar to the saved date
        calendarView?.date = selectedDate.time
    }
    
    private fun setupWeeklyChart() {
        weeklyChart = binding.weeklyMoodChart
        weeklyChart?.description?.isEnabled = false
        weeklyChart?.setDrawGridBackground(false)
        weeklyChart?.setDrawBorders(false)
        weeklyChart?.setTouchEnabled(false)
        weeklyChart?.setDragEnabled(false)
        weeklyChart?.setScaleEnabled(false)
        weeklyChart?.setPinchZoom(false)
        
        // Configure X axis
        val xAxis = weeklyChart?.xAxis
        xAxis?.position = XAxis.XAxisPosition.BOTTOM
        xAxis?.setDrawGridLines(false)
        xAxis?.setDrawAxisLine(false)
        xAxis?.textSize = 12f
        xAxis?.textColor = resources.getColor(R.color.text_secondary_light, null)
        
        // Configure Y axis
        val leftAxis = weeklyChart?.axisLeft
        leftAxis?.setDrawGridLines(false)
        leftAxis?.setDrawAxisLine(false)
        leftAxis?.textSize = 12f
        leftAxis?.textColor = resources.getColor(R.color.text_secondary_light, null)
        leftAxis?.axisMinimum = 0f
        leftAxis?.axisMaximum = 5f
        
        val rightAxis = weeklyChart?.axisRight
        rightAxis?.isEnabled = false
        
        // Configure legend
        weeklyChart?.legend?.isEnabled = false
        
        updateWeeklyChart()
    }
    
    private fun updateWeeklyChart() {
        val entries = mutableListOf<BarEntry>()
        val dayLabels = mutableListOf<String>()
        val colors = mutableListOf<Int>()
        
        // Get last 7 days
        val calendar = Calendar.getInstance()
        calendar.time = selectedDate
        calendar.add(Calendar.DAY_OF_MONTH, -6) // Start from 7 days ago
        
        val moodEntries = prefs.getMoodEntries()
        
        for (i in 0..6) {
            val currentDate = calendar.time
            val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(currentDate)
            dayLabels.add(dayName)
            
            // Find mood entry for this date
            val moodEntry = moodEntries.find { 
                val entryDate = Calendar.getInstance().apply { time = it.date }
                val currentDateCal = Calendar.getInstance().apply { time = currentDate }
                entryDate.get(Calendar.YEAR) == currentDateCal.get(Calendar.YEAR) &&
                entryDate.get(Calendar.DAY_OF_YEAR) == currentDateCal.get(Calendar.DAY_OF_YEAR)
            }
            
            if (moodEntry != null) {
                val moodValue = when (moodEntry.moodLevel) {
                    MoodLevel.VERY_SAD -> 1f
                    MoodLevel.SAD -> 2f
                    MoodLevel.NEUTRAL -> 3f
                    MoodLevel.HAPPY -> 4f
                    MoodLevel.VERY_HAPPY -> 5f
                }
                entries.add(BarEntry(i.toFloat(), moodValue))
                
                // Set color based on mood level
                val color = when (moodEntry.moodLevel) {
                    MoodLevel.VERY_SAD -> resources.getColor(R.color.error_red, null)
                    MoodLevel.SAD -> resources.getColor(R.color.warning_orange, null)
                    MoodLevel.NEUTRAL -> resources.getColor(R.color.accent_green, null)
                    MoodLevel.HAPPY -> resources.getColor(R.color.accent_green_light, null)
                    MoodLevel.VERY_HAPPY -> resources.getColor(R.color.accent_green, null)
                }
                colors.add(color)
            } else {
                // No data for this day
                entries.add(BarEntry(i.toFloat(), 0f))
                colors.add(resources.getColor(R.color.text_secondary_light, null))
            }
            
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        val dataSet = BarDataSet(entries, "Mood")
        dataSet.colors = colors
        dataSet.setDrawValues(false)
        
        val data = BarData(dataSet)
        data.barWidth = 0.6f
        
        weeklyChart?.data = data
        weeklyChart?.xAxis?.valueFormatter = IndexAxisValueFormatter(dayLabels)
        weeklyChart?.invalidate()
    }

    private fun setupCalendar() {
        calendarView = binding.moodCalendar
        calendarView?.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            selectedDate = cal.time
            updateDateDisplay()
        }
        calendarView?.date = selectedDate.time
    }

    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
