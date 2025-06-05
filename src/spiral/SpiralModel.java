/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package spiral;

import java.awt.Color;

/**
 *
 * @author Mosblinker
 */
public interface SpiralModel {
    
    public Color getColor1();
    
    public void setColor1(Color color);
    
    public Color getColor2();
    
    public void setColor2(Color color);
    
    public default void setColors(Color color1, Color color2){
        setColor1(color1);
        setColor2(color2);
    }
    
    public default Color blend(double t){
        return SpiralGeneratorUtilities.blendColor(getColor1(), getColor2(), t);
    }
    
    public double getRotation();
    
    public void setRotation(double angle);
    
    public default double getCenterX(){
        return 0.5;
    }
    
    public default void setCenterX(double x){
        throw new UnsupportedOperationException("Position cannot be changed");
    }
    
    public default double getCenterY(){
        return 0.5;
    }
    
    public default void setCenterY(double y){
        throw new UnsupportedOperationException("Position cannot be changed");
    }
    
    public default void setCenter(double x, double y){
        setCenterX(x);
        setCenterY(y);
    }
}
