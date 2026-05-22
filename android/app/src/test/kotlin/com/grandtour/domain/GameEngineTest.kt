package com.grandtour.domain

import com.grandtour.data.model.CardDef
import com.grandtour.data.model.ChapterDef
import com.grandtour.data.model.EffectValue
import com.grandtour.data.model.Effects
import com.grandtour.data.model.EndCondition
import com.grandtour.data.model.Stat
import com.grandtour.data.model.StatBlock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class GameEngineTest {

    private fun engine(seed: Long = 42L) = GameEngine(Random(seed))

    @Test
    fun `applyEffects clamps stats to 0-100`() {
        val out = engine().applyEffects(
            stats = StatBlock(purse = 95, reputation = 5, health = 50, knowledge = 50),
            cabinet = emptyList(),
            effects = Effects(
                purse = EffectValue.Fixed(20),
                reputation = EffectValue.Fixed(-20),
            ),
            item = null,
        )
        assertEquals(100, out.newStats.purse)
        assertEquals(0, out.newStats.reputation)
    }

    @Test
    fun `applyEffects adds item to cabinet when provided`() {
        val out = engine().applyEffects(
            stats = StatBlock(),
            cabinet = listOf("existing"),
            effects = Effects(purse = EffectValue.Fixed(5)),
            item = "new",
        )
        assertEquals(listOf("existing", "new"), out.newCabinet)
    }

    @Test
    fun `applyEffects leaves cabinet alone when item is null`() {
        val out = engine().applyEffects(
            stats = StatBlock(),
            cabinet = listOf("existing"),
            effects = Effects(purse = EffectValue.Fixed(5)),
            item = null,
        )
        assertEquals(listOf("existing"), out.newCabinet)
    }

    @Test
    fun `applyEffects with Random uses rng`() {
        val effects = Effects(purse = EffectValue.Random(listOf(-100, 100)))
        // Same seed → reproducible outcome. We just assert the result is one of the choices.
        val out = engine(seed = 7L).applyEffects(StatBlock(purse = 50), emptyList(), effects, null)
        val delta = out.changes.single { it.stat == Stat.PURSE }.delta
        assertTrue("delta=$delta", delta == -100 || delta == 100)
    }

    @Test
    fun `checkEndCondition fires bankruptcy at purse 0`() {
        assertEquals(EndCondition.BANKRUPTCY, engine().checkEndCondition(StatBlock(purse = 0)))
    }

    @Test
    fun `checkEndCondition fires excess at purse 100`() {
        assertEquals(EndCondition.EXCESS, engine().checkEndCondition(StatBlock(purse = 100)))
    }

    @Test
    fun `checkEndCondition fires disgrace at reputation 0`() {
        assertEquals(EndCondition.DISGRACE, engine().checkEndCondition(StatBlock(reputation = 0)))
    }

    @Test
    fun `checkEndCondition fires celebrity at reputation 100`() {
        assertEquals(EndCondition.CELEBRITY, engine().checkEndCondition(StatBlock(reputation = 100)))
    }

    @Test
    fun `checkEndCondition fires illness at health 0`() {
        assertEquals(EndCondition.ILLNESS, engine().checkEndCondition(StatBlock(health = 0)))
    }

    @Test
    fun `checkEndCondition fires reckless at health 100`() {
        assertEquals(EndCondition.RECKLESS, engine().checkEndCondition(StatBlock(health = 100)))
    }

    @Test
    fun `checkEndCondition fires ignorance at knowledge 0`() {
        assertEquals(EndCondition.IGNORANCE, engine().checkEndCondition(StatBlock(knowledge = 0)))
    }

    @Test
    fun `checkEndCondition fires obsession at knowledge 100`() {
        assertEquals(EndCondition.OBSESSION, engine().checkEndCondition(StatBlock(knowledge = 100)))
    }

    @Test
    fun `checkEndCondition returns null in safe range`() {
        assertNull(engine().checkEndCondition(StatBlock(50, 50, 50, 50)))
        assertNull(engine().checkEndCondition(StatBlock(1, 99, 50, 50)))
    }

    @Test
    fun `pickNextCard picks only cards whose week is reached`() {
        val chapter = ChapterDef(
            id = "test", title = "Test", ordinal = 1,
            cards = listOf(
                card("a", week = 1),
                card("b", week = 5),
                card("c", week = 3),
            ),
        )
        val pick = engine().pickNextCard(chapter, currentWeek = 2, usedCards = emptySet())
        assertNotNull(pick)
        assertTrue(pick!!.card.id == "a") // only one with week<=2
    }

    @Test
    fun `pickNextCard skips used cards`() {
        val chapter = ChapterDef(
            id = "test", title = "Test", ordinal = 1,
            cards = listOf(card("a", week = 1), card("b", week = 1)),
        )
        val pick = engine().pickNextCard(chapter, currentWeek = 1, usedCards = setOf("a"))
        assertEquals("b", pick?.card?.id)
    }

    @Test
    fun `pickNextCard advances week when pool is empty`() {
        val chapter = ChapterDef(
            id = "test", title = "Test", ordinal = 1,
            cards = listOf(card("a", week = 1), card("b", week = 3)),
        )
        // Week 2, but card "a" is used. The engine should advance the week
        // until it finds card "b" at week 3.
        val pick = engine().pickNextCard(chapter, currentWeek = 2, usedCards = setOf("a"))
        assertEquals("b", pick?.card?.id)
        assertEquals(3, pick?.week)
    }

    @Test
    fun `pickNextCard returns null when chapter is exhausted`() {
        val chapter = ChapterDef(
            id = "test", title = "Test", ordinal = 1, maxWeeks = 12,
            cards = listOf(card("a", week = 1)),
        )
        val pick = engine().pickNextCard(chapter, currentWeek = 1, usedCards = setOf("a"))
        assertNull(pick)
    }

    private fun card(id: String, week: Int) = CardDef(
        id = id, week = week, character = "x", illustration = "default",
        dialogue = "", leftChoice = "L", rightChoice = "R",
    )
}
