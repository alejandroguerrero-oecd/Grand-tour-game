package com.grandtour.data.repo

import android.content.Context
import com.grandtour.data.json.AppJson
import com.grandtour.data.model.ChapterDef
import com.grandtour.data.model.ChapterManifest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChapterRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val cache: List<ChapterDef> by lazy { loadAllFromAssets() }

    fun all(): List<ChapterDef> = cache

    fun byId(id: String): ChapterDef? = cache.firstOrNull { it.id == id }

    private fun loadAllFromAssets(): List<ChapterDef> {
        val manifestText = context.assets.open("chapters/manifest.json").bufferedReader().use { it.readText() }
        val manifest = AppJson.decodeFromString(ChapterManifest.serializer(), manifestText)
        return manifest.chapters.mapNotNull { id ->
            runCatching {
                val text = context.assets.open("chapters/$id.json").bufferedReader().use { it.readText() }
                AppJson.decodeFromString(ChapterDef.serializer(), text)
            }.getOrNull()
        }.sortedBy { it.ordinal }
    }
}
