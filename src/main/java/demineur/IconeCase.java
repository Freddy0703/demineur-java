package demineur;

import java.awt.*;
import javax.swing.Icon;

enum IconeCase implements Icon {
    DRAPEAU("Drapeau"), MINE("Mine"), MARQUEE("Mine marquée"), EXPLOSEE("Mine déclenchée");

    final String description;
    IconeCase(String description) { this.description = description; }
    public int getIconWidth() { return 24; }
    public int getIconHeight() { return 24; }

    public void paintIcon(Component composant, Graphics graphics, int x, int y) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.translate(x, y);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (this != DRAPEAU) {
                if (this == EXPLOSEE) {
                    g.setColor(new Color(0xffb144));
                    g.fillPolygon(new int[]{0,6,4,11,15,16,23,20,24,17,16,10,4,5},
                            new int[]{8,8,0,5,0,6,3,11,16,17,24,19,24,15},14);
                }
                g.setColor(new Color(0x263342));
                g.setStroke(new BasicStroke(2.3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                for (int i = 0; i < 4; i++) {
                    g.drawLine(3,12,21,12);
                    g.rotate(Math.PI / 4,12,12);
                }
                g.rotate(-Math.PI,12,12);
                g.fillOval(5,5,14,14);
                g.setColor(Color.WHITE);
                g.fillOval(8,8,3,3);
            }
            if (this == DRAPEAU || this == MARQUEE) {
                if (this == MARQUEE) g.scale(.68,.68);
                g.setColor(new Color(0x263342));
                g.fillRoundRect(5,2,3,19,2,2);
                g.fillRoundRect(2,20,12,3,2,2);
                g.setColor(new Color(0xc93235));
                g.fillPolygon(new int[]{8,22,8},new int[]{2,7,12},3);
            }
        } finally {
            g.dispose();
        }
    }
}
