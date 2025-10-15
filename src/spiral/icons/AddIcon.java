/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.icons;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

/**
 *
 * @author Mosblinker
 */
public class AddIcon implements Icon{
    
    private static final Color PLUS_COLOR = new Color(0, 128, 0);

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        g = g.create();
        g.translate(x, y);
        g.setColor(PLUS_COLOR);
        g.fillRect(0, 6, getIconWidth(), 4);
        g.fillRect(6, 0, 4, getIconHeight());
        g.dispose();
    }

    @Override
    public int getIconWidth() {
        return 16;
    }

    @Override
    public int getIconHeight() {
        return 16;
    }
    
}
