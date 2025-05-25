/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import geom.GeometryMath;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author Mosblinker
 */
public class RadialFadeSpiralPainter extends SpiralPainter implements LogarithmicSpiral{
    
    public static final Color TRANSPARENT_COLOR = new Color(0x00000000, true);
    /**
     * This is the base for the spiral.
     */
    private double base = 2.0;
    
    public RadialFadeSpiralPainter() { }
    
    public RadialFadeSpiralPainter(RadialFadeSpiralPainter painter){
        super(painter);
        this.base = painter.base;
    }
    /**
     * 
     * @param base 
     */
    @Override
    public void setBase(double base){
            // If the base is less than 1
        if (base < 1)
            throw new IllegalArgumentException();
            // If the base would change
        if (this.base != base){
                // Get the old value for the base
            double old = this.base;
            this.base = base;
            firePropertyChange(BASE_PROPERTY_CHANGED,old,base);
        }
    }
    /**
     * 
     * @return 
     */
    @Override
    public double getBase(){
        return base;
    }
    /**
     * This returns the k value for a logarithmic spiral using the equation 
     * {@code r=a*e^(k*p)}, with {@code r} being the radius, {@code p} being the 
     * azimuth, and {@code a} being some constant. This is equal to the tangent 
     * of the pitch of the spiral.
     * @return The value multiplied with the azimuth before it is used as the 
     * exponent for Eular's number.
     * @see #getBase() 
     */
    protected double getLogarithmicK(){
        return Math.log(getBase());
    }
    @Override
    protected void paintSpiral(Graphics2D g, double angle, int width,int height, 
            double centerX, double centerY, boolean clockwise, double radius, 
            double thickness) {
            // If the thickness is greater than zero
        if (thickness > 0.0){
                // If the thickness is greater than or equal to 1 or the base is 
                // equal to 1
            if (thickness >= 1.0 || getBase() == 1.0)
                fillWithTransparency(g,width,height,thickness);
            else{
                Color color = g.getColor();
                    // Bound the angle
                angle = (FULL_CIRCLE_DEGREES - GeometryMath.boundDegrees(angle)) / FULL_CIRCLE_DEGREES;
                
                double k = getLogarithmicK();
                    // Get the maximum radius for the spiral
                double r1 = Math.sqrt(width*width+height*height)/2.0;
                
                double m1 = radius / r1;
                System.out.print(m1 + " ");
                while (m1 < 1.0)
                    m1 /= k;
                r1 *= m1;
                System.out.println(m1);
                double f1 = radius / r1;
                double temp = ((f1/k/k)-f1) * angle;
                System.out.println(f1 + " " + temp + " " + (f1 + temp));
                f1 += temp;
                
                ArrayList<Double> fractionList = new ArrayList<>();
                double f0 = LogarithmicSpiralPainter.STARTING_RADIUS / r1;
                fractionList.add(0.0);
                
                for (double f = f1; f >= f0; f *= k)
                    fractionList.add(f);
                if (!fractionList.contains(f0))
                    fractionList.add(f0);
                for (double f = f1/k; f <= 1.0; f /= k)
                    fractionList.add(f);
                fractionList.sort(null);
                if (fractionList.get(fractionList.size()-1).floatValue() < 1.0f)
                    fractionList.add(1.0);
                
                int index = fractionList.indexOf(f1);
                
                System.out.println(r1);
                System.out.println("List: " + fractionList);
                System.out.println(k);
                System.out.println(f1);
                System.out.println(index);
                
                float[] fractions = new float[fractionList.size()];
                Color[] colors = new Color[fractionList.size()];
                boolean colorOnEven = (index % 2 == 0) == clockwise;
                
                for (int i = 0; i < fractionList.size(); i++){
                    fractions[i] = fractionList.get(i).floatValue();
                    colors[i] = (colorOnEven == (i % 2 == 0)) ? color : TRANSPARENT_COLOR;
                }
                
                System.out.println(Arrays.toString(fractions));
                g.setPaint(new RadialGradientPaint((float) centerX, (float) centerY, (float) r1, fractions,colors));
                if (rect == null)
                    rect = new Rectangle2D.Double();
//                rect.setFrame(0, 0, width, height);
                rect.setFrameFromCenter(centerX, centerY, centerX+r1, centerY+r1);
                g.fill(rect);
            }
        }
    }
    @Override
    public String getName() {
        return "Ripple";
    }
    @Override
    protected String paramString(){
        return super.paramString()+
                ",base="+getBase();
    }
    @Override
    protected int getByteArrayLength(){
        return super.getByteArrayLength()+Double.BYTES;
    }
    @Override
    protected void toByteArray(ByteBuffer buffer){
        super.toByteArray(buffer);
        buffer.putDouble(getBase());
    }
    @Override
    protected void fromByteArray(ByteBuffer buffer){
        super.fromByteArray(buffer);
        setBase(buffer.getDouble());
    }
    @Override
    public void reset(){
        super.reset();
        setBase(2.0);
    }
}
