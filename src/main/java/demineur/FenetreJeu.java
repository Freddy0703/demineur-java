package demineur;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

final class FenetreJeu {
    static void lancer() {
        JFrame menu = new JFrame("Démineur");
        menu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel actions = new JPanel(new java.awt.GridLayout(0, 1, 0, 10));
        actions.setBorder(BorderFactory.createEmptyBorder(20, 36, 24, 36));
        actions.add(new JLabel("Démineur", SwingConstants.CENTER));
        JButton jouer = new JButton("Jouer");
        JButton scores = new JButton("Scoreboard");
        JButton quitter = new JButton("Quitter");
        actions.add(jouer);
        actions.add(scores);
        actions.add(quitter);
        jouer.addActionListener(e -> choisirNiveau(menu));
        scores.addActionListener(e -> Scores.afficher(menu));
        quitter.addActionListener(e -> menu.dispose());
        menu.setContentPane(actions);
        menu.getRootPane().setDefaultButton(jouer);
        menu.pack();
        menu.setLocationRelativeTo(null);
        menu.setVisible(true);
    }

    private static void choisirNiveau(JFrame menu) {
        String[] niveaux = {"Facile", "Moyen", "Difficile", "Impossible"};
        int choix = JOptionPane.showOptionDialog(menu, "Choisis une difficulté", "Démineur",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, niveaux, niveaux[0]);
        if (choix < 0) return;
        menu.dispose();
        int[] tailles = {10, 30, 60, 100};
        int[] mines = {10, 108, 540, 2000};
        Plateau plateau = new Plateau(tailles[choix], mines[choix]);
        JFrame fenetre = new JFrame("Démineur · " + niveaux[choix]);
        fenetre.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JLabel statut = new JLabel("Drapeaux disponibles : " + plateau.reserve());
        JTable grille = creerGrille(plateau, statut, niveaux[choix]);
        fenetre.add(new JLabel("Flèches : déplacer   ·   Entrée ou clic gauche : ouvrir   ·   F ou clic droit : drapeau"), BorderLayout.NORTH);
        fenetre.add(new JScrollPane(grille));
        JButton recommencer = new JButton("Recommencer");
        recommencer.addActionListener(e -> {
            fenetre.dispose();
            SwingUtilities.invokeLater(FenetreJeu::lancer);
        });
        JPanel pied = new JPanel(new BorderLayout(8, 0));
        pied.add(statut, BorderLayout.CENTER);
        pied.add(recommencer, BorderLayout.EAST);
        fenetre.add(pied, BorderLayout.SOUTH);
        fenetre.pack();
        fenetre.setLocationRelativeTo(null);
        fenetre.setVisible(true);
        grille.requestFocusInWindow();
    }

    static JTable creerGrille(Plateau plateau, JLabel statut, String niveau) {
        JTable grille = new JTable(new AbstractTableModel() {
            public int getRowCount() { return plateau.taille(); }
            public int getColumnCount() { return plateau.taille(); }
            public Object getValueAt(int l, int c) { return plateau.symbole(l, c); }
        });
        grille.setTableHeader(null);
        grille.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        grille.setRowHeight(30);
        for (var colonne : java.util.Collections.list(grille.getColumnModel().getColumns())) {
            colonne.setPreferredWidth(32);
            colonne.setWidth(32);
        }
        var rendu = new DefaultTableCellRenderer() {
            @Override protected void setValue(Object valeur) {
                IconeCase icone = switch (valeur.toString()) {
                    case "F" -> IconeCase.DRAPEAU;
                    case "*" -> IconeCase.MINE;
                    case "F*" -> IconeCase.MARQUEE;
                    case "X" -> IconeCase.EXPLOSEE;
                    default -> null;
                };
                setIcon(icone);
                setText(icone == null ? valeur.toString() : "");
                setToolTipText(icone == null ? null : icone.description);
                getAccessibleContext().setAccessibleName(icone == null ? valeur.toString() : icone.description);
            }
        };
        rendu.setHorizontalAlignment(SwingConstants.CENTER);
        grille.setDefaultRenderer(Object.class, rendu);
        grille.setCellSelectionEnabled(true);
        grille.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        grille.changeSelection(0, 0, false, false);
        grille.setPreferredScrollableViewportSize(new Dimension(Math.min(800, plateau.taille()*32),
                Math.min(540, plateau.taille()*30)));
        for (String touche : new String[]{"ENTER", "F"}) {
            grille.getInputMap().put(KeyStroke.getKeyStroke(touche), touche);
            grille.getActionMap().put(touche, new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    jouer(plateau, grille, statut, touche.equals("F"), niveau);
                }
            });
        }
        grille.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                int l = grille.rowAtPoint(e.getPoint()), c = grille.columnAtPoint(e.getPoint());
                if (l < 0 || c < 0) return;
                boolean drapeau = SwingUtilities.isRightMouseButton(e);
                if (!drapeau && !SwingUtilities.isLeftMouseButton(e)) return;
                grille.changeSelection(l, c, false, false);
                jouer(plateau, grille, statut, drapeau, niveau);
            }
        });
        return grille;
    }

    private static void jouer(Plateau plateau, JTable grille, JLabel statut, boolean drapeau, String niveau) {
        if (plateau.termine()) return;
        int l = grille.getSelectedRow(), c = grille.getSelectedColumn();
        if (l < 0 || c < 0) return;
        String message = drapeau ? plateau.drapeau(l, c) : plateau.ouvrir(l, c);
        if (plateau.termine() && !plateau.gagne()) message = "Game Over ! La mine déclenchée est entourée d’une explosion orange.";
        if (plateau.termine()) {
            message += " Score : " + plateau.points();
            try {
                Scores.enregistrer(niveau, plateau);
            } catch (java.io.IOException e) {
                JOptionPane.showMessageDialog(grille, "Impossible d’enregistrer le score : " + e.getMessage());
            }
        }
        statut.setText(message + "   ·   Drapeaux disponibles : " + plateau.reserve());
        grille.repaint();
    }
}
