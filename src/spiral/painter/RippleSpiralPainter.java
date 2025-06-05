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
import spiral.SpiralGeneratorUtilities;
import spiral.SpiralModel;

/**
 *
 * @author Mosblinker
 */
public class RippleSpiralPainter extends LogarithmicSpiralPainter{
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
        return GeometryMath.boundDegrees(angle);
    }
    @Override
    protected double unadjustRotation(double angle, double thickness, 
            boolean clockwise){
        return adjustRotation(angle,thickness,clockwise);
    }
    @Override
    protected double alterRotationWhenNoColor2(double angle){ 
        return angle;
    }
    @Override
    protected double alterThicknessWhenNoColor2(double thickness){
        return thickness;
    }
    @Override
    protected void fillBackground(Graphics2D g, SpiralModel model, double width, 
            double height){ }
    @Override
    protected void paintSpiralPolar(Graphics2D g, SpiralModel model, 
            double angle, int width,int height, double centerX, double centerY, 
            boolean clockwise, double radius, double thickness) {
            // If the rectangle object has not been initialized yet
        if (rect == null)
            rect = new Rectangle2D.Double();
            // If the scratch list object has not been initialized yet
        if (rList == null)
            rList = new ArrayList<>();
        else
            rList.clear();
            // Get the first color in the model
        Color color1 = model.getColor1();
            // Get the second color in the model
        Color color2 = model.getColor2();
            // If thre is no first color, use a transparent color
        if (SpiralGeneratorUtilities.hasNoColor(color1))
            color1 = SpiralGeneratorUtilities.getTranslucentColor(color2, 0.0);
            // If there is no second color, use a transparent color
        else if (SpiralGeneratorUtilities.hasNoColor(color2))
            color2 = SpiralGeneratorUtilities.getTranslucentColor(color1, 0.0);
            // This gets the amount by which to multiply the angle when 
            // computing the spiral.
        double k = getLogarithmicK();
            // Get the azimuth of the point on the spiral where the spiral 
            // radius lies. This ignores whether the spiral is clockwise or not, 
            // treating it as if it was always clockwise. 
        double pR = getAzimuth(radius, k, radius, angle,true);
            // Get the ratio between the radius of the point half the rotation 
            // from the spiral radius and the spiral radius. This is the amount 
            // by which to multiply each radius to get the next radius to use.
        double m = getRadius(radius,k,pR+HALF_CIRCLE_DEGREES,angle,true)/radius;
            // This get the target starting radius for the spiral
        double r0 = getStartRadius(g);
            // This gets the radius of the polar point at (r1, 0).
        double r1 = getRadius(radius,k,0,angle,!clockwise);
            // This gets the target ending radius for the spiral
        double r2 = getMaximumRadius(width,height,model);
        
            // Go through and calculates the radiuses between the radius for the 
            // polar point (r1, 0) and target starting radius
        for (double r = r1; r > r0; r /= m)
            rList.add(r);
            // Go through and calculates the radiuses between the radius for the 
            // polar point (r1, 0) and target ending radius
        for (double r = r1 * m; r < r2; r *= m)
            rList.add(r);
            // Sort the list of radiuses
        rList.sort(null);
            // If the first radius is smaller than the starting radius
        if (rList.get(0) > r0)
                // Insert an even smaller radius before it
            rList.add(0,rList.get(0)/m);
            // If the last radius is larger than the ending radius
        if (rList.get(rList.size()-1) < r2)
                // Insert an even larger radius after it
            rList.add(rList.get(rList.size()-1)*m);
            // Get the largest radius in the list and use it as the radius for 
            // the gradient
        r2 = rList.get(rList.size()-1);
            // Get whether the index of r1 is even when the spiral is clockwise 
            // and odd when the spiral is counter-clockwise. This is used to 
            // indicate whether the radiuses with even indexes should be the 
            // graphics context's color, whith the others getting the 
            // transparent color.
        boolean isColorEven = ((rList.indexOf(r1) % 2) == 0) == clockwise;
            // This gets whether the thickness should be applied to the radiuses 
            // at the even indexes
        boolean isThickerEven = false;
            // This is the length of the fraction and color arrays
        int length = rList.size();
            // If the thickness is not 0.5 (i.e. one color is thicker than the 
        if (thickness != 0.5){  // other color)
                // Get if the thickness is greater than 0.5 when the color is 
                // is on even indexes, or if the thickness is less than 0.5 and 
                // the color is on odd indexes
            isThickerEven = thickness > 0.5 == isColorEven;
                // Add half the list length to the array length to be 1.5 times 
                // the list length
            length += Math.floorDiv(length, 2);
                // If the even indexes are thicker
            if (isThickerEven)
                length += rList.size() % 2;
        }   // This array will get the fractions for the colors
        float[] fractions = new float[length];
            // This array will get the colors at the points on the gradient at 
            // each fraction
        Color[] colors = new Color[length];
            // This is amount to increase the thickness of the color being made 
            // thicker
        double t = thickness * 2.0;
            // If the fraction multiplier is greater than 1 (thickness is 
            // greater than 0.5)
        if (t > 1.0)
                // Start going in the opposite direction with the fraction 
            t = 2.0 - t;    // multiplier
        t = 1.0 - t;
            // This is the index in the fraction and color arrays
        int fIndex = 0;
            // Go through the radiuses in the list
        for (int i = 0; i < rList.size(); i++, fIndex++){
                // Get the current radius
            double r = rList.get(i);
                // Get if the current index is even
            boolean isEven = i % 2 == 0;
                // Set the color to the graphics color if this index is even and 
                // even indexes are colored, or if this index is odd and odd 
                // indexes are colored. If nether condition is met, then the 
                // color is transparent
            colors[fIndex] = (isEven == isColorEven) ? color2 : color1;
                // If the thickness is not 0.5 and either this index is even and 
                // even indexes are thicker or this index is odd and odd indexes 
                // are thicker
            if (thickness != 0.5 && isEven == isThickerEven){
                    // This is the amount by which to subtract from the radius 
                    // to make the current ring thicker
                double f = 0;
                    // If this is not the first index
                if (i > 0)
                        // Get the difference between the current radius and the 
                        // previous radius, and multiply that difference by the 
                        // thickness multiplier
                    f = (r-rList.get(i-1)) * t;
                    // Get the fraction, adjusted to make the ring thicker with 
                    // a smaller fraction
                fractions[fIndex] = (float)((r-f) / r2);
                fIndex++;
                    // Copy the color to span to fractions
                colors[fIndex] = colors[fIndex-1];
                    // This is the amount by which to add to the radius to make 
                    // the current ring thicker
                f = 0;
                    // If this is not the last index
                if (i < rList.size()-1)
                        // Get the difference between the current radius and the 
                        // next radius, and multiply that difference by the 
                        // thickness multiplier
                    f = (rList.get(i+1)-r)*t;
                    // Get the fraction, adjusted to make the ring thicker with 
                    // a larger fraction
                fractions[fIndex] = (float)((r+f) / r2);
            } else 
                fractions[fIndex] = (float)(r / r2);
        }
            // Create a radial gradient paint to paint the spiral
        g.setPaint(new RadialGradientPaint((float) centerX, (float) centerY, 
                (float) r2, fractions,colors));
            // Fill the area with the radial gradient paint
        rect.setFrame(0, 0, width, height);
        g.fill(rect);
    }
    @Override
    public String getName() {
        return "Ripple";
    }
    @Override
    public RippleSpiralPainter clone() {
        return new RippleSpiralPainter(this);
    }
}
