package com.modejota.unitcardgame.serverstuff

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.ObjectInputStream
import java.net.Socket

// Por ahora, todo public var, ya veré que puede ser privado y que no.

class ThreadServidor(
    private var cliente: Socket,
    var servidor: ServidorUno,
    var playerID: Int,
    var numeroOponentes: Int,
): Thread() {

    private var entrada: DataInputStream? = null
    private var salida: DataOutputStream? = null
    private var servidoresEscucha: ArrayList<ThreadServidor>? = null

    override fun run() {
        try {
            entrada = DataInputStream(cliente.getInputStream())
            salida = DataOutputStream(cliente.getOutputStream())
            // val recibeObjeto = ObjectInputStream(entrada)
            // jugador = recibeObjeto.readObject() as Jugador
            // servidor.getPartida().addJugador(jugador)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        var opcion = 0
        while (true) {
            // Parsear el contenido del mensaje que se recibe del cliente
        }

    }

    // Aquí faltan muchísimos métodos sobre como actualizar los datos.
}