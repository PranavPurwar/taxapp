package dev.pranav.myapplication

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import dev.pranav.myapplication.databinding.ActivityMainBinding
import dev.pranav.myapplication.util.setLocale
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("prefs", MODE_PRIVATE)
        this.setLocale(Locale(prefs.getString("language", "en")!!))

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStackImmediate()
            } else {
                finish()
            }
        }

        supportFragmentManager.beginTransaction().add(R.id.fragment_container, HomeFragment())
            .commit()

        supportFragmentManager.addFragmentOnAttachListener { _, fragment ->
            if (fragment is HomeFragment) {
                binding.toolbar.navigationIcon = null
            } else {
                binding.toolbar.navigationIcon =
                    ContextCompat.getDrawable(this, R.drawable.round_arrow_back_ios_new_24)
            }
            supportActionBar?.title = fragment.toString()
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val fragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
            if (fragment is HomeFragment) {
                binding.toolbar.navigationIcon = null
            } else {
                binding.toolbar.navigationIcon =
                    ContextCompat.getDrawable(this, R.drawable.round_arrow_back_ios_new_24)
            }
            supportActionBar?.title =
                fragment.toString()
        }

        supportFragmentManager.addFragmentOnAttachListener { fragmentManager, fragment ->
            if (fragment is HomeFragment) {
                binding.toolbar.navigationIcon = null
            } else {
                binding.toolbar.navigationIcon =
                    ContextCompat.getDrawable(this, R.drawable.round_arrow_back_ios_new_24)
            }
            supportActionBar?.title = fragment.toString()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStackImmediate()
                } else {
                    finish()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, SettingsFragment())
                    addToBackStack(null)
                    commit()
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
