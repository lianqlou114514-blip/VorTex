# VorTex — Android フローティングパネル UI 習作

**DSL 駆動のフローティングメニューを備えた、純 Java 製 Android UI プロトタイプ。**

VorTex は XML レイアウトを一切使わず、すべて Java コードで画面を組み立てる Android の UI 習作です。ログイン画面、2 タブのホーム、模擬リソース管理、設定画面、そしてパネルに変形するフローティングボールで構成されます。氷昔（Bingxi）系のフローティング補助ツールのパネル配置を参考にしています。

Languages / 语言 / 言語 / 언어 / Idiomas / Langues / Sprachen / Языки / Idiomas: [English](README.md) · [简体中文](README.zh-CN.md) · **日本語** · [한국어](README.ko.md) · [Español](README.es.md) · [Français](README.fr.md) · [Deutsch](README.de.md) · [Русский](README.ru.md) · [Português (Brasil)](README.pt-BR.md)

> VorTex は UI プロトタイプであり、実際に動作するゲームツールではありません。スイッチは SharedPreferences に書き込むだけ、ダウンロードバーはタイマーによる模擬で、他のアプリやゲームを読み取ったり注入したりするコードはありません。

## Highlights

- UI は 100% コード生成 —— View は Java で生成し、res/ はアイコン・スタイル・アニメーター・文字列 2 件のみ。
- フローティングボールの変形 —— 39dp の球が 300ms で 357×263dp のパネルへ。ドラッグ・傾き・ピンチ対応。
- DSL 駆動メニュー —— DslConfig.DSL_TEXT がページ／タブ／カード／スイッチ／シークバー／ボタンを記述し、実行時に 12 分類へ解釈。
- モーション —— 下部ナビのスライド指標、スワイプ切替、Fragment トランジション、ショートカットバブル、画面通知。
- 状態保存 —— SharedPreferences がログイン状態・スイッチ・シークバー値・最終タブ・模擬ダウンロード進捗を保持。
- 依存ゼロ —— AndroidX なし、外部ライブラリなし、実行時のネットワーク通信なし。

## Modules

| Class | What it renders |
|---|---|
| `MainActivity` | ログイン／解除画面。パスワード表示切替と Canvas 描画の目アイコン。 |
| `HomeActivity` | 青いスライダーとスワイプ操作を備えた 2 タブホスト。 |
| `HomeFragment` | お知らせカード、フローティングウィンドウ起動、リソース管理への入口。 |
| `ResourceFragment` | カテゴリタブ、リソースカード、模擬ダウンロード進捗、適用／削除、詳細ダイアログ。 |
| `SettingsFragment` | グループ設定、着色 Switch、RippleDrawable の行。 |
| `FloatBallService` | 約 1,600 行のオーバーレイエンジン。変形アニメ、DSL 解析、12 分類、ショートカット、通知。 |

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

署名済み APK を Android 8.0 以降にインストールし、ホーム画面のカードから重ねて表示の権限を付与してください。

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

A static, search-engine-friendly site with `hreflang` alternates lives in [`docs/`](docs/): [English](https://lianqlou114514-blip.github.io/VorTex/index.html) · [简体中文](https://lianqlou114514-blip.github.io/VorTex/zh-CN.html) · [한국어](https://lianqlou114514-blip.github.io/VorTex/ko.html) · [Español](https://lianqlou114514-blip.github.io/VorTex/es.html) · [Français](https://lianqlou114514-blip.github.io/VorTex/fr.html) · [Deutsch](https://lianqlou114514-blip.github.io/VorTex/de.html) · [Русский](https://lianqlou114514-blip.github.io/VorTex/ru.html) · [Português (Brasil)](https://lianqlou114514-blip.github.io/VorTex/pt-BR.html).

## Keywords

Android UI クローン, フローティングメニュー, 氷昔風, 純 Java, XML レイアウトなし, DSL 駆動 UI, ゲーム補助インターフェース, フローティングボール, VorTex

---

VorTex is an independent UI study. It is not affiliated with, nor derived from the code of, any commercial assistant product. All product names belong to their respective owners.
