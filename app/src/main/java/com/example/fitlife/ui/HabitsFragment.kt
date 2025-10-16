package com.example.fitlife.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitlife.R
import com.example.fitlife.data.Prefs
import com.example.fitlife.databinding.FragmentHabitsBinding
import com.example.fitlife.model.Habit
import com.example.fitlife.model.HabitCompletion
import com.example.fitlife.ui.adapters.HabitAdapter
import com.example.fitlife.ui.dialogs.HabitDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.*

class HabitsFragment : Fragment() {
    
    private var _binding: FragmentHabitsBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefs: Prefs
    private lateinit var habitAdapter: HabitAdapter
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val today = dateFormat.format(Date())
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        prefs = Prefs(requireContext())
        setupRecyclerView()
        setupClickListeners()
        loadHabits()
    }
    
    private fun setupRecyclerView() {
        habitAdapter = HabitAdapter(
            habits = emptyList(),
            onHabitComplete = { habit, isCompleted ->
                toggleHabitCompletion(habit, isCompleted)
            },
            onHabitEdit = { habit ->
                showHabitDialog(habit)
            },
            onHabitDelete = { habit ->
                showDeleteConfirmation(habit)
            }
        )
        
        binding.rvHabits.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = habitAdapter
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddHabit.setOnClickListener {
            showHabitDialog()
        }
    }
    
    private fun loadHabits() {
        val habits = prefs.getHabits()
        habitAdapter.updateHabits(habits)
        updateProgress()
    }
    
    private fun updateProgress() {
        val completedCount = habitAdapter.getCompletedTodayCount()
        val totalCount = habitAdapter.getTotalActiveHabitsCount()
        
        binding.textProgressCount.text = "$completedCount / $totalCount completed"
        
        val progress = if (totalCount > 0) {
            (completedCount * 100) / totalCount
        } else {
            0
        }
        
        binding.progressBarHabits.progress = progress
    }
    
    private fun toggleHabitCompletion(habit: Habit, isCompleted: Boolean) {
        val updatedCompletedDates = if (isCompleted) {
            if (!habit.completedDates.contains(today)) {
                habit.completedDates + today
            } else {
                habit.completedDates
            }
        } else {
            habit.completedDates.filter { it != today }
        }
        
        // Create completion entry
        val completionEntry = if (isCompleted) {
            HabitCompletion(
                id = UUID.randomUUID().toString(),
                habitId = habit.id,
                date = Date(),
                completedValue = habit.targetValue,
                isCompleted = true
            )
        } else {
            null
        }
        
        val updatedCompletionHistory = if (isCompleted) {
            val existingIndex = habit.completionHistory.indexOfFirst { 
                dateFormat.format(it.date) == today 
            }
            if (existingIndex >= 0) {
                habit.completionHistory.toMutableList().apply {
                    set(existingIndex, completionEntry!!)
                }
            } else {
                habit.completionHistory + completionEntry!!
            }
        } else {
            habit.completionHistory.filter { 
                dateFormat.format(it.date) != today 
            }
        }
        
        // Calculate new streak
        val newStreak = calculateStreak(updatedCompletedDates)
        val newBestStreak = maxOf(habit.bestStreak, newStreak)
        
        val updatedHabit = habit.copy(
            completedDates = updatedCompletedDates,
            completionHistory = updatedCompletionHistory,
            streak = newStreak,
            bestStreak = newBestStreak,
            updatedAt = Date()
        )
        
        prefs.saveHabit(updatedHabit)
        loadHabits() // Reload to update UI
    }
    
    private fun calculateStreak(completedDates: List<String>): Int {
        if (completedDates.isEmpty()) return 0
        
        val sortedDates = completedDates.sortedDescending()
        var streak = 0
        val calendar = Calendar.getInstance()
        
        for (dateString in sortedDates) {
            calendar.time = dateFormat.parse(dateString) ?: continue
            val expectedDate = dateFormat.format(calendar.time)
            
            if (dateString == expectedDate) {
                streak++
                calendar.add(Calendar.DAY_OF_MONTH, -1)
            } else {
                break
            }
        }
        
        return streak
    }
    
    private fun showHabitDialog(habit: Habit? = null) {
        val dialog = HabitDialogFragment.newInstance(habit)
        dialog.setOnHabitSavedListener { savedHabit ->
            prefs.saveHabit(savedHabit)
            loadHabits()
        }
        dialog.show(parentFragmentManager, "HabitDialog")
    }
    
    private fun showDeleteConfirmation(habit: Habit) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Habit")
            .setMessage("Are you sure you want to delete \"${habit.name}\"? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                prefs.deleteHabit(habit.id)
                loadHabits()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
