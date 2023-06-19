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
import com.example.vkrmain.databinding.ItemStatisticsRecyclerViewBinding

interface StatisticsActionListener { fun onStatisticsDetails(category: Categories) }

class StatisticsAdapter(
    private val statisticsActionListener: StatisticsActionListener,
    listMain: ArrayList<Categories>
) : RecyclerView.Adapter<StatisticsAdapter.MyHolder>(), View.OnClickListener {

    private var listArray = listMain

    override fun onClick(v: View) {
        val category = v.tag as Categories

        when(v.id){
            R.id.statisticsNameTextView ->{
                statisticsActionListener.onStatisticsDetails(category)
            }
        }

    }

    class MyHolder(private val binding: ItemStatisticsRecyclerViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(categories: Categories) = with(binding){
            statisticsNameTextView.tag = categories
            statisticsNameTextView.text = categories.name
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemStatisticsRecyclerViewBinding.inflate(inflater, parent, false)

        binding.statisticsNameTextView.setOnClickListener(this)

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


}
