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
public class ArithmeticSpiralPainter extends PolarSpiralPainter {
    /**
     * 
     */
    public ArithmeticSpiralPainter(){
    }
    /**
     * 
     * @param painter The ArithmeticSpiralPainter to copy.
     */
    public ArithmeticSpiralPainter(ArithmeticSpiralPainter painter){
        super(painter);
    }
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
     * 
     * @param b
     * @param r0
     * @param r1
     * @param angle
     * @param clockwise
     * @return 
     */
    protected double getArcLengthRadius(double b, double r0, double r1, 
            double angle, boolean clockwise){
        return getArcLengthAzimuthHelper(b,getAzimuthImpl(b,r0),
                getAzimuthImpl(b,r1));
    }
    @Override
    public double getArcLengthRadius(double r0, double r1, double angle) {
        return getArcLengthRadius(getSpiralRadius(), r0,r1,angle,isClockwise());
    }
    /**
     * 
     * @param p
     * @return 
     */
    protected double getArcLengthHelper(double p){
            // Get the value of the square root of (1+p^2)
        double a = Math.sqrt(1+p*p);
        return p * a + Math.log(p + a);
    }
    /**
     * 
     * @param b
     * @param p0
     * @param p1
     * @return 
     */
    protected double getArcLengthAzimuthHelper(double b, double p0, double p1){
        return (b / 2.0) * (getArcLengthHelper(p1) - getArcLengthHelper(p0));
    }
    /**
     * 
     * @param b
     * @param p0
     * @param p1
     * @param angle
     * @param clockwise
     * @return 
     */
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
    protected void paintSpiralGegl(Graphics2D g, double angle, int width,int height, 
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
            // This is the amount by which to offset the angle for getting the 
            // the second spiral that makes up the shape
        double offset = FULL_CIRCLE_DEGREES * (1-thickness);
            // This is the value to use to calculate the other spiral that 
            // completes the path. This is used for the outer spiral curve when 
            // clockwise and the inner spiral curve when counter-clockwise 
        double angle2 = angle + offset;
            // This is the starting azimuth
        double p0 = angle;
            // This is the ending azimuth
        double p1 = getAzimuth(radius,Math.sqrt(width*width+height*height)/2.0, 
                angle, true);
            // If the spiral is going anti-clockwise
        if (!clockwise){
            p1 = -p1-FULL_CIRCLE_DEGREES;
            p0 = p0 + (HALF_CIRCLE_DEGREES);
            p0 -= offset;
        }   // Effectively round the ending azimuth up to the nearest quarter angle
        p1 += (QUARTER_CIRCLE_DEGREES - (p1 % QUARTER_CIRCLE_DEGREES)) % QUARTER_CIRCLE_DEGREES;
            // Process the first spiral from out to in
        path = processLinearSpiral(radius,p0,p1,angle,clockwise,centerX,
                centerY,true,point1,point2,point3,path);
            // Get the radius of the center of the spiral
        double r0 = getRadius(radius, p0, angle, clockwise);
            // Get the starting azimuth for the second spiral
        p0 = getAzimuth(radius,r0,angle2,clockwise);
            // Process the second spiral from in to out
        path = processLinearSpiral(radius,p0,p1+FULL_CIRCLE_DEGREES,angle2,
                clockwise,centerX,centerY,false,point1,point2,point3,path);
            // Close the path
        path.closePath();
            // Fill in the spiral
        g.fill(path);
    }
    /**
     * 
     * @param b
     * @param p0
     * @param p1
     * @param angle
     * @param clockwise
     * @param x
     * @param y
     * @param point1
     * @param point2
     * @param point3
     * @param path 
     */
    protected void processLinearSpiral(double b, double p0, double p1, 
            double angle, boolean clockwise, double x, double y, Point2D point1, 
            Point2D point2, Point2D point3, Path2D path){
            // Get the azimuth between the two azimuths. This will be used for 
            // interpolating the segment of the spiral
        double pInter = (p0 + p1) / 2.0;
            // Get the end point for the segment of the spiral
        point3 = GeometryMath.polarToCartesianDegrees(getRadius(b,p1,angle,
                clockwise),p1,x,y,point3);
             // Get the interpolation point for the segment of the spiral
        point2 = GeometryMath.polarToCartesianDegrees(getRadius(b,pInter,angle,
                clockwise),pInter,x,y,point2);
            // Get the control point for the bezier curve used to draw the 
            // segment of the spiral
        point2 = GeometryMath.getQuadBezierControlPoint(point1, point2, point3, 
                point2);
            // Add the bezier curve to the path
        path.quadTo(point2.getX(), point2.getY(), point3.getX(), point3.getY());
    }
    /**
     * 
     * @param b
     * @param p0
     * @param p1
     * @param angle
     * @param clockwise
     * @param x
     * @param y
     * @param reverse
     * @param point1
     * @param point2
     * @param point3
     * @param path
     * @return 
     */
    protected Path2D processLinearSpiral(double b, double p0, double p1, 
            double angle, boolean clockwise, double x, double y,boolean reverse, 
            Point2D point1, Point2D point2, Point2D point3, Path2D path){
            // If the given path is null
        if (path == null)
            path = new Path2D.Double();
            // Calculate the first azimuth rounded to the nearest interpolation 
            // angle
        double p2 = p0 + (INTERPOLATION_ANGLE - (p0 % INTERPOLATION_ANGLE)) 
                % INTERPOLATION_ANGLE;
            // Get whether the first interpolation angle is the same as the 
            // first azimuth
        boolean mismatch = p2 != p0;
            // This gets the azimuth for the first point. If this is going in 
            // reverse, then this will be the last azimuth. Otherwise get the 
            // first azimuth.
        double startP = (reverse) ? p1 : p0;
            // Get the point for the starting azimuth
        point1 = GeometryMath.polarToCartesianDegrees(getRadius(b,startP,angle,
                clockwise),startP,x,y,point1);
            // Get the current position of the path
        Point2D currPoint = path.getCurrentPoint();
            // If the path is empty
        if (currPoint == null)
            path.moveTo(point1.getX(), point1.getY());
            // If the path is not at the first point
        else if (!currPoint.equals(point1))
            path.lineTo(point1.getX(), point1.getY());
            // This is the previous azimuth for the point
        double prevP = startP;
            // If the path is not starting from the center outwards and the 
            // center azimuth is different from the first interpolation azimuth
        if (!reverse && mismatch){ 
                // Process the part of the spiral between the azimuths
//            processLinearSpiral(b,p2,p0,angle,clockwise,x,y,point1,point2,
//                    point3,path);
//            point1.setLocation(point3);
//            p0 = p2;
//            prevP = p0;
        }   // Get the smaller of the two azimuths
        double minP = Math.min(p0, p1);
            // Get the larger of the two azimuths
        double maxP = Math.max(p0, p1);
            // This is the amount by which to increment the azimuths to get the 
            // next azimuth. 
            // If the value of reverse is the same as the direction of the 
            // spiral (i.e. reverse the spiral and the spiral is clockwise or 
            // don't reverse the spiral and the spiral is counter-clockwise), 
            // then decrement the azimuths
        double inc = (reverse == clockwise) ? -INTERPOLATION_ANGLE : INTERPOLATION_ANGLE;
            // If the path is not going in reverse
        if (!reverse){
                // If the spiral is going clockwise
            if (clockwise)
                maxP += INTERPOLATION_ANGLE;
            else
                minP -= INTERPOLATION_ANGLE;
        }
            // A for loop to go through the points on the spiral 
        for (double p = prevP + inc;     
                    // Go up until it reaches the opposite azimuth extreme
                (p > minP && p < maxP); p += inc){
                // Process the part of the spiral between the azimuths
            processLinearSpiral(b,prevP,p,angle,clockwise,x,y,point1,point2,
                    point3,path);
            point1.setLocation(point3);
            prevP = p;
        }   // If the spiral is reversed and has not reached the center of the 
            // spiral due to the mismatch
        if (reverse && prevP != p0){
            System.out.println("Adjusting 1 " + prevP + " " + p2);
                // Process the part of the spiral between the azimuths
            processLinearSpiral(b,prevP,p0,angle,clockwise,x,y,point1,point2,
                    point3,path);
            point1.setLocation(point3);
        }
        return path;
    }
    @Override
    public String getName() {
        return "Arithmetic";
    }
    @Override
    public ArithmeticSpiralPainter clone() {
        return new ArithmeticSpiralPainter(this);
    }
}
