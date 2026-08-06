在 AutoJs6 主文件浏览器中使用 HTML Preview:

1. 安装并启用 `HTML Preview` 插件.
2. 打开一个受支持 HTML 文件的溢出菜单.
3. 选择 `预览 HTML`.

插件通过 content URI 临时获得所选文件及其父目录的读取权限. 插件不会接收原始文件系统路径.

支持的扩展名: `html`, `htm`, `shtm`, `shtml`, `xht`, `xhtml`.

查看器会在显示前净化内容. 它会移除脚本, 事件处理属性, 框架, 嵌入对象和不安全的资源地址. JavaScript, WebView 存储, Cookie 和直接文件访问保持禁用.

版本 1 仅支持 AutoJs6 主文件浏览器中的单文件只读动作.
