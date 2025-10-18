/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package spiral.config;

/**
 *
 * @author Mosblinker
 */
public interface OverlayMaskTextSettings extends TextOverlayMaskSettings{
    /**
     * 
     */
    public static final String TEXT_KEY = "Text";
    /**
     * 
     */
    public static final String LINE_SPACING_KEY = "LineSpacing";
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public double getLineSpacing(double defaultValue);
    /**
     * 
     * @return 
     */
    public default double getLineSpacing(){
        return getLineSpacing(0.0);
    }
    /**
     * 
     * @param value 
     */
    public void setLineSpacing(double value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public String getText(String defaultValue);
    /**
     * 
     * @return 
     */
    public default String getText(){
        return getText("");
    }
    /**
     * 
     * @param value 
     */
    public void setText(String value);
}
