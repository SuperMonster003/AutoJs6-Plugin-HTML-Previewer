******

### 發行歷史

******

# v1.0.1

###### 2026/08/08

* `修復` 外掛程式中心啟用時因服務傳回空繫結而失敗的問題
* `優化` 更簡潔的外掛程式名稱, 描述和使用者文件

# v1.0.0

###### 2026/08/06

* `新增` HTML Previewer 外掛, 外掛 ID 為 `html-previewer`, 引擎為 `explorer-action`, 變體為 `default`
* `新增` 透過 `org.autojs.plugin.EXPLORER_ACTION` 為檔案管理器提供單一檔案唯讀更多選單動作
* `新增` 透過 `org.autojs.plugin.EXPLORER_ACTION_EXECUTE` 接收檔案和上層目錄 content URI 的暫時讀取權限
* `新增` 顯示前移除腳本/事件處理屬性/框架/嵌入物件和不安全資源位址的 HTML 淨化
* `新增` 依受控請求規則載入允許的相對資源/data 資源/HTTPS 圖片, 並提供固定樣式/重新整理/全螢幕控制
* `新增` 透過 CSP/受控 URI 導覽/停用 JavaScript 和儲存空間/有界輸入強化 WebView 安全
* `新增` 外掛資訊/介面文字/使用說明/README/changelog 支援西班牙文/法文/俄文/阿拉伯文/日文/韓文/英文/簡體中文/香港繁體/台灣繁體
