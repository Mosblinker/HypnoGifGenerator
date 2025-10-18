/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package spiral.config;

/**
 *
 * @author Mosblinker
 */
public interface AntialiasedOverlayMaskSettings {
    /**
     * 
     */
    public static final String ANTIALIASING_KEY = "Antialiasing";
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public boolean isAntialiased(boolean defaultValue);
    /**
     * 
     * @return 
     */
    public default boolean isAntialiased(){
        return isAntialiased(true);
    }
    /**
     * 
     * @param value 
     */
    public void setAntialiased(boolean value);
}
