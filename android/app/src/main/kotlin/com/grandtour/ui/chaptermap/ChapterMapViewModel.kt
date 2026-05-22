package com.grandtour.ui.chaptermap

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grandtour.data.model.ChapterDef
import com.grandtour.data.repo.ChapterRepository
import com.grandtour.data.repo.SaveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ChapterMapViewModel @Inject constructor(
    chapterRepo: ChapterRepository,
    saveRepo: SaveRepository,
) : ViewModel() {
    val chapters: StateFlow<List<ChapterDef>> =
        MutableStateFlow(chapterRepo.all())

    val completedChapters: StateFlow<Set<String>> =
        saveRepo.completedChaptersFlow.stateIn(
            viewModelScope, SharingStarted.Eagerly, emptySet()
        )
}
