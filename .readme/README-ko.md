<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="html-preview-ic-launcher" border="0" width="128" />
  </p>

  <p>파일 관리자 플러그인. HTML 파일의 안전한 읽기 전용 미리보기</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-HTML-Preview?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-HTML-Preview?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-HTML-Preview?color=534BAE&label=License"/></a>
  </p>
</div>

******

### 언어 (Languages)

******

현재 README.md는 다음 언어를 지원합니다:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-ja.md)
- 한국어 [ko] # 현재
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-ar.md)

******

### 소개

******

HTML Preview는 파일 관리자에 단일 파일 읽기 전용 HTML 미리보기 작업을 추가합니다. 웹 콘텐츠를 정리한 뒤 호스트 앱에 구현을 포함하지 않고 전용 뷰어에서 표시합니다.

******

### 기능

******

- 공유 `org.autojs.plugin.EXPLORER_ACTION` 프로토콜로 단일 파일 읽기 전용 탐색기 작업을 등록합니다.
- 원시 파일 시스템 경로 대신 content URI를 통해 선택한 HTML 파일과 상위 디렉터리 리소스에 대한 임시 읽기 권한을 받습니다.
- 표시 전에 스크립트, 이벤트 처리기 속성, 프레임, 포함된 개체, 안전하지 않은 리소스 주소를 제거합니다.
- 허용된 상대 리소스, data 리소스, HTTPS 이미지를 제어된 요청 정책으로 불러옵니다.
- 고정된 읽기 스타일, 새로 고침, 전체 화면 모드, 전체 화면 시작 설정, HTML 입력 크기 제한을 제공합니다.

******

### 지원 형식

******

첫 번째 릴리스는 다음 파일 확장자를 인식합니다:

```text
html, htm, shtm, shtml, xht, xhtml
```

******

### 플러그인 인터페이스

******

호스트는 다음 식별 정보로 플러그인을 검색하고 실행합니다:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: html-preview
engine: explorer-action
variant: default
```

버전 1은 파일 관리자의 단일 파일 읽기 전용 더보기 작업으로 제한됩니다.

******

### 보안

******

뷰어는 문서를 정리하고 JavaScript, WebView 저장소, 쿠키, 직접 파일 접근을 비활성화합니다. CSP 및 URI 정책으로 리소스와 탐색을 제한하고 호스트가 부여한 임시 읽기 권한만 허용합니다.

******

### 릴리스 기록

******

# v1.0.1

###### 2026/08/08

* `수정` 플러그인 센터 활성화를 막던 null 서비스 바인딩
* `개선` 더 간결한 플러그인 이름, 설명 및 사용자 문서

# v1.0.0

###### 2026/08/06

* `기능` 플러그인 ID `html-preview`, 엔진 `explorer-action`, 변형 `default`인 HTML Preview 플러그인
* `기능` `org.autojs.plugin.EXPLORER_ACTION`을 통한 파일 관리자의 단일 파일 읽기 전용 더보기 작업
* `기능` `org.autojs.plugin.EXPLORER_ACTION_EXECUTE`을 통한 실행과 파일 및 상위 디렉터리 content URI 임시 읽기 권한
* `기능` 표시 전에 스크립트/이벤트 처리기 속성/프레임/포함된 개체/안전하지 않은 리소스 주소를 제거하는 HTML 정리
* `기능` 허용된 상대 리소스/data 리소스/HTTPS 이미지의 제어된 불러오기와 고정 스타일/새로 고침/전체 화면 제어
* `기능` CSP/제어된 URI 탐색/JavaScript 및 저장소 비활성화/입력 크기 제한으로 강화된 WebView 정책
* `기능` 스페인어/프랑스어/러시아어/아랍어/일본어/한국어/영어/중국어 간체/홍콩 번체/대만 번체 플러그인 정보/인터페이스/사용 설명/README/changelog

##### 더 많은 릴리스 기록

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/app/src/main/assets/doc/CHANGELOG-ko.md)

******

### 빌드

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Release 빌드:

```powershell
.\gradlew.bat :app:assembleRelease
```

빌드 매개변수는 `version.properties`에서 가져옵니다. 현재 최소 SDK는 24이고 대상 SDK는 36입니다.

******

### 리소스 구조

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml`은 플러그인 정보와 뷰어 UI를 현지화하고 `plugin_instruction.md`는 호스트에 표시되는 사용 설명을 제공합니다. README 및 changelog 파일은 `.python/generate_markdown.py`가 JSON 소스에서 생성합니다.

******

### 링크

******

- AutoJs6 문서: https://docs.autojs6.com
- HTML Living Standard: https://html.spec.whatwg.org
