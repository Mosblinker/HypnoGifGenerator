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
public class RadialSpiralPainter extends SpiralPainter{
    /**
     * This is a scratch Ellipse2D object used to draw the circles. This is 
     * initially null and is initialized the first time it is used. 
     */
    private Ellipse2D ellipse = null;
    
    public RadialSpiralPainter() { }
    
    public RadialSpiralPainter(RadialSpiralPainter painter){
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
            else{
                if (ellipse == null)
                    ellipse = new Ellipse2D.Double();
                angle = adjustRotation(angle,thickness,clockwise);
                
                double m = radius / 2.0;
                
                double lineWidth = thickness * m;
                
                g.setStroke(new BasicStroke((float)lineWidth));
                
                double r1 = Math.sqrt(width*width+height*height)/2.0 + lineWidth;
                
                for (double r = m * (angle / FULL_CIRCLE_DEGREES); r <= r1; r+= m){
                    ellipse.setFrameFromCenter(centerX, centerY, centerX+r, centerY+r);
                    g.draw(ellipse);
                }
            }
        }
    }
    @Override
    public String getName() {
        return "Radial";
    }
}
