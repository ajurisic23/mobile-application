package com.example.auto_moto_oglasnik.fragmenti

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.auto_moto_oglasnik.R
import com.example.auto_moto_oglasnik.pomagaci.SQLConnection
import com.example.auto_moto_oglasnik.pomagaci.Sesija
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DodajKomentarFragment(
    private val oglasId: Int,
    private val onCommentAdded: () -> Unit
) : DialogFragment(R.layout.dialog_dodaj_komentar) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val etTekst = view.findViewById<EditText>(R.id.etKomentarTekst)
        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val btnPosalji = view.findViewById<Button>(R.id.btnPosaljiKomentar)

        btnPosalji.setOnClickListener {
            val tekst = etTekst.text.toString()
            val ocjena = ratingBar.rating.toInt()

            if (tekst.isBlank()) {
                Toast.makeText(context, "Unesite tekst komentara", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val uspjeh = spremiKomentarUBazu(oglasId, tekst, ocjena)
                if (uspjeh) {
                    Toast.makeText(context, "Komentar dodan!", Toast.LENGTH_SHORT).show()
                    onCommentAdded()
                    dismiss()
                } else {
                    Toast.makeText(context, "Greška! Možda ste već komentirali?", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private suspend fun spremiKomentarUBazu(oglasId: Int, tekst: String, ocjena: Int): Boolean {
        val korisnikId = Sesija.trenutniKorisnik?.id ?: return false

        return withContext(Dispatchers.IO) {
            try {
                SQLConnection.connect()?.use { conn ->
                    val sql = "INSERT INTO Komentar_Recenzija (oglas_id, korisnik_id, tekst, ocjena) VALUES (?, ?, ?, ?)"
                    conn.prepareStatement(sql).use { ps ->
                        ps.setInt(1, oglasId)
                        ps.setInt(2, korisnikId)
                        ps.setString(3, tekst)
                        ps.setInt(4, ocjena)
                        ps.executeUpdate() > 0
                    }
                } ?: false
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}
