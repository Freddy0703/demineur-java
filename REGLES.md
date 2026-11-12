# Règles du démineur

Avant la partie, le joueur choisit une difficulté.

| Difficulté | Grille | Cases minées | Mines | Drapeaux disponibles au départ |
|---|---|---:|---:|---:|
| Facile | 10 × 10 | 10 % | 10 | 10 |
| Moyen | 30 × 30 | 12 % | 108 | 108 |
| Difficile | 60 × 60 | 15 % | 540 | 540 |
| Impossible | 100 × 100 | 20 % | 2 000 | 2 000 |

La première case ouverte est toujours sûre. Poser ou retirer un drapeau avant cette première ouverture conserve la protection.

À tous les niveaux, une case sûre affiche le nombre de mines parmi ses voisines immédiates, diagonales comprises. Dans le schéma, seules les cases C comptent pour X.

```text
C C C
C X C
C C C
```

Une case intérieure a huit voisines, une case de bord en a cinq et une case de coin en a trois. Un zéro ouvre automatiquement ses voisines cachées sans drapeau. Chaque nouveau zéro prolonge la chaîne. Les cases numérotées de 1 à 8 se révèlent et arrêtent la propagation à cet endroit.

Le joueur peut poser un drapeau sur une case cachée pour signaler une mine supposée. Chaque pose utilise un drapeau disponible. Quand le compteur atteint zéro, il faut retirer un drapeau pour pouvoir en poser un autre. Le compteur indique la réserve disponible et garde secret le contenu des cases marquées.

Tant que le drapeau est posé, la case reste fermée, même lors d’une ouverture en chaîne. Seul le joueur peut retirer le drapeau. Il récupère alors ce drapeau et la case reste cachée. Elle peut ensuite être ouverte par le joueur ou révélée lors d’une prochaine ouverture en chaîne.

Le joueur gagne dès que toutes les cases sans mine sont révélées. S’il ouvre une mine, la partie s’arrête immédiatement avec le message **Game Over**.

Après un Game Over, toutes les mines deviennent visibles. Le symbole `X` distingue la mine que le joueur a ouverte. Les emplacements où il avait posé des drapeaux restent identifiables pour qu’il puisse vérifier ses choix.

En cas de victoire, moins le joueur a posé de drapeaux pendant la partie, plus il gagne de points au classement. À difficulté égale, une victoire sans poser de drapeau rapporte le maximum de points. Chaque pose compte, même si le joueur retire ensuite le drapeau.
