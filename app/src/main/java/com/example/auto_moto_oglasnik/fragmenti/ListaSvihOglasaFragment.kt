package com.example.auto_moto_oglasnik.fragmenti

import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.auto_moto_oglasnik.PrikazSvihVozilaActivity
import com.example.auto_moto_oglasnik.UsporedbaActivity
import com.example.auto_moto_oglasnik.R
import com.example.auto_moto_oglasnik.adapteri.OglasAdapter
import com.example.auto_moto_oglasnik.entiteti.OglasZaListu
import com.example.auto_moto_oglasnik.pomagaci.OglasRepository
import kotlinx.coroutines.launch

class ListaSvihOglasaFragment : Fragment(R.layout.fragment_lista_oglasa) {

    private lateinit var rvVozila: RecyclerView
    private lateinit var searchView: androidx.appcompat.widget.SearchView

    private var adapter: OglasAdapter? = null
    private var listaSviOglasi: List<OglasZaListu> = emptyList()
    private var trenutnaLista: List<OglasZaListu> = emptyList()

    private var samoIspod100k = false
    private var samoIspod50k = false
    private var samoIznad2000 = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvVozila = view.findViewById(R.id.rvVozila)
        rvVozila.layoutManager = LinearLayoutManager(requireContext())
        searchView = view.findViewById(R.id.searchViewVozila)

        postaviLogikuPretrage()
        postaviLogikuSortiranja(view)
        postaviLogikuFiltera(view)
        ucitajOglase()
    }

    private fun postaviLogikuFiltera(view: View) {
        val layoutNaslov = view.findViewById<LinearLayout>(R.id.layoutNaslovFiltera)
        val layoutPanel = view.findViewById<LinearLayout>(R.id.layoutSviFilteri)
        val strelica = view.findViewById<ImageView>(R.id.imgStrelica)

        val spinnerKM = view.findViewById<Spinner>(R.id.spinnerKilometraza)
        val spinnerCijena = view.findViewById<Spinner>(R.id.spinnerCijena)
        val spinnerGodiste = view.findViewById<Spinner>(R.id.spinnerGodiste)

        layoutNaslov.setOnClickListener {
            if (layoutPanel.visibility == View.GONE) {
                layoutPanel.visibility = View.VISIBLE
                strelica.rotation = 180f
            } else {
                layoutPanel.visibility = View.GONE
                strelica.rotation = 0f
            }
        }

        val filterListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                samoIspod100k = spinnerKM.selectedItemPosition == 1
                samoIspod50k = spinnerCijena.selectedItemPosition == 1
                samoIznad2000 = spinnerGodiste.selectedItemPosition == 1

                primijeniSveFiltere()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spinnerKM.onItemSelectedListener = filterListener
        spinnerCijena.onItemSelectedListener = filterListener
        spinnerGodiste.onItemSelectedListener = filterListener
    }

    private fun primijeniSveFiltere() {
        val upit = searchView.query.toString()

        val filtrirana = listaSviOglasi.filter { oglas ->
            val kmOK = !samoIspod100k || oglas.kilometri < 100000
            val cijenaOK = !samoIspod50k || oglas.cijena < 50000.00
            val godinaOK = !samoIznad2000 || oglas.godinaProizvodnje > 2000
            val searchOK = upit.isEmpty() || oglas.naslov.contains(upit, ignoreCase = true)

            kmOK && cijenaOK && godinaOK && searchOK
        }

        trenutnaLista = filtrirana
        adapter?.updateList(filtrirana)
    }

    private fun postaviLogikuPretrage() {
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                primijeniSveFiltere()
                return true
            }
        })
    }

    private fun postaviLogikuSortiranja(view: View) {
        view.findViewById<Button>(R.id.btnSortCijenamin).setOnClickListener {
            primijeniSortiranje(false) { it.cijena }
        }
        view.findViewById<Button>(R.id.btnSortCijenamax).setOnClickListener {
            primijeniSortiranje(true) { it.cijena }
        }
        view.findViewById<Button>(R.id.btnSortGodina).setOnClickListener {
            primijeniSortiranje(true) { it.godinaProizvodnje.toDouble() }
        }
        view.findViewById<Button>(R.id.btnSortKM).setOnClickListener {
            primijeniSortiranje(false) { it.kilometri.toDouble() }
        }
        view.findViewById<Button>(R.id.btnSortKm).setOnClickListener {
            primijeniSortiranje(true) { it.kilometri.toDouble() }
        }
    }

    private fun primijeniSortiranje(opadajuce: Boolean, kriterij: (OglasZaListu) -> Double) {
        val sortirana = if (opadajuce) {
            trenutnaLista.sortedByDescending(kriterij)
        } else {
            trenutnaLista.sortedBy(kriterij)
        }
        trenutnaLista = sortirana
        adapter?.updateList(sortirana)
    }

    private fun ucitajOglase() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val oglasi = OglasRepository.dohvatiSveOglaseZaListu(requireContext().packageName)
                listaSviOglasi = oglasi
                primijeniSveFiltere()
                if (adapter == null) {
                    adapter = OglasAdapter(trenutnaLista) { odabrani ->
                        (requireActivity() as PrikazSvihVozilaActivity).otvoriDetalje(odabrani.oglasId)
                    }
                    val btnUsporedi = view?.findViewById<Button>(R.id.btnPokreniUsporedbu)
                    adapter?.onUsporedbaChanged = { listaIdova ->
                        if (listaIdova.size == 2) {
                            btnUsporedi?.visibility = View.VISIBLE
                            btnUsporedi?.setOnClickListener {
                                val intent = android.content.Intent(requireContext(), UsporedbaActivity::class.java)
                                intent.putExtra("ID1", listaIdova[0])
                                intent.putExtra("ID2", listaIdova[1])
                                startActivity(intent)
                            }
                        } else {
                            btnUsporedi?.visibility = View.GONE
                        }
                    }

                    rvVozila.adapter = adapter
                } else {
                    adapter?.updateList(trenutnaLista)
                }

            } catch (e: Exception) {
                Log.e("Oglasi", "Greška:", e)
                Toast.makeText(requireContext(), "Greška: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        ucitajOglase()
    }
}