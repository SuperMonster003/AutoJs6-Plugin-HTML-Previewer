<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="html-preview-ic-launcher" border="0" width="128" />
  </p>

  <p>ファイルマネージャープラグイン. HTML ファイルを安全に読み取り専用でプレビュー</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-HTML-Preview?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-HTML-Preview?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-HTML-Preview?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 言語 (Languages)

******

現在の README.md は次の言語をサポートします:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-es.md)
- 日本語 [ja] # 現在
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-ar.md)

******

### 概要

******

HTML Preview はファイルマネージャーに単一ファイル用の読み取り専用 HTML プレビューアクションを追加します. Web コンテンツをサニタイズし, ホストアプリに実装を組み込まず専用ビューアーで表示します.

******

### 機能

******

- 共有 `org.autojs.plugin.EXPLORER_ACTION` プロトコルで単一ファイル用の読み取り専用アクションを登録します.
- 生のファイルシステムパスではなく content URI で選択した HTML ファイルと親ディレクトリのリソースへの一時読み取り権限を受け取ります.
- 表示前にスクリプト, イベントハンドラー属性, フレーム, 埋め込みオブジェクト, 安全でないリソースアドレスを削除します.
- 許可された相対リソース, data リソース, HTTPS 画像を制御された要求ポリシーで読み込みます.
- 固定の読みやすいスタイル, 再読み込み, 全画面モード, 全画面開始設定, HTML 入力サイズ制限を提供します.

******

### 対応形式

******

最初のリリースは次のファイル拡張子を認識します:

```text
html, htm, shtm, shtml, xht, xhtml
```

******

### プラグインインターフェース

******

ホストは次の識別情報でプラグインを検出して実行します:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: html-preview
engine: explorer-action
variant: default
```

バージョン 1 はファイルマネージャーにある単一ファイル用の読み取り専用オーバーフローアクションに限定されます.

******

### セキュリティ

******

ビューアーは文書をサニタイズし, JavaScript, WebView ストレージ, Cookie, 直接ファイルアクセスを無効化します. CSP と URI ポリシーでリソースとナビゲーションを制限し, ホストが付与した一時読み取り権限だけを受け入れます.

******

### リリース履歴

******

# v1.0.1

###### 2026/08/08

* `修正` プラグインセンターでの有効化を妨げていたサービスの null バインディング
* `改善` より簡潔なプラグイン名, 説明, ユーザードキュメント

# v1.0.0

###### 2026/08/06

* `機能` プラグイン ID `html-preview`, エンジン `explorer-action`, バリアント `default` の HTML Preview プラグイン
* `機能` `org.autojs.plugin.EXPLORER_ACTION` によるファイルマネージャーの単一ファイル用読み取り専用オーバーフローアクション
* `機能` `org.autojs.plugin.EXPLORER_ACTION_EXECUTE` による実行とファイルおよび親ディレクトリ content URI への一時読み取り権限
* `機能` 表示前にスクリプト/イベントハンドラー属性/フレーム/埋め込みオブジェクト/安全でないリソースアドレスを削除する HTML サニタイズ
* `機能` 許可された相対リソース/data リソース/HTTPS 画像の制御された読み込みと固定スタイル/再読み込み/全画面コントロール
* `機能` CSP/制御された URI ナビゲーション/JavaScript とストレージの無効化/入力サイズ制限による WebView 保護
* `機能` スペイン語/フランス語/ロシア語/アラビア語/日本語/韓国語/英語/簡体字中国語/香港繁体字/台湾繁体字のプラグイン情報/インターフェース/使用説明/README/changelog

##### その他のリリース履歴

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/app/src/main/assets/doc/CHANGELOG-ja.md)

******

### ビルド

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Release ビルド:

```powershell
.\gradlew.bat :app:assembleRelease
```

ビルド設定は `version.properties` から読み込みます. 現在の最小 SDK は 24, ターゲット SDK は 36 です.

******

### リソース構成

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` はプラグイン情報とビューアー UI をローカライズし, `plugin_instruction.md` はホストに表示する使用説明を提供します. README と changelog は `.python/generate_markdown.py` が JSON ソースから生成します.

******

### リンク

******

- AutoJs6 ドキュメント: https://docs.autojs6.com
- HTML Living Standard: https://html.spec.whatwg.org
