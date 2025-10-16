package com.example.fitlife.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.fitlife.MainActivity
import com.example.fitlife.R
import com.example.fitlife.data.Prefs
import com.example.fitlife.databinding.FragmentHomeBinding
import com.example.fitlife.model.Habit
import com.example.fitlife.worker.HydrationWorkManager
import com.example.fitlife.worker.HydrationWorker
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {
    
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var prefs: Prefs
    private lateinit var hydrationWorkManager: HydrationWorkManager
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val today = dateFormat.format(Date())
    
    private val intervalOptions = listOf(
        "30 minutes" to 30,
        "60 minutes" to 60,
        "120 minutes" to 120
    )
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        prefs = Prefs(requireContext())
        hydrationWorkManager = HydrationWorkManager(requireContext())
        
        setupWelcomeMessage()
        updateProgress()
        setupHydrationSettings()
        setupClickListeners()
    }
    
    private fun setupWelcomeMessage() {
        // Get user name from preferences or use default
        val userName = prefs.getUserName() ?: "Heshani"
        binding.textWelcome.text = "Welcome, $userName 👋"
    }
    
    private fun updateProgress() {
        val habits = prefs.getHabits()
        // Only consider active habits for progress, to match Habits screen and widget
        val activeHabits = habits.filter { it.isActive }
        val completedCount = activeHabits.count { habit ->
            habit.completedDates.contains(today)
        }
        val totalCount = activeHabits.size

        binding.textProgressCount.text = "$completedCount / $totalCount completed"

        val progress = if (totalCount > 0) {
            (completedCount * 100) / totalCount
        } else {
            0
        }

        binding.progressBarHabits.progress = progress
    }
    
    private fun setupHydrationSettings() {
        // Setup interval spinner
        val intervalAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            intervalOptions.map { it.first }
        )
        binding.spinnerInterval.setAdapter(intervalAdapter)
        
        // Load current settings
        val isEnabled = prefs.isHydrationEnabled()
        val intervalMinutes = prefs.getHydrationIntervalMinutes()
        
        // Set switch state
        binding.switchHydrationReminders.isChecked = isEnabled
        
        // Set interval spinner
        val intervalIndex = intervalOptions.indexOfFirst { it.second == intervalMinutes }
        if (intervalIndex >= 0) {
            binding.spinnerInterval.setText(intervalOptions[intervalIndex].first, false)
        }
        
        updateIntervalSpinnerState(isEnabled)
    }
    
    private fun updateIntervalSpinnerState(isEnabled: Boolean) {
        binding.spinnerInterval.isEnabled = isEnabled
        binding.layoutIntervalSelector.isEnabled = isEnabled
        
        // Update visual state
        if (isEnabled) {
            binding.layoutIntervalSelector.alpha = 1.0f
        } else {
            binding.layoutIntervalSelector.alpha = 0.5f
        }
    }
    
    private fun setupClickListeners() {
        // Hydration toggle switch listener
        binding.switchHydrationReminders.setOnCheckedChangeListener { _, isChecked ->
            prefs.setHydrationEnabled(isChecked)
            updateIntervalSpinnerState(isChecked)
        }
        
        // Interval spinner listener
        binding.spinnerInterval.setOnItemClickListener { _, _, position, _ ->
            val selectedInterval = intervalOptions[position].second
            prefs.setHydrationIntervalMinutes(selectedInterval)
        }
        
        // Save hydration settings button
        binding.btnSaveHydrationSettings.setOnClickListener {
            saveHydrationSettings()
        }
        
        // Test notification button
        binding.btnTestNotification.setOnClickListener {
            testNotification()
        }
    }
    
    private fun saveHydrationSettings() {
        val isEnabled = prefs.isHydrationEnabled()
        val intervalMinutes = prefs.getHydrationIntervalMinutes()
        
        if (isEnabled) {
            // Check notification permissions first
            if (!areNotificationsEnabled()) {
                showNotificationPermissionDialog()
                return
            }
            
            // Schedule hydration reminders
            hydrationWorkManager.scheduleHydrationReminders(intervalMinutes)
            Toast.makeText(requireContext(), "Hydration reminders enabled! You'll receive notifications every $intervalMinutes minutes.", Toast.LENGTH_SHORT).show()
        } else {
            // Cancel hydration reminders
            hydrationWorkManager.cancelHydrationReminders()
            Toast.makeText(requireContext(), "Hydration reminders disabled", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showNotificationPermissionDialog() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Enable Notifications")
            .setMessage("To receive hydration reminders, please enable notifications for FitLife in your device settings.\n\nWould you like to open the app settings now?")
            .setPositiveButton("Open Settings") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun openAppSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = android.net.Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }
    
    private fun areNotificationsEnabled(): Boolean {
        return NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
    }
    
    private fun testNotification() {
        if (!areNotificationsEnabled()) {
            Toast.makeText(requireContext(), "Notifications are disabled. Please enable them in device settings.", Toast.LENGTH_LONG).show()
            return
        }
        
        try {
            // Create a test notification
            val notificationManager = NotificationManagerCompat.from(requireContext())
            val intent = Intent(requireContext(), MainActivity::class.java)
            val pendingIntent = android.app.PendingIntent.getActivity(
                requireContext(),
                0,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )
            
            val notification = androidx.core.app.NotificationCompat.Builder(requireContext(), HydrationWorker.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_alarm)
                .setContentTitle("💧 Test Hydration Reminder")
                .setContentText("This is a test notification to verify hydration reminders are working!")
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setCategory(androidx.core.app.NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
                .build()
            
            notificationManager.notify(9999, notification) // Use different ID for test
            Toast.makeText(requireContext(), "Test notification sent!", Toast.LENGTH_SHORT).show()
        } catch (_: SecurityException) {
            Toast.makeText(requireContext(), "Permission denied. Please enable notifications in device settings.", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to send test notification: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    
    override fun onResume() {
        super.onResume()
        updateProgress() // Refresh progress when returning to home
    }
    
    override fun onStart() {
        super.onStart()
        updateProgress() // Also refresh when fragment becomes visible
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
