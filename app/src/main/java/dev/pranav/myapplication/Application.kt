package dev.pranav.myapplication

import android.app.Application
import com.google.android.material.color.DynamicColors

class Application : Application() {
    override fun onCreate() {
        super.onCreate()

        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}
