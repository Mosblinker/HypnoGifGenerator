/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package spiral.config;

import java.awt.Font;
import spiral.SpiralGeneratorUtilities;

/**
 *
 * @author Mosblinker
 */
public interface TextOverlayMaskSettings extends AntialiasedOverlayMaskSettings{
    /**
     * 
     */
    public static final String FONT_SIZE_KEY = "FontSize";
    /**
     * 
     */
    public static final String FONT_STYLE_KEY = "FontStyle";
    /**
     * 
     */
    public static final String FONT_FAMILY_KEY = "FontFamily";
    /**
     * 
     */
    public static final String FONT_NAME_KEY = "FontName";
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public float getFontSize(float defaultValue);
    /**
     * 
     * @return 
     */
    public default float getFontSize(){
        return getFontSize(11.0f);
    }
    /**
     * 
     * @param value 
     */
    public void setFontSize(Float value);
    /**
     * 
     * @param font 
     */
    public default void setFontSize(Font font){
        setFontSize((font == null)?null:font.getSize2D());
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public int getFontStyle(int defaultValue);
    /**
     * 
     * @return 
     */
    public default int getFontStyle(){
        return getFontStyle(Font.PLAIN);
    }
    /**
     * 
     * @param value 
     */
    public void setFontStyle(Integer value);
    /**
     * 
     * @param font 
     */
    public default void setFontStyle(Font font){
        setFontStyle((font == null)?null:font.getStyle());
    }/**
     * 
     * @param flag
     * @return 
     */
    public default boolean getFontStyleValue(int flag){
        return SpiralGeneratorUtilities.getFlag(getFontStyle(),flag);
    }
    /**
     * 
     * @param flag
     * @param value 
     */
    public default void setFontStyleValue(int flag, boolean value){
        setFontStyle(SpiralGeneratorUtilities.setFlag(getFontStyle(),flag,value));
    }
    /**
     * 
     * @return 
     */
    public default boolean isFontBold(){
        return getFontStyleValue(Font.BOLD);
    }
    /**
     * 
     * @param value 
     */
    public default void setFontBold(boolean value){
        setFontStyleValue(Font.BOLD,value);
    }
    /**
     * 
     * @return 
     */
    public default boolean isFontItalic(){
        return getFontStyleValue(Font.ITALIC);
    }
    /**
     * 
     * @param value 
     */
    public default void setFontItalic(boolean value){
        setFontStyleValue(Font.ITALIC,value);
    }
    /**
     * 
     * @param bold
     * @param italic 
     */
    public default void setFontStyle(boolean bold, boolean italic){
            // Get the style, but without the bold and italic flags
        int value = getFontStyle() & ~(Font.BOLD | Font.ITALIC);
            // If the font's style is bold
        if (bold)
            value |= Font.BOLD;
            // If the font's style is italic
        if (italic)
            value |= Font.ITALIC;
        setFontStyle(value);
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public String getFontFamily(String defaultValue);
    /**
     * 
     * @return 
     */
    public default String getFontFamily(){
        return getFontFamily(Font.SANS_SERIF);
    }
    /**
     * 
     * @param value 
     */
    public void setFontFamily(String value);
    /**
     * 
     * @param font 
     */
    public default void setFontFamily(Font font){
        setFontFamily((font != null) ? font.getFamily() : null);
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public String getFontName(String defaultValue);
    /**
     * 
     * @return 
     */
    public default String getFontName(){
        return getFontName(null);
    }
    /**
     * 
     * @param value 
     */
    public void setFontName(String value);
    /**
     * 
     * @param font 
     */
    public default void setFontName(Font font){
        setFontName((font != null) ? font.getName() : null);
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public default Font getFont(Font defaultValue){
            // This gets the font to be returned
        Font font = SpiralGeneratorUtilities.getFont(getFontFamily(null),
                getFontName(null));
            // If the font is null (the selected font was not found)
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
            size = getFontSize();
            style = getFontStyle();
                // If the font is still null at this point (the selected font 
                // was not found and the given default font is null)
            if (font == null)
                    // Use Sans Serif as the font with the style and size
                return new Font(Font.SANS_SERIF,style,0).deriveFont(size);
        } else {
                // Get the size, defaulting to the size of the given font
            size = getFontSize(defaultValue.getSize2D());
                // Get the style, defaulting to the style of the given font
            style = getFontStyle(defaultValue.getStyle());
        }
        return font.deriveFont(style, size);
    }
    /**
     * 
     * @param value 
     */
    public default void setFont(Font value){
        setFontSize(value);
        setFontStyle(value);
        setFontFamily(value);
        setFontName(value);
    }
}
