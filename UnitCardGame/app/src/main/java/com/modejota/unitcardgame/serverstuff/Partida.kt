package com.modejota.unitcardgame.serverstuff

import com.modejota.unitcardgame.clientstuff.Jugador
import com.modejota.unitcardgame.model.Card
import com.modejota.unitcardgame.model.CardType
import java.io.Serializable
import kotlin.math.abs

class Partida(
    private val servidorJuego: ServidorUno
): Serializable {

    private var mazo: ArrayList<Card> = ArrayList()
    private var descarte: ArrayList<Card> = ArrayList()
    private var jugadores: ArrayList<Jugador> = ArrayList()
    private var colorEnJuego: String = ""
    private var numeroEnJuego: Int = 0
    private var turnoJugador: Int = 0
    private var reverse: Boolean = false

    init {
        crearMazo()
    }

    private fun crearMazo() {
        // Para cada color, hay 2 cartas con cada uno de los valores de 1 a 9.
        for (i in 0..18) {
            mazo.add(Card(i % 2, "BLUE", CardType.NUMBER))
            mazo.add(Card(i % 2, "GREEN", CardType.NUMBER))
            mazo.add(Card(i % 2, "RED", CardType.NUMBER))
            mazo.add(Card(i % 2, "YELLOW", CardType.NUMBER))
        }
        // Para cada color, hay una carta con el valor 0.
        mazo.add(Card(0, "BLUE", CardType.NUMBER))
        mazo.add(Card(0, "GREEN", CardType.NUMBER))
        mazo.add(Card(0, "RED", CardType.NUMBER))
        mazo.add(Card(0, "YELLOW", CardType.NUMBER))
        // Para cada color:
        // 2 cartas para saltar al siguiente jugador
        // 2 cartas para cambiar el sentido.
        // 2 cartas para que el siguiente jugador robe 2 cartas
        for (i in 0..2) {
            mazo.add(Card(0, "BLUE", CardType.SKIP))
            mazo.add(Card(0, "GREEN", CardType.SKIP))
            mazo.add(Card(0, "RED", CardType.SKIP))
            mazo.add(Card(0, "YELLOW", CardType.SKIP))
            mazo.add(Card(0, "BLUE", CardType.REVERSE))
            mazo.add(Card(0, "GREEN", CardType.REVERSE))
            mazo.add(Card(0, "RED", CardType.REVERSE))
            mazo.add(Card(0, "YELLOW", CardType.REVERSE))
            mazo.add(Card(0, "BLUE", CardType.DRAW2))
            mazo.add(Card(0, "GREEN", CardType.DRAW2))
            mazo.add(Card(0, "RED", CardType.DRAW2))
            mazo.add(Card(0, "YELLOW", CardType.DRAW2))
        }
        // Hay 4 cartas comodín para cambiar de color y otras 4 cartas para robar 4
        for (i in 0..4) {
            mazo.add(Card(0, "RAINBOW", CardType.WILDCARD))
            mazo.add(Card(0, "RAINBOW", CardType.DRAW4))
        }
        // Finalmente, bajaramos el mazo.
        mazo.shuffle()
    }

    private fun voltearMazo() {
        mazo = descarte
        mazo.shuffle()
    }

    fun getTopeDescarte() = descarte.last()

    private fun sacarCartaConNumero(): Card {
        var cont = mazo.size - 1
        while (mazo[cont].type != CardType.NUMBER) {
            cont--
        }
        return mazo.removeAt(cont)
    }

    fun getServidorActual(): ThreadServidor {
        return servidorJuego.getServidores()[turnoJugador]
    }

    fun inicializarPartida() {
        // La primera carta del mazo tiene que ser una carta de numero obligatoriamente.
        descarte.add(sacarCartaConNumero())
        colorEnJuego = descarte[0].color
        numeroEnJuego = descarte[0].number

        for (jugador in jugadores) {
            for (i in 0..7) {
                jugador.aniadirCarta(mazo.removeAt(mazo.lastIndex))
            }
        }
    }

    fun manejarCarta(carta: Card) {
        if (carta.color == colorEnJuego || carta.number == numeroEnJuego || carta.color == "RAINBOW") {
            when (carta.type) {
                CardType.DRAW2 -> {
                    colorEnJuego = carta.color
                    numeroEnJuego = -1
                    enviarCartaAlJugadorSiguiente(2)
                    pasarTurno(2)
                    descarte.add(carta)
                }
                CardType.DRAW4 -> {
                    //colorEnJuego =    //preguntar al usuario que color quiere
                    numeroEnJuego = -1
                    enviarCartaAlJugadorSiguiente(4)
                    pasarTurno(2)
                    descarte.add(carta)
                }
                CardType.WILDCARD -> {
                    //colorEnJuego =    //preguntar al usuario que color quiere
                    numeroEnJuego = -1  // Mirar reglas: puede que deba preguntarse numero
                    pasarTurno()
                    descarte.add(carta)
                }
                CardType.REVERSE -> {
                    colorEnJuego = carta.color
                    numeroEnJuego = -1
                    reverse = !reverse
                    pasarTurno()
                    descarte.add(carta)
                }
                CardType.SKIP -> {
                    colorEnJuego = carta.color
                    numeroEnJuego = -1
                    pasarTurno(2)
                    descarte.add(carta)
                }
                CardType.NUMBER -> {
                    colorEnJuego = carta.color
                    numeroEnJuego = carta.number
                    pasarTurno()
                    descarte.add(carta)
                }
            }
        }
    }

    private fun enviarCartaAlJugadorSiguiente(numeroCartas: Int) {
        if (reverse) {
            for (i in 0..numeroCartas) {
                if (mazo.isEmpty()) {
                    voltearMazo()
                }
                if (turnoJugador == 0) {
                    jugadores[jugadores.size - 1].aniadirCarta(mazo.removeAt(mazo.lastIndex))
                } else {
                    jugadores[turnoJugador - 1].aniadirCarta(mazo.removeAt(mazo.lastIndex))
                }
            }
        } else {
            for (i in 0..numeroCartas) {
                if (mazo.isEmpty()) {
                    voltearMazo()
                }
                jugadores[turnoJugador + 1 % jugadores.size].aniadirCarta(mazo.removeAt(mazo.lastIndex))
            }
        }
    }

    private fun pasarTurno(salto: Int = 1) {
        turnoJugador = if (reverse) {
            abs(turnoJugador - salto) % jugadores.size
        } else {
            (turnoJugador + salto) % jugadores.size
        }
        //getServidorActual().actualizarJugadorActual()
    }
}