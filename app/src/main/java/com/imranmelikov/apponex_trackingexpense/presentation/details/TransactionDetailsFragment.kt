package com.imranmelikov.apponex_trackingexpense.presentation.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.imranmelikov.apponex_trackingexpense.R
import com.imranmelikov.apponex_trackingexpense.constants.FireStoreCollectionConstants
import com.imranmelikov.apponex_trackingexpense.databinding.FragmentTransactionDetailsBinding
import com.imranmelikov.apponex_trackingexpense.domain.model.TotalTransaction
import com.imranmelikov.apponex_trackingexpense.domain.model.Transaction
import com.imranmelikov.apponex_trackingexpense.presentation.dashboard.TransactionViewModel
import com.imranmelikov.apponex_trackingexpense.util.Resource
import com.imranmelikov.apponex_trackingexpense.util.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.exp

@Suppress("DEPRECATION")
@AndroidEntryPoint
class TransactionDetailsFragment : Fragment() {
    private lateinit var binding:FragmentTransactionDetailsBinding
    private lateinit var viewModel: TransactionViewModel
    private lateinit var totalTransaction: TotalTransaction
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentTransactionDetailsBinding.inflate(inflater,container,false)
        viewModel=ViewModelProvider(requireActivity())[TransactionViewModel::class.java]
        getTransaction()
        getTotalTransaction()
        return binding.root
    }

    private fun getTransaction(){
        val transaction=arguments?.getSerializable("transaction") as Transaction
        "$${transaction.amount}".also { binding.transactionDetails.amount.text = it }
        binding.transactionDetails.title.text=transaction.title
        binding.transactionDetails.note.text=transaction.note
        binding.transactionDetails.date.text=transaction.date
        binding.transactionDetails.type.text=transaction.transactionType

        binding.editTransaction.setOnClickListener {
            val bundle=Bundle()
            bundle.putSerializable("transaction",transaction)
            findNavController().navigate(R.id.action_transactionDetailsFragment_to_editTransactionFragment,bundle)
        }

        binding.deleteTransaction.setOnClickListener {
            var totalBalance=totalTransaction.totalIncome.toDouble()
            var income=totalTransaction.totalIncome.toDouble()
            var expense=totalTransaction.totalExpense.toDouble()
            when(transaction.transactionType){
                FireStoreCollectionConstants.income->{
                    totalBalance-=transaction.amount.toDouble()
                    income-=transaction.amount.toDouble()
                }
                FireStoreCollectionConstants.expense->{
                    totalBalance+=transaction.amount.toDouble()
                    expense-=transaction.amount.toDouble()
                }
            }
            totalTransaction.totalBalance=totalBalance.toString()
            totalTransaction.totalIncome=income.toString()
            totalTransaction.totalExpense= expense.toString()
            viewModel.updateTotalTransaction(totalTransaction)
            viewModel.deleteTransaction(transaction)
            findNavController().popBackStack()
        }
    }
    private fun getTotalTransaction(){
        viewModel.totalTransactionLiveData.observe(viewLifecycleOwner){result->
            handleResult(result){
                totalTransaction=it
            }
        }
    }
    private fun <T> handleResult(result: Resource<T>, actionOnSuccess: (T) -> Unit) {
        when (result.status) {
            Status.ERROR -> {
            }
            Status.SUCCESS -> {
                result.data?.let(actionOnSuccess)
            }
            Status.LOADING -> {
            }
        }
    }
}