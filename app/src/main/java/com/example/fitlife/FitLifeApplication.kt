//Initializes global settings (e.g., notifications, shared preferences, or dependency injection).

package com.example.fitlife

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager

class FitLifeApplication : Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()
}
