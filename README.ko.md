# VorTex — 안드로이드 플로팅 패널 UI 연구

**DSL 기반 플로팅 메뉴를 담은 순수 Java 안드로이드 UI 프로토타입.**

VorTex는 XML 레이아웃 없이 모든 화면을 Java 코드로 구성한 안드로이드 UI 프로젝트입니다. 로그인 화면, 두 개의 탭 홈, 모의 리소스 관리자, 설정 화면, 그리고 전체 패널로 변형되는 플로팅 볼로 이루어져 있습니다. 빙시(Bingxi) 계열 플로팅 도구의 패널 구성을 참고했습니다.

Languages / 语言 / 言語 / 언어 / Idiomas / Langues / Sprachen / Языки / Idiomas: [English](README.md) · [简体中文](README.zh-CN.md) · [日本語](README.ja.md) · **한국어** · [Español](README.es.md) · [Français](README.fr.md) · [Deutsch](README.de.md) · [Русский](README.ru.md) · [Português (Brasil)](README.pt-BR.md)

> VorTex는 UI 프로토타입이며 실제로 동작하는 게임 도구가 아닙니다. 스위치는 SharedPreferences에 기록될 뿐이고 다운로드 바는 타이머로模拟되며, 다른 앱이나 게임을 읽거나 주입하는 코드는 없습니다.

## Highlights

- UI 100% 코드 생성 —— View를 Java에서 만들고 res/에는 아이콘, 스타일, 애니메이터, 문자열 두 개만 있습니다.
- 플로팅 볼 변형 —— 39dp 공이 300ms 만에 357×263dp 패널로 확장되며 드래그·기울임·핀치를 지원합니다.
- DSL 기반 메뉴 —— DslConfig.DSL_TEXT가 페이지·탭·카드·스위치·시크바·버튼을 서술하고 런타임에 12개 분류로 해석됩니다.
- 모션 시스템 —— 하단 내비 슬라이더, 스와이프 탭 전환, Fragment 전환 애니메이션, 단축 버블, 화면 알림.
- 상태 저장 —— SharedPreferences가 로그인 상태, 스위치, 시크바 값, 마지막 탭, 모의 다운로드 진행률을 보관합니다.
- 의존성 없음 —— AndroidX 없음, 서드파티 라이브러리 없음, 런타임 네트워크 호출 없음.

## Modules

| Class | What it renders |
|---|---|
| `MainActivity` | 로그인 / 해제 화면, 비밀번호 표시 전환, Canvas로 그린 눈 아이콘. |
| `HomeActivity` | 파란 슬라이더와 스와이프 제스처를 가진 2탭 호스트. |
| `HomeFragment` | 공지 카드, 플로팅 창 실행, 리소스 관리 진입점. |
| `ResourceFragment` | 카테고리 탭, 리소스 카드, 모의 다운로드 진행률, 적용/제거, 상세 다이얼로그. |
| `SettingsFragment` | 그룹 설정, 틴트 Switch, RippleDrawable 행. |
| `FloatBallService` | 약 1,600줄의 오버레이 엔진. 변형 애니메이션, DSL 파서, 12개 분류, 단축키, 알림. |

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

서명된 APK를 Android 8.0 이상에 설치하고 홈 카드에서 오버레이 권한을 허용하세요.

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

A static, search-engine-friendly site with `hreflang` alternates lives in [`docs/`](docs/): [English](https://lianqlou114514-blip.github.io/VorTex/index.html) · [简体中文](https://lianqlou114514-blip.github.io/VorTex/zh-CN.html) · [日本語](https://lianqlou114514-blip.github.io/VorTex/ja.html) · [Español](https://lianqlou114514-blip.github.io/VorTex/es.html) · [Français](https://lianqlou114514-blip.github.io/VorTex/fr.html) · [Deutsch](https://lianqlou114514-blip.github.io/VorTex/de.html) · [Русский](https://lianqlou114514-blip.github.io/VorTex/ru.html) · [Português (Brasil)](https://lianqlou114514-blip.github.io/VorTex/pt-BR.html).

## Keywords

안드로이드 UI 클론, 플로팅 메뉴, 빙시 스타일, 순수 Java, XML 레이아웃 없음, DSL UI, 게임 보조 인터페이스, 플로팅 볼, VorTex

---

VorTex is an independent UI study. It is not affiliated with, nor derived from the code of, any commercial assistant product. All product names belong to their respective owners.
