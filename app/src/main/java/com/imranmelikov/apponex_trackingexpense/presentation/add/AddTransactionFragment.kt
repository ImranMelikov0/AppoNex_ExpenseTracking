package com.imranmelikov.apponex_trackingexpense.presentation.add

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
import com.imranmelikov.apponex_trackingexpense.R
import com.imranmelikov.apponex_trackingexpense.constants.ErrorMsgConstants
import com.imranmelikov.apponex_trackingexpense.constants.FireStoreCollectionConstants
import com.imranmelikov.apponex_trackingexpense.databinding.FragmentAddTransactionBinding
import com.imranmelikov.apponex_trackingexpense.domain.model.TotalTransaction
import com.imranmelikov.apponex_trackingexpense.domain.model.Transaction
import com.imranmelikov.apponex_trackingexpense.presentation.dashboard.TransactionViewModel
import com.imranmelikov.apponex_trackingexpense.util.Resource
import com.imranmelikov.apponex_trackingexpense.util.Status
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import kotlin.math.exp
import kotlin.math.max

@AndroidEntryPoint
class AddTransactionFragment : Fragment() {
    private lateinit var binding:FragmentAddTransactionBinding
    private lateinit var viewModel: TransactionViewModel
    private lateinit var totalTransaction:TotalTransaction
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentAddTransactionBinding.inflate(inflater,container,false)
        viewModel=ViewModelProvider(requireActivity())[TransactionViewModel::class.java]

        val type=binding.addTransactionLayout.etTransactionType
        type.setText("Income")
        val transactionTypes = listOf("Income","Expense")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, transactionTypes)
        type.setAdapter(adapter)

        getTotalTransaction()
        setDate()
        addTransaction()
        observeMessage()
        observeTotalMessage()
        return binding.root
    }

    private fun addTransaction() {
        binding.apply {
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
                    var totalBalance=totalTransaction.totalIncome.toDouble()
                    var income=totalTransaction.totalIncome.toDouble()
                    var expense=totalTransaction.totalExpense.toDouble()
                    when(addTransactionLayout.etTransactionType.text.toString()){
                        FireStoreCollectionConstants.income->{
                            totalBalance+=roundedAmount
                            income+=roundedAmount
                        }
                        FireStoreCollectionConstants.expense->{
                            totalBalance=(totalBalance-roundedAmount)
                            expense+=roundedAmount
                        }
                    }
                    totalTransaction.totalBalance=totalBalance.toString()
                    totalTransaction.totalIncome=income.toString()
                    totalTransaction.totalExpense= expense.toString()
                    viewModel.updateTotalTransaction(totalTransaction)
                    viewModel.insertTransaction(transaction)
                    findNavController().popBackStack()
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