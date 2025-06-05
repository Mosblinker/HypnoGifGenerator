/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.painter;

import geom.GeometryMath;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
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
            // If the thickness is less than or equal to zero or the thickness 
            // is greater than or equal to 1
        if (thickness <= 0.0 || thickness >= 1.0){
                // Set the color to use
            g.setColor(model.blend(thickness));
                // Fill the area
            fillArea(g,width,height);
            return;
        }   // If there is no foreground color
        if (SpiralGeneratorUtilities.hasNoColor(model.getColor2())){
                // Invert the thickness
            thickness = 1.0-thickness;
                // Shift it by 180 degrees
            GeometryMath.boundDegrees(angle+HALF_CIRCLE_DEGREES);
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
        }   // This is the shape to use to render the concentric shapes
        RectangularShape shape;
            // Determine the shape to use for the spiral
        switch (getShape()){
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
                shape = rect;
                break;
            default:
                    // If the scratch ellipse object is null
                if (ellipse == null)
                    ellipse = new Ellipse2D.Double();
                shape = ellipse;
        }   // Bound the angle
        angle = GeometryMath.boundDegrees(angle);
            // Get the amount by which to increase the radiuses of the shapes by
        double m = radius / 2.0;
            // Get the line width for the shapes
        double lineWidth = thickness * m;
            // Get half the line width
        double halfWidth = lineWidth / 2.0;
            // Set the stroke to use the line width
        g.setStroke(new BasicStroke((float)lineWidth));
            // Get the maximum radius for any of the shapes
        double r1 = getMaximumRadius(width,height,model)+lineWidth;
            // Get the radius for the first shape
        double startR = m * (angle / FULL_CIRCLE_DEGREES);
            // If the spiral is going counter-clockwise
        if (!clockwise)
                // Shift the spiral by half
            startR = (startR + (m/2.0)) % m;
            // If the starting radius is for a shape that would be less than the 
            // line width in size
        if (startR <= halfWidth || startR > m - halfWidth){
                // If the starting radius is for a shape that is just starting 
                // to form
            if (startR > halfWidth)
                startR -= m;
                // Get the radius for the first shape
            double r = startR+halfWidth;
                // Set the frame for the shape with the radius
            shape.setFrameFromCenter(centerX, centerY, centerX+r, centerY+r);
                // Fill this shape
            g.fill(shape);
                // Move on to the next shape
            startR += m;
        }   // A for loop to draw the circles that make up this spiral
        for (double r = startR; r <= r1; r+= m){
                // Set the frame for the shape with the current radius
            shape.setFrameFromCenter(centerX, centerY, centerX+r, centerY+r);
                // Draw the shape
            g.draw(shape);
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
