package com.grandtour.illustration

import androidx.annotation.DrawableRes
import com.grandtour.R

/**
 * Maps the string keys used in chapter JSON to Android drawable resources.
 *
 * Placeholders ship as VectorDrawables in `res/drawable/`. Running
 * `tools/fetch_illustrations.py` replaces them with 18th-c. engraving
 * JPGs in `res/drawable-nodpi/` (and deletes the matching .xml so
 * resource names don't collide). See `docs/illustrations.md`.
 */
object IllustrationRegistry {
    private val map: Map<String, Int> = mapOf(
        // Rome
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
        // Florence
        "florence_duomo"   to R.drawable.illus_florence_duomo,
        "florence_palazzo" to R.drawable.illus_florence_palazzo,
        "florence_gallery" to R.drawable.illus_florence_gallery,
        "florence_market"  to R.drawable.illus_florence_market,
        // Venice
        "venice_bacino"    to R.drawable.illus_venice_bacino,
        "venice_carnival"  to R.drawable.illus_venice_carnival,
        "venice_palace"    to R.drawable.illus_venice_palace,
        "venice_murano"    to R.drawable.illus_venice_murano,
        // Paris
        "paris_tuileries"  to R.drawable.illus_paris_tuileries,
        "paris_versailles" to R.drawable.illus_paris_versailles,
        "paris_salon"      to R.drawable.illus_paris_salon,
        "paris_voltaire"   to R.drawable.illus_paris_voltaire,
    )

    @DrawableRes
    fun resOf(key: String): Int = map[key] ?: R.drawable.illus_default
}
