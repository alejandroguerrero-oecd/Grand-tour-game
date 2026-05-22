package com.grandtour.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.int
import kotlinx.serialization.json.intOrNull

/**
 * A stat-effect value. The web build encodes randomized outcomes as a JS array
 * (e.g. `purse: [-20, 30]` meaning "pick one at random"). We mirror that by
 * accepting either a JSON integer or a JSON array of integers.
 */
@Serializable(with = EffectValueSerializer::class)
sealed class EffectValue {
    data class Fixed(val delta: Int) : EffectValue()
    data class Random(val choices: List<Int>) : EffectValue()

    fun resolve(rng: kotlin.random.Random): Int = when (this) {
        is Fixed -> delta
        is Random -> choices.random(rng)
    }
}

object EffectValueSerializer : KSerializer<EffectValue> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("EffectValue")

    override fun deserialize(decoder: Decoder): EffectValue {
        val jsonDecoder = decoder as? JsonDecoder
            ?: error("EffectValue only supports JSON")
        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> EffectValue.Fixed(element.int)
            is JsonArray -> EffectValue.Random(
                element.map { (it as JsonPrimitive).intOrNull ?: error("Non-int in EffectValue array") }
            )
            else -> error("Unexpected JSON for EffectValue: $element")
        }
    }

    override fun serialize(encoder: Encoder, value: EffectValue) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: error("EffectValue only supports JSON")
        when (value) {
            is EffectValue.Fixed -> jsonEncoder.encodeJsonElement(JsonPrimitive(value.delta))
            is EffectValue.Random -> jsonEncoder.encodeJsonElement(
                JsonArray(value.choices.map { JsonPrimitive(it) })
            )
        }
    }
}
