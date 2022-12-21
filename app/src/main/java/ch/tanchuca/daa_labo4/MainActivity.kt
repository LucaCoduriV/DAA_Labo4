package ch.tanchuca.daa_labo4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import ch.tanchuca.daa_labo4.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val adapter = ImagesViewAdapter(lifecycleScope)
    private val workManager = WorkManager.getInstance(applicationContext)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)

        val constraints = Constraints.Builder()
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(false)
            .setRequiresDeviceIdle(true)
            .build()
        val periodicCacheCleaning = PeriodicWorkRequestBuilder<CacheCleaningWork>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
            .build()
        workManager.enqueue(periodicCacheCleaning);

    }

    override fun onCreateOptionsMenu(menu: Menu?) : Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.main_menu_empty_cache -> {
                val myWorkRequest = OneTimeWorkRequestBuilder<CacheCleaningWork>().build()
                workManager.enqueue(myWorkRequest)
                adapter.notifyDataSetChanged()
                return true
            }
            else-> super.onOptionsItemSelected(item) }
    }

    override fun onDestroy() {
        super.onDestroy()
        var viewHolders = adapter.getViewHolders()
        viewHolders.forEach {
            it.stopLoading()
        }
    }
}