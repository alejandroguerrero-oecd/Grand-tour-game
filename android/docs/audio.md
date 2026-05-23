# Audio assets

The runtime reads these files via `Resources.getIdentifier()` from
`app/src/main/res/raw/`. The current files are committed and play
out of the box — `AudioEngine` checks the resource exists and silently
no-ops otherwise.

Files in `res/raw/`:
- `sfx_swipe_left.wav`   — descending tritone (392, 330, 294 Hz)
- `sfx_swipe_right.wav`  — ascending fifth   (392, 494, 587 Hz)
- `sfx_game_over.wav`    — 5-note descent    (392, 370, 330, 294, 262 Hz)
- `sfx_victory.wav`      — ascending scale   (392, 440, 494, 523, 587, 659 Hz)
- `music_ambient.wav`    — 32-note harpsichord-style loop, ~32s

## Regenerating

Files are deterministic (the music uses seed=1788). To regenerate from
scratch — for example to tune the gain envelope or change the music
pattern — run:

```bash
cd android/app/src/main/res/raw
python3 ../../../../../tools/synthesize_audio.py
```

The script uses pure Python stdlib (no NumPy/SciPy required) and matches
the Web Audio parameters from `index.html:889–984`: sawtooth oscillator
→ biquad lowpass (cutoff 2 kHz, Q=2) → exponential gain ramp from 0.12
to 0.001 over each note's duration.
