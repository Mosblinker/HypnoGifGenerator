/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import geom.GeometryMath;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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
        double angle2 = unadjustRotation(angle,thickness,false);
            // This gets the amount by which to multiply the angle when 
            // computing the spiral.
        double k = getLogarithmicK();
            // This is the value by which to use to get the actual curve of the 
            // spiral
        double lim = Math.exp(k * (1-thickness));
        
        double m = getBase();
        
            // This gets the starting azimuth for the spiral. This ignores 
            // whether the spiral is clockwise or not, treating it as if it was 
            // always clockwise.
        double p0 = getAzimuth(radius,k,getStartRadius(g),angle2,true);
        
            // This gets the ending azimuth for the spiral. This ignores whether 
            // the spiral is clockwise or not, treating it as if it was always 
            // clockwise. This uses the target radius of half the diagonal 
            // length of the area
        double p1 = getAzimuth(radius, k, Math.sqrt(width*width+height*height)/2.0, 
                angle2,true);
            // TODO: Add ending azimuth adjustment code here if necessary
//            // Effectively round it up to the nearest full circle
//        p1 += (FULL_CIRCLE_DEGREES - (p1 % FULL_CIRCLE_DEGREES)) % FULL_CIRCLE_DEGREES;
        
            // Get the azimuth of the point on the spiral where the spiral 
            // radius lies. This ignores whether the spiral is clockwise or not, 
            // treating it as if it was always clockwise. 
        double pR = getAzimuth(radius, k, radius, angle2,true);
        
            // If the spiral is going counter-clockwise
        if (!clockwise){
            p1 = -p1;
                // Not only swap the signs but offset this by 360
            p0 = -p0+FULL_CIRCLE_DEGREES;
        } else
            m = 1/m;
        
        System.out.println("Angle: " + angle);
        System.out.println("Angle: " + angle2);
        System.out.println("lim: " + lim);
        System.out.println("a: " + radius);
        System.out.println("p0: "+ p0);
        System.out.println("p1: " + p1);
        System.out.println("m: " + m);
        System.out.println("pR: " + pR);
        System.out.println(getRadius(radius,k,0,angle2,clockwise));
        System.out.println(getRadius(radius,k,pR,angle2,clockwise));
        System.out.println();
        
        
        g.setColor(Color.GREEN);
        g.draw(new Line2D.Double(new Point2D.Double(centerX,centerY), GeometryMath.polarToCartesian(radius, angle, centerX, centerY, null)));
        g.draw(new Line2D.Double(new Point2D.Double(centerX,centerY), GeometryMath.polarToCartesian(radius, getAzimuth(radius, k, radius, angle,clockwise), centerX, centerY, null)));
        g.setColor(Color.RED);
        g.draw(new Line2D.Double(new Point2D.Double(centerX,centerY), GeometryMath.polarToCartesian(getRadius(radius,k,0,angle2,clockwise), 0, centerX, centerY, null)));
        g.setColor(Color.CYAN);
        Ellipse2D e = new Ellipse2D.Double();
        if (rect == null)
            rect = new Rectangle2D.Double();
        double rT = getRadius(radius,k,p1,angle2,clockwise);
        rect.setFrameFromCenter(centerX, centerY, centerX+rT, centerY+rT);
        e.setFrame(rect);
        g.draw(rect);
        g.draw(e);
        rT = getRadius(radius,k,0,angle2,clockwise);
        e.setFrameFromCenter(centerX, centerY, centerX+rT, centerY+rT);
        g.draw(e);
    }
    @Override
    public String getName() {
        return "Ripple";
    }
}
