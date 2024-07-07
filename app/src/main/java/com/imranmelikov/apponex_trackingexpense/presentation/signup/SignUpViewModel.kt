package com.imranmelikov.apponex_trackingexpense.presentation.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imranmelikov.apponex_trackingexpense.constants.ErrorMsgConstants
import com.imranmelikov.apponex_trackingexpense.domain.Repository
import com.imranmelikov.apponex_trackingexpense.domain.model.CRUD
import com.imranmelikov.apponex_trackingexpense.domain.model.User
import com.imranmelikov.apponex_trackingexpense.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private val repository: Repository):ViewModel() {
    private val mutableMessageLiveData= MutableLiveData<Resource<CRUD>>()
    val msgLiveData: LiveData<Resource<CRUD>>
        get() = mutableMessageLiveData

    private val exceptionHandlerMessage = CoroutineExceptionHandler { _, throwable ->
        println("${ErrorMsgConstants.error} ${throwable.localizedMessage}")
        mutableMessageLiveData.value= Resource.error(ErrorMsgConstants.errorViewModel,null)
    }

    fun signUp(user: User){
        mutableMessageLiveData.value=Resource.loading(null)
        viewModelScope.launch(Dispatchers.IO + exceptionHandlerMessage){
            val response=repository.signUpUser(user)
            viewModelScope.launch (Dispatchers.Main + exceptionHandlerMessage){
                response.data?.let {
                    mutableMessageLiveData.value=Resource.success(it)
                }
            }
        }
    }
}