/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral;

import icons.Icon2D;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import spiral.painter.SpiralPainter;

/**
 *
 * @author Mosblinker
 */
public class SpiralProgramIcon implements Icon2D{
    /**
     * The width and height for this icon.
     */
    private int size;
    /**
     * The painter for this icon.
     */
    private SpiralPainter painter;
    /**
     * The mask for the overlay for this icon.
     */
    private BufferedImage mask;
    /**
     * The model for the main part of the spiral.
     */
    private SpiralModel model;
    /**
     * The model for the spiral overlay.
     */
    private SpiralModel maskModel;
    /**
     * This is the image drawn by this icon. This will be null until it's 
     * used for the first time.
     */
    private BufferedImage img = null;
    /**
     * 
     * @param size
     * @param painter
     * @param mask
     * @param model
     * @param maskModel 
     */
    public SpiralProgramIcon(int size, SpiralPainter painter, BufferedImage mask, 
            SpiralModel model, SpiralModel maskModel){
        this.size = size;
        this.painter = painter;
        this.mask = mask;
        this.model = model;
        this.maskModel = maskModel;
    }
    /**
     * 
     * @param size
     * @param icon 
     */
    public SpiralProgramIcon(int size, SpiralProgramIcon icon){
        this(size,icon.painter,icon.mask,icon.model,icon.maskModel);
    }
    @Override
    public void paintIcon2D(Component c, Graphics2D g, int x, int y) {
            // Get the icon's width
        int width = getIconWidth();
            // Get the icon's height
        int height = getIconHeight();
            // Get any scaling transform that's been applied to the graphics 
            // context
        AffineTransform tx = g.getTransform();
            // Prioritize rendering quality over speed
        g.setRenderingHint(RenderingHints.KEY_RENDERING, 
                RenderingHints.VALUE_RENDER_QUALITY);
            // Prioritize quality over speed for alpha interpolation
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, 
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
            // The width for the image
        int w = (int) Math.ceil(width*tx.getScaleX());
            // The height for the image
        int h = (int) Math.ceil(height*tx.getScaleY());
            // If the image is null or the width or height doesn't match
        if (img == null || img.getWidth() != w || img.getHeight() != h)
                // Generate the image for the program icon
            img = SpiralGenerator.getProgramIcon(w,h,model,maskModel,painter,mask);
            // Draw the program icon to the graphics context
        g.drawImage(img, x, y, width, height, c);
    }
    @Override
    public int getIconWidth() {
        return size;
    }
    @Override
    public int getIconHeight() {
        return getIconWidth();
    }
    /**
     * This returns the painter for this icon.
     * @return The painter for this icon.
     */
    public SpiralPainter getSpiralPainter(){
        return painter;
    }
    /**
     * The model for the main part of the spiral.
     * @return 
     */
    public SpiralModel getSpiralModel(){
        return model;
    }
    /**
     * The mask for the overlay for this icon.
     * @return 
     */
    public BufferedImage getMask(){
        return mask;
    }
    /**
     * The model for the spiral overlay.
     * @return 
     */
    public SpiralModel getMaskModel(){
        return maskModel;
    }
}
