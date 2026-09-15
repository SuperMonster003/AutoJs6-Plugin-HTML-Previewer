<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="html-previewer-ic-launcher" border="0" width="128" />
  </p>

  <p>Preview HTML and MHTML files</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Languages

******

The current README.md supports the following languages:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-zh-Hant-TW.md)
- English [en] # current
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ar.md)

******

### Introduction

******

One-tap viewing: open web files straight from the file manager via the `HTML Previewer` menu, with no browser required and no network needed for purely local pages.

Scripts are disabled by default. For a trusted, complete ordinary HTML file, enable `Interactive mode` in `Settings` to run JavaScript, button events, WebGL, and local storage. MHTML, truncated HTML, source view, and PDF export always keep scripts disabled.

******

### Features

******

- One-tap viewing: open web files straight from the file manager via the `HTML Previewer` menu, with no browser required and no network needed for purely local pages.
- Scripts are disabled by default. For a trusted, complete ordinary HTML file, enable `Interactive mode` in `Settings` to run JavaScript, button events, WebGL, and local storage. MHTML, truncated HTML, source view, and PDF export always keep scripts disabled.
- Safe source view: switch between the sanitized page and a read-only monospace view of the original HTML, or the decoded root HTML for MHTML; tags stay literal and source mode never makes network requests.
- System PDF export: choose `Export PDF` to send a freshly sanitized, light-themed copy to Android's print panel, where page settings and `Save as PDF` are handled without storage permission.
- Faithful layout: supports the page's own styles, permitted resources embedded in MHTML, local resources next to ordinary HTML such as images, fonts, audio, and video, plus optional HTTPS web images that can be disabled completely.
- Comfortable reading: includes in-page search, 75%-200% text sizing, pinch-to-zoom, and automatic, light, or dark themes. Menus and dialogs follow the AutoJs6 language and dark mode. GitHub (Auto), or the automatic HTML theme, follows AutoJs6 as well. The toolbar and system bars use one representative color sampled from the rendered page edges, with contrasting black or white text and icons. For gradients, images, and animation, that color stays fixed until reload to avoid flicker.
- Fullscreen mode: switch to immersive fullscreen at any time, or enable `Start in fullscreen mode` in the settings.
- External links: tapping an http/https link hands it over to the system browser, while in-page anchor jumps keep working.
- Multilingual: interface, instructions, README, and changelog are available in 10 languages.

******

### How to Use

******

1. Download the latest plugin APK from the [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/releases) page and install it on your device.
2. Open the AutoJs6 plugin center and enable the `HTML Previewer` plugin.
3. In the AutoJs6 file manager, locate the HTML or MHTML file you want to view and open its overflow menu (more actions).
4. Choose `HTML Previewer`; the page opens in a dedicated viewer.
5. While reading, use the top-right menu to find text, switch between `View source` and `View rendered page`, `Export PDF`, `Refresh`, toggle `Fullscreen mode`, or open `Settings` to adjust text size, theme, network images, and fullscreen startup; press Back to close search, leave fullscreen, or close the viewer.

> If the plugin does not appear in the plugin center, update AutoJs6 to a recent version first (internal build 5269 or later). Explorer Action v2 supports both the primary previewer button and the overflow menu for a single file, using temporary read grants for the document and its parent directory. AutoJs6 build 5269 or later is required.

******

### Supported Formats

******

The plugin recognizes the following filename extensions, plus extensionless files the host explicitly marks as `text/html`, `application/xhtml+xml`, `multipart/related`, or `application/x-mimearchive`:

```text
html, htm, shtm, shtml, xht, xhtml, mht, mhtml
```

Files up to 8 MB are loaded in full. Larger ordinary HTML files may be viewed only after confirming a clearly marked first-8 MB truncation; MHTML must stay complete and is rejected above the limit. A BOM takes priority when decoding HTML; otherwise a MIME charset or an early `meta charset` or `http-equiv` declaration is used, with UTF-8 as the fallback. MHTML supports bounded MIME nesting and the standard identity, quoted-printable, and Base64 transfer encodings.

******

### FAQ

******

#### Why do buttons and dynamic effects in the page not respond?

Scripts are disabled by default. For a trusted, complete ordinary HTML file, enable `Interactive mode` in `Settings` to run JavaScript, button events, WebGL, and local storage. MHTML, truncated HTML, source view, and PDF export always keep scripts disabled.

#### Why are some images or styles missing?

Local resources are read from the authorized directory, or from the selected MHTML archive. Safe mode allows public HTTPS images; interactive mode also allows public HTTPS scripts, styles, and requests when `Load network resources` is enabled. Turning the network switch off blocks outbound WebView requests. Interactive scripts can send page data to remote services, so enable this mode only for trusted pages.

#### The rendered page does not look exactly like in a browser. Is that normal?

In the default safe mode: Yes. Besides removing scripts, the viewer injects a base reading style and disables some advanced features. The goal is safe and legible reading rather than pixel-perfect rendering. Use a browser to verify the final appearance.

#### Does this plugin upload my files anywhere?

Local resources are read from the authorized directory, or from the selected MHTML archive. Safe mode allows public HTTPS images; interactive mode also allows public HTTPS scripts, styles, and requests when `Load network resources` is enabled. Turning the network switch off blocks outbound WebView requests. Interactive scripts can send page data to remote services, so enable this mode only for trusted pages.

#### Where is an exported PDF saved?

`Export PDF` opens Android's system print panel. Choose `Save as PDF`, then choose the file name and destination in the system picker. The plugin never writes directly to storage or requests storage permission. Export always rereads the HTML or complete MHTML archive and sanitizes the root page, even when started from source view, and leaves the current viewer state unchanged.

#### Can I edit HTML files or view a whole website project?

The current version is scoped to single-file read-only viewing: there is no editing capability and no directory browsing. Resources next to ordinary HTML are read on demand, while MHTML resources stay inside the selected archive; the plugin only ever holds the temporary read permission granted by the host. See [ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/ROADMAP.md) for what is planned.

******

### Permissions and Security

******

Interactive mode is off for every new preview and must be enabled explicitly for the currently trusted document. CSP blocks fetch, WebSocket, workers and frames. Interactive JavaScript is not a general network sandbox: APIs such as WebRTC may use the network independently of the resource switch. Keep unfamiliar documents in the default static mode.

- In the default safe mode: Sanitize before display: scripts, event handler attributes, embedded frames, embedded objects, and form targets are removed, and unsafe link or resource addresses are filtered out.
- Cookies, direct file/content access, native JavaScript bridges, and new windows remain disabled. Safe mode disables page storage; interactive mode allows local storage for page preferences and game scores. Source view and PDF use a fresh sanitized rendering.
- Interactive mode is off for every new preview and must be enabled explicitly for the currently trusted document. CSP blocks fetch, WebSocket, workers and frames. Interactive JavaScript is not a general network sandbox: APIs such as WebRTC may use the network independently of the resource switch. Keep unfamiliar documents in the default static mode.
- Least privilege: the plugin only receives the temporary content URI read permission granted by the host, never sees filesystem paths, and sends PDF output through Android's system print service instead of writing to storage itself.
- Bounded input: reads are capped at 8 MB; MHTML also limits MIME parts, nesting, headers, and decoded bodies; file names and resource paths are strictly validated to prevent out-of-scope access.
- Enable only for this trusted document. Scripts can use network APIs outside the resource switch, including WebRTC. Every new preview starts with scripts off.
- Static preview and the controlled HTTPS resource client validate and bind public DNS addresses and recheck every redirect. The resource switch controls this client; requests are limited to GET and HEAD.

In the default safe mode: The source manifest requests only the network permission (used when HTTPS images are enabled) and the AutoJs6 plugin permission. AndroidX also contributes a package-scoped signature permission that protects non-exported dynamic receivers; it grants no access to device data. Turning network images off prevents WebView outbound requests. PDF destination access belongs to Android's system print and file-picker UI, so the plugin requests no storage, media, camera, location, or other sensitive permissions.

******

### Plugin Interface

******

The following information is for developers; the host discovers and executes the plugin with these identities:

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

Explorer Action v2 supports both the primary previewer button and the overflow menu for a single file, using temporary read grants for the document and its parent directory. AutoJs6 build 5269 or later is required.

- [View the Explorer Action compatibility matrix](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/docs/explorer-action-compatibility.md)

******

### Roadmap

******

Planned capabilities and their completion status are tracked as a checkable list in ROADMAP.md, organized by milestones with acceptance criteria and covering in-page search, adjustable text size, broader encoding support, a switch for web images, and more. Unchecked items describe plans rather than shipped capabilities. Feedback via Issues is welcome.

- [View ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/ROADMAP.md)

******

### Release History

******

#### v1.1.1

_2026/09/15_

- `Improvement` Raise compileSdk to 37 (Android 17); targetSdk stays at 36 until the behavior that depends on the target is verified

#### v1.1.0

_2026/09/13_

- `Feature` Local release history is available from the interface, with localized text and an English fallback
- `Fix` Static preview and the controlled HTTPS resource client validate and bind public DNS addresses and recheck every redirect. The resource switch controls this client; requests are limited to GET and HEAD.
- `Fix` Enable only for this trusted document. Scripts can use network APIs outside the resource switch, including WebRTC. Every new preview starts with scripts off.
- `Fix` Preserve HTTP 206 and other successful status codes when loading remote resources
- `Improvement` Release packages are checked for a complete signing configuration, exact APK contents and reproducible documentation
- `Dependency` Add OkHttp 4.12.0 for controlled HTTPS resource loading

#### v1.0.1

_2026/09/11_

- `Feature` Scripts are disabled by default. For a trusted, complete ordinary HTML file, enable `Interactive mode` in `Settings` to run JavaScript, button events, WebGL, and local storage. MHTML, truncated HTML, source view, and PDF export always keep scripts disabled.
- `Fix` An issue where enabling the plugin in the plugin center could fail with an error
- `Fix` Fixed primary previewer protocol rejection and settings crashes; synchronized the host appearance, page chrome, and monochrome dialog controls
- `Improvement` Leaner plugin name and description, easier-to-read user documentation
- `Improvement` Build verification rejects accidental native dependencies and produces a JSON report

##### For more release history

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/app/src/main/assets/doc/CHANGELOG-en.md)

******

### Build

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Release build:

```powershell
.\gradlew.bat :app:assembleRelease
```

Build parameters come from `version.properties`. The current minimum SDK is 24 and the target SDK is 36.

******

### Localization and Docs Generation

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

`strings.xml` localizes plugin metadata and the viewer UI, while `plugin_instruction.md` provides host-visible usage instructions. For README and changelog, always edit the JSON sources under `.readme/` and `.changelog/`, then run `py .python/generate_markdown.py` to regenerate; generated files are never edited by hand. Run `py .python/generate_markdown.py --check` to verify that sources and artifacts are in sync.

******

### Links

******

- AutoJs6 documentation: https://docs.autojs6.com
- HTML Living Standard: https://html.spec.whatwg.org


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/docs/16kb.md)
