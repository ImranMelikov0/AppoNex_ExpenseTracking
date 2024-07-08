package com.imranmelikov.apponex_trackingexpense.data

import android.content.Context
import android.widget.Toast
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.imranmelikov.apponex_trackingexpense.constants.ErrorMsgConstants
import com.imranmelikov.apponex_trackingexpense.constants.FireStoreCollectionConstants
import com.imranmelikov.apponex_trackingexpense.constants.FireStoreConstants
import com.imranmelikov.apponex_trackingexpense.domain.Repository
import com.imranmelikov.apponex_trackingexpense.domain.model.CRUD
import com.imranmelikov.apponex_trackingexpense.domain.model.TotalTransaction
import com.imranmelikov.apponex_trackingexpense.domain.model.Transaction
import com.imranmelikov.apponex_trackingexpense.domain.model.User
import com.imranmelikov.apponex_trackingexpense.util.Resource
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RepositoryImpl(private val auth: FirebaseAuth,private val fireStore: FirebaseFirestore,private val context: Context):Repository {
    override suspend fun signUpUser(user: User):Resource<CRUD> {
        return try {
            suspendCoroutine { continuation ->
                auth.createUserWithEmailAndPassword(user.email, user.password)
                    .addOnSuccessListener { authResult ->
                        val uid = authResult.user?.uid ?: ""
                        val updatedUser = user.copy(id = uid)
                        fireStore.collection(FireStoreCollectionConstants.users)
                            .document(user.email).collection(FireStoreCollectionConstants.user).add(updatedUser).addOnSuccessListener {reference->
                                continuation.resume(Resource.success(CRUD(reference.id, 1)))
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                                continuation.resume(Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null))
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                        continuation.resume(Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null))
                    }
            }

        }catch (e:Exception){
            Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null)
        }
    }

    override suspend fun signInUser(email: String, password: String): Resource<CRUD> {
        return try {
            suspendCoroutine { continuation ->
                auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                    it.user?.let {user->
                        user.email?.let {email->
                            continuation.resume(Resource.success(CRUD(email,4)))
                        }
                    }
                }.addOnFailureListener {e ->
                    Toast.makeText(context,e.localizedMessage,Toast.LENGTH_SHORT).show()
                    continuation.resume(Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null))
                }
            }
        }catch (e:Exception){
            Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null)
        }
    }

    override suspend fun insertTransaction(transaction: Transaction): Resource<CRUD> {
        return try {
            suspendCoroutine { continuation ->
                val transactionWithTimestamp = transaction.copy(timestamp = Timestamp.now())
                when(transaction.transactionType){
                    FireStoreCollectionConstants.income->{
                        auth.currentUser?.let {user->
                            fireStore.collection(FireStoreCollectionConstants.balance).document(user.uid).collection(FireStoreCollectionConstants.income).add(transactionWithTimestamp)
                                .addOnSuccessListener {
                                    continuation.resume(Resource.success(CRUD(it.id,1)))
                                }.addOnFailureListener {e ->
                                    Toast.makeText(context,e.localizedMessage,Toast.LENGTH_SHORT).show()
                                    continuation.resume(Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null))
                                }
                        }
                    }
                    FireStoreCollectionConstants.expense->{
                        auth.currentUser?.let {user->
                            fireStore.collection(FireStoreCollectionConstants.balance).document(user.uid).collection(FireStoreCollectionConstants.expense).add(transaction)
                                .addOnSuccessListener {
                                    continuation.resume(Resource.success(CRUD(it.id,1)))
                                }.addOnFailureListener {e ->
                                    Toast.makeText(context,e.localizedMessage,Toast.LENGTH_SHORT).show()
                                    continuation.resume(Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null))
                                }
                        }
                    }
                }
            }
        }catch (e:Exception){
            Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null)
        }
    }

    override suspend fun getIncomeTransactions(): Resource<List<Transaction>> {
        return try {
            suspendCoroutine { continuation ->
                auth.currentUser?.let { userFirebase ->
                    val transactionList = mutableListOf<Transaction>()
                    fireStore.collection(FireStoreCollectionConstants.balance)
                        .document(userFirebase.uid)
                        .collection(FireStoreCollectionConstants.income)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.isEmpty){
                                continuation.resume(Resource.success(emptyList()))
                            }else{
                            for (documentSnapshot in querySnapshot.documents) {
                                val transaction = Transaction(
                                    id = documentSnapshot.getString(FireStoreConstants.id) as String,
                                    title = documentSnapshot.getString(FireStoreConstants.title) as String,
                                    amount = documentSnapshot.getString(FireStoreConstants.amount) as String,
                                    transactionType = documentSnapshot.getString(FireStoreConstants.transactionType) as String,
                                    date = documentSnapshot.getString(FireStoreConstants.date) as String,
                                    timestamp = documentSnapshot.getTimestamp("timestamp") as Timestamp,
                                    note = documentSnapshot.getString(FireStoreConstants.note) as String
                                )
                                transaction.id = documentSnapshot.id
                                transactionList.add(transaction)
                            }
                            continuation.resume(Resource.success(transactionList))
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                            continuation.resume(Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null))
                        }
                }?: continuation.resume(Resource.error(ErrorMsgConstants.errorForUser, null))
            }
        } catch (e: Exception) {
            Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null)
        }
    }

    override suspend fun getExpenseTransactions(): Resource<List<Transaction>> {
        return try {
            suspendCoroutine { continuation ->
                auth.currentUser?.let { userFirebase ->
                    val transactionList = mutableListOf<Transaction>()
                    fireStore.collection(FireStoreCollectionConstants.balance)
                        .document(userFirebase.uid)
                        .collection(FireStoreCollectionConstants.expense)
                        .orderBy("timestamp", Query.Direction.DESCENDING)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.isEmpty) {
                                continuation.resume(Resource.success(emptyList()))
                            } else {
                                for (documentSnapshot in querySnapshot.documents) {
                                    val transaction = Transaction(
                                        id = documentSnapshot.getString(FireStoreConstants.id) as String,
                                        title = documentSnapshot.getString(FireStoreConstants.title) as String,
                                        amount = documentSnapshot.getString(FireStoreConstants.amount) as String,
                                        transactionType = documentSnapshot.getString(FireStoreConstants.transactionType) as String,
                                        date = documentSnapshot.getString(FireStoreConstants.date) as String,
                                        timestamp = documentSnapshot.getTimestamp("timestamp") as Timestamp,
                                        note = documentSnapshot.getString(FireStoreConstants.note) as String
                                    )
                                    transaction.id = documentSnapshot.id
                                    transactionList.add(transaction)
                                }
                                continuation.resume(Resource.success(transactionList))
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                            continuation.resume(Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null))
                        }
                } ?: continuation.resume(Resource.error(ErrorMsgConstants.errorForUser, null))
            }
        } catch (e: Exception) {
            Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null)
        }
    }


    override suspend fun deleteTransaction(transaction: Transaction): Resource<CRUD> {
        return try {
            suspendCoroutine { continuation ->
                when(transaction.transactionType){
                    FireStoreCollectionConstants.income->{
                        auth.currentUser?.let { currentUser->
                            fireStore.collection(FireStoreCollectionConstants.balance).document(currentUser.uid).collection(FireStoreCollectionConstants.income)
                                .document(transaction.id).delete().addOnSuccessListener {
                                    continuation.resume(Resource.success(CRUD(transaction.id,2)))
                                }.addOnFailureListener {e ->
                                    Toast.makeText(context,e.localizedMessage,Toast.LENGTH_SHORT).show()
                                    continuation.resume(Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null))
                                }
                        }
                    }
                    FireStoreCollectionConstants.expense->{
                        auth.currentUser?.let { currentUser->
                            fireStore.collection(FireStoreCollectionConstants.balance).document(currentUser.uid).collection(FireStoreCollectionConstants.expense)
                                .document(transaction.id).delete().addOnSuccessListener {
                                    continuation.resume(Resource.success(CRUD(transaction.id,2)))
                                }.addOnFailureListener {e ->
                                    Toast.makeText(context,e.localizedMessage,Toast.LENGTH_SHORT).show()
                                    continuation.resume(Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null))
                                }
                        }
                    }
                }
            }
        }catch (e:Exception){
            Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null)
        }
    }

    override suspend fun insertTotalTransaction(totalTransaction: TotalTransaction): Resource<CRUD> {
            return try {
                suspendCoroutine { continuation ->
                    auth.currentUser?.let {
                        fireStore.collection(FireStoreCollectionConstants.totalTransaction)
                            .document(it.uid)
                            .collection(FireStoreCollectionConstants.totalTransaction)
                            .add(totalTransaction).addOnSuccessListener { reference ->
                                continuation.resume(Resource.success(CRUD(reference.id,2)))
                            }.addOnFailureListener {e ->
                                Toast.makeText(context,e.localizedMessage,Toast.LENGTH_SHORT).show()
                                continuation.resume(Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null))
                            }
                    }
                }
            }catch (e:Exception){
                Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null)
            }
    }

    override suspend fun updateTotalTransaction(totalTransaction: TotalTransaction): Resource<CRUD> {
        return try {
            suspendCoroutine { continuation ->
                auth.currentUser?.let {
                    fireStore.collection(FireStoreCollectionConstants.totalTransaction)
                        .document(it.uid)
                        .collection(FireStoreCollectionConstants.totalTransaction).document(totalTransaction.id)
                        .update(  FireStoreConstants.totalBalance, totalTransaction.totalBalance,
                            FireStoreConstants.totalIncome, totalTransaction.totalIncome,
                            FireStoreConstants.totalExpense, totalTransaction.totalExpense)
                        .addOnSuccessListener {
                            continuation.resume(Resource.success(CRUD(totalTransaction.id,2)))
                        }.addOnFailureListener {e ->
                            Toast.makeText(context,e.localizedMessage,Toast.LENGTH_SHORT).show()
                            continuation.resume(Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null))
                        }
                }
            }
        }catch (e:Exception){
            Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null)
        }
    }

    override suspend fun getTotalTransaction(): Resource<TotalTransaction> {
        return try {
            suspendCoroutine { continuation ->
                var totalTransaction: TotalTransaction? = null
                auth.currentUser?.let { userFirebase ->
                    fireStore.collection(FireStoreCollectionConstants.totalTransaction)
                        .document(userFirebase.uid)
                        .collection(FireStoreCollectionConstants.totalTransaction)
                        .get()
                        .addOnSuccessListener { querySnapshot ->
                            if (querySnapshot.isEmpty) {
                                // Handle case where no documents are found
                                continuation.resume(Resource.success(TotalTransaction("", "", "", "")))
                            } else {
                                for (documentSnapshot in querySnapshot.documents) {
                                    val transactionTotal = TotalTransaction(
                                        id = documentSnapshot.getString(FireStoreConstants.id) as String,
                                        totalBalance = documentSnapshot.getString(FireStoreConstants.totalBalance) as String,
                                        totalIncome = documentSnapshot.getString(FireStoreConstants.totalIncome) as String,
                                        totalExpense = documentSnapshot.getString(FireStoreConstants.totalExpense) as String
                                    )
                                    transactionTotal.id = documentSnapshot.id
                                    totalTransaction = transactionTotal
                                }
                                // Check if totalTransaction is null to avoid multiple resumes
                                if (totalTransaction == null) {
                                    continuation.resume(Resource.success(TotalTransaction("", "", "", "")))
                                } else {
                                    continuation.resume(Resource.success(totalTransaction!!))
                                }
                            }
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, e.localizedMessage, Toast.LENGTH_SHORT).show()
                            continuation.resume(Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null))
                        }
                }
            }
        } catch (e: Exception) {
            Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null)
        }
    }


}