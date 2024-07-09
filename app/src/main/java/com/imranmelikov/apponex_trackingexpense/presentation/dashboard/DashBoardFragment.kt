package com.imranmelikov.apponex_trackingexpense.presentation.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.imranmelikov.apponex_trackingexpense.R
import com.imranmelikov.apponex_trackingexpense.constants.ErrorMsgConstants
import com.imranmelikov.apponex_trackingexpense.databinding.FragmentDashBoardBinding
import com.imranmelikov.apponex_trackingexpense.domain.model.TotalTransaction
import com.imranmelikov.apponex_trackingexpense.domain.model.Transaction
import com.imranmelikov.apponex_trackingexpense.presentation.LoginActivity
import com.imranmelikov.apponex_trackingexpense.presentation.MainActivity
import com.imranmelikov.apponex_trackingexpense.sharedpreferencesmanager.SharedPreferencesManager
import com.imranmelikov.apponex_trackingexpense.util.Resource
import com.imranmelikov.apponex_trackingexpense.util.Status
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DashBoardFragment : Fragment() {
    private lateinit var binding:FragmentDashBoardBinding
    private lateinit var viewModel: TransactionViewModel
    private lateinit var dashBoardAdapter: DashBoardAdapter
    private val transactions = mutableListOf<Transaction>()
    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager
    @Inject
    lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=FragmentDashBoardBinding.inflate(inflater,container,false)
        viewModel=ViewModelProvider(requireActivity())[TransactionViewModel::class.java]

        onBackPress()
        viewModel.getTotalTransaction()
        viewModel.getIncomeTransaction()
        viewModel.getExpenseTransaction()

        binding.expenseCardView.totalIcon.setImageResource(R.drawable.ic_expense)
        "Total Expense".also { binding.expenseCardView.totalTitle.text = it }
        dashBoardAdapter= DashBoardAdapter()


        observeTotalTransaction()
        observeTotalMessage()
        observeMessage()
        observeExpense()
        observeIncome()

        initializeRv()
        onClickItem()

        val totalTransaction= TotalTransaction("","0.0","0.0","0.0")
        val checkTotalTransaction= sharedPreferencesManager.load("totalTransaction",false)
        if (!checkTotalTransaction){
            viewModel.insertTotalTransaction(totalTransaction)
            sharedPreferencesManager.save("totalTransaction",true)
        }

        binding.logout.setOnClickListener {
            auth.signOut()
            val intent=Intent(requireActivity(),LoginActivity::class.java)
            sharedPreferencesManager.save("totalTransaction",false)
            startActivity(intent)
            (activity as MainActivity).finishAffinity()
        }

        binding.btnAddTransaction.setOnClickListener {
            findNavController().navigate(R.id.action_dashBoardFragment_to_addTransactionFragment)
            transactions.clear()
        }
        return binding.root
    }

    private fun observeTotalTransaction(){
        viewModel.totalTransactionLiveData.observe(viewLifecycleOwner){result->
            handleResult(result){totalTransaction ->
                "$${totalTransaction.totalBalance}".also { binding.totalBalanceView.totalBalance.text = it }
                "$${totalTransaction.totalIncome}".also { binding.incomeCardView.total.text = it }
                "$${totalTransaction.totalExpense}".also { binding.expenseCardView.total.text = it }
            }
        }
    }
    private fun onClickItem(){
        dashBoardAdapter.onClickItem={transaction->
            val bundle=Bundle()
            bundle.putSerializable("transaction",transaction)
           findNavController().navigate(R.id.action_dashBoardFragment_to_transactionDetailsFragment,bundle)
            transactions.clear()
        }
    }
    private fun observeExpense(){
        viewModel.expenseLiveData.observe(viewLifecycleOwner){result->
            handleResult(result){
                transactions.addAll(it)
            }
        }
    }

    private fun observeIncome(){
        viewModel.incomeLiveData.observe(viewLifecycleOwner){result->
            handleResult(result){
                transactions.addAll(it)
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
    private fun observeTotalMessage(){
        viewModel.msgTotalLiveData.observe(viewLifecycleOwner){result->
            handleResult(result){
                Log.d(it.message,it.success.toString())
            }
        }
    }
    private fun initializeRv(){
        binding.transactionRv.layoutManager=LinearLayoutManager(requireContext())
        dashBoardAdapter.transactionList=transactions
        binding.transactionRv.adapter=dashBoardAdapter
    }
    private fun <T> handleResult(result: Resource<T>, actionOnSuccess: (T) -> Unit) {
        when (result.status) {
            Status.ERROR -> {
                errorResult()
            }
            Status.SUCCESS -> {
                result.data?.let(actionOnSuccess)
                successResult()
                if (transactions.isNotEmpty()){
                    binding.emptyStateLayout.emptyStateView.visibility=View.GONE
                }else{
                    binding.emptyStateLayout.emptyStateView.visibility=View.VISIBLE
                }
            }
            Status.LOADING -> {
                loadingResult()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun successResult() {
        dashBoardAdapter.notifyDataSetChanged()
    }

    private fun loadingResult() {
    }

    private fun errorResult() {
        Toast.makeText(requireContext(), ErrorMsgConstants.errorForUser, Toast.LENGTH_SHORT).show()
        binding.emptyStateLayout.emptyStateView.visibility=View.VISIBLE
    }
    private fun onBackPress(){
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                (activity as MainActivity).finishAffinity()
            }
        })
    }
}