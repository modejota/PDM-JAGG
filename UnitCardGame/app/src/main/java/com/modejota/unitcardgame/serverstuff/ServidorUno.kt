package com.modejota.unitcardgame.serverstuff

import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.modejota.unitcardgame.MainActivity
import java.net.ServerSocket
import java.net.Socket

class ServidorUno(
    private var numeroJugadores: Int
): Thread() {

    private val sockets = ArrayList<Socket>()
    private val servidores = ArrayList<ThreadServidor>()
    var servidor: ServerSocket? = null
    // val partida: Partida
    // En principio, tiene que guardar una referencia a la interfaz para poder actualizarla


    override fun run() {
        try {
            servidor = ServerSocket(9029)
            // Para cada jugador, crear un socket y añadirlo al array de sockets
            for (jugador in 0..numeroJugadores) {
                sockets.add(servidor!!.accept())
                val usuario = ThreadServidor(sockets[sockets.size-1], this, jugador, numeroJugadores-1)
                servidores.add(usuario)
                servidores[servidores.size-1].start()
            }
            // Establecer conexiones entre jugadores
            try {
                Thread.sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}