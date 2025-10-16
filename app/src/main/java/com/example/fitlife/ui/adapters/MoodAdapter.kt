package com.example.fitlife.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitlife.R
import com.example.fitlife.model.MoodEntry
import java.text.SimpleDateFormat
import java.util.*

class MoodAdapter(
    private var moodEntries: List<MoodEntry> = emptyList(),
    private val onMoodShare: (MoodEntry) -> Unit,
    private val onMoodDelete: (MoodEntry) -> Unit
) : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {

    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

    class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textEmoji: TextView = itemView.findViewById(R.id.text_mood_emoji)
        val textMoodLevel: TextView = itemView.findViewById(R.id.text_mood_level)
        val textTime: TextView = itemView.findViewById(R.id.text_mood_time)
        val textNote: TextView = itemView.findViewById(R.id.text_mood_note)
        val btnShare: ImageButton = itemView.findViewById(R.id.btn_share_mood)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btn_delete_mood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_mood, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val moodEntry = moodEntries[position]
        
        // Set mood details
        holder.textEmoji.text = moodEntry.moodLevel.emoji
        holder.textMoodLevel.text = moodEntry.moodLevel.name.replace("_", " ")
        
        // Format time display
        val timeText = timeFormat.format(moodEntry.date)
        val dateText = dateFormat.format(moodEntry.date)
        holder.textTime.text = "$dateText, $timeText"
        
        // Set note if available
        if (moodEntry.notes.isNotEmpty()) {
            holder.textNote.text = moodEntry.notes
            holder.textNote.visibility = View.VISIBLE
        } else {
            holder.textNote.visibility = View.GONE
        }
        
        // Set up click listeners
        holder.btnShare.setOnClickListener {
            onMoodShare(moodEntry)
        }
        
        holder.btnDelete.setOnClickListener {
            onMoodDelete(moodEntry)
        }
    }

    override fun getItemCount(): Int = moodEntries.size

    fun updateMoodEntries(newMoodEntries: List<MoodEntry>) {
        moodEntries = newMoodEntries.sortedByDescending { it.date }
        notifyDataSetChanged()
    }

    fun getMoodEntries(): List<MoodEntry> = moodEntries

    fun getMoodSummary(): String {
        if (moodEntries.isEmpty()) return "No mood entries yet"
        
        val totalEntries = moodEntries.size
        val averageMood = moodEntries.map { it.moodLevel.value }.average()
        val moodLevel = when {
            averageMood >= 4.5 -> "Very Happy"
            averageMood >= 3.5 -> "Happy"
            averageMood >= 2.5 -> "Neutral"
            averageMood >= 1.5 -> "Sad"
            else -> "Very Sad"
        }
        
        val recentEntries = moodEntries.take(3)
        val recentMoods = recentEntries.joinToString(", ") { it.moodLevel.emoji }
        
        return buildString {
            appendLine("📊 Mood Summary")
            appendLine("Total entries: $totalEntries")
            appendLine("Average mood: $moodLevel")
            appendLine("Recent moods: $recentMoods")
        }
    }
}
