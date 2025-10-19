/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package spiral.config;

import java.awt.Color;
import java.awt.Dimension;
import spiral.SpiralGenerator;
import spiral.SpiralGeneratorUtilities;
import spiral.painter.SpiralPainter;

/**
 *
 * @author Mosblinker
 */
public interface SpiralGeneratorSettings {
    /**
     * 
     */
    public static final String SPIRAL_COLOR_KEY_PREFIX = "SpiralColor";
    /**
     * 
     */
    public static final String SPIRAL_TYPE_KEY = "SpiralType";
    /**
     * 
     */
    public static final String FRAME_DURATION_KEY = "FrameDuration";
    /**
     * 
     */
    public static final String IMAGE_SIZE_KEY = "ImageSize";
    /**
     * 
     */
    public static final String MASK_TYPE_KEY = "MaskType";
    /**
     * 
     */
    public static final String MASK_SCALE_KEY = "MaskScale";
    /**
     * 
     */
    public static final String MASK_ROTATION_KEY = "MaskRotation";
    /**
     * 
     */
    public static final String MASK_FLAGS_KEY = "MaskFlags";
    /**
     * 
     */
    @Deprecated
    public static final String MASK_SHAPE_TYPE_KEY = "MaskShapeType";
    /**
     * 
     */
    @Deprecated
    public static final String MASK_SHAPE_WIDTH_KEY = "MaskShapeWidth";
    /**
     * 
     */
    @Deprecated
    public static final String MASK_SHAPE_HEIGHT_KEY = "MaskShapeHeight";
    /**
     * 
     */
    @Deprecated
    public static final String MASK_SHAPE_LINK_SIZE_KEY = "MaskShapeLinkSize";
    /**
     * 
     */
    public static final int MASK_FLIP_HORIZONTAL_FLAG = 0x01;
    /**
     * 
     */
    public static final int MASK_FLIP_VERTICAL_FLAG = 0x02;
    /**
     * 
     * @param key
     * @return 
     */
    public byte[] getSpiralData(String key);
    /**
     * 
     * @param painter
     * @return 
     */
    public default byte[] getSpiralData(SpiralPainter painter){
        return getSpiralData(painter.getPreferenceKey());
    }
    /**
     * 
     * @param key
     * @param data 
     */
    public void setSpiralData(String key, byte[] data);
    /**
     * 
     * @param painter
     * @param data 
     */
    public default void setSpiralData(SpiralPainter painter, byte[] data){
        setSpiralData(painter.getPreferenceKey(),data);
    }
    /**
     * 
     * @param painter 
     */
    public default void setSpiralData(SpiralPainter painter){
        setSpiralData(painter,painter.toByteArray());
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public int getSpiralType(int defaultValue);
    /**
     * 
     * @return 
     */
    public default int getSpiralType(){
        return getSpiralType(0);
    }
    /**
     * 
     * @param value 
     */
    public void setSpiralType(int value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public int getFrameDuration(int defaultValue);
    /**
     * 
     * @return 
     */
    public default int getFrameDuration(){
        return getFrameDuration(SpiralGenerator.DEFAULT_SPIRAL_FRAME_DURATION);
    }
    /**
     * 
     * @param value 
     */
    public void setFrameDuration(int value);
    /**
     * 
     * @param index
     * @param defaultValue
     * @return 
     */
    public Color getSpiralColor(int index, Color defaultValue);
    /**
     * 
     * @param index
     * @return 
     */
    public default Color getSpiralColor(int index){
        return getSpiralColor(index,null);
    }
    /**
     * 
     * @param index
     * @param value 
     */
    public void setSpiralColor(int index, Color value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public double getMaskScale(double defaultValue);
    /**
     * 
     * @return 
     */
    public default double getMaskScale(){
        return getMaskScale(1.0);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskScale(double value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public int getMaskType(int defaultValue);
    /**
     * 
     * @return 
     */
    public default int getMaskType(){
        return getMaskType(0);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskType(int value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public double getMaskRotation(double defaultValue);
    /**
     * 
     * @return 
     */
    public default double getMaskRotation(){
        return getMaskRotation(0.0);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskRotation(double value);
    /**
     * 
     * @return 
     */
    public int getMaskFlags();
    /**
     * 
     * @param value 
     */
    public void setMaskFlags(int value);
    /**
     * 
     * @param flag
     * @return 
     */
    public default boolean getMaskFlag(int flag){
        return SpiralGeneratorUtilities.getFlag(getMaskFlags(),flag);
    }
    /**
     * 
     * @param flag
     * @param value 
     */
    public default void setMaskFlag(int flag, boolean value){
        setMaskFlags(SpiralGeneratorUtilities.setFlag(getMaskFlags(),flag,value));
    }
    /**
     * 
     * @return 
     */
    public default boolean isMaskFlippedHorizontally(){
        return getMaskFlag(MASK_FLIP_HORIZONTAL_FLAG);
    }
    /**
     * 
     * @param value 
     */
    public default void setMaskFlippedHorizontally(boolean value){
        setMaskFlag(MASK_FLIP_HORIZONTAL_FLAG,value);
    }
    /**
     * 
     * @return 
     */
    public default boolean isMaskFlippedVertically(){
        return getMaskFlag(MASK_FLIP_VERTICAL_FLAG);
    }
    /**
     * 
     * @param value 
     */
    public default void setMaskFlippedVertically(boolean value){
        setMaskFlag(MASK_FLIP_VERTICAL_FLAG,value);
    }
    /**
     * 
     * @return 
     */
    public OverlayMaskTextSettings getMaskTextSettings();
    /**
     * 
     * @return 
     */
    public OverlayMaskImageSettings getMaskImageSettings();
    /**
     * 
     * @return 
     */
    public OverlayMaskMessagesSettings getMaskMessageSettings();
    /**
     * 
     * @return 
     */
    public OverlayMaskShapeSettings getMaskShapeSettings();
    /**
     * 
     * @param defaultValue
     * @return 
     */
    @Deprecated
    public default double getMaskShapeWidth(double defaultValue){
        return getMaskShapeSettings().getShapeWidth(defaultValue);
    }
    /**
     * 
     * @return 
     */
    @Deprecated
    public default double getMaskShapeWidth(){
        return getMaskShapeWidth(0.1);
    }
    /**
     * 
     * @param value 
     */
    @Deprecated
    public default void setMaskShapeWidth(double value){
        getMaskShapeSettings().setShapeWidth(value);
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    @Deprecated
    public default double getMaskShapeHeight(double defaultValue){
        return getMaskShapeSettings().getShapeHeight(defaultValue);
    }
    /**
     * 
     * @return 
     */
    @Deprecated
    public default double getMaskShapeHeight(){
        return getMaskShapeHeight(0.1);
    }
    /**
     * 
     * @param value 
     */
    @Deprecated
    public default void setMaskShapeHeight(double value){
        getMaskShapeSettings().setShapeHeight(value);
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    @Deprecated
    public default boolean isMaskShapeSizeLinked(boolean defaultValue){
        return getMaskShapeSettings().isShapeSizeLinked(defaultValue);
    }
    /**
     * 
     * @return 
     */
    @Deprecated
    public default boolean isMaskShapeSizeLinked(){
        return isMaskShapeSizeLinked(true);
    }
    /**
     * 
     * @param value 
     */
    @Deprecated
    public default void setMaskShapeSizeLinked(boolean value){
        getMaskShapeSettings().setShapeSizeLinked(value);
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    @Deprecated
    public default int getMaskShapeType(int defaultValue){
        return getMaskShapeSettings().getShapeType(defaultValue);
    }
    /**
     * 
     * @return 
     */
    @Deprecated
    public default int getMaskShapeType(){
        return getMaskShapeType(0);
    }
    /**
     * 
     * @param value 
     */
    @Deprecated
    public default void setMaskShapeType(int value){
        getMaskShapeSettings().setShapeType(value);
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public Dimension getImageSize(Dimension defaultValue);
    /**
     * 
     * @param defaultWidth
     * @param defaultHeight
     * @return 
     */
    public default Dimension getImageSize(int defaultWidth, int defaultHeight){
        return getImageSize(new Dimension(defaultWidth,defaultHeight));
    }
    /**
     * 
     * @return 
     */
    public default Dimension getImageSize(){
        return getImageSize(SpiralGenerator.DEFAULT_SPIRAL_WIDTH,
                SpiralGenerator.DEFAULT_SPIRAL_HEIGHT);
    }
    /**
     * 
     * @param width
     * @param height 
     */
    public void setImageSize(int width, int height);
    /**
     * 
     * @param value 
     */
    public void setImageSize(Dimension value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public default int getImageWidth(int defaultValue){
        Dimension value = getImageSize(null);
        if (value == null)
            return defaultValue;
        return value.width;
    }
    /**
     * 
     * @return 
     */
    public default int getImageWidth(){
        return getImageWidth(SpiralGenerator.DEFAULT_SPIRAL_WIDTH);
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public default int getImageHeight(int defaultValue){
        Dimension value = getImageSize(null);
        if (value == null)
            return defaultValue;
        return value.height;
    }
    /**
     * 
     * @return 
     */
    public default int getImageHeight(){
        return getImageHeight(SpiralGenerator.DEFAULT_SPIRAL_HEIGHT);
    }
}
