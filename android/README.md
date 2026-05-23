# The Grand Tour — Android

Native Kotlin + Jetpack Compose port of the web build at
[../index.html](../index.html). See
[`plan`](../README.md) (or the saved plan file) for the full design.

## Quick start

1. Install Android Studio (Ladybug or newer).
2. `File → Open` → select this `android/` directory.
3. Wait for Gradle sync to finish (first sync downloads ~500 MB of SDK
   components + dependencies).
4. Connect a device with USB debugging enabled, or boot an emulator
   (Pixel 7 / API 35 system image recommended).
5. Press Run.

From the command line, with the Android SDK installed and `ANDROID_HOME`
set:

```bash
cd android
./gradlew :app:installDebug
adb shell am start -n com.grandtour.debug/com.grandtour.MainActivity
```

## Module layout

- `app/src/main/kotlin/com/grandtour/` — Kotlin sources
  - `data/model/` — pure data classes (cards, chapters, stats, save state)
  - `data/repo/` — `ChapterRepository`, `SaveRepository` (DataStore)
  - `domain/` — `GameEngine` (pure logic, JVM-testable)
  - `ui/{theme,nav,title,character,chaptermap,game,gameover,chaptercomplete}` — Compose screens
  - `audio/` — `AudioEngine` (SoundPool + MediaPlayer; silent until WAVs are added)
  - `illustration/` — `IllustrationRegistry` mapping JSON keys to VectorDrawables
- `app/src/main/assets/chapters/` — chapter JSON (rome.json + future cities)
- `app/src/main/res/drawable/` — 10 hand-converted VectorDrawables
- `app/src/test/kotlin/` — JVM unit tests for `GameEngine`, `EffectValueSerializer`

## Tests

```bash
cd android
./gradlew :app:testDebugUnitTest
```

Covers all 8 game-over conditions, randomized-effect resolution, and the
JSON `int | int[]` decoding pattern.

## Adding a new chapter

1. Drop a new JSON in `app/src/main/assets/chapters/`, e.g. `florence.json`.
2. Add its id to `app/src/main/assets/chapters/manifest.json`.
3. If the chapter uses new illustrations, add the VectorDrawables and register
   their keys in `IllustrationRegistry`.
4. No Kotlin changes required.

## Outstanding work (post-M3)

- **Fonts** — `Type.kt` currently uses system serif. Drop Cinzel,
  Cormorant Garamond, and IM Fell English `.ttf` files into `res/font/`
  and update `Type.kt` to reference them. All three are SIL OFL 1.1
  licensed (ship `OFL.txt` in `assets/licenses/`).
- **Audio** — see [`docs/audio.md`](docs/audio.md). Until WAVs are
  generated and dropped in `app/src/main/res/raw/`, `AudioEngine` silently
  no-ops.
- **Chapter complete payload** — the final stats and cabinet aren't
  currently passed through the navigation. Fix: extend
  `ChapterCompleteRoute` and thread them from `GameSessionViewModel`.
- **Florence / Venice / Paris content** — each chapter JSON ships with
  one placeholder card. Fill in 12–16 cards per chapter.
