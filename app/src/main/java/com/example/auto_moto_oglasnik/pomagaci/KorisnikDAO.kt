package com.example.auto_moto_oglasnik.pomagaci
import android.util.Log
import com.example.auto_moto_oglasnik.entiteti.Korisnik
import java.sql.Connection
 object KorisnikDAO {
    private const val izvor = "KorisnikDAO"
    suspend fun registrirajKorisnika(korisnik: Korisnik): Boolean {
        var connection: Connection? = null
        val sql = "INSERT INTO dbo.Korisnik (ime, prezime, korisnicko_ime, email, lozinka,datum_registracije, tip_korisnika_id) VALUES (?, ?, ?, ?, ?, GETDATE(),?)"
        try {
            connection = SQLConnection.connect()
            if (connection == null) {
                Log.e(izvor, "Veza s bazom nije uspostavljena.")
                return false
            }
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, korisnik.ime)
                statement.setString(2, korisnik.prezime)
                statement.setString(3, korisnik.korime)
                statement.setString(4, korisnik.email)
                statement.setString(5, korisnik.lozinka)
                statement.setInt(6, korisnik.tipKorisnikaId)

                val rowsAffected = statement.executeUpdate()
                return rowsAffected > 0
            }
        } catch (e: Exception) {
            Log.e(izvor, "Greška pri registraciji korisnika: ${e.message}", e)
            return false
        } finally {
            SQLConnection.close(connection)
        }
    }

    suspend fun provjeriPostojanjeEmaila(email: String): Boolean {
        var connection: Connection? = null
        val sql = "SELECT COUNT(*) FROM dbo.Korisnik WHERE email = ?"
        try {
            connection = SQLConnection.connect()

            if (connection == null) return false


            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, email)
                val resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0
                } else {
                    return false
                }
            }
        } catch (e: Exception) {
            Log.e(izvor, "Greška pri provjeri emaila: ${e.message}", e)
            return false
        } finally {
            SQLConnection.close(connection)
        }
    }

    suspend fun provjeriPostojanjeKorimena(korime: String): Boolean {
        var connection: Connection? = null
        val sql = "SELECT COUNT(*) FROM dbo.Korisnik WHERE korisnicko_ime = ?"
        try {
            connection = SQLConnection.connect()
            if (connection == null) return false
            connection.prepareStatement(sql).use { statement ->
                statement.setString(1, korime)
                val resultSet = statement.executeQuery()

                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0
                } else {
                    return false
                }
            }
        } catch (e: Exception) {
            Log.e(izvor, "Greška pri provjeri korisničkog imena: ${e.message}", e)
            return false
        } finally {
            SQLConnection.close(connection)
        }
    }

     suspend fun prijavaKorisnika(korime: String, lozinka: String): Korisnik? {
         var connection: Connection? = null
         val sql = "SELECT * FROM dbo.Korisnik WHERE korisnicko_ime = ? AND lozinka = ?"

         try {
             connection = SQLConnection.connect()
             if (connection == null) return null

             val statement = connection.prepareStatement(sql)

             statement.setString(1, korime)
             statement.setString(2, lozinka)
             val resultSet = statement.executeQuery()
             var pronadjeniKorisnik: Korisnik? = null
             if (resultSet.next()) {
                 pronadjeniKorisnik = Korisnik()

                 pronadjeniKorisnik.id = resultSet.getInt("korisnik_id")
                 pronadjeniKorisnik.ime = resultSet.getString("ime")
                 pronadjeniKorisnik.prezime = resultSet.getString("prezime")
                 pronadjeniKorisnik.korime = resultSet.getString("korisnicko_ime")
                 pronadjeniKorisnik.email = resultSet.getString("email")
                 pronadjeniKorisnik.lozinka = resultSet.getString("lozinka")
                 pronadjeniKorisnik.datum=resultSet.getDate("datum_registracije")
                 pronadjeniKorisnik.tipKorisnikaId = resultSet.getInt("tip_korisnika_id")

             }
             statement.close()
             return pronadjeniKorisnik

         } catch (e: Exception) {
             Log.e(izvor, "Greška pri prijavi: ${e.message}", e)
             return null
         } finally {
             SQLConnection.close(connection)
         }
     }
}