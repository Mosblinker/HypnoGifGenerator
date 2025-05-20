/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import java.awt.Graphics2D;
import java.awt.geom.*;

/**
 *
 * @author Mosblinker
 */
public class ArithmeticSpiralPainter extends GEGLSpiralPainter {
    /**
     * A scratch Path2D object to use to create the spiral. This is initially 
     * null and is initialized the first time it is used.
     */
    private Path2D path = null;
    /**
     * A scratch Point2D object to use to create the spiral. This is initially 
     * null and is initialized the first time it is used.
     */
    private Point2D point1 = null;
    /**
     * A second scratch Point2D object to use to create the spiral. This is 
     * initially null and is initialized the first time it is used.
     */
    private Point2D point2 = null;
    /**
     * A third scratch Point2D object to use to create the spiral. This is 
     * initially null and is initialized the first time it is used.
     */
    private Point2D point3 = null;
    /**
     * A fourth scratch Point2D object to use to create the spiral. This is 
     * initially null and is initialized the first time it is used.
     */
    private Point2D point4 = null;
    
    protected double getArcLengthRadius(double b, double r0, double r1, 
            double angle, boolean clockwise){
        return getArcLengthAzimuthHelper(b,getAzimuthImpl(b,r0),
                getAzimuthImpl(b,r1));
    }
    @Override
    public double getArcLengthRadius(double r0, double r1, double angle) {
        return getArcLengthRadius(getSpiralRadius(), r0,r1,angle,isClockwise());
    }
    
    protected double getArcLengthHelper(double p){
        if (p <= 0)
            return 0;
        double a = Math.sqrt(1+p*p);
        return p * a + Math.log(p + a);
    }
    
    protected double getArcLengthAzimuthHelper(double b, double p0, double p1){
        return (b / 2.0) * (getArcLengthHelper(p1) - getArcLengthHelper(p0));
    }
    
    protected double getArcLengthAzimuth(double b, double p0, double p1, 
            double angle, boolean clockwise){
        return getArcLengthAzimuthHelper(b,getAzimuthValue(p0,angle,clockwise),
                getAzimuthValue(p1,angle,clockwise));
    }
    @Override
    public double getArcLengthAzimuth(double p0, double p1, double angle) {
        return getArcLengthAzimuth(getSpiralRadius(),p0,p1,angle,isClockwise());
    }
    @Override
    public double getArcLength(double r0, double p0, double r1, double p1, double angle){
        return getArcLengthAzimuth(p0,p1,angle);
    }
    /**
     * 
     * @param b
     * @param p
     * @return 
     */
    protected double getRadiusImpl(double b, double p){
        return b * p;
    }
    @Override
    protected double getRadiusImpl(double p){
        return getRadiusImpl(getSpiralRadius(),p);
    }
    /**
     * 
     * @param b
     * @param p
     * @param angle
     * @param clockwise
     * @return 
     */
    protected double getRadius(double b, double p, double angle, boolean clockwise) {
        return getRadiusImpl(b,getAzimuthValue(p,angle,clockwise));
    }
    /**
     * 
     * @param b
     * @param r
     * @return 
     */
    protected double getAzimuthImpl(double b, double r){
        return r / b;
    }
    @Override
    protected double getAzimuthImpl(double r){
        return getAzimuthImpl(getSpiralRadius(),r);
    }
    /**
     * 
     * @param b
     * @param r
     * @param angle
     * @param clockwise
     * @return 
     */
    protected double getAzimuth(double b, double r, double angle, boolean clockwise){
        return getAzimuthDegrees(getAzimuthImpl(b,r),angle,clockwise);
    }
    @Override
    protected double fillConditionValue() {
        return getSpiralRadius();
    }
    @Override
    protected void paintSpiral(Graphics2D g, double angle, int width,int height, 
            double centerX, double centerY, boolean clockwise, double radius, 
            double thickness) {
            // If the path has not been initialized yet
        if (path == null)
            path = new Path2D.Double();
        else
            path.reset();
            // If the first point has not been initialized yet
        if (point1 == null)
            point1 = new Point2D.Double();
            // If the second point has not been initialized yet
        if (point2 == null)
            point2 = new Point2D.Double();
            // If the third point has not been initialized yet
        if (point3 == null)
            point3 = new Point2D.Double();
            // If the fourth point has not been initialized yet
        if (point4 == null)
            point4 = new Point2D.Double();
        
        
    }
    
}
