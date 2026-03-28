package com.example.auto_moto_oglasnik.fragmenti

import android.widget.Toast
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.auto_moto_oglasnik.PrikazSvihVozilaActivity
import com.example.auto_moto_oglasnik.R
import com.example.auto_moto_oglasnik.adapteri.OglasAdapter
import com.example.auto_moto_oglasnik.pomagaci.OglasRepository
import com.example.auto_moto_oglasnik.pomagaci.Sesija
import kotlinx.coroutines.launch

class MojiOglasiFragment : Fragment(R.layout.activity_moji_oglasi) {

    private lateinit var rvMojiOglasi: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvMojiOglasi = view.findViewById(R.id.rvMojiOglasi)
        rvMojiOglasi.layoutManager = LinearLayoutManager(requireContext())

        ucitajMojeOglase()
    }

    private fun ucitajMojeOglase() {
        val korisnikId = Sesija.trenutniKorisnik?.id
        if (korisnikId == null) {
            Toast.makeText(requireContext(), "Niste prijavljeni!", Toast.LENGTH_LONG).show()
            requireActivity().onBackPressedDispatcher.onBackPressed()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val oglasi = OglasRepository.dohvatiMojeOglaseZaListu(
                    korisnikId = korisnikId,
                    packageName = requireContext().packageName
                )

                rvMojiOglasi.adapter = OglasAdapter(oglasi) { odabrani ->
                    (requireActivity() as PrikazSvihVozilaActivity)
                        .otvoriDetalje(odabrani.oglasId)
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Greška: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
