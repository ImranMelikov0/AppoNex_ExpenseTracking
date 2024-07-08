package com.imranmelikov.apponex_trackingexpense.domain

import com.imranmelikov.apponex_trackingexpense.domain.model.CRUD
import com.imranmelikov.apponex_trackingexpense.domain.model.TotalTransaction
import com.imranmelikov.apponex_trackingexpense.domain.model.Transaction
import com.imranmelikov.apponex_trackingexpense.domain.model.User
import com.imranmelikov.apponex_trackingexpense.util.Resource

interface Repository {

    suspend fun signUpUser(user: User):Resource<CRUD>

    suspend fun signInUser(email:String,password:String):Resource<CRUD>

    suspend fun insertTransaction(transaction: Transaction):Resource<CRUD>

    suspend fun getIncomeTransactions():Resource<List<Transaction>>

    suspend fun getExpenseTransactions():Resource<List<Transaction>>

    suspend fun deleteTransaction(transaction: Transaction):Resource<CRUD>

    suspend fun insertTotalTransaction(totalTransaction: TotalTransaction):Resource<CRUD>

    suspend fun updateTotalTransaction(totalTransaction: TotalTransaction):Resource<CRUD>

    suspend fun getTotalTransaction():Resource<TotalTransaction>
}