package com.modejota.unitcardgame.serverstuff

import android.util.Log
import com.modejota.unitcardgame.model.Partida
import java.io.ObjectInputStream
import java.io.ObjectOutput
import java.io.ObjectOutputStream
import java.net.ServerSocket
import java.net.Socket

class Server(
    private val numeroJugadores: Int
) {

    private var server: ServerSocket? = null
    //private var din: DataInputStream? = null
    //private var dout: DataOutputStream? = null
    private val clients = ArrayList<Socket>()
    private val partida = Partida()

    fun startServer() {
        try {
            for (i in 0..numeroJugadores) {
                server = ServerSocket(9029)
                val client = server!!.accept()
                clients.add(client)
            }
        } catch (e: Exception) {
            Log.d("Server", "Initialization error: " + e.message)
        }
        try {
            for (index in 0..numeroJugadores) {
                val initCartas = partida.getInitCards()
                val cl = clients[index]
                val objectStream = ObjectOutputStream(cl.getOutputStream())
                objectStream.writeObject(initCartas)
            }
        } catch (e: Exception) {
            Log.d("Server", "Initialization error: " + e.message)
        }
    }


}