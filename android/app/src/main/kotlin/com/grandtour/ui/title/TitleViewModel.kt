package com.grandtour.ui.title

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.grandtour.data.model.GameState
import com.grandtour.data.repo.SaveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class TitleViewModel @Inject constructor(
    saveRepo: SaveRepository,
) : ViewModel() {
    val activeGame: StateFlow<GameState?> =
        saveRepo.activeGameFlow.stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
