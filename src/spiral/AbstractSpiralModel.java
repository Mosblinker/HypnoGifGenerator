/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral;

import java.awt.Color;

/**
 *
 * @author Mosblinker
 */
public abstract class AbstractSpiralModel implements SpiralModel{
    /**
     * 
     */
    protected AbstractSpiralModel(){}
    @Override
    public Color blend(double t){
        return SpiralGeneratorUtilities.blendColor(getColor1(), getColor2(), t);
    }
    @Override
    public double getCenterX(){
        return 0.5;
    }
    @Override
    public void setCenterX(double x){
        throw new UnsupportedOperationException("Position cannot be changed");
    }
    @Override
    public double getCenterY(){
        return 0.5;
    }
    @Override
    public void setCenterY(double y){
        throw new UnsupportedOperationException("Position cannot be changed");
    }
    @Override
    public void setCenter(double x, double y){
            // If the x-coordinate is out of range
        if (x < 0.0 || x > 1.0)
            throw new IllegalArgumentException("Center X out of range");
            // If the y-coordinate is out of range
        if (y < 0.0 || y > 1.0)
            throw new IllegalArgumentException("Center Y out of range");
        setCenterX(x);
        setCenterY(y);
    }
}
