package com.imranmelikov.apponex_trackingexpense.presentation.dashboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.imranmelikov.apponex_trackingexpense.databinding.ItemTransactionLayoutBinding
import com.imranmelikov.apponex_trackingexpense.domain.model.Transaction
import kotlin.math.exp

class DashBoardAdapter:RecyclerView.Adapter<DashBoardAdapter.DashBoardViewHolder>() {
    class DashBoardViewHolder(val binding:ItemTransactionLayoutBinding):RecyclerView.ViewHolder(binding.root)

    var onClickItem:((Transaction)->Unit)?=null
    // DiffUtil for efficient RecyclerView updates
    private val diffUtil=object : DiffUtil.ItemCallback<Transaction>(){
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem==newItem
        }

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
            return oldItem==newItem
        }
    }
    private val recyclerDiffer= AsyncListDiffer(this,diffUtil)

    // Getter and setter for the list of transactions
    var transactionList:List<Transaction>
        get() = recyclerDiffer.currentList
        set(value) = recyclerDiffer.submitList(value)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashBoardViewHolder {
        val binding=ItemTransactionLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return DashBoardViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return transactionList.size
    }

    override fun onBindViewHolder(holder: DashBoardViewHolder, position: Int) {
        val transaction=transactionList[position]
        holder.binding.transactionName.text=transaction.title
        holder.binding.transactionCategory.text=transaction.transactionType
        "$${transaction.amount}".also { holder.binding.transactionAmount.text = it }

        holder.itemView.setOnClickListener {
            onClickItem?.invoke(transaction)
        }
    }
}