package com.example.auto_moto_oglasnik.adapteri

import Komentar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.auto_moto_oglasnik.R
import com.example.auto_moto_oglasnik.pomagaci.Sesija
import java.text.SimpleDateFormat
import java.util.Locale

class KomentarAdapter(
    private val lista: List<Komentar>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<KomentarAdapter.KomentarViewHolder>() {

    class KomentarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtAutor: TextView = view.findViewById(R.id.txtAutor)
        val txtOcjena: TextView = view.findViewById(R.id.txtOcjena)
        val txtDatum: TextView = view.findViewById(R.id.txtDatum)
        val txtSadrzaj: TextView = view.findViewById(R.id.txtSadrzaj)
        val btnObrisi: ImageButton = view.findViewById(R.id.btnObrisiKomentar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KomentarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_komentar, parent, false)
        return KomentarViewHolder(view)
    }

    override fun onBindViewHolder(holder: KomentarViewHolder, position: Int) {
        val komentar = lista[position]
        holder.txtAutor.text = komentar.korisnickoIme
        holder.txtSadrzaj.text = komentar.tekst

        try {
            val ulazniFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val datumObjekt = ulazniFormat.parse(komentar.datumKreiranja.substringBefore("."))

            val izlazniFormat = SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.getDefault())
            holder.txtDatum.text = izlazniFormat.format(datumObjekt!!)
        } catch (e: Exception) {
            holder.txtDatum.text = komentar.datumKreiranja
        }

        if (komentar.ocjena != null) {
            holder.txtOcjena.text = "${komentar.ocjena}/5"
            holder.txtOcjena.visibility = View.VISIBLE
        } else {
            holder.txtOcjena.visibility = View.GONE
        }

        val trenutniKorisnikId = Sesija.trenutniKorisnik?.id
        if (trenutniKorisnikId != null && trenutniKorisnikId == komentar.korisnikId) {
            holder.btnObrisi.visibility = View.VISIBLE
            holder.btnObrisi.setOnClickListener {
                onDeleteClick(komentar.id)
            }
        } else {
            holder.btnObrisi.visibility = View.GONE
        }
    }

    override fun getItemCount() = lista.size
}
