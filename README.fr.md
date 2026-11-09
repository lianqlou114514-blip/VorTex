# VorTex — Étude d’interface flottante Android

**Un prototype d’interface Android en Java pur, avec un menu flottant piloté par DSL.**

VorTex est une étude d’interface Android entièrement construite en Java, sans layouts XML. Elle réunit un écran de connexion, un accueil à deux onglets, un gestionnaire de ressources simulé, des réglages et une boule flottante qui se déploie en panneau complet. Le langage visuel s’inspire des panneaux flottants de style Bingxi (冰昔).

Languages / 语言 / 言語 / 언어 / Idiomas / Langues / Sprachen / Языки / Idiomas: [English](README.md) · [简体中文](README.zh-CN.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Español](README.es.md) · **Français** · [Deutsch](README.de.md) · [Русский](README.ru.md) · [Português (Brasil)](README.pt-BR.md)

> VorTex est un prototype d’interface, pas un outil de jeu fonctionnel. Les interrupteurs écrivent seulement dans SharedPreferences, les barres de progression sont simulées par un timer et aucun code ne lit ni n’injecte d’autre application ou jeu.

## Highlights

- Interface 100 % code — les vues sont instanciées en Java ; res/ ne contient que icônes, styles, animateurs et deux chaînes.
- Morphing de la boule — une bille de 39dp devient un panneau de 357×263dp en 300 ms, avec glisser, inclinaison et pincement.
- Menu piloté par DSL — DslConfig.DSL_TEXT décrit pages, onglets, cartes, interrupteurs, curseurs et boutons, interprétés en 12 catégories.
- Système de mouvement — curseur de navigation animé, balayage entre onglets, transitions de Fragment, bulles de raccourci et notifications à l’écran.
- Persistance — SharedPreferences conserve connexion, interrupteurs, curseurs, dernier onglet et progression de téléchargement simulée.
- Zéro dépendance — pas d’AndroidX, aucune bibliothèque tierce, aucun appel réseau à l’exécution.

## Modules

| Class | What it renders |
|---|---|
| `MainActivity` | Connexion / dissociation, visibilité du mot de passe, icônes œil dessinées avec Canvas. |
| `HomeActivity` | Hôte à deux onglets avec curseur bleu animé et gestes de balayage. |
| `HomeFragment` | Carte d’annonce, lancement de la fenêtre flottante, accès aux ressources. |
| `ResourceFragment` | Onglets de catégorie, cartes, progression simulée, appliquer/désinstaller, dialogues. |
| `SettingsFragment` | Réglages groupés, switches teintés, lignes RippleDrawable. |
| `FloatBallService` | Moteur d’overlay de 1 600 lignes : morphing, parseur DSL, 12 catégories, raccourcis, notifications. |

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

Téléchargez l’APK signé, installez-le sur Android 8.0 ou plus, puis accordez l’autorisation de superposition depuis la carte d’accueil.

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

A static, search-engine-friendly site with `hreflang` alternates lives in [`docs/`](docs/): [English](https://lianqlou114514-blip.github.io/VorTex/index.html) · [简体中文](https://lianqlou114514-blip.github.io/VorTex/zh-CN.html) · [日本語](https://lianqlou114514-blip.github.io/VorTex/ja.html) · [한국어](https://lianqlou114514-blip.github.io/VorTex/ko.html) · [Español](https://lianqlou114514-blip.github.io/VorTex/es.html) · [Deutsch](https://lianqlou114514-blip.github.io/VorTex/de.html) · [Русский](https://lianqlou114514-blip.github.io/VorTex/ru.html) · [Português (Brasil)](https://lianqlou114514-blip.github.io/VorTex/pt-BR.html).

## Keywords

clone UI Android, menu flottant, style Bingxi, Java pur, sans layout XML, UI pilotée par DSL, interface assistant de jeu, boule flottante, VorTex

---

VorTex is an independent UI study. It is not affiliated with, nor derived from the code of, any commercial assistant product. All product names belong to their respective owners.
