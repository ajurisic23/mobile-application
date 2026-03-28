package com.example.auto_moto_oglasnik

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.auto_moto_oglasnik.fragmenti.DetaljiOglasaFragment
import com.example.auto_moto_oglasnik.fragmenti.ListaSvihOglasaFragment
import com.example.auto_moto_oglasnik.fragmenti.MojiOglasiFragment
import com.example.auto_moto_oglasnik.fragmenti.NoviOglasFragment
import com.example.auto_moto_oglasnik.fragmenti.UrediOglasFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class PrikazSvihVozilaActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_EKRAN = "ekran"
        const val EKRAN_MOJI = "moji"
        const val EKRAN_NOVI = "novi"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_vozila)

        val donjaNavigacija = findViewById<BottomNavigationView>(R.id.donjaNavigacija)
        donjaNavigacija.setOnItemSelectedListener { stavka ->
            when (stavka.itemId) {
                R.id.nav_pocetna -> {
                    prikaziFragment(ListaSvihOglasaFragment(), false)
                    true
                }

                R.id.nav_postavke -> {
                    val intent = Intent(this, PostavkeActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }

                R.id.nav_profil -> {
                    val intent = Intent(this, ProfilActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        if (savedInstanceState == null) {
            val fragment = when (intent.getStringExtra(EXTRA_EKRAN)) {
                EKRAN_MOJI -> MojiOglasiFragment()
                EKRAN_NOVI -> NoviOglasFragment()
                else -> ListaSvihOglasaFragment()
            }
            prikaziFragment(fragment, false)
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!supportFragmentManager.popBackStackImmediate()) {
                    finish()
                }
            }
        })
    }

    fun otvoriDetalje(oglasId: Int) {
        prikaziFragment(DetaljiOglasaFragment.newInstance(oglasId), true)
    }

    fun otvoriUrediOglas(oglasId: Int) {
        prikaziFragment(UrediOglasFragment.newInstance(oglasId), true)
    }
    //TODO kod profila za novi oglas i moje oglase
    fun otvoriMojeOglase() {
        prikaziFragment(MojiOglasiFragment(), true)
    }

    fun otvoriNoviOglas() {
        prikaziFragment(NoviOglasFragment(), true)
    }

    private fun prikaziFragment(fragment: Fragment, dodajNaBackStack: Boolean) {
        val transakcija = supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragmentKontejner, fragment)

        if (dodajNaBackStack) {
            transakcija.addToBackStack(null)
        }

        transakcija.commit()
    }
}
