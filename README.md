# VorTex — Android Floating-Panel UI Study

**A pure-Java Android UI prototype with a DSL-driven floating menu.**

VorTex is an Android interface study built entirely in Java. Every screen is constructed in code instead of XML layouts: a login page, a two-tab home, a mock resource manager, a settings page, and a floating ball that morphs into a full menu panel. The visual language is inspired by the Bingxi-style (冰昔) floating assistant panels.

Languages / 语言 / 言語 / 언어 / Idiomas / Langues / Sprachen / Языки / Idiomas: [English](README.md) · [简体中文](README.zh-CN.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Español](README.es.md) · [Français](README.fr.md) · [Deutsch](README.de.md) · [Русский](README.ru.md) · [Português (Brasil)](README.pt-BR.md)

> VorTex is a UI prototype, not a functional game tool. Switches write to SharedPreferences, download bars are simulated with a timer, and no code reads from or injects into any other app or game.

## Highlights

- 100% code-built UI — Views are instantiated in Java; res/ holds only icons, styles, animators and two strings.
- Floating ball morph — a 39dp ball expands into a 357×263dp panel in 300ms with ValueAnimator, drag, tilt and pinch.
- DSL-driven menu — DslConfig.DSL_TEXT describes pages, tabs, cards, switches, seekbars and buttons in a compact Chinese DSL, parsed at runtime into 12 menu categories.
- Motion system — sliding bottom-nav indicator, swipe-to-switch tabs, fragment transition animators, shortcut bubbles and on-screen notifications.
- State persistence — SharedPreferences keeps login state, toggle states, seekbar values, last tab and mock download progress.
- Zero dependencies — no AndroidX, no third-party libraries, no runtime network calls.

## Modules

| Class | What it renders |
|---|---|
| `MainActivity` | Login / unbind screen, password visibility toggle with Canvas-drawn eye icons. |
| `HomeActivity` | Two-tab host with an animated blue slider and swipe gestures. |
| `HomeFragment` | Notice card, floating-window launcher, resource manager entry. |
| `ResourceFragment` | Category tabs, resource cards, simulated download progress, apply/uninstall and detail dialogs. |
| `SettingsFragment` | Grouped settings, tinted switches, RippleDrawable rows. |
| `FloatBallService` | The 1,600-line overlay engine: morph animation, DSL parser, 12 categories, shortcuts, notifications. |

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

Download the signed APK, install it on Android 8.0 or newer, and grant the overlay permission from the home card.

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

A static, search-engine-friendly site with `hreflang` alternates lives in [`docs/`](docs/): [简体中文](https://lianqlou114514-blip.github.io/VorTex/zh-CN.html) · [日本語](https://lianqlou114514-blip.github.io/VorTex/ja.html) · [한국어](https://lianqlou114514-blip.github.io/VorTex/ko.html) · [Español](https://lianqlou114514-blip.github.io/VorTex/es.html) · [Français](https://lianqlou114514-blip.github.io/VorTex/fr.html) · [Deutsch](https://lianqlou114514-blip.github.io/VorTex/de.html) · [Русский](https://lianqlou114514-blip.github.io/VorTex/ru.html) · [Português (Brasil)](https://lianqlou114514-blip.github.io/VorTex/pt-BR.html).

## Keywords

Android UI clone, floating overlay menu, Bingxi style, pure Java Android, no XML layout, DSL driven UI, game assistant interface, floating ball, AIDE project, VorTex

---

VorTex is an independent UI study. It is not affiliated with, nor derived from the code of, any commercial assistant product. All product names belong to their respective owners.
