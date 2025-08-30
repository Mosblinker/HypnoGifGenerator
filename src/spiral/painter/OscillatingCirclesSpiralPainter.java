/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import geom.GeometryMath;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
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
     * This is a scratch Point2D object used to calculate the centers of the 
     * circles. This is initially null and is initialized the first time it is 
     * used. 
     */
    private Point2D point1 = null;
    /**
     * This is a scratch Point2D object used to calculate the centers of the 
     * circles. This is initially null and is initialized the first time it is 
     * used. 
     */
    private Point2D point2 = null;
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
            // If the scratch ellipse object is null
        if (ellipse == null)
            ellipse = new Ellipse2D.Double();
            // Set the stroke to use the line width
        g.setStroke(new BasicStroke((float)lineWidth));
            // Get the angle in radians
        double rad = Math.toRadians(angle);
            // Get the smaller of the width and height
        double t = Math.min(width, height);
            // Get the center point of the first set of circles. These will be 
            // oscillating in a sine wave as it orbits around the center
        point1 = GeometryMath.polarToCartesianDegrees(
                getCircleCenterRadius(Math.sin(rad),t), 
                angle, centerX, centerY, point1);
            // Get the center point of the second set of circles. These will be 
            // oscillating in a cosine wave as it orbits around the center on 
            // the other side of the center
        point2 = GeometryMath.polarToCartesianDegrees(-
                getCircleCenterRadius(Math.cos(rad),t), 
                angle, centerX, centerY, point2);
            // Get the maximum radius for the first set of circles
        double r1 = getMaximumRadius(width,height,point1.getX()/width,point1.getY()/height);
            // Get the maximum radius for the second set of circles
        double r2 = getMaximumRadius(width,height,point2.getX()/width,point2.getY()/height);
            // This will be the starting radius
        double startR;
            // If this is going clockwise or there's no foreground color but not 
            // both (the center of the circles will be filled in)
        if (clockwise != noFG){
                // Get half the line width
            double halfWidth = lineWidth / 2.0;
                // Draw the first circle set's center
            ellipse.setFrameFromCenter(point1.getX(), point1.getY(), 
                    point1.getX()-halfWidth, point1.getY()-halfWidth);
            g.fill(ellipse);
                // Draw the second circle set's center
            ellipse.setFrameFromCenter(point2.getX(), point2.getY(), 
                    point2.getX()-halfWidth, point2.getY()-halfWidth);
            g.fill(ellipse);
                // First circle's radius will be equal to the spiral radius
            startR = radius;
        } else
                // First circle's radius will be equal to half the spiral radius
            startR = radius / 2.0;
            // Go through the radiuses to draw the circles
        for (double r = startR; r <= r1 || r <= r2; r+=radius){
                // If the current radius is within the range of the first set of 
                // circles
            if (r <= r1){
                ellipse.setFrameFromCenter(point1.getX(), point1.getY(), 
                        point1.getX()-r, point1.getY()-r);
                g.draw(ellipse);
            }   // If the current radius is within the range of the second set 
                // of circles
            if (r <= r2){
                ellipse.setFrameFromCenter(point2.getX(), point2.getY(), 
                        point2.getX()-r, point2.getY()-r);
                g.draw(ellipse);
            }
        }
    }
    /**
     * 
     * @param r
     * @param maxR
     * @return 
     */
    private double getCircleCenterRadius(double r, double maxR){
        return (0.05 + (0.2 * (1 + r))) * maxR;
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
