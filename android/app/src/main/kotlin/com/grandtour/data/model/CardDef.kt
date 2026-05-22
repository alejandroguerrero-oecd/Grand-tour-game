package com.grandtour.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CardDef(
    val id: String,
    val week: Int,
    val character: String,
    val illustration: String,
    val dialogue: String,
    val leftChoice: String,
    val rightChoice: String,
    val leftEffects: Effects = Effects(),
    val rightEffects: Effects = Effects(),
    val leftText: String = "",
    val rightText: String = "",
    val leftItem: String? = null,
    val rightItem: String? = null,
)
