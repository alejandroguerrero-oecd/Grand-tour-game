package com.grandtour.ui.chaptercomplete

import androidx.lifecycle.ViewModel
import com.grandtour.data.repo.ChapterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

data class ChapterCompletePayload(
    val chapterTitle: String,
    val completionText: String,
    val cabinet: List<String>,
    val purse: Int,
    val reputation: Int,
    val health: Int,
    val knowledge: Int,
)

@HiltViewModel
class ChapterCompleteViewModel @Inject constructor(
    private val chapterRepo: ChapterRepository,
) : ViewModel() {
    // Final stats and cabinet aren't actually wired through navigation in this
    // minimal scaffold — a follow-up should pass them via the route. For now
    // we display the chapter's completion text and let the user proceed.
    fun data(chapterId: String): Flow<ChapterCompletePayload?> {
        val chapter = chapterRepo.byId(chapterId) ?: return flowOf(null)
        return flowOf(
            ChapterCompletePayload(
                chapterTitle = chapter.title,
                completionText = chapter.completionText ?: "",
                cabinet = emptyList(),
                purse = 0, reputation = 0, health = 0, knowledge = 0,
            )
        )
    }
}
