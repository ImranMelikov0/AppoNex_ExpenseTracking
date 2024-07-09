package com.imranmelikov.apponex_trackingexpense.presentation.edit

import android.app.DatePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.imranmelikov.apponex_trackingexpense.constants.ErrorMsgConstants
import com.imranmelikov.apponex_trackingexpense.constants.FireStoreCollectionConstants
import com.imranmelikov.apponex_trackingexpense.databinding.FragmentEditTransactionBinding
import com.imranmelikov.apponex_trackingexpense.domain.model.TotalTransaction
import com.imranmelikov.apponex_trackingexpense.domain.model.Transaction
import com.imranmelikov.apponex_trackingexpense.presentation.dashboard.TransactionViewModel
import com.imranmelikov.apponex_trackingexpense.util.Resource
import com.imranmelikov.apponex_trackingexpense.util.Status
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import kotlin.math.max
import kotlin.math.min

@Suppress("DEPRECATION")
@AndroidEntryPoint
class EditTransactionFragment : Fragment() {
    private lateinit var binding:FragmentEditTransactionBinding
    private lateinit var viewModel: TransactionViewModel
    private lateinit var totalTransaction: TotalTransaction
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentEditTransactionBinding.inflate(inflater,container,false)
        viewModel=ViewModelProvider(requireActivity())[TransactionViewModel::class.java]
        getTotalTransaction()
        setDate()
        updateTransaction()
        observeMessage()
        observeTotalMessage()
        return binding.root
    }

    private fun updateTransaction() {
        binding.apply {
            val transactionArg=arguments?.getSerializable("transaction") as Transaction
            addTransactionLayout.etTitle.setText(transactionArg.title)
            addTransactionLayout.etAmount.setText(transactionArg.amount)
            addTransactionLayout.etNote.setText(transactionArg.note)
            addTransactionLayout.etWhen.setText(transactionArg.date)
            "Update".also { btnSaveTransaction.text = it }

            val type=binding.addTransactionLayout.etTransactionType
            type.setText(transactionArg.transactionType)
            val transactionTypes = listOf("Income","Expense")
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, transactionTypes)
            type.setAdapter(adapter)

            btnSaveTransaction.setOnClickListener {
                val title = addTransactionLayout.etTitle.text!!.trim()
                val amount = addTransactionLayout.etAmount
                val note = addTransactionLayout.etNote.text!!.trim()
                if (title.isEmpty() && amount.text?.trim()!!.isEmpty() &&
                    addTransactionLayout.etWhen.text!!.isEmpty() && note.isEmpty()&&addTransactionLayout.etTransactionType.text.isEmpty()) {
                    Toast.makeText(requireContext(), ErrorMsgConstants.editTextOneField, Toast.LENGTH_SHORT).show()
                }  else {
                    if (amount.text!!.trim().isEmpty()) {
                        amount.setText("0")
                    }
                    val roundedAmount = Math.round(amount.text.toString().toDouble() * 10.0) / 10.0
                    val transaction=Transaction("",title.toString(),roundedAmount.toString(),addTransactionLayout.etTransactionType.text.toString()
                        ,addTransactionLayout.etWhen.text.toString(),note.toString())
                    var totalBalance=totalTransaction.totalBalance.toDouble()
                    var income=totalTransaction.totalIncome.toDouble()
                    var expense=totalTransaction.totalExpense.toDouble()
                    when {
                        transaction.transactionType==FireStoreCollectionConstants.income&&transactionArg.transactionType==FireStoreCollectionConstants.income -> {
                            totalBalance-=transactionArg.amount.toDouble()
                            totalBalance+=roundedAmount
                            income-=transactionArg.amount.toDouble()
                            income+=roundedAmount
                        }
                        transaction.transactionType==FireStoreCollectionConstants.expense&&transactionArg.transactionType==FireStoreCollectionConstants.expense -> {
                            totalBalance+=transactionArg.amount.toDouble()
                            totalBalance-=roundedAmount
                            expense-=transactionArg.amount.toDouble()
                            expense+=roundedAmount
                        }
                        transaction.transactionType==FireStoreCollectionConstants.income&&transactionArg.transactionType==FireStoreCollectionConstants.expense -> {
                            totalBalance +=roundedAmount
                            income+=roundedAmount
                            expense-=transactionArg.amount.toDouble()
                        }
                        transaction.transactionType==FireStoreCollectionConstants.expense&&transactionArg.transactionType==FireStoreCollectionConstants.income -> {
                            totalBalance-=roundedAmount
                            income-=transactionArg.amount.toDouble()
                            expense+=roundedAmount
                        }
                    }
                    totalTransaction.totalBalance=totalBalance.toString()
                    totalTransaction.totalIncome=income.toString()
                    totalTransaction.totalExpense= expense.toString()
                    viewModel.updateTotalTransaction(totalTransaction)
                    transaction.id=transactionArg.id
                    viewModel.deleteTransaction(transactionArg)
                    viewModel.insertTransaction(transaction)
                    findNavController().navigate(com.imranmelikov.apponex_trackingexpense.R.id.action_editTransactionFragment_to_dashBoardFragment)
                }
            }
        }
    }

    private fun getTotalTransaction(){
        viewModel.totalTransactionLiveData.observe(viewLifecycleOwner){result->
            handleResult(result){
                totalTransaction=it
            }
        }
    }
    private fun observeTotalMessage(){
        viewModel.msgTotalLiveData.observe(viewLifecycleOwner){result->
            handleResult(result){
                Log.d(it.message,it.success.toString())
            }
        }
    }
    private fun observeMessage(){
        viewModel.msgLiveData.observe(viewLifecycleOwner){result->
            handleResult(result){
                Log.d(it.message,it.success.toString())
            }
        }
    }
    private fun setDate(){
        binding.addTransactionLayout.etWhen.setOnClickListener {
            val calendar= Calendar.getInstance()

            val year= calendar.get(Calendar.YEAR)
            val month= calendar.get(Calendar.MONTH)
            val day= calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker= DatePickerDialog(requireContext(), { _, y, m, d ->
                "$d/${m+1}/$y".also { binding.addTransactionLayout.etWhen.setText(it) }
            },year,month,day)

            datePicker.setButton(DialogInterface.BUTTON_POSITIVE, "Set",datePicker)
            datePicker.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel",datePicker)

            datePicker.show()
        }
    }
    private fun <T> handleResult(result: Resource<T>, actionOnSuccess: (T) -> Unit) {
        when (result.status) {
            Status.ERROR -> {
                errorResult()
            }
            Status.SUCCESS -> {
                result.data?.let(actionOnSuccess)
                successResult()
            }
            Status.LOADING -> {
                loadingResult()
            }
        }
    }

    private fun successResult() {
    }

    private fun loadingResult() {
    }

    private fun errorResult() {
        Toast.makeText(requireContext(), ErrorMsgConstants.errorForUser, Toast.LENGTH_SHORT).show()
    }
}