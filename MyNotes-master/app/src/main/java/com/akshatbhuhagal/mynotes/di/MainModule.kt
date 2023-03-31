package com.akshatbhuhagal.mynotes.di

import android.content.Context
import android.widget.EditText
import androidx.room.Room
import com.akshatbhuhagal.mynotes.R
import com.akshatbhuhagal.mynotes.data.encryption.KeyMgr
import com.akshatbhuhagal.mynotes.data.encryption.test
import com.akshatbhuhagal.mynotes.data.local.NotesDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton
import com.akshatbhuhagal.mynotes.presentation.PinActivity
import java.net.PasswordAuthentication

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context):NotesDataBase {
        println("sqlgetbytes: " + SQLiteDatabase.getBytes(KeyMgr.dbCharKey).test())
        val supportFactory = SupportFactory(SQLiteDatabase.getBytes(KeyMgr.dbCharKey))
        return Room.databaseBuilder(context, NotesDataBase::class.java, "notes.db").openHelperFactory(supportFactory).build()
//        return Room.databaseBuilder(context, NotesDataBase::class.java, "notes.db").build()
    }

    @Provides
    fun providesNotesDao(dataBase: NotesDataBase) = dataBase.noteDao()
}