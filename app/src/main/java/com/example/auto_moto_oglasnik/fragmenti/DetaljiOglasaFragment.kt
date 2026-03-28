package com.example.auto_moto_oglasnik.fragmenti

import Komentar
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.auto_moto_oglasnik.PrikazSvihVozilaActivity
import com.example.auto_moto_oglasnik.R
import com.example.auto_moto_oglasnik.adapteri.SlikePagerAdapter
import com.example.auto_moto_oglasnik.entiteti.VoziloDetalji
import com.example.auto_moto_oglasnik.pomagaci.SQLConnection
import com.example.auto_moto_oglasnik.pomagaci.OglasRepository
import com.example.auto_moto_oglasnik.pomagaci.Sesija
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.sql.Connection

class DetaljiOglasaFragment : Fragment(R.layout.activity_detalji_oglasa) {

    private var oglasId: Int = -1
    private var detaljiTrenutni: VoziloDetalji? = null
    private var fromMojiOglasi: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        oglasId = requireArguments().getInt(ARG_OGLAS_ID, -1)

        setFragmentResultListener(UrediOglasFragment.REQUEST_KEY) { _, _ ->
            view?.let { ucitajDetalje(it, oglasId) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postaviLoading(view, true)

        if (oglasId == -1) {
            Toast.makeText(requireContext(), getString(R.string.nedostaje_oglas_id), Toast.LENGTH_LONG).show()
            parentFragmentManager.popBackStack()
            return
        }

        ucitajDetalje(view, oglasId)
    }

    private fun postaviLoading(view: View, ucitava: Boolean) {
        view.findViewById<View>(R.id.sadrzajDetalja).visibility =
            if (ucitava) View.INVISIBLE else View.VISIBLE

        view.findViewById<View>(R.id.progressDetalji).visibility =
            if (ucitava) View.VISIBLE else View.GONE
    }

    private fun ucitajDetalje(view: View, oglasId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            postaviLoading(view, true)

            try {
                val detalji = OglasRepository.dohvatiDetaljeOglasa(oglasId)
                detaljiTrenutni = detalji

                val komentari = dohvatiKomentareIzBaze(oglasId)

                prikaziDetalje(view, detalji)
                prikaziKomentare(view, komentari)
                postaviGumbKomentar(view)

            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.greska_format, e.message ?: getString(R.string.nepoznata_greska)),
                    Toast.LENGTH_LONG
                ).show()
                parentFragmentManager.popBackStack()
            } finally {
                postaviLoading(view, false)
            }
        }
    }

    private suspend fun dohvatiKomentareIzBaze(oglasId: Int): List<Komentar> {
        return withContext(Dispatchers.IO) {
            val listaKomentara = mutableListOf<Komentar>()
            val sql = """
            SELECT k.komentar_id, k.korisnik_id, k.tekst, k.ocjena, k.datum_kreiranja, u.korisnicko_ime
            FROM Komentar_Recenzija k
            JOIN Korisnik u ON k.korisnik_id = u.korisnik_id
            WHERE k.oglas_id = ?
            ORDER BY k.datum_kreiranja DESC
        """.trimIndent()

            try {
                SQLConnection.connect()?.use { connection ->
                    connection.prepareStatement(sql).use { ps ->
                        ps.setInt(1, oglasId)
                        ps.executeQuery().use { rs ->
                            while (rs.next()) {
                                listaKomentara.add(
                                    Komentar(
                                        id = rs.getInt("komentar_id"),
                                        korisnikId = rs.getInt("korisnik_id"),
                                        korisnickoIme = rs.getString("korisnicko_ime"),
                                        tekst = rs.getString("tekst"),
                                        ocjena = rs.getObject("ocjena") as? Int,
                                        datumKreiranja = rs.getTimestamp("datum_kreiranja").toString()
                                    )
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SQLKomentari", "Greška dohvaćanja komentara", e)
            }
            listaKomentara
        }
    }

    private fun prikaziDetalje(view: View, d: VoziloDetalji) {
        val vp = view.findViewById<ViewPager2>(R.id.vpSlike)

        val slikeZaPrikaz = when (oglasId) {
            1 -> listOf(
                "android.resource://${requireContext().packageName}/${R.drawable.bmw320d}",
                "android.resource://${requireContext().packageName}/${R.drawable.bmw2}",
                "android.resource://${requireContext().packageName}/${R.drawable.bmw3}"
            )
            else -> listOf("android.resource://${requireContext().packageName}/${R.drawable.ic_launcher_foreground}")
        }

        vp.adapter = SlikePagerAdapter(slikeZaPrikaz)

        view.findViewById<TextView>(R.id.txtNaslov).text = d.naslov
        view.findViewById<TextView>(R.id.txtCijenaVrijednost).text = "${d.cijena} ${getString(R.string.valuta_euro)}"
        view.findViewById<TextView>(R.id.txtVoziloVrijednost).text = "${d.marka} ${d.model}"
        view.findViewById<TextView>(R.id.txtGodinaVrijednost).text = d.godinaProizvodnje.toString()
        view.findViewById<TextView>(R.id.txtKilometriVrijednost).text = "${d.kilometri} ${getString(R.string.oznaka_km)}"
        view.findViewById<TextView>(R.id.txtGorivoVrijednost).text = d.tipGoriva
        view.findViewById<TextView>(R.id.txtSnagaVrijednost).text = "${d.snagaMotora} ${getString(R.string.oznaka_kw)}"
        view.findViewById<TextView>(R.id.txtMjenjacVrijednost).text = d.tipMjenjaca
        view.findViewById<TextView>(R.id.txtBrzineVrijednost).text = d.brojBrzina.toString()
        view.findViewById<TextView>(R.id.txtOpisVrijednost).text = d.opis ?: ""
        view.findViewById<TextView>(R.id.txtLokacijaVrijednost).text = d.lokacija
        view.findViewById<TextView>(R.id.txtKorisnikVrijednost).text = d.korisnickoIme

        val btnObrisi = view.findViewById<Button>(R.id.btnObrisi)
        val btnUredi = view.findViewById<Button>(R.id.btnUredi)

        val isOwner = Sesija.trenutniKorisnik?.id == d.korisnikId
        btnObrisi.visibility = if (isOwner) View.VISIBLE else View.GONE
        btnUredi.visibility = if (isOwner) View.VISIBLE else View.GONE

        btnObrisi.setOnClickListener {
            potvrdaBrisanja()
        }
        btnUredi.setOnClickListener {
            (activity as? PrikazSvihVozilaActivity)?.otvoriUrediOglas(oglasId)
        }
    }
    private fun potvrdaBrisanja() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.brisanje_oglasa_naslov))
            .setMessage(getString(R.string.brisanje_oglasa_poruka))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.obrisi)) { _, _ ->
                obrisiOglas()
            }
            .setNegativeButton(getString(R.string.odustani), null)
            .show()
    }
    private fun obrisiOglas() {
        val d = detaljiTrenutni ?: run {
            Toast.makeText(requireContext(), getString(R.string.detalji_nisu_ucitani), Toast.LENGTH_LONG).show()
            return
        }

        val trenutniId = Sesija.trenutniKorisnik?.id
        if (trenutniId != d.korisnikId) {
            Toast.makeText(requireContext(), getString(R.string.nemate_pravo_brisanja), Toast.LENGTH_LONG).show()
            return
        }


        viewLifecycleOwner.lifecycleScope.launch {
            try {
                OglasRepository.obrisiOglas(oglasId = oglasId, korisnikId = trenutniId)
                Toast.makeText(requireContext(), getString(R.string.oglas_obrisan), Toast.LENGTH_LONG).show()
                parentFragmentManager.popBackStack()
            } catch (e: Exception) {
                Toast.makeText(requireContext(), getString(R.string.greska_format, e.message), Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun prikaziKomentare(view: View, komentari: List<Komentar>) {
        val rvKomentari = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recyclerViewKomentari)

        if (komentari.isEmpty()) {
            rvKomentari.visibility = View.GONE
        } else {
            rvKomentari.visibility = View.VISIBLE
            rvKomentari.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)

            rvKomentari.adapter = com.example.auto_moto_oglasnik.adapteri.KomentarAdapter(komentari) { idKomentara ->
                potvrdiBrisanjeKomentara(idKomentara)
            }
        }
    }
    private fun potvrdiBrisanjeKomentara(komentarId: Int) {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Brisanje komentara")
            .setMessage("Jeste li sigurni da želite obrisati ovaj komentar?")
            .setPositiveButton("Da") { _, _ ->
                obrisiKomentarLogika(komentarId)
            }
            .setNegativeButton("Ne", null)
            .show()
    }

    private fun obrisiKomentarLogika(komentarId: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            val uspjeh = withContext(Dispatchers.IO) {
                try {
                    SQLConnection.connect()?.use { conn ->
                        conn.prepareStatement("DELETE FROM Komentar_Recenzija WHERE komentar_id = ?").use { ps ->
                            ps.setInt(1, komentarId)
                            ps.executeUpdate() > 0
                        }
                    } ?: false
                } catch (e: Exception) {
                    Log.e("Brisanje", "Greška", e)
                    false
                }
            }

            if (uspjeh) {
                Toast.makeText(context, "Komentar obrisan", Toast.LENGTH_SHORT).show()
                ucitajDetalje(requireView(), oglasId)
            } else {
                Toast.makeText(context, "Greška pri brisanju", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun postaviGumbKomentar(view: View) {
        val btnDodaj = view.findViewById<Button>(R.id.btnDodajKomentar)

        val korisnikLogiran = Sesija.trenutniKorisnik != null
        val korisnikJeVlasnik = Sesija.trenutniKorisnik?.id == detaljiTrenutni?.korisnikId

        if (korisnikLogiran && !korisnikJeVlasnik) {
            btnDodaj.visibility = View.VISIBLE
            btnDodaj.setOnClickListener {
                val dialog = DodajKomentarFragment(oglasId) {
                    ucitajDetalje(view, oglasId)
                }
                dialog.show(parentFragmentManager, "DodajKomentarTag")
            }
        } else {
            btnDodaj.visibility = View.GONE
        }
    }

    companion object {
        private const val ARG_OGLAS_ID = "oglasId"

        fun newInstance(oglasId: Int): DetaljiOglasaFragment {
            val f = DetaljiOglasaFragment()
            f.arguments = Bundle().apply { putInt(ARG_OGLAS_ID, oglasId) }
            return f
        }
    }
}
