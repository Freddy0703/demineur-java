package demineur;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0 || !args[0].equals("--console")) {
            javax.swing.SwingUtilities.invokeLater(FenetreJeu::lancer);
            return;
        }
        Scanner entree = new Scanner(System.in);
        System.out.println("Démineur — choisis ton niveau (ou q pour quitter).");
        System.out.println("1 Facile | 2 Moyen | 3 Difficile | 4 Impossible");
        Plateau plateau = null;
        while (plateau == null && entree.hasNextLine()) {
            String choix = entree.nextLine().trim();
            if (choix.equalsIgnoreCase("q")) return;
            plateau = switch (choix) {
                case "1" -> new Plateau(10, 10);
                case "2" -> new Plateau(30, 108);
                case "3" -> new Plateau(60, 540);
                case "4" -> new Plateau(100, 2000);
                default -> null;
            };
            if (plateau == null) System.out.println("Choisis un nombre de 1 à 4.");
        }
        if (plateau == null) return;
        plateau.afficher();
        while (!plateau.termine()) {
            System.out.println("o ligne colonne : ouvrir | f ligne colonne : poser/retirer un drapeau | q : quitter");
            if (!entree.hasNextLine()) return;
            String commande = entree.nextLine().trim();
            if (commande.equalsIgnoreCase("q")) return;
            String[] mots = commande.split("\\s+");
            if (mots.length != 3 || !(mots[0].equals("o") || mots[0].equals("f"))) {
                System.out.println("Exemple : o 2 3 pour ouvrir la ligne 2, colonne 3.");
                continue;
            }
            try {
                int ligne = Integer.parseInt(mots[1]) - 1;
                int colonne = Integer.parseInt(mots[2]) - 1;
                String message = mots[0].equals("o") ? plateau.ouvrir(ligne, colonne) : plateau.drapeau(ligne, colonne);
                plateau.afficher();
                System.out.println(message);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                System.out.println("Indique deux coordonnées comprises dans la grille.");
            }
        }
    }
}
