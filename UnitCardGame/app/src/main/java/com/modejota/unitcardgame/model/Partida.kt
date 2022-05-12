package com.modejota.unitcardgame.model

class Partida {

    private val mazo = ArrayList<Card>()
    private val descarte = ArrayList<Card>()
    private var colorEnJuego: String = ""
    private var numeroEnJuego: Int = 0
    private var turnoJugador: Int = 0
    private var reverse: Boolean = false

    init {
        crearMazo()
    }

    private fun crearMazo() {
        // Para cada color, hay 2 cartas con cada uno de los valores de 1 a 9.
        for (j in 0..1) {
            for (i in 1..9) {
                mazo.add(Card(i, "azul", CardType.NUMBER, "carta_azul_$i"))
                mazo.add(Card(i, "verde", CardType.NUMBER, "carta_verde_$i"))
                mazo.add(Card(i, "roja", CardType.NUMBER, "carta_roja_$i"))
                mazo.add(Card(i, "amarilla", CardType.NUMBER, "carta_amarilla_$i"))
            }
        }
        // Para cada color, hay una carta con el valor 0.
        mazo.add(Card(0, "azul", CardType.NUMBER, "carta_azul_0"))
        mazo.add(Card(0, "verde", CardType.NUMBER, "carta_verde_0"))
        mazo.add(Card(0, "roja", CardType.NUMBER, "carta_roja_0"))
        mazo.add(Card(0, "amarilla", CardType.NUMBER, "carta_amarilla_0"))
        // Para cada color:
        // 2 cartas para saltar al siguiente jugador
        // 2 cartas para cambiar el sentido.
        // 2 cartas para que el siguiente jugador robe 2 cartas
        for (i in 0..2) {
            mazo.add(Card(0, "azul", CardType.SKIP, "carta_azul_skip"))
            mazo.add(Card(0, "verde", CardType.SKIP, "carta_verde_skip"))
            mazo.add(Card(0, "roja", CardType.SKIP, "carta_roja_skip"))
            mazo.add(Card(0, "amarilla", CardType.SKIP, "carta_amarilla_skip"))
            mazo.add(Card(0, "azul", CardType.REVERSE, "carta_azul_reverse"))
            mazo.add(Card(0, "verde", CardType.REVERSE, "carta_verde_reverse"))
            mazo.add(Card(0, "roja", CardType.REVERSE, "carta_roja_reverse"))
            mazo.add(Card(0, "amarilla", CardType.REVERSE, "carta_amarilla_reverse"))
            mazo.add(Card(0, "azul", CardType.DRAW2, "carta_azul_draw2"))
            mazo.add(Card(0, "verde", CardType.DRAW2, "carta_verde_draw2"))
            mazo.add(Card(0, "roja", CardType.DRAW2, "carta_roja_draw2"))
            mazo.add(Card(0, "amarilla", CardType.DRAW2, "carta_amarilla_draw2"))
        }
        // Hay 4 cartas comodín para cambiar de color y otras 4 cartas para robar 4
        for (i in 0..4) {
            mazo.add(Card(0, "RAINBOW", CardType.WILDCARD, "carta_wildcards"))
            mazo.add(Card(0, "RAINBOW", CardType.DRAW4, "carta_draw4"))
        }
        // Finalmente, bajaramos el mazo.
        mazo.shuffle()
    }

    fun getInitCards(): ArrayList<Card> {
        val cartas = ArrayList<Card>()
        for (i in 0..7) {
            cartas.add(mazo.removeAt(i))
        }
        return cartas
    }

}