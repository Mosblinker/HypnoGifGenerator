/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.config;

import config.ConfigUtilities;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Base64;
import java.util.Properties;
import spiral.SpiralGenerator;
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
    public static final String MASK_TEXT_KEY_PREFIX = "MaskText";
    /**
     * 
     */
    public static final String MASK_IMAGE_KEY_PREFIX = "MaskImage";
    /**
     * 
     */
    public static final String MASK_MESSAGES_KEY_PREFIX = "MaskMessages";
    /**
     * 
     */
    public static final String MASK_SHAPE_KEY_PREFIX = "MaskShape";
    /**
     * 
     */
    private final OverlayMaskTextSettings maskTextConfig;
    /**
     * 
     */
    private final OverlayMaskImageSettings maskImageConfig;
    /**
     * 
     */
    private final OverlayMaskMessagesSettings maskMessagesConfig;
    /**
     * 
     */
    private final OverlayMaskShapeSettings maskShapeConfig;
    /**
     * 
     */
    public SpiralGeneratorProperties(){ 
        maskTextConfig = new OverlayMaskTextSettingsImpl();
        maskImageConfig = new OverlayMaskImageSettingsImpl();
        maskMessagesConfig = new OverlayMaskMessagesSettingsImpl();
        maskShapeConfig = new OverlayMaskShapeSettingsImpl();
    }
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
    /**
     * 
     * @param key
     * @param defaultValue
     * @return 
     */
    public Dimension getDimensionProperty(String key, Dimension defaultValue){
        return ConfigUtilities.dimensionFromByteArray(getByteArrayProperty(key),
                defaultValue);
    }
    /**
     * 
     * @param key
     * @return 
     */
    public Dimension getDimensionProperty(String key){
        return getDimensionProperty(key,null);
    }
    /**
     * 
     * @param key
     * @param value
     * @return 
     */
    public Object setDimensionProperty(String key, Dimension value){
        return setByteArrayProperty(key,ConfigUtilities.dimensionToByteArray(value));
    }
    /**
     * 
     * @param key
     * @param width
     * @param height
     * @return 
     */
    public Object setDimensionProperty(String key, int width, int height){
        return setByteArrayProperty(key,
                ConfigUtilities.dimensionToByteArray(width,height));
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
    public double getMaskScale(double defaultValue) {
        return getDoubleProperty(MASK_SCALE_KEY,defaultValue);
    }
    @Override
    public void setMaskScale(double value) {
        setDoubleProperty(MASK_SCALE_KEY,value);
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
    public OverlayMaskTextSettings getMaskTextSettings() {
        return maskTextConfig;
    }
    @Override
    public OverlayMaskImageSettings getMaskImageSettings() {
        return maskImageConfig;
    }
    @Override
    public OverlayMaskMessagesSettings getMaskMessageSettings() {
        return maskMessagesConfig;
    }
    @Override
    public OverlayMaskShapeSettings getMaskShapeSettings(){
        return maskShapeConfig;
    }
    @Override
    public double getMaskShapeWidth(double defaultValue) {
        return maskShapeConfig.getShapeWidth(defaultValue);
    }
    @Override
    public void setMaskShapeWidth(double value) {
        maskShapeConfig.setShapeWidth(value);
    }
    @Override
    public double getMaskShapeHeight(double defaultValue) {
        return maskShapeConfig.getShapeHeight(defaultValue);
    }
    @Override
    public void setMaskShapeHeight(double value) {
        maskShapeConfig.setShapeHeight(value);
    }
    @Override
    public boolean isMaskShapeSizeLinked(boolean defaultValue) {
        return maskShapeConfig.isShapeSizeLinked(defaultValue);
    }
    @Override
    public void setMaskShapeSizeLinked(boolean value) {
        maskShapeConfig.setShapeSizeLinked(value);
    }
    @Override
    public int getMaskShapeType(int defaultValue) {
        return maskShapeConfig.getShapeType(defaultValue);
    }
    @Override
    public void setMaskShapeType(int value) {
        maskShapeConfig.setShapeType(value);
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
    @Override
    public Dimension getImageSize(Dimension defaultValue) {
        return getDimensionProperty(IMAGE_SIZE_KEY,defaultValue);
    }
    @Override
    public void setImageSize(int width, int height) {
        setDimensionProperty(IMAGE_SIZE_KEY,width,height);
    }
    @Override
    public void setImageSize(Dimension value) {
        setDimensionProperty(IMAGE_SIZE_KEY,value);
    }
    /**
     * 
     */
    private abstract class AntialiasedOverlayMaskSettingsImpl implements 
            AntialiasedOverlayMaskSettings{
        
        protected abstract String getPrefix();
        @Override
        public boolean isAntialiased(boolean defaultValue) {
            return getBooleanProperty(getPrefix()+ANTIALIASING_KEY,defaultValue);
        }
        @Override
        public void setAntialiased(boolean value) {
            setBooleanProperty(getPrefix()+ANTIALIASING_KEY,value);
        }
    }
    /**
     * 
     */
    private abstract class TextOverlayMaskSettingsImpl extends 
            AntialiasedOverlayMaskSettingsImpl implements TextOverlayMaskSettings{
        @Override
        public float getFontSize(float defaultValue) {
            return getFloatProperty(getPrefix()+FONT_SIZE_KEY,defaultValue);
        }
        @Override
        public void setFontSize(Float value) {
            setFloatProperty(getPrefix()+FONT_SIZE_KEY,value);
        }
        @Override
        public int getFontStyle(int defaultValue) {
            return getIntProperty(getPrefix()+FONT_STYLE_KEY,defaultValue);
        }
        @Override
        public void setFontStyle(Integer value) {
            setIntProperty(getPrefix()+FONT_STYLE_KEY,value);
        }
        @Override
        public String getFontFamily(String defaultValue) {
            return getProperty(getPrefix()+FONT_FAMILY_KEY,defaultValue);
        }
        @Override
        public void setFontFamily(String value) {
            setProperty(getPrefix()+FONT_FAMILY_KEY,value);
        }
        @Override
        public String getFontName(String defaultValue) {
            return getProperty(getPrefix()+FONT_NAME_KEY,defaultValue);
        }
        @Override
        public void setFontName(String value) {
            setProperty(getPrefix()+FONT_NAME_KEY,value);
        }
    }
    /**
     * 
     */
    private class OverlayMaskTextSettingsImpl extends TextOverlayMaskSettingsImpl 
            implements OverlayMaskTextSettings{
        @Override
        protected String getPrefix() {
            return MASK_TEXT_KEY_PREFIX;
        }
        @Override
        public double getLineSpacing(double defaultValue) {
            return getDoubleProperty(getPrefix()+LINE_SPACING_KEY,defaultValue);
        }
        @Override
        public void setLineSpacing(double value) {
            setDoubleProperty(getPrefix()+LINE_SPACING_KEY,value);
        }
        @Override
        public String getText(String defaultValue) {
            return getProperty(getPrefix()+TEXT_KEY,defaultValue);
        }
        @Override
        public void setText(String value) {
            setProperty(getPrefix()+TEXT_KEY,value);
        }
    }
    /**
     * 
     */
    private class OverlayMaskImageSettingsImpl 
            extends AntialiasedOverlayMaskSettingsImpl 
            implements OverlayMaskImageSettings{
        @Override
        protected String getPrefix() {
            return MASK_IMAGE_KEY_PREFIX;
        }
        @Override
        public int getAlphaIndex(int defaultValue) {
            return getIntProperty(getPrefix()+ALPHA_CHANNEL_INDEX_KEY,defaultValue);
        }
        @Override
        public void setAlphaIndex(int value) {
            setIntProperty(getPrefix()+ALPHA_CHANNEL_INDEX_KEY,value);
        }
        @Override
        public boolean isImageInverted(boolean defaultValue) {
            return getBooleanProperty(getPrefix()+IMAGE_INVERT_KEY,defaultValue);
        }
        @Override
        public void setImageInverted(boolean value) {
            setBooleanProperty(getPrefix()+IMAGE_INVERT_KEY,value);
        }
        @Override
        public int getDesaturateMode(int defaultValue) {
            return getIntProperty(getPrefix()+DESATURATE_MODE_KEY,defaultValue);
        }
        @Override
        public void setDesaturateMode(int value) {
            setIntProperty(getPrefix()+DESATURATE_MODE_KEY,value);
        }
        @Override
        public File getImageFile(File defaultValue) {
            return getFileProperty(getPrefix()+IMAGE_FILE_KEY,defaultValue);
        }
        @Override
        public void setImageFile(File value) {
            setFileProperty(getPrefix()+IMAGE_FILE_KEY,value);
        }
        @Override
        public int getImageFrameIndex() {
            return getIntProperty(getPrefix()+IMAGE_FRAME_INDEX_KEY,0);
        }
        @Override
        public void setImageFrameIndex(int value) {
            setIntProperty(getPrefix()+IMAGE_FRAME_INDEX_KEY,value);
        }
        @Override
        public int getImageInterpolation(int defaultValue) {
            return getIntProperty(getPrefix()+IMAGE_INTERPOLATION_KEY,defaultValue);
        }
        @Override
        public void setImageInterpolation(int value) {
            setIntProperty(getPrefix()+IMAGE_INTERPOLATION_KEY,value);
        }
    }
    /**
     * 
     */
    private class OverlayMaskMessagesSettingsImpl 
            extends TextOverlayMaskSettingsImpl
            implements OverlayMaskMessagesSettings{
        @Override
        protected String getPrefix() {
            return MASK_MESSAGES_KEY_PREFIX;
        }
        @Override
        public boolean getAddBlankFrames(boolean defaultValue) {
            return getBooleanProperty(getPrefix()+ADD_BLANK_FRAMES_KEY,defaultValue);
        }
        @Override
        public void setAddBlankFrames(boolean value) {
            setBooleanProperty(getPrefix()+ADD_BLANK_FRAMES_KEY,value);
        }
        @Override
        public int getMessageCount() {
            return getIntProperty(getPrefix()+MESSAGE_COUNT_KEY,0);
        }
        @Override
        public void setMessageCount(int value) {
            setIntProperty(getPrefix()+MESSAGE_COUNT_KEY,value);
        }
        @Override
        public String getMessage(int index) {
            return getProperty(getPrefix()+MESSAGE_KEY_PREFIX+index);
        }
        @Override
        public void setMessage(int index, String value) {
            String key = getPrefix()+MESSAGE_KEY_PREFIX+index;
            if (value == null)
                remove(key);
            else
                setProperty(key,value);
        }
    }
    /**
     * 
     */
    private class OverlayMaskShapeSettingsImpl extends 
            AntialiasedOverlayMaskSettingsImpl implements OverlayMaskShapeSettings{
        @Override
        protected String getPrefix() {
            return MASK_SHAPE_KEY_PREFIX;
        }
        @Override
        public double getShapeWidth(double defaultValue) {
            return getDoubleProperty(getPrefix()+SHAPE_WIDTH_KEY,defaultValue);
        }
        @Override
        public void setShapeWidth(double value) {
            setDoubleProperty(getPrefix()+SHAPE_WIDTH_KEY,value);
        }
        @Override
        public double getShapeHeight(double defaultValue) {
            return getDoubleProperty(getPrefix()+SHAPE_HEIGHT_KEY,defaultValue);
        }
        @Override
        public void setShapeHeight(double value) {
            setDoubleProperty(getPrefix()+SHAPE_HEIGHT_KEY,value);
        }
        @Override
        public boolean isShapeSizeLinked(boolean defaultValue) {
            return getBooleanProperty(getPrefix()+SHAPE_LINK_SIZE_KEY,defaultValue);
        }
        @Override
        public void setShapeSizeLinked(boolean value) {
            setBooleanProperty(getPrefix()+SHAPE_LINK_SIZE_KEY,value);
        }
        @Override
        public int getShapeType(int defaultValue) {
            return getIntProperty(getPrefix()+SHAPE_TYPE_KEY,defaultValue);
        }
        @Override
        public void setShapeType(int value) {
            setIntProperty(getPrefix()+SHAPE_TYPE_KEY,value);
        }
    }
}
