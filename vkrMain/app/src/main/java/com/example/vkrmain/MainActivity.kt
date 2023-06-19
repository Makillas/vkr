package com.example.vkrmain

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vkrmain.databinding.ActivityMainBinding
import com.example.vkrmain.databinding.CategoriesAlertDialogBinding
import com.example.vkrmain.db.AppSQLiteManager
import com.example.vkrmain.db.Categories
import com.example.vkrmain.db.CategoriesActionListener
import com.example.vkrmain.db.CategoriesAdapter
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(){

    private lateinit var binding: ActivityMainBinding
    private lateinit var myAdapter : CategoriesAdapter
    private var enteredText by Delegates.notNull<String>()
    private var seekBarRepeat by Delegates.notNull<Int>()
    private lateinit var editErrorText: String
    private val myDbManager = AppSQLiteManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            navMenuNavigationView.setNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.menuCategories -> {
                        /*return@setNavigationItemSelectedListener true*/
                    }
                    R.id.menuStudy -> {
                        onStudyPressed()
                    }
                    R.id.menuStatistics -> {
                        onStatisticsPressed()
                    }
                }
                categoriesDrawerLayout.closeDrawer(GravityCompat.END)
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.burger_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.burgerMenu) {
            if(binding.categoriesDrawerLayout.isDrawerOpen(GravityCompat.END))
            {
                binding.categoriesDrawerLayout.closeDrawer(GravityCompat.END)
            }
            else
            {
                binding.categoriesDrawerLayout.openDrawer(GravityCompat.END)
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

    private fun init(){
        myAdapter = CategoriesAdapter(object: CategoriesActionListener{
            override fun onCategoryDetails(category: Categories) {
                val intent = Intent(this@MainActivity, BlocksActivity::class.java)
                intent.putExtra("category_item", category.id)
                intent.putExtra("category_system", category.system)
                startActivity(intent)
            }

            override fun onCategoryDelete(category: Categories, pos: Int) {
                val dialog = AlertDialog.Builder(this@MainActivity)
                    .setTitle(getString(R.string.messageAdapterDeleteCategory))
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

            override fun onCategoryUpdate(category: Categories) {
                showAlertDialogCategoryUpdate(category)
            }
        }, ArrayList())
        binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.categoriesRecyclerView.adapter = myAdapter
    }

    private fun fillAdapter(){
        myAdapter.updateAdapter(myDbManager.readDbDataCategories())
    }

    private fun showAlertDialogCategoryUpdate(category: Categories) {
        val dialogBinding = CategoriesAlertDialogBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.updateCollection))
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.update), null)
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
        dialog.setOnShowListener {
            dialogBinding.categoryEditText.setText(category.name)
            dialogBinding.repeatSeekBar.progress = category.max_count_repeat
            dialogBinding.categoryEditText.isEnabled = category.system == 0
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
                enteredText = dialogBinding.categoryEditText.text.toString()
                seekBarRepeat = dialogBinding.repeatSeekBar.progress
                if(enteredText.isBlank()){
                    dialogBinding.categoryEditText.error = getString(R.string.messageEditEmpty)
                    return@setOnClickListener
                }
                else if(category.name == dialogBinding.categoryEditText.text.toString()){
                    if(category.max_count_repeat == dialogBinding.repeatSeekBar.progress){
                        myDbManager.updateItemCategories(category.id, enteredText, category.system, seekBarRepeat.toString())
                        fillAdapter()
                    }
                    else{
                        categoryUpdateConfirmation(category)
                    }
                }
                else if(editError()){
                    dialogBinding.categoryEditText.error = getString(R.string.messageEditError)
                    return@setOnClickListener
                }
                else{
                    if(category.max_count_repeat == dialogBinding.repeatSeekBar.progress){
                        myDbManager.updateItemCategories(category.id, enteredText, category.system, seekBarRepeat.toString())
                        fillAdapter()
                    }
                    else{
                        categoryUpdateConfirmation(category)
                    }
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun categoryUpdateConfirmation(category: Categories){
        val dialog = AlertDialog.Builder(this@MainActivity)
            .setMessage(getString(R.string.updateCollectionConfirmation))
            .setPositiveButton(getString(R.string.updateContinue), null)
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
                myDbManager.updateItemCategories(category.id, enteredText, category.system, seekBarRepeat.toString())
                myDbManager.updateCountRepeatInCategory(category.id)
                fillAdapter()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun showAlertDialogCategoryCreate() {
        val dialogBinding = CategoriesAlertDialogBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.createCollection))
            .setView(dialogBinding.root)
            .setPositiveButton(getString(R.string.create), null)
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
                enteredText = dialogBinding.categoryEditText.text.toString()
                if(enteredText.isBlank()){
                    dialogBinding.categoryEditText.error = getString(R.string.messageEditEmpty)
                    return@setOnClickListener
                }
                seekBarRepeat = dialogBinding.repeatSeekBar.progress
                if(editError()){
                    dialogBinding.categoryEditText.error = getString(R.string.messageEditError)
                    return@setOnClickListener
                }
                myDbManager.insertToDbCategories(enteredText, seekBarRepeat.toString())
                fillAdapter()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun editError(): Boolean{
        var dataList = ArrayList<Categories>()
        dataList = myDbManager.readDbDataCategories()
        for(name in dataList){
            if(name.name == enteredText){
                return true
            }
        }
        return false
    }

    fun onClickNewCategory(view: View){
        showAlertDialogCategoryCreate()
    }


}

