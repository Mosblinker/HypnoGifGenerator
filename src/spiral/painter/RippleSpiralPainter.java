/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import geom.GeometryMath;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;

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
//        super.paintSpiralGegl(g, angle, width, height, centerX, centerY, !clockwise, radius, thickness);
        Color color = g.getColor();
        Color color1 = getTranslucentColor(color,thickness*2.0);
        Color color2 = getTranslucentColor(color,(thickness*2.0)-1.0);
//        System.out.println(thickness + " " + color1.getAlpha() + " " + color2.getAlpha());
        double angle2 = unadjustRotation(angle,thickness,false);
            // This gets the amount by which to multiply the angle when 
            // computing the spiral.
        double k = getLogarithmicK();
            // This is the value by which to use to get the actual curve of the 
            // spiral
        double lim = Math.exp(k * (1-thickness));
        
            // This gets the starting azimuth for the spiral. This ignores 
            // whether the spiral is clockwise or not, treating it as if it was 
            // always clockwise.
        double p0 = getAzimuth(radius,k,getStartRadius(g),angle2,true);
            // Subtract the starting azimuth by itself mod 180 degrees to ensure 
            // it ends at half a rotation.
//        p0 -= p0 % HALF_CIRCLE_DEGREES;
        
            // This gets the ending azimuth for the spiral. This ignores whether 
            // the spiral is clockwise or not, treating it as if it was always 
            // clockwise. This uses the target radius of half the diagonal 
            // length of the area
        double p1 = getAzimuth(radius, k, Math.sqrt(width*width+height*height)/2.0, 
                angle2,true);
            // TODO: Add ending azimuth adjustment code here if necessary
//            // Effectively round it up to the nearest half of a rotation
//        p1 += (HALF_CIRCLE_DEGREES - (p1 % HALF_CIRCLE_DEGREES)) % HALF_CIRCLE_DEGREES;
        
            // Get the azimuth of the point on the spiral where the spiral 
            // radius lies. This ignores whether the spiral is clockwise or not, 
            // treating it as if it was always clockwise. 
        double pR = getAzimuth(radius, k, radius, angle2,true);
        
        double m = getRadius(radius,k,pR+HALF_CIRCLE_DEGREES,angle2,true) / radius;
        
        double r0 = getStartRadius(g);
        double r1 = getRadius(radius,k,0,angle2,!clockwise);
        double r2 = Math.sqrt(width*width+height*height)/2.0;
        
        System.out.println("Angle: " + angle);
        System.out.println("Angle: " + angle2);
        System.out.println("lim: " + lim);
        System.out.println("a: " + radius);
        System.out.println("p0: "+ p0);
        System.out.println("p1: " + p1);
        System.out.println("m: " + m);
        System.out.println("pR: " + pR);
        System.out.println("r0: " + r0);
        System.out.println("r1: " + r1);
        System.out.println("r2: " + r2);
        System.out.println("r1/r0: " + (r1 / r0));
        System.out.println("(r/r0)/m: " + (r1 / r0) / m);
        System.out.println("(r/r0)%m: " + (r1 / r0) % m);
        System.out.println("r2/r1: " + (r2 / r1));
        System.out.println("(r2/r1)/m: " + (r2 / r1) / m);
        System.out.println("(r2/r1)%m: " + (r2 / r1) % m);
        
        ArrayList<Double> rList = new ArrayList<>();
        for (double r = r1; r > r0; r /= m)
            rList.add(r);
        for (double r = r1 * m; r < r2; r *= m)
            rList.add(r);
        rList.sort(null);
        if (rList.get(0) > r0)
            rList.add(0,rList.get(0)/m);
        if (rList.get(rList.size()-1) < r2)
            rList.add(rList.get(rList.size()-1)*m);
        r2 = rList.get(rList.size()-1);
        
        int index = rList.indexOf(r1);
        boolean isColorEven = ((index % 2) == 0) == clockwise;
        
        System.out.println("List: " + rList);
        System.out.println("r2: " + r2);
        System.out.println("Index: " + index);
        System.out.println("Is Color Even: " + isColorEven);
        
        float[] fractions = new float[rList.size()];
        Color[] colors = new Color[rList.size()];
        for (int i = 0; i < rList.size(); i++){
            fractions[i] = (float)(rList.get(i) / r2);
            colors[i] = ((i % 2 == 0) == isColorEven) ? color1 : color2;
        }
        
        System.out.println("Fractions: " + Arrays.toString(fractions));
        System.out.println();
        
        g.setPaint(new RadialGradientPaint((float) centerX, (float) centerY, (float) r2, fractions,colors));
        if (rect == null)
            rect = new Rectangle2D.Double();
        rect.setFrame(0, 0, width, height);
        g.fill(rect);
        
        g.setColor(Color.RED);
        g.draw(new Line2D.Double(new Point2D.Double(centerX,centerY), GeometryMath.polarToCartesian(getRadius(radius,k,0,angle2,!clockwise), 0, centerX, centerY, null)));
        g.setColor(Color.CYAN);
        Ellipse2D e = new Ellipse2D.Double();
        
        rect.setFrameFromCenter(centerX, centerY, centerX+r2, centerY+r2);
        g.draw(rect);
        for (int i = 0; i < rList.size(); i++){
            double r = rList.get(i);
            if ((i % 2 == 0) == isColorEven)
                g.setColor(Color.BLUE);
            else
                g.setColor(Color.GREEN);
            e.setFrameFromCenter(centerX, centerY, centerX+r, centerY+r);
            g.draw(e);
        }
    }
    @Override
    public String getName() {
        return "Ripple";
    }
}
