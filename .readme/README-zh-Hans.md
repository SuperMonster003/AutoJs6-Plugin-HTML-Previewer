<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="html-previewer-ic-launcher" border="0" width="128" />
  </p>

  <p>预览 HTML 和 MHTML 文件</p>

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

一键预览: 在文件管理器中通过 `预览 HTML` 菜单直接打开网页文件, 无需浏览器, 纯本地页面无需联网.

默认禁用脚本. 对于可信的完整普通 HTML 文件, 可在 `设置` 中开启 `交互模式`, 支持 JavaScript, 按钮事件, WebGL 和本地存储. MHTML, 截断 HTML, 源码视图和 PDF 导出始终禁用脚本.

******

### 功能亮点

******

- 一键预览: 在文件管理器中通过 `预览 HTML` 菜单直接打开网页文件, 无需浏览器, 纯本地页面无需联网.
- 默认禁用脚本. 对于可信的完整普通 HTML 文件, 可在 `设置` 中开启 `交互模式`, 支持 JavaScript, 按钮事件, WebGL 和本地存储. MHTML, 截断 HTML, 源码视图和 PDF 导出始终禁用脚本.
- 安全源码视图: 可在净化预览与只读等宽的原始 HTML 之间切换; MHTML 显示解码后的根 HTML, 标签始终按原文显示, 源码模式不会发出任何网络请求.
- 系统 PDF 导出: 选择 `导出 PDF` 后, 插件会将重新净化并强制浅色的副本交给 Android 系统打印界面; 页面设置与 `保存为 PDF` 均由系统处理, 无需存储权限.
- 还原排版: 支持页面自带样式, MHTML 内允许的嵌入资源, 普通 HTML 同目录下的图片, 字体, 音频, 视频等本地资源, 以及可彻底关闭的 HTTPS 网络图片.
- 阅读体验: 支持页内查找, 75%-200% 字号, 双指缩放, 以及跟随 AutoJs6/强制浅色/强制深色主题. 菜单和对话框跟随 AutoJs6 的语言与暗色模式. GitHub (Auto) 和 HTML 自动主题也跟随 AutoJs6. 工具栏及系统栏使用渲染页面边缘的代表色, 配合有对比度的黑色或白色文字与图标. 渐变, 图片和动画背景取色后保持稳定, 重新加载时更新, 避免闪烁.
- 全屏模式: 可随时切换沉浸式全屏, 并可在设置中开启 `以全屏模式启动`.
- 外部链接: 页面内点按 http/https 链接时交由系统浏览器打开, 页内锚点跳转不受影响.
- 多语言: 界面, 使用说明, README 与更新日志均提供 10 种语言.

******

### 使用方法

******

1. 从 [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/releases) 页面下载最新的插件 APK 并安装到设备.
2. 打开 AutoJs6 的插件中心, 启用 `HTML 预览` 插件.
3. 在 AutoJs6 的文件管理器中找到想查看的 HTML 或 MHTML 文件, 打开该文件的溢出菜单 (更多操作菜单).
4. 选择 `预览 HTML`, 页面将在独立的查看器中打开.
5. 阅读时可通过右上角菜单查找文字, 在 `查看源码` 与 `查看预览` 间切换, `导出 PDF`, `刷新`, 切换 `全屏模式`, 或在 `设置` 中调整字号, 主题, 网络图片与全屏启动; 按返回键可关闭查找, 退出全屏或关闭查看器.

> 若插件中心未显示该插件, 请先将 AutoJs6 升级到较新版本 (内部版本号 5269 及以上). Explorer Action v2 同时支持单文件的主预览按钮和溢出菜单, 通过临时只读授权访问文档及其父目录. 需要 AutoJs6 构建 5269 或更高版本.

******

### 支持的格式

******

插件识别以下文件扩展名, 同时接受宿主明确标记为 `text/html`, `application/xhtml+xml`, `multipart/related` 或 `application/x-mimearchive` 的无扩展名文件:

```text
html, htm, shtm, shtml, xht, xhtml, mht, mhtml
```

不超过 8 MB 的文件会完整加载. 更大的普通 HTML 可在确认后仅预览明确标记的前 8 MB; MHTML 必须保持完整, 超限时会直接拒绝. HTML 解码时 BOM 优先, 其后依次使用 MIME 字符集, 文件前部的 `meta charset` 或 `http-equiv` 声明, 未声明时回退到 UTF-8. MHTML 支持有界 MIME 嵌套及标准恒等, quoted-printable 与 Base64 传输编码.

******

### 常见问题

******

#### 为什么页面里的按钮和动态效果没有反应?

默认禁用脚本. 对于可信的完整普通 HTML 文件, 可在 `设置` 中开启 `交互模式`, 支持 JavaScript, 按钮事件, WebGL 和本地存储. MHTML, 截断 HTML, 源码视图和 PDF 导出始终禁用脚本.

#### 为什么有些图片或样式没有显示?

本地资源来自已授权目录或选中的 MHTML 归档. 安全模式允许公共 HTTPS 图片; 交互模式开启 `加载网络资源` 后还允许公共 HTTPS 脚本, 样式及请求. 关闭网络开关会拦截 WebView 出站请求. 交互脚本能够向远程服务发送页面数据, 因此仅应对可信页面启用.

#### 预览效果和浏览器里不完全一样, 正常吗?

默认安全模式下: 正常. 除移除脚本外, 查看器还会注入一套基础排版样式并禁用部分高级特性, 目标是安全清晰的阅读体验, 而非逐像素还原. 检查最终显示效果请以浏览器为准.

#### 这个插件会联网上传我的文件吗?

本地资源来自已授权目录或选中的 MHTML 归档. 安全模式允许公共 HTTPS 图片; 交互模式开启 `加载网络资源` 后还允许公共 HTTPS 脚本, 样式及请求. 关闭网络开关会拦截 WebView 出站请求. 交互脚本能够向远程服务发送页面数据, 因此仅应对可信页面启用.

#### 导出的 PDF 保存在哪里?

`导出 PDF` 会打开 Android 系统打印界面. 选择 `保存为 PDF`, 再在系统文件选择器中指定文件名与位置. 插件不会直接写入存储空间, 也不申请存储权限. 即使从源码视图发起, 导出仍会重新读取 HTML 或完整 MHTML 归档并净化根页面, 且不会改变当前查看器状态.

#### 能不能编辑 HTML 文件, 或预览整个网站项目?

当前版本定位为单文件只读预览: 不提供编辑能力, 也不会枚举目录. 页面引用的同目录资源会被按需读取, 但插件始终只持有宿主授予的临时读取权限. 后续规划可查看 [ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/ROADMAP.md).

******

### 权限与安全

******

每次新预览均关闭交互模式, 仅在明确信任当前文档时手动启用. CSP 阻止 fetch, WebSocket, worker 和框架. 交互 JavaScript 不是通用网络沙箱: WebRTC 等接口可能独立于资源开关联网. 陌生文档应保持默认静态模式.

- 默认安全模式下: 显示前净化: 移除脚本, 事件处理属性, 内嵌框架, 嵌入对象与表单提交地址, 并过滤不安全的链接和资源地址.
- Cookie, 直接文件/content 访问, 原生 JavaScript 桥和新窗口保持禁用. 安全模式禁用页面存储; 交互模式允许本地存储, 用于页面偏好或游戏分数. 源码视图与 PDF 使用重新净化的内容.
- 每次新预览均关闭交互模式, 仅在明确信任当前文档时手动启用. CSP 阻止 fetch, WebSocket, worker 和框架. 交互 JavaScript 不是通用网络沙箱: WebRTC 等接口可能独立于资源开关联网. 陌生文档应保持默认静态模式.
- 最小权限: 插件只接收宿主授予的临时 content URI 读取权限, 接触不到文件系统路径; PDF 输出交由 Android 系统打印服务处理, 插件自身不写入存储空间.
- 输入有界: 读取大小上限为 8 MB; MHTML 还限制 MIME 部件数, 嵌套层数, 头部与解码总量; 文件名与资源路径均经过严格校验, 防止越界访问.
- 仅为当前可信文档启用. 脚本可能通过 WebRTC 等接口联网, 不受资源开关控制. 每次新预览默认关闭脚本.
- 静态预览和受控 HTTPS 资源客户端校验并绑定公网 DNS 地址, 每次重定向均重新校验. 资源开关控制该客户端, 请求仅支持 GET 和 HEAD.

默认安全模式下: 源清单仅申请网络权限 (在开启 HTTPS 图片时使用) 与 AutoJs6 插件权限. AndroidX 还会自动加入一个仅限本应用签名的权限, 用于保护未导出的动态接收器, 不授予任何设备数据访问能力. 关闭网络图片后 WebView 不会发出出站请求. PDF 目标位置的访问由 Android 系统打印与文件选择界面负责, 因此插件不申请存储, 媒体, 相机, 位置等其他敏感权限.

******

### 插件接口

******

以下信息面向开发者, 宿主通过这些标识发现并执行插件:

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

Explorer Action v2 同时支持单文件的主预览按钮和溢出菜单, 通过临时只读授权访问文档及其父目录. 需要 AutoJs6 构建 5269 或更高版本.

- [查看 Explorer Action 兼容矩阵](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/docs/explorer-action-compatibility.md)

******

### 开发路线图

******

插件的能力规划与完成情况以可勾选的清单维护在 ROADMAP.md 中, 按里程碑组织并附验收条件, 涵盖页内查找, 字号调节, 更多编码支持, 网络图片开关等方向. 未勾选的条目表示规划而非当前版本已支持的能力, 欢迎通过 Issues 参与讨论.

- [查看 ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/ROADMAP.md)

******

### 发行历史

******

#### v1.1.3

_2026/09/19_

- `修复` 页面背景渲染后及时更新状态栏和导航栏颜色, 无需等待 JavaScript 加载完成
- `修复` AGP 9.1 构建时的 SDK XML v4 解析警告及 JVM 单元测试组装任务误触发 APK 原生库对齐检查的问题 (共享构建插件 1.8.3)

#### v1.1.2

_2026/09/16_

- `优化` 继 compileSdk 之后将 targetSdk 提升到 37 (Android 17), 插件行为不受新目标版本影响

#### v1.1.1

_2026/09/15_

- `优化` 将 compileSdk 提升到 37 (Android 17), targetSdk 保持 36, 待依赖目标版本的行为验证后再提升

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

构建参数来自 `version.properties`, 当前最低 SDK 为 24, 目标 SDK 为 37.

******

### 本地化与文档生成

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

`strings.xml` 提供插件信息和查看器界面的本地化, `plugin_instruction.md` 提供宿主侧展示的使用说明. README 与更新日志一律修改 `.readme/` 与 `.changelog/` 下的 JSON 源文件, 再运行 `py .python/generate_markdown.py` 重新生成, 生成产物不手工编辑; 运行 `py .python/generate_markdown.py --check` 可校验源文件与生成产物是否同步.

******

### 相关链接

******

- AutoJs6 文档: https://docs.autojs6.com
- HTML Living Standard: https://html.spec.whatwg.org


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/docs/16kb.md)
