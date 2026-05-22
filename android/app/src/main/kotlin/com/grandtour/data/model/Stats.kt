package com.grandtour.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class Stat { PURSE, REPUTATION, HEALTH, KNOWLEDGE }

@Serializable
data class StatBlock(
    val purse: Int = 50,
    val reputation: Int = 50,
    val health: Int = 50,
    val knowledge: Int = 50,
) {
    operator fun get(stat: Stat): Int = when (stat) {
        Stat.PURSE -> purse
        Stat.REPUTATION -> reputation
        Stat.HEALTH -> health
        Stat.KNOWLEDGE -> knowledge
    }

    fun with(stat: Stat, value: Int): StatBlock {
        val clamped = value.coerceIn(0, 100)
        return when (stat) {
            Stat.PURSE -> copy(purse = clamped)
            Stat.REPUTATION -> copy(reputation = clamped)
            Stat.HEALTH -> copy(health = clamped)
            Stat.KNOWLEDGE -> copy(knowledge = clamped)
        }
    }
}
