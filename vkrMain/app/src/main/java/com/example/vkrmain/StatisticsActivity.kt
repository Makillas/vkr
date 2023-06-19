package com.example.vkrmain


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vkrmain.databinding.ActivityStatisticsBinding
import com.example.vkrmain.db.AppSQLiteManager
import com.example.vkrmain.db.Categories
import com.example.vkrmain.db.StatisticsActionListener
import com.example.vkrmain.db.StatisticsAdapter

class StatisticsActivity : AppCompatActivity() {

    private lateinit var myAdapter : StatisticsAdapter
    private val myDbManager = AppSQLiteManager(this)
    private lateinit var binding: ActivityStatisticsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater).also { setContentView(it.root) }

        binding.apply {
            navMenuNavigationView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.menuCategories -> {
                        onMainPressed()
                    }
                    R.id.menuStudy -> {
                        onStudyPressed()
                    }
                    R.id.menuStatistics -> {
                        /*return@setNavigationItemSelectedListener true*/
                    }
                }
                statisticsDrawerLayout.closeDrawer(GravityCompat.END)
                true
            }
        }
        init()
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
        fillAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    private fun init(){
        myAdapter = StatisticsAdapter(object: StatisticsActionListener{
            @SuppressLint("SuspiciousIndentation")
            override fun onStatisticsDetails(category: Categories) {
                Toast.makeText(this@StatisticsActivity, category.name, Toast.LENGTH_SHORT).show()
                val intent = Intent(this@StatisticsActivity, HistogramActivity::class.java)
                    intent.putExtra("statistics_item", category.id)
                    intent.putExtra("statistics_nameCategory", category.name)
                    startActivity(intent)
            }
        }, ArrayList())
        binding.statisticsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.statisticsRecyclerView.adapter = myAdapter
    }

    private fun fillAdapter(){
        myAdapter.updateAdapter(myDbManager.readDbDataCategories())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.burger_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.burgerMenu) {
            if(binding.statisticsDrawerLayout.isDrawerOpen(GravityCompat.END))
            {
                binding.statisticsDrawerLayout.closeDrawer(GravityCompat.END)
            }
            else
            {
                binding.statisticsDrawerLayout.openDrawer(GravityCompat.END)
            }
        }
        return true
    }

    private fun onStudyPressed(){
        val intent = Intent(this, StudyActivity::class.java)
        startActivity(intent)
    }

    private fun onMainPressed(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}