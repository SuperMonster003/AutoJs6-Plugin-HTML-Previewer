# AutoJs6 HTML Previewer Roadmap

本文档是 HTML 预览插件从 "单文件安全预览" 演进为 "安卓端可信 HTML 阅读器" 的执行清单.
每一项只有在代码/测试与可验证的验收条件同时完成后才可勾选.

## 状态与边界

- `[x]`: 已完成并在当前仓库验证.
- `[ ]`: 尚未完成; 括号中的 `插件`/`宿主`/`API`/`测试`/`发布` 表示主要落点.
- 原始文件保持只读, 不申请存储/相机/位置等新的敏感权限. 默认安全模式禁用脚本; 用户主动开启交互模式后, 可信完整普通 HTML 可执行 JavaScript/WebGL 并使用本地存储及受网络开关控制的公共 HTTPS 资源. MHTML/截断/源码/PDF 始终禁用脚本.
- 当前部署基线: Explorer Action 协议 v2, 宿主版本代码 5269+, 单文件主预览与溢出菜单动作, 输入上限 8 MB, 10 种语言.
- 未勾选条目代表规划, 不代表当前版本已支持; README 各语言版本的 Roadmap 章节均复述此约定.

## M0: 已交付基线 (v1.0.x)

- [x] (插件) 通过 `org.autojs.plugin.EXPLORER_ACTION` 注册单文件只读动作; 执行入口逐字段校验协议版本、动作 ID、content URI 结构、文件名、声明大小与只读授权 (`HtmlPreviewerIntentPolicy`).
- [x] (插件) Jsoup 净化管线: 移除脚本/事件属性/框架/嵌入对象/表单目标, 过滤链接与资源地址, 拦截内网与保留地址 (`HtmlPreviewerRenderer` + `HtmlPreviewerUrlPolicy`).
- [x] (插件) WebView 加固: 禁用 JavaScript/存储/Cookie/文件访问, 注入严格 CSP 与 no-referrer, 经 `WebViewAssetLoader` 虚拟域按父目录路径策略加载本地资源 (`HtmlPreviewerWebController` + `HtmlPreviewerPathHandler`).
- [x] (插件) 查看器体验: 深浅色自适应、双指缩放、刷新、全屏模式与 `以全屏模式启动` 偏好, 8 MB 上限与明确的错误提示, BOM 编码探测 (UTF-8/16/32).
- [x] (测试) 策略与净化的 JVM 单元测试 (IntentPolicy/PathPolicy/Renderer/RequestPolicy/TextCodec) 与 3 份 instrumentation 测试 (IntentPolicy/WebController/PluginContract).
- [x] (发布) v1.0.0 / v1.0.1 已发布并存档于 `app/releases`; 界面文本、使用说明、README 与 CHANGELOG 覆盖 10 种语言.

验收条件: 已随 v1.0.0 / v1.0.1 发布; 单元测试与 instrumentation 测试可在仓库内复跑通过.

## M1: 文档可读性与工程可信度

- [x] (发布) README 按 "简介 → 功能亮点 → 使用方法 → FAQ → 权限与安全" 的用户优先结构重写, 开发者内容 (插件接口/构建/资源结构) 后置, 10 种语言同步更新.
- [x] (发布) CHANGELOG 条目改为面向用户的表述 (标签 + 人话), 保留全部版本号与日期事实; README 内嵌最近 3 个版本.
- [x] (发布) `generate_markdown.py` 升级: `--check` 漂移检测、10 语言键位/列表形状/版本序一致性校验、全角符号与机翻占位符拦截、JSON 重复键拒绝、`version.properties` 版本联动、产物清单断言与孤儿文件检测.
- [x] (发布) 文档与应用文案锁定: 校验各语言 `strings.xml` 键位完整性, 且 `plugin_description` 与 README `text_plugin_synopsis` 逐字一致; `plugin_instruction.md` 全语言存在性校验.
- [x] (发布) 建立本 ROADMAP.md 并接入 README 的多语言 Roadmap 章节.
- [x] (发布) 在 CI 中接入 `.python/check_markdown.bat`, 文档源与生成物漂移时令 `Check generated documentation` 检查失败 (`.github/workflows/markdown.yml`).
- [ ] (发布) 在远端默认分支的规则/规则集中将 `Check generated documentation` 设为必需检查, 使文档漂移真正阻断合并; 待远端仓库可用后配置.

验收条件: `py .python/generate_markdown.py --check` 在干净工作区通过; 全部 25 份 README/CHANGELOG 产物仅由 JSON 源生成, 无手改痕迹.

## M2: 阅读体验增强

- [x] (插件) 页内查找: 工具栏查找入口, 基于 `WebView.findAllAsync` 的高亮、匹配计数与上一处/下一处导航 (`HtmlPreviewerWebControllerInstrumentationTest`).
- [x] (插件) 字号调节: 提供 75%-200% 的文本缩放分档 (`WebSettings.textZoom`), 持久化到偏好并即时生效.
- [x] (插件) 主题覆盖: 在跟随 AutoJs6 之外提供强制浅色/强制深色选项, 与注入样式的 `color-scheme` 声明联动.
- [x] (测试) 以上能力的 instrumentation 覆盖: 查找计数正确性、偏好跨进程重启持久化、主题切换后的背景色断言 (`HtmlPreviewerWebControllerInstrumentationTest` + `HtmlPreviewerPreferencesInstrumentationTest`).

验收条件: 新设置项在查看器重启后保持; 1 MB 级样本文档上查找与缩放无明显卡顿; 全部新增字符串同步补齐 10 种语言.

## M3: 编码与大文件兼容

- [x] (插件) 编码嗅探: 在 BOM 探测之外解析 `<meta charset>` 与 `http-equiv content-type` 声明, 支持 GBK、Big5、Shift_JIS、EUC-KR、windows-125x 等常见编码 (`HtmlPreviewerTextCodec`).
- [x] (插件) 超限处理: 超过 8 MB 的文件提供 "仅预览前 8 MB" 的截断预览选项, 并在界面顶部及预览末端明示截断位置.
- [x] (测试) 编码样本矩阵单元测试 (BOM/无 BOM x 各编码 x 声明缺失/冲突) 与超限文件回归 (`HtmlPreviewerTextCodecTest` + `HtmlPreviewerTruncatedPreviewerInstrumentationTest`).

验收条件: 无 BOM 且带 `<meta charset="gbk">` 声明的中文页面正确显示; 编码矩阵单元测试全绿; 截断预览不影响既有 8 MB 内文件的行为.

## M4: 隐私与资源控制

- [x] (插件) `加载网络图片` 开关 (默认开启): 关闭后 CSP `img-src` 收紧为 `'self' data:`, 渲染器同步剥离远程图片地址, WebView 层拒绝全部出站请求.
- [x] (插件) 拦截透明化: 在状态区展示本页被拦截的网络资源计数, 帮助用户理解 "为什么有内容没显示".
- [x] (测试) 开关两种状态下的出站行为断言: 关闭时除 `appassets.androidplatform.net` 虚拟域外零网络请求, 且 Activity 加载前后应用 UID 网络收发字节均无增长 (`HtmlPreviewerRequestPolicyTest` + `HtmlPreviewerNetworkImagesInstrumentationTest`).

验收条件: 关闭开关后以 WebView 分层策略断言和应用 UID 网络流量计数验证无出站连接; FAQ 与使用说明同步更新并覆盖 10 种语言.

## M5: 能力扩展 (逐项评估后实施)

### M5.1 设计: 查看源码模式

- 显示边界: 原始文本只作为 `<pre><code>` 内的文本节点写入可信文档外壳, 不进入 Jsoup 的 HTML 解析/净化分支; 标签、实体与事件属性均按字面量显示, 不产生可执行 DOM.
- 交互边界: 查看器溢出菜单提供 "查看源码" / "查看预览" 单次切换, 刷新保持当前模式, Activity 重建后恢复当前模式; 页内查找、字号、主题与截断提示在两种模式下语义一致.
- 资源边界: 源码模式忽略 `加载网络图片` 偏好并强制关闭 WebView 出站加载, CSP 仅允许插件自带样式及本地/内联展示资源; 切回净化预览后重新遵循用户的网络图片偏好.
- 性能边界: 沿用 8 MB 输入上限和前 8 MB 截断预览, 不额外保留一份完整源码副本, 切换时重新读取并在后台构建文档.

M5.1 验收条件: 恶意标签仅显示为可查找文本且 DOM 中无对应可执行节点; 源码模式下即使文本含远程资源地址也无出站请求; 一键往返切换、刷新、Activity 重建、主题、字号、查找与截断提示均正常; JVM 测试及 API 28/35 instrumentation 回归通过; 菜单文案、使用说明与 README 覆盖 10 种语言.

- [x] (插件) 查看源码模式: 只读等宽源码视图, 与净化预览一键切换 (`HtmlPreviewerViewMode` + `HtmlPreviewerSourceModeInstrumentationTest`).

### M5.2 设计: 导出 PDF

- 内容边界: 每次导出都重新只读读取输入并进入 `SANITIZED_PREVIEWER` 净化分支, 不直接打印当前屏幕或源码视图; 截断预览沿用已确认的前 8 MB 与末端标记, 网络图片继续遵循用户开关及既有 CSP/请求策略.
- 呈现边界: 打印目标强制使用浅色、100% 字号与 eager 图片策略, 在专用离屏 WebView 完成加载后才创建 `PrintDocumentAdapter`; 屏幕上的主题、缩放、查找、滚动位置与当前源码/预览模式不被改变.
- 框架边界: 仅由 Activity 调用 Android `PrintManager` 打开系统打印界面, 页张、方向、目标打印机或 `保存为 PDF` 路径均由系统/用户选择; 插件不直接创建目标文件, 不申请存储权限.
- 权限边界: 源清单仍仅声明 `INTERNET` 与插件权限; 合并清单另有 AndroidX 自动生成的应用内动态接收器签名保护权限, 不包含存储或媒体读取权限.
- 生命周期边界: 同一查看器同时只准备一个导出任务; 离屏 WebView 与适配器保持到 `onFinish`, 用户取消、启动失败或 Activity 销毁时释放资源, 不影响原查看器继续使用.

M5.2 验收条件: 从源码模式发起导出时生成的 PDF 只包含净化后的可见标记且不含脚本专属标记; 真实多页 PDF 可由 Android `PdfRenderer` 与 Poppler 打开并渲染, 页面非空、文字可读、无裁切/重叠/黑块; API 28/35 的适配器生命周期与取消清理测试通过; 源清单仍仅声明 `INTERNET` 与插件权限, 合并清单只额外包含 AndroidX 的应用内签名保护权限且不含存储/媒体权限; 菜单文案、FAQ/使用说明与 README 覆盖 10 种语言.

- [x] (插件) 导出 PDF: 经系统打印框架输出净化后的文档, 不引入新的设备数据权限 (`HtmlPreviewerPdfExporter` + `HtmlPreviewerPdfExporterInstrumentationTest`).

### M5.3 设计: MHTML (`.mht`/`.mhtml`) 支持

- 输入边界: 仅把 `.mht`/`.mhtml` 或明确的 MHTML MIME 类型识别为归档; 整包沿用 8 MB 上限且必须完整读取, 不提供会破坏 MIME 边界与传输编码的截断预览; 解析器同时限制部件数、嵌套层数及头部大小.
- MIME 边界: 按 `multipart/related` 的 `start` 参数选择根部件, 缺省时使用首部件; 支持受限的嵌套 multipart 与 `7bit`/`8bit`/`binary`、quoted-printable、Base64 解码, 根部件只接受 `text/html` 或 `application/xhtml+xml`.
- 资源边界: `Content-Location` 与 `Content-ID` 只作为归档内标签解析, 命中的图片、样式、字体及媒体资源改写到不可猜测含义的虚拟 HTTPS 路径, 不落盘、不写缓存、不把归档标签当成可信网络来源; 重复或歧义标签不参与映射, 未命中资源继续遵循既有本地资源与 `加载网络图片` 策略.
- 渲染边界: 解出的根 HTML 继续进入既有 Jsoup 净化、CSP、请求拦截及禁用 JavaScript/存储/Cookie/文件访问的 WebView 管线; 源码模式显示解码后的根 HTML, PDF 导出重新读取整包并复用同一净化结果与归档内资源.
- 兼容边界: 普通 HTML 的 8 MB 截断预览及同目录资源行为保持不变; MHTML 不新增存储或媒体权限, 刷新、主题、字号、页内查找与 Activity 重建沿用现有语义.

M5.3 验收条件: JVM 固件覆盖根部件选择、折叠头、三类恒等编码、quoted-printable/Base64、CID/相对与绝对 `Content-Location`、嵌套 multipart、重复标签、畸形边界、部件/层级/头部/整包限额及字符集; instrumentation 在 API 28/35 验证归档图片与 CSS 可见、脚本和危险部件不可执行、缺失资源遵循网络图片开关、源码/预览切换与 PDF 导出正常且无新增权限; FAQ、使用说明、README 与用户可见文案覆盖 10 种语言.

- [x] (插件) MHTML (`.mht`/`.mhtml`) 支持: 解包 multipart 归档后复用既有净化管线与资源策略 (`HtmlPreviewerMhtmlParser` + `HtmlPreviewerMhtmlInstrumentationTest`).

### M5.4 设计: Explorer Action 协议兼容治理

- 语义边界: HTML Previewer 继续注册 `FILE + SINGLE + READ_ONLY + OVERFLOW + ACTIVITY` 动作; 多选、目录动作、宿主原生归档浏览、输出事务与目标替换均不符合单文档只读查看器的产品边界, 不因宿主协议版本增长而自动启用.
- 协商边界: catalog 与执行 Intent 显式声明协议 v2, 不再从所编译 AAR 的 `ExplorerActionProtocol.VERSION` 隐式继承版本; 宿主按动作声明版本选择 legacy envelope, 因此 v4+ 的 `TARGETS`、仅目标 ClipData 与 `HOST_SESSION` 不能被误当成 v2 请求处理.
- 输入边界: 执行入口除既有动作 ID、URI、授权与来源校验外, 还要求宿主版本代码不低于 5269、协议恰为 v2、ClipData 恰含一个目标与一个父目录; 低版本、缺失版本、额外目标、目录或较新协议 envelope 一律失败关闭.
- 资源边界: 普通 HTML 当前依靠 v2 父目录 prefix URI 授权读取相对资源; 若未来迁移到 v4+ envelope, 必须另行设计基于 v12 `READ_SIBLINGS` host session 的有界资源适配, 同时重新评估最低宿主构建, 不允许只提升 catalog 版本而沿用旧父目录假设.
- 构建边界: vendored `explorer-action-api.aar` 固定为已审计的 v1 二进制及 SHA-256; 每次 Android 构建前校验摘要. 有意升级 AAR 时必须同时更新协议声明、Binder/Intent 适配、兼容矩阵、10 种语言说明及双 API 设备测试.
- 向前兼容边界: 宿主 v22 保留 `MIN_SUPPORTED_VERSION = 1`, catalog parser 对 v2 默认补齐 single/activity 语义, launcher 仍按动作声明版本生成两项 ClipData 的 legacy envelope; 未来宿主构建在继续满足这些条件时可向前兼容, 但只有完成源码与设备复核后才进入已审计范围.

| 宿主版本代码 | 宿主协议检查点 | 本插件声明 | 状态 |
| --- | --- | --- | --- |
| `< 5268` | Explorer Action 尚不可用 | v2 | 不支持 |
| `5268` | v1: 单文件只读动作 | v2 | 不支持主入口, 已排除 |
| `5269` | v2-v3: 主入口与输出事务 | v2 | 已审计, 仍走两 URI envelope |
| `5276` | v4-v21: 多目标、目录、host session、归档与事务扩展 | v2 | 已审计, 新能力不声明 |
| `5277` | v22: 有界 related-file 描述符 | v2 | 已审计, 仍走两 URI envelope |
| `5279` | v22: 当前宿主源码与设备 | v2 | 已审计, 主入口与溢出菜单 |
| `> 5279` | 后续协议 | v2 | 条件向前兼容, 尚未审计 |

M5.4 验收条件: 构建任务拒绝摘要漂移的 Explorer Action AAR; JVM 测试锁定声明协议、最低/最高已审计宿主、兼容矩阵连续性与动作形状; instrumentation 在 API 28/35 接受宿主构建 5269/5279 及未来构建发出的合法 v2 envelope, 拒绝低版本、缺失版本、协议 v22、额外 ClipData 目标与不完整授权; 当前宿主 v22 的 catalog policy/launcher 兼容测试通过; README、插件使用说明与独立兼容矩阵覆盖为何不启用多选/目录及未来升级触发条件.

- [x] (宿主/API) 跟进 explorer-action 协议演进 (多选、目录动作等), 维护与宿主版本代码的兼容矩阵 (`HtmlPreviewerExplorerCompatibility` + `docs/explorer-action-compatibility.md`).

验收条件: 每项能力动工前需单独补充设计说明与验收条件; 未立项条目不承诺时间表.

## 持续任务 (不绑定里程碑)

- 文档改动只编辑 `.readme/` 与 `.changelog/` 的 JSON 源, 随后运行 `py .python/generate_markdown.py` 重新生成全部 25 份产物; 生成物不手改.
- 每次发布前: 更新 `.changelog/lang_*.json` (10 种语言同步) 与 `version.properties`, 运行 `--check`, 再执行 `:app:assembleRelease` 与 `appendDigestToReleasedFiles`.
- 新增用户可见字符串时, 同步补齐 `values-*/strings.xml` 全部 10 种语言, 依赖 `generate_markdown.py` 的键位校验兜底.

## M6: 宿主外观与交互页面

- [x] (插件) 修复主入口协议, 设置开关样式与点击崩溃; 宿主语言/暗色配置和黑白对话框控件保持一致.
- [x] (插件) 渲染后提取页面边缘代表色用于工具栏和系统栏, 按亮度选择黑白前景; 复杂和动画背景取色后保持稳定至重新加载.
- [x] (插件/测试) 可选交互模式覆盖本地脚本、HTTPS CDN、按钮事件、DOM 存储和 WebGL; 默认/源码/PDF 禁用脚本的边界有回归测试. QV770340J7 的 snake3d.html 已验证开始游戏及暂停触屏操作.
