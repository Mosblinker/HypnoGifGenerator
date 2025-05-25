/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import geom.GeometryMath;
import java.awt.Graphics2D;
import java.awt.geom.*;
import java.nio.ByteBuffer;

/**
 *
 * @author Mosblinker
 */
public class LogarithmicSpiralPainter extends GEGLSpiralPainter implements LogarithmicSpiral{
    /**
     * This is the target radius for the start of the spiral. This is set to 0.1 
     * to ensure that the spiral is properly formed in the center of the image 
     * with a pitch that makes it appear that it infinitely gets smaller as it 
     * gets closer to the center of the image.
     */
    protected static final double STARTING_RADIUS = 0.1;
    /**
     * This returns the target radius for the start of the spiral.
     * @param tx
     * @return 
     */
    protected static double getStartRadius(AffineTransform tx){
            // This gets the amount by which to scale the starting radius for 
            // the spiral
        double rScale = 1.0;
            // If there is a non-null transform provided
        if (tx != null){
                // Get the larger of the scale factors applied on the transform, 
                // or 1 if both scale factors are less than 1
            rScale = Math.max(Math.max(tx.getScaleX(), tx.getScaleY()),1.0);
        }   // Divide the starting radius by the scale in order to ensure that 
            // the spiral appears as if it is infinitely getting smaller the 
            // closer it gets to the center
        return STARTING_RADIUS / rScale;
    }
    /**
     * This returns the target radius for the start of the spiral.
     * @param g
     * @return 
     */
    protected static double getStartRadius(Graphics2D g){
        return getStartRadius(g.getTransform());
    }
    /**
     * This is the base for the spiral.
     */
    private double base = 2.0;
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
    /**
     * 
     */
    public LogarithmicSpiralPainter(){
    }
    /**
     * 
     * @param painter The LogarithmicSpiralPainter to copy.
     */
    public LogarithmicSpiralPainter(LogarithmicSpiralPainter painter){
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
    /**
     * 
     * @param k
     * @return 
     */
    private double getSignPitchInverted(double k){
        return Math.sqrt((k*k)+1)/k;
    }
    /**
     * 
     * @param a
     * @param k
     * @param r0
     * @param r1
     * @return 
     */
    protected double getArcLengthRadius(double a, double k, double r0, double r1){
        return getSignPitchInverted(k)* Math.abs(r1-r0);
    }
    @Override
    public double getArcLengthRadius(double r0, double r1, double angle) {
        return getArcLengthRadius(getSpiralRadius(),getLogarithmicK(),r0,r1);
    }
    /**
     * 
     * @param a
     * @param k
     * @param p0
     * @param p1
     * @param angle
     * @param clockwise
     * @return 
     */
    protected double getArcLengthAzimuth(double a, double k, double p0, 
            double p1, double angle, boolean clockwise){
        return getArcLengthRadius(a,k,getRadius(a,k,p0,angle,clockwise),
                getRadius(a,k,p1,angle,clockwise));
    }
    @Override
    public double getArcLength(double r0, double p0, double r1, double p1, 
            double angle){
        return getArcLengthRadius(r0,r1,angle);
    }
    @Override
    public double getArcLengthAzimuth(double p0, double p1, double angle) {
        return getArcLengthAzimuth(getSpiralRadius(),getLogarithmicK(),p0,p1,
                angle,isClockwise());
    }
    /**
     * 
     * @param a
     * @param k
     * @param p
     * @return 
     */
    protected double getRadiusImpl(double a, double k, double p){
        return a * Math.exp(p * k);
    }
    @Override
    protected double getRadiusImpl(double p){
        return getRadiusImpl(getSpiralRadius(),getLogarithmicK(),p);
    }
    /**
     * 
     * @param a
     * @param k
     * @param p
     * @param angle
     * @param clockwise
     * @return 
     */
    protected double getRadius(double a, double k, double p, double angle, 
            boolean clockwise){
        return getRadiusImpl(a,k,getAzimuthValue(p,angle,clockwise));
    }
    /**
     * 
     * @param k
     * @param r0
     * @param length
     * @return 
     */
    protected double getRadius(double k, double r0, double length){
        return (length/getSignPitchInverted(k))+r0;
    }
    /**
     * 
     * @param a
     * @param k
     * @param r
     * @return 
     */
    protected double getAzimuthImpl(double a, double k, double r){
        return (Math.log(r / a) / k);
    }
    @Override
    protected double getAzimuthImpl(double r){
        return getAzimuthImpl(getSpiralRadius(),getLogarithmicK(),r);
    }
    /**
     * 
     * @param a
     * @param k
     * @param r
     * @param angle
     * @param clockwise
     * @return 
     */
    protected double getAzimuth(double a, double k, double r, double angle, 
            boolean clockwise){
        return getAzimuthDegrees(getAzimuthImpl(a,k,r),angle,clockwise);
    }
    @Override
    protected double fillConditionValue() {
        return getBase();
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
            // If the fourth point has not been initialized yet
        if (point4 == null)
            point4 = new Point2D.Double();
        
            // This gets the amount by which to multiply the angle when 
            // computing the spiral.
        double k = getLogarithmicK();
            // This is the value by which to adjust the radius to get the other 
            // spiral that completes the shape
        double lim = Math.exp(k * (1-thickness));
            // This is the value to use to calculate the other spiral that 
            // completes the path. This is used for the outer spiral curve when 
            // clockwise and the inner spiral curve when counter-clockwise 
        double a = radius;
            // If the spiral is going clockwise
        if (clockwise)
            a /= lim;
        else
            a *= lim;
        
            // This gets the starting azimuth for the spiral. This uses the 
            // multiplier for the secondary curve and ignores whether the spiral 
            // is clockwise or not, treating it as if it was always clockwise.
        double p0 = getAzimuth(a,k,getStartRadius(g),angle,true);
            // Subtract the starting azimuth by itself mod 90 degrees to ensure 
            // it ends at a multiple of 90 degrees.
        p0 -= p0 % QUARTER_CIRCLE_DEGREES;
        
            // This gets the ending azimuth for the spiral. This uses the 
            // radius and ignores whether the spiral is clockwise or not, 
            // treating it as if it was always clockwise. This uses the target 
            // radius of half the diagonal length of the area
        double p1 = getAzimuth(radius, k, 
                Math.sqrt(width*width+height*height)/2.0, angle,true);
            // Effectively round it up to the nearest quarter angle
        p1 += (QUARTER_CIRCLE_DEGREES - (p1 % QUARTER_CIRCLE_DEGREES)) % QUARTER_CIRCLE_DEGREES;
        
            // Get the azimuth of the point on the spiral where the spiral 
            // radius lies. This ignores whether the spiral is clockwise or not, 
            // treating it as if it was always clockwise. 
        double pR = getAzimuth(radius, k, radius, angle,true);
            // Get the radius of the point on the spiral that is the 
            // interpolation angle away from the spiral radius. This ignores 
            // whether the spiral is clockwise or not, treating it as if it was 
            // always clockwise. 
        double m0 = getRadius(radius,k,pR+INTERPOLATION_ANGLE,angle,true);
            // Get 1/3rd of the arc length of the segment that starts at the 
            // spiral radius and spans the whole interpolation angle
        double m1 = getArcLengthRadius(radius,k,radius,m0) / 3.0;
            // Get the multiplier for the radius to use to calculate the radius 
            // for the points that are the interpolation angle away from each 
            // other
        double m2 = m0/radius;
            // Get the radius that is 1/3rd of the way on the segment of the spiral
        m0 = getRadius(k,radius,m1);
            // Get the radius that is 2/3rd of the way on the segment of the spiral
        m1 = getRadius(k,radius,m1*2);
            // Get the difference for the azimuth that gets the azimuth that is 
            // 1/3rd of the way on the segment of the spiral
        double n0 = getAzimuth(radius, k, m0, angle,true) - pR;
            // Get the difference for the azimuth that gets the azimuth that is 
            // 2/3rd of the way on the segment of the spiral
        double n1 = getAzimuth(radius, k, m1, angle,true) - pR;
            // Divide the two radiuses by the spiral radius to get the multipliers
        m0 /= radius;
        m1 /= radius;
        
            // If the spiral is going counter-clockwise
        if (!clockwise){
            p1 = -p1;
                // Not only swap the signs but offset this by 360
            p0 = -p0+FULL_CIRCLE_DEGREES;
                // Invert the values m0, m1, and m2
            m0 = 1/m0;
            m1 = 1/m1;
            m2 = 1/m2;
        }
        
        /* 
        At this point, for a given polar coordinate on the logarithmic spiral, 
        pt1=(r, p), the point pt4=(r*m2, p+INTERPOLATION_ANGLE) is the ending 
        point of the segment of the spiral. Additionally, the point 
        pt2=(r*m0, p+n0) is 1/3rd of the way between pt1 and pt4, and 
        pt3=(r*m1, p+n1) is 2/3rd of the way between pt1 and pt4. The values m0, 
        m1, and m2 will be used to calculate the radiuses for those points, and 
        the values n0, n1, and INTERPOLATION_ANGLE will be used to calculate the 
        azimuths for those points.
         */
        
            // Now it's time to create the paths for the spiral
        
            // Create the path for the first logarithmic spiral
        path = processLogSpiral(centerX, centerY, radius, k, p0, p1, angle,
                clockwise,m0,m1,m2,n0,n1,false,point1,point2,point3,point4,path);
            // Go in reverse and create the path for the second logarithmic 
            // spiral. This has an additional 360 degrees on the ending azimuth. 
            // This is the return path for the spiral that turns the spiral from 
            // a line to a shape.
        path = processLogSpiral(centerX, centerY, a, k, p0, p1+FULL_CIRCLE_DEGREES, 
                angle,clockwise,m0,m1,m2,n0,n1,true,point1,point2,point3,point4,path);
            // Close the path now
        path.closePath();
            // Fill the path
        g.fill(path);
    }
    /**
     * 
     * @param x The x-coordinate of the center of the area.
     * @param y The y-coordinate of the center of the area.
     * @param a
     * @param k
     * @param p0 The starting azimuth for the spiral.
     * @param p1 The ending azimuth for the spiral.
     * @param angle The angle of rotation.
     * @param clockwise {@code true} if the spiral is clockwise, {@code false} 
     * if the spiral is counter-clockwise.
     * @param m0 The difference between the radiuses of the start and the point 
     * 1/3rd of the way on a segment of the spiral.
     * @param m1 The difference between the radiuses of the start and the point 
     * 2/3rd of the way on a segment of the spiral.
     * @param m2 The difference between the radiuses of the start and the end of 
     * a segment of the spiral.
     * @param n0 The difference between the azimuths of the start and the point 
     * 1/3rd of the way on a segment of the spiral.
     * @param n1 The difference between the azimuths of the start and the point 
     * 2/3rd of the way on a segment of the spiral.
     * @param reverse
     * @param point1
     * @param point2
     * @param point3
     * @param point4
     * @param path
     * @return 
     */
    protected Path2D processLogSpiral(double x, double y, double a, double k, 
            double p0, double p1, double angle, boolean clockwise, 
            double m0, double m1, double m2,double n0,double n1,boolean reverse, 
            Point2D point1, Point2D point2, Point2D point3, Point2D point4, 
            Path2D path){
            // If the given path is null
        if (path == null)
            path = new Path2D.Double();
            // Get the starting radius for the spiral
        double r0 = getRadius(a,k,p0,angle,clockwise);
            // Get the ending radius for the spiral
        double r1 = getRadius(a,k,p1,angle,clockwise);
            // This gets the difference between the azimuths of the start and 
            // end points of a segment of a spiral
        double n2 = INTERPOLATION_ANGLE;
            // This gets the starting radius for the spiral. If this is going in 
            // reverse, then this is the maximum radius. Otherwise, this is the 
            // minimum radius
        double startR = (reverse)?r1:r0;
            // This gets the starting azimuth for the spiral. If this is going 
            // in reverse, then this is the maximum azimuth. Otherwise, this is 
            // the minimum azimuth
        double startP = (reverse)?p1:p0;
            // If the value of reverse is the same as the direction of the 
            // spiral (i.e. reverse the spiral and the spiral is clockwise or 
            // don't reverse the spiral and the spiral is counter-clockwise)
        if (reverse == clockwise){
                // Invert the differences to reverse the direction to go in
            m0 = 1/m0;
            m1 = 1/m1;
            m2 = 1/m2;
            n0 = -n0;
            n1 = -n1;
            n2 = -n2;
        }
            // Get the minimum azimuth
        double temp = p0;
            // Make sure the minimum azimuth is the smaller azimuth
        p0 = Math.min(p0, p1);
            // Make sure the maximum azimuth is the larger azimuth
        p1 = Math.max(temp, p1);
            // Calculate the point on the spiral for the start of the spiral
        point1 = GeometryMath.polarToCartesianDegrees(startR,startP,x,y,point1);
            // If the path is empty
        if (path.getCurrentPoint() == null)
                // Move the path to the starting point
            path.moveTo(point1.getX(), point1.getY());
        else    // Draw a line to the starting point
            path.lineTo(point1.getX(), point1.getY());
        
            // A for loop to go through the points on the spiral 
        for (double r = startR, p = startP; 
                    // Go up until it reaches the opposite azimuth extreme
                (p > p0 && p < p1) || p == startP; r*=m2, p+=n2){
                // Calculate the point on the spiral that is 1/3rd of the way on 
                // the current segment of the spiral
            point2 = GeometryMath.polarToCartesianDegrees(r*m0,p+n0,x,y,point2);
                // Calculate the point on the spiral that is 2/3rd of the way on 
                // the current segment of the spiral
            point3 = GeometryMath.polarToCartesianDegrees(r*m1,p+n1,x,y,point3);
                // Calculate the point on the spiral that is the end of the 
                // current segment of the spiral
            point4 = GeometryMath.polarToCartesianDegrees(r*m2,p+n2,x,y,point4);
                // Calculate the control points for the cubic bezier curve that 
                // passes through point1, point2, point3, and point4, and store 
                // the control points in point2 and point3
            GeometryMath.getCubicBezierControlPoints(point1, point2, point3, 
                    point4, point2, point3);
                // Draw a cubic bezier curve to point4, using point2 and point3 
                // as the control points
            path.curveTo(point2.getX(), point2.getY(), 
                    point3.getX(), point3.getY(), 
                    point4.getX(),point4.getY());
                // Move point4 into point1 since it's the start of the next 
                // segment
            point1.setLocation(point4);
        }
        return path;
    }
    @Override
    protected String paramString(){
        return super.paramString()+
                ",base="+getBase();
    }
    @Override
    public String getName() {
        return "Logarithmic";
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
