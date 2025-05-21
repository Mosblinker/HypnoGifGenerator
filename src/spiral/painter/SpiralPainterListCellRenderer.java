/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import java.awt.Component;
import javax.swing.*;

/**
 * This is a list cell renderer that renders the names of 
 * {@code SpiralPainter}s.
 * @author Mosblinker
 * @see SpiralPainter
 * @see SpiralPainter#getName()
 */
public class SpiralPainterListCellRenderer extends DefaultListCellRenderer{
    @Override
    public Component getListCellRendererComponent(JList list, Object value, 
            int index, boolean isSelected, boolean cellHasFocus) {
            // If the value is a SpiralPainter
        if (value instanceof SpiralPainter){
                // Get the name of the spiral
            String name = ((SpiralPainter)value).getName();
                // If there is a name for the spiral
            if (name != null)
                value = value.getClass().getSimpleName();
        }
        return super.getListCellRendererComponent(list, value, index, 
                isSelected, cellHasFocus);
    }
}
