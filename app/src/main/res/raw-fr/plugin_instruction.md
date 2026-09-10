Utilisez HTML Previewer depuis le gestionnaire de fichiers:

1. Installez et activez le plugin `HTML Previewer`.
2. Ouvrez le menu secondaire d'un fichier HTML ou MHTML pris en charge.
3. Sélectionnez `Prévisualiser HTML`.

Le plugin reçoit un accès temporaire en lecture au fichier sélectionné et à son dossier parent par des URI de contenu. Il ne reçoit aucun chemin brut du système de fichiers.

Extensions prises en charge: `html`, `htm`, `shtm`, `shtml`, `xht`, `xhtml`, `mht`, `mhtml`.

Pour MHTML, la visionneuse sélectionne la racine HTML de l'archive et associe les styles, images, polices, contenus audio et vidéo intégrés autorisés à une origine virtuelle isolée. Les emplacements et ID de contenu ne sont que des étiquettes internes; les étiquettes ambiguës sont ignorées et rien n'est extrait vers le stockage.

Les scripts sont désactivés par défaut. Pour un fichier HTML ordinaire complet et fiable, activez le mode interactif dans les paramètres pour JavaScript, les boutons, WebGL et le stockage local. MHTML, HTML tronqué, code source et export PDF gardent les scripts désactivés. Cookies, accès direct aux fichiers/content, ponts JavaScript natifs et nouvelles fenêtres restent désactivés. Le stockage local est réservé au mode interactif, pour les préférences et scores. Le code source et le PDF utilisent un contenu assaini à nouveau.

La visionneuse propose aussi la recherche dans la page, une taille de texte réglable et le choix du thème. Elle reconnaît le BOM, le jeu de caractères MIME et les déclarations d'encodage HTML placées au début. Un HTML ordinaire de plus de 8 MB demande confirmation avant de n'afficher que les premiers 8 MB avec la coupure indiquée; MHTML doit rester complet et est refusé au-delà de 8 MB.

Choisissez `Afficher le code source` dans le menu pour consulter le texte original en lecture seule avec une police à chasse fixe, puis `Afficher l’aperçu` pour revenir. Les balises restent du texte littéral dans ce mode et toutes les requêtes sortantes sont bloquées.

Les ressources locales proviennent du dossier autorisé ou de l’archive MHTML sélectionnée. Le mode sûr autorise les images HTTPS publiques; le mode interactif autorise aussi scripts, styles et requêtes HTTPS publics lorsque les ressources réseau sont activées. Désactiver le réseau bloque les requêtes WebView sortantes. Les scripts interactifs peuvent envoyer des données à des services distants; réservez ce mode aux pages fiables.

Choisissez `Exporter en PDF` pour ouvrir le panneau d'impression Android avec une copie relue, nettoyée et en thème clair. Cela reste vrai depuis l'affichage du code source et ne modifie pas l'état actuel de la visionneuse. Sélectionnez `Enregistrer au format PDF` et la destination dans l'interface système; le plugin ne demande aucune autorisation de stockage.

Explorer Action v2 prend en charge le bouton principal et le menu contextuel pour un fichier, avec une autorisation temporaire de lecture du document et de son dossier parent. AutoJs6 build 5269 ou ultérieur est requis.

Les menus et dialogues suivent la langue et le mode sombre AutoJs6, tout comme GitHub (Auto) et le thème HTML automatique. Les barres utilisent une couleur représentative des bords de la page avec du texte et des icônes noirs ou blancs contrastés. Pour les dégradés, images et animations, cette couleur reste fixe jusqu’au rechargement pour éviter le scintillement.
