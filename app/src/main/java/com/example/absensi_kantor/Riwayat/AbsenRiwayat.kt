package com.example.absensi_kantor.Riwayat

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "absen_riwayat")
data class AbsenRiwayat(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tanggal: String,
    val jam: String,
    val status: String,
    val fotoPath: String? = null // ← Tambahan untuk path foto
)