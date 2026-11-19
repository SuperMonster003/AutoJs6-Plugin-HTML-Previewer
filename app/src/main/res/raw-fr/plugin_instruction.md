HTML Previewer affiche un aperçu des fichiers HTML et MHTML dans AutoJs6, sans ouvrir de navigateur. Les pages s'ouvrent en mode sécurisé, sans exécuter de scripts; vous pouvez aussi afficher le code source, rechercher dans la page ou l'exporter en PDF. L'aperçu se contente de lire le fichier et ne modifie jamais l'original.

### Prise en main

1. Vérifiez que ce plugin est activé dans le "Centre des plugins" d'AutoJs6.
2. Ouvrez le gestionnaire de fichiers d'AutoJs6 et repérez le fichier HTML ou MHTML.
3. Choisissez "Prévisualiser HTML" dans le menu du fichier (dans AutoJs6 6.8.1 et versions ultérieures, il se trouve dans le menu de premier niveau "HTML Previewer").
4. Le menu en haut à droite de l'aperçu donne accès à la recherche, au code source, à l'export PDF, aux "Paramètres", etc.

### Fonctionnalités principales

- **Rechercher dans la page**: surligne les correspondances du texte saisi et permet de passer de l'une à l'autre.
- **Afficher le code source**: affiche le texte d'origine en lecture seule et en police à chasse fixe; choisissez "Afficher l'aperçu" pour revenir à la page.
- **Exporter en PDF**: ouvre l'écran d'impression du système, où vous choisissez d'enregistrer en PDF et l'emplacement; le plugin n'a besoin d'aucune autorisation de stockage.
- **Mode interactif**: activez-le dans les "Paramètres" pour exécuter les scripts, afin que les boutons, les animations et le stockage local fonctionnent; il se désactive à chaque nouvel aperçu, activez-le uniquement pour des pages de confiance.
- **Ressources réseau**: activez "Charger les images réseau" pour afficher les images HTTPS; en mode interactif, cette option devient "Charger les ressources réseau" et autorise aussi les scripts et styles HTTPS.
- **Taille du texte et thème**: réglez la "Taille du texte" dans les "Paramètres" et choisissez le thème "Suivre AutoJs6", "Clair" ou "Sombre".
- **Mode plein écran**: masque la barre d'outils pour lire sans distraction; activez "Démarrer en mode plein écran" pour ouvrir chaque aperçu en plein écran.
- **Archives MHTML**: affiche la page d'une archive web avec ses styles, images et polices intégrés, sans rien extraire vers le stockage.

### Prérequis

- Android 7.0 ou version ultérieure.
- AutoJs6 6.8.0 ou version ultérieure.
- Aucune autorisation de stockage n'est nécessaire; le plugin lit uniquement le fichier sélectionné et son dossier, et n'utilise le réseau que pour charger des ressources HTTPS.

### Questions fréquentes

**Pourquoi les boutons et les animations ne réagissent pas**

Par défaut, les pages n'exécutent aucun script. Pour un fichier HTML complet de confiance, activez le "Mode interactif" dans les "Paramètres". Les fichiers MHTML, les gros fichiers dont seul le début est affiché et la vue du code source n'exécutent jamais de scripts.

**Pourquoi certaines images ou certains styles ne s'affichent pas**

Les images et styles locaux doivent se trouver dans le dossier du fichier HTML ou dans l'un de ses sous-dossiers. Les images réseau nécessitent l'option "Charger les images réseau", et seules les adresses HTTPS sont prises en charge.

**Pourquoi seul le début d'un gros fichier est affiché**

Pour un fichier HTML de plus de 8 MB, le plugin demande d'abord confirmation, puis n'affiche que les premiers 8 MB en indiquant l'endroit de la coupure. Les archives MHTML doivent être affichées en entier: au-delà de 8 MB, elles ne peuvent pas être prévisualisées.

**Pourquoi "Prévisualiser HTML" n'apparaît pas dans le menu du fichier**

Le plugin ne traite que les fichiers portant les extensions html, htm, shtm, shtml, xht, xhtml, mht, mhtml. Vérifiez l'extension du fichier et assurez-vous que le plugin est activé dans le "Centre des plugins".

Plus de détails sur la [page du projet](https://github.com/SuperMonster003/AutoJs6-Plugin-HTML-Previewer).
