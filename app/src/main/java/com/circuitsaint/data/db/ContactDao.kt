package com.circuitsaint.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.circuitsaint.data.model.Contact
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    
    @Query("SELECT * FROM contactos ORDER BY created_at DESC")
    fun getAllContacts(): LiveData<List<Contact>>
    
    @Query("SELECT * FROM contactos ORDER BY created_at DESC")
    fun getAllContactsFlow(): Flow<List<Contact>>
    
    @Query("SELECT * FROM contactos WHERE id = :contactId")
    suspend fun getContactById(contactId: Long): Contact?
    
    @Query("SELECT * FROM contactos WHERE leido = :leido ORDER BY created_at DESC")
    fun getContactsByLeido(leido: Boolean): LiveData<List<Contact>>
    
    @Query("SELECT COUNT(*) FROM contactos WHERE leido = false")
    fun getUnreadContactCount(): LiveData<Int>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: Contact): Long
    
    @Update
    suspend fun updateContact(contact: Contact)
    
    @Delete
    suspend fun deleteContact(contact: Contact)
    
    @Query("UPDATE contactos SET leido = :leido WHERE id = :contactId")
    suspend fun updateContactLeido(contactId: Long, leido: Boolean)
    
    @Query("UPDATE contactos SET respondido = :respondido WHERE id = :contactId")
    suspend fun updateContactRespondido(contactId: Long, respondido: Boolean)
    
    @Query("SELECT COUNT(*) FROM contactos")
    fun getContactCount(): LiveData<Int>
}

