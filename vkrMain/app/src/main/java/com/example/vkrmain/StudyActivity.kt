package com.example.vkrmain

import android.content.Intent
import android.os.Bundle
import android.text.method.ArrowKeyMovementMethod
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.example.vkrmain.databinding.ActivityStudyBinding
import com.example.vkrmain.db.AppSQLiteManager
import com.example.vkrmain.db.Blocks
import com.example.vkrmain.db.Categories
import kotlin.properties.Delegates

class StudyActivity: AppCompatActivity() {

    private lateinit var binding: ActivityStudyBinding
    private val myDbManager = AppSQLiteManager(this)
    private var dataListStudy = ArrayList<Blocks>()
    private var questionStudy by Delegates.notNull<String>()
    private var answerStudy by Delegates.notNull<String>()
    private var maxCountRepeat by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudyBinding.inflate(layoutInflater).also { setContentView(it.root) }
        binding.apply {
            navMenuNavigationView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.menuCategories -> {
                        onMainPressed()
                    }
                    R.id.menuStudy -> {
                        /*return@setNavigationItemSelectedListener true*/
                    }
                    R.id.menuStatistics -> {
                        onStatisticsPressed()
                    }
                }
                studyDrawerLayout.closeDrawer(GravityCompat.END)
                true
            }
        }

    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
        dataListStudy = myDbManager.readDbDataBlocksStudy()
        if(dataListStudy.isNotEmpty()){
            questionStudy = dataListStudy[0].question
            answerStudy = dataListStudy[0].answer
            binding.textViewStudy.text = questionStudy
        }
        else{
            binding.textViewStudy.text = getString(R.string.empty)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.burger_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.burgerMenu) {
            if(binding.studyDrawerLayout.isDrawerOpen(GravityCompat.END))
            {
                binding.studyDrawerLayout.closeDrawer(GravityCompat.END)
            }
            else
            {
                binding.studyDrawerLayout.openDrawer(GravityCompat.END)
            }
        }
        return true
    }

    private fun onStatisticsPressed(){
        val intent = Intent(this, StatisticsActivity::class.java)
        startActivity(intent)
    }

    private fun onMainPressed(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun onClickKnowStudy(view: View){
        if(dataListStudy.isNotEmpty()){
            maxCountRepeat = myDbManager.searchCategoryCountRepeat(dataListStudy[0].category_id)
            if(dataListStudy[0].count_repeat == maxCountRepeat-1){
                Toast.makeText(this, R.string.messageAllCountRepeat, Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this, R.string.messageOneCountRepeat, Toast.LENGTH_SHORT).show()
            }
            myDbManager.updateItemBlocksStudy(
                dataListStudy[0].id,
                dataListStudy[0].count_repeat+1,
                (System.currentTimeMillis() / 1000) + choiceUnixTime(dataListStudy[0].count_repeat+1)
            )
        }
        dataListStudy = myDbManager.readDbDataBlocksStudy()
        if(dataListStudy.isNotEmpty()){
            questionStudy = dataListStudy[0].question
            answerStudy = dataListStudy[0].answer
            binding.textViewStudy.text = questionStudy
        }
        else{
            Toast.makeText(this, getString(R.string.empty), Toast.LENGTH_SHORT).show()
            binding.textViewStudy.text = getString(R.string.empty)
        }
    }

    fun onClickDontKnowStudy(view: View){
        if(dataListStudy.isNotEmpty()){
            myDbManager.updateItemBlocksStudy(
                dataListStudy[0].id,
                0,
                (System.currentTimeMillis() / 1000)
            )
            Toast.makeText(this, getString(R.string.messageBlockDontKnow), Toast.LENGTH_SHORT).show()
        }
        dataListStudy = myDbManager.readDbDataBlocksStudy()
        if(dataListStudy.isNotEmpty()){
            questionStudy = dataListStudy[0].question
            answerStudy = dataListStudy[0].answer
            binding.textViewStudy.text = questionStudy
        }
        else{
            Toast.makeText(this, getString(R.string.empty), Toast.LENGTH_SHORT).show()
            binding.textViewStudy.text = getString(R.string.empty)
        }
    }

    fun onClickStudyTextView(view: View){
        if(binding.textViewStudy.text == getString(R.string.empty)){
            Toast.makeText(this, getString(R.string.empty), Toast.LENGTH_SHORT).show()
        }
        else if(binding.textViewStudy.text == questionStudy){
            binding.textViewStudy.text = answerStudy
            binding.textViewStudyHint.text = getString(R.string.answerElement)
        }
        else{
            binding.textViewStudy.text = questionStudy
            binding.textViewStudyHint.text = getString(R.string.questionElement)
        }
    }

    private fun choiceUnixTime(item: Int): Long{
        val itemMain: Long = when(item){
            1 -> 3600        // 1 hour
            2 -> 86400       // 1 day
            3 -> 259200      // 3 days
            4 -> 604800      // 1 week
            5 -> 1814400     // 3 weeks
            6 -> 5184000     // 2 months
            7 -> 15552000    // 6 months
            else -> 0
        }
        return itemMain
    }

}