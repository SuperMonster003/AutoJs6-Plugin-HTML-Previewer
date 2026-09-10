******

### 发行历史

******

# v1.0.1

###### 2026/08/08

* `修复` 插件中心启用时因服务返回空绑定而失败的问题
* `优化` 更简洁的插件名称, 描述和用户文档

# v1.0.0

###### 2026/08/06

* `新增` HTML Previewer 插件, 插件 ID 为 `html-previewer`, 引擎为 `explorer-action`, 变体为 `default`
* `新增` 通过 `org.autojs.plugin.EXPLORER_ACTION` 为文件管理器提供单文件只读溢出菜单动作
* `新增` 通过 `org.autojs.plugin.EXPLORER_ACTION_EXECUTE` 接收文件和父目录 content URI 的临时读取权限
* `新增` 显示前移除脚本/事件处理属性/框架/嵌入对象和不安全资源地址的 HTML 净化
* `新增` 按受控请求规则加载允许的相对资源/data 资源/HTTPS 图片, 并提供固定样式/刷新/全屏控制
* `新增` 通过 CSP/受控 URI 导航/禁用 JavaScript 和存储/有界输入强化 WebView 安全
* `新增` 插件信息/界面文本/使用说明/README/changelog 支持西班牙语/法语/俄语/阿拉伯语/日语/韩语/英语/简体中文/香港繁体/台湾繁体
