/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import java.awt.*;

/**
 *
 * @author Mosblinker
 */
public class RadialFadeSpiralPainter extends SpiralPainter{
    
    public static final Color TRANSPARENT_COLOR = new Color(0x00000000, true);
    
    public RadialFadeSpiralPainter() { }
    
    public RadialFadeSpiralPainter(RadialFadeSpiralPainter painter){
        super(painter);
    }
    @Override
    protected void paintSpiral(Graphics2D g, double angle, int width,int height, 
            double centerX, double centerY, boolean clockwise, double radius, 
            double thickness) {
        
    }
    @Override
    public String getName() {
        return "Radial";
    }
}
