data class Komentar(
    val id: Int,
    val korisnikId: Int,
    val korisnickoIme: String,
    val tekst: String,
    val ocjena: Int?,
    val datumKreiranja: String
)
        