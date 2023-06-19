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

interface CategoriesActionListener {

    fun onCategoryDelete(category: Categories, pos: Int)

    fun onCategoryUpdate(category: Categories)

    fun onCategoryDetails(category: Categories)

}

class CategoriesAdapter(
    private val categoriesActionListener: CategoriesActionListener,
    listMain: ArrayList<Categories>
) : RecyclerView.Adapter<CategoriesAdapter.MyHolder>(), View.OnClickListener {

    private var listArray = listMain

    override fun onClick(v: View) {
        val category = v.tag as Categories

        when(v.id){
            R.id.categoryNameTextView ->{
                categoriesActionListener.onCategoryDetails(category)
            }
            R.id.moreImageViewButton ->{
                showPopupMenu(v)
            }
        }

    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        val context = view.context
        val category = view.tag as Categories
        val position = listArray.indexOfFirst { it.id == category.id }

        popupMenu.menu.add(0, ID_UPDATE, Menu.NONE, context.getString(R.string.ID_UPDATE))
        popupMenu.menu.add(0, ID_DELETE, Menu.NONE, context.getString(R.string.ID_DELETE)).apply{
            isEnabled = category.system == 0
        }

        popupMenu.setOnMenuItemClickListener {
            when(it.itemId){
                ID_UPDATE -> {
                    categoriesActionListener.onCategoryUpdate(category)
                }
                ID_DELETE ->{
                    categoriesActionListener.onCategoryDelete(category, position)
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
        fun setData(categories: Categories) = with(binding){
            categoryNameTextView.tag = categories
            moreImageViewButton.tag = categories
            categoryNameTextView.text = categories.name

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoriesRecyclerViewBinding.inflate(inflater, parent, false)
//        return MyHolder(inflater.inflate(R.layout.item_categories_recycler_view, parent, false))

        binding.categoryNameTextView.setOnClickListener(this)
        binding.moreImageViewButton.setOnClickListener(this)

        return MyHolder(binding)
    }

    override fun getItemCount(): Int { return listArray.size }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.setData(listArray[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAdapter(listItems:List<Categories>){
        listArray.clear()
        listArray.addAll(listItems)
        notifyDataSetChanged()
    }

    fun removeItem(pos: Int, dbManager: AppSQLiteManager){
        dbManager.removeItemFromDb(listArray[pos].id)
        listArray.removeAt(pos)
        notifyItemRangeChanged(0, listArray.size)
        notifyItemRemoved(pos)
    }


}
