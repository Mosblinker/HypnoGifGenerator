/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package spiral;

import io.github.dheid.fontchooser.FontFamilies;
import io.github.dheid.fontchooser.FontFamily;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.util.Iterator;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import spiral.painter.SpiralPainter;
import utils.SwingExtendedUtilities;

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
    public static final String IMAGE_WIDTH_KEY = "ImageWidth";
    /**
     * 
     */
    public static final String IMAGE_HEIGHT_KEY = "ImageHeight";
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
    public static final String MASK_FONT_SIZE_KEY = "MaskFontSize";
    /**
     * 
     */
    public static final String MASK_FONT_STYLE_KEY = "MaskFontStyle";
    /**
     * 
     */
    public static final String MASK_FONT_FAMILY_KEY = "MaskFontFamily";
    /**
     * 
     */
    public static final String MASK_FONT_NAME_KEY = "MaskFontName";
    /**
     * 
     */
    public static final String MASK_TEXT_KEY = "MaskText";
    /**
     * 
     */
    public static final String MASK_LINE_SPACING_KEY = "MaskLineSpacing";
    /**
     * 
     */
    public static final String MASK_TEXT_ANTIALIASING_KEY = "MaskTextAntialiasing";
    /**
     * 
     */
    public static final String MASK_IMAGE_ANTIALIASING_KEY = "MaskImageAntialiasing";
    /**
     * 
     */
    public static final String MASK_ALPHA_CHANNEL_INDEX_KEY = "MaskAlphaChannelIndex";
    /**
     * 
     */
    public static final String MASK_IMAGE_INVERT_KEY = "MaskImageInvert";
    /**
     * 
     */
    public static final String MASK_DESATURATE_MODE_KEY = "MaskDesaturateMode";
    /**
     * 
     */
    public static final String MASK_IMAGE_FILE_KEY = "MaskImageFile";
    /**
     * 
     */
    public static final String MASK_IMAGE_FRAME_INDEX_KEY = "MaskImageFrameIndex";
    /**
     * 
     */
    public static final String MASK_IMAGE_INTERPOLATION_KEY = "MaskImageInterpolation";
    /**
     * 
     */
    public static final String MASK_SHAPE_TYPE_KEY = "MaskShapeType";
    /**
     * 
     */
    public static final String MASK_SHAPE_WIDTH_KEY = "MaskShapeWidth";
    /**
     * 
     */
    public static final String MASK_SHAPE_HEIGHT_KEY = "MaskShapeHeight";
    /**
     * 
     */
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
        return getFrameDuration(SpiralGenerator.SPIRAL_FRAME_DURATION);
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
    public boolean isMaskTextAntialiased(boolean defaultValue);
    /**
     * 
     * @return 
     */
    public default boolean isMaskTextAntialiased(){
        return isMaskTextAntialiased(true);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskTextAntialiased(boolean value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public double getMaskLineSpacing(double defaultValue);
    /**
     * 
     * @return 
     */
    public default double getMaskLineSpacing(){
        return getMaskLineSpacing(0.0);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskLineSpacing(double value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public float getMaskFontSize(float defaultValue);
    /**
     * 
     * @return 
     */
    public default float getMaskFontSize(){
        return getMaskFontSize(11.0f);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskFontSize(Float value);
    /**
     * 
     * @param font 
     */
    public default void setMaskFontSize(Font font){
        setMaskFontSize((font == null)?null:font.getSize2D());
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public int getMaskFontStyle(int defaultValue);
    /**
     * 
     * @return 
     */
    public default int getMaskFontStyle(){
        return getMaskFontStyle(Font.PLAIN);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskFontStyle(Integer value);
    /**
     * 
     * @param font 
     */
    public default void setMaskFontStyle(Font font){
        setMaskFontStyle((font == null)?null:font.getStyle());
    }/**
     * 
     * @param flag
     * @return 
     */
    public default boolean getMaskFontStyleValue(int flag){
        return SpiralGeneratorUtilities.getFlag(getMaskFontStyle(),flag);
    }
    /**
     * 
     * @param flag
     * @param value 
     */
    public default void setMaskFontStyleValue(int flag, boolean value){
        setMaskFontStyle(SpiralGeneratorUtilities.setFlag(getMaskFontStyle(),flag,value));
    }
    /**
     * 
     * @return 
     */
    public default boolean isMaskFontBold(){
        return getMaskFontStyleValue(Font.BOLD);
    }
    /**
     * 
     * @param value 
     */
    public default void setMaskFontBold(boolean value){
        setMaskFontStyleValue(Font.BOLD,value);
    }
    /**
     * 
     * @return 
     */
    public default boolean isMaskFontItalic(){
        return getMaskFontStyleValue(Font.ITALIC);
    }
    /**
     * 
     * @param value 
     */
    public default void setMaskFontItalic(boolean value){
        setMaskFontStyleValue(Font.ITALIC,value);
    }
    /**
     * 
     * @param bold
     * @param italic 
     */
    public default void setMaskFontStyle(boolean bold, boolean italic){
            // Get the style, but without the bold and italic flags
        int value = getMaskFontStyle() & ~(Font.BOLD | Font.ITALIC);
            // If the font's style is bold
        if (bold)
            value |= Font.BOLD;
            // If the font's style is italic
        if (italic)
            value |= Font.ITALIC;
        setMaskFontStyle(value);
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public String getMaskFontFamily(String defaultValue);
    /**
     * 
     * @return 
     */
    public default String getMaskFontFamily(){
        return getMaskFontFamily(Font.SANS_SERIF);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskFontFamily(String value);
    /**
     * 
     * @param font 
     */
    public default void setMaskFontFamily(Font font){
        setMaskFontFamily((font != null) ? font.getFamily() : null);
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public String getMaskFontName(String defaultValue);
    /**
     * 
     * @return 
     */
    public default String getMaskFontName(){
        return getMaskFontName(null);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskFontName(String value);
    /**
     * 
     * @param font 
     */
    public default void setMaskFontName(Font font){
        setMaskFontName((font != null) ? font.getName() : null);
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public default Font getMaskFont(Font defaultValue){
            // This gets the font to be returned
        Font font = null;
            // Get the name for the font family for the font to return, 
        String familyName = getMaskFontFamily(null);    // defaulting to null
            // If a non-null font family name was retrieved
        if (familyName != null){
                // Get the font family with the name
            FontFamily family = FontFamilies.getInstance().get(familyName);
                // If there is actually a font family with that name
            if (family != null){
                    // Get the logical name for the font, defaulting to null
                String fontName = getMaskFontName(null);
                    // Get an iterator to go through the fonts in the font 
                    // family to find a matching font
                Iterator<Font> fontItr = family.iterator();
                    // This gets the first font in the iterator, used as a 
                    // fall-back in case the font name does not match any of the 
                Font firstFont = null;      // fonts
                    // While the iterator still has elements and the font with 
                    // the matching name has not been found
                while (fontItr.hasNext() && font == null){
                        // Get the current font
                    Font temp = fontItr.next();
                        // If the stored font name is null (use the first font 
                        // in the iterator) or the current font's name matches 
                        // the stored font name
                    if (fontName == null || fontName.equals(temp.getName()))
                            // Use this font
                        font = temp;
                        // If the first font is null (this will be false for all 
                        // fonts after this)
                    if (firstFont == null)
                        firstFont = temp;
                }   // If the font is still null (no font with a matching name 
                if (font == null)       // was found)
                    font = firstFont;
            }
        }   // If the font is null (the selected font was not found)
        if (font == null)
                // Use the given font
            font = defaultValue;
            // This will get the size of the font
        float size;
            // This will get the style of the font
        int style;
            // If the given default font is null
        if (defaultValue == null){
                // Get the size and style, defaulting to their respective values
            size = getMaskFontSize();
            style = getMaskFontStyle();
                // If the font is still null at this point (the selected font 
                // was not found and the given default font is null)
            if (font == null)
                    // Use Sans Serif as the font with the style and size
                return new Font(Font.SANS_SERIF,style,0).deriveFont(size);
        } else {
                // Get the size, defaulting to the size of the given font
            size = getMaskFontSize(defaultValue.getSize2D());
                // Get the style, defaulting to the style of the given font
            style = getMaskFontStyle(defaultValue.getStyle());
        }
        return font.deriveFont(style, size);
    }
    /**
     * 
     * @param value 
     */
    public default void setMaskFont(Font value){
        setMaskFontSize(value);
        setMaskFontStyle(value);
        setMaskFontFamily(value);
        setMaskFontName(value);
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public String getMaskText(String defaultValue);
    /**
     * 
     * @return 
     */
    public default String getMaskText(){
        return getMaskText("");
    }
    /**
     * 
     * @param value 
     */
    public void setMaskText(String value);
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
    public boolean isMaskImageAntialiased(boolean defaultValue);
    /**
     * 
     * @return 
     */
    public default boolean isMaskImageAntialiased(){
        return isMaskImageAntialiased(true);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskImageAntialiased(boolean value);
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
    public int getMaskAlphaIndex(int defaultValue);
    /**
     * 
     * @return 
     */
    public default int getMaskAlphaIndex(){
        return getMaskAlphaIndex(0);
    }
    /**
     * 
     * @param group 
     */
    public default void loadMaskAlphaIndex(ButtonGroup group){
        int index = getMaskAlphaIndex(-1);
        if (index < 0 || index >= group.getButtonCount())
            return;
        AbstractButton button = SwingExtendedUtilities.getButton(group, index);
        if (button != null)
            button.setSelected(true);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskAlphaIndex(int value);
    /**
     * 
     * @param group 
     */
    public default void setMaskAlphaIndex(ButtonGroup group){
        setMaskAlphaIndex(SwingExtendedUtilities.indexOfSelected(group));
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public boolean isMaskImageInverted(boolean defaultValue);
    /**
     * 
     * @return 
     */
    public default boolean isMaskImageInverted(){
        return isMaskImageInverted(false);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskImageInverted(boolean value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public int getMaskDesaturateMode(int defaultValue);
    /**
     * 
     * @return 
     */
    public default int getMaskDesaturateMode(){
        return getMaskDesaturateMode(0);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskDesaturateMode(int value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public File getMaskImageFile(File defaultValue);
    /**
     * 
     * @return 
     */
    public default File getMaskImageFile(){
        return getMaskImageFile(null);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskImageFile(File value);
    /**
     * 
     * @return 
     */
    public int getMaskImageFrameIndex();
    /**
     * 
     * @param value 
     */
    public void setMaskImageFrameIndex(int value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public int getMaskImageInterpolation(int defaultValue);
    /**
     * 
     * @return 
     */
    public default int getMaskImageInterpolation(){
        return getMaskImageInterpolation(0);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskImageInterpolation(int value);
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
     * @param defaultValue
     * @return 
     */
    public double getMaskShapeWidth(double defaultValue);
    /**
     * 
     * @return 
     */
    public default double getMaskShapeWidth(){
        return getMaskShapeWidth(0.1);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskShapeWidth(double value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public double getMaskShapeHeight(double defaultValue);
    /**
     * 
     * @return 
     */
    public default double getMaskShapeHeight(){
        return getMaskShapeHeight(0.1);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskShapeHeight(double value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public boolean isMaskShapeSizeLinked(boolean defaultValue);
    /**
     * 
     * @return 
     */
    public default boolean isMaskShapeSizeLinked(){
        return isMaskShapeSizeLinked(true);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskShapeSizeLinked(boolean value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public int getMaskShapeType(int defaultValue);
    /**
     * 
     * @return 
     */
    public default int getMaskShapeType(){
        return getMaskShapeType(0);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskShapeType(int value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public int getImageWidth(int defaultValue);
    /**
     * 
     * @return 
     */
    public default int getImageWidth(){
        return getImageWidth(SpiralGenerator.DEFAULT_SPIRAL_WIDTH);
    }
    /**
     * 
     * @param value 
     */
    public void setImageWidth(int value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public int getImageHeight(int defaultValue);
    /**
     * 
     * @return 
     */
    public default int getImageHeight(){
        return getImageHeight(SpiralGenerator.DEFAULT_SPIRAL_HEIGHT);
    }
    /**
     * 
     * @param value 
     */
    public void setImageHeight(int value);
}
