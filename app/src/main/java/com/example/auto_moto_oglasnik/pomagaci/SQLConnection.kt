package com.example.auto_moto_oglasnik.pomagaci

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager

object SQLConnection {
    private const val IP = "31.147.206.65"
    private const val PORT = "1433"
    private const val DATABASE = "RPP2025_06_DB"
    private const val USERNAME = "RPP2025_06_User"
    private const val PASSWORD = "@#u8m-7HTnavo|V["
    private const val DRIVER = "net.sourceforge.jtds.jdbc.Driver"

    suspend fun connect(): Connection? = withContext(Dispatchers.IO) {

        try {
            Class.forName(DRIVER)
            val url = "jdbc:jtds:sqlserver://$IP:$PORT/$DATABASE"
            val connection = DriverManager.getConnection(url, USERNAME, PASSWORD)
            Log.d("SQLConnection", "Uspješno spojeno na bazu!")
            connection
        } catch (e: Exception) {
            Log.e("SQLConnection", "Greška pri spajanju: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    suspend fun close(connection: Connection?) = withContext(Dispatchers.IO) {
        try {
            connection?.close()
            Log.d("SQLConnection", "Konekcija zatvorena.")
        } catch (e: Exception) {
            Log.e("SQLConnection", "Greška pri zatvaranju konekcije: ${e.message}")
        }
    }
}
