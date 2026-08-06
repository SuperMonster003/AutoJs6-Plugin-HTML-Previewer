Utilisez HTML Preview depuis l'explorateur AutoJs6 principal:

1. Installez et activez le plugin `HTML Preview`.
2. Ouvrez le menu secondaire d'un fichier HTML pris en charge.
3. Sélectionnez `Prévisualiser HTML`.

Le plugin reçoit un accès temporaire en lecture au fichier sélectionné et à son dossier parent par des URI de contenu. Il ne reçoit aucun chemin brut du système de fichiers.

Extensions prises en charge: `html`, `htm`, `shtm`, `shtml`, `xht`, `xhtml`.

La visionneuse nettoie le contenu avant affichage. Elle supprime les scripts, les attributs de gestionnaires d'événements, les cadres, les objets intégrés et les adresses de ressources non sûres. JavaScript, le stockage WebView, les cookies et l'accès direct aux fichiers restent désactivés.

La version 1 prend uniquement en charge les actions de lecture seule sur un fichier dans l'explorateur AutoJs6 principal.
