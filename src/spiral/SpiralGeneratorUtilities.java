/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Mosblinker
 */
public final class SpiralGeneratorUtilities {
    
    public static final Color TRANSPARENT_COLOR = new Color(0x00000000, true);
    /**
     * 
     */
    private static final double[][] HEART_CONTROL_POINTS = {
        {0.15, 1.468, 0.328, 0.781, -0.295},
        { 1.0, 0.219, -0.295, -0.468, 0.328}
    };
    /**
     * This class cannot be constructed.
     */
    private SpiralGeneratorUtilities() {}
    /**
     * 
     */
    private static final Logger logger = Logger.getLogger("HypnoGifGenerator");
    /**
     * 
     * @return 
     */
    public static Logger getLogger(){
        return logger;
    }
    /**
     * 
     * @param level
     * @param sourceClass
     * @param method
     * @param msg
     */
    public static void log(Level level, Class sourceClass, String method, 
            String msg){
        getLogger().logp(level, sourceClass.getName(), method, msg);
    }
    /**
     * 
     * @param level
     * @param sourceClass
     * @param method
     * @param msg
     * @param thrown
     */
    public static void log(Level level, Class sourceClass, String method, 
            String msg, Throwable thrown){
        getLogger().logp(level, sourceClass.getName(), method, msg, thrown);
    }
    /**
     * 
     * @param level
     * @param sourceClass
     * @param method
     * @param msg
     * @param param1
     */
    public static void log(Level level, Class sourceClass, String method, 
            String msg, Object param1){
        getLogger().logp(level, sourceClass.getName(), method, msg, param1);
    }
    /**
     * 
     * @param level
     * @param sourceClass
     * @param method
     * @param msg
     * @param params
     */
    public static void log(Level level, Class sourceClass, String method, 
            String msg, Object[] params){
        getLogger().logp(level, sourceClass.getName(), method, msg, params);
    }
    /**
     * 
     * @param sourceClass
     * @param method
     * @param thrown 
     */
    public static void logThrown(Class sourceClass, String method, 
            Throwable thrown){
        getLogger().throwing(sourceClass.getName(), method, thrown);
    }
    /**
     * 
     * @param path
     * @return 
     * @throws java.io.IOException 
     */
    public static BufferedImage readImageResource(String path) throws IOException{
        return ImageIO.read(SpiralGenerator.class.getResource(path));
    }
    /**
     * 
     * @return 
     */
    public static File getProgramDirectory(){
        try{
            java.net.URL url = SpiralGenerator.class.getProtectionDomain().getCodeSource().getLocation();
            if (url != null)
                return new File(url.toURI()).getParentFile();
        } catch (URISyntaxException ex) {
            log(Level.WARNING, SpiralGeneratorUtilities.class, 
                    "getProgramDirectory", 
                    "Failed to retrieve program directory", ex);
        }
        return null;
    }
    /**
     * 
     * @param arr
     * @return 
     */
    public static String toByteString(byte[] arr){
            // If the array is null
        if (arr == null)
            return "null";
            // If the array is empty
        if (arr.length == 0)
            return "[]";
        String str = "[";
            // Go through the bytes in the array
        for (byte value : arr){
            str += String.format("0x%02X, ", Byte.toUnsignedInt(value));
        }
        return str.substring(0, str.length()-2) + "]";
    }
    /**
     * 
     * @param g
     * @param x
     * @param y
     * @param scaleX
     * @param scaleY 
     */
    public static void scale(Graphics2D g, double x, double y, double scaleX, 
            double scaleY){
            // Translate the graphics context to the given point
        g.translate(x, y);
            // Scale the graphics context
        g.scale(scaleX, scaleY);
            // Translate the graphics context back to where would be before 
            // scaling it
        g.translate(-x, -y);
    }
    /**
     * 
     * @param g
     * @param x
     * @param y
     * @param scale 
     */
    public static void scale(Graphics2D g, double x, double y, double scale){
        scale(g,x,y,scale,scale);
    }
    /**
     * 
     * @param color
     * @return 
     */
    public static boolean hasNoColor(Color color){
        return color == null || color.getAlpha() == 0;
    }
    /**
     * 
     * @param color1
     * @param color2
     * @return 
     */
    public static boolean hasNoColor(Color color1, Color color2){
        return hasNoColor(color1) && hasNoColor(color2);
    }
    /**
     * 
     * @param g
     * @param mask
     * @param x
     * @param y
     * @param invert 
     */
    public static void maskImage(Graphics2D g, Image mask, int x, int y, 
            boolean invert){
        g.setComposite((invert)?AlphaComposite.DstOut:AlphaComposite.DstIn);
        g.drawImage(mask, x, y, null);
    }
    /**
     * 
     * @param g
     * @param mask
     * @param x
     * @param y 
     */
    public static void maskImage(Graphics2D g, Image mask, int x, int y){
        maskImage(g,mask,x,y,false);
    }
    /**
     * 
     * @param g
     * @param mask
     * @param invert 
     */
    public static void maskImage(Graphics2D g, Image mask, boolean invert){
        maskImage(g,mask,0,0,invert);
    }
    /**
     * 
     * @param g
     * @param mask 
     */
    public static void maskImage(Graphics2D g, Image mask){
        maskImage(g,mask,false);
    }
    /**
     * 
     * @param g
     * @param image
     * @param width
     * @param height 
     */
    public static void drawCenteredImage(Graphics2D g, BufferedImage image, 
            int width, int height){
            // Center and draw the image
        g.drawImage(image, 
                Math.max(0, Math.floorDiv(width-image.getWidth(), 2)), 
                Math.max(0, Math.floorDiv(height-image.getHeight(), 2)), 
                null);
    }
    /**
     * 
     * @param image
     * @param width
     * @param height
     * @return 
     */
    public static BufferedImage getCenteredImage(BufferedImage image, 
            int width, int height){
            // If the image's width and height already match the given size
        if (image.getWidth() == width && image.getHeight() == height)
            return image;
            // Get an image with the given size
        BufferedImage img = new BufferedImage(width,height,
                BufferedImage.TYPE_INT_ARGB);
            // Get the graphics context for the new image
        Graphics2D g = img.createGraphics();
            // Draw the old image centered on the new image
        drawCenteredImage(g,image,width,height);
        g.dispose();
        return img;
    }
    /**
     * 
     * @param image
     * @return 
     */
    public static BufferedImage getSquareImage(BufferedImage image){
            // If the image width is the same as its height
        if (image.getWidth() == image.getHeight())
            return image;
            // Get the larger of the two sizes
        int size = Math.max(image.getWidth(), image.getHeight());
        return getCenteredImage(image,size,size);
    }
    /**
     * 
     * @param r
     * @param g
     * @param b
     * @param mode
     * @return 
     */
    public static float toGrayscale(int r, int g, int b, int mode){
            // This will get the RGB values as floating point numbers between 0 
            // and 1
        float[] rgb = new float[]{r, g, b};
            // Go through the RGB values
        for (int i = 0; i < rgb.length; i++){
                // Divide it by 255 to get it within the range of 0 and 1
            rgb[i] /= 255.0f;
        }   // Determine how to get the luminance for this pixel
        switch(mode){
                // If using the Luma mode
            case(1):
                    // Use the standard luminance equation
                return (float)(0.2126 * rgb[0] + 0.7152 * rgb[1] + 0.0722 * rgb[2]);
                // If using the Lightness value of HSL
            case(2):
                    // This will get the minimum of the RGB values
                float min = 1;
                    // This will get the maximum of the RGB values
                float max = 0;
                    // Go through the RGB values
                for (float value : rgb){
                    min = Math.min(min, value);
                    max = Math.max(max, value);
                }
                return (min + max) / 2.0f;
                // If using the Value of HSV
            case(4):
                    // This will get the largest of the RGB values
                float v = 0;
                    // Go through the RGB values
                for (float value : rgb)
                    v = Math.max(value, v);
                return v;
                // If using the Luminance
            case(0):
                    // Convert the color to gray using the gray color space
                rgb = ColorSpace.getInstance(ColorSpace.CS_GRAY).fromRGB(rgb);
                    // If there is now only one value in the array
                if (rgb.length == 1)
                    return rgb[0];
            default:
                    // This will get the average of the RGB values
                double l = 0;
                for (float value : rgb)
                    l += value;
                return (float)(l / rgb.length);
        }
    }
    /**
     * 
     * @param rgb
     * @param mode
     * @return 
     */
    public static float toGrayscale(int rgb, int mode){
        return toGrayscale((rgb >> 16) & 0xFF,(rgb >> 8) & 0xFF,rgb & 0xFF,mode);
    }
    /**
     * 
     * @param color
     * @param alpha
     * @return 
     */
    public static Color getTranslucentColor(Color color, double alpha){
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
     * @param x
     * @param y
     * @param w
     * @param h
     * @param path
     * @return 
     */
    public static Path2D getHeartShape(double x, double y, double w, double h, 
            Path2D path){
        if (path == null)
            path = new Path2D.Double();
        else
            path.reset();
        double centerX = x + (w / 2.0);
        path.moveTo(centerX, y + (h * HEART_CONTROL_POINTS[0][0]));
        for (int i = HEART_CONTROL_POINTS.length-1; i >= 0; i--){
            path.curveTo(x + (w * HEART_CONTROL_POINTS[i][1]), 
                    y + (h * HEART_CONTROL_POINTS[i][2]), 
                    x + (w * HEART_CONTROL_POINTS[i][3]), 
                    y + (h * HEART_CONTROL_POINTS[i][4]),
                    centerX, y + (h * HEART_CONTROL_POINTS[i][0]));
        }
        path.closePath();
        return path;
    }
}
