package com.example.auto_moto_oglasnik

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.auto_moto_oglasnik.pomagaci.Sesija
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfilActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)

        val txtIme = findViewById<TextView>(R.id.txtImeProfil)
        val txtPrezime = findViewById<TextView>(R.id.txtPrezimeProfil)
        val txtKorime = findViewById<TextView>(R.id.txtKorimeProfil)
        val txtDatumRegistracije = findViewById<TextView>(R.id.txtDatumRegistracijeProfil)
        val btnMojiOglasi = findViewById<Button>(R.id.btnMojiOglasi)
        val btnNoviOglas = findViewById<Button>(R.id.btnNoviOglas)
        val donjaNavigacija = findViewById<BottomNavigationView>(R.id.donjaNavigacija)

        val korisnik = Sesija.trenutniKorisnik
        Log.d("ProfilActivity", "Trenutni korisnik: ${korisnik?.ime}")

        if (korisnik != null) {
            txtIme.text = getString(R.string.profil_ime, korisnik.ime)
            Log.d("ProfilActivity", "Ime korisnika: ${korisnik.ime}")
            txtPrezime.text = getString(R.string.profil_prezime, korisnik.prezime)
            txtKorime.text = getString(R.string.profil_korisnicko_ime, korisnik.korime)
            txtDatumRegistracije.text = getString(R.string.profil_datum_registracije, korisnik.datum.toString())
        } else {
            Toast.makeText(this, getString(R.string.greska_niste_prijavljeni), Toast.LENGTH_SHORT).show()
            val intent = Intent(this, PrikazSvihVozilaActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        btnNoviOglas.setOnClickListener {
            val intent = Intent(this, PrikazSvihVozilaActivity::class.java)
            intent.putExtra("ekran", "novi")
            startActivity(intent)
        }

        btnMojiOglasi.setOnClickListener {
            val intent = Intent(this, PrikazSvihVozilaActivity::class.java)
            intent.putExtra("ekran", "moji")
            startActivity(intent)
        }
        
        donjaNavigacija.setOnItemSelectedListener { stavka ->
            when (stavka.itemId) {
                R.id.nav_pocetna -> {
                    val intent = Intent(this, PrikazSvihVozilaActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }
                R.id.nav_postavke -> {
                    val intent = Intent(this, PostavkeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }
                R.id.nav_profil -> {
                    true
                }
                else -> false
            }
        }

        donjaNavigacija.menu.findItem(R.id.nav_profil).isChecked = true
    }
}
