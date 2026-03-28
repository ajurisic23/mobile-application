package com.example.auto_moto_oglasnik
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.auto_moto_oglasnik.pomagaci.KorisnikDAO
import com.example.auto_moto_oglasnik.pomagaci.Sesija
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
class PrijavaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_prijava)

        val korisnickoime = findViewById<EditText>(R.id.et_korimePrijava)
        val lozinka = findViewById<EditText>(R.id.et_lozinkaPrijava)
        val gumb = findViewById<Button>(R.id.btnPrijava)
        val preusmjeri = findViewById<TextView>(R.id.txtIdiNaRegistraciju)
        val pocetna = findViewById<TextView>(R.id.txtIdiNaPocetnuStr)

        preusmjeri.setOnClickListener {
            val intent = Intent(this, RegistracijaActivity::class.java)
            startActivity(intent)
        }

        pocetna.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        gumb.setOnClickListener {
            val korime = korisnickoime.text.toString().trim()
            val pass = lozinka.text.toString()

            if (korime.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Unesite korisničko ime i lozinku!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch(Dispatchers.IO) {

                val prijavljeniKorisnik = KorisnikDAO.prijavaKorisnika(korime, pass)

                withContext(Dispatchers.Main) {
                    if (prijavljeniKorisnik != null) {

                        Sesija.trenutniKorisnik = prijavljeniKorisnik

                        Toast.makeText(
                            this@PrijavaActivity,
                            "Prijava uspješna! Dobrodošao ${prijavljeniKorisnik.ime}",
                            Toast.LENGTH_LONG
                        ).show()

                        val intent = Intent(this@PrijavaActivity, PrikazSvihVozilaActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@PrijavaActivity,
                            "Pogrešno korisničko ime ili lozinka!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}