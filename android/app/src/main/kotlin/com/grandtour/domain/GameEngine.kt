package com.grandtour.domain

import com.grandtour.data.model.CardDef
import com.grandtour.data.model.ChapterDef
import com.grandtour.data.model.EffectValue
import com.grandtour.data.model.Effects
import com.grandtour.data.model.EndCondition
import com.grandtour.data.model.Stat
import com.grandtour.data.model.StatBlock
import javax.inject.Inject
import kotlin.random.Random

/**
 * Pure game logic — no Android deps, JVM-testable with a seeded [Random].
 * Mirrors the web build at index.html:1510–1635.
 *
 * The no-arg constructor is the Hilt-injected one; the rng-taking
 * constructor exists for tests that need a seeded RNG.
 */
class GameEngine(private val rng: Random) {

    @Inject constructor() : this(Random.Default)


    data class StatChange(val stat: Stat, val delta: Int)
    data class ChoiceOutcome(
        val newStats: StatBlock,
        val changes: List<StatChange>,
        val newCabinet: List<String>,
    )

    fun applyEffects(
        stats: StatBlock,
        cabinet: List<String>,
        effects: Effects,
        item: String?,
    ): ChoiceOutcome {
        var current = stats
        val changes = mutableListOf<StatChange>()
        for (stat in Stat.entries) {
            val effect = effects[stat] ?: continue
            val delta = effect.resolve(rng)
            val before = current[stat]
            val after = (before + delta).coerceIn(0, 100)
            current = current.with(stat, after)
            changes.add(StatChange(stat, delta))
        }
        val newCabinet = if (item != null) cabinet + item else cabinet
        return ChoiceOutcome(current, changes, newCabinet)
    }

    /**
     * Returns the first [EndCondition] triggered by the current stats, or `null`
     * if the game continues. Matches index.html:1514–1521 in both order and
     * thresholds.
     */
    fun checkEndCondition(stats: StatBlock): EndCondition? = when {
        stats.purse <= 0 -> EndCondition.BANKRUPTCY
        stats.purse >= 100 -> EndCondition.EXCESS
        stats.reputation <= 0 -> EndCondition.DISGRACE
        stats.reputation >= 100 -> EndCondition.CELEBRITY
        stats.health <= 0 -> EndCondition.ILLNESS
        stats.health >= 100 -> EndCondition.RECKLESS
        stats.knowledge <= 0 -> EndCondition.IGNORANCE
        stats.knowledge >= 100 -> EndCondition.OBSESSION
        else -> null
    }

    /**
     * Picks the next card to show. Candidate set is all cards whose `week <=
     * currentWeek` and id not in `usedCards`. If nothing's available at the
     * current week, advances the week counter (cards may be gated by week)
     * but does NOT recycle used cards — each card is one-shot per chapter.
     * Returns `null` once the deck is exhausted; the caller treats that as
     * chapter completion.
     */
    fun pickNextCard(
        chapter: ChapterDef,
        currentWeek: Int,
        usedCards: Set<String>,
    ): CardSelection? {
        if (usedCards.size >= chapter.cards.size) return null
        var week = currentWeek
        while (week <= chapter.maxWeeks) {
            val available = chapter.cards.filter { it.week <= week && it.id !in usedCards }
            if (available.isNotEmpty()) {
                val pick = available.random(rng)
                return CardSelection(pick, week)
            }
            week++
        }
        return null
    }

    data class CardSelection(val card: CardDef, val week: Int)
}
