# VorTex — Estudo de UI flutuante para Android

**Um protótipo de interface Android em Java puro com menu flutuante orientado por DSL.**

VorTex é um estudo de interface Android construído inteiramente em Java, sem layouts XML. Reúne uma tela de login, uma home com duas abas, um gerenciador de recursos simulado, configurações e uma bola flutuante que se transforma em um painel completo. A linguagem visual se inspira nos painéis flutuantes estilo Bingxi (冰昔).

Languages / 语言 / 言語 / 언어 / Idiomas / Langues / Sprachen / Языки / Idiomas: [English](README.md) · [简体中文](README.zh-CN.md) · [日本語](README.ja.md) · [한국어](README.ko.md) · [Español](README.es.md) · [Français](README.fr.md) · [Deutsch](README.de.md) · [Русский](README.ru.md) · **Português (Brasil)**

> VorTex é um protótipo de interface, não uma ferramenta funcional para jogos. Os switches apenas gravam em SharedPreferences, as barras de progresso são simuladas por um timer e nenhum código lê ou injeta outros aplicativos ou jogos.

## Highlights

- UI 100% em código — as Views são criadas em Java; res/ guarda apenas ícones, estilos, animadores e duas strings.
- Morfismo da bola — uma esfera de 39dp vira um painel de 357×263dp em 300ms, com arraste, inclinação e pinça.
- Menu orientado por DSL — DslConfig.DSL_TEXT descreve páginas, abas, cartões, switches, barras e botões, interpretados em 12 categorias.
- Sistema de movimento — indicador deslizante inferior, troca de abas por gesto, transições de Fragment, bolhas de atalho e notificações na tela.
- Persistência — SharedPreferences guarda login, switches, valores de barras, última aba e progresso de download simulado.
- Zero dependências — sem AndroidX, sem bibliotecas de terceiros, sem chamadas de rede em execução.

## Modules

| Class | What it renders |
|---|---|
| `MainActivity` | Login / desvincular, visibilidade da senha, ícones de olho desenhados com Canvas. |
| `HomeActivity` | Host de duas abas com deslizador azul animado e gestos. |
| `HomeFragment` | Cartão de avisos, inicializador da janela flutuante, entrada de recursos. |
| `ResourceFragment` | Abas de categoria, cartões, progresso simulado, aplicar/remover, diálogos. |
| `SettingsFragment` | Configurações agrupadas, switches tingidos, linhas RippleDrawable. |
| `FloatBallService` | Motor de overlay de 1.600 linhas: morphing, parser DSL, 12 categorias, atalhos, notificações. |

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

Baixe o APK assinado, instale em Android 8.0 ou superior e conceda a permissão de sobreposição pelo cartão inicial.

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

A static, search-engine-friendly site with `hreflang` alternates lives in [`docs/`](docs/): [English](https://lianqlou114514-blip.github.io/VorTex/index.html) · [简体中文](https://lianqlou114514-blip.github.io/VorTex/zh-CN.html) · [日本語](https://lianqlou114514-blip.github.io/VorTex/ja.html) · [한국어](https://lianqlou114514-blip.github.io/VorTex/ko.html) · [Español](https://lianqlou114514-blip.github.io/VorTex/es.html) · [Français](https://lianqlou114514-blip.github.io/VorTex/fr.html) · [Deutsch](https://lianqlou114514-blip.github.io/VorTex/de.html) · [Русский](https://lianqlou114514-blip.github.io/VorTex/ru.html).

## Keywords

clone de UI Android, menu flutuante, estilo Bingxi, Java puro, sem layout XML, UI por DSL, interface de assistente de jogo, bola flutuante, VorTex

---

VorTex is an independent UI study. It is not affiliated with, nor derived from the code of, any commercial assistant product. All product names belong to their respective owners.
