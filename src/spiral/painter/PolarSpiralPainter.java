/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import geom.GeometryMath;
import java.awt.Graphics2D;
import spiral.SpiralGeneratorUtilities;
import spiral.SpiralModel;

/**
 *
 * @author Mosblinker
 */
public abstract class PolarSpiralPainter extends SpiralPainter{
    /**
     * Implicit constructor.
     */
    protected PolarSpiralPainter(){}
    /**
     * Copy constructor for the class.
     * @param painter The GEGLSpiralPainter to copy.
     */
    protected PolarSpiralPainter(PolarSpiralPainter painter){
        super(painter);
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
     * 
     * @param r0
     * @param p0
     * @param r1
     * @param p1
     * @param angle
     * @return 
     */
    public abstract double getArcLength(double r0, double p0, double r1, 
            double p1, double angle);
    /**
     * 
     * @param r0
     * @param p0
     * @param r1
     * @param p1
     * @return 
     */
    public double getArcLength(double r0, double p0, double r1, double p1){
        return getArcLength(r0,p0,r1,p1,0.0);
    }
    /**
     * 
     * @param p
     * @param angle
     * @param clockwise
     * @return 
     */
    protected double getAzimuthValue(double p,double angle,boolean clockwise){
        p -= angle;
            // If the spiral is counter-clockwise
        if (!clockwise)
            p = -p;
        return p / FULL_CIRCLE_DEGREES;
    }
    /**
     * 
     * @param p
     * @param angle
     * @param clockwise
     * @return 
     */
    public double getAzimuthDegrees(double p,double angle,boolean clockwise){
        p *= FULL_CIRCLE_DEGREES;
            // If the spiral is counter-clockwise
        if (!clockwise)
            p = -p;
        return p + angle;
    }
    /**
     * 
     * @param p
     * @return 
     */
    protected abstract double getRadiusImpl(double p);
    /**
     * This returns the radial distance for the point on the spiral with the 
     * given azimuth.
     * @param p The azimuth of the point on the spiral to get.
     * @param angle The angle of rotation for the spiral.
     * @param clockwise {@code true} if the spiral is clockwise, {@code false} 
     * if the spiral is counter-clockwise.
     * @return The radial distance of the given point on the spiral.
     */
    protected double getRadius(double p, double angle, boolean clockwise){
        return getRadiusImpl(getAzimuthValue(p,angle,clockwise));
    }
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
     * 
     * @param r
     * @return 
     */
    protected abstract double getAzimuthImpl(double r);
    /**
     * This returns the azimuth for the point on the spiral with the given 
     * radial distance. This is used to do the calculations.
     * @param r The radial distance of the point on the spiral to get.
     * @param angle The angle of rotation for the spiral.
     * @param clockwise {@code true} if the spiral is clockwise, {@code false} 
     * if the spiral is counter-clockwise.
     * @return The azimuth of the given point on the spiral.
     */
    protected double getAzimuth(double r, double angle, boolean clockwise){
        return getAzimuthDegrees(getAzimuthImpl(r),angle,clockwise);
    }
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
     * boolean, double, double) 
     * @see #paintSpiralPolar(java.awt.Graphics2D, double, int, int, double, double, boolean, double, double) 
     */
    protected abstract double fillConditionValue();
    /**
     * 
     * @param angle
     * @return 
     */
    protected double alterRotationWhenNoColor2(double angle){
            // Shift it by 180 degrees
        return GeometryMath.boundDegrees(angle+HALF_CIRCLE_DEGREES);
    }
    /**
     * 
     * @param thickness
     * @return 
     */
    protected double alterThicknessWhenNoColor2(double thickness){
            // Invert the thickness
        return 1.0-thickness;
    }
    /**
     * 
     * @param g
     * @param model
     * @param width
     * @param height 
     */
    protected void fillBackground(Graphics2D g, SpiralModel model, 
            double width, double height){
            // If there is a background color
        if (!SpiralGeneratorUtilities.hasNoColor(model.getColor1())){
                // Set the color to use to the background color
            g.setColor(model.getColor1());
                // Fill the area
            fillArea(g,width,height);
        }
    }
    @Override
    protected boolean willFillAreaWithoutSpiral(double thickness){
            // If the thickness is less than or equal to zero or the thickness 
            // is greater than or equal to 1 or the fill condition value is 
            // equal to 1
        return super.willFillAreaWithoutSpiral(thickness) || 
                fillConditionValue() == 1.0;
    }
    @Override
    protected void paintSpiral(Graphics2D g, SpiralModel model, double angle, 
            int width,int height, double centerX, double centerY, 
            boolean clockwise, double radius, double thickness) {
            // If there is no foreground color
        if (SpiralGeneratorUtilities.hasNoColor(model.getColor2())){
                // Alter the thickness
            thickness = alterThicknessWhenNoColor2(thickness);
                // Alter the angle of rotation
            angle = alterRotationWhenNoColor2(angle);
                // Set the color to use to the background color
            g.setColor(model.getColor1());
        } else {
                // Fill the background
            fillBackground(g,model,width,height);
                // Set the color to use to the foreground color
            g.setColor(model.getColor2());
        }   // Paint the spiral
        paintSpiralPolar(g,model,adjustRotation(angle,thickness,clockwise),
                width,height,centerX,centerY,clockwise,radius,thickness);
    }
    /**
     * This is used to paint the spiral. This is given a copy of the graphics 
     * context that is clipped to the painted region.
     * @param g The graphics context to render to.
     * @param model The model containing data for the spiral
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
     * boolean, double, double) 
     * @see #isClockwise() 
     * @see #getSpiralRadius() 
     * @see #getThickness() 
     */
    protected abstract void paintSpiralPolar(Graphics2D g, SpiralModel model, 
            double angle, int width, int height, double centerX, double centerY, 
            boolean clockwise, double radius, double thickness); 
}
