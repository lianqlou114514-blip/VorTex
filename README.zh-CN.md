# VorTex — 安卓悬浮面板 UI 习作

**一个纯 Java 编写、由 DSL 驱动的安卓悬浮菜单界面原型。**

VorTex 是一个完全用 Java 代码搭建界面的 Android UI 项目，不使用 XML 布局。它包含登录页、双标签主页、模拟资源管理器、设置页，以及一枚可形变为完整功能面板的悬浮球。界面风格仿照冰昔类悬浮辅助工具的面板布局。

Languages / 语言 / 言語 / 언어 / Idiomas / Langues / Sprachen / Языки / Idiomas: [English](README.md) · **简体中文** · [日本語](README.ja.md) · [한국어](README.ko.md) · [Español](README.es.md) · [Français](README.fr.md) · [Deutsch](README.de.md) · [Русский](README.ru.md) · [Português (Brasil)](README.pt-BR.md)

> VorTex 是界面原型，不是可用的游戏工具。开关只写入 SharedPreferences，下载进度由定时器模拟，代码不会读取或注入任何其他应用与游戏。

## Highlights

- 全代码构建 UI —— 所有 View 在 Java 中实例化，res/ 只放图标、样式、动画和两个字符串。
- 悬浮球形变 —— 39dp 小球在 300ms 内展开为 357×263dp 面板，支持拖拽、倾斜与捏合动画。
- DSL 驱动菜单 —— DslConfig.DSL_TEXT 用一段简洁的中文 DSL 描述分页、标签、卡片、开关、拉条和按钮，运行时解析为 12 个分类。
- 动效体系 —— 底部导航滑块、左右滑动切页、Fragment 转场、快捷气泡与屏幕通知。
- 状态持久化 —— SharedPreferences 保存登录态、开关状态、拉条值、上次分页与模拟下载进度。
- 零依赖 —— 不用 AndroidX、不引第三方库、运行时不发起网络请求。

## Modules

| Class | What it renders |
|---|---|
| `MainActivity` | 登录 / 解绑页，密码可见性切换，眼睛图标由 Canvas 绘制。 |
| `HomeActivity` | 双标签容器，蓝色滑块动画与滑动切页手势。 |
| `HomeFragment` | 公告卡片、悬浮窗启动入口、资源管理入口。 |
| `ResourceFragment` | 分类标签、资源卡片、模拟下载进度、应用/卸载与详情弹窗。 |
| `SettingsFragment` | 分组设置项、着色 Switch、RippleDrawable 行。 |
| `FloatBallService` | 一千六百行的悬浮窗引擎：形变动画、DSL 解析、12 个分类、快捷键与通知。 |

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

下载已签名 APK，在 Android 8.0 及以上安装，并在首页卡片中授予悬浮窗权限。

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

A static, search-engine-friendly site with `hreflang` alternates lives in [`docs/`](docs/): [English](https://lianqlou114514-blip.github.io/VorTex/index.html) · [日本語](https://lianqlou114514-blip.github.io/VorTex/ja.html) · [한국어](https://lianqlou114514-blip.github.io/VorTex/ko.html) · [Español](https://lianqlou114514-blip.github.io/VorTex/es.html) · [Français](https://lianqlou114514-blip.github.io/VorTex/fr.html) · [Deutsch](https://lianqlou114514-blip.github.io/VorTex/de.html) · [Русский](https://lianqlou114514-blip.github.io/VorTex/ru.html) · [Português (Brasil)](https://lianqlou114514-blip.github.io/VorTex/pt-BR.html).

## Keywords

安卓 UI 仿写, 悬浮窗菜单, 冰昔风格, 纯 Java 安卓, 无 XML 布局, DSL 界面, 游戏辅助界面, 悬浮球, AIDE 工程, VorTex

---

VorTex is an independent UI study. It is not affiliated with, nor derived from the code of, any commercial assistant product. All product names belong to their respective owners.
