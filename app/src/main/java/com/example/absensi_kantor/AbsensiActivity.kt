package com.example.absensi_kantor

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.absensi_kantor.Riwayat.AbsenRiwayat
import com.example.absensi_kantor.database.AppDatabase
import com.example.absensi_kantor.databinding.ActivityAbsensiBinding
import kotlinx.coroutines.launch
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody.Part
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AbsensiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAbsensiBinding
    private val REQUEST_IMAGE_CAPTURE = 1
    private var capturedBitmap: Bitmap? = null
    private var capturedImageFile: File? = null
    private lateinit var database: AppDatabase
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAbsensiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = AppDatabase.getDatabase(this)
        updateTanggalDanJam()

        // Ambil username dari SharedPreferences
        val preferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        username = preferences.getString("username", "Guest") ?: "Guest"

        binding.btnMasuk.setOnClickListener {
            saveRiwayat("Masuk")

            // Simpan ke SharedPreferences bahwa sudah absen
            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
            prefs.edit().putBoolean("sudah_absen_masuk", true).apply()
        }

        binding.btnPulang.setOnClickListener {
            saveRiwayat("Pulang")
        }

        binding.btnAmbilFoto.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun updateTanggalDanJam() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val date = Date()
        binding.tanggalTextView.text = "Tanggal: ${dateFormat.format(date)}"
        binding.jamTextView.text = "Jam: ${timeFormat.format(date)}"
    }

    private fun saveRiwayat(status: String) {
        val tanggal = binding.tanggalTextView.text.toString().replace("Tanggal: ", "")
        val jam = binding.jamTextView.text.toString().replace("Jam: ", "")
        val keterangan = binding.editKeterangan.text.toString().trim()
        val fotoPath = capturedImageFile?.absolutePath

        val riwayat = AbsenRiwayat(
            tanggal = tanggal,
            jam = jam,
            status = status,
            fotoPath = fotoPath,
            keterangan = keterangan
        )

        lifecycleScope.launch {
            database.absenRiwayatDao().insert(riwayat)
        }

        kirimDataKeLaravel(tanggal, jam, status, keterangan)
    }


    private fun kirimDataKeLaravel(tanggal: String, jam: String, status: String, keterangan: String) {
        val client = OkHttpClient()
        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("tanggal", tanggal)
            .addFormDataPart("jam", jam)
            .addFormDataPart("status", status)
            .addFormDataPart("username", username)
            .addFormDataPart("keterangan", keterangan)

        if (capturedImageFile != null) {
            val fileRequest = capturedImageFile!!.asRequestBody("image/jpeg".toMediaTypeOrNull())
            requestBodyBuilder.addFormDataPart("foto", capturedImageFile!!.name, fileRequest)
        }

        val requestBody = requestBodyBuilder.build()
        val request = Request.Builder()
            .url("https://your-backend-api.com/api/absen")
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(this@AbsensiActivity, "Gagal kirim ke server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                runOnUiThread {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AbsensiActivity, "Absen berhasil dikirim ke server", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@AbsensiActivity, "Gagal simpan ke server", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.fotoImageView.setImageBitmap(imageBitmap)
            capturedBitmap = imageBitmap
            simpanFotoKeStorage(imageBitmap)
        }
    }

    private fun simpanFotoKeStorage(bitmap: Bitmap) {
        val folder = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "AbsenFoto")
        if (!folder.exists()) folder.mkdirs()

        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "ABSEN_${timeStamp}.jpg"
        val file = File(folder, fileName)

        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
            fos.flush()
            fos.close()
            capturedImageFile = file
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Gagal menyimpan foto", Toast.LENGTH_SHORT).show()
        }
    }
}