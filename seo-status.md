# VorTex 搜索引擎推送结果

- 时间: 2026-11-12 03:51:11 UTC
- 运行: https://github.com/lianqlou114514-blip/VorTex/actions/runs/43150918701
- 提交页面数: 9
- IndexNow key: `c4e91a7f2b6d48305e9a1c7f35b8d2064e` → `https://lianqlou114514-blip.github.io/VorTex/c4e91a7f2b6d48305e9a1c7f35b8d2064e.txt`

## 各端点响应

| 端点 | 方法 | HTTP | 响应体 |
|---|---|---|---|
| `https://api.indexnow.org/indexnow` | POST | 200 |  |
| `https://www.bing.com/indexnow` | POST | 200 |  |
| `https://yandex.com/indexnow` | POST | 422 | {   "success":false,   "invalidUrlList":     [       "https://lianqlou114514-blip.github.io/VorTex/",       "h |
| `https://search.seznam.cz/indexnow` | POST | 200 |  |
| `https://www.google.com/ping?sitemap=https://lianqlou114514-blip.github.io/VorTex/sitemap.xml` | GET | 404 | <HTML> <HEAD> <TITLE>Sitemaps ping is deprecated. See https://developers.google.com/search/blog/2023/06/sitema |
| `https://www.bing.com/ping?sitemap=https://lianqlou114514-blip.github.io/VorTex/sitemap.xml` | GET | 410 |  |
| `key file` | GET | 200 | c4e91a7f2b6d48305e9a1c7f35b8d2064e  |
| `site root` | GET | 200 | <!DOCTYPE html> <html lang="en" dir="ltr"> <head> <meta charset="utf-8"> <meta name="viewport" content="width= |
| `sitemap` | GET | 200 | <?xml version="1.0" encoding="UTF-8"?> <urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"         xml |
| `llms.txt` | GET | 200 | # VorTex  > A pure-Java Android floating-panel UI study (Bingxi / 冰昔 style). No XML layouts, no dependencies.  |

## 提交的 URL

- https://lianqlou114514-blip.github.io/VorTex/
- https://lianqlou114514-blip.github.io/VorTex/zh-CN.html
- https://lianqlou114514-blip.github.io/VorTex/ja.html
- https://lianqlou114514-blip.github.io/VorTex/ko.html
- https://lianqlou114514-blip.github.io/VorTex/es.html
- https://lianqlou114514-blip.github.io/VorTex/fr.html
- https://lianqlou114514-blip.github.io/VorTex/de.html
- https://lianqlou114514-blip.github.io/VorTex/ru.html
- https://lianqlou114514-blip.github.io/VorTex/pt-BR.html

> IndexNow 返回 200 / 202 表示引擎已接收 URL；是否收录、何时收录由各引擎自行调度。
> Google 不接受 IndexNow，仅靠 sitemap ping 与 GitHub 仓库页抓取发现。

## 收录自检

| 引擎 | 查询 | HTTP | 已收录 |
|---|---|---|---|
| Bing | `site:lianqlou114514-blip.github.io/VorTex` | 200 | no |
| Bing 标题 | `site:lianqlou114514-blip.github.io/VorTex` | 200 | no |
| Google | `site:lianqlou114514-blip.github.io/VorTex` | 200 | no |
| DuckDuckGo | `site:lianqlou114514-blip.github.io/VorTex` | 202 | no |
