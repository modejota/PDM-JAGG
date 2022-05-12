package com.modejota.unitcardgame.model

import java.io.Serializable

data class Card(
    val number: Int,
    val color: String,
    val type: CardType
): Serializable