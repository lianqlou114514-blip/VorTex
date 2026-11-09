# VorTex — исследование плавающего интерфейса Android

**Прототип интерфейса Android на чистом Java с плавающим меню на основе DSL.**

VorTex — это исследование интерфейса Android, полностью собранное на Java без XML-разметки. В него входят экран входа, главный экран с двумя вкладками, имитация менеджера ресурсов, настройки и плавающий шарик, раскрывающийся в полноценную панель. Визуальный язык вдохновлён плавающими панелями в стиле Bingxi (冰昔).

Languages / 语言 / 言語 / 언어 / Idiomas / Langues / Sprachen / Языки / Idiomas: [English](README.md) · [简体中文](README.zh-CN.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Español](README.es.md) · [Français](README.fr.md) · [Deutsch](README.de.md) · **Русский** · [Português (Brasil)](README.pt-BR.md)

> VorTex — прототип интерфейса, а не рабочий игровой инструмент. Переключатели лишь пишут в SharedPreferences, прогресс загрузки имитируется таймером, и ни один фрагмент кода не читает и не изменяет другие приложения или игры.

## Highlights

- Интерфейс на 100 % в коде — View создаются в Java; в res/ только иконки, стили, аниматоры и две строки.
- Трансформация шарика — шар 39dp за 300 мс становится панелью 357×263dp, с перетаскиванием, наклоном и жестом сжатия.
- Меню на DSL — DslConfig.DSL_TEXT описывает страницы, вкладки, карточки, переключатели, ползунки и кнопки, превращаясь в 12 разделов.
- Система движения — скользящий индикатор навигации, свайп между вкладками, переходы Fragment, всплывающие ярлыки и уведомления на экране.
- Сохранение состояния — SharedPreferences хранит вход, переключатели, значения ползунков, последнюю вкладку и имитацию прогресса загрузки.
- Ноль зависимостей — без AndroidX, без сторонних библиотек, без сетевых вызовов во время работы.

## Modules

| Class | What it renders |
|---|---|
| `MainActivity` | Вход / отвязка, переключение видимости пароля, иконки глаза на Canvas. |
| `HomeActivity` | Контейнер на две вкладки с анимированным синим ползунком и свайпами. |
| `HomeFragment` | Карточка объявления, запуск плавающего окна, вход в ресурсы. |
| `ResourceFragment` | Вкладки категорий, карточки, имитация загрузки, применить/удалить, диалоги. |
| `SettingsFragment` | Группы настроек, окрашенные Switch, строки RippleDrawable. |
| `FloatBallService` | Overlay-движок на 1 600 строк: морфинг, парсер DSL, 12 разделов, ярлыки, уведомления. |

## Specification

| Item | Value |
|---|---|
| Package | `com.vortex.tool` |
| Language | Java 17 (`record`, `var`, arrow `switch`) |
| minSdk / targetSdk | 26 (Android 8.0) / 34 |
| Built against | Android SDK platform 35, build-tools 35.0.0 |
| Layouts | None — all views created in code |
| Dependencies | None (no AndroidX, no Gradle plugin modules) |
| Signing | APK Signature Scheme v2 + v3 |
| APK SHA-256 | `c484a40fa3eb79e3aaa1f5ef73253b06cafb5eaa02ec379e8fbbe0a798febcb1` |

## Download

Скачайте подписанный APK, установите на Android 8.0+ и выдайте разрешение на наложение через карточку на главном экране.

- APK: [`releases/VorTex-1.0.apk`](releases/VorTex-1.0.apk) · 6.0 MB · signed
- Source: this repository, exactly as authored (AIDE+ project layout)

## Build

The project is an AIDE+ style Gradle project. A dependency-free reproducible build is provided in [`tools/build-apk.sh`](tools/build-apk.sh) — it uses `aapt2`, `javac`, `d8`, `zipalign` and `apksigner` from any Android SDK with platform 35 + build-tools 35.0.0 and a JDK 17:

```bash
export ANDROID_HOME=/path/to/android-sdk
bash tools/build-apk.sh
```

## Permissions

`SYSTEM_ALERT_WINDOW` for the floating ball, `FOREGROUND_SERVICE` for the overlay on Android 9+, `INTERNET`/`ACCESS_NETWORK_STATE` declared by the template but unused, storage permissions for the AIDE+ template. None are used to contact a server.

## Multilingual site

A static, search-engine-friendly site with `hreflang` alternates lives in [`docs/`](docs/): [English](https://lianqlou114514-blip.github.io/VorTex/index.html) · [简体中文](https://lianqlou114514-blip.github.io/VorTex/zh-CN.html) · [日本語](https://lianqlou114514-blip.github.io/VorTex/ja.html) · [한국어](https://lianqlou114514-blip.github.io/VorTex/ko.html) · [Español](https://lianqlou114514-blip.github.io/VorTex/es.html) · [Français](https://lianqlou114514-blip.github.io/VorTex/fr.html) · [Deutsch](https://lianqlou114514-blip.github.io/VorTex/de.html) · [Português (Brasil)](https://lianqlou114514-blip.github.io/VorTex/pt-BR.html).

## Keywords

клон UI Android, плавающее меню, стиль Bingxi, чистый Java, без XML-разметки, DSL-интерфейс, интерфейс игрового помощника, плавающий шар, VorTex

---

VorTex is an independent UI study. It is not affiliated with, nor derived from the code of, any commercial assistant product. All product names belong to their respective owners.
