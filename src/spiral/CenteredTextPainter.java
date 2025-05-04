/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Objects;
import swing.ListenedPainter;

/**
 *
 * @author Mosblinker
 */
public class CenteredTextPainter extends ListenedPainter<String>{
    
    public static final String LINE_SPACING_PROPERTY_CHANGED = 
            "LineSpacingPropertyChanged";
    
    public static final String ANTIALIASING_PROPERTY_CHANGED = 
            "AntialiasingPropertyChanged";
    
    private double lineSpacing = 0.0;
    
    private boolean antialiasing = true;
    
    public double getLineSpacing(){
        return lineSpacing;
    }
    
    public CenteredTextPainter setLineSpacing(double value){
        if (value != lineSpacing){
            double old = lineSpacing;
            lineSpacing = value;
            firePropertyChange(LINE_SPACING_PROPERTY_CHANGED,old,lineSpacing);
        }
        return this;
    }
    
    public boolean isAntialiasingEnabled(){
        return antialiasing;
    }
    
    public CenteredTextPainter setAntialiasingEnabled(boolean value){
        if (value != antialiasing){
            antialiasing = value;
            firePropertyChange(ANTIALIASING_PROPERTY_CHANGED,value);
        }
        return this;
    }

    @Override
    public void paint(Graphics2D g, String text, int width, int height) {
            // Check if the graphics context is null
        Objects.requireNonNull(g);
            // If either the width or height are less than or equal to zero 
            // (nothing would be rendered anyway)
        if (width <= 0 || height <= 0 || text.isBlank())
            return;
        g = configureGraphics((Graphics2D) g.create());
        g.clipRect(0, 0, width, height);
        
//        g.drawLine(0, 0, width, height);
//        g.drawLine(width, 0, 0, height);
        
        FontMetrics metrics = g.getFontMetrics();
        ArrayList<String> lines = new ArrayList<>();
        ArrayList<Rectangle2D> lineBounds = new ArrayList<>();
        double textHeight = 0;
        double centerX = width/2.0;
        double spacing = lineSpacing * (g.getFont().getSize2D()/10.0);
        for (Object temp : text.lines().toArray()){
            String t = temp.toString();
            lines.add(t);
            Rectangle2D bounds = metrics.getStringBounds(t, g);
            lineBounds.add(bounds);
            textHeight += bounds.getHeight() + spacing;
            bounds.setFrameFromCenter(centerX, bounds.getCenterY(), (width-bounds.getWidth())/2.0, bounds.getY());
//            GeometryMath.printShape(t, bounds);
        }
        textHeight -= spacing;
        
        double y = (height - textHeight) / 2.0;
        for (int i = 0; i < lines.size(); i++){
            Rectangle2D bounds = lineBounds.get(i);
            g.drawString(lines.get(i), (float)bounds.getX(), (float)(y-bounds.getY()));
            y += bounds.getHeight() + spacing;
        }
        
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
            // Enable/Disable antialiasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                (antialiasing) ? RenderingHints.VALUE_ANTIALIAS_ON : 
                        RenderingHints.VALUE_ANTIALIAS_OFF);
            // Enable/Disable antialiasing
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, 
                (antialiasing) ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : 
                        RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
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
}
