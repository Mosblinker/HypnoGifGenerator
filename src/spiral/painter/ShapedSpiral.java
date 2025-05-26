/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package spiral.painter;

/**
 *
 * @author Mosblinker
 */
public interface ShapedSpiral {
    
    public static final String SHAPE_PROPERTY_CHANGED ="ShapePropertyChanged";
    /**
     * 
     * @return 
     */
    public SpiralShape getShape();
    /**
     * 
     * @param shape
     */
    public void setShape(SpiralShape shape);
}
