package com.example.vkrmain

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vkrmain.databinding.ActivityBlocksBinding
import com.example.vkrmain.db.AppSQLiteManager
import com.example.vkrmain.db.Blocks
import com.example.vkrmain.db.BlocksActionListener
import com.example.vkrmain.db.BlocksAdapter
import kotlin.properties.Delegates

class BlocksActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBlocksBinding
    private lateinit var myAdapter : BlocksAdapter
    private var category_id by Delegates.notNull<Int>()
    private var category_system by Delegates.notNull<Int>()
    private val myDbManager = AppSQLiteManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBlocksBinding.inflate(layoutInflater).also { setContentView(it.root) }
        category_id = intent.getIntExtra("category_item", -1)
        category_system = intent.getIntExtra("category_system", -1)
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
                        onStatisticsPressed()
                    }
                }
                blocksDrawerLayout.closeDrawer(GravityCompat.END)
                true
            }
        }
        init()

    }

    override fun onResume() {
        super.onResume()
        /*supportActionBar?.title = category_name*/
        binding.blocksButton.isEnabled = category_system == 0
        myDbManager.openDb()
        fillAdapter()
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
            if(binding.blocksDrawerLayout.isDrawerOpen(GravityCompat.END))
            {
                binding.blocksDrawerLayout.closeDrawer(GravityCompat.END)
            }
            else
            {
                binding.blocksDrawerLayout.openDrawer(GravityCompat.END)
            }
        }
        return true
    }

    private fun onStudyPressed(){
        val intent = Intent(this, StudyActivity::class.java)
        startActivity(intent)
    }

    private fun onStatisticsPressed(){
        val intent = Intent(this, StatisticsActivity::class.java)
        startActivity(intent)
    }

    private fun onMainPressed(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun init(){
        myAdapter = BlocksAdapter(object: BlocksActionListener {
            override fun onBlockDetails(blocks: Blocks) {
                startActivity(Intent(this@BlocksActivity, ElementActivity::class.java).apply {
                    putExtra("element_id", blocks.id)
                    putExtra("element_category_id", blocks.category_id)
                    putExtra("element_question", blocks.question)
                    putExtra("element_answer", blocks.answer)
                    putExtra("element_type", "Learn")
                    putExtra("category_system", category_system)
                })
            }

            override fun onBlockDelete(blocks: Blocks, pos: Int) {
                val dialog = AlertDialog.Builder(this@BlocksActivity)
                    .setTitle(getString(R.string.messageAdapterDeleteBlock))
                    .setPositiveButton(getString(R.string.delete), null)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create()
                dialog.setOnShowListener {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
                        myAdapter.removeItem(pos,myDbManager)
                        dialog.dismiss()
                    }
                }
                dialog.show()
            }

            override fun onBlockUpdate(blocks: Blocks) {
                startActivity(Intent(this@BlocksActivity, ElementActivity::class.java).apply {
                    putExtra("element_id", blocks.id)
                    putExtra("element_category_id", blocks.category_id)
                    putExtra("element_question", blocks.question)
                    putExtra("element_answer", blocks.answer)
                    putExtra("element_type", "Update")
                    putExtra("category_system", category_system)
                })
            }
        }, ArrayList(), category_system)
        binding.blocksRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.blocksRecyclerView.adapter = myAdapter
    }

    private fun fillAdapter(){
        myAdapter.updateAdapter(myDbManager.readDbDataBlocks(category_id))
    }

    fun onClickNewBlock(view: View){
        startActivity(Intent(this, ElementActivity::class.java).apply {
            putExtra("element_category_id", category_id)
            putExtra("element_type", "Create")
            putExtra("category_system", category_system)
        })
    }

}

