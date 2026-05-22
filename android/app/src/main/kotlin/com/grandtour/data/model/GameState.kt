package com.grandtour.data.model

import kotlinx.serialization.Serializable

/**
 * Snapshot of an in-progress game session. Persisted to DataStore so the user
 * can quit and resume mid-tour.
 */
@Serializable
data class GameState(
    val schemaVersion: Int = 1,
    val chapterId: String,
    val characterId: String,
    val week: Int = 1,
    val stats: StatBlock = StatBlock(),
    val cabinet: List<String> = emptyList(),
    val usedCards: List<String> = emptyList(),
)

enum class EndCondition(val key: String) {
    BANKRUPTCY("bankruptcy"),
    EXCESS("excess"),
    DISGRACE("disgrace"),
    CELEBRITY("celebrity"),
    ILLNESS("illness"),
    RECKLESS("reckless"),
    IGNORANCE("ignorance"),
    OBSESSION("obsession"),
}

/**
 * Default game-over blurbs, taken verbatim from the web build
 * (`index.html:1514–1521`). Chapters may override via [ChapterDef.gameOverMessages].
 */
object DefaultGameOverMessages {
    val text: Map<EndCondition, String> = mapOf(
        EndCondition.BANKRUPTCY to "Your purse is empty. Without funds, you cannot continue. The Grand Tour ends in ignominy.",
        EndCondition.EXCESS to "Your wealth attracts the wrong attention. Bandits relieve you of everything on the road to Florence.",
        EndCondition.DISGRACE to "Your reputation is ruined. Doors close; letters go unanswered. You slink home in shame.",
        EndCondition.CELEBRITY to "Your fame becomes your prison. You cannot move without crowds. The quiet study you sought is impossible.",
        EndCondition.ILLNESS to "The Roman fever claims another victim. You are buried in the Protestant Cemetery, beside Keats.",
        EndCondition.RECKLESS to "Overconfidence in your vigour leads to a fatal accident in the catacombs.",
        EndCondition.IGNORANCE to "You have learned nothing. Your patron withdraws support, deeming the investment wasted.",
        EndCondition.OBSESSION to "Your pursuit of knowledge consumes all else. You forget to eat, to sleep. Madness follows.",
    )
}
