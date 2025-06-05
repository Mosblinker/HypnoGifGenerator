/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral;

import java.awt.Color;
import java.util.Objects;

/**
 *
 * @author Mosblinker
 */
public class ImmutableSpiralModel implements SpiralModel{
    
    private Color color1;
    
    private Color color2;
    
    private double rotation;
    
    private double x;
    
    private double y;
    
    public ImmutableSpiralModel(Color color1, Color color2, double angle, 
            double x, double y){
        this.color1 = Objects.requireNonNull(color1);
        this.color2 = Objects.requireNonNull(color2);
        if (x < 0.0 || x > 1.0)
            throw new IllegalArgumentException("Center X out of range");
        if (y < 0.0 || y > 1.0)
            throw new IllegalArgumentException("Center y out of range");
        this.rotation = angle;
        this.x = x;
        this.y = y;
    }
    
    public ImmutableSpiralModel(Color color1, Color color2, double angle){
        this(color1,color2,angle,0.5,0.5);
    }
    
    public ImmutableSpiralModel(SpiralModel model){
        this(model.getColor1(),model.getColor2(),model.getRotation(),
                model.getCenterX(),model.getCenterY());
    }
    @Override
    public Color getColor1() {
        return color1;
    }
    @Override
    public void setColor1(Color color) {
        throw new UnsupportedOperationException("Immutable Spiral Model");
    }
    @Override
    public Color getColor2() {
        return color2;
    }
    @Override
    public void setColor2(Color color) {
        throw new UnsupportedOperationException("Immutable Spiral Model");
    }
    @Override
    public double getRotation() {
        return rotation;
    }
    @Override
    public void setRotation(double angle) {
        throw new UnsupportedOperationException("Immutable Spiral Model");
    }
    @Override
    public double getCenterX(){
        return x;
    }
    @Override
    public double getCenterY(){
        return y;
    }
}
