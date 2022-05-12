package com.modejota.unitcardgame.activities

import android.os.Bundle
import android.provider.Telephony
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.modejota.unitcardgame.databinding.ActivityGameBinding
import com.modejota.unitcardgame.clientstuff.Client
import com.modejota.unitcardgame.model.Card
import com.modejota.unitcardgame.model.Partida
import com.modejota.unitcardgame.otherstuff.CardAdapter
import com.modejota.unitcardgame.serverstuff.Server


class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private var client: Client?= null
    private var server: Server?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Temporal, las conexiones deben realizarse en hebra secundaria.
        //val policy = ThreadPolicy.Builder().permitAll().build()
        //StrictMode.setThreadPolicy(policy)

        val ip = intent.getStringExtra("IP")
        Toast.makeText(this, "IP RECIBIDA: $ip", Toast.LENGTH_LONG).show()
        val amIServer = intent.getBooleanExtra("AM_I_SERVER", false)
        if (amIServer) {

            // Create a thread to run the server on.
            val serverThread = Thread {
                server = Server(2)
                server!!.startServer()
            }

            Toast.makeText(this, "SERVIDOR", Toast.LENGTH_LONG).show()
            // Una dificultad que me estoy viendo venir, es sincronizar la interfaz con esto.
            // Los sockets no me generar un callback con el que pueda actualizar la interfaz.
        }
        // Aqui habría que poner un cartelito diciendo "Espere a que se conecte el servidor"
        // Create a thread to run the client on. (Se supone que el servidor también es cliente)
        val clientThread = Thread {
            client = Client(ip!!)
        }



        //Toast.makeText(this, client?.getCartas()?.size ?: 0, Toast.LENGTH_LONG).show()
        Toast.makeText(this, "CLIENTE CONECTADO", Toast.LENGTH_LONG).show()

        binding.rvMyCards.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvMyCards.adapter = CardAdapter(Partida().getInitCards())

        // Arraylist de prueba, ya veré como consigo esto a partir del servidor
        /*
        val mazo = ArrayList<Card>()
        for (i in 0..2) {
            mazo.add(Card(i % 2, "BLUE", CardType.NUMBER))
            mazo.add(Card(i % 2, "GREEN", CardType.NUMBER))
            mazo.add(Card(i % 2, "RED", CardType.NUMBER))
            mazo.add(Card(i % 2, "YELLOW", CardType.NUMBER))
        }

        binding.rvMyCards.adapter = CardAdapter(mazo)

         */
    }
}