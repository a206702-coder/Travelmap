# Green Travel — Sustainable City Companion (Project 2)

> **TK2323 / TM2213 — Mobile Application Programming · Project 2**
> Advanced Data, APIs & Sensor Integration

An Android (Jetpack Compose) app that continues the **SDG 11 — Sustainable Cities &
Communities** theme from Project 1. It helps users record green travel, check the
**live air quality** around them, and share sustainable-travel tips with a community —
combining on-device persistence, the cloud, a live web API, and a hardware sensor.

---

## 🌍 SDG Theme — SDG 11: Sustainable Cities and Communities

Malaysian cities face traffic congestion, carbon emissions, and poor urban air
quality. **Green Travel** encourages low-carbon mobility by letting users:

- track their own green-travel footprints,
- understand the **air quality** at their exact location before deciding how to travel, and
- learn from and contribute to a shared community board of sustainable-travel tips.

---

## ✅ The Four Technical Pillars

| Pillar | Implementation | Key files |
|--------|----------------|-----------|
| **Local Persistence (Room)** | Private "My Travel Footprints" stored on-device, offline-accessible | `data/TravelPlace.kt`, `data/TravelDao.kt`, `data/TravelDatabase.kt`, `data/TravelRepository.kt`, `TravelViewModel.kt` |
| **Data from the Internet (Retrofit)** | Live air-quality data from the **Open-Meteo Air Quality REST API** (free, no API key) | `remote/AirQualityApi.kt`, `remote/AirQualityModels.kt`, `remote/RetrofitClient.kt`, `data/AirQualityRepository.kt` |
| **Sensor (GPS / Location)** | `FusedLocationProviderClient` reads the current latitude/longitude, which feeds the air-quality lookup | `sensor/LocationHelper.kt`, `AirQualityViewModel.kt`, `AirQualityScreen.kt` |
| **Cloud Integration (Firebase Firestore)** | Public "Community Green Board" — users publish tips; any local record can be shared to the cloud | `cloud/CommunityPost.kt`, `cloud/CloudRepository.kt`, `CommunityViewModel.kt`, `CommunityScreen.kt` |

The **GPS sensor and the web API are deliberately chained**: tapping *"Use GPS &
Check Air Quality"* reads the device location (sensor) and then queries Open-Meteo
(internet) for the pollution levels at that exact spot — a single, coherent feature
that demonstrates two pillars at once.

---

## 📱 Screens (10 total — minimum required is 7)

1. **Home / Map** — overview, quick functions, list of saved travel footprints (Room).
2. **Travel Detail** — details of a saved place + **"Share to Community"** (Room → Firestore).
3. **Add Travel** — form that saves a new record locally (Room write).
4. **SDG Intro** — the SDG 11 problem & solution statement.
5. **Travel Statistics** — counts derived from the Room data.
6. **Explore** — static curated green-travel suggestions.
7. **Profile** — user profile and trip stats.
8. **Air Quality** — **GPS sensor + Open-Meteo API** live air-quality reading.
9. **Community Green Board** — live Firestore feed of community tips (cloud read).
10. **Add Community Post** — publish a new tip to Firestore (cloud write).

---

## 🏗️ Architecture (MVVM + Repository)

```
Compose UI (screens)
      ⇅  collectAsState() / events
ViewModels  (TravelViewModel · AirQualityViewModel · CommunityViewModel)
      ⇅
Repositories (TravelRepository · AirQualityRepository · CloudRepository)
      ⇅                 ⇅                    ⇅
Room (SQLite)   Retrofit → Open-Meteo   Firebase Firestore
                + FusedLocation (GPS)
```

All long-running work runs inside `viewModelScope`; data is exposed to Compose as
`StateFlow` and collected with `collectAsState()`.

---

## 🔧 Setup Instructions

### Prerequisites
- Android Studio (Ladybug or newer)
- JDK 17 or 21
- Android SDK 35, min SDK 24
- A physical device or emulator with Google Play Services (for the GPS sensor)

### Steps
1. **Clone** this repository and open it in Android Studio.
2. Android Studio writes `local.properties` (`sdk.dir`) automatically.
3. **Firebase (Cloud pillar):**
   - The app reads `app/google-services.json`. A working file for the Firebase
     project `hxyt-f98ea` is already included.
   - The build is **defensive**: `app/build.gradle.kts` only applies the Google
     Services plugin *when* `google-services.json` is present, and `CloudRepository`
     degrades gracefully if Firebase is not initialized — so the project compiles
     and runs even without it. To use your own Firebase project, replace
     `app/google-services.json` (package name must be `com.example.travelmap`).
   - In the Firebase console, create a **Cloud Firestore** database named `(default)`.
     For demos, *test mode* rules are sufficient; tighten them for production.
4. **Run** the app (`Shift+F10`). Grant the **location permission** when prompted to
   use the Air Quality feature.

> No API key is needed for the Open-Meteo Air Quality API.

---

## ✨ Feature List

- 📍 **Air quality near me** — one tap reads GPS and shows live AQI + PM2.5/PM10/O₃/NO₂/SO₂/CO with colour-coded health bands.
- 💾 **Offline travel log** — add/view travel footprints saved with Room; survives app restarts.
- ☁️ **Community Green Board** — real-time Firestore feed; publish tips and watch them appear live.
- 🔁 **Share local → cloud** — push any private travel record to the public board from its detail screen.
- 🌗 **Light / dark theme** toggle.
- 🧭 **Bottom-navigation** across Map · Air · Community · Explore · Profile.

---

## 🧰 Tech Stack

Kotlin · Jetpack Compose · Navigation Compose · Material 3 · MVVM · Room ·
Retrofit + Gson · Firebase Cloud Firestore · Google Play Services Location
(FusedLocationProvider) · Kotlin Coroutines & Flow.

## 📚 APIs & References

- **Open-Meteo Air Quality API** — https://open-meteo.com/en/docs/air-quality-api (free, no key)
- **Firebase Firestore** — https://firebase.google.com/docs/firestore
- **Android Location** — https://developer.android.com/training/location

## 🙏 AI Assistance Acknowledgement

AI tools were used to help scaffold boilerplate and documentation. All core logic
(Room, Retrofit, sensor lifecycle, Firestore) is understood and maintained by the
author, who can explain and modify it live.
