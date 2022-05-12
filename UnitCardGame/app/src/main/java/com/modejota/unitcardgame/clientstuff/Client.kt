package com.modejota.unitcardgame.clientstuff

import com.modejota.unitcardgame.model.Card
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.ObjectInputStream
import java.net.Socket

class Client(
    private val serverIP: String
): Thread() {

    private val client = Socket(serverIP, 9029)
    private val din: DataInputStream = DataInputStream(client.getInputStream())
    private val dout: DataOutputStream = DataOutputStream(client.getOutputStream())
    private val dobjin: ObjectInputStream = ObjectInputStream(client.getInputStream())

    private var misCartas = ArrayList<Card>()

    override fun run() {
        when (din.readInt()) {
            1 -> {
                misCartas = dobjin.readObject() as ArrayList<Card>
            }
        }
    }


    fun getCartas() = misCartas

}