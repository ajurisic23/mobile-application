package com.example.auto_moto_oglasnik.fragmenti

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.auto_moto_oglasnik.R
import com.example.auto_moto_oglasnik.pomagaci.OglasFormHelper
import com.example.auto_moto_oglasnik.pomagaci.OglasRepository
import com.example.auto_moto_oglasnik.pomagaci.Sesija
import kotlinx.coroutines.launch

class NoviOglasFragment : Fragment(R.layout.activity_novi_oglas) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btnSpremiOglas).setOnClickListener {
            spremiOglas(view)
        }
    }

    private fun spremiOglas(view: View) {
        val idKorisnika = Sesija.trenutniKorisnik?.id
        if (idKorisnika == null) {
            Toast.makeText(requireContext(), getString(R.string.greska_niste_prijavljeni), Toast.LENGTH_LONG).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val noviOglas = OglasFormHelper.procitajOglasIzForme(view, idKorisnika)
                OglasRepository.kreirajOglas(noviOglas)

                Toast.makeText(requireContext(), getString(R.string.oglas_spremljen), Toast.LENGTH_LONG).show()
                requireActivity().onBackPressedDispatcher.onBackPressed()

            } catch (e: Exception) {
                Toast.makeText(requireContext(), getString(R.string.greska_format, e.message), Toast.LENGTH_LONG).show()
            }
        }
    }
}
