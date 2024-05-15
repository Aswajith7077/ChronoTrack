package com.example.stopwatch

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class LapAdapter(private val lapArrayList:ArrayList<LapElements>):
    RecyclerView.Adapter<LapAdapter.LapViewHolder>() {

    class LapViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val lapId = itemView.findViewById<TextView>(R.id.lap_id);
        val lapTime = itemView.findViewById<TextView>(R.id.lap_time)
        val lapDiff = itemView.findViewById<TextView>(R.id.lap_diff);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LapViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.lap_layout,parent,false);
        return LapViewHolder(itemView);
    }

    override fun getItemCount(): Int {
        return lapArrayList.size;
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LapViewHolder, position: Int) {
        val currentIndex = lapArrayList[position];
        holder.lapId.text = "#Lap "+currentIndex.lapId;
        holder.lapDiff.text = currentIndex.lapDiff;
        holder.lapTime.text = currentIndex.lapTime;
    }

}