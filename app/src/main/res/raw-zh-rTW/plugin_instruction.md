在檔案管理器中使用 HTML Preview:

1. 安裝並啟用 `HTML Preview` 外掛.
2. 開啟一個受支援 HTML 檔案的更多選單.
3. 選擇 `預覽 HTML`.

外掛透過 content URI 暫時取得所選檔案及其上層目錄的讀取權限. 外掛不會接收原始檔案系統路徑.

支援的副檔名: `html`, `htm`, `shtm`, `shtml`, `xht`, `xhtml`.

檢視器會在顯示前淨化內容. 它會移除腳本, 事件處理屬性, 框架, 嵌入物件和不安全的資源位址. JavaScript, WebView 儲存空間, Cookie 和直接檔案存取保持停用.

版本 1 僅支援檔案管理器中的單一檔案唯讀動作.
