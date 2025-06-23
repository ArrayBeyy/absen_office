package com.example.absensi_kantor.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.absensi_kantor.User
import com.example.absensi_kantor.dao.UserDao

@Database(entities = [User::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Tambahkan MIGRATION
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE users ADD COLUMN email TEXT DEFAULT ''")
                database.execSQL("ALTER TABLE users ADD COLUMN jabatan TEXT DEFAULT ''")
                database.execSQL("ALTER TABLE users ADD COLUMN nohp TEXT DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2)  // Inilah kuncinya
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
