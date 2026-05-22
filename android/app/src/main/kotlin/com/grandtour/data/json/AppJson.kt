package com.grandtour.data.json

import kotlinx.serialization.json.Json

val AppJson: Json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    explicitNulls = false
    encodeDefaults = true
}
