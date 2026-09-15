******

### Historique des versions

******

# v1.1.1

###### 2026/09/15

* `Amélioration` compileSdk passe à 37 (Android 17) ; targetSdk reste à 36 jusqu'à la vérification du comportement dépendant de la cible

# v1.1.0

###### 2026/09/13

* `Fonctionnalité` Historique local accessible depuis l'interface, avec traductions et repli en anglais
* `Correctif` L’aperçu statique et le client HTTPS contrôlé valident les adresses DNS publiques, les fixent pour la connexion et revérifient chaque redirection. Le réglage des ressources contrôle ce client, limité à GET et HEAD.
* `Correctif` Activez uniquement pour le document actuel de confiance. WebRTC et certaines API peuvent communiquer hors du réglage des ressources. Chaque nouvel aperçu désactive les scripts.
* `Correctif` Conserver HTTP 206 et les autres codes de succès lors du chargement des ressources distantes
* `Amélioration` Vérification de la signature complète, des APK attendus et de la reproductibilité de la documentation
* `Dépendance` Ajout de OkHttp 4.12.0 pour le chargement contrôlé des ressources HTTPS

# v1.0.1

###### 2026/09/11

* `Fonctionnalité` Les scripts sont désactivés par défaut. Pour un fichier HTML ordinaire complet et fiable, activez le mode interactif dans les paramètres pour JavaScript, les boutons, WebGL et le stockage local. MHTML, HTML tronqué, code source et export PDF gardent les scripts désactivés.
* `Correctif` Échec possible de l'activation du plugin dans le centre de plugins
* `Correctif` Correction du rejet du protocole du bouton principal et des plantages des paramètres; apparence AutoJs6, couleurs des barres et contrôles monochromes synchronisés
* `Amélioration` Nom et description du plugin allégés, documentation utilisateur plus lisible
* `Amélioration` La vérification de compilation rejette les dépendances natives involontaires et produit un rapport JSON

# v1.0.0

###### 2026/08/06

* `Fonctionnalité` Première version: une action de menu `Prévisualiser HTML` pour les fichiers HTML dans le gestionnaire de fichiers d'AutoJs6 (ID de plugin `html-previewer`)
* `Fonctionnalité` Visionneuse sécurisée: scripts, cadres, objets intégrés et envois de formulaires supprimés avant l'affichage, entièrement en lecture seule et sans jamais exécuter JavaScript
* `Fonctionnalité` Chargement des ressources: prend en charge les styles de la page, les ressources locales voisines du fichier et les images web HTTPS selon des règles contrôlées, toutes les autres requêtes étant bloquées
* `Fonctionnalité` Confort de lecture: thème clair/sombre automatique, zoom par pincement, actualisation, mode plein écran et réglage `Démarrer en mode plein écran`
* `Fonctionnalité` Limites de sécurité: seule l'autorisation temporaire de lecture accordée par l'hôte est acceptée, chaque fichier est plafonné à 8 MB, et les noms de fichiers comme les chemins de ressources sont strictement validés
* `Fonctionnalité` Multilingue: interface, instructions, README et changelog en 10 langues
