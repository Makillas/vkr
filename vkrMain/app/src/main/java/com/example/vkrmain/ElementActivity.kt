package com.example.vkrmain

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.vkrmain.databinding.ActivityElementBinding
import com.example.vkrmain.db.AppSQLiteManager
import com.example.vkrmain.db.Blocks


class ElementActivity : AppCompatActivity() {

    private val myDbManager = AppSQLiteManager(this)
    private lateinit var binding:ActivityElementBinding
    private var element: Int = -1
    private var category: Int = -1
    private var category_system: Int = 1
    private lateinit var editErrorText: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityElementBinding.inflate(layoutInflater).also { setContentView(it.root) }
        category_system = intent.getIntExtra("category_system",-1)
        if(intent.getStringExtra("element_type") == "Learn"){
            binding.apply {
                questionElementTextView.text = intent.getStringExtra("element_question")
                answerElementTextView.text = intent.getStringExtra("element_answer")

                questionElementTextView.visibility = View.VISIBLE
                questionElementTextView.movementMethod = ScrollingMovementMethod()
                questionHintTextView.visibility = View.VISIBLE

                questionElementEditText.visibility = View.GONE
                questionHintEditView.visibility = View.GONE

                answerElementTextView.visibility = View.VISIBLE
                answerElementTextView.movementMethod = ScrollingMovementMethod()
                answerHintTextView.visibility = View.VISIBLE

                answerElementEditText.visibility = View.GONE
                answerHintEditView.visibility = View.GONE

                elementAction.text = getString(R.string.elementRepeat)
            }
            element = intent.getIntExtra("element_id", -1)
            category = intent.getIntExtra("element_category_id", -1)
        }

        if(intent.getStringExtra("element_type") == "Update"){
            binding.apply {
                questionElementEditText.setText(intent.getStringExtra("element_question"))
                answerElementEditText.setText(intent.getStringExtra("element_answer"))

                questionElementTextView.visibility = View.GONE
                questionHintTextView.visibility = View.GONE

                questionElementEditText.visibility = View.VISIBLE
                questionElementEditText.movementMethod = ScrollingMovementMethod()
                questionHintEditView.visibility = View.VISIBLE

                answerElementTextView.visibility = View.GONE
                answerHintTextView.visibility = View.GONE

                answerElementEditText.visibility = View.VISIBLE
                answerElementEditText.movementMethod = ScrollingMovementMethod()
                answerHintEditView.visibility = View.VISIBLE

                elementAction.text = getString(R.string.save)
            }
            element = intent.getIntExtra("element_id", -1)
            category = intent.getIntExtra("element_category_id", -1)
        }

        if(intent.getStringExtra("element_type") == "Create"){
            binding.apply {
                questionElementTextView.visibility = View.GONE
                questionHintTextView.visibility = View.GONE

                questionElementEditText.visibility = View.VISIBLE
                questionElementEditText.movementMethod = ScrollingMovementMethod()
                questionHintEditView.visibility = View.VISIBLE

                answerElementTextView.visibility = View.GONE
                answerHintTextView.visibility = View.GONE

                answerElementEditText.visibility = View.VISIBLE
                answerElementEditText.movementMethod = ScrollingMovementMethod()
                answerHintEditView.visibility = View.VISIBLE

                elementAction.text = getString(R.string.create)
            }
            category = intent.getIntExtra("element_category_id", -1)
        }
    }

    fun onClickElementAction(view: View){
        if(binding.elementAction.text == getString(R.string.elementRepeat))
        {
            if(myDbManager.searchBlockCountRepeat(element) == 0)
            {
                myDbManager.updateItemBlocks(element,1)
                Toast.makeText(this, getString(R.string.messageBlockLearn), Toast.LENGTH_SHORT).show()
                onBlockPressed()
            }
            else
            {
                val dialog = AlertDialog.Builder(this@ElementActivity)
                    .setTitle(getString(R.string.messageBlockLearnPlus))
                    .setPositiveButton(getString(R.string.messageBlockLearnNew), null)
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create()
                dialog.setOnShowListener {
                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener{
                        myDbManager.updateItemBlocks(element,0)
                        dialog.dismiss()
                        onBlockPressed()
                    }
                    dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener {
                        dialog.dismiss()
                        onBlockPressed()
                    }
                }
                dialog.show()
            }
        }

        if(binding.elementAction.text == getString(R.string.create))
        {
            if(binding.questionElementEditText.text.toString().isBlank()){
                binding.questionElementEditText.error = getString(R.string.messageEditEmpty)
            }
            else if(binding.answerElementEditText.text.toString().isBlank()){
                binding.answerElementEditText.error = getString(R.string.messageEditEmpty)
            }
            else if (editError()){
                binding.questionElementEditText.error = getString(R.string.messageEditErrorBlock)
            }
            else{
                myDbManager.insertToDbBlock(binding.questionElementEditText.text.toString(),
                                            binding.answerElementEditText.text.toString(),
                                            category)
                Toast.makeText(this, getString(R.string.messageBlockCreate), Toast.LENGTH_SHORT).show()
                onBlockPressed()
            }

        }

        if(binding.elementAction.text == getString(R.string.save))
        {
            if(binding.questionElementEditText.text.toString().isBlank()){
                binding.questionElementEditText.error = getString(R.string.messageEditEmpty)
            }
            else if(binding.answerElementEditText.text.toString().isBlank()){
                binding.answerElementEditText.error = getString(R.string.messageEditEmpty)
            }
            else if(editErrorText == binding.questionElementEditText.text.toString()){
                updateBlock()
            }
            else if(editError()){
                binding.questionElementEditText.error = getString(R.string.messageEditErrorBlock)
            }
            else{
                updateBlock()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        editErrorText = binding.questionElementEditText.text.toString()
        myDbManager.openDb()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    private fun updateBlock(){
        myDbManager.updateItemBlocks(
            element,
            binding.questionElementEditText.text.toString(),
            binding.answerElementEditText.text.toString())
        Toast.makeText(this, getString(R.string.messageBlockUpdate), Toast.LENGTH_SHORT).show()
        onBlockPressed()
    }

    private fun editError(): Boolean{
        var dataList = ArrayList<Blocks>()
        dataList = myDbManager.readDbDataBlocksElement()
        for(block in dataList){
            if(block.question == binding.questionElementEditText.text.toString()){
                return true
            }
        }
        return false
    }

    fun onClickElementCancel(view: View){
        onBlockPressed()
    }

    private fun onBlockPressed(){
        val intent = Intent(this, BlocksActivity::class.java)
        intent.putExtra("category_item", category)
        intent.putExtra("category_system", category_system)
        startActivity(intent)
    }


}