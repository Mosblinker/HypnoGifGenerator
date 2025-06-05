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
    
    public ImmutableSpiralModel(Color color1, Color color2, double angle){
        this.color1 = Objects.requireNonNull(color1);
        this.color2 = Objects.requireNonNull(color2);
        this.rotation = angle;
    }
    
    public ImmutableSpiralModel(SpiralModel model){
        this(model.getColor1(),model.getColor2(),model.getRotation());
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
}
