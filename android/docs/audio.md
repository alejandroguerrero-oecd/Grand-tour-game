# Audio assets

The runtime reads these files via `Resources.getIdentifier()` from
`app/src/main/res/raw/`. They are optional — the app builds and runs
silently without them. (The `raw/` directory does not exist in the
checked-in tree because git does not track empty directories; create it
when you drop the first file in.)

Files expected:
- `sfx_swipe_left.wav`  — descending tritone (392, 330, 294 Hz)
- `sfx_swipe_right.wav` — ascending fifth   (392, 494, 587 Hz)
- `sfx_game_over.wav`   — 5-note descent    (392, 370, 330, 294, 262 Hz)
- `sfx_victory.wav`     — ascending scale   (392, 440, 494, 523, 587, 659 Hz)
- `music_ambient.ogg`   — 30-60s seamless harpsichord-style loop

To generate, run a NumPy/SciPy synthesis script that matches the Web Audio
parameters in `index.html:889–984`: sawtooth oscillator → biquad lowpass
(cutoff 2 kHz, Q=2) → exponential gain ramp from 0.12 to 0.001 over the
note's duration. See the M4 milestone in the plan file for details.
