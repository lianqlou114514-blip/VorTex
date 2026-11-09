# VorTex — Estudio de UI flotante para Android

**Un prototipo de interfaz Android en Java puro con un menú flotante dirigido por DSL.**

VorTex es un estudio de interfaz para Android construido por completo en Java, sin layouts XML. Incluye una pantalla de acceso, un inicio con dos pestañas, un gestor de recursos simulado, ajustes y una bola flotante que se transforma en un panel completo. El lenguaje visual se inspira en los paneles flotantes de estilo Bingxi (冰昔).

Languages / 语言 / 言語 / 언어 / Idiomas / Langues / Sprachen / Языки / Idiomas: [English](README.md) · [简体中文](README.zh-CN.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · **Español** · [Français](README.fr.md) · [Deutsch](README.de.md) · [Русский](README.ru.md) · [Português (Brasil)](README.pt-BR.md)

> VorTex es un prototipo de interfaz, no una herramienta funcional para juegos. Los interruptores solo escriben en SharedPreferences, las barras de descarga se simulan con un temporizador y ningún código lee o inyecta otras aplicaciones o juegos.

## Highlights

- UI 100% en código — las vistas se crean en Java; res/ solo guarda iconos, estilos, animadores y dos cadenas.
- Transformación de la bola — una esfera de 39dp se expande a un panel de 357×263dp en 300ms, con arrastre, inclinación y pellizco.
- Menú por DSL — DslConfig.DSL_TEXT describe páginas, pestañas, tarjetas, interruptores, barras y botones en un DSL compacto, interpretado en 12 categorías.
- Sistema de movimiento — indicador deslizante inferior, cambio de pestaña por gesto, transiciones de Fragment, burbujas de atajo y notificaciones en pantalla.
- Persistencia — SharedPreferences guarda acceso, interruptores, valores de barras, última pestaña y progreso de descarga simulado.
- Cero dependencias — sin AndroidX, sin librerías de terceros, sin llamadas de red en ejecución.

## Modules

| Class | What it renders |
|---|---|
| `MainActivity` | Acceso / desvinculación, visibilidad de contraseña e iconos de ojo dibujados con Canvas. |
| `HomeActivity` | Contenedor de dos pestañas con deslizador azul animado y gestos. |
| `HomeFragment` | Tarjeta de avisos, lanzador de ventana flotante, acceso a recursos. |
| `ResourceFragment` | Pestañas de categoría, tarjetas, progreso de descarga simulado, aplicar/desinstalar y diálogos. |
| `SettingsFragment` | Ajustes agrupados, switches teñidos y filas RippleDrawable. |
| `FloatBallService` | Motor de superposición de 1.600 líneas: animación, parser DSL, 12 categorías, atajos y notificaciones. |

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

Descarga el APK firmado, instálalo en Android 8.0 o superior y concede el permiso de superposición desde la tarjeta de inicio.

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

A static, search-engine-friendly site with `hreflang` alternates lives in [`docs/`](docs/): [English](https://lianqlou114514-blip.github.io/VorTex/index.html) · [简体中文](https://lianqlou114514-blip.github.io/VorTex/zh-CN.html) · [日本語](https://lianqlou114514-blip.github.io/VorTex/ja.html) · [한국어](https://lianqlou114514-blip.github.io/VorTex/ko.html) · [Français](https://lianqlou114514-blip.github.io/VorTex/fr.html) · [Deutsch](https://lianqlou114514-blip.github.io/VorTex/de.html) · [Русский](https://lianqlou114514-blip.github.io/VorTex/ru.html) · [Português (Brasil)](https://lianqlou114514-blip.github.io/VorTex/pt-BR.html).

## Keywords

clon UI Android, menú flotante, estilo Bingxi, Java puro, sin layout XML, UI por DSL, interfaz de asistente de juego, bola flotante, VorTex

---

VorTex is an independent UI study. It is not affiliated with, nor derived from the code of, any commercial assistant product. All product names belong to their respective owners.
