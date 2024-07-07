package com.imranmelikov.apponex_trackingexpense.domain

import com.imranmelikov.apponex_trackingexpense.domain.model.CRUD
import com.imranmelikov.apponex_trackingexpense.domain.model.User
import com.imranmelikov.apponex_trackingexpense.util.Resource

interface Repository {

    suspend fun signUpUser(user: User):Resource<CRUD>

    suspend fun signInUser(email:String,password:String):Resource<CRUD>
}