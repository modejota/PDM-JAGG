package com.modejota.unitcardgame.clientstuff

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.modejota.unitcardgame.model.Card
import com.modejota.unitcardgame.serverstuff.ServidorUno
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

class Jugador(
    private val serverIP : String,
    private val context: Context
) {

    private val cartas = ArrayList<Card>()
    //Puede que necesite una serie de booleanos para controlar cosas
    private var entrada: DataInputStream? = null
    private var salida: DataOutputStream? = null
    private var jugador: Socket? = null

    fun crearPartida(cantidadJugadores: Int) {
        val handler = Handler(Looper.getMainLooper())
        try {
            val servidor = ServidorUno(cantidadJugadores, context)
            servidor.start()

            jugador = Socket(serverIP, 9029)
            entrada = DataInputStream(jugador?.getInputStream())
            salida = DataOutputStream(jugador?.getOutputStream())

            ThreadJugador(this, jugador!!, entrada!!).start()

            handler.post {
                Toast.makeText(context, "Partida creada", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun unirseAPartida() {
        val handler = Handler(Looper.getMainLooper())
        try {
            jugador = Socket(serverIP, 9029)
            entrada = DataInputStream(jugador?.getInputStream())
            salida = DataOutputStream(jugador?.getOutputStream())

            ThreadJugador(this, jugador!!, entrada!!).start()

            handler.post {
                Toast.makeText(context, "Unido a partida", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}