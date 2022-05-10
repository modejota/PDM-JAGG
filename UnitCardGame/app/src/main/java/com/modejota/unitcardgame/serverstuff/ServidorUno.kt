package com.modejota.unitcardgame.serverstuff

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import java.net.ServerSocket
import java.net.Socket

class ServidorUno(
    private var numeroJugadores: Int,
    private var context: Context
): Thread() {

    private val sockets = ArrayList<Socket>()
    private val servidores = ArrayList<ThreadServidor>()
    private var servidor: ServerSocket? = null
    // val partida: Partida
    // En principio, tiene que guardar una referencia a la interfaz para poder actualizarla


    override fun run() {
        try {
            servidor = ServerSocket(9029)

            // Asi es como se puede hacer un Toast desde una hebra, simplemente necesita un handler y un contexto
            val handler = Handler(Looper.getMainLooper())
            handler.post{
                Toast.makeText(context, "Esperando jugadores", Toast.LENGTH_SHORT).show()
            }

            // Para cada jugador, crear un socket y añadirlo al array de sockets.
            for (jugador in 0..numeroJugadores) {
                sockets.add(servidor!!.accept())    // Esto es bloqueante, asi que por mucho que este en una hebra, esperamos.
                val usuario = ThreadServidor(sockets[sockets.size-1], this, jugador, numeroJugadores-1)
                servidores.add(usuario)
                servidores[servidores.size-1].start()
            }
            // Establecer conexiones entre jugadores
            try {
                sleep(1000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}