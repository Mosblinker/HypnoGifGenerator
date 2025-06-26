/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package spiral.painter;

/**
 *
 * @author Mosblinker
 */
public enum SpiralShape {
    
    CIRCLE, 
    
    SQUARE,
    
    DIAMOND,
    
    HEART,
    
    STAR;
    /**
     * 
     * @param shape
     * @return 
     */
    public static int getShapeIndex(SpiralShape shape){
        if (shape == null)
            return -1;
        SpiralShape[] shapes = SpiralShape.values();
        for (int i = 0; i < shapes.length; i++){
            if (shapes[i] == shape)
                return i;
        }
        return -1;
    }
    
}
