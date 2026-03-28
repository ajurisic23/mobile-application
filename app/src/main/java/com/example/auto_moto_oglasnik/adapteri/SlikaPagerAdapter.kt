package com.example.auto_moto_oglasnik.adapteri

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.auto_moto_oglasnik.R

class SlikePagerAdapter(
    private val putanjeSlika: List<String>
) : RecyclerView.Adapter<SlikePagerAdapter.SlikaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlikaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_slika_pager, parent, false)
        return SlikaViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlikaViewHolder, position: Int) {
        val putanja = putanjeSlika[position]
        if (putanja.isNotBlank()) {
            holder.img.setImageURI(putanja.toUri())
        } else {

            holder.img.setImageResource(R.mipmap.ic_launcher)
        }
    }

    override fun getItemCount(): Int = putanjeSlika.size

    class SlikaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imgPager)
    }
}
