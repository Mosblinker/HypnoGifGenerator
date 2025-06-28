/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral;

import config.ConfigUtilities;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Base64;
import java.util.Properties;

/**
 *
 * @author Mosblinker
 */
public class SpiralGeneratorProperties extends Properties implements SpiralGeneratorSettings {
    /**
     * 
     */
    public static final String FRAME_DATA_FILE_HEADER = 
            "["+SpiralGenerator.PROGRAM_NAME+" Data]";
    /**
     * 
     */
    public SpiralGeneratorProperties(){ }
    /**
     * 
     * @param key
     * @param defaultFile
     * @return 
     */
    public File getFileProperty(String key, File defaultFile){
        String data = getProperty(key);
        if (data == null)
            return defaultFile;
        return new File(data);
    }
    /**
     * 
     * @param key
     * @return 
     */
    public File getFileProperty(String key){
        return getFileProperty(key,null);
    }
    /**
     * 
     * @param key
     * @param value 
     */
    public void setFileProperty(String key, File value){
        setProperty(key, (value == null)?null:value.toString());
    }
    /**
     * This returns the color mapped to the given key in the preference node, or 
     * null if no color is mapped to that key.
     * @param key The key to get the associated color for.
     * @param defaultValue
     * @return The color associated with the given key in the preference node, 
     * or null if no color is associated with the given key.
     * @throws IllegalStateException If the preference node is not available, 
     * either due to not being available when the program started up or due to 
     * the preference node being removed.
     * @throws IllegalArgumentException If the key contains the null control 
     * character.
     */
    public Color getColorProperty(String key, Color defaultValue){
        return ConfigUtilities.colorFromString(getProperty(key),defaultValue);
    }
    /**
     * 
     * @param key
     * @return 
     */
    public Color getColorProperty(String key){
        return getColorProperty(key,null);
    }
    /**
     * This maps the given key to the given color in the preference node. If the 
     * given color is null, then the key will be removed.
     * @param key The key to map the color to.
     * @param color The color to map to the key.
     * @throws IllegalStateException If the preference node is not available, 
     * either due to not being available when the program started up or due to 
     * the preference node being removed.
     * @throws IllegalArgumentException If the key either contains the null 
     * control character or is too long to be stored in the preference node. 
     * @throws NullPointerException If the key is null.
     */
    public void setColorProperty(String key, Color color){
        setProperty(key,ConfigUtilities.colorToString(color));
    }
    /**
     * 
     * @param key
     * @param defaultValue
     * @return 
     */
    public Boolean getBooleanProperty(String key, Boolean defaultValue){
            // Get the value for the property
        Boolean value = ConfigUtilities.booleanValueOf(getProperty(key));
            // If the value is null, return the default value. Otherwise, return 
            // the value for the property
        return (value == null) ? defaultValue : value;
    }
    /**
     * 
     * @param key
     * @return 
     */
    public Boolean getBooleanProperty(String key){
        return getBooleanProperty(key,null);
    }
    /**
     * 
     * @param key
     * @param value
     * @return 
     */
    public Object setBooleanProperty(String key, Boolean value){
        return setProperty(key,(value!=null)?value.toString():null);
    }
    @Override
    public Object setProperty(String key, String value){
        if (value == null)
            return super.remove(key);
        else
            return super.setProperty(key, value);
    }
    /**
     * 
     * @param key
     * @param defaultValue
     * @return 
     */
    public Integer getIntProperty(String key, Integer defaultValue){
        String value = getProperty(key);
        if (value != null){
            try{
                return Integer.valueOf(value);
            } catch (NumberFormatException ex){}
        }
        return defaultValue;
    }
    /**
     * 
     * @param key
     * @return 
     */
    public Integer getIntProperty(String key){
        return getIntProperty(key,null);
    }
    /**
     * 
     * @param key
     * @param value
     * @return 
     */
    public Object setIntProperty(String key, Integer value){
        return setProperty(key,(value!=null)?value.toString():null);
    }
    /**
     * 
     * @param key
     * @param defaultValue
     * @return 
     */
    public byte[] getByteArrayProperty(String key, byte[] defaultValue){
        String value = getProperty(key);
        if (value != null){
            try{
                return Base64.getDecoder().decode(value);
            } catch (NumberFormatException ex){}
        }
        return defaultValue;
    }
    /**
     * 
     * @param key
     * @return 
     */
    public byte[] getByteArrayProperty(String key){
        return getByteArrayProperty(key,null);
    }
    /**
     * 
     * @param key
     * @param value
     * @return 
     */
    public Object setByteArrayProperty(String key, byte[] value){
        String str = null;
        if (value != null)
            str = Base64.getEncoder().encodeToString(value);
        return setProperty(key,str);
    }
    /**
     * 
     * @param key
     * @param defaultValue
     * @return 
     */
    public Double getDoubleProperty(String key, Double defaultValue){
        String value = getProperty(key);
        if (value != null){
            try{
                return Double.valueOf(value);
            } catch (NumberFormatException ex){}
        }
        return defaultValue;
    }
    /**
     * 
     * @param key
     * @return 
     */
    public Double getDoubleProperty(String key){
        return getDoubleProperty(key,null);
    }
    /**
     * 
     * @param key
     * @param value
     * @return 
     */
    public Object setDoubleProperty(String key, Double value){
        return setProperty(key,(value!=null)?value.toString():null);
    }
    /**
     * 
     * @param key
     * @param defaultValue
     * @return 
     */
    public Float getFloatProperty(String key, Float defaultValue){
        String value = getProperty(key);
        if (value != null){
            try{
                return Float.valueOf(value);
            } catch (NumberFormatException ex){}
        }
        return defaultValue;
    }
    /**
     * 
     * @param key
     * @return 
     */
    public Float getFloatProperty(String key){
        return getFloatProperty(key,null);
    }
    /**
     * 
     * @param key
     * @param value
     * @return 
     */
    public Object setFloatProperty(String key, Float value){
        return setProperty(key,(value!=null)?value.toString():null);
    }
    @Override
    public byte[] getSpiralData(String key) {
        return getByteArrayProperty(key);
    }
    @Override
    public void setSpiralData(String key, byte[] data) {
        setByteArrayProperty(key,data);
    }
    @Override
    public int getSpiralType(int defaultValue) {
        return getIntProperty(SPIRAL_TYPE_KEY,defaultValue);
    }
    @Override
    public void setSpiralType(int value) {
        setIntProperty(SPIRAL_TYPE_KEY,value);
    }
    @Override
    public int getFrameDuration(int defaultValue) {
        return getIntProperty(FRAME_DURATION_KEY,defaultValue);
    }
    @Override
    public void setFrameDuration(int value) {
        setIntProperty(FRAME_DURATION_KEY,value);
    }
    @Override
    public Color getSpiralColor(int index, Color defaultValue) {
        return getColorProperty(SPIRAL_COLOR_KEY_PREFIX+index,defaultValue);
    }
    @Override
    public void setSpiralColor(int index, Color value) {
        setColorProperty(SPIRAL_COLOR_KEY_PREFIX+index,value);
    }
    @Override
    public boolean isMaskTextAntialiased(boolean defaultValue) {
        return getBooleanProperty(MASK_TEXT_ANTIALIASING_KEY,defaultValue);
    }
    @Override
    public void setMaskTextAntialiased(boolean value) {
        setBooleanProperty(MASK_TEXT_ANTIALIASING_KEY,value);
    }
    @Override
    public double getMaskLineSpacing(double defaultValue) {
        return getDoubleProperty(MASK_LINE_SPACING_KEY,defaultValue);
    }
    @Override
    public void setMaskLineSpacing(double value) {
        setDoubleProperty(MASK_LINE_SPACING_KEY,value);
    }
    @Override
    public float getMaskFontSize(float defaultValue) {
        return getFloatProperty(MASK_FONT_SIZE_KEY,defaultValue);
    }
    @Override
    public void setMaskFontSize(Float value) {
        setFloatProperty(MASK_FONT_SIZE_KEY,value);
    }
    @Override
    public int getMaskFontStyle(int defaultValue) {
        return getIntProperty(MASK_FONT_STYLE_KEY,defaultValue);
    }
    @Override
    public void setMaskFontStyle(Integer value) {
        setIntProperty(MASK_FONT_STYLE_KEY,value);
    }
    @Override
    public String getMaskFontFamily(String defaultValue) {
        return getProperty(MASK_FONT_FAMILY_KEY,defaultValue);
    }
    @Override
    public void setMaskFontFamily(String value) {
        setProperty(MASK_FONT_FAMILY_KEY,value);
    }
    @Override
    public String getMaskFontName(String defaultValue) {
        return getProperty(MASK_FONT_NAME_KEY,defaultValue);
    }
    @Override
    public void setMaskFontName(String value) {
        setProperty(MASK_FONT_NAME_KEY,value);
    }
    @Override
    public String getMaskText(String defaultValue) {
        return getProperty(MASK_TEXT_KEY,defaultValue);
    }
    @Override
    public void setMaskText(String value) {
        setProperty(MASK_TEXT_KEY,value);
    }
    @Override
    public double getMaskScale(double defaultValue) {
        return getDoubleProperty(MASK_SCALE_KEY,defaultValue);
    }
    @Override
    public void setMaskScale(double value) {
        setDoubleProperty(MASK_SCALE_KEY,value);
    }
    @Override
    public boolean isMaskImageAntialiased(boolean defaultValue) {
        return getBooleanProperty(MASK_IMAGE_ANTIALIASING_KEY,defaultValue);
    }
    @Override
    public void setMaskImageAntialiased(boolean value) {
        setBooleanProperty(MASK_IMAGE_ANTIALIASING_KEY,value);
    }
    @Override
    public int getMaskType(int defaultValue) {
        return getIntProperty(MASK_TYPE_KEY,defaultValue);
    }
    @Override
    public void setMaskType(int value) {
        setIntProperty(MASK_TYPE_KEY,value);
    }
    @Override
    public int getMaskAlphaIndex(int defaultValue) {
        return getIntProperty(MASK_ALPHA_CHANNEL_INDEX_KEY,defaultValue);
    }
    @Override
    public void setMaskAlphaIndex(int value) {
        setIntProperty(MASK_ALPHA_CHANNEL_INDEX_KEY,value);
    }
    @Override
    public boolean isMaskImageInverted(boolean defaultValue) {
        return getBooleanProperty(MASK_IMAGE_INVERT_KEY,defaultValue);
    }
    @Override
    public void setMaskImageInverted(boolean value) {
        setBooleanProperty(MASK_IMAGE_INVERT_KEY,value);
    }
    @Override
    public int getMaskDesaturateMode(int defaultValue) {
        return getIntProperty(MASK_DESATURATE_MODE_KEY,defaultValue);
    }
    @Override
    public void setMaskDesaturateMode(int value) {
        setIntProperty(MASK_DESATURATE_MODE_KEY,value);
    }
    @Override
    public File getMaskImageFile(File defaultValue) {
        return getFileProperty(MASK_IMAGE_FILE_KEY,defaultValue);
    }
    @Override
    public void setMaskImageFile(File value) {
        setFileProperty(MASK_IMAGE_FILE_KEY,value);
    }
    @Override
    public int getMaskImageFrameIndex() {
        return getIntProperty(MASK_IMAGE_FRAME_INDEX_KEY,0);
    }
    @Override
    public void setMaskImageFrameIndex(int value) {
        setIntProperty(MASK_IMAGE_FRAME_INDEX_KEY,value);
    }
    @Override
    public int getMaskImageInterpolation(int defaultValue) {
        return getIntProperty(MASK_IMAGE_INTERPOLATION_KEY,defaultValue);
    }
    @Override
    public void setMaskImageInterpolation(int value) {
        setIntProperty(MASK_IMAGE_INTERPOLATION_KEY,value);
    }
    @Override
    public double getMaskRotation(double defaultValue) {
        return getDoubleProperty(MASK_ROTATION_KEY,defaultValue);
    }
    @Override
    public void setMaskRotation(double value) {
        setDoubleProperty(MASK_ROTATION_KEY,value);
    }
    @Override
    public int getMaskFlags() {
        return getIntProperty(MASK_FLAGS_KEY,0);
    }
    @Override
    public void setMaskFlags(int value) {
        setIntProperty(MASK_FLAGS_KEY,value);
    }
    @Override
    public double getMaskShapeWidth(double defaultValue) {
        return getDoubleProperty(MASK_SHAPE_WIDTH_KEY,defaultValue);
    }
    @Override
    public void setMaskShapeWidth(double value) {
        setDoubleProperty(MASK_SHAPE_WIDTH_KEY,value);
    }
    @Override
    public double getMaskShapeHeight(double defaultValue) {
        return getDoubleProperty(MASK_SHAPE_HEIGHT_KEY,defaultValue);
    }
    @Override
    public void setMaskShapeHeight(double value) {
        setDoubleProperty(MASK_SHAPE_HEIGHT_KEY,value);
    }
    @Override
    public boolean isMaskShapeSizeLinked(boolean defaultValue) {
        return getBooleanProperty(MASK_SHAPE_LINK_SIZE_KEY,defaultValue);
    }
    @Override
    public void setMaskShapeSizeLinked(boolean value) {
        setBooleanProperty(MASK_SHAPE_LINK_SIZE_KEY,value);
    }
    @Override
    public int getMaskShapeType(int defaultValue) {
        return getIntProperty(MASK_SHAPE_TYPE_KEY,defaultValue);
    }
    @Override
    public void setMaskShapeType(int value) {
        setIntProperty(MASK_SHAPE_TYPE_KEY,value);
    }
    @Override
    public int getImageWidth(int defaultValue) {
        return getIntProperty(IMAGE_WIDTH_KEY,defaultValue);
    }
    @Override
    public void setImageWidth(int value) {
        setIntProperty(IMAGE_WIDTH_KEY,value);
    }
    @Override
    public int getImageHeight(int defaultValue) {
        return getIntProperty(IMAGE_HEIGHT_KEY,defaultValue);
    }
    @Override
    public void setImageHeight(int value) {
        setIntProperty(IMAGE_HEIGHT_KEY,value);
    }
    /**
     * 
     * @param writer 
     * @throws java.io.IOException 
     */
    public void store(Writer writer) throws IOException{
        store(writer,FRAME_DATA_FILE_HEADER);
    }
    /**
     * 
     * @param out
     * @throws IOException 
     */
    public void store(OutputStream out) throws IOException{
        store(out,FRAME_DATA_FILE_HEADER);
    }
    /**
     * 
     * @param out
     * @throws IOException 
     */
    public void storeToXML(OutputStream out) throws IOException{
        storeToXML(out, FRAME_DATA_FILE_HEADER);
    }
}
