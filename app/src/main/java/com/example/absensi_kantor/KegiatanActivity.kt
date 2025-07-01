package com.example.absensi_kantor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.absensi_kantor.databinding.ActivityKegiatanBinding
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson

class KegiatanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKegiatanBinding
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKegiatanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSimpanKegiatan.setOnClickListener {
            val judul = binding.edtJudul.text.toString().trim()
            val tanggal = binding.edtTanggal.text.toString().trim()
            val keterangan = binding.edtKeterangan.text.toString().trim()

            if (judul.isNotEmpty() && tanggal.isNotEmpty() && keterangan.isNotEmpty()) {
                val kegiatan = Kegiatan(judul, tanggal, keterangan)
                simpanKegiatan(kegiatan)

                Toast.makeText(this, "Kegiatan disimpan!", Toast.LENGTH_SHORT).show()

                // Pindah ke PengumumanActivity
                val intent = Intent(this, PengumumanActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun simpanKegiatan(kegiatan: Kegiatan) {
        val preferences = getSharedPreferences("kegiatan_prefs", MODE_PRIVATE)
        val listType = object : com.google.gson.reflect.TypeToken<MutableList<Kegiatan>>() {}.type
        val existingJson = preferences.getString("daftar_kegiatan", "[]")
        val daftar: MutableList<Kegiatan> = gson.fromJson(existingJson, listType)
        daftar.add(kegiatan)

        val jsonBaru = gson.toJson(daftar)
        preferences.edit().putString("daftar_kegiatan", jsonBaru).apply()
    }
}
