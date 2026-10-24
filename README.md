# Nera

Native Kotlin Android foundation for the VirexDL experience from `index.html`.

## Phase 1 (implemented)
- Android Gradle project + wrapper
- Compose app shell with VirexDL-style dark theme
- Bottom navigation sections: Home, Search, Playlists, Downloads
- Mini player shell and settings sheet
- Data/domain scaffolding for songs, playlists, categories, and repository contracts
- In-memory repository used as bootstrapping data source
- API endpoint contracts prepared for JioSaavn proxy + LrcLib integration

## Next phases
1. Wire real networking and offline persistence (IndexedDB equivalent in Room/DataStore).
2. Integrate Media3 playback queue, shuffle/loop/crossfade, and media session controls.
3. Implement playlist import, lyrics sync, downloads pipeline, queue drawer, and settings parity.
4. Add instrumentation/UI tests for end-to-end parity with VirexDL interactions.

## Build
```bash
./gradlew tasks
```
