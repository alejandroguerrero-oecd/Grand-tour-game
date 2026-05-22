package com.grandtour.data.model

data class CharacterPreset(
    val id: String,
    val nameRes: Int,
    val descriptionRes: Int,
    val startingStats: StatBlock,
)
