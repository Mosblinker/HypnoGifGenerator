/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral;

import geom.GeometryMath;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.*;
import java.util.Objects;
import swing.ListenedPainter;

/**
 *
 * @author Mosblinker
 */
public class LogarithmicSpiralPainter extends ListenedPainter<Double>{
    /**
     * This is the maximum angle for a circle. In other words, 360 degrees.
     */
    public static final double MAXIMUM_ANGLE = 360.0;
    /**
     * This is is the angle for quarter of a circle. In other words, 90 degrees.
     */
    public static final double QUARTER_ANGLE = 90.0;
    /**
     * This is the angle to use for interpolating the spiral curve. The end of 
     * each segment is {@value INTERPOLATION_ANGLE} degrees away from the start 
     * of the curve.
     */
    protected static final double INTERPOLATION_ANGLE = 45.0;
    /**
     * This is the target radius for the start of the spiral. This is set to 0.1 
     * to ensure that the spiral is properly formed in the center of the image 
     * with a pitch that makes it appear that it infinitely gets smaller as it 
     * gets closer to the center of the image.
     */
    private static final double STARTING_RADIUS = 0.1;
    
    public static final String RADIUS_PROPERTY_CHANGED ="RadiusPropertyChanged";
    
    public static final String BASE_PROPERTY_CHANGED ="BasePropertyChanged";
    
    public static final String BALANCE_PROPERTY_CHANGED ="BalancePropertyChanged";
    
    public static final String CLOCKWISE_PROPERTY_CHANGED ="ClockwisePropertyChanged";
    /**
     * This method bounds the given angle to be within the range of 0 and 
     * {@value MAXIMUM_ANGLE}, exclusive. If the given angle is negative, then 
     * it will loop back to being positive.
     * @param p The angle to bound.
     * @return The angle, limited to a range of 0 and {@value MAXIMUM_ANGLE}, 
     * exclusive.
     * @see MAXIMUM_ANGLE
     */
    public static double boundAngle(double p){
        return ((p%MAXIMUM_ANGLE)+MAXIMUM_ANGLE)%MAXIMUM_ANGLE;
    }
    /**
     * This returns a {@code Point2D} object with the given point on a polar 
     * coordinate system converted to Cartesian coordinates with the origin at 
     * ({@code x}, {@code y}).
     * @param r The radius of the point on the polar coordinate system.
     * @param p The azimuth of the point on the polar coordinate system, in 
     * degrees.
     * @param x The x-coordinate of the origin.
     * @param y The y-coordinate of the origin.
     * @param point A {@code Point2D} object to reuse to store the resulting 
     * coordinate, or null.
     * @return A {@code Point2D} object with the given polar coordinate 
     * converted into Cartesian coordinates.
     */
    public Point2D polarToCartesianCoords(double r, double p,double x,double y, 
            Point2D point){
            // If the given point is null
        if (point == null)
            point = new Point2D.Double();
            // Get the bounded azimuth in radians
        double theta = Math.toRadians(boundAngle(p));
            // Add the radius multiplied by the cosine of the azimuth to the 
            // x-coordinate of the origin, and add the radius multiplied by the 
            // sine of the azimuth to the y-coordinate of the origin
        point.setLocation(x + r*Math.cos(theta), y + r*Math.sin(theta));
        return point;
    }
    /**
     * This returns a {@code Point2D} object with the given point on a polar 
     * coordinate system converted to Cartesian coordinates with the origin at 
     * ({@code 0}, {@code 0}).
     * @param r The radius of the point on the polar coordinate system.
     * @param p The azimuth of the point on the polar coordinate system, in 
     * degrees.
     * @param point A {@code Point2D} object to reuse to store the resulting 
     * coordinate, or null.
     * @return A {@code Point2D} object with the given polar coordinate 
     * converted into Cartesian coordinates.
     */
    public Point2D polarToCartesianCoords(double r, double p, Point2D point){
        return polarToCartesianCoords(r,p,0,0,point);
    }
    
    
    
    /**
     * This is the radius used to control the logarithmic spiral. This will be 
     * the radius for the point on the logarithmic spiral that is 90 degrees 
     * ahead of the point at 0 degrees, not factoring in balance or angle of 
     * rotation.
     */
    private double radius = 100.0;
    /**
     * 
     */
    private double base = 2.0;
    
    private double balance = 0.0;
    
    private boolean clockwise = true;
    
    private Path2D path = null;
    
    private Rectangle2D rect = null;
    
    private Point2D point1 = null;
    
    private Point2D point2 = null;
    
    private Point2D point3 = null;
    
    private Point2D point4 = null;
    
    public void setRadius(double radius){
        if (radius < 1)
            throw new IllegalArgumentException();
        if (this.radius != radius){
            double old = this.radius;
            this.radius = radius;
            firePropertyChange(RADIUS_PROPERTY_CHANGED,old,radius);
        }
    }
    
    public double getRadius(){
        return radius;
    }
    
    public void setBase(double base){
        if (base < 1)
            throw new IllegalArgumentException();
        if (this.base != base){
            double old = this.base;
            this.base = base;
            firePropertyChange(BASE_PROPERTY_CHANGED,old,base);
        }
    }
    
    public double getBase(){
        return base;
    }
    
    public void setBalance(double b){
        if (b < -1 || b > 1)
            throw new IllegalArgumentException();
        if (this.balance != b){
            double old = this.balance;
            this.balance = b;
            firePropertyChange(BALANCE_PROPERTY_CHANGED,old,balance);
        }
    }
    
    public double getBalance(){
        return balance;
    }
    
    public void setClockwise(boolean value){
        if (this.clockwise != value){
            this.clockwise = value;
            firePropertyChange(CLOCKWISE_PROPERTY_CHANGED,!clockwise,clockwise);
        }
    }
    
    public boolean isClockwise(){
        return clockwise;
    }
    /**
     * This returns the k value for a logarithmic spiral using the equation 
     * {@code r=a*e^(k*p)}, with {@code r} being the radius, {@code p} being the 
     * azimuth, and {@code a} being some constant.
     * @return The value multiplied with the azimuth before it is used as the 
     * exponent for Eular's number.
     * @see #getBase() 
     */
    protected double getLogarithmicK(){
        return Math.log(getBase());
    }

    @Override
    public void paint(Graphics2D g, Double angle, int width, int height) {
            // Check if the graphics context is null
        Objects.requireNonNull(g);
            // If either the width or height are less than or equal to zero 
            // (nothing would be rendered anyway)
        if (width <= 0 || height <= 0)
            return;
            // Create a copy of the given graphics context and configure it to 
            // render the spiral
        g = configureGraphics((Graphics2D)g.create());
        
        if (balance <= -1 || balance >= 1 || base == 1){
            if (balance <= -1){
                g.dispose();
                return;
            }
            if (rect == null)
                rect = new Rectangle2D.Double();
            rect.setFrame(0, 0, width, height);
            if (base == 1){
                Color c = g.getColor();
                g.setColor(new Color((c.getRGB() & 0x00FFFFFF) | 
                        (Math.floorDiv(c.getAlpha(),2) << 24), true));
            }
            g.fill(rect);
            g.dispose();
            return;
        }
        
        g.clipRect(0, 0, width, height);
        
        if (path == null)
            path = new Path2D.Double();
        else
            path.reset();
        
        if (point1 == null)
            point1 = new Point2D.Double();
        if (point2 == null)
            point2 = new Point2D.Double();
        if (point3 == null)
            point3 = new Point2D.Double();
        if (point4 == null)
            point4 = new Point2D.Double();
        
            // This gets the x-coordinate for the center of the area
        double centerX = width / 2.0;
            // This gets the y-coordinate for the center of the area
        double centerY = height / 2.0;
        
            // This gets the thickness of the spiral
        double thickness = (1.0 + balance) / 2.0;
            // This gets the amount by which to multiply the angle when 
            // computing the logarithmic spiral. This is equal to the tangent 
            // of the pitch of the spiral.
        double k = Math.log(base);
            // This is the value by which to adjust the radius to get the other 
            // spiral that completes the shape
        double lim = Math.exp(k * (1-thickness));
            // This is the value to use to calculate the other logarithmic 
            // spiral that completes the path. This is used for the outer spiral 
            // curve when clockwise and the inner spiral curve when 
            // counter-clockwise 
        double a = radius;
            // When the spiral is going clockwise
        if (clockwise)
            a /= lim;
        else
            a *= lim;
        
            // Bound the given angle, defaulting to an angle of 0.0 if there 
            // isn't one
        angle = boundAngle((angle!=null)?angle:0.0);
            // If the spiral is going clockwise and the angle is not zero
        if (clockwise && angle > 0)
            angle = MAXIMUM_ANGLE - angle;
            // Alter the angle based off the thickness of the spiral
        angle = (angle + (thickness / 2.0)*MAXIMUM_ANGLE) % MAXIMUM_ANGLE;
        
            // This gets the amount by which to scale the starting radius for 
            // the spiral
        double rScale = 1.0;
            // Get the transform for the graphics
        AffineTransform tx = g.getTransform();
            // If there is a non-null transform applied to the graphics
        if (tx != null){
                // Get the larger of the scale factors applied to the graphics, 
                // or 1 if both scale factors are less than 1
            rScale = Math.max(Math.max(tx.getScaleX(), tx.getScaleY()),1.0);
        }
        
            // This gets the smallest azimuth for the spiral. This uses the 
            // multiplier for the secondary curve and ignores whether the spiral 
            // is clockwise or not, treating it as if it was always clockwise.
            // This divides the starting radius by the scale in order to ensure 
            // that the spiral appears as if it is infinitely getting smaller 
            // the closer it gets to the center
        double p0 = getLogSpiralAzimuth(a,k,STARTING_RADIUS/rScale,angle,true);
            // Subtract the smallest azimuth by it mod 90 degrees to ensure it 
            // ends at a multiple of 90 degrees.
        p0 -= p0 % QUARTER_ANGLE;
            
        double p1 = getLogSpiralAzimuth(radius, k, 
                Math.sqrt(width*width+height*height)/2.0, angle,true);
        p1 += (QUARTER_ANGLE - (p1 % QUARTER_ANGLE)) % QUARTER_ANGLE;
        
        double pR = getLogSpiralAzimuth(radius, k, radius, angle,true);
        double m0 = getLogSpiralRadius(radius,k,pR+INTERPOLATION_ANGLE,angle,true);
        double m1 = getLogSpiralArcLength(radius,k,radius,m0) / 3.0;
        double m2 = m0/radius;
        
        m0 = getLogSpiralRadius(k,radius,m1);
        m1 = getLogSpiralRadius(k,radius,m1*2);
        
        double n0 = getLogSpiralAzimuth(radius, k, m0, angle,true) - pR;
        double n1 = getLogSpiralAzimuth(radius, k, m1, angle,true) - pR;
        
        m0 /= radius;
        m1 /= radius;
        
        if (!clockwise){
            p1 = -p1;
            p0 = -p0+MAXIMUM_ANGLE;//180;
            m0 = 1/m0;
            m1 = 1/m1;
            m2 = 1/m2;
        }
        
        path = processLogSpiral(centerX, centerY, radius, k, p0, p1, angle,
                clockwise,m0,m1,m2,n0,n1,false,point1,point2,point3,point4,path);
        
        path = processLogSpiral(centerX, centerY, a, k, p0, p1+MAXIMUM_ANGLE, 
                angle,clockwise,m0,m1,m2,n0,n1,true,point1,point2,point3,point4,path);
        
        path.closePath();
        
        g.fill(path);
        
        g.dispose();
    }
    /**
     * This is used to configure the graphics context used to render the spiral. 
     * It's assumed that the returned graphics context is the same as the given 
     * graphics context, or at least that the returned graphics context 
     * references the given graphics context in some way. 
     * @param g The graphics context to render to.
     * @return The given graphics context, now configured for rendering the 
     * spiral.
     * @see #paint 
     */
    protected Graphics2D configureGraphics(Graphics2D g){
            // Enable antialiasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
            // Prioritize rendering quality over speed
        g.setRenderingHint(RenderingHints.KEY_RENDERING, 
                RenderingHints.VALUE_RENDER_QUALITY);
            // Prioritize quality over speed for alpha interpolation
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, 
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            // Enable dithering
        g.setRenderingHint(RenderingHints.KEY_DITHERING, 
                RenderingHints.VALUE_DITHER_ENABLE);
            // Prioritize color rendering quality over speed
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, 
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
            // Set the stroke normalization to be pure, i.e. geometry should be 
            // left unmodified
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, 
                RenderingHints.VALUE_STROKE_PURE);
        return g;
    }
    
    
    
    protected double getLogSpiralPitchRad(double k){
        return Math.atan(k);
    }
    
    protected double getLogSpiralPitch(double k){
        return Math.toDegrees(getLogSpiralPitchRad(k));
    }
    
    protected double getLogSpiralCurvature(double r, double k){
        return Math.cos(getLogSpiralPitchRad(k)) / r;
    }
    
    private double getLogSpiralSignPitchInv(double k){
        return Math.sqrt((k*k)+1)/k;
    }
    
    protected double getLogSpiralArcLength(double a, double k, double r1, double r2){
        return getLogSpiralSignPitchInv(k)* Math.abs(r2-r1);
    }
    
    protected double getLogSpiralArcLength(double a, double k, double p1, 
            double p2, double angle, boolean clockwise){
        return getLogSpiralArcLength(a,k,getLogSpiralRadius(a,k,p1,angle,clockwise),
                getLogSpiralRadius(a,k,p2,angle,clockwise));
    }
    
    protected double getLogSpiralRadius(double a, double k, double p, 
            double angle, boolean clockwise){
        p -= angle;
        if (!clockwise)
            p = -p;
        p /= MAXIMUM_ANGLE;
        return a * Math.exp(p * k);
    }
    
    protected double getLogSpiralRadius(double k, double r0, double l){
        return (l/getLogSpiralSignPitchInv(k))+r0;
    }
    
    protected double getLogSpiralAzimuth(double a, double k, double r, 
            double angle, boolean clockwise){
        double p = (Math.log(r / a) / k) * MAXIMUM_ANGLE;
        if (!clockwise)
            p = -p;
        p += angle;
        return p;
    }
    
    protected Point2D getLogSpiralDerPoint(double x, double y, double a, 
            double k, double p, double angle, boolean clockwise, Point2D point){
        if (point == null)
            point = new Point2D.Double();
        double r = getLogSpiralRadius(a, k, p, angle, clockwise);
        double theta = Math.toRadians(boundAngle(p));
        double cos = r*Math.cos(theta);
        double sin = r*Math.sin(theta);
        point.setLocation(x+(cos*k-sin), y+(sin*k+cos));
        return point;
    }
    
    private Path2D processLogSpiral(double x, double y, double a, double k, 
            double p0, double p1, double angle, boolean clockwise, 
            double m0, double m1, double m2, double n0, double n1, boolean reverse, 
            Point2D point1, Point2D point2, Point2D point3, Point2D point4, Path2D path){
        if (path == null)
            path = new Path2D.Double();
        
        double r0 = getLogSpiralRadius(a,k,p0,angle,clockwise);
        double r1 = getLogSpiralRadius(a,k,p1,angle,clockwise);
        
        double n2 = INTERPOLATION_ANGLE;
        
        double startR = (reverse)?r1:r0;
        double startP = (reverse)?p1:p0;
        
        if (reverse == clockwise){
            m0 = 1/m0;
            m1 = 1/m1;
            m2 = 1/m2;
            n0 = -n0;
            n1 = -n1;
            n2 = -n2;
        }
        
        double temp = p0;
        p0 = Math.min(p0, p1);
        p1 = Math.max(temp, p1);
        
        point1 = polarToCartesianCoords(startR,startP,x,y,point1);
        if (path.getCurrentPoint() == null)
            path.moveTo(point1.getX(), point1.getY());
        else
            path.lineTo(point1.getX(), point1.getY());
        
        for (double r = startR, p = startP; (p > p0 && p < p1) || p == startP; r*=m2, p+=n2){
            point2 = polarToCartesianCoords(r*m0,p+n0,x,y,point2);
            point3 = polarToCartesianCoords(r*m1,p+n1,x,y,point3);
            point4 = polarToCartesianCoords(r*m2,p+n2,x,y,point4);
            GeometryMath.getCubicBezierControlPoints(point1, point2, point3, 
                    point4, point2, point3);
            path.curveTo(point2.getX(), point2.getY(), 
                    point3.getX(), point3.getY(), 
                    point4.getX(),point4.getY());
            point1.setLocation(point4);
        }
        return path;
    }
    
}
