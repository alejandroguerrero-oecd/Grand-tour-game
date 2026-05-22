package com.grandtour.domain

import com.grandtour.data.json.AppJson
import com.grandtour.data.model.EffectValue
import com.grandtour.data.model.Effects
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class EffectValueSerializerTest {

    @Test
    fun `decodes a plain integer as Fixed`() {
        val effects = AppJson.decodeFromString(
            Effects.serializer(),
            """{ "purse": -25, "knowledge": 20 }""",
        )
        assertEquals(EffectValue.Fixed(-25), effects.purse)
        assertEquals(EffectValue.Fixed(20), effects.knowledge)
        assertEquals(null, effects.reputation)
    }

    @Test
    fun `decodes a JSON array as Random`() {
        val effects = AppJson.decodeFromString(
            Effects.serializer(),
            """{ "purse": [-20, 30], "reputation": 10 }""",
        )
        assertEquals(EffectValue.Random(listOf(-20, 30)), effects.purse)
        assertEquals(EffectValue.Fixed(10), effects.reputation)
    }

    @Test
    fun `Random resolves to one of its choices`() {
        val random = EffectValue.Random(listOf(-20, 30))
        val rng = Random(42)
        repeat(100) {
            val v = random.resolve(rng)
            assertTrue("resolved to $v", v == -20 || v == 30)
        }
    }

    @Test
    fun `Fixed always resolves to its delta`() {
        val fixed = EffectValue.Fixed(7)
        repeat(50) { assertEquals(7, fixed.resolve(Random(it.toLong()))) }
    }

    @Test
    fun `round-trips through serialize-deserialize`() {
        val original = Effects(
            purse = EffectValue.Random(listOf(-20, 30)),
            knowledge = EffectValue.Fixed(15),
        )
        val json = AppJson.encodeToString(Effects.serializer(), original)
        val decoded = AppJson.decodeFromString(Effects.serializer(), json)
        assertEquals(original, decoded)
    }
}
