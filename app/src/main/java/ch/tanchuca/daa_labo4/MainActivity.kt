package ch.tanchuca.daa_labo4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.lifecycle.lifecycleScope
import ch.tanchuca.daa_labo4.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private val adapter = ImagesViewAdapter(lifecycleScope)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)

    }

    override fun onCreateOptionsMenu(menu: Menu?) : Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.main_menu_empty_cache -> {
                val workManager = WorkManager.getInstance(applicationContext)
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