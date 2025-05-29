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
     * @param image
     * @return 
     */
    public static BufferedImage squareImage(BufferedImage image){
            // If the image is null
        if (image == null)
            return null;
            // If the image width is the same as its height
        if (image.getWidth() == image.getHeight())
            return image;
            // Get the larger of the two sizes
        int size = Math.max(image.getWidth(), image.getHeight());
            // Get a square image with the size
        BufferedImage img = new BufferedImage(size,size,
                BufferedImage.TYPE_INT_ARGB);
            // Get the graphics context for the image
        Graphics2D g = img.createGraphics();
            // Draw the original image to the new image
        g.drawImage(image, 
                Math.max(0, Math.floorDiv(size-image.getWidth(), 2)), 
                Math.max(0, Math.floorDiv(size-image.getHeight(), 2)), 
                null);
        g.dispose();
        return img;
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
}
