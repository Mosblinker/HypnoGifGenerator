/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import geom.*;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.nio.ByteBuffer;
import java.util.Objects;
import spiral.SpiralGeneratorConfig;
import swing.ListenedPainter;

/**
 *
 * @author Mosblinker
 */
public abstract class SpiralPainter extends ListenedPainter<Double> implements 
        GeometryMathConstants{
    
    public static final String CLOCKWISE_PROPERTY_CHANGED = 
            "ClockwisePropertyChanged";
    /**
     * This stores whether this spiral is clockwise or counter-clockwise.
     */
    private boolean clockwise = true;
    /**
     * Implicit constructor.
     */
    protected SpiralPainter(){}
    /**
     * Copy constructor for the class.
     * @param painter The SpiralPainter to copy.
     */
    protected SpiralPainter(SpiralPainter painter){
        this.clockwise = painter.clockwise;
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
                width/2.0,height/2.0,isClockwise());
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
     * @see #paint
     * @see #isClockwise() 
     */
    protected abstract void paintSpiral(Graphics2D g, double angle, 
            int width, int height, double centerX, double centerY, 
            boolean clockwise);
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
     * @param config 
     */
    public void loadSpiralFromPreferences(SpiralGeneratorConfig config){
        setClockwise(config.isSpiralClockwise(this, isClockwise()));
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
        int length = getByteArrayLength()+1;
        if (arr == null || arr.length < offset+length){
            byte[] temp = arr;
            arr = new byte[offset+length];
            if (temp != null && temp.length > 0)
                System.arraycopy(temp, 0, arr, 0, Math.min(temp.length,offset));
        }
        arr[offset] = (byte)((isClockwise()) ? 0x01 : 0x00);
        if (length > 1){
            ByteBuffer buffer = ByteBuffer.wrap(arr, offset+1, length-1);
            toByteArray(buffer);
        }
        return arr;
    }
    /**
     * 
     * @param buffer 
     */
    protected void toByteArray(ByteBuffer buffer){
        
    }
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
        int length = getByteArrayLength();
        if (arr == null || (arr.length - offset) < length + 1)
            return;
        setClockwise((arr[offset] & 0x01) != 0);
        if (length > 0){
            ByteBuffer buffer = ByteBuffer.wrap(arr, offset+1, length)
                    .asReadOnlyBuffer();
            fromByteArray(buffer);
        }
    }
    /**
     * 
     * @param buffer 
     */
    protected void fromByteArray(ByteBuffer buffer){
        
    }
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
    }
    @Override
    protected String paramString(){
            // If the spiral is counter-clockwise, say so
        return ((isClockwise())?"":"counter-")+"clockwise";
    }
}
