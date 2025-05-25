/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import geom.GeometryMath;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

/**
 *
 * @author Mosblinker
 */
public class ConcentricSpiralPainter extends SpiralPainter{
    /**
     * This is a scratch Ellipse2D object used to draw the circles. This is 
     * initially null and is initialized the first time it is used. 
     */
    private Ellipse2D ellipse = null;
    
    public ConcentricSpiralPainter() { }
    
    public ConcentricSpiralPainter(ConcentricSpiralPainter painter){
        super(painter);
    }
    @Override
    protected void paintSpiral(Graphics2D g, double angle, int width,int height, 
            double centerX, double centerY, boolean clockwise, double radius, 
            double thickness) {
            // If the thickness is greater than zero
        if (thickness > 0.0){
                // If the thickness is greater than or equal to 1
            if (thickness >= 1.0)
                fillWithTransparency(g,width,height,thickness);
            else{   // If the scratch ellipse object is null
                if (ellipse == null)
                    ellipse = new Ellipse2D.Double();
                    // Bound the angle
                angle = GeometryMath.boundDegrees(angle);
                    // Get the amount by which to increase the radiuses of the 
                    // circles by
                double m = radius / 2.0;
                    // Get the line width for the circles
                double lineWidth = thickness * m;
                    // Get half the line width
                double halfWidth = lineWidth / 2.0;
                    // Set the stroke to use the line width
                g.setStroke(new BasicStroke((float)lineWidth));
                    // Get the maximum radius for any of the circles
                double r1 = Math.sqrt(width*width+height*height)/2.0+lineWidth;
                    // Get the radius for the first circle
                double startR = m - (m * (angle / FULL_CIRCLE_DEGREES));
                    // If the spiral is going counter-clockwise
                if (!clockwise)
                        // Shift the spiral by half
                    startR = (startR + (m/2.0)) % m;
                    // If the starting radius is for a circle that would be less 
                    // than the line width in size
                if (startR <= halfWidth || startR > m - halfWidth){
                        // If the starting radius is for a circle that is just 
                        // starting to form
                    if (startR > halfWidth)
                        startR -= m;
                        // Get the radius for the first circle
                    double r = startR+halfWidth;
                        // Set the frame for the circle with the radius
                    ellipse.setFrameFromCenter(centerX, centerY, centerX+r, centerY+r);
                        // Fill this circle
                    g.fill(ellipse);
                        // Move on to the next circle
                    startR += m;
                }   // A for loop to draw the circles that make up this spiral
                for (double r = startR; r <= r1; r+= m){
                        // Set the frame for the circle with the current radius
                    ellipse.setFrameFromCenter(centerX, centerY, centerX+r, centerY+r);
                        // Draw the circle
                    g.draw(ellipse);
                }
            }
        }
    }
    @Override
    public String getName() {
        return "Concentric Circles";
    }
}
