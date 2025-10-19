/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package spiral.config;

/**
 *
 * @author Mosblinker
 */
public interface OverlayMaskShapeSettings {
    /**
     * 
     */
    public static final String SHAPE_TYPE_KEY = "MaskShapeType";
    /**
     * 
     */
    public static final String SHAPE_WIDTH_KEY = "Width";
    /**
     * 
     */
    public static final String SHAPE_HEIGHT_KEY = "Height";
    /**
     * 
     */
    public static final String SHAPE_LINK_SIZE_KEY = "LinkSize";
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public double getShapeWidth(double defaultValue);
    /**
     * 
     * @return 
     */
    public default double getShapeWidth(){
        return getShapeWidth(0.1);
    }
    /**
     * 
     * @param value 
     */
    public void setShapeWidth(double value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public double getShapeHeight(double defaultValue);
    /**
     * 
     * @return 
     */
    public default double getShapeHeight(){
        return getShapeHeight(0.1);
    }
    /**
     * 
     * @param value 
     */
    public void setShapeHeight(double value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public boolean isShapeSizeLinked(boolean defaultValue);
    /**
     * 
     * @return 
     */
    public default boolean isShapeSizeLinked(){
        return isShapeSizeLinked(true);
    }
    /**
     * 
     * @param value 
     */
    public void setShapeSizeLinked(boolean value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public int getShapeType(int defaultValue);
    /**
     * 
     * @return 
     */
    public default int getShapeType(){
        return getShapeType(0);
    }
    /**
     * 
     * @param value 
     */
    public void setShapeType(int value);
}
