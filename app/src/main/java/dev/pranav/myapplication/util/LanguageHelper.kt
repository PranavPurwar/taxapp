package dev.pranav.myapplication.util

import androidx.fragment.app.FragmentActivity
import java.util.Locale


fun FragmentActivity.setLocale(locale: Locale) {
    Locale.setDefault(locale)
    val resources = resources
    val config = resources.configuration
    config.setLocale(locale)
    resources.updateConfiguration(config, resources.displayMetrics)
}
