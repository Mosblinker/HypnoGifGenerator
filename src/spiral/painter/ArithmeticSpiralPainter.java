/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import geom.GeometryMath;
import java.awt.Graphics2D;
import java.awt.geom.*;

/**
 *
 * @author Mosblinker
 */
public class ArithmeticSpiralPainter extends GEGLSpiralPainter {
    public int i0 = 0;
    public boolean b1 = false;
    public boolean b2 = false;
    
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
        
            // Adjust the angle of rotation for the spiral
        angle = adjustRotation(angle,thickness,clockwise);
        
        double offset = FULL_CIRCLE_DEGREES * (1-thickness);
        
        double r1 = Math.sqrt(width*width+height*height)/2.0;
        
        double angle2 = GeometryMath.boundDegrees(angle + offset);
        
        double p1 = getAzimuth(radius,r1, angle, true);
        
        double startP = angle;
        
        if (!clockwise){
            p1 = -p1-FULL_CIRCLE_DEGREES;
            startP = FULL_CIRCLE_DEGREES-startP;
            startP -= offset;
        }
            // Effectively round it up to the nearest interplation angle
        p1 += (INTERPOLATION_ANGLE - (p1 % INTERPOLATION_ANGLE)) % INTERPOLATION_ANGLE;
        double p0 = startP + (INTERPOLATION_ANGLE - (startP % INTERPOLATION_ANGLE)) % INTERPOLATION_ANGLE;
        
        processLinearSpiral(radius,startP,p0,p1,angle,clockwise,centerX,
                centerY,true,point1,point2,point3,path);
        double r0 = getRadius(radius, startP, angle, clockwise);
        startP = getAzimuth(radius,r0,angle2,clockwise);
        processLinearSpiral(radius,startP,p0,p1+FULL_CIRCLE_DEGREES,angle2,
                clockwise,centerX,centerY,false,point1,point2,point3,path);
        
        g.draw(path);
    }
    
    protected void processLinearSpiral(double b, double p0, double p1, 
            double angle, boolean clockwise, double x, double y, Point2D point1, 
            Point2D point2, Point2D point3, Path2D path){
        double r = getRadius(b,p1,angle,clockwise);
        double pInter = (p0 + p1) / 2.0;
        point3 = GeometryMath.polarToCartesianDegrees(r,p1,x,y,point3);
        point2 = GeometryMath.polarToCartesianDegrees(
                getRadius(b,pInter,angle,clockwise),pInter,x,y,point2);
        if (b1){
            System.out.println("Polar: " + r + ", " + p1);
            System.out.println("Point 1: " + point1);
            System.out.println("Point 2: " + point2);
            System.out.println("Point 3: " + point3);
        }
        point2 = GeometryMath.getQuadBezierControlPoint(point1, point2, point3, 
                point2);
        if (b1){
            System.out.println("Point C: " +point2);
            System.out.println();
        }
        path.quadTo(point2.getX(), point2.getY(), point3.getX(), point3.getY());
    }
    
    protected Path2D processLinearSpiral(double b, double p0, double p1, 
            double p2, double angle, boolean clockwise, double x, double y, 
            boolean reverse, Point2D point1, Point2D point2, Point2D point3, 
            Path2D path){
        if (path == null)
            path = new Path2D.Double();
        Point2D currPoint = path.getCurrentPoint();
        p1 = p0 + (INTERPOLATION_ANGLE - (p0 % INTERPOLATION_ANGLE)) % INTERPOLATION_ANGLE;
        double minP = Math.min(p1, p2);
        double maxP = Math.max(p1, p2);
        double startP = (reverse) ? p2 : p1;
        double inc = (reverse == clockwise) ? -INTERPOLATION_ANGLE : INTERPOLATION_ANGLE;
        double p3 = (reverse) ? p2 : p0;
        System.out.println(minP + " " + maxP + " " + p3 + " " + startP + " " + inc);
        point1 = GeometryMath.polarToCartesianDegrees(getRadius(b,p3,angle,
                clockwise),p3,x,y,point1);
        if (currPoint == null)
            path.moveTo(point1.getX(), point1.getY());
        else if (!currPoint.equals(point1))
            path.lineTo(point1.getX(), point1.getY());
        if (!reverse && p3 != startP){
            processLinearSpiral(b,p3,startP,angle,clockwise,x,y,point1,point2,
                    point3,path);
            point1.setLocation(point3);
        }
        for (double p = startP;     
                    // Go up until it reaches the opposite azimuth extreme
                (p > minP && p < maxP) || p == startP; p += inc){
            System.out.println("Hello " + p);
            processLinearSpiral(b,p,p+inc,angle,clockwise,x,y,point1,point2,
                    point3,path);
            point1.setLocation(point3);
        }
        if (reverse && p0 != p1){
            processLinearSpiral(b,p0,p1,angle,clockwise,x,y,point1,point2,
                    point3,path);
            point1.setLocation(point3);
        }
        return path;
    }
    
    
}
