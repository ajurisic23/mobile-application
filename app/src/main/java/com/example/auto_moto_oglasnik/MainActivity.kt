package com.example.auto_moto_oglasnik

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)


        val prijava= findViewById<Button>(R.id.btn_prijava)
        val registracija=findViewById<Button>(R.id.btn_reg)
        val gost=findViewById<TextView>(R.id.txt_kaoGost)

        gost.setOnClickListener {

            val intent= Intent(this, PrikazSvihVozilaActivity::class.java)
            startActivity(intent)
        }

        prijava.setOnClickListener {

            val intent = Intent(this, PrijavaActivity::class.java)
                startActivity(intent)
        }

        registracija.setOnClickListener {

            val intent = Intent(this, RegistracijaActivity::class.java)
            startActivity(intent)

        }

    }
}