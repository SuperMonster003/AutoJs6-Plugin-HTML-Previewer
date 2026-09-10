<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="html-previewer-ic-launcher" border="0" width="128" />
  </p>

  <p>檔案管理器外掛程式. 安全唯讀預覽 HTML 檔案</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 語言 (Languages)

******

目前 README.md 支援以下語言:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-zh-Hans.md)
- 繁體中文 (香港) [zh-Hant-HK] # 目前
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ar.md)

******

### 簡介

******

HTML Previewer 為檔案管理器提供單一檔案唯讀 HTML 預覽動作. 網頁內容在獨立檢視器中淨化並顯示, 無需將預覽實現嵌入主程式.

******

### 功能

******

- 透過共用的 `org.autojs.plugin.EXPLORER_ACTION` 協議註冊單一檔案唯讀檔案瀏覽器動作.
- 透過 content URI 臨時讀取所選 HTML 檔案及其上層目錄資源, 不接收原始檔案系統路徑.
- 顯示前移除腳本, 事件處理屬性, 框架, 嵌入物件和不安全的資源位址.
- 按受控規則載入允許的相對資源, data 資源和 HTTPS 圖片.
- 提供固定的易讀樣式, 重新整理, 全螢幕模式, 啟動時全螢幕偏好和有界的 HTML 輸入大小.

******

### 支援的格式

******

首個版本識別以下檔案副檔名:

```text
html, htm, shtm, shtml, xht, xhtml
```

******

### 插件介面

******

主程式透過以下識別資訊探索並執行外掛程式:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: html-previewer
engine: explorer-action
variant: default
```

版本 1 僅支援檔案管理器中的單一檔案唯讀更多選單動作.

******

### 安全

******

檢視器淨化文件並停用 JavaScript/WebView 儲存空間/Cookie 和直接檔案存取, 透過 CSP 與 URI 約束限制資源和導覽, 並只接受宿主授予的臨時讀取權限.

******

### 發行歷史

******

# v1.0.1

###### 2026/08/08

* `修復` 外掛程式中心啟用時因服務傳回空綁定而失敗的問題
* `優化` 更簡潔的外掛程式名稱, 描述和使用者文件

# v1.0.0

###### 2026/08/06

* `新增` HTML Previewer 插件, 插件 ID 為 `html-previewer`, 引擎為 `explorer-action`, 變體為 `default`
* `新增` 透過 `org.autojs.plugin.EXPLORER_ACTION` 為檔案管理器提供單一檔案唯讀更多選單動作
* `新增` 透過 `org.autojs.plugin.EXPLORER_ACTION_EXECUTE` 接收檔案和上層目錄 content URI 的臨時讀取權限
* `新增` 顯示前移除腳本/事件處理屬性/框架/嵌入物件和不安全資源位址的 HTML 淨化
* `新增` 按受控請求規則載入允許的相對資源/data 資源/HTTPS 圖片, 並提供固定樣式/重新整理/全螢幕控制
* `新增` 透過 CSP/受控 URI 導覽/停用 JavaScript 和儲存空間/有界輸入強化 WebView 安全
* `新增` 插件資訊/介面文字/使用說明/README/changelog 支援西班牙語/法語/俄語/阿拉伯語/日語/韓語/英語/簡體中文/香港繁體/台灣繁體

##### 更多發行歷史可參閱

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hant-HK.md)

******

### 構建

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Release 構建:

```powershell
.\gradlew.bat :app:assembleRelease
```

構建參數來自 `version.properties`, 目前最低 SDK 為 24, 目標 SDK 為 36.

******

### 資源結構

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` 提供插件資訊和檢視器介面的本地化, `plugin_instruction.md` 提供宿主側展示的使用說明. README 與 changelog 檔案由 `.python/generate_markdown.py` 根據 JSON 源檔案生成.

******

### 相關連結

******

- AutoJs6 文件: https://docs.autojs6.com
- HTML Living Standard: https://html.spec.whatwg.org
