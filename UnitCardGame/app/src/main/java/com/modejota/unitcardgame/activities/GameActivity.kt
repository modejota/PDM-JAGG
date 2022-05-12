package com.modejota.unitcardgame.activities

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.modejota.unitcardgame.databinding.ActivityGameBinding
import com.modejota.unitcardgame.model.Card
import com.modejota.unitcardgame.model.CardType
import com.modejota.unitcardgame.otherstuff.CardAdapter
import java.io.DataInputStream
import java.io.DataOutputStream
import java.lang.Thread.sleep
import java.net.ServerSocket
import java.net.Socket


class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Temporal, las conexiones deben realizarse en hebra secundaria.
        val policy = ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        val ip = intent.getStringExtra("IP")
        Toast.makeText(this, "IP RECIBIDA: $ip", Toast.LENGTH_LONG).show()
        val amIServer = intent.getBooleanExtra("AM_I_SERVER", false)
        if (amIServer) {
            val serverSocket = ServerSocket(9029)
            val socket = serverSocket.accept()
            sleep(2000)
            val din = DataInputStream(socket.getInputStream())
            val message = din.readUTF()
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()

        } else {
            // Aqui habría que poner un cartelito diciendo "Espere a que se conecte el servidor"
            val socket = Socket(ip, 9029)
            val dout = DataOutputStream(socket.getOutputStream())
            dout.writeUTF("HOLA ME HE ENVIADO ALGO")
            dout.flush()
            Toast.makeText(this, "CLIENTE CONECTADO", Toast.LENGTH_LONG).show()
        }

        binding.rvMyCards.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Arraylist de prueba, ya veré como consigo esto a partir del servidor
        val mazo = ArrayList<Card>()
        for (i in 0..2) {
            mazo.add(Card(i % 2, "BLUE", CardType.NUMBER))
            mazo.add(Card(i % 2, "GREEN", CardType.NUMBER))
            mazo.add(Card(i % 2, "RED", CardType.NUMBER))
            mazo.add(Card(i % 2, "YELLOW", CardType.NUMBER))
        }

        binding.rvMyCards.adapter = CardAdapter(mazo)

    }
}