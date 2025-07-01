package com.example.absensi_kantor

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.example.absensi_kantor.dao.UserDao
import com.example.absensi_kantor.database.AppDatabase
import com.example.absensi_kantor.databinding.ActivityHomeBinding
import kotlinx.coroutines.launch
import java.io.File

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var userDao: UserDao
    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        supportActionBar?.hide()
        setContentView(binding.root)

        // Pasang toolbar sebagai action bar
        setSupportActionBar(binding.toolbar)

        // Setup Sidebar Hamburger
        drawerToggle = ActionBarDrawerToggle(this, binding.drawerLayout, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Sidebar Navigation
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_logout -> {
                    val preferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    preferences.edit().clear().apply()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Load User Data
        userDao = AppDatabase.getDatabase(this).userDao()
        val preferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val username = preferences.getString("username", "Guest") ?: "Guest"
        val profilePath = preferences.getString("profile_pic_path", null)


        if (profilePath != null) {
            val file = File(profilePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                binding.imageProfileHome.setImageBitmap(bitmap)
            }
        }

        binding.userNameTextView.text = username

        val sudahAbsen = preferences.getBoolean("sudah_absen_masuk", false)
        if (sudahAbsen) {
            binding.statusAbsenTextView.text = "Sudah Melakukan Absen"
        } else {
            binding.statusAbsenTextView.text = "Belum Melakukan Absen Masuk"
        }

        lifecycleScope.launch {
            val user = userDao.getUserByUsername(username)
            if (user != null) {
                binding.userNameTextView.text = user.username
                binding.userPositionTextView.text = user.jabatan
                binding.emailTextView.text = "Email: ${user.email}"
                binding.nohpTextView.text = "No HP: ${user.nohp}"
            } else {
                binding.userPositionTextView.text = "Jabatan tidak ditemukan"
                binding.emailTextView.text = "Email: -"
                binding.nohpTextView.text = "No HP: -"
            }
        }

        // Grid Menu Navigasi
        binding.menuKegiatan.setOnClickListener {
            startActivity(Intent(this, KegiatanActivity::class.java))
        }
        binding.menuAbsensi.setOnClickListener {
            startActivity(Intent(this, AbsensiActivity::class.java))
        }
        binding.menuPengumuman.setOnClickListener {
            startActivity(Intent(this, PengumumanActivity::class.java))
        }
        binding.btnRiwayat.setOnClickListener {
            startActivity(Intent(this, RiwayatActivity::class.java))
        }
        binding.btnCameraAbsen.setOnClickListener {
            val intent = Intent(this, AbsensiActivity::class.java)
            startActivity(intent)
        }
        binding.btnBeranda.setOnClickListener {
            Toast.makeText(this, "Kamu sedang berada di halaman Beranda", Toast.LENGTH_SHORT).show()
        }
        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
