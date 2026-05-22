package com.grandtour.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Effects(
    val purse: EffectValue? = null,
    val reputation: EffectValue? = null,
    val health: EffectValue? = null,
    val knowledge: EffectValue? = null,
) {
    operator fun get(stat: Stat): EffectValue? = when (stat) {
        Stat.PURSE -> purse
        Stat.REPUTATION -> reputation
        Stat.HEALTH -> health
        Stat.KNOWLEDGE -> knowledge
    }

    fun isEmpty(): Boolean =
        purse == null && reputation == null && health == null && knowledge == null
}
