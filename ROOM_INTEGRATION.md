# Lab 5 — Room Integration (MVVM + Repository + Room)

This app (a Travel/Map note app) now persists data with **Room**, so travel records
survive app restarts. Below is how each required component maps to the code.

## Architecture (data flow)

```
UI (Compose screens in MainActivity)
        ⇅  collectAsState() / calls
TravelViewModel            ← StateFlow exposed to UI, runs coroutines
        ⇅
TravelRepository           ← single entry point for data
        ⇅
TravelDao (interface)      ← SQL queries, returns Flow
        ⇅
TravelDatabase (Room)      ← singleton, holds the SQLite DB "travel_database"
```

## Required components (Part 2)

| Component   | File                                   | Notes |
|-------------|----------------------------------------|-------|
| **Entity**  | `data/TravelPlace.kt`                  | `@Entity(tableName = "travel_places")`, `@PrimaryKey(autoGenerate = true)` |
| **DAO**     | `data/TravelDao.kt`                    | `insert()`, `update()`, `delete()`, `getAll(): Flow<List<TravelPlace>>`, `getById()` |
| **Database**| `data/TravelDatabase.kt`              | `@Database`, thread-safe singleton, seeds 3 sample rows on first creation |
| **Repository** | `data/TravelRepository.kt`         | Connects ViewModel ↔ DAO |
| **ViewModel** | `TravelViewModel.kt`                | Exposes `StateFlow`, calls repository inside `viewModelScope` |
| **Application** | `TravelApplication.kt`            | Owns the DB + repository singletons (registered in `AndroidManifest.xml`) |

Room is wired with **KSP** (`com.google.devtools.ksp`) + `androidx.room:room-runtime/ktx/compiler` 2.6.1 — see `app/build.gradle.kts` and `gradle/libs.versions.toml`.

## How writes/reads flow

- **Add a place** → `AddTravelScreen` → `viewModel.addTravelPlace(...)` →
  `repository.insert()` → `dao.insert()` (suspend, off the main thread).
- **List/detail screens** read `dao.getAll()` / `dao.getById()` as a `Flow`.
  Room emits a new list automatically whenever the table changes, so the UI
  updates with no manual refresh.

## Demo checklist for the VSR video (Part 3)

1. **Room concept (20s):** Room = an abstraction layer over SQLite. Entity = table,
   DAO = queries, Database = the SQLite instance. Data is persistent.
2. **Live demo (1 min):**
   - Add a new travel record → it appears in the list.
   - Fully close and reopen the app → the record is still there (persistence).
   - **Android Studio → View → Tool Windows → App Inspection → Database Inspector**,
     select `travel_database` → `travel_places` → show the saved rows.
   - Explain one class, e.g. `TravelDao` (the `@Query` returning `Flow`) or
     `TravelViewModel` (`stateIn` turning the Flow into UI state).

## Run / build

- Open in Android Studio and Run (recommended — it has the matching JDK + SDK).
- `local.properties` `sdk.dir` is machine-specific; on this Linux machine it points
  to `/home/alanwine/Android/Sdk`. On your own machine Android Studio sets it.
