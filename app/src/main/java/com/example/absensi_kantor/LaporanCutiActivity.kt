package com.example.absensi_kantor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.absensi_kantor.databinding.ActivityLaporanCutiBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LaporanCutiActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLaporanCutiBinding
    private val sharedPrefs by lazy { getSharedPreferences("cuti_prefs", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaporanCutiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ambil username dari SharedPreferences
        val username = getSharedPreferences("user_prefs", MODE_PRIVATE)
            .getString("username", "Guest") ?: "Guest"

        val json = sharedPrefs.getString("laporan_cuti", "[]")
        val type = object : TypeToken<MutableList<CutiLaporan>>() {}.type
        val list: MutableList<CutiLaporan> = Gson().fromJson(json, type)

        // Filter cuti user
        val cutiUser = list.filter { it.nama.equals(username, ignoreCase = true) }.toMutableList()
        val totalDiambil = cutiUser.sumOf { it.jumlahHari }

        // Reset jika cuti sudah habis
        var sisaCuti = 12 - totalDiambil
        if (sisaCuti <= 0) {
            sisaCuti = 12
            // Reset riwayat user di SharedPreferences
            val newList = list.filterNot { it.nama.equals(username, ignoreCase = true) }
            sharedPrefs.edit().putString("laporan_cuti", Gson().toJson(newList)).apply()

            cutiUser.clear()
        }

        val laporan = StringBuilder().apply {
            append("Nama: $username\n")
            append("Sisa Cuti: $sisaCuti dari 12 hari\n\n")
            append("Riwayat Cuti:\n")
            if (cutiUser.isEmpty()) {
                append("- Belum ada riwayat cuti aktif\n")
            } else {
                cutiUser.reversed().forEach {
                    append("- ${it.tanggal}: ${it.jumlahHari} hari (${it.alasan})\n")
                }
            }
        }

        binding.textLaporanCuti.text = laporan.toString()
    }
    
    private fun tampilkanSisaCuti(username: String) {
        val prefs = getSharedPreferences("cuti_prefs", MODE_PRIVATE)
        val json = prefs.getString("laporan_cuti", "[]")
        val listType = object : TypeToken<List<CutiLaporan>>() {}.type
        val laporanCuti: List<CutiLaporan> = Gson().fromJson(json, listType)

        val cutiUser = laporanCuti.filter { it.nama.equals(username, ignoreCase = true) }
        val totalCutiDiambil = cutiUser.sumOf { it.jumlahHari }
        val sisaCuti = 12 - totalCutiDiambil

        binding.textLaporanCuti.text = "Sisa Cuti: $sisaCuti hari"
    }
}
