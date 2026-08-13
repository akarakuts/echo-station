# Эхо станции

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](LICENSE)

English: [README.md](README.md)

**Эхо станции** — тихая сюжетная головоломка для Android. Лови кадры сигнала на законсервированном КВ-посту «Орион-7», чини застрявший голос и дособери письмо 1994 года. Стек: **Kotlin**, **Jetpack Compose**, **Navigation Compose**, сохранение прогресса в **DataStore**.

## Возможности

- **80 уровней** в 3 актах (Шум / Имя / Письмо).
- **Пазлы** — Wave, Cable, Cassette, Frequency, Multi-финал.
- **Лог-карточки** после каждого кадра (журнал / голос / фото / письмо).
- **Архив** собранных фрагментов.
- **Три тона эпилога**.
- **Удобства** — звук, атмосфера станции, вибрация, снижение анимации; локальное имя дежурного.
- **Сохранение** — прогресс, архив, отметки и настройки в **DataStore** на устройстве.
- **Офлайн** — без рекламы, покупок, аккаунтов и аналитики в этой версии.
- **Языки** — UI и сюжет следуют **языку системы** (23 локали). Для сюжета без перевода — английский.

## Требования и сборка

Как в [README.md](README.md): JDK 11+, Android SDK (compile 36, min 24), `./gradlew :app:assembleDebug` / установка через Android Studio. Подпись **release** — в англ. README, раздел [Release signing](README.md#release-signing).

**Иконки лаунчера** — векторы `ic_launcher_background.xml` / `ic_launcher_foreground.xml` и скрипт `scripts/generate_launcher_icons.py` (venv с Pillow: `.venv-icon`).

## CI (GitHub Actions)

Как в англ. README: [CI](.github/workflows/ci.yml) (`:app:check` — юнит-тесты Robolectric, Lint, сборка), [Security](.github/workflows/security.yml) (OSV + CodeQL по расписанию), [Release](.github/workflows/release.yml) по тегу `v*` (подписанные APK/AAB в GitHub Release). Секреты и подпись release — в [README.md](README.md#release-signing). [Dependabot](.github/dependabot.yml) — еженедельные PR по Gradle и Actions.

**Тесты:** движки пазлов и целостность контента — `app/src/test` (Robolectric, JDK 21); Compose — `app/src/androidTest`. Подробнее — раздел Testing в [README.md](README.md#testing).

## Контакты

**Aleksey Karakuts** — [aleksey@karakuts.com](mailto:aleksey@karakuts.com)

## Лицензия

Программа распространяется на условиях **GNU GPLv3** — полный текст в файле [`LICENSE`](LICENSE).

Copyright (C) 2026 Aleksey Karakuts &lt;aleksey@karakuts.com&gt;
