/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import geom.GeometryMath;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import spiral.SpiralGeneratorUtilities;
import spiral.SpiralModel;

/**
 *
 * @author Mosblinker
 */
public class OscillatingCirclesSpiralPainter extends SpiralPainter{
    /**
     * This is a scratch Ellipse2D object used to draw the circles. This is 
     * initially null and is initialized the first time it is used. 
     */
    private Ellipse2D ellipse = null;
    /**
     * 
     */
    public OscillatingCirclesSpiralPainter(){
        super();
    }
    /**
     * 
     * @param painter 
     */
    public OscillatingCirclesSpiralPainter(OscillatingCirclesSpiralPainter painter){
        super(painter);
    }
    @Override
    protected double getDefaultRadius(){
        return 50.0;
    }
    @Override
    protected void paintSpiral(Graphics2D g, SpiralModel model, double angle, 
            int width, int height, double centerX, double centerY, 
            boolean clockwise, double radius, double thickness) {
        boolean noFG = SpiralGeneratorUtilities.hasNoColor(model.getColor2());
            // If there is no foreground color
        if (noFG){
                // Invert the thickness
            thickness = 1.0-thickness;
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
            // Get the line width for the shapes
        double lineWidth = thickness * radius;
            // Get half the line width
        double halfWidth = lineWidth / 2.0;
        
            // If the scratch ellipse object is null
        if (ellipse == null)
            ellipse = new Ellipse2D.Double();
            // Set the stroke to use the line width
        g.setStroke(new BasicStroke((float)lineWidth));
        
        double x1 = 0.20*width;
        double y1 = 0.20*height;
        double x2 = 0.80*width;
        double y2 = 0.80*height;
        
        double startR;
        
        if (clockwise != noFG){
            ellipse.setFrameFromCenter(x1, y1, x1-halfWidth, y1-halfWidth);
            g.fill(ellipse);
            ellipse.setFrameFromCenter(x2, y2, x2-halfWidth, y2-halfWidth);
            g.fill(ellipse);
            startR = radius;
        } else
            startR = radius / 2.0;
        
        
        
        for (double r = startR; r < radius*5; r+=radius){
            ellipse.setFrameFromCenter(x1, y1, x1-r, y1-r);
            GeometryMath.printShape("Circle 1: ", ellipse);
            g.draw(ellipse);
            ellipse.setFrameFromCenter(x2, y2, x2-r, y2-r);
            GeometryMath.printShape("Circle 2: ", ellipse);
            g.draw(ellipse);
        }
    }
    @Override
    public String getName() {
        return "Oscillating Circles";
    }
    @Override
    public OscillatingCirclesSpiralPainter clone() {
        return new OscillatingCirclesSpiralPainter();
    }
}
