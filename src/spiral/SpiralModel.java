/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package spiral;

import java.awt.Color;
import java.util.Objects;

/**
 *
 * @author Mosblinker
 */
public interface SpiralModel {
    /**
     * 
     * @return 
     */
    public Color getColor1();
    /**
     * 
     * @param color 
     */
    public void setColor1(Color color);
    /**
     * 
     * @return 
     */
    public Color getColor2();
    /**
     * 
     * @param color 
     */
    public void setColor2(Color color);
    /**
     * 
     * @param color1
     * @param color2 
     */
    public default void setColors(Color color1, Color color2){
        Objects.requireNonNull(color1);
        Objects.requireNonNull(color2);
        setColor1(color1);
        setColor2(color2);
    }
    /**
     * 
     * @param t
     * @return 
     */
    public Color blend(double t);
    /**
     * 
     * @return 
     */
    public double getRotation();
    /**
     * 
     * @param angle 
     */
    public void setRotation(double angle);
    /**
     * 
     * @return 
     */
    public double getCenterX();
    /**
     * 
     * @param x 
     */
    public void setCenterX(double x);
    /**
     * 
     * @return 
     */
    public double getCenterY();
    /**
     * 
     * @param y 
     */
    public void setCenterY(double y);
    /**
     * 
     * @param x
     * @param y 
     */
    public void setCenter(double x, double y);
}
