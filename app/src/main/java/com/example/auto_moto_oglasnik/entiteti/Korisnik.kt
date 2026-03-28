package com.example.auto_moto_oglasnik.entiteti

import java.util.Date

data class Korisnik(
    var id:      Int?=null,
    var ime:     String="",
    var prezime: String="",
    var korime:  String="",
    var email:   String="",
    var lozinka: String="",
    var datum: Date?=null,
    var tipKorisnikaId: Int=2
    )

