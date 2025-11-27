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
