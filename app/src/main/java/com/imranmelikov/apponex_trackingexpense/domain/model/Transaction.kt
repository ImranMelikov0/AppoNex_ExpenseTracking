package com.imranmelikov.apponex_trackingexpense.domain.model

import com.google.firebase.Timestamp
import java.io.Serializable

data class Transaction(var id:String,val title:String,val amount:String,val transactionType:String,
                       val date:String,val note:String, var timestamp: Timestamp = Timestamp.now()):Serializable