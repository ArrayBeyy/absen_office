package com.example.absensi_kantor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.absensi_kantor.databinding.ActivityPengumumanBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PengumumanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPengumumanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengumumanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences("kegiatan_prefs", MODE_PRIVATE)
        val json = prefs.getString("daftar_kegiatan", "[]")
        val listType = object : TypeToken<List<Kegiatan>>() {}.type
        val kegiatanList: List<Kegiatan> = Gson().fromJson(json, listType)

        val hasil = StringBuilder()
        kegiatanList.reversed().forEach { kegiatan ->
            hasil.append("• ${kegiatan.judul}\n   ${kegiatan.tanggal} - ${kegiatan.keterangan}\n\n")
        }

        binding.textPengumuman.text = hasil.toString()
    }
}
