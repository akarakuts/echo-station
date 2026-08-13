# Echo Station

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)

Russian / Русский: [README.ru.md](README.ru.md)

**Echo Station** — a quiet narrative puzzle for Android. Catch signal frames on mothballed shortwave post Orion-7, repair the stuck voice, and finish a 1994 letter. **Kotlin**, **Jetpack Compose**, **Navigation Compose**, **DataStore**.

## Features

- **80 levels** in 3 acts (Noise / Name / Letter).
- **Puzzle types** — Wave, Cable, Cassette, Frequency, Multi finale.
- **Story cards** after each frame (log / voice / photo / letter).
- **Archive** of collected fragments.
- **Three epilogue tones** (broadcast / archive / leave on frequency).
- **Quality of life** — optional sound, station ambience, haptics, reduce motion; local display name; in-app privacy note.
- **Persistence** — progress, archive, marks, and settings via **DataStore** (on-device only).
- **Offline** — no ads, IAP, accounts, or analytics in this version.
- **Locales** — UI and story follow the **system language** (23 locales: en, ru, de, fr, es, pt, pt-BR, it, pl, uk, tr, ja, ko, zh-CN, zh-TW, ar, hi, id, vi, th, nl, sv, cs). Unsupported system languages fall back to English for story text.

## Android stack

| Area | Choice |
|------|--------|
| UI | Compose Material 3 |
| Navigation | Navigation Compose |
| State | ViewModel, Lifecycle Compose |
| Async | Kotlin Coroutines (Android) |
| Preferences / progress | DataStore Preferences |
| Content | `assets/levels`, `assets/story` JSON |

See `app/build.gradle.kts` for versions and the full dependency list.

## Requirements

- **JDK 11+**
- **Android SDK** with compile SDK **36** (minor 1 as in the project); **minSdk 24**, **targetSdk 36**
- **Android Studio** Ladybug+ or Gradle **8+/9+** via `./gradlew`

## CI & automation

| Workflow | Trigger | Purpose |
|----------|---------|---------|
| [CI](.github/workflows/ci.yml) | push / PR to `main`, manual | `:app:check` (unit tests, Lint, compile) |
| [Security](.github/workflows/security.yml) | push / PR to `main`, weekly | OSV dependency scan, CodeQL |
| [Release](.github/workflows/release.yml) | tag `v*` | Upload-keystore–signed **APK + AAB** + GitHub Release (requires secrets) |

[Dependabot](.github/dependabot.yml) opens weekly PRs for Gradle and GitHub Actions dependencies.

## Build & run

```bash
./gradlew :app:assembleDebug
./gradlew :app:installDebug
```

Open the `app` run configuration in Android Studio and deploy to a device or emulator. For **signed release** builds, see [Release signing](#release-signing).

**Launcher icons:** adaptive layers in `app/src/main/res/drawable/ic_launcher_*.xml`; run `.venv-icon/bin/python scripts/generate_launcher_icons.py` (Pillow in `.venv-icon`) to refresh all `mipmap-*/ic_launcher*.webp`.

## Release signing

`app/build.gradle.kts` loads **`keystore.properties`** from the repo root; if it exists, **`signingConfigs.upload`** is applied to **`release`**; otherwise **`release`** uses the **debug** keystore so fresh clones and CI still build installable APKs.

### 1. Create an upload keystore (once)

```bash
keytool -genkeypair -v \
  -keystore upload-keystore.jks \
  -alias upload \
  -keyalg RSA -keysize 2048 -validity 10000
```

Keep **`upload-keystore.jks`** and passwords in a password manager; **back up** the file — without it you cannot ship compatible updates.

### 2. Local signed `release` builds

1. Copy [`keystore.properties.example`](keystore.properties.example) to **`keystore.properties`** in the **repository root** (this file is gitignored).
2. Set `storeFile`, passwords, and `keyAlias` to match your keystore.
3. Run:

```bash
./gradlew :app:assembleRelease :app:bundleRelease
```

Outputs: `app/build/outputs/apk/release/*.apk` and `app/build/outputs/bundle/release/*.aab`.

If **`keystore.properties` is missing**, `release` still signs with the **debug** keystore so the project builds on fresh clones — **do not** publish that build to an app store.

### 3. GitHub Actions tag releases (`v*`)

Configure these **repository secrets** (Settings → Secrets and variables → Actions):

| Secret | Value |
|--------|-------|
| `RELEASE_KEYSTORE_BASE64` | Base64 of `upload-keystore.jks` (e.g. `base64 -i upload-keystore.jks \| tr -d '\n'` on macOS) |
| `RELEASE_STORE_PASSWORD` | Keystore password |
| `RELEASE_KEY_ALIAS` | Key alias (e.g. `upload`) |
| `RELEASE_KEY_PASSWORD` | Key password |

The [Release](.github/workflows/release.yml) workflow writes `keystore.properties` and `upload-keystore.jks` on the runner, then runs **`assembleRelease`** and **`bundleRelease`**, and attaches **`echostation-<tag>.apk`** and **`.aab`** to the GitHub Release. If any secret is missing, the workflow **fails** with an error message (no silent debug-signed store builds).

## GitHub Releases

Tagged pushes (`v*`) run the Release workflow: **APK + AAB** signed with your **upload keystore** from GitHub secrets. Without secrets the workflow fails on purpose (see table above).

## Project layout

| Path | Role |
|------|------|
| `app/.../puzzle/` | Wave, Cable, Cassette, Frequency, Multi engines |
| `app/.../story/` | Content parser, models, system-locale story language |
| `app/.../data/` | `ProgressRepository` — DataStore progress and settings |
| `app/.../audio/` | Station hum, solve/relay cues, haptics |
| `app/.../ui/EchoStationApp.kt` | Navigation Compose |
| `app/.../ui/Screens.kt` | Home, hub, story, archive, settings, epilogue |
| `app/.../ui/PuzzleViewModel.kt` | Puzzle session state |
| `app/src/main/assets/` | `levels/levels.json`, `story/rewards.json` |

## Testing

```bash
./gradlew :app:check
./scripts/check_strings_parity.sh   # string keys across all values-*
./gradlew :app:connectedDebugAndroidTest   # Compose UI tests (device / emulator)
```

| Suite | Location | Coverage |
|-------|----------|----------|
| Engines | `app/src/test/.../PuzzleEngineTest.kt` | Wave, cable, cassette, frequency, multi |
| Content | `app/src/test/.../ContentIntegrityTest.kt` | 80 levels, reward ids, en/ru/uk titles |
| Compose UI | `app/src/androidTest/.../HomeScreenComposeTest.kt` | Home, settings, archive, hub smoke |

Robolectric unit tests need **JDK 21** (same as CI).

## Scripts

| Script | Purpose |
|--------|---------|
| `scripts/build_release.sh` | Signed APK/AAB → path from `store-upload.dir` (see `store-upload.dir.example`) |
| `scripts/check_strings_parity.sh` | Verify string keys across all `values*` |
| `scripts/generate_content.py` | Regenerate level/reward JSON |
| `scripts/generate_i18n.py` | Regenerate UI translations |
| `scripts/generate_launcher_icons.py` | Regenerate launcher webp |
| `scripts/generate_sfx.py` | Regenerate short UI sounds |

## Contact

**Aleksey Karakuts** — [aleksey@karakuts.com](mailto:aleksey@karakuts.com)

## License

This program is free software: you can redistribute it and/or modify it under the terms of the **GNU General Public License** as published by the Free Software Foundation, either **version 3** of the License, or (at your option) any later version.

See the [`LICENSE`](LICENSE) file for the full GPLv3 text.

Copyright (C) 2026 Aleksey Karakuts &lt;aleksey@karakuts.com&gt;
