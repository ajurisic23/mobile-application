package com.example.auto_moto_oglasnik.entiteti

data class VoziloDetalji(
    // Oglas
    val naslov: String,
    val opis: String?,
    val cijena: Double,
    val status: String,
    val datumObjave: String,
    val lokacija: String,
    val korisnickoIme: String,
    val korisnikId: Int,

    // Vozilo
    val marka: String,
    val model: String,
    val godinaProizvodnje: Int,
    val kilometri: Int,
    val tipGoriva: String,
    val snagaMotora: Int,
    val brojBrzina: Int,
    val tipMjenjaca: String,

    // Slike
    val putanjeSlika: List<String>
)
