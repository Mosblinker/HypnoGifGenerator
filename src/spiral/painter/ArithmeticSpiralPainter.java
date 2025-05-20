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
public class ArithmeticSpiralPainter extends GEGLSpiralPainter {
    
    private static final double INTERPOLATION_ANGLE_2 = INTERPOLATION_ANGLE/2.0;
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
        
        double maxR = Math.sqrt(width*width+height*height)/2.0;
        
        double p1 = getAzimuth(radius,maxR, angle, true);
            // Effectively round it up to the nearest quarter angle
        p1 += (QUARTER_CIRCLE_DEGREES - (p1 % QUARTER_CIRCLE_DEGREES)) % QUARTER_CIRCLE_DEGREES;
        
        double p0 = 90;
        
        double pA = p0;
        double rA = getRadius(radius,pA,angle,clockwise);
        point1 = GeometryMath.polarToCartesianDegrees(rA,pA,centerX,centerY,point1);
        
        path.moveTo(point1.getX(), point1.getY());
        
        int i = 0;
        for (double p = pA+INTERPOLATION_ANGLE; p <= p1; p+= INTERPOLATION_ANGLE, i++){
            double r = getRadius(radius,p,angle,clockwise);
            point4 = GeometryMath.polarToCartesianDegrees(r,p,centerX,centerY,point4);
            double pI = p - INTERPOLATION_ANGLE_2;
            point3 = GeometryMath.polarToCartesianDegrees(getRadius(radius,pI,angle,clockwise),
                    pI,centerX,centerY,point3);
            point2 = GeometryMath.getQuadBezierControlPoint(point1, point3, 
                    point4, point2);
            
            if (i < i0){
                System.out.println(i+": ");
                System.out.println("\tPoint 1: " +point1);
                System.out.println("\tPoint 4: " +point4);
                System.out.println("\tPoint 3: " + point3);
                System.out.println("\tPoint 2: " + point2);
                System.out.println();
            }
            
            path.quadTo(point2.getX(), point2.getY(), point4.getX(), point4.getY());
            
            point1.setLocation(point4);
            rA = r;
            pA = p;
        }
        g.draw(path);
        
        if (b1){
            for (int t = 0; t <= i0; t++){
                double p = p0 + (INTERPOLATION_ANGLE*t);
                point1 = GeometryMath.polarToCartesianDegrees(getRadius(radius,p ,
                        angle,clockwise),p ,centerX,centerY,point1);
                double m = getTangentSlope(radius,getRadius(radius,p,angle,clockwise),
                        p,angle,clockwise);
                double y1 = GeometryMath.getLineY(m,0,point1);
                double y2 = GeometryMath.getLineY(m,width,point1);

                System.out.printf("%5d: %10.5f %10.5f %10.5f %10.5f %10.5f %n",t,m,0.0,y1,(double)width,y2);
                g.setColor(Color.BLUE);
                g.draw(new Line2D.Double(0, y1, width, y2));
            }
        }
    }
    
    protected double getTangentSlope(double b, double r, double p,double angle, boolean clockwise){
        p = getAzimuthValue(p,angle,clockwise);
        double theta = Math.toRadians(GeometryMath.boundDegrees(p*GeometryMath.FULL_CIRCLE_DEGREES));
        double sin = Math.sin(theta);
        double cos = Math.cos(theta);
        return (r * cos + b * sin) / (-r*sin + b * cos);
    }
    
    public int i0 = 0;
    public boolean b1;
}
