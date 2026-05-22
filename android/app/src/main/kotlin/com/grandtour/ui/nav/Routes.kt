package com.grandtour.ui.nav

import kotlinx.serialization.Serializable

@Serializable
object TitleRoute

@Serializable
object CharacterSelectRoute

@Serializable
data class ChapterMapRoute(val characterId: String)

@Serializable
data class GameRoute(val chapterId: String, val characterId: String, val resume: Boolean = false)

@Serializable
data class GameOverRoute(val reason: String, val weeksSurvived: Int, val chapterTitle: String, val cabinet: List<String>)

@Serializable
data class ChapterCompleteRoute(val chapterId: String)
