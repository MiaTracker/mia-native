# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Desktop (JVM)
./gradlew jvmRun                  # Run desktop app
./gradlew jvmRun --hot-reload     # Run with Compose hot reload

# Android
./gradlew assembleDebug           # Build debug APK
./gradlew installDebug            # Install on connected device/emulator

# Web (WASM)
./gradlew wasmJsBrowserRun        # Run in browser

# Distribution
./gradlew packageDmg              # macOS
./gradlew packageMsi              # Windows
./gradlew packageDeb              # Linux
```

There are no tests in this project.

## Architecture

Kotlin Multiplatform app targeting **Android**, **Desktop (JVM)**, and **Web (WASM)**. All UI and business logic lives in `commonMain`; platform-specific code is minimal and gated by `expect`/`actual`.

### Source set hierarchy

```
commonMain
├── androidMain
├── desktopMain         (shared between jvmMain and webMain)
│   ├── jvmMain
│   └── webMain
│       └── wasmJsMain
```

### Key layers (all in `src/commonMain/kotlin/`)

**Navigation** — `Navigation.kt`  
Type-safe Compose Navigation routes defined as nested serializable objects/data classes under `Navigation`. The root `NavHost` starts at `Navigation.InstanceSelection`. Authenticated routes live inside the `Navigation.Inner` nested graph (start destination: `MoviesIndex`). `extensions/NavControllerExtensions.kt` provides `navigateFresh()` which navigates and clears the entire back stack via `popUpTo(graph.id) { inclusive = true }`.

**ViewModels** — `view_models/`  
Standard `androidx.lifecycle.ViewModel` with `viewModelScope`. UI state is modelled as `MutableStateFlow<UiState>` sealed classes. ViewModels receive an `ErrorHandler` and delegate all API errors to it.

**API client** — `infrastructure/Api.kt`  
Single Ktor-based facade. Instantiated per base URL via `Api(baseUrl)`. Endpoints are grouped as inner classes — e.g. `Api.instance.Movies()`, `Api.instance.Series()`, `Api.instance.Users()`. All calls return a custom `Result<T>` (Success/Error). Authentication tokens are injected from `Preferences` into every request header.

**Preferences** — `infrastructure/Preferences.kt` (expect) + platform actuals  
Stores instance URL, auth token, token expiry, and admin flag. Platform actuals: Android → SharedPreferences, JVM → file-based, Web → LocalStorage.

**Error handling** — `infrastructure/ErrorHandler.kt`  
Receives errors from ViewModels and shows Snackbar messages. Handles 401 responses by navigating to `Navigation.Login` via `navigateFresh`.

**Platform abstraction** — `infrastructure/Platform.kt`  
`expect`/`actual` for anything that differs per platform beyond Preferences (e.g. HTTP engine selection: CIO on Android/JVM, JS engine on Web).

### Data model

Data objects in `data_objects/` are `@Serializable` Kotlin data classes. The Ktor client uses Kotlinx Serialization JSON with a `snake_case` naming strategy, so field names in requests/responses use snake_case while Kotlin properties use camelCase.