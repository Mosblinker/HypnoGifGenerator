/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import geom.GeometryMath;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mosblinker
 */
public class RippleSpiralPainter extends LogarithmicSpiralPainter{
    
    public static final Color TRANSPARENT_COLOR = new Color(0x00000000, true);
    /**
     * This is a scratch list to use to get the radiuses for the points on the 
     * spiral where the color changes. This is initially null and is initialized 
     * the first time it's used.
     */
    private List<Double> rList = null;
    
    public RippleSpiralPainter() { }
    
    public RippleSpiralPainter(RippleSpiralPainter painter){
        super(painter);
    }
    @Override
    protected double adjustRotation(double angle, double thickness, 
            boolean clockwise){
            // If the spiral is clockwise
        if (clockwise)
            angle = -angle;
        return GeometryMath.boundDegrees(angle);
    }
    @Override
    protected double unadjustRotation(double angle, double thickness, 
            boolean clockwise){
        return adjustRotation(angle,thickness,clockwise);
    }
    @Override
    protected void paintSpiralGegl(Graphics2D g, double angle, int width,int height, 
            double centerX, double centerY, boolean clockwise, double radius, 
            double thickness) {
        Color color = g.getColor();
            // This gets the amount by which to multiply the angle when 
            // computing the spiral.
        double k = getLogarithmicK();
        
            // Get the azimuth of the point on the spiral where the spiral 
            // radius lies. This ignores whether the spiral is clockwise or not, 
            // treating it as if it was always clockwise. 
        double pR = getAzimuth(radius, k, radius, angle,true);
        
        double m = getRadius(radius,k,pR+HALF_CIRCLE_DEGREES,angle,true) / radius;
        
        double r0 = getStartRadius(g);
        double r1 = getRadius(radius,k,0,angle,!clockwise);
        double r2 = Math.sqrt(width*width+height*height)/2.0;
        
        if (rList == null)
            rList = new ArrayList<>();
        else
            rList.clear();
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
        boolean isThickerEven = false;
        int length = rList.size();
        if (thickness != 0.5){
            isThickerEven = thickness > 0.5 == isColorEven;
            length += Math.floorDiv(length, 2);
            if (isThickerEven)
                length += rList.size() % 2;
        }
        
        float[] fractions = new float[length];
        Color[] colors = new Color[length];
        double t = thickness * 2.0;
        if (t > 1.0)
            t = 2.0 - t;
        t = 1.0 - t;
        int fIndex = 0;
        for (int i = 0; i < rList.size(); i++, fIndex++){
            double r = rList.get(i);
            boolean isEven = i % 2 == 0;
            colors[fIndex] = (isEven == isColorEven) ? color : TRANSPARENT_COLOR;
            if (thickness != 0.5 && isEven == isThickerEven){
                double f = 0;
                if (i > 0)
                    f = (r-rList.get(i-1)) * t;
                fractions[fIndex] = (float)((r-f) / r2);
                fIndex++;
                colors[fIndex] = colors[fIndex-1];
                f = 0;
                if (i < rList.size()-1)
                    f = (rList.get(i+1)-r)*t;
                fractions[fIndex] = (float)((r+f) / r2);
            } else 
                fractions[fIndex] = (float)(r / r2);
        }
        
        g.setPaint(new RadialGradientPaint((float) centerX, (float) centerY, (float) r2, fractions,colors));
        if (rect == null)
            rect = new Rectangle2D.Double();
        rect.setFrame(0, 0, width, height);
        g.fill(rect);
    }
    @Override
    public String getName() {
        return "Ripple";
    }
}
