package demineur;

import java.awt.event.ActionEvent;
import java.nio.file.Files;
import java.util.Arrays;
import javax.swing.*;

/** À lancer avec Java 17 ou plus. Les scores de test sont isolés dans un dossier temporaire. */
public final class VerificationJeu {
    public static void main(String[] args) throws Exception {
        System.setProperty("user.home", Files.createTempDirectory("demineur-test-").toString());
        for (int[] niveau : new int[][] {{10,10}, {30,108}, {60,540}, {100,2000}}) {
            verifierPartie(niveau[0], niveau[1]);
        }
        // Toutes les cases sauf la première sont minées : les indices sont connus exactement.
        for (int[] exemple : new int[][] {{0,0,3}, {0,2,5}, {2,2,8}}) {
            Plateau plateau = new Plateau(5,24);
            plateau.ouvrir(exemple[0], exemple[1]);
            verifier(plateau.symbole(exemple[0], exemple[1]).equals("" + exemple[2]), "indice du voisinage");
        }
        Plateau vide = new Plateau(100,1);
        vide.drapeau(50,50);
        vide.drapeau(51,51);
        verifier(vide.reserve() == 0 && vide.poses() == 1, "limite de drapeaux");
        // Sans mine, un zéro doit atteindre toute la grille sauf la case marquée.
        Plateau chaine = new Plateau(100,0);
        chaine.ouvrir(0,0);
        verifier(chaine.gagne() && chaine.symbole(99,99).equals("0"), "grande chaîne");
        SwingUtilities.invokeAndWait(VerificationJeu::verifierCommandes);
        var scores = Scores.lire();
        verifier(scores.size() == 1 && Arrays.equals(Arrays.copyOfRange(scores.get(0), 1, 5),
                new String[]{"Test", "Victoire", "1", "799"}), "score sauvegardé et relu");
        System.out.println("Vérifications réussies.");
    }

    private static void verifierPartie(int taille, int mines) {
        Plateau plateau = new Plateau(taille, mines);
        // Garder une case protégée puis vérifier son état après la première ouverture.
        plateau.drapeau(0,0);
        plateau.ouvrir(0,0);
        plateau.ouvrir(taille - 1, taille - 1);
        verifier(plateau.symbole(0,0).equals("F"), "protection du drapeau");
        verifier(plateau.symbole(taille - 1, taille - 1).matches("[0-8]"), "premier coup sûr");
        plateau.drapeau(0,0);
        verifier(plateau.reserve() == mines && plateau.symbole(0,0).equals("."), "retrait du drapeau");
        // La défaite révèle les mines. Leur emplacement fournit une vérification indépendante des indices.
        for (int l = 0; l < taille && !plateau.termine(); l++)
            for (int c = 0; c < taille && !plateau.termine(); c++) plateau.ouvrir(l,c);
        if (plateau.gagne()) {
            verifier(plateau.points() == 100L * mines - 1, "points en cas de victoire");
            return;
        }
        int total = 0, declenchees = 0;
        for (int l = 0; l < taille; l++) {
            for (int c = 0; c < taille; c++) {
                String symbole = plateau.symbole(l,c);
                if (symbole.equals("X")) declenchees++;
                if (symbole.equals("*") || symbole.equals("X")) total++;
                if (!symbole.matches("[0-8]")) continue;
                int attendu = 0;
                for (int dl = -1; dl <= 1; dl++) {
                    for (int dc = -1; dc <= 1; dc++) {
                        int y = l + dl, x = c + dc;
                        if (y < 0 || x < 0 || y >= taille || x >= taille) continue;
                        String voisin = plateau.symbole(y,x);
                        if (voisin.equals("*") || voisin.equals("X")) attendu++;
                    }
                }
                verifier(Integer.parseInt(symbole) == attendu, "indice en difficulté de taille " + taille);
            }
        }
        verifier(total == mines && declenchees == 1 && plateau.points() == 0, "défaite et mines révélées");
    }

    private static void verifierCommandes() {
        Plateau plateau = new Plateau(3,8);
        JTable grille = FenetreJeu.creerGrille(plateau, new JLabel(), "Test");
        for (String touche : new String[]{"RIGHT","DOWN","F","ENTER"}) appuyer(grille,touche);
        verifier(plateau.symbole(1,1).equals("F"), "sélection et blocage au clavier");
        for (String touche : new String[]{"F","ENTER","ENTER"}) appuyer(grille,touche);
        verifier(plateau.gagne() && plateau.points() == 799, "victoire et coût du drapeau retiré");
    }

    private static void appuyer(JTable grille, String touche) {
        KeyStroke frappe = KeyStroke.getKeyStroke(touche);
        Object action = grille.getInputMap().get(frappe);
        if (action == null) action = grille.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).get(frappe);
        grille.getActionMap().get(action).actionPerformed(new ActionEvent(grille, 0, touche));
    }

    private static void verifier(boolean succes, String nom) {
        if (!succes) throw new AssertionError(nom);
    }
}
