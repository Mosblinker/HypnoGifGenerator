/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import java.awt.Component;
import javax.swing.*;

/**
 * This is a list cell renderer that renders the names of {@code SpiralShape} 
 * values.
 * @author Mosblinker
 * @see SpiralShape
 */
public class SpiralShapeListCellRenderer extends DefaultListCellRenderer{
    @Override
    public Component getListCellRendererComponent(JList list, Object value, 
            int index, boolean isSelected, boolean cellHasFocus) {
            // If the value is a SpiralShape value
        if (value instanceof SpiralShape){
                // Get the name of the value
            String name = value.toString();
                // Make the name lowercase and replace all underscores with 
                // spaces
            name = name.toLowerCase().replaceAll("_", " ");
                // Capitalize the first letter
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
                // Use the name as the value
            value = name;
        }
        return super.getListCellRendererComponent(list, value, index, 
                isSelected, cellHasFocus);
    }
}
