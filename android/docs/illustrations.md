# Illustrations

Card illustrations are 18th-century public-domain engravings from
Wikimedia Commons (Piranesi's Vedute, Panini, Canaletto, Zocchi, etc.),
displayed under a sepia color filter so they blend with the parchment
theme.

## Layout

- `tools/illustrations.json` — manifest mapping illustration keys to
  candidate Wikimedia file titles, per city.
- `tools/fetch_illustrations.py` — Python 3 script that resolves each
  title via the Commons API, downloads an 800-px thumbnail, and saves
  to `app/src/main/res/drawable-nodpi/<key>.jpg`. Removes the
  corresponding placeholder vector in `res/drawable/` when the bitmap
  lands.
- `app/src/main/kotlin/com/grandtour/illustration/IllustrationRegistry.kt`
  — maps the string keys from chapter JSON to `R.drawable.*`
  resource ids.
- `app/src/main/kotlin/com/grandtour/ui/game/SwipeableCard.kt` —
  applies `SepiaFilter` (a classic sepia color matrix) to whatever
  drawable is loaded.

## First-time fetch

From the `android/` directory:

```bash
python3 tools/fetch_illustrations.py
```

You need Python 3.6+. On Windows: `winget install Python.Python.3.12`
or download from python.org. No `pip install` needed — the script
uses only the standard library.

`--only rome` to fetch one city. `--dry-run` to test resolution
without writing files.

## Adding a new illustration

1. Find an engraving on https://commons.wikimedia.org. Copy the file
   page title (e.g. `File:Piranesi-_Veduta_dell-_Acqua_Felice.jpg`).
2. Add an entry under the relevant city in
   `tools/illustrations.json`:
   ```json
   "illus_rome_acqua_felice": {
     "titles": ["Piranesi-_Veduta_dell-_Acqua_Felice.jpg"],
     "search": "Piranesi Acqua Felice",
     "credit": "Piranesi — Veduta dell'Acqua Felice"
   }
   ```
3. Add the matching entry to `IllustrationRegistry.kt`:
   ```kotlin
   "illus_rome_acqua_felice" to R.drawable.illus_rome_acqua_felice,
   ```
4. Reference the key from your card's JSON:
   ```json
   { "illustration": "illus_rome_acqua_felice", ... }
   ```
5. Rerun the fetch script. Build and run.

## When a title fails

The script tries each candidate in `titles` first, then falls back to
the `search` query. If both miss, edit `titles` with a better
candidate. To find the canonical file title:

1. Open https://commons.wikimedia.org and search.
2. Click the image you want.
3. The page URL ends with `/wiki/File:Something.jpg` — that's the title.

## Image rights

All images sourced this way are pre-1929 and public domain in the US
and EU. Wikimedia tracks licensing on each file page. The fetch script
saves no licensing metadata — keep a record of credits in the
`credit:` field of each manifest entry if you ship to the Play Store
(Google's content policy doesn't require it for PD works, but it's
good practice).
