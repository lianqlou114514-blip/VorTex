# VorTex — Android-Floating-Panel-UI-Studie

**Ein reines Java-Android-UI-Protoyp mit DSL-gesteuertem Floating-Menü.**

VorTex ist eine Android-Oberflächenstudie, die vollständig in Java ohne XML-Layouts entsteht. Sie umfasst einen Anmeldebildschirm, einen Startbereich mit zwei Tabs, einen simulierten Ressourcenmanager, Einstellungen und eine Schwebe-Kugel, die sich zu einem vollständigen Bedienfeld aufklappt. Die Formsprache orientiert sich an schwebenden Panels im Bingxi-Stil (冰昔).

Languages / 语言 / 言語 / 언어 / Idiomas / Langues / Sprachen / Языки / Idiomas: [English](README.md) · [简体中文](README.zh-CN.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Español](README.es.md) · [Français](README.fr.md) · **Deutsch** · [Русский](README.ru.md) · [Português (Brasil)](README.pt-BR.md)

> VorTex ist ein UI-Prototyp, kein funktionierendes Spielwerkzeug. Schalter schreiben nur in SharedPreferences, Fortschrittsbalken sind per Timer simuliert, und kein Code liest oder injiziert andere Apps oder Spiele.

## Highlights

- UI zu 100 % im Code — Views werden in Java erzeugt; res/ enthält nur Icons, Styles, Animatoren und zwei Strings.
- Kugel-Morphing — eine 39dp-Kugel wird in 300 ms zu einem 357×263dp-Panel, mit Ziehen, Neigen und Pinch.
- DSL-gesteuertes Menü — DslConfig.DSL_TEXT beschreibt Seiten, Tabs, Karten, Schalter, Regler und Buttons und wird zur Laufzeit in 12 Kategorien übersetzt.
- Bewegungssystem — gleitender Nav-Indikator, Wisch-Tab-Wechsel, Fragment-Übergänge, Shortcut-Blasen und Bildschirm-Benachrichtigungen.
- Zustandsspeicherung — SharedPreferences hält Anmeldung, Schalter, Reglerwerte, letzten Tab und simulierten Download-Fortschritt.
- Null Abhängigkeiten — kein AndroidX, keine Fremdbibliotheken, keine Netzwerkaufrufe zur Laufzeit.

## Modules

| Class | What it renders |
|---|---|
| `MainActivity` | Anmeldung / Entbindung, Passwort-Sichtbarkeit, mit Canvas gezeichnete Augen-Icons. |
| `HomeActivity` | Zwei-Tab-Host mit animiertem blauem Schieber und Wischgesten. |
| `HomeFragment` | Ankündigungskarte, Start des Floating-Fensters, Einstieg in die Ressourcenverwaltung. |
| `ResourceFragment` | Kategorie-Tabs, Ressourcenkarten, simulierter Fortschritt, Anwenden/Entfernen, Detaildialoge. |
| `SettingsFragment` | Gruppierte Einstellungen, getönte Switches, RippleDrawable-Zeilen. |
| `FloatBallService` | Overlay-Engine mit 1.600 Zeilen: Morphing, DSL-Parser, 12 Kategorien, Shortcuts, Benachrichtigungen. |

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

Signierte APK herunterladen, auf Android 8.0 oder neuer installieren und die Overlay-Berechtigung über die Startkarte erteilen.

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

A static, search-engine-friendly site with `hreflang` alternates lives in [`docs/`](docs/): [English](https://lianqlou114514-blip.github.io/VorTex/index.html) · [简体中文](https://lianqlou114514-blip.github.io/VorTex/zh-CN.html) · [日本語](https://lianqlou114514-blip.github.io/VorTex/ja.html) · [한국어](https://lianqlou114514-blip.github.io/VorTex/ko.html) · [Español](https://lianqlou114514-blip.github.io/VorTex/es.html) · [Français](https://lianqlou114514-blip.github.io/VorTex/fr.html) · [Русский](https://lianqlou114514-blip.github.io/VorTex/ru.html) · [Português (Brasil)](https://lianqlou114514-blip.github.io/VorTex/pt-BR.html).

## Keywords

Android UI Klon, Floating-Menü, Bingxi-Stil, reines Java, kein XML-Layout, DSL-UI, Spiel-Assistent-Oberfläche, Schwebe-Kugel, VorTex

---

VorTex is an independent UI study. It is not affiliated with, nor derived from the code of, any commercial assistant product. All product names belong to their respective owners.
