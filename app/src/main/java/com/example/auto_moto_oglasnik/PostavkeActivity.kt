package com.example.auto_moto_oglasnik

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.lifecycleScope
import com.example.auto_moto_oglasnik.pomagaci.SQLConnection
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.switchmaterial.SwitchMaterial
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection

class PostavkeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_postavke)

        val switchTamniNacin = findViewById<SwitchMaterial>(R.id.switchTamniNacin)
        val donjaNavigacija = findViewById<BottomNavigationView>(R.id.donjaNavigacija)
        val btnHrvatski = findViewById<Button>(R.id.btnHrvatski)
        val btnEngleski = findViewById<Button>(R.id.btnEngleski)
        //val btnTestBaze = findViewById<Button>(R.id.btnTestBaze)
        //val txtRezultatBaze = findViewById<TextView>(R.id.txtRezultatBaze)

        //btnTestBaze.text = getString(R.string.test_baze)

        val trenutniNocniNacin = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        switchTamniNacin.isChecked = trenutniNocniNacin == Configuration.UI_MODE_NIGHT_YES

        switchTamniNacin.setOnCheckedChangeListener { _, isChecked ->
            val zeljeniMod = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            if (AppCompatDelegate.getDefaultNightMode() != zeljeniMod) {
                AppCompatDelegate.setDefaultNightMode(zeljeniMod)
            }
        }

        btnHrvatski.setOnClickListener {
            val locales = LocaleListCompat.forLanguageTags("hr")
            AppCompatDelegate.setApplicationLocales(locales)
        }

        btnEngleski.setOnClickListener {
            val locales = LocaleListCompat.forLanguageTags("en")
            AppCompatDelegate.setApplicationLocales(locales)
        }

        donjaNavigacija.setOnItemSelectedListener { stavka ->
            when (stavka.itemId) {
                R.id.nav_pocetna -> {
                    val intent = Intent(this, PrikazSvihVozilaActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.nav_postavke -> {
                    true
                }
                R.id.nav_profil -> {
                    val intent = Intent(this, ProfilActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }

        donjaNavigacija.menu.findItem(R.id.nav_postavke).isChecked = true

        /*
        btnTestBaze.setOnClickListener {
            lifecycleScope.launch {
                var imeKorisnika: String? = null
                var error: String? = null

                withContext(Dispatchers.IO) {
                    var connection: Connection? = null
                    try {
                        connection = SQLConnection.connect()
                        if (connection != null) {
                            val statement = connection.createStatement()
                            val resultSet = statement.executeQuery("SELECT korisnicko_ime FROM dbo.Korisnik WHERE korisnik_id = 1")
                            if (resultSet.next()) {
                                imeKorisnika = resultSet.getString("korisnicko_ime")
                            } else {
                                error = getString(R.string.greska_korisnik_nije_pronadjen)
                            }
                        } else {
                            error = getString(R.string.greska_konekcija_baza)
                        }
                    } catch (e: Exception) {
                        Log.e("DatabaseTest", "Greška u radu s bazom: ", e)
                        error = e.message
                    } finally {
                        SQLConnection.close(connection)
                    }
                }

                if (imeKorisnika != null) {
                    txtRezultatBaze.text = imeKorisnika
                    Toast.makeText(this@PostavkeActivity, getString(R.string.podaci_dohvaceni), Toast.LENGTH_SHORT).show()
                } else {
                    txtRezultatBaze.text = getString(R.string.greska)
                    Toast.makeText(this@PostavkeActivity, getString(R.string.greska_format, error), Toast.LENGTH_LONG).show()
                }
            }
        }*/
    }
}
