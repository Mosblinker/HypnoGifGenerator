/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import java.awt.*;
import java.nio.ByteBuffer;

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
