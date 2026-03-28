package com.example.auto_moto_oglasnik.entiteti

data class OglasZaListu(
    val oglasId: Int,
    val naslov: String,
    val cijena: Double,
    val godinaProizvodnje: Int,
    val kilometri: Int,
    val putanjaPrveSlike: String?
)