package com.example.vkrmain.db

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.vkrmain.R
import com.example.vkrmain.databinding.ItemCategoriesRecyclerViewBinding

interface BlocksActionListener {

    fun onBlockDelete(blocks: Blocks, pos: Int)

    fun onBlockUpdate(blocks: Blocks)

    fun onBlockDetails(blocks: Blocks)

}

class BlocksAdapter(
    private val blocksActionListener: BlocksActionListener,
    listMain: ArrayList<Blocks>,
    private val category_system: Int
) : RecyclerView.Adapter<BlocksAdapter.MyHolder>(), View.OnClickListener {

    private var listArray = listMain

    override fun onClick(v: View) {
        val block = v.tag as Blocks

        when(v.id){
            R.id.categoryNameTextView ->{
                blocksActionListener.onBlockDetails(block)
            }
            R.id.moreImageViewButton ->{
                showPopupMenu(v)
            }
        }

    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val context = view.context
        val block = view.tag as Blocks
        val position = listArray.indexOfFirst { it.id == block.id }

        popupMenu.menu.add(0, ID_UPDATE, Menu.NONE, context.getString(R.string.ID_UPDATE)).apply{
            isEnabled = category_system == 0
        }
        popupMenu.menu.add(0, ID_DELETE, Menu.NONE, context.getString(R.string.ID_DELETE)).apply{
            isEnabled = category_system == 0
        }

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                ID_UPDATE -> {
                    blocksActionListener.onBlockUpdate(block)
                }
                ID_DELETE ->{
                    blocksActionListener.onBlockDelete(block, position)
                }
            }
            return@setOnMenuItemClickListener true
        }

        popupMenu.show()
    }

    companion object{
        private const val ID_UPDATE = 1
        private const val ID_DELETE = 2
    }

    class MyHolder(private val binding: ItemCategoriesRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(blocks: Blocks) = with(binding){
            categoryNameTextView.tag = blocks
            moreImageViewButton.tag = blocks
            categoryNameTextView.text = blocks.question
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoriesRecyclerViewBinding.inflate(inflater, parent, false)

        binding.categoryNameTextView.setOnClickListener(this)
        binding.moreImageViewButton.setOnClickListener(this)

        return MyHolder(binding)
    }

    override fun getItemCount(): Int { return listArray.size }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setData(listArray[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(listItems:List<Blocks>){
        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()
    }

    fun removeItem(pos: Int, dbManager: AppSQLiteManager){
        dbManager.removeItemFromDbBlock(listArray[pos].id)
        listArray.removeAt(pos)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(pos)
    }


}
