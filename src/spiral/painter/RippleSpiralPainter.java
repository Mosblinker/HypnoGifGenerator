/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import geom.GeometryMath;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 *
 * @author Mosblinker
 */
public class RippleSpiralPainter extends LogarithmicSpiralPainter{
    
    public static final Color TRANSPARENT_COLOR = new Color(0x00000000, true);
    
    public RippleSpiralPainter() { }
    
    public RippleSpiralPainter(RippleSpiralPainter painter){
        super(painter);
    }
    @Override
    protected void paintSpiralGegl(Graphics2D g, double angle, int width,int height, 
            double centerX, double centerY, boolean clockwise, double radius, 
            double thickness) {
        super.paintSpiralGegl(g, angle, width, height, centerX, centerY, clockwise, radius, thickness);
            // This gets the amount by which to multiply the angle when 
            // computing the spiral.
        double k = getLogarithmicK();
            // This is the value by which to adjust the radius to get the other 
            // spiral that completes the shape
        double lim = Math.exp(k * (1-thickness));
            // This is the value to use to calculate the other spiral that 
            // completes the path. This is used for the outer spiral curve when 
            // clockwise and the inner spiral curve when counter-clockwise 
        double a = radius;
            // When the spiral is going clockwise
        if (clockwise)
            a /= lim;
        else
            a *= lim;
        
            // This gets the starting azimuth for the spiral. This uses the 
            // multiplier for the secondary curve and ignores whether the spiral 
            // is clockwise or not, treating it as if it was always clockwise.
        double p0 = getAzimuth(a,k,getStartRadius(g),angle,true);
        
        System.out.println("Angle: " + angle);
        System.out.println("Angle: " + unadjustRotation(angle,thickness,clockwise));
        System.out.println("a: " + a);
        System.out.println("p0: "+ p0);
    }
    @Override
    public String getName() {
        return "Ripple";
    }
}
