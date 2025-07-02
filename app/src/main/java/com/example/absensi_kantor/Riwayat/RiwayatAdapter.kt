package com.example.absensi_kantor

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.graphics.BitmapFactory
import android.widget.ImageView
import java.io.File
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.absensi_kantor.Riwayat.AbsenRiwayat

class RiwayatAdapter : ListAdapter<AbsenRiwayat, RiwayatAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tanggal: TextView = itemView.findViewById(R.id.textTanggal)
        val jam: TextView = itemView.findViewById(R.id.textJam)
        val status: TextView = itemView.findViewById(R.id.textStatus)
        val keteranganText: TextView = itemView.findViewById(R.id.keteranganText)
        val foto: ImageView = itemView.findViewById(R.id.imageFoto) // Tambahkan ini
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_riwayat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val riwayat = getItem(position)
        holder.tanggal.text = "Tanggal: ${riwayat.tanggal}"
        holder.jam.text = "Jam: ${riwayat.jam}"
        holder.status.text = "Status: ${riwayat.status}"
        holder.keteranganText.text = "Keterangan: ${riwayat.keterangan ?: "-"}"


        // Tampilkan foto jika ada
        if (!riwayat.fotoPath.isNullOrEmpty()) {
            val file = File(riwayat.fotoPath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                holder.foto.setImageBitmap(bitmap)
                holder.foto.visibility = View.VISIBLE
            } else {
                holder.foto.setImageDrawable(null)
                holder.foto.visibility = View.GONE
            }
        } else {
            holder.foto.setImageDrawable(null)
            holder.foto.visibility = View.GONE
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AbsenRiwayat>() {
        override fun areItemsTheSame(oldItem: AbsenRiwayat, newItem: AbsenRiwayat): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AbsenRiwayat, newItem: AbsenRiwayat): Boolean {
            return oldItem == newItem
        }
    }
}