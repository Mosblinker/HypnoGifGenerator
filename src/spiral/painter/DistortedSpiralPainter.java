/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import geom.GeometryMath;
import static geom.GeometryMathConstants.HALF_CIRCLE_DEGREES;
import java.awt.Graphics2D;
import java.awt.geom.*;
import spiral.SpiralGeneratorUtilities;
import spiral.SpiralModel;

/**
 *
 * @author Mosblinker
 */
public class DistortedSpiralPainter extends SpiralPainter {
    /**
     * 
     */
    private Path2D path = null;
    /**
     * 
     */
    public DistortedSpiralPainter(){
        
    }
    /**
     * 
     * @param painter 
     */
    public DistortedSpiralPainter(DistortedSpiralPainter painter){
        super(painter);
    }
    
    
    @Override
    protected void paintSpiral(Graphics2D g, SpiralModel model, double angle, 
            int width, int height, double centerX, double centerY, 
            boolean clockwise, double radius, double thickness) {
            // If there is no foreground color
        if (SpiralGeneratorUtilities.hasNoColor(model.getColor2())){
                // Invert the thickness
            thickness = 1.0-thickness;
                // Shift it by 180 degrees
            angle += HALF_CIRCLE_DEGREES;
                // Set the color to use to the background color
            g.setColor(model.getColor1());
        } else {
                // If there is a background color
            if (!SpiralGeneratorUtilities.hasNoColor(model.getColor1())){
                    // Set the color to use to the background color
                g.setColor(model.getColor1());
                    // Fill the area
                fillArea(g,width,height);
            }   // Set the color to use to the foreground color
            g.setColor(model.getColor2());
        }   // Bound the angle
        angle = GeometryMath.boundDegrees(angle);
        
        if (path == null)
            path = new Path2D.Double();
        else
            path.reset();
        
        
    }
    @Override
    public String getName() {
        return "Distorted";
    }
    @Override
    public SpiralPainter clone() {
        return new DistortedSpiralPainter(this);
    }
}
