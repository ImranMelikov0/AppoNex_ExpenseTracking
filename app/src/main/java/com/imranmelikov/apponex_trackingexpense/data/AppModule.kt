package com.imranmelikov.apponex_trackingexpense.data

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.imranmelikov.apponex_trackingexpense.constants.SharedPrefConstant
import com.imranmelikov.apponex_trackingexpense.domain.Repository
import com.imranmelikov.apponex_trackingexpense.sharedpreferencesmanager.SharedPreferencesManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun injectSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(SharedPrefConstant.myPref, Context.MODE_PRIVATE)
    }
    @Provides
    @Singleton
    fun injectSharedPreferencesManager(sharedPreferences: SharedPreferences): SharedPreferencesManager {
        return SharedPreferencesManager(sharedPreferences)
    }
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