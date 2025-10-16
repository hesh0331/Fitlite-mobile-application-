package com.example.fitlife.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.fitlife.data.Prefs
import com.example.fitlife.model.MoodEntry

class MoodViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = Prefs(application.applicationContext)

    private val _entries = MutableLiveData<List<MoodEntry>>(prefs.getMoodEntries())
    val entries: LiveData<List<MoodEntry>> = _entries

    fun addMood(entry: MoodEntry) {
        val current = _entries.value?.toMutableList() ?: mutableListOf()
        current.add(entry)
        _entries.value = current
    }
}


