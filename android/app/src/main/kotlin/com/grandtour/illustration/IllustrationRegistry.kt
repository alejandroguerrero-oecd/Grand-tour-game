package com.grandtour.illustration

import androidx.annotation.DrawableRes
import com.grandtour.R

/**
 * Maps the string keys used in chapter JSON (e.g. "cardinal", "ruins") to
 * Android VectorDrawable resources. Adding a new chapter that uses a new
 * illustration is: drop the XML in `res/drawable/`, add an entry here.
 *
 * Mirrors the web's `Illustrations` object at index.html:1052–1211.
 */
object IllustrationRegistry {
    private val map: Map<String, Int> = mapOf(
        "default"     to R.drawable.illus_default,
        "cardinal"    to R.drawable.illus_cardinal,
        "ruins"       to R.drawable.illus_ruins,
        "salon"       to R.drawable.illus_salon,
        "antiquarian" to R.drawable.illus_antiquarian,
        "tomb"        to R.drawable.illus_tomb,
        "artist"      to R.drawable.illus_artist,
        "pope"        to R.drawable.illus_pope,
        "fever"       to R.drawable.illus_fever,
        "stpeters"    to R.drawable.illus_stpeters,
    )

    @DrawableRes
    fun resOf(key: String): Int = map[key] ?: R.drawable.illus_default
}
