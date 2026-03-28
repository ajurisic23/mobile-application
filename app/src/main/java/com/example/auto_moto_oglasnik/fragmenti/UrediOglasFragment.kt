package com.example.auto_moto_oglasnik.fragmenti

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.example.auto_moto_oglasnik.R
import com.example.auto_moto_oglasnik.pomagaci.OglasFormHelper
import com.example.auto_moto_oglasnik.pomagaci.OglasRepository
import com.example.auto_moto_oglasnik.pomagaci.Sesija
import kotlinx.coroutines.launch

class UrediOglasFragment : Fragment(R.layout.activity_novi_oglas) {

    private var oglasId: Int = -1
    private var voziloId: Int = -1
    private var korisnikIdVlasnika: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        oglasId = requireArguments().getInt(ARG_OGLAS_ID, -1)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (oglasId == -1) {
            Toast.makeText(requireContext(), getString(R.string.nedostaje_oglas_id), Toast.LENGTH_LONG).show()
            parentFragmentManager.popBackStack()
            return
        }

        view.findViewById<TextView>(R.id.txtNaslovEkrana).text = getString(R.string.uredi_oglas)

        val btnSpremi = view.findViewById<Button>(R.id.btnSpremiOglas)
        btnSpremi.text = getString(R.string.spremi_promjene)

        postaviFormuOmogucenu(view, false)
        ucitajPodatkeZaUredivanje(view)

        btnSpremi.setOnClickListener {
            spremiPromjene(view)
        }
    }

    private fun postaviFormuOmogucenu(view: View, omoguceno: Boolean) {
        val svi = listOf(
            R.id.edtNaslov,
            R.id.edtCijena,
            R.id.edtMarka,
            R.id.edtModel,
            R.id.edtGodina,
            R.id.edtKilometri,
            R.id.spinnerGorivo,
            R.id.edtSnaga,
            R.id.edtBrzine,
            R.id.spinnerMjenjac,
            R.id.edtOpis,
            R.id.edtLokacija,
            R.id.btnSpremiOglas
        )

        for (id in svi) {
            view.findViewById<View>(id).isEnabled = omoguceno
        }
    }

    private fun ucitajPodatkeZaUredivanje(view: View) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val podaci = OglasRepository.dohvatiZaUredivanje(oglasId)
                voziloId = podaci.voziloId
                korisnikIdVlasnika = podaci.detalji.korisnikId

                val trenutniId = Sesija.trenutniKorisnik?.id
                if (trenutniId == null || trenutniId != korisnikIdVlasnika) {
                    Toast.makeText(requireContext(), getString(R.string.nemate_pravo_uredivanja), Toast.LENGTH_LONG).show()
                    parentFragmentManager.popBackStack()
                    return@launch
                }

                OglasFormHelper.popuniFormu(view, podaci.detalji)
                postaviFormuOmogucenu(view, true)

            } catch (e: Exception) {
                Toast.makeText(requireContext(), getString(R.string.greska_format, e.message), Toast.LENGTH_LONG).show()
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun spremiPromjene(view: View) {
        val trenutniId = Sesija.trenutniKorisnik?.id
        if (trenutniId == null) {
            Toast.makeText(requireContext(), getString(R.string.greska_niste_prijavljeni), Toast.LENGTH_LONG).show()
            return
        }

        if (trenutniId != korisnikIdVlasnika) {
            Toast.makeText(requireContext(), getString(R.string.nemate_pravo_uredivanja), Toast.LENGTH_LONG).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                postaviFormuOmogucenu(view, false)

                val izmjene = OglasFormHelper.procitajOglasIzForme(view, trenutniId)
                OglasRepository.azurirajOglas(
                    oglasId = oglasId,
                    voziloId = voziloId,
                    korisnikId = trenutniId,
                    izmjene = izmjene
                )

                Toast.makeText(requireContext(), getString(R.string.oglas_azuriran), Toast.LENGTH_LONG).show()

                setFragmentResult(REQUEST_KEY, Bundle.EMPTY)
                parentFragmentManager.popBackStack()

            } catch (e: Exception) {
                postaviFormuOmogucenu(view, true)
                Toast.makeText(requireContext(), getString(R.string.greska_format, e.message), Toast.LENGTH_LONG).show()
            }
        }
    }

    companion object {
        const val REQUEST_KEY = "urediOglasResult"
        private const val ARG_OGLAS_ID = "oglasId"

        fun newInstance(oglasId: Int): UrediOglasFragment {
            val f = UrediOglasFragment()
            f.arguments = Bundle().apply {
                putInt(ARG_OGLAS_ID, oglasId)
            }
            return f
        }
    }
}
