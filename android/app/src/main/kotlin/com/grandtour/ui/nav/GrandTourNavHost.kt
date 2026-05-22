package com.grandtour.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.grandtour.ui.character.CharacterSelectScreen
import com.grandtour.ui.chaptercomplete.ChapterCompleteScreen
import com.grandtour.ui.chaptermap.ChapterMapScreen
import com.grandtour.ui.game.GameScreen
import com.grandtour.ui.gameover.GameOverScreen
import com.grandtour.ui.title.TitleScreen

@Composable
fun GrandTourNavHost() {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = TitleRoute) {
        composable<TitleRoute> {
            TitleScreen(
                onBegin = { nav.navigate(CharacterSelectRoute) },
                onContinue = { resumedGame ->
                    nav.navigate(
                        GameRoute(
                            chapterId = resumedGame.chapterId,
                            characterId = resumedGame.characterId,
                            resume = true,
                        )
                    )
                },
            )
        }
        composable<CharacterSelectRoute> {
            CharacterSelectScreen(
                onPick = { characterId ->
                    nav.navigate(ChapterMapRoute(characterId = characterId))
                },
                onBack = { nav.popBackStack() },
            )
        }
        composable<ChapterMapRoute> { entry ->
            val args = entry.toRoute<ChapterMapRoute>()
            ChapterMapScreen(
                characterId = args.characterId,
                onPick = { chapterId ->
                    nav.navigate(
                        GameRoute(chapterId = chapterId, characterId = args.characterId)
                    )
                },
                onBack = { nav.popBackStack() },
            )
        }
        composable<GameRoute> { entry ->
            val args = entry.toRoute<GameRoute>()
            GameScreen(
                chapterId = args.chapterId,
                characterId = args.characterId,
                resume = args.resume,
                onGameOver = { reason, weeks, chapterTitle, cabinet ->
                    nav.navigate(
                        GameOverRoute(
                            reason = reason,
                            weeksSurvived = weeks,
                            chapterTitle = chapterTitle,
                            cabinet = cabinet,
                        )
                    ) {
                        popUpTo(TitleRoute) { inclusive = false }
                    }
                },
                onChapterComplete = { chapterId ->
                    nav.navigate(ChapterCompleteRoute(chapterId)) {
                        popUpTo(TitleRoute) { inclusive = false }
                    }
                },
            )
        }
        composable<GameOverRoute> { entry ->
            val args = entry.toRoute<GameOverRoute>()
            GameOverScreen(
                reason = args.reason,
                weeksSurvived = args.weeksSurvived,
                chapterTitle = args.chapterTitle,
                cabinet = args.cabinet,
                onRestart = {
                    nav.navigate(TitleRoute) {
                        popUpTo(TitleRoute) { inclusive = true }
                    }
                },
            )
        }
        composable<ChapterCompleteRoute> { entry ->
            val args = entry.toRoute<ChapterCompleteRoute>()
            ChapterCompleteScreen(
                chapterId = args.chapterId,
                onContinue = {
                    nav.navigate(TitleRoute) {
                        popUpTo(TitleRoute) { inclusive = true }
                    }
                },
            )
        }
    }
}
