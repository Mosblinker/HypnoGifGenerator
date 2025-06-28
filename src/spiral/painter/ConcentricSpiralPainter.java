/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import geom.GeometryMath;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.*;
import java.nio.ByteBuffer;
import spiral.SpiralGeneratorUtilities;
import spiral.SpiralModel;

/**
 *
 * @author Mosblinker
 */
public class ConcentricSpiralPainter extends SpiralPainter implements ShapedSpiral{
    /**
     * This is a scratch Ellipse2D object used to draw the circles. This is 
     * initially null and is initialized the first time it is used. 
     */
    private Ellipse2D ellipse = null;
    /**
     * This is a scratch Path2D object used to draw shapes. This is initially 
     * null and is initialized the first time it is used. 
     */
    private Path2D path = null;
    /**
     * This is the shape for the concentric shapes for this spiral.
     */
    private SpiralShape shape = SpiralShape.CIRCLE;
    
    public ConcentricSpiralPainter() { }
    
    public ConcentricSpiralPainter(ConcentricSpiralPainter painter){
        super(painter);
        this.shape = painter.shape;
    }
    @Override
    protected void paintSpiral(Graphics2D g, SpiralModel model, double angle, 
            int width,int height, double centerX, double centerY, 
            boolean clockwise, double radius, double thickness) {
            // If there is no foreground color
        if (SpiralGeneratorUtilities.hasNoColor(model.getColor2())){
                // Invert the thickness
            thickness = 1.0-thickness;
                // Shift it by 180 degrees
            angle += HALF_CIRCLE_DEGREES;
                // Set the color to use to the background color
            g.setColor(model.getColor1());
        } else {
                // If there is a background color
            if (!SpiralGeneratorUtilities.hasNoColor(model.getColor1())){
                    // Set the color to use to the background color
                g.setColor(model.getColor1());
                    // Fill the area
                fillArea(g,width,height);
            }   // Set the color to use to the foreground color
            g.setColor(model.getColor2());
        }   // Bound the angle
        angle = GeometryMath.boundDegrees(angle);
            // Get the line width for the shapes
        double lineWidth = thickness * radius;
            // Get half the line width
        double halfWidth = lineWidth / 2.0;
            // Get the maximum radius for any of the shapes
        double r1 = getMaximumRadius(width,height,model)+lineWidth;
            // Get the radius for the first shape
        double startR = radius * (angle / FULL_CIRCLE_DEGREES);
            // If the spiral is going counter-clockwise
        if (!clockwise)
                // Shift the spiral by half
            startR = (startR + (radius/2.0)) % radius;
            // This is the shape to use to render the concentric shapes
        RectangularShape rectShape;
            // Determine the shape to use for the spiral
        switch (getShape()){
                // If the shape is a heart
            case HEART:
                    // If the scratch rectangle object is null
                if (rect == null)
                    rect = new Rectangle2D.Double();
                    // Make the maximum radius the larger of the width and height,
                    // adjusted by the center x and y
                r1 = Math.max(width*(1+Math.abs(model.getCenterX()-0.5)*2), 
                        height*(1+Math.abs(model.getCenterY()-0.5)*2));
                    // Adjust the maximum radius to account for the start radius
                r1 -= ((r1 - startR) % radius);
                    // Go through the radiuses for the shapes
                for (double r = r1; r+halfWidth > 0; r-=radius){
                        // Get the radius plus half the line width, so that the 
                        // shape is the correct size
                    double rTemp = r+halfWidth;
                        // Create the shape and get the area of it
                    Area area = new Area(createShape(centerX,centerY,rTemp,
                            getShape(),rect));
                        // Subtract the line width to get the area to clear
                    rTemp -= lineWidth;
                        // If there is a part of the shape that will be cleared
                    if (rTemp > 0)
                            // Create the area that will be empty and remove it 
                            // from the area
                        area.subtract(new Area(createShape(centerX,centerY,
                                rTemp,getShape(),rect)));
                        // Fill the area for the shape
                    g.fill(area);
                }
                return;
                // If the shape is a diamond
            case DIAMOND:
                    // Rotate the painted area by 45 degrees to turn the squares 
                    // into diamonds
                g.rotate(Math.PI/4.0, centerX, centerY);
                // If the shape is square
            case SQUARE:
                    // If the scratch rectangle object is null
                if (rect == null)
                    rect = new Rectangle2D.Double();
                rectShape = rect;
                break;
            default:
                    // If the scratch ellipse object is null
                if (ellipse == null)
                    ellipse = new Ellipse2D.Double();
                rectShape = ellipse;
        }
            // Set the stroke to use the line width
        g.setStroke(new BasicStroke((float)lineWidth));
            // If the starting radius is for a shape that would be less than the 
            // line width in size
        if (startR <= halfWidth || startR > radius - halfWidth){
                // If the starting radius is for a shape that is just starting 
                // to form
            if (startR > halfWidth)
                startR -= radius;
                // Get the radius for the first shape
            double r = startR+halfWidth;
                // Set the frame for the shape with the radius
            rectShape.setFrameFromCenter(centerX, centerY, centerX+r, centerY+r);
                // Fill this shape
            g.fill(rectShape);
                // Move on to the next shape
            startR += radius;
        }   // A for loop to draw the circles that make up this spiral
        for (double r = startR; r <= r1; r+= radius){
                // Set the frame for the shape with the current radius
            rectShape.setFrameFromCenter(centerX, centerY, centerX+r, centerY+r);
                // Draw the shape
            g.draw(rectShape);
        }
    }
    /**
     * 
     * @param centerX
     * @param centerY
     * @param r
     * @param shape
     * @param rect
     * @return 
     */
    protected Shape createShape(double centerX, double centerY, double r, 
            SpiralShape shape, Rectangle2D rect){
            // Set the frame for the shape with the given radius
        rect.setFrameFromCenter(centerX, centerY, centerX+r, centerY+r);
            // Determine what shape to produce
        switch(shape){
                // If the shape is a heart
            case HEART:
                    // Create the heart shape
                return path = SpiralGeneratorUtilities.getHeartShape(rect, path);
            default:
                return rect;
        }
    }
    @Override
    public SpiralShape getShape() {
        return shape;
    }
    @Override
    public void setShape(SpiralShape shape) {
        if (this.shape != shape){
            SpiralShape old = this.shape;
            this.shape = shape;
            firePropertyChange(SHAPE_PROPERTY_CHANGED,old,shape);
        }
    }
    @Override
    public String getName() {
        return "Concentric Shapes";
    }
    @Override
    public ConcentricSpiralPainter clone() {
        return new ConcentricSpiralPainter(this);
    }
    @Override
    protected int getByteArrayLength(){
        return super.getByteArrayLength()+Integer.BYTES;
    }
    @Override
    protected void toByteArray(ByteBuffer buffer){
        super.toByteArray(buffer);
        buffer.putInt(SpiralShape.getShapeIndex(getShape()));
    }
    @Override
    protected void fromByteArray(ByteBuffer buffer){
        super.fromByteArray(buffer);
        SpiralShape[] shapes = SpiralShape.values();
        setShape(shapes[Math.min(Math.max(buffer.getInt(), 0), 
                shapes.length-1)]);
    }
    @Override
    public void reset(){
        super.reset();
        setShape(SpiralShape.CIRCLE);
    }
    @Override
    protected String paramString(){
        return super.paramString()+
                ",shape="+getShape();
    }
}
