package com.example.absensi_kantor

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.absensi_kantor.databinding.ActivityCutiBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CutiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCutiBinding
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCutiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefs = getSharedPreferences("cuti_prefs", MODE_PRIVATE)

        binding.btnAjukanCuti.setOnClickListener {
            val nama = binding.editNama.text.toString()
            val tanggalMulai = binding.tanggalMulaiEdit.text.toString().trim()
            val tanggalSelesai = binding.tanggalSelesaiEdit.text.toString().trim()
            val alasan = binding.alasanEdit.text.toString().trim()
            val jumlahHari = binding.editJumlahHari.text.toString().toIntOrNull() ?: 0

            if (jumlahHari > 12) {
                Toast.makeText(this, "Jumlah cuti tidak boleh melebihi 12 hari", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (nama.isEmpty() || tanggalMulai.isEmpty() || tanggalSelesai.isEmpty() || alasan.isEmpty() || jumlahHari <= 0) {
                Toast.makeText(this, "Mohon lengkapi semua field dengan benar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val rangeTanggal = "$tanggalMulai - $tanggalSelesai"
            val newCuti = CutiLaporan(nama, rangeTanggal, alasan, jumlahHari)

            val json = sharedPrefs.getString("laporan_cuti", "[]")
            val type = object : TypeToken<MutableList<CutiLaporan>>() {}.type
            val list: MutableList<CutiLaporan> = Gson().fromJson(json, type)

            list.add(newCuti)
            sharedPrefs.edit().putString("laporan_cuti", Gson().toJson(list)).apply()

            Toast.makeText(this, "Cuti berhasil diajukan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
