package demineur;

final class Case {
    boolean mine;
    boolean ouverte;
    boolean drapeau;
    int minesVoisines;

    String symbole(boolean defaite) {
        if (defaite && mine) {
            if (ouverte) return "X";
            return drapeau ? "F*" : "*";
        }
        if (drapeau) return "F";
        return ouverte ? Integer.toString(minesVoisines) : ".";
    }
}
