package com.example.vkrmain

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.vkrmain.databinding.ActivityHistogramBinding
import android.graphics.Color
import android.view.View
import com.example.vkrmain.db.AppSQLiteManager
import com.example.vkrmain.db.Blocks
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class HistogramActivity: AppCompatActivity() {

    private var category: Int = -1
    private val myDbManager = AppSQLiteManager(this)

    private lateinit var binding: ActivityHistogramBinding
    lateinit var barChart: BarChart
    lateinit var barData: BarData
    lateinit var barDataSet: BarDataSet
    lateinit var barEntriesList: ArrayList<BarEntry>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistogramBinding.inflate(layoutInflater).also { setContentView(it.root) }
    }

    override fun onResume() {
        super.onResume()
        myDbManager.openDb()

        category = intent.getIntExtra("statistics_item", -1)
        val category_name: String? = intent.getStringExtra("statistics_nameCategory")
        barChart = binding.idBarChart
        getBarChartData()
        barDataSet = BarDataSet(barEntriesList, category_name)
        barData = BarData(barDataSet)
        barChart.data = barData
        barDataSet.valueTextColor = Color.BLACK
        barDataSet.color = resources.getColor(R.color.histogram)
        barDataSet.valueTextSize = 12f
        barChart.description.isEnabled = false
    }

    private fun getBarChartData() {
        barEntriesList = ArrayList()
        val dataListStudy: ArrayList<Blocks> = myDbManager.readDbDataBlocks(category)
        if(dataListStudy.isEmpty()){
            binding.textViewHistogram.visibility = View.VISIBLE
            binding.idBarChart.visibility = View.GONE
        }
        else{
            for(element in dataListStudy.indices){
                binding.textViewHistogram.visibility = View.GONE
                binding.idBarChart.visibility = View.VISIBLE
                barEntriesList.add(BarEntry(1f * element, 1f * dataListStudy[element].count_repeat))
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }


}