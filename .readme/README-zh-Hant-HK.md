<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="html-previewer-ic-launcher" border="0" width="128" />
  </p>

  <p>檔案管理器外掛程式. 安全唯讀預覽 HTML 及 MHTML 檔案</p>

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

一鍵預覽: 在檔案管理器中透過 `預覽 HTML` 選單直接開啟網頁檔案, 無需瀏覽器, 純本機頁面無需連線.

預設停用指令碼. 對於可信的完整普通 HTML 檔案, 可在 `設定` 中開啟 `互動模式`, 支援 JavaScript, 按鈕事件, WebGL 和本機儲存. MHTML, 截斷 HTML, 原始碼檢視和 PDF 匯出始終停用指令碼.

******

### 功能亮點

******

- 一鍵預覽: 在檔案管理器中透過 `預覽 HTML` 選單直接開啟網頁檔案, 無需瀏覽器, 純本機頁面無需連線.
- 預設停用指令碼. 對於可信的完整普通 HTML 檔案, 可在 `設定` 中開啟 `互動模式`, 支援 JavaScript, 按鈕事件, WebGL 和本機儲存. MHTML, 截斷 HTML, 原始碼檢視和 PDF 匯出始終停用指令碼.
- 安全原始碼檢視: 可在淨化預覽與唯讀等寬的原始 HTML 之間切換; MHTML 顯示解碼後的根 HTML, 標籤始終按原文顯示, 原始碼模式不會發出任何網絡請求.
- 系統 PDF 匯出: 選擇 `匯出 PDF` 後, 外掛程式會將重新淨化並強制淺色的副本交給 Android 系統列印介面; 頁面設定與 `另存為 PDF` 均由系統處理, 無需儲存空間權限.
- 還原排版: 支援頁面自帶樣式, MHTML 內允許的嵌入資源, 普通 HTML 同目錄下的圖片, 字型, 音訊, 影片等本機資源, 以及可完全關閉的 HTTPS 網絡圖片.
- 閱讀體驗: 支援頁內搜尋, 75%-200% 字號, 雙指縮放, 以及跟隨 AutoJs6/強制淺色/強制深色主題. 選單和對話框跟隨 AutoJs6 的語言與深色模式. GitHub (Auto) 和 HTML 自動主題也跟隨 AutoJs6. 工具列及系統列使用渲染頁面邊緣的代表色, 配合有對比度的黑色或白色文字與圖示. 漸變, 圖片和動畫背景取色後保持穩定, 重新載入時更新, 避免閃爍.
- 全螢幕模式: 可隨時切換沉浸式全螢幕, 並可在設定中開啟 `以全螢幕模式啟動`.
- 外部連結: 頁面內點按 http/https 連結時交由系統瀏覽器開啟, 頁內錨點跳轉不受影響.
- 多語言: 介面, 使用說明, README 與更新日誌均提供 10 種語言.

******

### 使用方法

******

1. 從 [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/releases) 頁面下載最新的外掛程式 APK 並安裝到裝置.
2. 開啟 AutoJs6 的外掛程式中心, 啟用 `HTML 預覽` 外掛程式.
3. 在 AutoJs6 的檔案管理器中找到想查看的 HTML 或 MHTML 檔案, 開啟該檔案的更多選單.
4. 選擇 `預覽 HTML`, 頁面將在獨立的檢視器中開啟.
5. 閱讀時可透過右上角選單搜尋文字, 在 `檢視原始碼` 與 `檢視預覽` 之間切換, `匯出 PDF`, `重新整理`, 切換 `全螢幕模式`, 或在 `設定` 中調整字號, 主題, 網絡圖片與全螢幕啟動; 按返回鍵可關閉搜尋, 退出全螢幕或關閉檢視器.

> 若外掛程式中心未顯示該外掛程式, 請先將 AutoJs6 升級到較新版本 (內部版本號 5269 及以上). Explorer Action v2 同時支援單檔案的主預覽按鈕和溢出選單, 透過臨時唯讀授權存取文件及其父目錄. 需要 AutoJs6 組建 5269 或更新版本.

******

### 支援的格式

******

外掛程式識別以下檔案副檔名, 同時接受宿主明確標記為 `text/html`, `application/xhtml+xml`, `multipart/related` 或 `application/x-mimearchive` 的無副檔名檔案:

```text
html, htm, shtm, shtml, xht, xhtml, mht, mhtml
```

不超過 8 MB 的檔案會完整載入. 更大的普通 HTML 可在確認後只預覽明確標記的前 8 MB; MHTML 必須保持完整, 超限時會直接拒絕. HTML 解碼時 BOM 優先, 其後使用 MIME 字元集, 檔案前部的 `meta charset` 或 `http-equiv` 聲明, 未聲明時回退至 UTF-8. MHTML 支援有界 MIME 嵌套及標準恒等, quoted-printable 與 Base64 傳輸編碼.

******

### 常見問題

******

#### 為什麼頁面裏的按鈕和動態效果沒有反應?

預設停用指令碼. 對於可信的完整普通 HTML 檔案, 可在 `設定` 中開啟 `互動模式`, 支援 JavaScript, 按鈕事件, WebGL 和本機儲存. MHTML, 截斷 HTML, 原始碼檢視和 PDF 匯出始終停用指令碼.

#### 為什麼有些圖片或樣式沒有顯示?

本機資源來自已授權目錄或選取的 MHTML 封存檔. 安全模式允許公共 HTTPS 圖片; 互動模式開啟 `載入網絡資源` 後亦允許公共 HTTPS 指令碼, 樣式及請求. 關閉網絡開關會攔截 WebView 對外請求. 互動指令碼能夠向遠端服務傳送頁面資料, 因此僅應對可信頁面啟用.

#### 預覽效果和瀏覽器裏不完全一樣, 正常嗎?

預設安全模式下: 正常. 除移除腳本外, 檢視器還會注入一套基礎排版樣式並停用部分進階特性, 目標是安全清晰的閱讀體驗, 而非逐像素還原. 檢查最終顯示效果請以瀏覽器為準.

#### 這個外掛程式會連線上傳我的檔案嗎?

本機資源來自已授權目錄或選取的 MHTML 封存檔. 安全模式允許公共 HTTPS 圖片; 互動模式開啟 `載入網絡資源` 後亦允許公共 HTTPS 指令碼, 樣式及請求. 關閉網絡開關會攔截 WebView 對外請求. 互動指令碼能夠向遠端服務傳送頁面資料, 因此僅應對可信頁面啟用.

#### 匯出的 PDF 儲存在哪裏?

`匯出 PDF` 會開啟 Android 系統列印介面. 選擇 `另存為 PDF`, 再在系統檔案選擇器中指定檔名與位置. 外掛程式不會直接寫入儲存空間, 亦不申請儲存空間權限. 即使從原始碼檢視發起, 匯出仍會重新讀取 HTML 或完整 MHTML 封存檔並淨化根頁面, 且不會改變目前檢視器狀態.

#### 能不能編輯 HTML 檔案, 或預覽整個網站項目?

目前版本定位為單一檔案唯讀預覽: 不提供編輯能力, 也不會枚舉目錄. 頁面引用的同目錄資源會被按需讀取, 但外掛程式始終只持有宿主授予的臨時讀取權限. 後續規劃可查看 [ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/ROADMAP.md).

******

### 權限與安全

******

外掛程式在設計上假定被預覽的檔案不可信, 因此採取多層防護:

- 預設安全模式下: 顯示前淨化: 移除腳本, 事件處理屬性, 內嵌框架, 嵌入物件與表單提交位址, 並過濾不安全的連結和資源位址.
- Cookie, 直接檔案/content 存取, 原生 JavaScript 橋和新視窗保持停用. 安全模式停用頁面儲存; 互動模式允許本機儲存, 用於頁面偏好或遊戲分數. 原始碼檢視與 PDF 使用重新淨化的內容.
- 本機資源來自已授權目錄或選取的 MHTML 封存檔. 安全模式允許公共 HTTPS 圖片; 互動模式開啟 `載入網絡資源` 後亦允許公共 HTTPS 指令碼, 樣式及請求. 關閉網絡開關會攔截 WebView 對外請求. 互動指令碼能夠向遠端服務傳送頁面資料, 因此僅應對可信頁面啟用.
- 最小權限: 外掛程式只接收宿主授予的臨時 content URI 讀取權限, 接觸不到檔案系統路徑; PDF 輸出交由 Android 系統列印服務處理, 外掛程式本身不寫入儲存空間.
- 輸入有界: 讀取大小上限為 8 MB; MHTML 亦限制 MIME 部件數, 嵌套層數, 頭部與解碼總量; 檔案名與資源路徑均經過嚴格校驗, 防止越界存取.
- Cookie, 直接檔案/content 存取, 原生 JavaScript 橋和新視窗保持停用. 安全模式停用頁面儲存; 互動模式允許本機儲存, 用於頁面偏好或遊戲分數. 原始碼檢視與 PDF 使用重新淨化的內容.

預設安全模式下: 來源清單僅申請網絡權限 (在開啟 HTTPS 圖片時使用) 與 AutoJs6 外掛程式權限. AndroidX 還會自動加入一個僅限本應用程式簽名的權限, 用於保護未匯出的動態接收器, 不授予任何裝置資料存取能力. 關閉網絡圖片後 WebView 不會發出對外請求. PDF 目標位置的存取由 Android 系統列印與檔案選擇介面負責, 因此外掛程式不申請儲存空間, 媒體, 相機, 位置等其他敏感權限.

******

### 插件介面

******

以下資訊面向開發者, 宿主透過這些識別資訊探索並執行外掛程式:

```text
application id: io.github.supermonster003.autojs6.plugin.htmlpreviewer
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: html-previewer
engine: explorer-action
variant: default
protocol version: 2
minimum host build: 5269
audited host build: 5279
audited host protocol: 22
```

Explorer Action v2 同時支援單檔案的主預覽按鈕和溢出選單, 透過臨時唯讀授權存取文件及其父目錄. 需要 AutoJs6 組建 5269 或更新版本.

- [查看 Explorer Action 相容矩陣](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/docs/explorer-action-compatibility.md)

******

### 開發路線圖

******

外掛程式的能力規劃與完成情況以可勾選的清單維護在 ROADMAP.md 中, 按里程碑組織並附驗收條件, 涵蓋頁內搜尋, 字號調節, 更多編碼支援, 網絡圖片開關等方向. 未勾選的條目表示規劃而非目前版本已支援的能力, 歡迎透過 Issues 參與討論.

- [查看 ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/ROADMAP.md)

******

### 發行歷史

******

#### v1.0.1

_2026/09/11_

- `新增` 預設停用指令碼. 對於可信的完整普通 HTML 檔案, 可在 `設定` 中開啟 `互動模式`, 支援 JavaScript, 按鈕事件, WebGL 和本機儲存. MHTML, 截斷 HTML, 原始碼檢視和 PDF 匯出始終停用指令碼.
- `修復` 在外掛程式中心啟用外掛程式時可能提示啟用失敗的問題
- `修復` 修復主預覽按鈕協議拒絕和設定崩潰; 同步宿主個人化配置, 頁面列配色及對話框黑白控制項
- `優化` 更簡潔的外掛程式名稱與描述, 更易讀的使用文件
- `優化` 建置階段阻止意外引入原生相依套件, 並輸出 JSON 校驗報告

#### v1.0.0

_2026/08/06_

- `新增` 首個版本: 在 AutoJs6 檔案管理器中為 HTML 檔案提供 `預覽 HTML` 選單動作 (外掛程式 ID `html-previewer`)
- `新增` 安全檢視器: 顯示前自動移除腳本, 框架, 嵌入物件與表單提交, 全程唯讀且不執行 JavaScript
- `新增` 資源載入: 支援頁面樣式, 同目錄本機資源與 HTTPS 網絡圖片的受控載入, 其餘請求一律攔截
- `新增` 閱讀體驗: 深色/淺色主題自適應, 雙指縮放, 重新整理, 全螢幕模式與 `以全螢幕模式啟動` 設定
- `新增` 安全邊界: 僅接受宿主授予的臨時讀取權限, 單一檔案上限 8 MB, 嚴格校驗檔案名與資源路徑
- `新增` 多語言: 介面, 使用說明, README 與更新日誌支援 10 種語言

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

### 本地化與文件生成

******

```text
.readme/common.json
.readme/lang_*.json
.readme/template_readme.md
.changelog/lang_*.json
.changelog/template_changelog.md
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` 提供插件資訊和檢視器介面的本地化, `plugin_instruction.md` 提供宿主側展示的使用說明. README 與更新日誌一律修改 `.readme/` 與 `.changelog/` 下的 JSON 源檔案, 再執行 `py .python/generate_markdown.py` 重新生成, 生成產物不手工編輯; 執行 `py .python/generate_markdown.py --check` 可校驗源檔案與生成產物是否同步.

******

### 相關連結

******

- AutoJs6 文件: https://docs.autojs6.com
- HTML Living Standard: https://html.spec.whatwg.org


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/docs/16kb.md)
