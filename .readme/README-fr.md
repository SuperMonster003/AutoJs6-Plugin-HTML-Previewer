<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="html-previewer-ic-launcher" border="0" width="128" />
  </p>

  <p>Plugin de gestionnaire de fichiers. Aperçu sécurisé en lecture seule des fichiers HTML et MHTML</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-HTML-Previewer?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Langues (Languages)

******

Le fichier README.md actuel prend en charge les langues suivantes:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-en.md)
- Français [fr] # actuel
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/.readme/README-ar.md)

******

### Introduction

******

Aperçu en un geste: ouvrez les fichiers web directement depuis le gestionnaire de fichiers via le menu `Prévisualiser HTML`, sans navigateur et sans réseau pour les pages purement locales.

Les scripts sont désactivés par défaut. Pour un fichier HTML ordinaire complet et fiable, activez le mode interactif dans les paramètres pour JavaScript, les boutons, WebGL et le stockage local. MHTML, HTML tronqué, code source et export PDF gardent les scripts désactivés.

******

### Points forts

******

- Aperçu en un geste: ouvrez les fichiers web directement depuis le gestionnaire de fichiers via le menu `Prévisualiser HTML`, sans navigateur et sans réseau pour les pages purement locales.
- Les scripts sont désactivés par défaut. Pour un fichier HTML ordinaire complet et fiable, activez le mode interactif dans les paramètres pour JavaScript, les boutons, WebGL et le stockage local. MHTML, HTML tronqué, code source et export PDF gardent les scripts désactivés.
- Affichage sûr du code source: alternez entre l'aperçu nettoyé et le HTML original en lecture seule, ou le HTML racine décodé pour MHTML; les balises restent du texte littéral et ce mode n'émet aucune requête réseau.
- Export PDF système: choisissez `Exporter en PDF` pour envoyer une copie de nouveau nettoyée et en thème clair au panneau d'impression Android; le système gère les réglages de page et `Enregistrer au format PDF` sans autorisation de stockage.
- Mise en page fidèle: prend en charge les styles et ressources autorisées incluses dans MHTML, les ressources locales voisines du HTML ordinaire (images, polices, audio, vidéo) ainsi que les images web HTTPS facultatives, qui peuvent être entièrement désactivées.
- Lecture confortable: recherche dans la page, texte de 75%-200%, zoom par pincement et thèmes AutoJs6, clair ou sombre. Les menus et dialogues suivent la langue et le mode sombre AutoJs6, tout comme GitHub (Auto) et le thème HTML automatique. Les barres utilisent une couleur représentative des bords de la page avec du texte et des icônes noirs ou blancs contrastés. Pour les dégradés, images et animations, cette couleur reste fixe jusqu’au rechargement pour éviter le scintillement.
- Mode plein écran: passez à tout moment en plein écran immersif, ou activez `Démarrer en mode plein écran` dans les paramètres.
- Liens externes: un appui sur un lien http/https est confié au navigateur du système, tandis que les ancres internes continuent de fonctionner.
- Multilingue: interface, instructions, README et changelog disponibles en 10 langues.

******

### Mode d'emploi

******

1. Téléchargez le dernier APK du plugin depuis la page [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/releases) et installez-le sur votre appareil.
2. Ouvrez le centre de plugins d'AutoJs6 et activez le plugin `Aperçu HTML`.
3. Dans le gestionnaire de fichiers d'AutoJs6, repérez le fichier HTML ou MHTML à consulter et ouvrez son menu contextuel (actions supplémentaires).
4. Choisissez `Prévisualiser HTML`; la page s'ouvre dans une visionneuse dédiée.
5. Pendant la lecture, utilisez le menu en haut à droite pour rechercher du texte, alterner entre `Afficher le code source` et `Afficher l'aperçu`, `Exporter en PDF`, `Actualiser`, basculer le `Mode plein écran` ou ouvrir `Paramètres` afin de régler la taille, le thème, les images réseau et le démarrage en plein écran; Retour ferme la recherche, quitte le plein écran ou ferme la visionneuse.

> Si le plugin n'apparaît pas dans le centre de plugins, mettez d'abord AutoJs6 à jour vers une version récente (build interne 5269 ou ultérieur). Explorer Action v2 prend en charge le bouton principal et le menu contextuel pour un fichier, avec une autorisation temporaire de lecture du document et de son dossier parent. AutoJs6 build 5269 ou ultérieur est requis.

******

### Formats pris en charge

******

Le plugin reconnaît les extensions suivantes, ainsi que les fichiers sans extension explicitement marqués `text/html`, `application/xhtml+xml`, `multipart/related` ou `application/x-mimearchive` par l'hôte:

```text
html, htm, shtm, shtml, xht, xhtml, mht, mhtml
```

Les fichiers jusqu’à 8 MB sont chargés entièrement. Un HTML ordinaire plus grand peut afficher, après confirmation, ses premiers 8 MB avec une coupure signalée; MHTML doit rester complet et est refusé au-delà de la limite. Le BOM est prioritaire au décodage HTML; viennent ensuite le jeu de caractères MIME ou une déclaration `meta charset` ou `http-equiv`, puis UTF-8 par défaut. MHTML accepte un emboîtement MIME borné et les encodages de transfert identité, quoted-printable et Base64.

******

### Questions fréquentes

******

#### Pourquoi les boutons et les effets dynamiques de la page ne répondent-ils pas?

Les scripts sont désactivés par défaut. Pour un fichier HTML ordinaire complet et fiable, activez le mode interactif dans les paramètres pour JavaScript, les boutons, WebGL et le stockage local. MHTML, HTML tronqué, code source et export PDF gardent les scripts désactivés.

#### Pourquoi certaines images ou certains styles sont-ils absents?

Les ressources locales proviennent du dossier autorisé ou de l’archive MHTML sélectionnée. Le mode sûr autorise les images HTTPS publiques; le mode interactif autorise aussi scripts, styles et requêtes HTTPS publics lorsque les ressources réseau sont activées. Désactiver le réseau bloque les requêtes WebView sortantes. Les scripts interactifs peuvent envoyer des données à des services distants; réservez ce mode aux pages fiables.

#### L'aperçu ne ressemble pas exactement au rendu du navigateur. Est-ce normal?

En mode sûr par défaut: Oui. Outre la suppression des scripts, la visionneuse injecte un style de lecture de base et désactive certaines fonctionnalités avancées. L'objectif est une lecture sûre et lisible, pas un rendu au pixel près. Vérifiez l'apparence finale dans un navigateur.

#### Ce plugin envoie-t-il mes fichiers quelque part?

Les ressources locales proviennent du dossier autorisé ou de l’archive MHTML sélectionnée. Le mode sûr autorise les images HTTPS publiques; le mode interactif autorise aussi scripts, styles et requêtes HTTPS publics lorsque les ressources réseau sont activées. Désactiver le réseau bloque les requêtes WebView sortantes. Les scripts interactifs peuvent envoyer des données à des services distants; réservez ce mode aux pages fiables.

#### Où un PDF exporté est-il enregistré?

`Exporter en PDF` ouvre le panneau d'impression du système Android. Choisissez `Enregistrer au format PDF`, puis le nom et la destination dans le sélecteur de fichiers système. Le plugin n'écrit jamais directement dans le stockage et n'en demande pas l'autorisation. L'export relit toujours le HTML ou l'archive MHTML complète et nettoie la page racine, même depuis l'affichage du code source, sans modifier l'état actuel de la visionneuse.

#### Puis-je modifier des fichiers HTML ou prévisualiser un site web complet?

La version actuelle se limite à l'aperçu en lecture seule d'un seul fichier: pas d'édition ni de parcours de dossiers. Les ressources voisines du fichier sont lues à la demande, et le plugin ne détient jamais que l'autorisation de lecture temporaire accordée par l'hôte. Consultez [ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/ROADMAP.md) pour les évolutions prévues.

******

### Autorisations et sécurité

******

Le plugin part du principe que le fichier prévisualisé n'est pas fiable et applique plusieurs couches de protection:

- En mode sûr par défaut: Nettoyage avant affichage: scripts, attributs de gestionnaires d'événements, cadres intégrés, objets intégrés et cibles de formulaires sont supprimés, et les adresses de liens ou de ressources non sûres sont filtrées.
- Cookies, accès direct aux fichiers/content, ponts JavaScript natifs et nouvelles fenêtres restent désactivés. Le stockage local est réservé au mode interactif, pour les préférences et scores. Le code source et le PDF utilisent un contenu assaini à nouveau.
- Les ressources locales proviennent du dossier autorisé ou de l’archive MHTML sélectionnée. Le mode sûr autorise les images HTTPS publiques; le mode interactif autorise aussi scripts, styles et requêtes HTTPS publics lorsque les ressources réseau sont activées. Désactiver le réseau bloque les requêtes WebView sortantes. Les scripts interactifs peuvent envoyer des données à des services distants; réservez ce mode aux pages fiables.
- Moindre privilège: le plugin ne reçoit que l'autorisation temporaire de lecture par URI de contenu accordée par l'hôte, ne voit jamais les chemins du système de fichiers et transmet la sortie PDF au service d'impression Android au lieu d'écrire lui-même dans le stockage.
- Entrée bornée: la lecture est plafonnée à 8 MB; MHTML limite aussi les parties, l'imbrication, les en-têtes et les corps décodés; les noms de fichiers et chemins de ressources sont strictement validés pour empêcher tout accès hors périmètre.
- Cookies, accès direct aux fichiers/content, ponts JavaScript natifs et nouvelles fenêtres restent désactivés. Le stockage local est réservé au mode interactif, pour les préférences et scores. Le code source et le PDF utilisent un contenu assaini à nouveau.

En mode sûr par défaut: Le manifeste source ne demande que l'autorisation réseau (utilisée lorsque les images HTTPS sont activées) et l'autorisation de plugin AutoJs6. AndroidX ajoute aussi une autorisation de signature limitée au paquet pour protéger les récepteurs dynamiques non exportés; elle ne donne accès à aucune donnée de l'appareil. Lorsque les images réseau sont désactivées, WebView n'émet aucune requête sortante. L'accès à la destination PDF appartient aux interfaces d'impression et de sélection de fichiers Android; aucune autorisation de stockage, de médias, d'appareil photo, de localisation ou autre autorisation sensible n'est donc demandée.

******

### Interface du plugin

******

Les informations suivantes s'adressent aux développeurs; l'hôte découvre et exécute le plugin avec ces identités:

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

Explorer Action v2 prend en charge le bouton principal et le menu contextuel pour un fichier, avec une autorisation temporaire de lecture du document et de son dossier parent. AutoJs6 build 5269 ou ultérieur est requis.

- [Voir la matrice de compatibilité Explorer Action](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/docs/explorer-action-compatibility.md)

******

### Feuille de route

******

Les capacités prévues et leur avancement sont suivis sous forme de liste cochable dans ROADMAP.md, organisée par jalons avec des critères d'acceptation, couvrant la recherche dans la page, le réglage de la taille du texte, la prise en charge d'encodages supplémentaires, un interrupteur pour les images web, et plus encore. Les cases non cochées décrivent des projets, pas des capacités déjà livrées. Vos retours via Issues sont les bienvenus.

- [Voir ROADMAP.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/ROADMAP.md)

******

### Historique des versions

******

#### v1.0.1

_2026/08/08_

- `Fonctionnalité` Les scripts sont désactivés par défaut. Pour un fichier HTML ordinaire complet et fiable, activez le mode interactif dans les paramètres pour JavaScript, les boutons, WebGL et le stockage local. MHTML, HTML tronqué, code source et export PDF gardent les scripts désactivés.
- `Correctif` Échec possible de l'activation du plugin dans le centre de plugins
- `Correctif` Correction du rejet du protocole du bouton principal et des plantages des paramètres; apparence AutoJs6, couleurs des barres et contrôles monochromes synchronisés
- `Amélioration` Nom et description du plugin allégés, documentation utilisateur plus lisible

#### v1.0.0

_2026/08/06_

- `Fonctionnalité` Première version: une action de menu `Prévisualiser HTML` pour les fichiers HTML dans le gestionnaire de fichiers d'AutoJs6 (ID de plugin `html-previewer`)
- `Fonctionnalité` Visionneuse sécurisée: scripts, cadres, objets intégrés et envois de formulaires supprimés avant l'affichage, entièrement en lecture seule et sans jamais exécuter JavaScript
- `Fonctionnalité` Chargement des ressources: prend en charge les styles de la page, les ressources locales voisines du fichier et les images web HTTPS selon des règles contrôlées, toutes les autres requêtes étant bloquées
- `Fonctionnalité` Confort de lecture: thème clair/sombre automatique, zoom par pincement, actualisation, mode plein écran et réglage `Démarrer en mode plein écran`
- `Fonctionnalité` Limites de sécurité: seule l'autorisation temporaire de lecture accordée par l'hôte est acceptée, chaque fichier est plafonné à 8 MB, et les noms de fichiers comme les chemins de ressources sont strictement validés
- `Fonctionnalité` Multilingue: interface, instructions, README et changelog en 10 langues

##### Pour plus d'historique des versions

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer/blob/master/app/src/main/assets/doc/CHANGELOG-fr.md)

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

### Localisation et génération des documents

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

`strings.xml` localise les métadonnées du plugin et l'interface de la visionneuse, tandis que `plugin_instruction.md` fournit les instructions affichées par l'hôte. Pour le README et le changelog, modifiez toujours les sources JSON sous `.readme/` et `.changelog/`, puis exécutez `py .python/generate_markdown.py` pour tout régénérer; les fichiers générés ne sont jamais modifiés à la main. Exécutez `py .python/generate_markdown.py --check` pour vérifier que sources et fichiers générés sont synchronisés.

******

### Liens

******

- Documentation AutoJs6: https://docs.autojs6.com
- HTML Living Standard: https://html.spec.whatwg.org
