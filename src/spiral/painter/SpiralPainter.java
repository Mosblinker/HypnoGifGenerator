/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import geom.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.nio.ByteBuffer;
import java.util.Objects;
import swing.ListenedPainter;

/**
 *
 * @author Mosblinker
 */
public abstract class SpiralPainter extends ListenedPainter<Double> implements 
        GeometryMathConstants, Cloneable{
    
    public static final String SPIRAL_RADIUS_PROPERTY_CHANGED = 
            "SpiralRadiusPropertyChanged";
    
    public static final String THICKNESS_PROPERTY_CHANGED = 
            "ThicknessPropertyChanged";
    
    public static final String CLOCKWISE_PROPERTY_CHANGED = 
            "ClockwisePropertyChanged";
    
    private static final int BYTE_ARRAY_LENGTH = Double.BYTES*2 + 1;
    /**
     * This is the angle typically used for interpolating the spiral curve. The
     * end of each segment is {@value INTERPOLATION_ANGLE} degrees away from the 
     * start of the curve.
     */
    protected static final double INTERPOLATION_ANGLE = 45.0;
    /**
     * This stores whether this spiral is clockwise or counter-clockwise.
     */
    private boolean clockwise = true;
    /**
     * This is the spiral radius that controls the size of the spirals.
     */
    private double radius;
    /**
     * This is the thickness of the spiral.
     */
    private double thickness;
    /**
     * This is a scratch Rectangle2D object typically used to fill the painted 
     * area when the entire area is to be filled. This is initially null and is 
     * initialized the first time it is used. This scratch object may change at 
     * any time during the rendering process, and should not be assumed to be in 
     * a known state before being used.
     */
    protected Rectangle2D rect = null;
    /**
     * Implicit constructor.
     */
    protected SpiralPainter(){
        radius = SpiralPainter.this.getDefaultRadius();
        thickness = SpiralPainter.this.getDefaultThickness();
    }
    /**
     * Copy constructor for the class.
     * @param painter The SpiralPainter to copy.
     */
    protected SpiralPainter(SpiralPainter painter){
        this.clockwise = painter.clockwise;
        this.radius = painter.radius;
        this.thickness = painter.thickness;
    }
    /**
     * 
     * @return 
     */
    protected double getDefaultRadius(){
        return 100.0;
    }
    /**
     * 
     * @return 
     */
    protected double getDefaultThickness(){
        return 0.5;
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
     * This sets whether this spiral is clockwise or counter-clockwise.
     * @param value {@code true} if this spiral is clockwise, {@code false} if 
     * this spiral is counter-clockwise.
     * @see #isClockwise() 
     */
    public void setClockwise(boolean value){
            // If the clockwise value would change
        if (this.clockwise != value){
            this.clockwise = value;
            firePropertyChange(CLOCKWISE_PROPERTY_CHANGED,!clockwise,clockwise);
        }
    }
    /**
     * This returns whether this spiral is clockwise or counter-clockwise.
     * @return {@code true} if this spiral is clockwise, {@code false} if 
     * this spiral is counter-clockwise.
     */
    public boolean isClockwise(){
        return clockwise;
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
            // Clip the graphics context to only include the rendered area
        g.clipRect(0, 0, width, height);
            // Paint the spiral. If the angle is null, then default to 0. 
            // Otherwise, keep in in range of (-360, 360), exclusive.
        paintSpiral(g,(angle!=null)?(angle%FULL_CIRCLE_DEGREES):0.0,width,height,
                width/2.0,height/2.0,isClockwise(),getSpiralRadius(),getThickness());
        g.dispose();
    }
    /**
     * This is used to paint the spiral. This is given a copy of the graphics 
     * context that is clipped to the painted region.
     * @param g The graphics context to render to.
     * @param angle The angle for the spiral. This is in the range of -{@value 
     * MAXIMUM_ANGLE}, exclusive, to {@value MAXIMUM_ANGLE}, exclusive.
     * @param width This is the width of the area to fill with the spiral.
     * @param height This is the height of the area to fill with the spiral.
     * @param centerX This is the x-coordinate of the center of the area.
     * @param centerY This is the y-coordinate of the center of the area.
     * @param clockwise {@code true} if the spiral is clockwise, {@code false} 
     * if the spiral is counter-clockwise.
     * @param radius The spiral radius which controls the size of the spirals.
     * @param thickness The thickness of the spiral.
     * @see #paint
     * @see #isClockwise() 
     */
    protected abstract void paintSpiral(Graphics2D g, double angle, 
            int width, int height, double centerX, double centerY, 
            boolean clockwise, double radius, double thickness);
    /**
     * 
     * @param color
     * @param alpha
     * @return 
     */
    protected Color getTranslucentColor(Color color, double alpha){
            // If the alpha is greater than or equal to 1
        if (alpha >= 1.0)
            return color;
            // Get the RGB value of the color without the alpha component
        int rgb = color.getRGB() & 0x00FFFFFF;
            // If the alpha is greater than zero
        if (alpha > 0.0)
                // Multiply the color's alpha component by the alpha and shift 
                // it into the last 8 bits to use the result as the alpha 
                // component
            rgb |= ((int)Math.floor(color.getAlpha()*alpha)) << 24;
        return new Color(rgb, true);
    }
    /**
     * 
     * @param g
     * @param width
     * @param height
     * @param thickness 
     * @param color
     */
    protected void fillWithTransparency(Graphics2D g, double width, 
            double height, double thickness, Color color){
            // If the thickness is greater than zero
        if (thickness > 0.0){
                // Set the color to use
            g.setColor(getTranslucentColor(color,thickness));
                // If the rectangle object has not been initialized yet
            if (rect == null)
                rect = new Rectangle2D.Double();
                // Set the frame of the rectangle to cover the entire area
            rect.setFrame(0, 0, width, height);
                // Fill the area
            g.fill(rect);
        }
    }
    /**
     * 
     * @param g
     * @param width
     * @param height
     * @param thickness 
     */
    protected void fillWithTransparency(Graphics2D g, double width, 
            double height, double thickness){
        fillWithTransparency(g,width,height,thickness,g.getColor());
    }
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
    /**
     * 
     * @param angle
     * @param thickness
     * @param clockwise
     * @return 
     */
    protected double unadjustRotation(double angle, double thickness, 
            boolean clockwise){
            // Bound the angle of rotation
        angle = GeometryMath.boundDegrees(angle - (thickness / 2.0)*FULL_CIRCLE_DEGREES);
            // If the spiral is going clockwise and the angle is not 0
        if (clockwise && angle > 0.0)
            angle = FULL_CIRCLE_DEGREES - angle;
        return angle;
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
    /**
     * 
     * @return 
     */
    public String getName(){
        return getClass().getSimpleName();
    }
    /**
     * 
     * @return 
     */
    public String getPreferenceKey(){
        String name = getClass().getSimpleName();
        if (name.endsWith("Painter"))
            return name.substring(0, name.length()-7);
        return name;
    }
    /**
     * 
     * @return 
     */
    protected int getByteArrayLength(){
        return 0;
    }
    /**
     * 
     * @param arr
     * @param offset
     * @return 
     */
    public byte[] toByteArray(byte[] arr, int offset){
        int length = getByteArrayLength()+BYTE_ARRAY_LENGTH;
        if (arr == null || arr.length < offset+length){
            byte[] temp = arr;
            arr = new byte[offset+length];
            if (temp != null && temp.length > 0)
                System.arraycopy(temp, 0, arr, 0, Math.min(temp.length,offset));
        }
        arr[offset] = (byte)((isClockwise()) ? 0x01 : 0x00);
        ByteBuffer buffer = ByteBuffer.wrap(arr, offset+1, length-1);
        buffer.putDouble(getSpiralRadius());
        buffer.putDouble(getThickness());
        if (length > BYTE_ARRAY_LENGTH)
            toByteArray(buffer.slice());
        return arr;
    }
    /**
     * 
     * @param buffer 
     */
    protected void toByteArray(ByteBuffer buffer){ }
    /**
     * 
     * @return 
     */
    public byte[] toByteArray(){
        return toByteArray(null,0);
    }
    /**
     * 
     * @param arr
     * @param offset 
     */
    public void fromByteArray(byte[] arr, int offset){
        int length = getByteArrayLength()+BYTE_ARRAY_LENGTH;
        if (arr == null || (arr.length - offset) < length)
            return;
        setClockwise((arr[offset] & 0x01) != 0);
        ByteBuffer buffer = ByteBuffer.wrap(arr, offset+1, length-1)
                .asReadOnlyBuffer();
        setSpiralRadius(buffer.getDouble());
        setThickness(buffer.getDouble());
        if (length > BYTE_ARRAY_LENGTH)
            fromByteArray(buffer.slice());
    }
    /**
     * 
     * @param buffer 
     */
    protected void fromByteArray(ByteBuffer buffer){ }
    /**
     * 
     * @param arr 
     */
    public void fromByteArray(byte[] arr){
        fromByteArray(arr,0);
    }
    /**
     * 
     */
    public void reset(){
        setClockwise(true);
        setSpiralRadius(getDefaultRadius());
        setThickness(getDefaultThickness());
    }
    @Override
    protected String paramString(){
            // If the spiral is counter-clockwise, say so
        return ((isClockwise())?"":"counter-")+"clockwise"+
                ",spiralRadius="+getSpiralRadius()+
                ",thickness="+getThickness();
    }
    /**
     * 
     * @return 
     */
    @Override
    public abstract SpiralPainter clone();
}
