package com.olecco.android.companyprofile.ui.piechart

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.olecco.android.companyprofile.R

class PieChartLegendAdaper : RecyclerView.Adapter<PieChartLegendAdaper.ItemViewHolder>() {

    var pieChartAdapter: PieChartAdapter? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
                R.layout.pie_chart_legend_item, parent, false)
        return ItemViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return pieChartAdapter?.getSegmentCount() ?: 0
    }

    override fun onBindViewHolder(viewHolder: ItemViewHolder, position: Int) {
        viewHolder.nameView.text = pieChartAdapter?.getSegmentName(position) ?: ""
        viewHolder.setColor(pieChartAdapter?.getSegmentColor(position) ?: Color.BLACK)
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameView: TextView
        val colorView: ImageView

        init {
            nameView = itemView.findViewById(R.id.text)
            colorView = itemView.findViewById(R.id.color)
        }

        fun setColor(color: Int) {
            colorView.setColorFilter(color)
        }

    }

}