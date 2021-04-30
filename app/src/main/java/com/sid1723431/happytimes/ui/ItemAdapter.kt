package com.sid1723431.happytimes.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sid1723431.happytimes.data.Habit
import com.sid1723431.happytimes.databinding.ItemRowBinding

class ItemAdapter(private val listener: OnItemClickListener) :
    ListAdapter<Habit, ItemAdapter.ItemsViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        //create a binding object
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemsViewHolder(binding)
    }
// called when a new item is created, called over and over again
    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
    //define how we bind the data
       val currentItem = getItem(position)
        holder.bind(currentItem)
    }
// with inner class, item adapter can be accessed outside
    //be called when the view holder is first created
   inner class ItemsViewHolder(private val binding : ItemRowBinding):
        RecyclerView.ViewHolder(binding.root){
        init{
            binding.apply{
                root.setOnClickListener{
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION){
                        val habit = getItem(position)
                        listener.onItemClick(habit)
                    }
                }
                checkBoxCompleted.setOnClickListener{
                    val position = adapterPosition
                    if(position != RecyclerView.NO_POSITION){
                        val habit = getItem(position)
                        listener.onCheckBoxClick(habit, checkBoxCompleted.isChecked)
                    }
                }
            }
        }

        fun bind(habit: Habit){
            binding.apply {
                checkBoxCompleted.isChecked = habit.completed
                textViewName.text = habit.name
                textViewName.paint.isStrikeThruText = habit.completed
                labelPriority.isVisible = habit.important
                textViewDisplayStart.text = habit.startDay
                textViewDisplayEnd.text = habit.endDay

            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(habit: Habit)
        fun onCheckBoxClick(habit: Habit, isChecked: Boolean)
    }

    class DiffCallback : DiffUtil.ItemCallback<Habit>(){
        override fun areItemsTheSame(oldItem: Habit, newItem: Habit)=
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Habit, newItem: Habit) =
            oldItem == newItem

    }

}