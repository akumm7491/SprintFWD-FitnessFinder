package com.test.fitnessstudios.data.repositories.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.test.fitnessstudios.helpers.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataStoreRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    private val LAST_TAB_POSITION = intPreferencesKey(Constants.LAST_TAB_POSITION)

    suspend fun storeLastTab(position: Int){
        dataStore.edit { prefs ->
            prefs[LAST_TAB_POSITION] = position
         }
    }

    fun getLastTab(): Flow<Int> {
        return dataStore.data.map { prefs ->
            prefs[LAST_TAB_POSITION] ?: 0
        }
    }
}