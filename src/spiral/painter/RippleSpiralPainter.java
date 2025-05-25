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
        clockwise = !clockwise;
        super.paintSpiralGegl(g, angle, width, height, centerX, centerY, clockwise, radius, thickness);
            // This gets the amount by which to multiply the angle when 
            // computing the spiral.
        double k = getLogarithmicK();
            // This is the value by which to use to get the actual curve of the 
            // spiral
        double lim = Math.exp(k * (1-thickness));
            // This is the radius of the actual curve of the spiral
        double a = radius;
        
            // This gets the starting azimuth for the spiral. This uses the 
            // multiplier for the secondary curve and ignores whether the spiral 
            // is clockwise or not, treating it as if it was always clockwise.
        double p0 = getAzimuth(a,k,getStartRadius(g),angle,true);
            // If the spiral is going clockwise
//        if (!clockwise)
//            a /= lim;
//        else
//            a *= lim;
//            // Get the point in between the inner and outer spirals
//        a = (radius + a) / 2.0;
        
        System.out.println("Angle: " + angle);
        System.out.println("Angle: " + unadjustRotation(angle,thickness,clockwise));
        System.out.println("a: " + a);
        
            // This gets the ending azimuth for the spiral. This uses the 
            // radius and ignores whether the spiral is clockwise or not, 
            // treating it as if it was always clockwise. This uses the target 
            // radius of half the diagonal length of the area
        double p1 = getAzimuth(radius, k, 
                Math.sqrt(width*width+height*height)/2.0, angle,true);
            // Effectively round it up to the nearest full circle
        p1 += (FULL_CIRCLE_DEGREES - (p1 % FULL_CIRCLE_DEGREES)) % FULL_CIRCLE_DEGREES;
        
            // Get the azimuth of the point on the spiral where the spiral 
            // radius lies. This ignores whether the spiral is clockwise or not, 
            // treating it as if it was always clockwise. 
        double pR = getAzimuth(radius, k, radius, angle,true);
            // Get the radius of the point on the spiral that is one full 
            // revolution away from the spiral radius. This ignores whether the 
            // spiral is clockwise or not, treating it as if it was always 
            // clockwise. 
        double m0 = getRadius(radius,k,pR+FULL_CIRCLE_DEGREES,angle,true);
        m0 /= radius;
        
            // If the spiral is going counter-clockwise
        if (!clockwise){
            p1 = -p1;
                // Not only swap the signs but offset this by 360
            p0 = -p0+FULL_CIRCLE_DEGREES;
                // Invert the values m0, m1, and m2
            m0 = 1/m0;
        }
        
        System.out.println("p0: "+ p0);
        System.out.println("p1: " + p1);
        System.out.println("pR: " + pR);
        System.out.println("m0: " + m0);
        
    }
    @Override
    public String getName() {
        return "Ripple";
    }
}
