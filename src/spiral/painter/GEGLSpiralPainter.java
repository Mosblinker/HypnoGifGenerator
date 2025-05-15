/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import geom.GeometryMath;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.*;

/**
 *
 * @author Mosblinker
 */
public abstract class GEGLSpiralPainter extends SpiralPainter{
    
    public static final String SPIRAL_RADIUS_PROPERTY_CHANGED = 
            "SpiralRadiusPropertyChanged";
    
    public static final String THICKNESS_PROPERTY_CHANGED = 
            "ThicknessPropertyChanged";
    
    /**
     * This is the spiral radius that controls the size of the spirals.
     */
    private double radius = 100.0;
    /**
     * This is the thickness of the spiral.
     */
    private double thickness = 0.5;
    /**
     * This is a scratch Rectangle2D object used to fill the painted area when 
     * the entire area is to be filled with a solid color. This is initially 
     * null and is initialized the first time it is used.
     */
    private Rectangle2D rect = null;
    /**
     * Implicit constructor.
     */
    protected GEGLSpiralPainter(){}
    /**
     * Copy constructor for the class.
     * @param painter The GEGLSpiralPainter to copy.
     */
    protected GEGLSpiralPainter(GEGLSpiralPainter painter){
        super(painter);
        this.radius = painter.radius;
        this.thickness = painter.thickness;
    }
    /**
     * 
     * @param radius 
     */
    public void setSpiralRadius(double radius){
            // If the new radius is less than or equal to zero
        if (radius <= 0)
            throw new IllegalArgumentException();
            // If the radius would change
        if (this.radius != radius){
                // Get the old radius.
            double old = this.radius;
            this.radius = radius;
            firePropertyChange(SPIRAL_RADIUS_PROPERTY_CHANGED,old,radius);
        }
    }
    /**
     * 
     * @return 
     */
    public double getSpiralRadius(){
        return radius;
    }
    /**
     * 
     * @param thickness 
     * @see #setBalance(double) 
     */
    public void setThickness(double thickness){
            // If the new thickness is less than 0 or greater than 1
        if (thickness < 0 || thickness > 1)
            throw new IllegalArgumentException();
            // If the thicnkess would change
        if (this.thickness != thickness){
                // Get the old thickness
            double old = this.thickness;
            this.thickness = thickness;
            firePropertyChange(THICKNESS_PROPERTY_CHANGED,old,thickness);
        }
    }
    /**
     * 
     * @return 
     * @see #getBalance() 
     */
    public double getThickness(){
        return thickness;
    }
    /**
     * 
     * @param balance 
     * @see #setThickness(double) 
     */
    public void setBalance(double balance){
        setThickness((1.0 + balance) / 2.0);
    }
    /**
     * 
     * @return The balance between the two colors
     * @see #getThickness() 
     */
    public double getBalance(){
        return getThickness()*2.0 - 1.0;
    }
    /**
     * 
     * @param r0
     * @param r1
     * @param angle
     * @return 
     */
    public abstract double getArcLengthRadius(double r0, double r1, double angle);
    /**
     * 
     * @param r0
     * @param r1
     * @return 
     */
    public double getArcLengthRadius(double r0, double r1){
        return getArcLengthRadius(r0,r1,0.0);
    }
    /**
     * 
     * @param p0
     * @param p1
     * @param angle
     * @return 
     */
    public abstract double getArcLengthAzimuth(double p0, double p1, double angle);
    /**
     * 
     * @param p0
     * @param p1
     * @return 
     */
    public double getArcLengthAzimuth(double p0, double p1){
        return getArcLengthAzimuth(p0,p1,0.0);
    }
    /**
     * This returns the radial distance for the point on the spiral with the 
     * given azimuth.
     * @param p The azimuth of the point on the spiral to get.
     * @param angle The angle of rotation for the spiral.
     * @param clockwise {@code true} if the spiral is clockwise, {@code false} 
     * if the spiral is counter-clockwise.
     * @return The radial distance of the given point on the spiral.
     */
    protected abstract double getRadius(double p,double angle,boolean clockwise);
    /**
     * This returns the radial distance for the point on the spiral with the 
     * given azimuth.
     * @param p The azimuth of the point on the spiral to get.
     * @param angle The angle of rotation for the spiral.
     * @return The radial distance of the given point on the spiral.
     */
    public double getRadius(double p, double angle){
            // Get if the spiral is clockwise
        boolean clockwise = isClockwise();
            // Adjust the angle of rotation, and then use that to get the radius
        return getRadius(p,adjustRotation(angle,getThickness(),clockwise),
                clockwise);
    }
    /**
     * This returns the radial distance for the point on the spiral with the 
     * given azimuth.
     * @param p The azimuth of the point on the spiral to get.
     * @return The radial distance of the given point on the spiral.
     */
    public double getRadius(double p){
        return getRadius(p,0.0);
    }
    /**
     * This returns the azimuth for the point on the spiral with the given 
     * radial distance. This is used to do the calculations.
     * @param r The radial distance of the point on the spiral to get.
     * @param angle The angle of rotation for the spiral.
     * @param clockwise {@code true} if the spiral is clockwise, {@code false} 
     * if the spiral is counter-clockwise.
     * @return The azimuth of the given point on the spiral.
     */
    protected abstract double getAzimuth(double r, double angle,
            boolean clockwise);
    /**
     * This returns the azimuth for the point on the spiral with the given 
     * radial distance.
     * @param r The radial distance of the point on the spiral to get.
     * @param angle The angle of rotation for the spiral.
     * @return The azimuth of the given point on the spiral.
     */
    public double getAzimuth(double r, double angle){
            // Get if the spiral is clockwise
        boolean clockwise = isClockwise();
            // Get the azimuth, adjust the rotation, and then use that to get  
            // the azimuth 
        return getAzimuth(r,adjustRotation(angle,getThickness(),clockwise),
                clockwise);
    }
    /**
     * This returns the azimuth for the point on the spiral with the given 
     * radial distance.
     * @param r The radial distance of the point on the spiral to get.
     * @return The azimuth of the given point on the spiral.
     */
    public double getAzimuth(double r){
        return getAzimuth(r,0.0);
    }
    /**
     * This is the value that, when equal to 1.0, results in the entire area for 
     * the spiral to be filled with a translucent version of the color set on 
     * the graphics context. When this value is equal to 1.0, the spiral paint 
     * code will exit early.
     * @return The value to check to see if the area should be filled with a 
     * translucent color.
     * @see #paintSpiral(java.awt.Graphics2D, double, int, int, double, double, 
     * boolean) 
     * @see #paintSpiral(java.awt.Graphics2D, double, int, int, double, double, 
     * boolean, double, double) 
     */
    protected abstract double fillConditionValue();
    /**
     * This is used to adjust the angle of rotation for the spiral.
     * @param angle The angle of rotation.
     * @param thickness The thickness of the spiral.
     * @param clockwise {@code true} if the spiral is clockwise, {@code false} 
     * if the spiral is counter-clockwise.
     * @return The angle of rotation for the spiral, adjusted accordingly.
     * @see #getThickness() 
     * @see #isClockwise() 
     */
    protected double adjustRotation(double angle, double thickness, 
            boolean clockwise){
            // Bound the angle of rotation
        angle = GeometryMath.boundDegrees(angle);
            // If the spiral is going clockwise and the angle is not 0
        if (clockwise && angle > 0.0)
            angle = FULL_CIRCLE_DEGREES - angle;
            // Alter the angle based off the thickness of the spiral
        return (angle + (thickness / 2.0)*FULL_CIRCLE_DEGREES) % FULL_CIRCLE_DEGREES;
    }
    @Override
    protected void paintSpiral(Graphics2D g, double angle, int width,int height, 
            double centerX, double centerY, boolean clockwise) {
            // Get the thickness of the spiral
        double t = getThickness();
            // If the thickness is greater than zero
        if (t > 0.0){
                // If the thickness is greater than or equal to 1 or the fill 
                // condition value is equal to 1
            if (t >= 1.0 || fillConditionValue() == 1.0){
                    // If the thickness is less than 1
                if (t < 1.0){
                        // Get the color from the graphics context
                    Color c = g.getColor();
                        // Set the color to be a translucent color based off 
                        // the thickness of the spiral
                    g.setColor(new Color((c.getRGB() & 0x00FFFFFF) | 
                        (((int)Math.floor(c.getAlpha()*t)) << 24), true));
                }   // If the rectangle object has not been initialized yet
                if (rect == null)
                    rect = new Rectangle2D.Double();
                    // Set the frame of the rectangle to cover the entire area
                rect.setFrame(0, 0, width, height);
                    // Fill the area
                g.fill(rect);
            } else {
                    // Paint the spiral
                paintSpiral(g,adjustRotation(angle,t,clockwise),width,height,
                        centerX,centerY,clockwise,getSpiralRadius(),t);
            }
        }
    }
    /**
     * This is used to paint the spiral. This is given a copy of the graphics 
     * context that is clipped to the painted region.
     * @param g The graphics context to render to.
     * @param angle The angle for the spiral. This is in the range of {@code 0} 
     * to {@value MAXIMUM_ANGLE}, exclusive.
     * @param width This is the width of the area to fill with the spiral.
     * @param height This is the height of the area to fill with the spiral.
     * @param centerX This is the x-coordinate of the center of the area.
     * @param centerY This is the y-coordinate of the center of the area.
     * @param clockwise {@code true} if the spiral is clockwise, {@code false} 
     * if the spiral is counter-clockwise.
     * @param radius The spiral radius which controls the size of the spirals.
     * @param thickness The thickness of the spiral.
     * @see #paint
     * @see #paintSpiral(java.awt.Graphics2D, double, int, int, double, double, 
     * boolean) 
     * @see #isClockwise() 
     * @see #getSpiralRadius() 
     * @see #getThickness() 
     */
    protected abstract void paintSpiral(Graphics2D g, double angle, int width, 
            int height, double centerX, double centerY, boolean clockwise, 
            double radius, double thickness); 
    @Override
    protected String paramString(){
        return super.paramString()+
                ",spiralRadius="+getSpiralRadius()+
                ",thickness="+getThickness();
    }
}
