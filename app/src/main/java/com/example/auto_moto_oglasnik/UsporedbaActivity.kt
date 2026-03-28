package com.example.auto_moto_oglasnik

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.auto_moto_oglasnik.pomagaci.OglasRepository
import kotlinx.coroutines.launch

class UsporedbaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usporedba)

        val id1 = intent.getIntExtra("ID1", -1)
        val id2 = intent.getIntExtra("ID2", -1)

        if (id1 != -1 && id2 != -1) {
            popuniPodatke(id1, id2)
        } else {
            Toast.makeText(this, "Greška pri prijenosu podataka", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun popuniPodatke(id1: Int, id2: Int) {
        lifecycleScope.launch {
            try {
                val v1 = OglasRepository.dohvatiDetaljeOglasa(id1)
                val v2 = OglasRepository.dohvatiDetaljeOglasa(id2)

                if (v1 != null && v2 != null) {
                    findViewById<TextView>(R.id.txtNaslovV1).text = v1.naslov
                    findViewById<TextView>(R.id.txtCijenaV1).text = "${v1.cijena} €"
                    findViewById<TextView>(R.id.txtKmV1).text = "${v1.kilometri} km"
                    findViewById<TextView>(R.id.txtGodinaV1).text = "${v1.godinaProizvodnje}. god"
                    if (!v1.putanjeSlika.isNullOrEmpty()) {
                        findViewById<ImageView>(R.id.imgV1).setImageURI(Uri.parse(v1.putanjeSlika[0]))
                    }

                    findViewById<TextView>(R.id.txtNaslovV2).text = v2.naslov
                    findViewById<TextView>(R.id.txtCijenaV2).text = "${v2.cijena} €"
                    findViewById<TextView>(R.id.txtKmV2).text = "${v2.kilometri} km"
                    findViewById<TextView>(R.id.txtGodinaV2).text = "${v2.godinaProizvodnje}. god"
                    if (!v2.putanjeSlika.isNullOrEmpty()) {
                        findViewById<ImageView>(R.id.imgV2).setImageURI(Uri.parse(v2.putanjeSlika[0]))
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(this@UsporedbaActivity, "Greška: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}