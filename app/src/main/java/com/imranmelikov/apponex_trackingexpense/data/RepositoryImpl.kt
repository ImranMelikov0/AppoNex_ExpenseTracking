package com.imranmelikov.apponex_trackingexpense.data

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.imranmelikov.apponex_trackingexpense.constants.ErrorMsgConstants
import com.imranmelikov.apponex_trackingexpense.constants.FireStoreCollectionConstants
import com.imranmelikov.apponex_trackingexpense.domain.Repository
import com.imranmelikov.apponex_trackingexpense.domain.model.CRUD
import com.imranmelikov.apponex_trackingexpense.domain.model.User
import com.imranmelikov.apponex_trackingexpense.util.Resource
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RepositoryImpl(private val auth: FirebaseAuth,private val fireStore: FirebaseFirestore,private val context: Context):Repository {
    override suspend fun signUpUser(user: User):Resource<CRUD> {
        return try {
            suspendCoroutine { continuation ->
                auth.createUserWithEmailAndPassword(user.email,user.password).addOnSuccessListener {
                    fireStore.collection(FireStoreCollectionConstants.users).document(user.email).collection(FireStoreCollectionConstants.user)
                        .add(user).addOnSuccessListener {reference->
                            continuation.resume(Resource.success(CRUD(reference.id,1)))
                        }.addOnFailureListener {e ->
                            Toast.makeText(context,e.localizedMessage, Toast.LENGTH_SHORT).show()
                            continuation.resume(Resource.error("${ErrorMsgConstants.errorFromFirebase} ${e.localizedMessage}", null))
                        }
                }.addOnFailureListener {e ->
                    Toast.makeText(context,e.localizedMessage, Toast.LENGTH_SHORT).show()
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

}