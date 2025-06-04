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
public class DefaultSpiralModel implements SpiralModel{
    
    private Color color1;
    
    private Color color2;
    
    private double rotation;
    
    public DefaultSpiralModel(Color color1, Color color2, double angle){
        this.color1 = Objects.requireNonNull(color1);
        this.color2 = Objects.requireNonNull(color2);
        this.rotation = angle;
    }
    
    public DefaultSpiralModel(Color color1, Color color2){
        this(color1,color2,0.0);
    }
    
    public DefaultSpiralModel(double angle){
        this(Color.WHITE,Color.BLACK,angle);
    }
    
    public DefaultSpiralModel(){
        this(0.0);
    }
    
    public DefaultSpiralModel(SpiralModel model){
        this(model.getColor1(),model.getColor2(),model.getRotation());
    }

    @Override
    public Color getColor1() {
        return color1;
    }
    @Override
    public void setColor1(Color color) {
        this.color1 = color;
    }
    @Override
    public Color getColor2() {
        return color2;
    }
    @Override
    public void setColor2(Color color) {
        this.color2 = color;
    }
    @Override
    public double getRotation() {
        return rotation;
    }
    @Override
    public void setRotation(double angle) {
        this.rotation = angle;
    }
}
