package com.gabrielfv.samples.complete.di

import android.content.Context
import androidx.fragment.app.FragmentFactory
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gabrielfv.crane.annotations.CraneRoot
import com.gabrielfv.crane.core.Crane
import com.gabrielfv.samples.complete.db.NotesDb
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
@CraneRoot
interface RootModule {

  @Binds
  fun bindsFragmentFactory(daggerFragmentFactory: DaggerFragmentFactory): FragmentFactory

  companion object {

    @Singleton
    @Provides
    fun provideCrane(): Crane = Crane.create(Router.get())

    @Singleton
    @Provides
    fun provideDb(appContext: Context): NotesDb = Room.databaseBuilder(
      appContext,
      NotesDb::class.java,
      "notes-db"
    ).build()

    @Provides
    fun provideNotesDao(db: NotesDb) = db.notesDao()
  }
}
