package com.example.auto_moto_oglasnik.entiteti

data class NoviOglas(
    val naslov: String,
    val opis: String?,
    val cijena: Double,
    val lokacija: String,
    var korisnikId: Int,

    val marka: String,
    val model: String,
    val godinaProizvodnje: Int,
    val kilometri: Int,
    val tipGoriva: String,
    val snagaMotora: Int,
    val brojBrzina: Int,
    val tipMjenjaca: String,

    val putanjeSlika: List<String> = emptyList()
)
