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
        
        double offset = FULL_CIRCLE_DEGREES * (1-thickness);
        
        double angle2 = GeometryMath.boundDegrees(angle + offset);
        
        double p0 = angle;
        double p1 = getAzimuth(radius,Math.sqrt(width*width+height*height)/2.0, 
                angle, true);
        
        if (!clockwise){
            p1 = -p1-FULL_CIRCLE_DEGREES;
            p0 = FULL_CIRCLE_DEGREES-p0;
            p0 -= offset;
        }
            // Effectively round it up to the nearest quarter angle
        p1 += (QUARTER_CIRCLE_DEGREES - (p1 % QUARTER_CIRCLE_DEGREES)) % QUARTER_CIRCLE_DEGREES;
        
        processLinearSpiral(radius,p0,p1,angle,clockwise,centerX,
                centerY,true,point1,point2,point3,path);
        double r0 = getRadius(radius, p0, angle, clockwise);
        p0 = getAzimuth(radius,r0,angle2,clockwise);
        processLinearSpiral(radius,p0,p1+FULL_CIRCLE_DEGREES,angle2,
                clockwise,centerX,centerY,false,point1,point2,point3,path);
        path.closePath();
        
        g.fill(path);
    }
    
    protected void processLinearSpiral(double b, double p0, double p1, 
            double angle, boolean clockwise, double x, double y, Point2D point1, 
            Point2D point2, Point2D point3, Path2D path){
        double pInter = (p0 + p1) / 2.0;
        point3 = GeometryMath.polarToCartesianDegrees(getRadius(b,p1,angle,
                clockwise),p1,x,y,point3);
        point2 = GeometryMath.polarToCartesianDegrees(
                getRadius(b,pInter,angle,clockwise),pInter,x,y,point2);
        point2 = GeometryMath.getQuadBezierControlPoint(point1, point2, point3, 
                point2);
        path.quadTo(point2.getX(), point2.getY(), point3.getX(), point3.getY());
    }
    
    protected Path2D processLinearSpiral(double b, double p0, double p1, 
            double angle, boolean clockwise, double x, double y,boolean reverse, 
            Point2D point1, Point2D point2, Point2D point3, Path2D path){
        if (path == null)
            path = new Path2D.Double();
        Point2D currPoint = path.getCurrentPoint();
        double p2 = p0 + (INTERPOLATION_ANGLE - (p0 % INTERPOLATION_ANGLE)) 
                % INTERPOLATION_ANGLE;
        boolean startOdd = !reverse && p2 != p0;
        if (startOdd){
            point1 = GeometryMath.polarToCartesianDegrees(getRadius(b,p2,angle,
                    clockwise),p2,x,y,point1);
            processLinearSpiral(b,p2,p0,angle,clockwise,x,y,point1,point2,
                    point3,path);
            point1.setLocation(point3);
            p0 = p2;
        }
        double startP = (reverse) ? p1 : p0;
        if (!startOdd)
            point1 = GeometryMath.polarToCartesianDegrees(getRadius(b,startP,
                    angle,clockwise),startP,x,y,point1);
        double minP = Math.min(p0, p1);
        double maxP = Math.max(p0, p1);
        double inc = (reverse == clockwise) ? -INTERPOLATION_ANGLE : INTERPOLATION_ANGLE;
        if (currPoint == null)
            path.moveTo(point1.getX(), point1.getY());
        else if (!currPoint.equals(point1))
            path.lineTo(point1.getX(), point1.getY());
        for (double p = startP;     
                    // Go up until it reaches the opposite azimuth extreme
                (p > minP && p < maxP) || p == startP; p += inc){
            processLinearSpiral(b,p,p+inc,angle,clockwise,x,y,point1,point2,
                    point3,path);
            point1.setLocation(point3);
        }
        if (reverse && p0 != p2){
            processLinearSpiral(b,p0,p2,angle,clockwise,x,y,point1,point2,
                    point3,path);
            point1.setLocation(point3);
        }
        return path;
    }
    
    
}
