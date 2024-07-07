package com.imranmelikov.apponex_trackingexpense.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.imranmelikov.apponex_trackingexpense.domain.Repository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun injectFirebaseAuth():FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

    @Singleton
    @Provides
    fun injectFirebaseFireStore():FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }
    @Singleton
    @Provides
    fun injectRepo(auth: FirebaseAuth,firestore: FirebaseFirestore,@ApplicationContext context: Context)=RepositoryImpl(auth,firestore,context) as Repository
}