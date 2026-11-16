# Démineur en Java

Un démineur avec une interface graphique Swing, quatre niveaux de difficulté et un classement enregistré sur le PC. Le jeu se contrôle à la souris ou au clavier.

## Ma collaboration avec Codex

J’ai réalisé ce projet avec Codex, l’assistant de programmation d’OpenAI. Notre travail s’est réparti entre la conception du jeu et son implémentation.

J’ai défini l’architecture du projet, les classes et les méthodes à utiliser, ainsi que le fonctionnement attendu du jeu. J’ai notamment précisé le comportement des cases, l’ouverture en chaîne, la protection des drapeaux, les conditions de victoire et la place du score. J’ai aussi demandé l’interface graphique, le menu et le bouton pour recommencer.

J’ai confié à Codex la rédaction du code Java, la réalisation de l’interface Swing et la mise en œuvre technique de ces choix. Codex a implémenté les classes et les méthodes à partir de mes indications et écrit les vérifications automatiques. Nous avons ensuite ajusté le projet au fil de nos échanges et de mes retours.

Ce dépôt présente le résultat de cette collaboration et permet de consulter le code, de lancer le jeu et d’exécuter ses vérifications.

## Jouer

Le menu principal propose **Jouer**, **Scoreboard** et **Quitter**. Le bouton **Recommencer**, présent pendant une partie, ramène au menu principal.

| Action | Souris | Clavier |
|---|---|---|
| Choisir une case | Pointer la case | Flèches |
| Ouvrir une case | Clic gauche | Entrée |
| Poser ou retirer un drapeau | Clic droit | F |

| Difficulté | Grille | Mines | Drapeaux disponibles au départ |
|---|---|---:|---:|
| Facile | 10 × 10 | 10 | 10 |
| Moyen | 30 × 30 | 108 | 108 |
| Difficile | 60 × 60 | 540 | 540 |
| Impossible | 100 × 100 | 2 000 | 2 000 |

La première case ouverte est toujours sûre. Le moteur place les mines après ce premier choix et exclut la case sélectionnée.

Chaque chiffre indique le nombre de mines dans les cases voisines immédiates, diagonales comprises. Un zéro ouvre les cases voisines en chaîne, jusqu’aux cases numérotées. Une case marquée d’un drapeau reste fermée jusqu’à son retrait. Elle reprend ensuite le comportement d’une case normale.

Le joueur gagne en révélant toutes les cases sans mine. Ouvrir une mine termine la partie et révèle les mines de la grille. Une explosion orange distingue la mine déclenchée dans l’interface graphique.

Les règles détaillées sont dans [REGLES.md](REGLES.md).

## Score et sauvegarde

En cas de victoire, le score vaut `100 × nombre de mines − nombre total de drapeaux posés`. Chaque pose compte, même si le drapeau est ensuite retiré. Une victoire en Facile avec trois poses rapporte donc 997 points. Une défaite rapporte zéro point.

Les parties terminées dans l’interface graphique sont sauvegardées dans le fichier `Demineur/scores.csv` du dossier personnel de l’utilisateur. Le classement contient la date, la difficulté, le résultat, le nombre de poses et les points. Il mélange actuellement les difficultés et trie les résultats par points décroissants.

## Lancer le projet

Un **JDK 17 ou plus récent** est nécessaire. Swing fait partie du JDK.

### Avec IntelliJ IDEA

1. Ouvrir le dossier du projet et charger le fichier `pom.xml` si IntelliJ le propose.
2. Sélectionner un JDK 17 ou plus récent dans les paramètres du projet.
3. Ouvrir `src/main/java/demineur/Main.java` et exécuter la méthode `main`.

### Avec PowerShell sous Windows

Depuis la racine du projet, avec `java` et `javac` disponibles dans le terminal, exécuter les commandes suivantes.

```powershell
New-Item -ItemType Directory -Force target/classes | Out-Null
javac --release 17 -encoding UTF-8 -d target/classes (Get-ChildItem src/main/java/demineur/*.java).FullName
java -cp target/classes demineur.Main
```

Un mode console est également accessible.

```powershell
java -cp target/classes demineur.Main --console
```

Dans ce mode, `o 2 3` ouvre la case en ligne 2, colonne 3, `f 2 3` pose ou retire son drapeau et `q` quitte la partie. La sauvegarde des résultats est actuellement intégrée au mode graphique.

## Organisation du code

| Classe | Responsabilité |
|---|---|
| `Main` | Démarrer le mode graphique ou le mode console |
| `FenetreJeu` | Afficher le menu et la grille, recevoir les commandes du joueur |
| `Plateau` | Placer les mines, compter les voisines, ouvrir les cases, déterminer le résultat et le score |
| `Case` | Conserver l’état d’une case et son symbole |
| `IconeCase` | Dessiner les drapeaux, les mines et l’explosion |
| `Scores` | Lire et enregistrer les résultats, afficher le classement |
| `VerificationJeu` | Exécuter les vérifications automatiques du jeu |

## Exécuter les vérifications

Après avoir compilé les classes du jeu, lancer les commandes suivantes dans PowerShell.

```powershell
New-Item -ItemType Directory -Force target/test-classes | Out-Null
javac --release 17 -encoding UTF-8 -cp target/classes -d target/test-classes src/test/java/demineur/VerificationJeu.java
java "-Djava.awt.headless=true" -cp "target/classes;target/test-classes" demineur.VerificationJeu
```

Le programme vérifie notamment les indices, le premier coup sûr, les drapeaux, l’ouverture en chaîne, les commandes au clavier et l’enregistrement d’un score. Il utilise un dossier temporaire pour ses scores. La réussite se termine par le message `Vérifications réussies.`

Cette suite se lance par sa méthode `main`. Son exécution automatique avec `mvn test` reste à configurer. Une partie des essais utilise des grilles aléatoires, et les interactions dans une fenêtre réelle nécessitent aussi un essai manuel.

## Points à améliorer

- Permettre de réessayer une sauvegarde de score après une erreur d’écriture.
- Conserver l’accès aux lignes valides du classement si une ligne du CSV est abîmée.
- Renforcer les contrôles du moteur sur les configurations invalides et les actions après la fin d’une partie.
- Regrouper la définition des difficultés, actuellement présente dans les deux modes de jeu.
