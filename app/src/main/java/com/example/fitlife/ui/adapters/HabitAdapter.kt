package com.example.fitlife.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitlife.R
import com.example.fitlife.model.Habit
import java.text.SimpleDateFormat
import java.util.*

class HabitAdapter(
    private var habits: List<Habit> = emptyList(),
    private val onHabitComplete: (Habit, Boolean) -> Unit,
    private val onHabitEdit: (Habit) -> Unit,
    private val onHabitDelete: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val today = dateFormat.format(Date())
    private var lastClickTime = 0L
    private val clickDelay = 500L // 500ms delay between clicks

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkboxComplete: CheckBox = itemView.findViewById(R.id.checkbox_complete)
        val textTitle: TextView = itemView.findViewById(R.id.text_habit_title)
        val textCategory: TextView = itemView.findViewById(R.id.text_habit_category)
        val textStatus: TextView = itemView.findViewById(R.id.text_habit_status)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btn_edit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habits[position]
        
        // Set habit details
        holder.textTitle.text = habit.name
        holder.textCategory.text = habit.category.name.replace("_", " ")
        
        // Check if habit is completed today
        val isCompletedToday = habit.completedDates.contains(today)
        
        holder.checkboxComplete.isChecked = isCompletedToday
        
        // Set status text
        val statusText = buildString {
            append(habit.frequency.name.replace("_", " "))
            if (habit.streak > 0) {
                append(" • ${habit.streak} day streak")
            }
        }
        holder.textStatus.text = statusText
        
        // Set up click listeners with debouncing
        holder.checkboxComplete.setOnClickListener { view ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > clickDelay) {
                lastClickTime = currentTime
                val checkBox = view as CheckBox
                
                // Temporarily disable to prevent multiple clicks
                checkBox.isEnabled = false
                
                val isChecked = checkBox.isChecked
                onHabitComplete(habit, isChecked)
                
                // Re-enable after a short delay
                checkBox.postDelayed({
                    checkBox.isEnabled = true
                }, 200)
            }
        }
        
        holder.btnEdit.setOnClickListener { view ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > clickDelay) {
                lastClickTime = currentTime
                onHabitEdit(habit)
            }
        }
        
        holder.btnDelete.setOnClickListener { view ->
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > clickDelay) {
                lastClickTime = currentTime
                onHabitDelete(habit)
            }
        }
    }

    override fun getItemCount(): Int = habits.size

    fun updateHabits(newHabits: List<Habit>) {
        habits = newHabits
        notifyDataSetChanged()
    }

    fun getCompletedTodayCount(): Int {
        return habits.count { habit ->
            habit.completedDates.contains(today)
        }
    }

    fun getTotalActiveHabitsCount(): Int {
        return habits.count { it.isActive }
    }
}
