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
    
    private double x;
    
    private double y;
    
    public DefaultSpiralModel(Color color1, Color color2, double angle,
            double x, double y){
        this.color1 = Objects.requireNonNull(color1);
        this.color2 = Objects.requireNonNull(color2);
        this.rotation = angle;
        DefaultSpiralModel.this.setCenterX(x);
        DefaultSpiralModel.this.setCenterY(y);
    }
    
    public DefaultSpiralModel(Color color1, Color color2, double angle){
        this(color1,color2,angle,0.5,0.5);
    }
    
    public DefaultSpiralModel(Color color1, Color color2, double x, double y){
        this(color1,color2,0.0,x,y);
    }
    
    public DefaultSpiralModel(Color color1, Color color2){
        this(color1,color2,0.0);
    }
    
    public DefaultSpiralModel(double angle, double x, double y){
        this(Color.WHITE,Color.BLACK,angle,x,y);
    }
    
    public DefaultSpiralModel(double angle){
        this(angle,0.5,0.5);
    }
    
    public DefaultSpiralModel(double x, double y){
        this(0.0,x,y);
    }
    
    public DefaultSpiralModel(){
        this(0.0);
    }
    
    public DefaultSpiralModel(SpiralModel model){
        this(model.getColor1(),model.getColor2(),model.getRotation(),
                model.getCenterX(),model.getCenterY());
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
    @Override
    public double getCenterX(){
        return x;
    }
    @Override
    public void setCenterX(double x){
        if (x < 0.0 || x > 1.0)
            throw new IllegalArgumentException("Center X out of range");
        this.x = x;
    }
    @Override
    public double getCenterY(){
        return y;
    }
    @Override
    public void setCenterY(double y){
        if (y < 0.0 || y > 1.0)
            throw new IllegalArgumentException("Center Y out of range");
        this.y = y;
    }
}
