package demineur;

import java.awt.Dimension;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

final class Scores {
    private static final Path FICHIER = Path.of(System.getProperty("user.home"), "Demineur", "scores.csv");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    static void enregistrer(String niveau, Plateau plateau) throws IOException {
        Files.createDirectories(FICHIER.getParent());
        String resultat = plateau.gagne() ? "Victoire" : "Défaite";
        String ligne = String.join(";", LocalDateTime.now().format(DATE), niveau, resultat,
                Long.toString(plateau.poses()), Long.toString(plateau.points()));
        Files.writeString(FICHIER, ligne + System.lineSeparator(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    static List<String[]> lire() throws IOException {
        List<String[]> lignes = new ArrayList<>();
        try {
            for (String ligne : Files.readAllLines(FICHIER)) {
                String[] champs = ligne.split(";", -1);
                if (champs.length != 5) throw new IOException("Ligne de score incomplète.");
                Long.parseLong(champs[4]);
                lignes.add(champs);
            }
        } catch (NoSuchFileException e) {
            return lignes;
        } catch (NumberFormatException e) {
            throw new IOException("Nombre de points illisible.", e);
        }
        lignes.sort((a, b) -> Long.compare(Long.parseLong(b[4]), Long.parseLong(a[4])));
        return lignes;
    }

    static void afficher(JFrame menu) {
        try {
            var lignes = lire();
            if (lignes.isEmpty()) {
                JOptionPane.showMessageDialog(menu, "Aucune partie terminée pour le moment.", "Scoreboard", JOptionPane.PLAIN_MESSAGE);
                return;
            }
            JTable tableau = new JTable(new DefaultTableModel(lignes.toArray(String[][]::new),
                    new String[]{"Date", "Niveau", "Résultat", "Drapeaux posés", "Points"}) {
                public boolean isCellEditable(int ligne, int colonne) { return false; }
            });
            tableau.getColumnModel().getColumn(0).setPreferredWidth(155);
            tableau.setPreferredScrollableViewportSize(new Dimension(580, Math.min(280, tableau.getRowHeight() * lignes.size())));
            JOptionPane.showMessageDialog(menu, new JScrollPane(tableau), "Scoreboard", JOptionPane.PLAIN_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(menu, "Impossible de lire les scores : " + e.getMessage());
        }
    }
}
