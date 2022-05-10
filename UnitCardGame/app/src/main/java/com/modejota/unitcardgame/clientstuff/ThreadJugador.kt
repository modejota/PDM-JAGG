package com.modejota.unitcardgame.clientstuff

import java.io.DataInputStream
import java.io.ObjectInputStream
import java.net.Socket

class ThreadJugador(
    private val jugador: Jugador,
    private val cliente: Socket,
    private val entrada: DataInputStream
): Thread() {

    override fun run() {
        while (true) {
            try {
                when (entrada.readInt()) {
                    1 -> {
                        // Soy quien juega
                        var recibeObjeto = ObjectInputStream(cliente.getInputStream())
                        // Actualizar mis cartas y actualizar carta actual jugada
                    }
                    2 -> {
                        // Mostrar mensaje para elegir color del comodin
                    }
                    // Puede que haya alguna que otra opcion
                }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
    }

}