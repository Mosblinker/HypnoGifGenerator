/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral;

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
}
