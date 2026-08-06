<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-html-preview-ic-launcher" border="0" width="128" />
  </p>

  <p>Plugin de prévisualisation HTML sécurisé pour l'explorateur AutoJs6</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-HTML-Preview?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-HTML-Preview?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-HTML-Preview?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Langues (Languages)

******

Le fichier README.md actuel prend en charge les langues suivantes:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-en.md)
- Français [fr] # actuel
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/.readme/README-ar.md)

******

### Introduction

******

Le plugin AutoJs6 HTML Preview ajoute une action de prévisualisation HTML en lecture seule pour un fichier dans l'explorateur AutoJs6. Il nettoie et affiche le contenu web dans une visionneuse dédiée sans intégrer cette fonction dans l'application hôte.

******

### Fonctions

******

- Enregistre une action de lecture seule pour un fichier avec le protocole partagé `org.autojs.plugin.EXPLORER_ACTION`.
- Reçoit un accès temporaire en lecture par URI de contenu au fichier HTML sélectionné et aux ressources de son dossier parent, sans chemin brut du système de fichiers.
- Supprime les scripts, les attributs de gestionnaires d'événements, les cadres, les objets intégrés et les adresses de ressources non sûres avant affichage.
- Charge les ressources relatives, les ressources data et les images HTTPS autorisées avec des règles de requête contrôlées.
- Propose un style lisible fixe, l'actualisation, le mode plein écran, une préférence de démarrage en plein écran et une taille HTML limitée.

******

### Formats pris en charge

******

La première version reconnaît les extensions de fichier suivantes:

```text
html, htm, shtm, shtml, xht, xhtml
```

******

### Interface du plugin

******

AutoJs6 découvre et exécute le plugin avec les identités suivantes:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: html-preview
engine: explorer-action
variant: default
```

La version 1 est limitée à une action de menu secondaire en lecture seule pour un fichier dans l'explorateur AutoJs6 principal.

******

### Sécurité

******

La visionneuse nettoie le document, désactive JavaScript, le stockage WebView, les cookies et l'accès direct aux fichiers, limite les ressources et la navigation par CSP et règles URI, et accepte uniquement les autorisations temporaires accordées par l'hôte.

******

### Historique des versions

******

# v1.0.0

###### 2026/08/06

* `Fonctionnalité` Plugin HTML Preview avec ID `html-preview`, moteur `explorer-action` et variante `default`
* `Fonctionnalité` Action de menu secondaire en lecture seule pour un fichier dans l'explorateur AutoJs6 principal via `org.autojs.plugin.EXPLORER_ACTION`
* `Fonctionnalité` Exécution via `org.autojs.plugin.EXPLORER_ACTION_EXECUTE` avec accès temporaire en lecture aux URI de contenu du fichier et du dossier parent
* `Fonctionnalité` Nettoyage HTML supprimant les scripts, attributs de gestionnaires d'événements, cadres, objets intégrés et adresses de ressources non sûres avant affichage
* `Fonctionnalité` Chargement contrôlé des ressources relatives, ressources data et images HTTPS autorisées avec style fixe, actualisation et plein écran
* `Fonctionnalité` Politique WebView renforcée avec CSP, navigation URI contrôlée, JavaScript et stockage désactivés et entrée limitée
* `Fonctionnalité` Métadonnées, interface, instructions, README et changelog localisés en espagnol, français, russe, arabe, japonais, coréen, anglais, chinois simplifié, chinois traditionnel de Hong Kong et chinois traditionnel de Taïwan

##### Pour plus d'historique des versions

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Preview/blob/master/app/src/main/assets/doc/CHANGELOG-fr.md)

******

### Compilation

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Compilation Release:

```powershell
.\gradlew.bat :app:assembleRelease
```

Les paramètres de compilation proviennent de `version.properties`. Le SDK minimum actuel est 24 et le SDK cible est 36.

******

### Structure des ressources

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` localise les métadonnées du plugin et l'interface, tandis que `plugin_instruction.md` fournit les instructions affichées par l'hôte. Les fichiers README et changelog sont générés depuis les sources JSON par `.python/generate_markdown.py`.

******

### Liens

******

- Documentation AutoJs6: https://docs.autojs6.com
- HTML Living Standard: https://html.spec.whatwg.org
