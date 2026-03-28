package com.example.auto_moto_oglasnik.pomagaci

import com.example.auto_moto_oglasnik.R
import com.example.auto_moto_oglasnik.entiteti.NoviOglas
import com.example.auto_moto_oglasnik.entiteti.OglasZaListu
import com.example.auto_moto_oglasnik.entiteti.VoziloDetalji
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.Statement

object OglasRepository {

    data class UredivanjePodaci(
        val voziloId: Int,
        val detalji: VoziloDetalji
    )

    private suspend fun <T> sKonekcijom(
        transakcija: Boolean = false,
        blok: (Connection) -> T
    ): T {
        return withContext(Dispatchers.IO) {
            var connection: Connection? = null
            try {
                connection = SQLConnection.connect() ?: throw Exception("Konekcija na bazu nije uspjela.")
                if (transakcija) connection.autoCommit = false

                val rezultat = blok(connection)

                if (transakcija) connection.commit()
                rezultat
            } catch (e: Exception) {
                if (transakcija) {
                    try { connection?.rollback() } catch (_: Exception) {}
                }
                throw e
            } finally {
                if (transakcija) {
                    try { connection?.autoCommit = true } catch (_: Exception) {}
                }
                SQLConnection.close(connection)
            }
        }
    }

    private fun putanjaPrveSlikeZaListu(packageName: String, oglasId: Int): String {
        return when (oglasId) {
            1 -> "android.resource://$packageName/${R.drawable.bmw2}"
            else -> "android.resource://$packageName/${R.drawable.ic_launcher_foreground}"
        }
    }

    suspend fun dohvatiSveOglaseZaListu(packageName: String): List<OglasZaListu> {
        return sKonekcijom { connection ->
            val sql = """
                SELECT 
                    o.oglas_id,
                    o.naslov,
                    o.cijena,
                    v.godina_proizvodnje,
                    v.kilometri
                FROM dbo.Oglas o
                INNER JOIN dbo.Vozilo v ON v.vozilo_id = o.vozilo_id
                ORDER BY o.datum_objave DESC,
                         o.oglas_id DESC
            """.trimIndent()

            val lista = mutableListOf<OglasZaListu>()

            connection.createStatement().use { st ->
                st.executeQuery(sql).use { rs ->
                    while (rs.next()) {
                        val oglasId = rs.getInt("oglas_id")
                        lista.add(
                            OglasZaListu(
                                oglasId = oglasId,
                                naslov = rs.getString("naslov") ?: "Oglas bez naslova",
                                cijena = rs.getDouble("cijena"),
                                godinaProizvodnje = rs.getInt("godina_proizvodnje"),
                                kilometri = rs.getInt("kilometri"),
                                putanjaPrveSlike = putanjaPrveSlikeZaListu(packageName, oglasId)
                            )
                        )
                    }
                }
            }

            lista
        }
    }

    suspend fun dohvatiMojeOglaseZaListu(korisnikId: Int, packageName: String): List<OglasZaListu> {
        return sKonekcijom { connection ->
            val sql = """
                SELECT 
                    o.oglas_id,
                    o.naslov,
                    o.cijena,
                    v.godina_proizvodnje,
                    v.kilometri
                FROM dbo.Oglas o
                INNER JOIN dbo.Vozilo v ON v.vozilo_id = o.vozilo_id
                WHERE o.korisnik_id = ?
                ORDER BY o.datum_objave DESC, o.oglas_id DESC
            """.trimIndent()

            val lista = mutableListOf<OglasZaListu>()

            connection.prepareStatement(sql).use { ps ->
                ps.setInt(1, korisnikId)
                ps.executeQuery().use { rs ->
                    while (rs.next()) {
                        val oglasId = rs.getInt("oglas_id")
                        lista.add(
                            OglasZaListu(
                                oglasId = oglasId,
                                naslov = rs.getString("naslov") ?: "Oglas bez naslova",
                                cijena = rs.getDouble("cijena"),
                                godinaProizvodnje = rs.getInt("godina_proizvodnje"),
                                kilometri = rs.getInt("kilometri"),
                                putanjaPrveSlike = putanjaPrveSlikeZaListu(packageName, oglasId)
                            )
                        )
                    }
                }
            }

            lista
        }
    }

    suspend fun dohvatiDetaljeOglasa(oglasId: Int): VoziloDetalji {
        return sKonekcijom { connection ->
            val sql = """
                SELECT 
                    o.naslov, o.opis, o.cijena, o.status, o.datum_objave, o.lokacija,
                    o.korisnik_id,
                    k.korisnicko_ime,
                    v.marka, v.model, v.godina_proizvodnje, v.kilometri,
                    v.tip_goriva, v.snaga_motora, v.broj_brzina, v.tip_mjenjaca
                FROM dbo.Oglas o
                INNER JOIN dbo.Vozilo v ON v.vozilo_id = o.vozilo_id
                INNER JOIN dbo.Korisnik k ON k.korisnik_id = o.korisnik_id
                WHERE o.oglas_id = ?
            """.trimIndent()

            connection.prepareStatement(sql).use { ps ->
                ps.setInt(1, oglasId)
                ps.executeQuery().use { rs ->
                    if (!rs.next()) throw Exception("Oglas nije pronađen.")

                    VoziloDetalji(
                        naslov = rs.getString("naslov") ?: "",
                        opis = rs.getString("opis"),
                        cijena = rs.getDouble("cijena"),
                        status = rs.getString("status") ?: "",
                        datumObjave = rs.getTimestamp("datum_objave")?.toString() ?: "",
                        lokacija = rs.getString("lokacija") ?: "",
                        korisnickoIme = rs.getString("korisnicko_ime") ?: "",
                        korisnikId = rs.getInt("korisnik_id"),

                        marka = rs.getString("marka") ?: "",
                        model = rs.getString("model") ?: "",
                        godinaProizvodnje = rs.getInt("godina_proizvodnje"),
                        kilometri = rs.getInt("kilometri"),
                        tipGoriva = rs.getString("tip_goriva") ?: "",
                        snagaMotora = rs.getInt("snaga_motora"),
                        brojBrzina = rs.getInt("broj_brzina"),
                        tipMjenjaca = rs.getString("tip_mjenjaca") ?: "",

                        putanjeSlika = emptyList()
                    )
                }
            }
        }
    }

    suspend fun kreirajOglas(oglas: NoviOglas) {
        sKonekcijom(transakcija = true) { connection ->
            val voziloId = connection.prepareStatement(
                """
                    INSERT INTO dbo.Vozilo
                    (marka, model, godina_proizvodnje, kilometri, tip_goriva, snaga_motora, broj_brzina, tip_mjenjaca)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """.trimIndent(),
                Statement.RETURN_GENERATED_KEYS
            ).use { ps ->
                ps.setString(1, oglas.marka)
                ps.setString(2, oglas.model)
                ps.setInt(3, oglas.godinaProizvodnje)
                ps.setInt(4, oglas.kilometri)
                ps.setString(5, oglas.tipGoriva)
                ps.setInt(6, oglas.snagaMotora)
                ps.setInt(7, oglas.brojBrzina)
                ps.setString(8, oglas.tipMjenjaca)
                ps.executeUpdate()

                ps.generatedKeys.use {
                    if (it.next()) it.getInt(1) else throw Exception("Vozilo ID nije dobiven.")
                }
            }

            connection.prepareStatement(
                """
                    INSERT INTO dbo.Oglas
                    (naslov, opis, cijena, status, datum_objave, lokacija, korisnik_id, vozilo_id)
                    VALUES (?, ?, ?, 'aktivan', GETDATE(), ?, ?, ?)
                """.trimIndent()
            ).use { ps ->
                ps.setString(1, oglas.naslov)
                ps.setString(2, oglas.opis)
                ps.setDouble(3, oglas.cijena)
                ps.setString(4, oglas.lokacija)
                ps.setInt(5, oglas.korisnikId)
                ps.setInt(6, voziloId)
                ps.executeUpdate()
            }
        }
    }

    suspend fun dohvatiZaUredivanje(oglasId: Int): UredivanjePodaci {
        return sKonekcijom { connection ->
            val sql = """
                SELECT
                    o.oglas_id,
                    o.naslov, o.opis, o.cijena, o.status, o.datum_objave, o.lokacija,
                    o.korisnik_id,
                    o.vozilo_id,
                    k.korisnicko_ime,
                    v.marka, v.model, v.godina_proizvodnje, v.kilometri,
                    v.tip_goriva, v.snaga_motora, v.broj_brzina, v.tip_mjenjaca
                FROM dbo.Oglas o
                INNER JOIN dbo.Vozilo v ON v.vozilo_id = o.vozilo_id
                INNER JOIN dbo.Korisnik k ON k.korisnik_id = o.korisnik_id
                WHERE o.oglas_id = ?
            """.trimIndent()

            connection.prepareStatement(sql).use { ps ->
                ps.setInt(1, oglasId)
                ps.executeQuery().use { rs ->
                    if (!rs.next()) throw Exception("Oglas nije pronađen.")

                    val voziloId = rs.getInt("vozilo_id")

                    val detalji = VoziloDetalji(
                        naslov = rs.getString("naslov") ?: "",
                        opis = rs.getString("opis"),
                        cijena = rs.getDouble("cijena"),
                        status = rs.getString("status") ?: "",
                        datumObjave = rs.getTimestamp("datum_objave")?.toString() ?: "",
                        lokacija = rs.getString("lokacija") ?: "",
                        korisnickoIme = rs.getString("korisnicko_ime") ?: "",
                        korisnikId = rs.getInt("korisnik_id"),

                        marka = rs.getString("marka") ?: "",
                        model = rs.getString("model") ?: "",
                        godinaProizvodnje = rs.getInt("godina_proizvodnje"),
                        kilometri = rs.getInt("kilometri"),
                        tipGoriva = rs.getString("tip_goriva") ?: "",
                        snagaMotora = rs.getInt("snaga_motora"),
                        brojBrzina = rs.getInt("broj_brzina"),
                        tipMjenjaca = rs.getString("tip_mjenjaca") ?: "",

                        putanjeSlika = emptyList()
                    )

                    UredivanjePodaci(voziloId = voziloId, detalji = detalji)
                }
            }
        }
    }

    suspend fun azurirajOglas(
        oglasId: Int,
        voziloId: Int,
        korisnikId: Int,
        izmjene: NoviOglas
    ) {
        sKonekcijom(transakcija = true) { connection ->
            connection.prepareStatement(
                """
                    UPDATE dbo.Vozilo
                    SET marka = ?, model = ?, godina_proizvodnje = ?, kilometri = ?,
                        tip_goriva = ?, snaga_motora = ?, broj_brzina = ?, tip_mjenjaca = ?
                    WHERE vozilo_id = ?
                """.trimIndent()
            ).use { ps ->
                ps.setString(1, izmjene.marka)
                ps.setString(2, izmjene.model)
                ps.setInt(3, izmjene.godinaProizvodnje)
                ps.setInt(4, izmjene.kilometri)
                ps.setString(5, izmjene.tipGoriva)
                ps.setInt(6, izmjene.snagaMotora)
                ps.setInt(7, izmjene.brojBrzina)
                ps.setString(8, izmjene.tipMjenjaca)
                ps.setInt(9, voziloId)
                ps.executeUpdate()
            }

            connection.prepareStatement(
                """
                    UPDATE dbo.Oglas
                    SET naslov = ?, opis = ?, cijena = ?, lokacija = ?
                    WHERE oglas_id = ? AND korisnik_id = ?
                """.trimIndent()
            ).use { ps ->
                ps.setString(1, izmjene.naslov)
                ps.setString(2, izmjene.opis)
                ps.setDouble(3, izmjene.cijena)
                ps.setString(4, izmjene.lokacija)
                ps.setInt(5, oglasId)
                ps.setInt(6, korisnikId)

                val azurirano = ps.executeUpdate()
                if (azurirano == 0) throw Exception("Nemate pravo uređivanja.")
            }
        }
    }

    suspend fun obrisiOglas(oglasId: Int, korisnikId: Int) {
        sKonekcijom(transakcija = true) { connection ->
            val voziloId = connection.prepareStatement(
                "SELECT vozilo_id FROM dbo.Oglas WHERE oglas_id = ? AND korisnik_id = ?"
            ).use { ps ->
                ps.setInt(1, oglasId)
                ps.setInt(2, korisnikId)
                ps.executeQuery().use { rs ->
                    if (rs.next()) rs.getInt("vozilo_id")
                    else throw Exception("Nemate pravo brisanja ili oglas ne postoji.")
                }
            }

            connection.prepareStatement(
                "DELETE FROM dbo.Komentar_Recenzija WHERE oglas_id = ?"
            ).use { ps ->
                ps.setInt(1, oglasId)
                ps.executeUpdate()
            }

            connection.prepareStatement("DELETE FROM dbo.Oglas WHERE oglas_id = ?").use { ps ->
                ps.setInt(1, oglasId)
                val obrisano = ps.executeUpdate()
                if (obrisano == 0) throw Exception("Oglas nije obrisan.")
            }

            connection.prepareStatement("DELETE FROM dbo.Vozilo WHERE vozilo_id = ?").use { ps ->
                ps.setInt(1, voziloId)
                ps.executeUpdate()
            }
        }
    }
}
