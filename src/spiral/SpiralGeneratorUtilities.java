/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral;

import geom.GeometryMath;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.RectangularShape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import net.coobird.thumbnailator.Thumbnailator;

/**
 *
 * @author Mosblinker
 */
public final class SpiralGeneratorUtilities {
    
    public static final Color TRANSPARENT_COLOR = new Color(0x00000000, true);
    
    public static final int SCALE_IMAGE_SETTING_NEAREST_NEIGHBOR = 0;
    
    public static final int SCALE_IMAGE_SETTING_BILINEAR = 1;
    
    public static final int SCALE_IMAGE_SETTING_BICUBIC = 2;
    
    public static final int SCALE_IMAGE_SETTING_SMOOTH = 3;
    
    public static final int SCALE_IMAGE_SETTING_THUMBNAILATOR = 4;
    
    public static final int FIRST_SCALE_IMAGE_SETTING = SCALE_IMAGE_SETTING_NEAREST_NEIGHBOR;
    
    public static final int LAST_SCALE_IMAGE_SETTING = SCALE_IMAGE_SETTING_THUMBNAILATOR;
    /**
     * 
     */
    private static final double[][] HEART_CONTROL_POINTS = {
            // Right side of heart
        {0.15, 1.468, 0.328, 0.781, -0.295},
            // Left side of heart
        { 1.0, 0.219, -0.295, -0.468, 0.328}
    };
    /**
     * This class cannot be constructed.
     */
    private SpiralGeneratorUtilities() {}
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
            SpiralGenerator.getLogger().log(Level.WARNING, 
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
    
    public static BufferedImage scaleImage(BufferedImage image,
            int width,int height,int interpolation){
            // If the interpolation is the thumbnailator
        if (interpolation == SCALE_IMAGE_SETTING_THUMBNAILATOR)
            return Thumbnailator.createThumbnail(image,width,height);
            // Get the target size
        Dimension target = getTargetSize(image,width,height);
        BufferedImage img = new BufferedImage(target.width, target.height, BufferedImage.TYPE_INT_ARGB);
        Image drawn = image;
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, 
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, 
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, 
                RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
        switch(interpolation){
            case(SCALE_IMAGE_SETTING_SMOOTH):
                drawn = image.getScaledInstance(target.width, target.height, Image.SCALE_SMOOTH);
                break;
            default:
                Object interValue;
                switch(interpolation){
                    case(SCALE_IMAGE_SETTING_NEAREST_NEIGHBOR):
                        interValue = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
                        break;
                    case(SCALE_IMAGE_SETTING_BILINEAR):
                        interValue = RenderingHints.VALUE_INTERPOLATION_BILINEAR;
                        break;
                    case(SCALE_IMAGE_SETTING_BICUBIC):
                    default:
                        interValue = RenderingHints.VALUE_INTERPOLATION_BICUBIC;
                }
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interValue);
        }
        g.drawImage(drawn, 0, 0, target.width, target.height, null);
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
     * @param ratio
     * @param color1
     * @param color2
     * @return 
     */
    private static double blendColorValue(double ratio, int color1, int color2){
        double c = (color1 / 255.0);
        return c + ratio * ((color2 / 255.0) - c);
    }
    /**
     * 
     * @param color1
     * @param color2
     * @param t
     * @return 
     */
    public static Color blendColor(Color color1, Color color2, double t){
        if (hasNoColor(color1, color2))
            return TRANSPARENT_COLOR;
        if (Objects.equals(color1, color2) || t <= 0.0)
            return color1;
        if (t >= 1.0)
            return color2;
        double alpha = blendColorValue(t,color1.getAlpha(),color2.getAlpha());
        if (alpha <= 0.0)
            return getTranslucentColor(color1, alpha);
        double ratio = t * ((color2.getAlpha() / 255.0) / alpha);
        return new Color(
                (float)blendColorValue(ratio,color1.getRed(),color2.getRed()),
                (float)blendColorValue(ratio,color1.getGreen(),color2.getGreen()),
                (float)blendColorValue(ratio,color1.getBlue(),color2.getBlue()),
                (float)alpha);
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
    /**
     * 
     * @param rect
     * @param path
     * @return 
     */
    public static Path2D getHeartShape(RectangularShape rect, Path2D path){
        return getHeartShape(rect.getX(),rect.getY(),rect.getWidth(),rect.getHeight(),
                path);
    }
    /**
     * 
     * @param x
     * @param y
     * @param w
     * @param h
     * @param right
     * @param curve
     * @return 
     */
    public static CubicCurve2D getHeartHalfCurve(double x, double y, double w, 
            double h, boolean right, CubicCurve2D curve){
        if (curve == null)
            curve = new CubicCurve2D.Double();
        int index = (right)?0:1;
        double[] ctrlPts = HEART_CONTROL_POINTS[index];
        double centerX = x + (w / 2.0);
        curve.setCurve(centerX, y + (h * HEART_CONTROL_POINTS[(index+1)%2][0]), 
                x + (w * ctrlPts[1]), y + (h * ctrlPts[2]), 
                x + (w * ctrlPts[3]), y + (h * ctrlPts[4]), 
                centerX, y + (h * ctrlPts[0]));
        return curve;
    }
    /**
     * 
     * @param rect
     * @param right
     * @param curve
     * @return 
     */
    public static CubicCurve2D getHeartHalfCurve(RectangularShape rect, 
            boolean right, CubicCurve2D curve){
        return getHeartHalfCurve(rect.getX(),rect.getY(),rect.getWidth(),
                rect.getHeight(),right,curve);
    }
    /**
     * 
     * @param x
     * @param y
     * @param w
     * @param h
     * @param path
     * @param point
     * @return 
     */
    public static Path2D getStarShape(double x, double y, double w, double h, 
            Path2D path, Point2D point){
        if (path == null)
            path = new Path2D.Double();
        else
            path.reset();
        double centerX = x + (w / 2.0);
        path.moveTo(centerX, y);
        double t1 = w * 0.19444;
        double y1 = y+(h*0.38222);
        point = GeometryMath.getLinePointForY(centerX, y, x+t1, y+h, y1, point);
        double x1 = point.getX();
        path.lineTo(x1, y1);
        path.lineTo(x, y1);
        point = GeometryMath.getLineIntersection(centerX, y, x+t1, y+h, 
                x, y1, x+(w-t1), y+h, point);
        double x2 = point.getX();
        double y2 = point.getY();
        path.lineTo(x2, y2);
        path.lineTo(x+t1, y+h);
        point = GeometryMath.getLinePointForX(x, y1, x+(w-t1), y+h, centerX, point);
        path.lineTo(point.getX(), point.getY());
        path.lineTo(x+(w-t1), y+h);
        path.lineTo(x+(w-(x2-x)), y2);
        path.lineTo(x+w, y1);
        path.lineTo(x+(w-(x1-x)), y1);
        path.closePath();
        return path;
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
    public static Path2D getStarShape(double x, double y, double w, double h, 
            Path2D path){
        return getStarShape(x,y,w,h,path,null);
    }
    /**
     * 
     * @param rect
     * @param path
     * @param point
     * @return 
     */
    public static Path2D getStarShape(RectangularShape rect, Path2D path, 
            Point2D point){
        return getStarShape(rect.getX(),rect.getY(),rect.getWidth(),rect.getHeight(),
                path,point);
    }
    /**
     * 
     * @param rect
     * @param path
     * @return 
     */
    public static Path2D getStarShape(RectangularShape rect, Path2D path){
        return getStarShape(rect,path);
    }
    /**
     * 
     * @param flags
     * @param flag
     * @return 
     */
    public static boolean getFlag(int flags, int flag){
        return (flags & flag) != 0;
    }
    /**
     * 
     * @param flags
     * @param flag
     * @param value
     * @return 
     */
    public static int setFlag(int flags, int flag, boolean value){
        return (value) ? (flags | flag) : (flags & ~flag);
    }/**
     * 
     * @param srcWidth
     * @param srcHeight
     * @param width
     * @param height
     * @param fit
     * @param dim
     * @return 
     */
    
    public static Dimension getTargetSize(int srcWidth, int srcHeight, 
            int width, int height, boolean fit, Dimension dim){
            // If the given dimension is null
        if (dim == null)
            dim = new Dimension();
            // Get the aspect ratio of the source size
        double srcRatio = ((double)srcWidth) / srcHeight;
            // Get the aspect ratio of the target size
        double targetRatio = ((double)width) / height;
            // If the two ratios are the same
        if (Double.compare(srcRatio, targetRatio) == 0)
            dim.setSize(width, height);
        if ((srcRatio > targetRatio) == fit)
            dim.setSize(width, (int)Math.round(width/srcRatio));
        else
            dim.setSize((int)Math.round(height*srcRatio), height);
        return dim;
    }
    /**
     * 
     * @param srcWidth
     * @param srcHeight
     * @param width
     * @param height
     * @param fit
     * @return 
     */
    public static Dimension getTargetSize(int srcWidth, int srcHeight, 
            int width, int height, boolean fit){
        return getTargetSize(srcWidth,srcHeight,width,height,fit,
                new Dimension());
    }
    /**
     * 
     * @param srcWidth
     * @param srcHeight
     * @param width
     * @param height
     * @param dim
     * @return 
     */
    public static Dimension getTargetSize(int srcWidth, int srcHeight, 
            int width, int height, Dimension dim){
        return getTargetSize(srcWidth,srcHeight,width,height,true,dim);
    }
    /**
     * 
     * @param srcWidth
     * @param srcHeight
     * @param width
     * @param height
     * @return 
     */
    public static Dimension getTargetSize(int srcWidth, int srcHeight, 
            int width, int height){
        return getTargetSize(srcWidth,srcHeight,width,height,true);
    }
    /**
     * 
     * @param image
     * @param width
     * @param height
     * @param fit
     * @param dim
     * @return 
     */
    public static Dimension getTargetSize(BufferedImage image, 
            int width, int height, boolean fit, Dimension dim){
        return getTargetSize(image.getWidth(), image.getHeight(),width,height,
                fit,dim);
    }
    /**
     * 
     * @param image
     * @param width
     * @param height
     * @param fit
     * @return 
     */
    public static Dimension getTargetSize(BufferedImage image, 
            int width, int height, boolean fit){
        return getTargetSize(image,width,height,fit,new Dimension());
    }
    /**
     * 
     * @param image
     * @param width
     * @param height
     * @param dim
     * @return 
     */
    public static Dimension getTargetSize(BufferedImage image, 
            int width, int height, Dimension dim){
        return getTargetSize(image,width,height,true,dim);
    }
    /**
     * 
     * @param image
     * @param width
     * @param height
     * @return 
     */
    public static Dimension getTargetSize(BufferedImage image, 
            int width, int height){
        return getTargetSize(image,width,height,true);
    }
}
