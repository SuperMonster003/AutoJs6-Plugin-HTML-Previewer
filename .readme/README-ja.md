<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="html-previewer-ic-launcher" border="0" width="128" />
  </p>

  <p>ファイルマネージャープラグイン. HTML / MHTML ファイルを安全に読み取り専用でプレビュー</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 言語 (Languages)

******

現在の README.md は次の言語をサポートします:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-es.md)
- 日本語 [ja] # 現在
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ar.md)

******

### 概要

******

ワンタップでプレビュー: ファイルマネージャーの `HTML をプレビュー` メニューからウェブファイルを直接開けます. ブラウザーは不要で, ローカルのみのページなら通信も不要です.

スクリプトは既定で無効です. 信頼できる完全な通常 HTML ファイルでは設定のインタラクティブモードを有効にすると JavaScript, ボタンイベント, WebGL, ローカルストレージが使えます. MHTML, 切り詰めた HTML, ソース表示, PDF 出力では常にスクリプトを無効にします.

******

### 主な機能

******

- ワンタップでプレビュー: ファイルマネージャーの `HTML をプレビュー` メニューからウェブファイルを直接開けます. ブラウザーは不要で, ローカルのみのページなら通信も不要です.
- スクリプトは既定で無効です. 信頼できる完全な通常 HTML ファイルでは設定のインタラクティブモードを有効にすると JavaScript, ボタンイベント, WebGL, ローカルストレージが使えます. MHTML, 切り詰めた HTML, ソース表示, PDF 出力では常にスクリプトを無効にします.
- 安全なソース表示: サニタイズ済みプレビューと読み取り専用の元 HTML を切り替えられます. MHTML ではデコード済みルート HTML を表示し, タグは常に文字として扱われ, ネットワークリクエストを一切送信しません.
- システム PDF 書き出し: `PDF をエクスポート` を選ぶと, 再度サニタイズしてライトテーマに固定したコピーを Android の印刷画面へ渡します. ページ設定と `PDF として保存` はシステムが処理するため, ストレージ権限は不要です.
- レイアウト再現: ページ自身のスタイル, MHTML 内の許可された埋め込みリソース, 通常 HTML と同じディレクトリの画像, フォント, 音声, 動画, および完全に無効化できる HTTPS ウェブ画像に対応します.
- 快適な閲覧: ページ内検索, 75%-200% の文字サイズ, ピンチズーム, システム/ライト/ダークテーマに対応します. メニューとダイアログは AutoJs6 の言語とダークモードに従います. GitHub (Auto) と HTML 自動テーマも同様です. ツールバーとシステムバーには描画領域の端から取得した代表色を使い, 文字とアイコンは対比のある黒または白にします. グラデーション, 画像, アニメーションでは再読み込みまで色を固定してちらつきを防ぎます.
- 全画面モード: いつでも没入型の全画面に切り替えられ, 設定で `全画面モードで開始` を有効化できます.
- 外部リンク: ページ内の http/https リンクをタップするとシステムブラウザーに引き渡し, ページ内アンカーへの移動はそのまま機能します.
- 多言語対応: インターフェース, 使用説明, README, changelog を 10 言語で提供します.

******

### 使い方

******

1. [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/releases) ページから最新のプラグイン APK をダウンロードして端末にインストールします.
2. AutoJs6 のプラグインセンターを開き, `HTML プレビュー` プラグインを有効化します.
3. AutoJs6 のファイルマネージャーで表示したい HTML または MHTML ファイルを見つけ, そのオーバーフローメニュー (その他の操作) を開きます.
4. `HTML をプレビュー` を選ぶと, ページが専用ビューアーで開きます.
5. 閲覧中は右上のメニューから文字検索, `ソースを表示` と `プレビューを表示` の切り替え, `PDF をエクスポート`, `再読み込み`, `全画面モード` の切り替えができ, `設定` で文字サイズ, テーマ, ネットワーク画像, 全画面起動を調整できます. 戻るキーで検索を閉じる, 全画面を終了する, またはビューアーを閉じます.

> プラグインセンターにこのプラグインが表示されない場合は, まず AutoJs6 を新しいバージョン (内部ビルド 5269 以降) に更新してください. Explorer Action v2 は単一ファイルのメインプレビューボタンとメニューに対応し, 文書と親ディレクトリへの一時的な読み取り権限を使用します. AutoJs6 ビルド 5269 以降が必要です.

******

### 対応形式

******

プラグインは次の拡張子を認識し, ホストが `text/html`, `application/xhtml+xml`, `multipart/related`, `application/x-mimearchive` のいずれかと明示した拡張子なしファイルも受け付けます:

```text
html, htm, shtm, shtml, xht, xhtml, mht, mhtml
```

8 MB 以下のファイルは全体を読み込みます. より大きな通常 HTML は確認後に先頭 8 MB のみを切り詰め位置付きで表示できますが, MHTML は完全な状態が必要なため上限超過時は拒否します. HTML デコードは BOM, MIME 文字セット, 先頭付近の `meta charset` または `http-equiv`, UTF-8 の順で使用します. MHTML は上限付き MIME ネストと identity, quoted-printable, Base64 転送エンコーディングに対応します.

******

### よくある質問

******

#### ページ内のボタンや動的な効果が反応しないのはなぜですか?

スクリプトは既定で無効です. 信頼できる完全な通常 HTML ファイルでは設定のインタラクティブモードを有効にすると JavaScript, ボタンイベント, WebGL, ローカルストレージが使えます. MHTML, 切り詰めた HTML, ソース表示, PDF 出力では常にスクリプトを無効にします.

#### 一部の画像やスタイルが表示されないのはなぜですか?

ローカル資源は許可されたディレクトリまたは選択した MHTML 内から読み込みます. 安全モードでは公開 HTTPS 画像を許可し, インタラクティブモードではネットワーク資源を有効にすると公開 HTTPS のスクリプト, スタイル, 通信も許可します. ネットワークを無効にすると WebView の外部通信を遮断します. スクリプトはページのデータを外部へ送信できるため, 信頼できるページでのみ有効にしてください.

#### プレビューがブラウザーの表示と完全には一致しません. 正常ですか?

既定の安全モードでは: 正常です. スクリプトの除去に加えて, ビューアーは基本の読書用スタイルを注入し, 一部の高度な機能を無効化します. 目的は安全で読みやすい閲覧であり, ピクセル単位の再現ではありません. 最終的な表示はブラウザーで確認してください.

#### このプラグインはファイルをどこかにアップロードしますか?

ローカル資源は許可されたディレクトリまたは選択した MHTML 内から読み込みます. 安全モードでは公開 HTTPS 画像を許可し, インタラクティブモードではネットワーク資源を有効にすると公開 HTTPS のスクリプト, スタイル, 通信も許可します. ネットワークを無効にすると WebView の外部通信を遮断します. スクリプトはページのデータを外部へ送信できるため, 信頼できるページでのみ有効にしてください.

#### 書き出した PDF はどこに保存されますか?

`PDF をエクスポート` は Android のシステム印刷画面を開きます. `PDF として保存` を選び, システムのファイル選択画面で名前と保存先を指定してください. プラグインはストレージへ直接書き込まず, ストレージ権限も要求しません. ソース表示から開始した場合でも HTML または完全な MHTML を再読み込みしてルートページをサニタイズし, 現在のビューアー状態は変更しません.

#### HTML ファイルの編集や, ウェブサイト全体のプレビューはできますか?

現在のバージョンは単一ファイルの読み取り専用プレビューに特化しています: 編集機能はなく, ディレクトリの列挙も行いません. ファイルと同じディレクトリのリソースは必要に応じて読み取られますが, プラグインが持つのはホストから付与された一時的な読み取り権限だけです. 今後の計画は [ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/ROADMAP.md) を参照してください.

******

### 権限とセキュリティ

******

プラグインはプレビュー対象のファイルを信頼できないものとみなし, 多層防御を適用します:

- 既定の安全モードでは: 表示前のサニタイズ: スクリプト, イベントハンドラー属性, 埋め込みフレーム, 埋め込みオブジェクト, フォーム送信先を削除し, 安全でないリンクやリソースのアドレスを除外します.
- Cookie, ファイル/content への直接アクセス, ネイティブ JavaScript ブリッジ, 新規ウィンドウは無効のままです. インタラクティブモードのみ設定やスコア用のローカルストレージを許可します. ソース表示と PDF には再度無害化した内容を使用します.
- ローカル資源は許可されたディレクトリまたは選択した MHTML 内から読み込みます. 安全モードでは公開 HTTPS 画像を許可し, インタラクティブモードではネットワーク資源を有効にすると公開 HTTPS のスクリプト, スタイル, 通信も許可します. ネットワークを無効にすると WebView の外部通信を遮断します. スクリプトはページのデータを外部へ送信できるため, 信頼できるページでのみ有効にしてください.
- 最小権限: プラグインはホストが付与する一時的な content URI 読み取り権限だけを受け取り, ファイルシステムのパスには触れられません. PDF 出力は自身でストレージへ書き込まず Android のシステム印刷サービスへ渡します.
- 入力の上限: 読み取りは 8 MB までで, MHTML は部品数, ネスト, ヘッダー, デコード量も制限します. ファイル名とリソースパスは厳密に検証されて範囲外アクセスを防ぎます.
- Cookie, ファイル/content への直接アクセス, ネイティブ JavaScript ブリッジ, 新規ウィンドウは無効のままです. インタラクティブモードのみ設定やスコア用のローカルストレージを許可します. ソース表示と PDF には再度無害化した内容を使用します.

既定の安全モードでは: ソースマニフェストが要求するのはネットワーク権限 (HTTPS 画像が有効な場合に使用) と AutoJs6 プラグイン権限だけです. AndroidX は未公開の動的レシーバーを保護するため, パッケージ限定の署名権限も自動追加しますが, 端末データへのアクセスは一切付与しません. ネットワーク画像を無効にすると WebView は外向きリクエストを行いません. PDF の保存先へのアクセスは Android の印刷画面とファイル選択画面が担うため, ストレージ, メディア, カメラ, 位置情報などの機微な権限は要求しません.

******

### プラグインインターフェース

******

以下は開発者向けの情報です. ホストは次の識別情報でプラグインを検出して実行します:

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

Explorer Action v2 は単一ファイルのメインプレビューボタンとメニューに対応し, 文書と親ディレクトリへの一時的な読み取り権限を使用します. AutoJs6 ビルド 5269 以降が必要です.

- [Explorer Action 互換性マトリックスを表示](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/docs/explorer-action-compatibility.md)

******

### ロードマップ

******

予定機能とその進捗は ROADMAP.md でチェック可能なリストとして管理され, マイルストーンごとに受け入れ条件が付きます. ページ内検索, 文字サイズ調整, 追加エンコーディング対応, ウェブ画像のオン/オフなどを扱います. 未チェックの項目は計画であり, 現行バージョンで利用できる機能ではありません. Issues でのフィードバックを歓迎します.

- [ROADMAP.md を見る](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/ROADMAP.md)

******

### リリース履歴

******

#### v1.0.1

_2026/08/08_

- `機能` スクリプトは既定で無効です. 信頼できる完全な通常 HTML ファイルでは設定のインタラクティブモードを有効にすると JavaScript, ボタンイベント, WebGL, ローカルストレージが使えます. MHTML, 切り詰めた HTML, ソース表示, PDF 出力では常にスクリプトを無効にします.
- `修正` プラグインセンターでプラグインを有効化するとエラーになることがある問題
- `修正` メインプレビューボタンのプロトコル拒否と設定のクラッシュを修正し, ホストの外観, バーの配色, ダイアログの白黒コントロールを同期
- `改善` より簡潔なプラグイン名と説明, より読みやすいユーザードキュメント

#### v1.0.0

_2026/08/06_

- `機能` 初回リリース: AutoJs6 ファイルマネージャーの HTML ファイルに `HTML をプレビュー` メニューアクションを追加 (プラグイン ID `html-previewer`)
- `機能` 安全なビューアー: 表示前にスクリプト, フレーム, 埋め込みオブジェクト, フォーム送信を削除し, 全体を読み取り専用にして JavaScript を一切実行しない
- `機能` リソース読み込み: ページのスタイル, ファイルと同じディレクトリのローカルリソース, HTTPS ウェブ画像を制御されたルールで読み込み, それ以外のリクエストはすべてブロック
- `機能` 閲覧体験: ライト/ダークテーマ自動追従, ピンチ操作での拡大縮小, 再読み込み, 全画面モード, `全画面モードで開始` 設定
- `機能` 安全境界: ホストが付与する一時的な読み取り権限のみを受け入れ, 1 ファイルの上限は 8 MB, ファイル名とリソースパスを厳密に検証
- `機能` 多言語対応: インターフェース, 使用説明, README, changelog を 10 言語で提供

##### その他のリリース履歴

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/app/src/main/assets/doc/CHANGELOG-ja.md)

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

### ローカライズとドキュメント生成

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

`strings.xml` はプラグイン情報とビューアー UI をローカライズし, `plugin_instruction.md` はホストに表示する使用説明を提供します. README と changelog は必ず `.readme/` と `.changelog/` の JSON ソースを編集し, `py .python/generate_markdown.py` を実行して再生成します. 生成物を手で編集することはありません. `py .python/generate_markdown.py --check` でソースと生成物の同期を検証できます.

******

### リンク

******

- AutoJs6 ドキュメント: https://docs.autojs6.com
- HTML Living Standard: https://html.spec.whatwg.org
