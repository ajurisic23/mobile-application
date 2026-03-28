package com.example.auto_moto_oglasnik.pomagaci

import android.view.View
import android.widget.EditText
import android.widget.Spinner
import com.example.auto_moto_oglasnik.R
import com.example.auto_moto_oglasnik.entiteti.NoviOglas
import com.example.auto_moto_oglasnik.entiteti.VoziloDetalji

object OglasFormHelper {

    fun procitajOglasIzForme(view: View, korisnikId: Int): NoviOglas {
        return NoviOglas(
            naslov = view.findViewById<EditText>(R.id.edtNaslov).text.toString(),
            opis = view.findViewById<EditText>(R.id.edtOpis).text.toString(),
            cijena = view.findViewById<EditText>(R.id.edtCijena).text.toString().toDoubleOrNull() ?: 0.0,
            lokacija = view.findViewById<EditText>(R.id.edtLokacija).text.toString(),
            korisnikId = korisnikId,

            marka = view.findViewById<EditText>(R.id.edtMarka).text.toString(),
            model = view.findViewById<EditText>(R.id.edtModel).text.toString(),
            godinaProizvodnje = view.findViewById<EditText>(R.id.edtGodina).text.toString().toIntOrNull() ?: 0,
            kilometri = view.findViewById<EditText>(R.id.edtKilometri).text.toString().toIntOrNull() ?: 0,
            tipGoriva = view.findViewById<Spinner>(R.id.spinnerGorivo).selectedItem.toString(),
            snagaMotora = view.findViewById<EditText>(R.id.edtSnaga).text.toString().toIntOrNull() ?: 0,
            brojBrzina = view.findViewById<EditText>(R.id.edtBrzine).text.toString().toIntOrNull() ?: 0,
            tipMjenjaca = view.findViewById<Spinner>(R.id.spinnerMjenjac).selectedItem.toString()
        )
    }

    fun popuniFormu(view: View, d: VoziloDetalji) {
        view.findViewById<EditText>(R.id.edtNaslov).setText(d.naslov)
        view.findViewById<EditText>(R.id.edtOpis).setText(d.opis ?: "")
        view.findViewById<EditText>(R.id.edtCijena).setText(d.cijena.toString())
        view.findViewById<EditText>(R.id.edtLokacija).setText(d.lokacija)

        view.findViewById<EditText>(R.id.edtMarka).setText(d.marka)
        view.findViewById<EditText>(R.id.edtModel).setText(d.model)
        view.findViewById<EditText>(R.id.edtGodina).setText(d.godinaProizvodnje.toString())
        view.findViewById<EditText>(R.id.edtKilometri).setText(d.kilometri.toString())
        view.findViewById<EditText>(R.id.edtSnaga).setText(d.snagaMotora.toString())
        view.findViewById<EditText>(R.id.edtBrzine).setText(d.brojBrzina.toString())

        postaviSpinnerNaVrijednost(view.findViewById(R.id.spinnerGorivo), d.tipGoriva)
        postaviSpinnerNaVrijednost(view.findViewById(R.id.spinnerMjenjac), d.tipMjenjaca)
    }

    private fun postaviSpinnerNaVrijednost(spinner: Spinner, trazena: String) {
        val adapter = spinner.adapter ?: return
        for (i in 0 until adapter.count) {
            val vrijednost = adapter.getItem(i)?.toString() ?: continue
            if (vrijednost.equals(trazena, ignoreCase = true)) {
                spinner.setSelection(i)
                return
            }
        }
    }
}
