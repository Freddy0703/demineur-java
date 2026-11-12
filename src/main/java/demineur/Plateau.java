package demineur;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class Plateau {
    private final Case[][] cases;
    private final int mines;
    private int ouvertes;
    private int drapeaux;
    private long poses;
    private boolean initialise;
    private boolean defaite;

    Plateau(int taille, int mines) {
        this.mines = mines;
        cases = new Case[taille][taille];
        for (Case[] ligne : cases) {
            for (int i = 0; i < ligne.length; i++) ligne[i] = new Case();
        }
    }

    private List<Case> voisines(int ligne, int colonne) {
        List<Case> resultat = new ArrayList<>(8);
        for (int l = Math.max(0, ligne - 1); l <= Math.min(cases.length - 1, ligne + 1); l++) {
            for (int c = Math.max(0, colonne - 1); c <= Math.min(cases.length - 1, colonne + 1); c++) {
                if (l != ligne || c != colonne) resultat.add(cases[l][c]);
            }
        }
        return resultat;
    }

    private void placerMines(Case protegee) {
        List<Case> candidates = new ArrayList<>();
        for (Case[] ligne : cases) {
            for (Case caseJeu : ligne) {
                if (caseJeu != protegee) candidates.add(caseJeu);
            }
        }
        Collections.shuffle(candidates);
        for (int i = 0; i < mines; i++) candidates.get(i).mine = true;
        for (int l = 0; l < cases.length; l++) {
            for (int c = 0; c < cases.length; c++) {
                if (cases[l][c].mine) {
                    for (Case voisine : voisines(l, c)) voisine.minesVoisines++;
                }
            }
        }
        initialise = true;
    }

    String drapeau(int l, int c) {
        Case cible = cases[l][c];
        if (cible.ouverte) return "Cette case est déjà ouverte.";
        if (cible.drapeau) {
            cible.drapeau = false;
            drapeaux--;
            return "Drapeau retiré.";
        }
        if (drapeaux == mines) return "Aucun drapeau disponible.";
        cible.drapeau = true;
        drapeaux++;
        poses++;
        return "Drapeau posé.";
    }

    String ouvrir(int l, int c) {
        Case cible = cases[l][c];
        if (cible.drapeau) return "Retire le drapeau avant d’ouvrir cette case.";
        if (cible.ouverte) return "Cette case est déjà ouverte.";
        if (!initialise) placerMines(cible);
        cible.ouverte = true;
        if (cible.mine) {
            defaite = true;
            return "Game Over ! X = mine ouverte, * = mine, F* = mine marquée.";
        }
        ouvertes++;
        record Position(int ligne, int colonne) {}
        var file = new ArrayDeque<Position>();
        if (cible.minesVoisines == 0) file.add(new Position(l, c));
        while (!file.isEmpty()) {
            Position p = file.remove();
            for (int dl = -1; dl <= 1; dl++) {
                for (int dc = -1; dc <= 1; dc++) {
                    int y = p.ligne() + dl, x = p.colonne() + dc;
                    if (y < 0 || x < 0 || y >= cases.length || x >= cases.length) continue;
                    Case voisine = cases[y][x];
                    if (voisine.ouverte || voisine.drapeau) continue;
                    voisine.ouverte = true;
                    ouvertes++;
                    if (voisine.minesVoisines == 0) file.add(new Position(y, x));
                }
            }
        }
        return gagne() ? "Victoire ! Drapeaux posés au total : " + poses : "Case ouverte.";
    }

    boolean gagne() { return ouvertes == cases.length * cases.length - mines; }
    boolean termine() { return defaite || gagne(); }
    int taille() { return cases.length; }
    int reserve() { return mines - drapeaux; }
    long poses() { return poses; }
    long points() { return gagne() ? 100L * mines - poses : 0; }
    String symbole(int ligne, int colonne) { return cases[ligne][colonne].symbole(defaite); }

    void afficher() {
        StringBuilder texte = new StringBuilder("    ");
        for (int c = 1; c <= cases.length; c++) texte.append("%4d".formatted(c));
        for (int l = 0; l < cases.length; l++) {
            texte.append("%n%4d".formatted(l + 1));
            for (Case caseJeu : cases[l]) texte.append("%4s".formatted(caseJeu.symbole(defaite)));
        }
        System.out.println(texte);
        System.out.println("Drapeaux disponibles : " + (mines - drapeaux));
    }
}
