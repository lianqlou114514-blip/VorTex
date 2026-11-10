# language: Python, file: tools/seo_submit.py (deployed to repo as tools/seo_submit.py)
# Runs on a GitHub Actions runner (full egress). Submits URLs, records every response.
import os, json, base64, urllib.request, urllib.error, datetime, time

OWNER, REPO = 'lianqlou114514-blip', 'VorTex'
BASE = f'https://{OWNER}.github.io/{REPO}'
HOST = f'{OWNER}.github.io'
KEY = 'c4e91a7f2b6d48305e9a1c7f35b8d2064e'
PAGES = ['index.html', 'zh-CN.html', 'ja.html', 'ko.html', 'es.html',
         'fr.html', 'de.html', 'ru.html', 'pt-BR.html']
URLS = [f'{BASE}/' if p == 'index.html' else f'{BASE}/{p}' for p in PAGES]
TOKEN = os.environ.get('GH_TOKEN', '')
RUN_URL = os.environ.get('RUN_URL', '')

lines = []

def log(s=''):
    lines.append(s)
    print(s, flush=True)

def call(method, url, data=None, headers=None, timeout=30):
    h = {'User-Agent': 'VorTex-IndexNow/1.0'}
    if headers: h.update(headers)
    body = json.dumps(data, ensure_ascii=False).encode('utf-8') if data is not None else None
    try:
        req = urllib.request.Request(url, data=body, headers=h, method=method)
        with urllib.request.urlopen(req, timeout=timeout) as r:
            return r.status, r.read().decode('utf-8', 'replace')[:400]
    except urllib.error.HTTPError as e:
        return e.code, e.read().decode('utf-8', 'replace')[:400]
    except Exception as e:
        return -1, f'{type(e).__name__}: {e}'

payload = {'host': HOST, 'key': KEY, 'keyLocation': f'{BASE}/{KEY}.txt', 'urlList': URLS}

log('# VorTex 搜索引擎推送结果')
log('')
log(f'- 时间: {datetime.datetime.now(datetime.timezone.utc).strftime("%Y-%m-%d %H:%M:%S UTC")}')
log(f'- 运行: {RUN_URL}')
log(f'- 提交页面数: {len(URLS)}')
log(f'- IndexNow key: `{KEY}` → `{BASE}/{KEY}.txt`')
log('')
log('## 各端点响应')
log('')
log('| 端点 | 方法 | HTTP | 响应体 |')
log('|---|---|---|---|')

endpoints = [
    'https://api.indexnow.org/indexnow',
    'https://www.bing.com/indexnow',
    'https://yandex.com/indexnow',
    'https://search.seznam.cz/indexnow',
]
results = []
for ep in endpoints:
    code, body = call('POST', ep, payload, {'Content-Type': 'application/json; charset=utf-8'})
    results.append((ep, 'POST', code, body))
    log(f'| `{ep}` | POST | {code} | {body[:110].replace("|", "/").replace(chr(10), " ")} |')

pings = [
    f'https://www.google.com/ping?sitemap={BASE}/sitemap.xml',
    f'https://www.bing.com/ping?sitemap={BASE}/sitemap.xml',
]
for p in pings:
    code, body = call('GET', p)
    results.append((p, 'GET', code, body))
    log(f'| `{p}` | GET | {code} | {body[:110].replace("|", "/").replace(chr(10), " ")} |')

for label, u in [('key file', f'{BASE}/{KEY}.txt'), ('site root', f'{BASE}/'),
                 ('sitemap', f'{BASE}/sitemap.xml'), ('llms.txt', f'{BASE}/llms.txt')]:
    code, body = call('GET', u)
    results.append((label + ' ' + u, 'GET', code, body))
    log(f'| `{label}` | GET | {code} | {body[:110].replace("|", "/").replace(chr(10), " ")} |')

log('')
log('## 提交的 URL')
log('')
for u in URLS:
    log(f'- {u}')
log('')
log('> IndexNow 返回 200 / 202 表示引擎已接收 URL；是否收录、何时收录由各引擎自行调度。')
log('> Google 不接受 IndexNow，仅靠 sitemap ping 与 GitHub 仓库页抓取发现。')


# ── 收录自检（从 runner 直查搜索引擎结果页）────────────────────
import urllib.parse, re
SITE = 'lianqlou114514-blip.github.io/VorTex'
log('')
log('## 收录自检')
log('')
log('| 引擎 | 查询 | HTTP | 已收录 |')
log('|---|---|---|---|')
probes = [
    ('Bing', 'https://www.bing.com/search?q=' + urllib.parse.quote(f'site:{SITE}')),
    ('Bing 标题', 'https://www.bing.com/search?q=' + urllib.parse.quote('"VorTex — Android Floating-Panel UI Study"')),
    ('Google', 'https://www.google.com/search?q=' + urllib.parse.quote(f'site:{SITE}')),
    ('DuckDuckGo', 'https://html.duckduckgo.com/html/?q=' + urllib.parse.quote(f'site:{SITE}')),
]
for name, u in probes:
    code, body = call('GET', u, headers={'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 Chrome/124 Safari/537.36'})
    # 结果链接中真实出现我们的 URL 才算收录（查询词本身会出现在页面里，不能算）
    indexed = bool(re.search(r'href="https?://' + re.escape(SITE), body))
    log(f'| {name} | `site:{SITE}` | {code} | {"YES" if indexed else "no"} |')


report = '\n'.join(lines) + '\n'

# 写回仓库，供沙箱经 api.github.com 读取
if TOKEN:
    api = f'https://api.github.com/repos/{OWNER}/{REPO}/contents/seo-status.md'
    ah = {'Authorization': 'Bearer ' + TOKEN, 'Accept': 'application/vnd.github+json',
          'User-Agent': 'VorTex-SEO'}
    sha = None
    try:
        with urllib.request.urlopen(urllib.request.Request(api, headers=ah), timeout=25) as r:
            sha = json.load(r)['sha']
    except Exception:
        pass
    obj = {'message': 'Update SEO submission report',
           'content': base64.b64encode(report.encode('utf-8')).decode(), 'branch': 'main'}
    if sha: obj['sha'] = sha
    code, body = call('PUT', api, obj, ah)
    log('')
    log(f'report write-back: HTTP {code} {body[:150]}')
