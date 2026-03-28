package com.example.auto_moto_oglasnik.adapteri

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.auto_moto_oglasnik.R
import com.example.auto_moto_oglasnik.entiteti.OglasZaListu

class OglasAdapter(
    private var oglasi: List<OglasZaListu>,
    private var naKlik: (OglasZaListu) -> Unit
) : RecyclerView.Adapter<OglasAdapter.OglasVH>() {
    @SuppressLint("NotifyDataSetChanged")
    fun updateList(novaLista: List<OglasZaListu>) {
        this.oglasi = novaLista
        notifyDataSetChanged()
    }
    class OglasVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imgVozilo)
        val txtNaziv: TextView = itemView.findViewById(R.id.txtNazivVozila)
        val txtCijena: TextView = itemView.findViewById(R.id.txtCijena)
        val txtGodina: TextView = itemView.findViewById(R.id.txtGodinaProizvodnje)
        val txtKm: TextView = itemView.findViewById(R.id.txtKilometri)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OglasVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_prikaz_vozla, parent, false)
        return OglasVH(v)
    }
    private var odabranaZaUsporedbu = mutableSetOf<Int>()
    var onUsporedbaChanged: ((List<Int>) -> Unit)? = null
    override fun onBindViewHolder(holder: OglasVH, position: Int) {
        val oglas = oglasi[position]
        val context = holder.itemView.context

        holder.txtNaziv.text = oglas.naslov
        holder.txtCijena.text = context.getString(R.string.format_cijena_lista, oglas.cijena)
        holder.txtGodina.text = context.getString(R.string.format_godina_lista, oglas.godinaProizvodnje)
        holder.txtKm.text = context.getString(R.string.format_kilometri_lista, oglas.kilometri)

        val putanja = oglas.putanjaPrveSlike
        if (!putanja.isNullOrBlank()) {
            holder.img.setImageURI(putanja.toUri())
        } else {
            holder.img.setImageResource(R.mipmap.ic_launcher)
        }

        holder.itemView.setOnClickListener {
            naKlik(oglas)
        }

        if (odabranaZaUsporedbu.contains(oglas.oglasId)) {
            holder.itemView.setBackgroundColor(android.graphics.Color.LTGRAY)
        } else {
            holder.itemView.setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
        holder.itemView.setOnLongClickListener {
            if (odabranaZaUsporedbu.contains(oglas.oglasId)) {
                odabranaZaUsporedbu.remove(oglas.oglasId)
            } else {
                if (odabranaZaUsporedbu.size < 2) {
                    odabranaZaUsporedbu.add(oglas.oglasId)
                } else {
                    android.widget.Toast.makeText(context, "Maksimalno 2 vozila za usporedbu!", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
            notifyItemChanged(position)
            onUsporedbaChanged?.invoke(odabranaZaUsporedbu.toList())
            true
        }

    }

    override fun getItemCount(): Int = oglasi.size
}
