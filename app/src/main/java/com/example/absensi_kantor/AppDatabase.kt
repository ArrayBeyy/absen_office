package com.example.absensi_kantor.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.absensi_kantor.User
import com.example.absensi_kantor.Riwayat.AbsenRiwayat
import com.example.absensi_kantor.dao.UserDao
import com.example.absensi_kantor.dao.AbsenRiwayatDao

@Database(entities = [User::class, AbsenRiwayat::class], version = 3)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun absenRiwayatDao(): AbsenRiwayatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migrasi 1 -> 2 (sudah ada)
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE users ADD COLUMN email TEXT DEFAULT ''")
                database.execSQL("ALTER TABLE users ADD COLUMN jabatan TEXT DEFAULT ''")
                database.execSQL("ALTER TABLE users ADD COLUMN nohp TEXT DEFAULT ''")
            }
        }

        // Migrasi 2 -> 3 (tambah tabel riwayat absen)
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS absen_riwayat (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                tanggal TEXT NOT NULL,
                jam TEXT NOT NULL,
                status TEXT NOT NULL
            )
            """.trimIndent()
                )
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
