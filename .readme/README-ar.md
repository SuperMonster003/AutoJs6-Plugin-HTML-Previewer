<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-html-preview-ic-launcher" border="0" width="128" />
  </p>

  <p>مكون آمن لمعاينة HTML في مستكشف AutoJs6</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-HTML-Preview?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-HTML-Preview?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-HTML-Preview?color=534BAE&label=License"/></a>
  </p>
</div>

******

### اللغات (Languages)

******

يدعم README.md الحالي اللغات التالية:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-ru.md)
- العربية [ar] # الحالي

******

### مقدمة

******

يضيف مكون AutoJs6 HTML Preview إجراء معاينة HTML للقراءة فقط لملف واحد في مستكشف AutoJs6. ينقي محتوى الويب ويعرضه في عارض مخصص من دون تضمين التنفيذ في التطبيق المضيف.

******

### الميزات

******

- يسجل إجراء مستكشف للقراءة فقط لملف واحد عبر البروتوكول المشترك `org.autojs.plugin.EXPLORER_ACTION`.
- يتلقى إذن قراءة مؤقتا عبر content URI لملف HTML المحدد وموارد المجلد الأصل بدلا من مسارات نظام الملفات الخام.
- يزيل قبل العرض البرامج النصية وسمات معالجات الأحداث والإطارات والكائنات المضمنة وعناوين الموارد غير الآمنة.
- يحمل الموارد النسبية وموارد data وصور HTTPS المسموح بها وفق سياسات طلب مضبوطة.
- يوفر نمطا ثابتا للقراءة والتحديث ووضع ملء الشاشة وخيار البدء بملء الشاشة وحدا لحجم إدخال HTML.

******

### التنسيقات المدعومة

******

يتعرف الإصدار الأول على امتدادات الملفات التالية:

```text
html, htm, shtm, shtml, xht, xhtml
```

******

### واجهة المكون

******

يكتشف AutoJs6 المكون وينفذه باستخدام المعرفات التالية:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: html-preview
engine: explorer-action
variant: default
```

يقتصر الإصدار 1 على إجراء قائمة إضافية للقراءة فقط لملف واحد في مستكشف AutoJs6 الرئيسي.

******

### الأمان

******

ينقي العارض المستند ويعطل JavaScript وتخزين WebView وملفات تعريف الارتباط والوصول المباشر إلى الملفات ويقيد الموارد والتنقل بسياسات CSP و URI ولا يقبل إلا أذونات القراءة المؤقتة من المضيف.

******

### سجل الإصدارات

******

# v1.0.0

###### 2026/08/06

* `ميزة` مكون HTML Preview بالمعرف `html-preview` والمحرك `explorer-action` والمتغير `default`
* `ميزة` إجراء قائمة إضافية للقراءة فقط لملف واحد في مستكشف AutoJs6 الرئيسي عبر `org.autojs.plugin.EXPLORER_ACTION`
* `ميزة` تنفيذ عبر `org.autojs.plugin.EXPLORER_ACTION_EXECUTE` مع إذن قراءة مؤقت لـ content URI الخاص بالملف والمجلد الأصل
* `ميزة` تنقية HTML بإزالة البرامج النصية وسمات معالجات الأحداث والإطارات والكائنات المضمنة وعناوين الموارد غير الآمنة قبل العرض
* `ميزة` تحميل مضبوط للموارد النسبية وموارد data وصور HTTPS المسموح بها مع نمط ثابت وتحديث وملء الشاشة
* `ميزة` سياسة WebView معززة باستخدام CSP وتنقل URI مضبوط وتعطيل JavaScript والتخزين وحدود الإدخال
* `ميزة` ترجمة معلومات المكون والواجهة والتعليمات و README و changelog إلى الإسبانية والفرنسية والروسية والعربية واليابانية والكورية والإنجليزية والصينية المبسطة وصينية هونغ كونغ التقليدية وصينية تايوان التقليدية

##### لمزيد من سجل الإصدارات

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/app/src/main/assets/doc/CHANGELOG-ar.md)

******

### البناء

******

```powershell
.\gradlew.bat :app:assembleDebug
```

بناء Release:

```powershell
.\gradlew.bat :app:assembleRelease
```

تأتي معلمات البناء من `version.properties`. الحد الأدنى الحالي لـ SDK هو 24 والـ SDK المستهدف هو 36.

******

### بنية الموارد

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

يوفر `strings.xml` ترجمة معلومات المكون وواجهة العارض بينما يوفر `plugin_instruction.md` تعليمات يعرضها المضيف. ينشئ `.python/generate_markdown.py` ملفات README و changelog من مصادر JSON.

******

### الروابط

******

- وثائق AutoJs6: https://docs.autojs6.com
- HTML Living Standard: https://html.spec.whatwg.org
