<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="html-previewer-ic-launcher" border="0" width="128" />
  </p>

  <p>File manager plugin. Secure read-only preview for HTML files</p>

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

HTML Previewer adds a single-file read-only HTML preview action to the file manager. It sanitizes and displays web content in a focused viewer without embedding the preview implementation in the host application.

******

### Features

******

- Registers a single-file read-only Explorer action through the shared `org.autojs.plugin.EXPLORER_ACTION` protocol.
- Receives temporary content URI read access to the selected HTML file and its parent directory resources instead of raw filesystem paths.
- Removes scripts, event handler attributes, frames, embedded objects, and unsafe resource addresses before display.
- Loads permitted relative resources, data resources, and HTTPS images under guarded request policies.
- Provides fixed readable styling, refresh, fullscreen mode, a start-in-fullscreen preference, and a bounded HTML input size.

******

### Supported Formats

******

The first release recognizes the following filename extensions:

```text
html, htm, shtm, shtml, xht, xhtml
```

******

### Plugin Interface

******

The host discovers and executes the plugin with the following identities:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: html-previewer
engine: explorer-action
variant: default
```

Version 1 is limited to a single-file read-only overflow action in the file manager.

******

### Security

******

The viewer sanitizes the document, disables JavaScript, WebView storage, cookies, and direct file access, constrains resources and navigation with CSP and URI policies, and only accepts temporary read permissions granted by the host.

******

### Release History

******

# v1.0.1

###### 2026/08/08

* `Fix` Null service binding that prevented activation in Plugin Center
* `Improvement` Clearer plugin name, description, and user documentation

# v1.0.0

###### 2026/08/06

* `Feature` HTML Previewer plugin with plugin ID `html-previewer`, engine `explorer-action`, and variant `default`
* `Feature` Single-file read-only overflow action in the file manager through `org.autojs.plugin.EXPLORER_ACTION`
* `Feature` Activity execution through `org.autojs.plugin.EXPLORER_ACTION_EXECUTE` with temporary read access to file and parent directory content URIs
* `Feature` HTML sanitization that removes scripts, event handler attributes, frames, embedded objects, and unsafe resource addresses before display
* `Feature` Guarded loading of permitted relative resources, data resources, and HTTPS images with fixed styling, refresh, and fullscreen controls
* `Feature` Hardened WebView policy with CSP, guarded URI navigation, disabled JavaScript and storage, and bounded input
* `Feature` Localized plugin metadata, interface text, usage instructions, README files, and changelogs in Spanish, French, Russian, Arabic, Japanese, Korean, English, Simplified Chinese, Hong Kong Traditional Chinese, and Taiwan Traditional Chinese

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

### Resource Layout

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` localizes plugin metadata and viewer UI, while `plugin_instruction.md` provides host-visible usage instructions. README and changelog files are generated from JSON sources by `.python/generate_markdown.py`.

******

### Links

******

- AutoJs6 documentation: https://docs.autojs6.com
- HTML Living Standard: https://html.spec.whatwg.org
