<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="html-previewer-ic-launcher" border="0" width="128" />
  </p>

  <p>文件管理器插件. 安全只读预览 HTML 文件</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 语言 (Languages)

******

当前 README.md 支持以下语言:

- 简体中文 [zh-Hans] # 当前
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ar.md)

******

### 简介

******

HTML Previewer 为文件管理器提供单文件只读 HTML 预览动作. 网页内容在独立查看器中净化并显示, 无需将预览实现嵌入宿主应用.

******

### 功能

******

- 通过共享的 `org.autojs.plugin.EXPLORER_ACTION` 协议注册单文件只读文件浏览器动作.
- 通过 content URI 临时读取所选 HTML 文件及其父目录资源, 不接收原始文件系统路径.
- 显示前移除脚本, 事件处理属性, 框架, 嵌入对象和不安全的资源地址.
- 按受控规则加载允许的相对资源, data 资源和 HTTPS 图片.
- 提供固定的可读样式, 刷新, 全屏模式, 启动时全屏偏好和有界的 HTML 输入大小.

******

### 支持的格式

******

首个版本识别以下文件扩展名:

```text
html, htm, shtm, shtml, xht, xhtml
```

******

### 插件接口

******

宿主通过以下标识发现并执行插件:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: html-previewer
engine: explorer-action
variant: default
```

版本 1 仅支持文件管理器中的单文件只读溢出菜单动作.

******

### 安全

******

查看器净化文档并禁用 JavaScript/WebView 存储/Cookie 和直接文件访问, 通过 CSP 与 URI 约束限制资源和导航, 并只接受宿主授予的临时读取权限.

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

##### 更多发行历史可参阅

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/app/src/main/assets/doc/CHANGELOG-zh-Hans.md)

******

### 构建

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Release 构建:

```powershell
.\gradlew.bat :app:assembleRelease
```

构建参数来自 `version.properties`, 当前最低 SDK 为 24, 目标 SDK 为 36.

******

### 资源结构

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` 提供插件信息和查看器界面的本地化, `plugin_instruction.md` 提供宿主侧展示的使用说明. README 与 changelog 文件由 `.python/generate_markdown.py` 根据 JSON 源文件生成.

******

### 相关链接

******

- AutoJs6 文档: https://docs.autojs6.com
- HTML Living Standard: https://html.spec.whatwg.org
