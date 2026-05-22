package com.grandtour.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ChapterDef(
    val id: String,
    val title: String,
    val ordinal: Int,
    val maxWeeks: Int = 12,
    val cards: List<CardDef>,
    val gameOverMessages: Map<String, String> = emptyMap(),
    val completionText: String? = null,
    val previousChapterId: String? = null,
)

@Serializable
data class ChapterManifest(val chapters: List<String>)
