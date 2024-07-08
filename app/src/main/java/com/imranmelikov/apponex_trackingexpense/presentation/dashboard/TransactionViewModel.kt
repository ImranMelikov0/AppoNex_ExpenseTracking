package com.imranmelikov.apponex_trackingexpense.presentation.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imranmelikov.apponex_trackingexpense.constants.ErrorMsgConstants
import com.imranmelikov.apponex_trackingexpense.domain.Repository
import com.imranmelikov.apponex_trackingexpense.domain.model.CRUD
import com.imranmelikov.apponex_trackingexpense.domain.model.TotalTransaction
import com.imranmelikov.apponex_trackingexpense.domain.model.Transaction
import com.imranmelikov.apponex_trackingexpense.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(private val repository: Repository):ViewModel() {
    private val mutableIncomeLiveData= MutableLiveData<Resource<List<Transaction>>>()
    val incomeLiveData: LiveData<Resource<List<Transaction>>>
        get() = mutableIncomeLiveData

    private val exceptionHandlerIncome = CoroutineExceptionHandler { _, throwable ->
        println("${ErrorMsgConstants.error} ${throwable.localizedMessage}")
        mutableIncomeLiveData.value= Resource.error(ErrorMsgConstants.errorViewModel,null)
    }
    private val mutableExpenseLiveData= MutableLiveData<Resource<List<Transaction>>>()
    val expenseLiveData: LiveData<Resource<List<Transaction>>>
        get() = mutableExpenseLiveData

    private val exceptionHandlerExpense = CoroutineExceptionHandler { _, throwable ->
        println("${ErrorMsgConstants.error} ${throwable.localizedMessage}")
        mutableExpenseLiveData.value= Resource.error(ErrorMsgConstants.errorViewModel,null)
    }

    private val mutableMessageLiveData= MutableLiveData<Resource<CRUD>>()
    val msgLiveData: LiveData<Resource<CRUD>>
        get() = mutableMessageLiveData

    private val exceptionHandlerMessage = CoroutineExceptionHandler { _, throwable ->
        println("${ErrorMsgConstants.error} ${throwable.localizedMessage}")
        mutableMessageLiveData.value= Resource.error(ErrorMsgConstants.errorViewModel,null)
    }

    private val mutableTotalTransactionLiveData= MutableLiveData<Resource<TotalTransaction>>()
    val totalTransactionLiveData: LiveData<Resource<TotalTransaction>>
        get() = mutableTotalTransactionLiveData

    private val exceptionHandlerTotalTransaction = CoroutineExceptionHandler { _, throwable ->
        println("${ErrorMsgConstants.error} ${throwable.localizedMessage}")
        mutableTotalTransactionLiveData.value= Resource.error(ErrorMsgConstants.errorViewModel,null)
    }

    private val mutableTotalMessageLiveData= MutableLiveData<Resource<CRUD>>()
    val msgTotalLiveData: LiveData<Resource<CRUD>>
        get() = mutableTotalMessageLiveData

    private val exceptionHandlerTotalMessage = CoroutineExceptionHandler { _, throwable ->
        println("${ErrorMsgConstants.error} ${throwable.localizedMessage}")
        mutableTotalMessageLiveData.value= Resource.error(ErrorMsgConstants.errorViewModel,null)
    }
    fun getTotalTransaction(){
        mutableTotalTransactionLiveData.value=Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO + exceptionHandlerTotalTransaction){
            val response=repository.getTotalTransaction()
            viewModelScope.launch(Dispatchers.Main + exceptionHandlerTotalTransaction){
                response.data?.let {
                    mutableTotalTransactionLiveData.value=Resource.success(it)
                }
            }
        }
    }
    fun insertTotalTransaction(totalTransaction: TotalTransaction){
        mutableTotalMessageLiveData.value=Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO + exceptionHandlerTotalMessage){
            val response= repository.insertTotalTransaction(totalTransaction)
            viewModelScope.launch(Dispatchers.Main + exceptionHandlerTotalMessage){
                response.data?.let {
                    mutableTotalMessageLiveData.value=Resource.success(it)
                }
            }
        }
        getTotalTransaction()
    }
    fun updateTotalTransaction(totalTransaction: TotalTransaction){
        mutableTotalMessageLiveData.value=Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO + exceptionHandlerTotalMessage){
            val response= repository.updateTotalTransaction(totalTransaction)
            viewModelScope.launch(Dispatchers.Main + exceptionHandlerTotalMessage){
                response.data?.let {
                    mutableTotalMessageLiveData.value=Resource.success(it)
                }
            }
        }
        getTotalTransaction()
    }
    fun getExpenseTransaction(){
        mutableExpenseLiveData.value=Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO + exceptionHandlerExpense){
            val response=repository.getExpenseTransactions()
            viewModelScope.launch(Dispatchers.Main + exceptionHandlerExpense){
                response.data?.let {
                    mutableExpenseLiveData.value=Resource.success(it)
                }
            }
        }
    }
    fun getIncomeTransaction(){
        mutableIncomeLiveData.value=Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO + exceptionHandlerIncome){
            val response=repository.getIncomeTransactions()
            viewModelScope.launch(Dispatchers.Main + exceptionHandlerIncome){
                response.data?.let {
                    mutableIncomeLiveData.value=Resource.success(it)
                }
            }
        }
    }
    fun insertTransaction(transaction: Transaction){
        mutableMessageLiveData.value=Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO + exceptionHandlerMessage){
            val response= repository.insertTransaction(transaction)
            viewModelScope.launch(Dispatchers.Main + exceptionHandlerMessage){
                response.data?.let {
                    mutableMessageLiveData.value=Resource.success(it)
                }
            }
        }
    }
    fun deleteTransaction(transaction: Transaction){
        mutableMessageLiveData.value=Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO + exceptionHandlerMessage){
            val response=repository.deleteTransaction(transaction)
            viewModelScope.launch(Dispatchers.Main + exceptionHandlerMessage){
                response.data?.let {
                    mutableMessageLiveData.value=Resource.success(it)
                }
            }
        }
    }
}