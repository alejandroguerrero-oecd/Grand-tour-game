package com.grandtour.data.model

import com.grandtour.R

/**
 * Three starting characters from index.html:1034–1047.
 */
object CharacterPresets {
    val Aristocrat = CharacterPreset(
        id = "aristocrat",
        nameRes = R.string.character_aristocrat_name,
        descriptionRes = R.string.character_aristocrat_desc,
        startingStats = StatBlock(purse = 70, reputation = 60, health = 50, knowledge = 30),
    )

    val Scholar = CharacterPreset(
        id = "scholar",
        nameRes = R.string.character_scholar_name,
        descriptionRes = R.string.character_scholar_desc,
        startingStats = StatBlock(purse = 25, reputation = 40, health = 50, knowledge = 70),
    )

    val Widow = CharacterPreset(
        id = "widow",
        nameRes = R.string.character_widow_name,
        descriptionRes = R.string.character_widow_desc,
        startingStats = StatBlock(purse = 55, reputation = 45, health = 45, knowledge = 55),
    )

    val all: List<CharacterPreset> = listOf(Aristocrat, Scholar, Widow)

    fun byId(id: String): CharacterPreset = all.firstOrNull { it.id == id } ?: Aristocrat
}
