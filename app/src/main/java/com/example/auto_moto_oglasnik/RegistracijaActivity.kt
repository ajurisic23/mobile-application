package com.example.auto_moto_oglasnik
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.auto_moto_oglasnik.entiteti.Korisnik
import com.example.auto_moto_oglasnik.pomagaci.KorisnikDAO
import kotlinx.coroutines.launch
class RegistracijaActivity : AppCompatActivity() {
    private val korisnik = Korisnik()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registracija)
        val pocetna=findViewById<TextView>(R.id.txtIdiNaPocetnu)
        val gumb=findViewById<Button>(R.id.btnReg)
        val idi=findViewById<TextView>(R.id.txtIdiNaPrijavu)
        val etIme = findViewById<EditText>(R.id.et_ime)
        val etPrezime = findViewById<EditText>(R.id.et_prez)
        val etKorime = findViewById<EditText>(R.id.et_kor)
        val etEmail = findViewById<EditText>(R.id.et_email)
        val etLozinka = findViewById<EditText>(R.id.et_loz)
        idi.setOnClickListener{
            val intent = Intent(this, PrijavaActivity::class.java)
            startActivity(intent)
        }
        pocetna.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        gumb.setOnClickListener {
            korisnik.ime = etIme.text.toString().trim()
            korisnik.prezime = etPrezime.text.toString().trim()
            korisnik.korime = etKorime.text.toString().trim()
            korisnik.email = etEmail.text.toString().trim()
            korisnik.lozinka = etLozinka.text.toString()
            if (korisnik.ime.isEmpty() || korisnik.prezime.isEmpty() || korisnik.korime.isEmpty() || korisnik.email.isEmpty() || korisnik.lozinka.isEmpty()) {
                Toast.makeText(this, "Sva polja moraju biti popunjena!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch(Dispatchers.IO) {
                val emailPostoji = KorisnikDAO.provjeriPostojanjeEmaila(korisnik.email)
                if (emailPostoji) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegistracijaActivity, "Email je već registriran!", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                val korimeZauzeto = KorisnikDAO.provjeriPostojanjeKorimena(korisnik.korime)
                if (korimeZauzeto) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegistracijaActivity, "Korisničko ime je zauzeto!", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                val uspjeh = KorisnikDAO.registrirajKorisnika(korisnik)
                withContext(Dispatchers.Main) {
                    if (uspjeh) {
                        Toast.makeText(this@RegistracijaActivity, "${korisnik.ime} uspješna registracija", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@RegistracijaActivity, PrijavaActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@RegistracijaActivity, "Registracija neuspješna! Greška u bazi.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
