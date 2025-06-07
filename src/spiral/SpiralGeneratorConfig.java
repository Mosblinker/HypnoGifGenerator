/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral;

import config.ConfigUtilities;
import geom.GeometryMath;
import io.github.dheid.fontchooser.FontFamilies;
import io.github.dheid.fontchooser.FontFamily;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.io.File;
import java.util.*;
import java.util.prefs.Preferences;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import spiral.painter.SpiralPainter;
import utils.SwingExtendedUtilities;

/**
 *
 * @author Mosblinker
 */
public class SpiralGeneratorConfig {
    /**
     * 
     */
    public static final String PROGRAM_BOUNDS_KEY = "ProgramBounds";
    /**
     * 
     */
    public static final String FILE_CHOOSER_SIZE_KEY_SUFFIX = "FileChooserSize";
    /**
     * 
     */
    public static final String FILE_CHOOSER_CURRENT_DIRECTORY_KEY_SUFFIX = 
            "CurrentDirectory";
    /**
     * 
     */
    public static final String FILE_CHOOSER_SELECTED_FILE_KEY_SUFFIX = 
            "SelectedFile";
    /**
     * 
     */
    public static final String COMPONENT_SIZE_KEY = "Size";
    
    public static final String SPIRAL_NODE_NAME = "Spiral";
    
    public static final String MASK_NODE_NAME = "Mask";
    
    public static final String TEST_SPIRAL_NODE_NAME = "DebugTest";
    
    public static final String SPIRAL_RADIUS_KEY = "Radius";
    
    public static final String SPIRAL_BASE_KEY = "Base";
    
    public static final String SPIRAL_THICKNESS_KEY = "Thickness";
    
    public static final String SPIRAL_CLOCKWISE_KEY = "Clockwise";
    
    public static final String SPIN_CLOCKWISE_KEY = "SpinClockwise";
    
    public static final String SPIRAL_COLOR_KEY_PREFIX = "SpiralColor";
    
    public static final String SPIRAL_TYPE_KEY = "SpiralType";
    /**
     * 
     */
    public static final String ALWAYS_SCALE_KEY = "AlwaysScale";
    
    public static final String IMAGE_WIDTH_KEY = "ImageWidth";
    
    public static final String IMAGE_HEIGHT_KEY = "ImageHeight";
    
    public static final String MASK_TEXT_ANTIALIASING_KEY = "TextAntialiasing";
    
    public static final String MASK_IMAGE_ANTIALIASING_KEY = "ImageAntialiasing";
    
    public static final String MASK_LINE_SPACING_KEY = "LineSpacing";
    
    public static final String MASK_FONT_SIZE_KEY = "FontSize";
    
    public static final String MASK_FONT_STYLE_KEY = "FontStyle";
    
    public static final String MASK_FONT_FAMILY_KEY = "FontFamily";
    
    public static final String MASK_FONT_NAME_KEY = "FontName";
    
    public static final String MASK_TEXT_KEY = "MaskText";
    
    public static final String MASK_FONT_SELECTOR_NAME = "MaskFontSelector";
    
    public static final String MASK_SCALE_KEY = "MaskScale";
    
    public static final String MASK_TYPE_KEY = "MaskType";
    
    public static final String MASK_ALPHA_CHANNEL_INDEX_KEY = "MaskAlphaChannelIndex";
    
    public static final String MASK_IMAGE_INVERT_KEY = "MaskImageInvert";
    
    public static final String MASK_DESATURATE_MODE_KEY = "MaskDesaturateMode";
    
    public static final String MASK_IMAGE_FILE_KEY = "MaskImageFile";
    
    public static final String MASK_SHAPE_WIDTH_KEY = "MaskShapeWidth";
    
    public static final String MASK_SHAPE_HEIGHT_KEY = "MaskShapeHeight";
    
    public static final String MASK_SHAPE_LINK_SIZE_KEY = "MaskShapeLinkSize";
    
    public static final String TEST_SPIRAL_IMAGE_KEY = "TestImage";
    
    public static final String TEST_SPIRAL_ROTATION_KEY = "TestRotation";
    
    public static final String TEST_SPIRAL_SCALE_KEY = "TestScale";
    
    public static final String FRAME_DURATION_KEY = "FrameDuration";
    /**
     * This is a preference node to store the settings for this program.
     */
    private final Preferences node;
    
    private final Preferences spiralNode;
    
    private final Preferences maskNode;
    
    private Preferences testDebugNode = null;
    /**
     * 
     */
    private final Map<Component, String> compNames;
    /**
     * 
     * @param node 
     */
    public SpiralGeneratorConfig(Preferences node) {
        this.node = Objects.requireNonNull(node);
        spiralNode = node.node(SPIRAL_NODE_NAME);
        maskNode = node.node(MASK_NODE_NAME);
        compNames = new HashMap<>();
    }
    /**
     * 
     * @return 
     */
    public Preferences getPreferences(){
        return node;
    }
    /**
     * 
     * @return 
     */
    public Preferences getSpiralPreferences(){
        return spiralNode;
    }
    /**
     * 
     * @return 
     */
    public Map<Component, String> getComponentNames(){
        return compNames;
    }
    /**
     * 
     * @param comp
     * @param name
     * @return 
     */
    public String setComponentName(Component comp, String name){
        return compNames.put(comp, name);
    }
    /**
     * 
     * @param comp
     * @return 
     */
    public String getComponentName(Component comp){
        return compNames.getOrDefault(comp, comp.getName());
    }
    /**
     * 
     * @param key
     * @param defaultValue
     * @return 
     */
    public Dimension getDimension(String key, Dimension defaultValue){
        return ConfigUtilities.dimensionFromByteArray(node.getByteArray(key, 
                null), defaultValue);
    }
    /**
     * 
     * @param key
     * @return 
     */
    public Dimension getDimension(String key){
        return getDimension(key,null);
    }
    /**
     * 
     * @param key
     * @param width
     * @param height 
     */
    public void putDimension(String key, int width, int height){
        node.putByteArray(key, ConfigUtilities.dimensionToByteArray(width, height));
    }
    /**
     * 
     * @param key
     * @param dim 
     */
    public void putDimension(String key, Dimension dim){
            // If the dimension object is null
        if (dim == null)
                // Remove the key
            node.remove(key);
        else 
            putDimension(key,dim.width,dim.height);
    }
    /**
     * 
     * @param key
     * @param comp 
     */
    public void putDimension(String key, Component comp){
        putDimension(key,comp.getWidth(),comp.getHeight());
    }
    /**
     * 
     * @param key
     * @param defaultValue
     * @return 
     */
    public Rectangle getRectangle(String key, Rectangle defaultValue){
        return ConfigUtilities.rectangleFromByteArray(node.getByteArray(key, 
                null), defaultValue);
    }
    /**
     * 
     * @param key
     * @return 
     */
    public Rectangle getRectangle(String key){
        return getRectangle(key,null);
    }
    /**
     * 
     * @param key
     * @param x
     * @param y
     * @param width
     * @param height 
     */
    public void putRectangle(String key, int x, int y, int width, int height){
        node.putByteArray(key, ConfigUtilities.rectangleToByteArray(x, y, 
                width, height));
    }
    /**
     * 
     * @param key
     * @param width
     * @param height 
     */
    public void putRectangle(String key, int width, int height){
        putRectangle(key,0,0,width,height);
    }
    /**
     * 
     * @param key
     * @param value 
     */
    public void putRectangle(String key, Rectangle value){
            // If the rectangle object is null
        if (value == null)
                // Remove the key
            node.remove(key);
        else 
            putRectangle(key,value.x,value.y,value.width,value.height);
    }
    /**
     * 
     * @param key
     * @param comp 
     */
    public void putRectangle(String key, Component comp){
        putRectangle(key,comp.getX(),comp.getY(),comp.getWidth(),comp.getHeight());
    }
    /**
     * 
     * @param node
     * @param key
     * @param defaultFile
     * @return 
     */
    protected File getFile(Preferences node, String key, File defaultFile){
            // Get the name of the file from the preference node, or null
        String name = node.get(key, null);
            // If there is no value set for that key
        if (name == null)
            return defaultFile;
        return new File(name);
    }
    /**
     * 
     * @param key
     * @param defaultFile
     * @return 
     */
    public File getFile(String key, File defaultFile){
        return getFile(getPreferences(),key,defaultFile);
    }
    /**
     * 
     * @param key
     * @return 
     */
    public File getFile(String key){
        return getFile(key,null);
    }
    /**
     * 
     * @param node
     * @param key
     * @param value 
     */
    protected void putFile(Preferences node, String key, File value){
        if (value == null)
            node.remove(key);
        else
            node.put(key, value.toString());
    }
    /**
     * 
     * @param key
     * @param value 
     */
    public void putFile(String key, File value){
        putFile(getPreferences(),key,value);
    }
    /**
     * This returns the color mapped to the given key in the preference node, or 
     * null if no color is mapped to that key.
     * @param key The key to get the associated color for.
     * @param defaultValue
     * @return The color associated with the given key in the preference node, 
     * or null if no color is associated with the given key.
     * @throws IllegalStateException If the preference node is not available, 
     * either due to not being available when the program started up or due to 
     * the preference node being removed.
     * @throws IllegalArgumentException If the key contains the null control 
     * character.
     */
    public Color getColor(String key, Color defaultValue){
        return ConfigUtilities.colorFromString(node.get(key,null),defaultValue);
    }
    
    public Color getColor(String key){
        return getColor(key,null);
    }
    /**
     * This maps the given key to the given color in the preference node. If the 
     * given color is null, then the key will be removed.
     * @param key The key to map the color to.
     * @param color The color to map to the key.
     * @throws IllegalStateException If the preference node is not available, 
     * either due to not being available when the program started up or due to 
     * the preference node being removed.
     * @throws IllegalArgumentException If the key either contains the null 
     * control character or is too long to be stored in the preference node. 
     * @throws NullPointerException If the key is null.
     */
    public void putColor(String key, Color color){
        if (color != null)  // If the color is not null
            node.put(key,ConfigUtilities.colorToString(color));
        else
            node.remove(key);
    }
    /**
     * 
     * @return 
     */
    public Rectangle getProgramBounds(){
        return getRectangle(PROGRAM_BOUNDS_KEY);
    }
    /**
     * 
     * @param comp
     * @return 
     */
    public Rectangle getProgramBounds(SpiralGenerator comp){
        Rectangle rect = getProgramBounds();
        SwingExtendedUtilities.setComponentBounds(comp, rect);
        return rect;
    }
    /**
     * 
     * @param comp 
     */
    public void setProgramBounds(SpiralGenerator comp){
        putRectangle(PROGRAM_BOUNDS_KEY,comp);
    }
    /**
     * 
     * @param fc
     * @param defaultValue
     * @return 
     */
    public File getSelectedFile(JFileChooser fc, File defaultValue){
        return getFile(getComponentName(fc)+FILE_CHOOSER_SELECTED_FILE_KEY_SUFFIX,
                defaultValue);
    }
    /**
     * 
     * @param fc
     * @return 
     */
    public File getSelectedFile(JFileChooser fc){
        return getSelectedFile(fc,null);
    }
    /**
     * 
     * @param fc
     * @param file 
     */
    public void setSelectedFile(JFileChooser fc, File file){
        putFile(getComponentName(fc)+FILE_CHOOSER_SELECTED_FILE_KEY_SUFFIX,file);
    }
    /**
     * 
     * @param fc 
     */
    public void setSelectedFile(JFileChooser fc){
        setSelectedFile(fc,fc.getSelectedFile());
    }
    /**
     * 
     * @param fc
     * @return 
     */
    public Dimension getFileChooserSize(JFileChooser fc){
        return getDimension(getComponentName(fc)+FILE_CHOOSER_SIZE_KEY_SUFFIX);
    }
    /**
     * 
     * @param fc
     */
    public void setFileChooserSize(JFileChooser fc){
        putDimension(getComponentName(fc)+FILE_CHOOSER_SIZE_KEY_SUFFIX,fc);
    }
    /**
     * 
     * @param fc
     * @return 
     */
    public File getCurrentDirectory(JFileChooser fc){
        return getFile(getComponentName(fc)+FILE_CHOOSER_CURRENT_DIRECTORY_KEY_SUFFIX);
    }
    /**
     * 
     * @param fc 
     */
    public void setCurrentDirectory(JFileChooser fc){
        putFile(getComponentName(fc)+FILE_CHOOSER_CURRENT_DIRECTORY_KEY_SUFFIX,
                fc.getCurrentDirectory());
    }
    /**
     * 
     * @param fc 
     */
    public void storeFileChooser(JFileChooser fc){
            // Put the file chooser's size in the preference node
        setFileChooserSize(fc);
            // Put the file chooser's current directory in the preference node
        setCurrentDirectory(fc);
    }
    /**
     * 
     * @param fc 
     */
    public void loadFileChooser(JFileChooser fc){
            // Get the current directory for the file chooser
        File dir = getCurrentDirectory(fc);
            // If there is a current directory for the file chooser and it exists
        if (dir != null && dir.exists()){
                // Set the file chooser's current directory
            fc.setCurrentDirectory(dir);
        }   // Get the selected file for the file chooser, or null
        File file = getSelectedFile(fc);
            // If there is a selected file for the file chooser
        if (file != null){
                // Select that file in the file chooser
            fc.setSelectedFile(file);
        }   // Load the file chooser's size from the preference node
        SwingExtendedUtilities.setComponentSize(fc,getFileChooserSize(fc));
    }
    /**
     * 
     * @param comp
     * @return 
     */
    public Dimension getComponentSize(Component comp){
        return getDimension(getComponentName(comp)+COMPONENT_SIZE_KEY);
    }
    /**
     * 
     * @param comp
     * @param key
     * @return 
     */
    public Dimension loadComponentSize(Component comp, String key){
        Dimension dim = getDimension(key);
        SwingExtendedUtilities.setComponentSize(comp,dim);
        return dim;
    }
    /**
     * 
     * @param comp
     * @return 
     */
    public Dimension loadComponentSize(Component comp){
        Dimension dim = getComponentSize(comp);
        SwingExtendedUtilities.setComponentSize(comp,dim);
        return dim;
    }
    /**
     * 
     * @param comp 
     */
    public void setComponentSize(Component comp){
        putDimension(getComponentName(comp)+COMPONENT_SIZE_KEY,comp);
    }
    
    /**
     * 
     * @param key
     * @return 
     */
    public byte[] getSpiralData(String key){
        return getSpiralPreferences().getByteArray(key, null);
    }
    /**
     * 
     * @param painter
     * @return 
     */
    public byte[] getSpiralData(SpiralPainter painter){
        return getSpiralData(painter.getPreferenceKey());
    }
    /**
     * 
     * @param key
     * @param data 
     */
    public void setSpiralData(String key, byte[] data){
        getSpiralPreferences().putByteArray(key, data);
    }
    /**
     * 
     * @param painter
     * @param data 
     */
    public void setSpiralData(SpiralPainter painter, byte[] data){
        setSpiralData(painter.getPreferenceKey(),data);
    }
    /**
     * 
     * @param painter 
     */
    public void setSpiralData(SpiralPainter painter){
        setSpiralData(painter,painter.toByteArray());
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public boolean isSpinClockwise(boolean defaultValue){
        return getPreferences().getBoolean(SPIN_CLOCKWISE_KEY, defaultValue);
    }
    /**
     * 
     * @return 
     */
    public boolean isSpinClockwise(){
        return isSpinClockwise(true);
    }
    /**
     * 
     * @param value 
     */
    public void setSpinClockwise(boolean value){
        getPreferences().putBoolean(SPIN_CLOCKWISE_KEY, value);
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public int getSpiralType(int defaultValue){
        return getPreferences().getInt(SPIRAL_TYPE_KEY, defaultValue);
    }
    /**
     * 
     * @return 
     */
    public int getSpiralType(){
        return getSpiralType(0);
    }
    /**
     * 
     * @param value 
     */
    public void setSpiralType(int value){
        getPreferences().putInt(SPIRAL_TYPE_KEY, value);
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public int getFrameDuration(int defaultValue){
        return getPreferences().getInt(FRAME_DURATION_KEY, defaultValue);
    }
    /**
     * 
     * @return 
     */
    public int getFrameDuration(){
        return getFrameDuration(20);
    }
    /**
     * 
     * @param value 
     */
    public void setFrameDuration(int value){
        getPreferences().putInt(FRAME_DURATION_KEY, value);
    }
    /**
     * 
     * @param index
     * @param defaultValue
     * @return 
     */
    public Color getSpiralColor(int index, Color defaultValue){
        return getColor(SPIRAL_COLOR_KEY_PREFIX+index,defaultValue);
    }
    /**
     * 
     * @param index
     * @return 
     */
    public Color getSpiralColor(int index){
        return getSpiralColor(index,null);
    }
    /**
     * 
     * @param index
     * @param value 
     */
    public void setSpiralColor(int index, Color value){
        putColor(SPIRAL_COLOR_KEY_PREFIX+index,value);
    }
    /**
     * 
     * @return 
     */
    public boolean isImageAlwaysScaled(){
        return node.getBoolean(ALWAYS_SCALE_KEY, false);
    }
    /**
     * 
     * @param value 
     */
    public void setImageAlwaysScaled(boolean value){
        node.putBoolean(ALWAYS_SCALE_KEY, value);
    }
    
    public Preferences getMaskPreferences(){
        return maskNode;
    }
    
    public boolean isMaskTextAntialiased(boolean defaultValue){
        return getMaskPreferences().getBoolean(MASK_TEXT_ANTIALIASING_KEY, defaultValue);
    }
    
    public boolean isMaskTextAntialiased(){
        return isMaskTextAntialiased(true);
    }
    
    public void setMaskTextAntialiased(boolean value){
        getMaskPreferences().putBoolean(MASK_TEXT_ANTIALIASING_KEY, value);
    }
    
    public double getMaskLineSpacing(double defaultValue){
        return getMaskPreferences().getDouble(MASK_LINE_SPACING_KEY, defaultValue);
    }
    
    public double getMaskLineSpacing(){
        return getMaskLineSpacing(0.0);
    }
    
    public void setMaskLineSpacing(double value){
        getMaskPreferences().putDouble(MASK_LINE_SPACING_KEY, value);
    }
    
    public float getMaskFontSize(float defaultValue){
        return getMaskPreferences().getFloat(MASK_FONT_SIZE_KEY, defaultValue);
    }
    
    public float getMaskFontSize(){
        return getMaskFontSize(11.0f);
    }
    
    public void setMaskFontSize(float value){
        getMaskPreferences().putFloat(MASK_FONT_SIZE_KEY, value);
    }
    
    public void setMaskFontSize(Font font){
        if (font == null)
            getMaskPreferences().remove(MASK_FONT_SIZE_KEY);
        else
            setMaskFontSize(font.getSize2D());
    }
    
    public int getMaskFontStyle(int defaultValue){
        return getMaskPreferences().getInt(MASK_FONT_STYLE_KEY, defaultValue);
    }
    
    public int getMaskFontStyle(){
        return getMaskFontStyle(Font.PLAIN);
    }
    
    public void setMaskFontStyle(int value){
        getMaskPreferences().putInt(MASK_FONT_STYLE_KEY, value);
    }
    
    public void setMaskFontStyle(Font font){
        if (font == null)
            getMaskPreferences().remove(MASK_FONT_STYLE_KEY);
        else
            setMaskFontStyle(font.getStyle());
    }
    
    protected boolean getMaskFontStyleValue(int flag){
        return (getMaskFontStyle() & flag) != 0;
    }
    
    protected void setMaskFontStyleValue(int flag, boolean value){
        int style = getMaskFontStyle();
        if (value)
            style |= flag;
        else
            style &= ~flag;
        setMaskFontStyle(style);
    }
    
    public boolean isMaskFontBold(){
        return getMaskFontStyleValue(Font.BOLD);
    }
    
    public void setMaskFontBold(boolean value){
        setMaskFontStyleValue(Font.BOLD,value);
    }
    
    public boolean isMaskFontItalic(){
        return getMaskFontStyleValue(Font.ITALIC);
    }
    
    public void setMaskFontItalic(boolean value){
        setMaskFontStyleValue(Font.ITALIC,value);
    }
    
    public void setMaskFontStyle(boolean bold, boolean italic){
            // Get the style, but without the bold and italic flags
        int value = getMaskFontStyle() & ~(Font.BOLD | Font.ITALIC);
            // If the font's style is bold
        if (bold)
            value |= Font.BOLD;
            // If the font's style is italic
        if (italic)
            value |= Font.ITALIC;
        setMaskFontStyle(value);
    }
    
    public String getMaskFontFamily(String defaultValue){
        return getMaskPreferences().get(MASK_FONT_FAMILY_KEY, defaultValue);
    }
    
    public String getMaskFontFamily(){
        return getMaskFontFamily(Font.SANS_SERIF);
    }
    
    public void setMaskFontFamily(String value){
        if (value == null)
            getMaskPreferences().remove(MASK_FONT_FAMILY_KEY);
        else
            getMaskPreferences().put(MASK_FONT_FAMILY_KEY, value);
    }
    
    public void setMaskFontFamily(Font font){
        setMaskFontFamily((font != null) ? font.getFamily() : null);
    }
    
    public String getMaskFontName(String defaultValue){
        return getMaskPreferences().get(MASK_FONT_NAME_KEY, defaultValue);
    }
    
    public String getMaskFontName(){
        return getMaskFontName(null);
    }
    
    public void setMaskFontName(String value){
        if (value == null)
            getMaskPreferences().remove(MASK_FONT_NAME_KEY);
        else
            getMaskPreferences().put(MASK_FONT_NAME_KEY, value);
    }
    
    public void setMaskFontName(Font font){
        setMaskFontName((font != null) ? font.getName() : null);
    }
    
    public Font getMaskFont(Font defaultValue){
            // This gets the font to be returned
        Font font = null;
            // Get the name for the font family for the font to return, 
        String familyName = getMaskFontFamily(null);    // defaulting to null
            // If a non-null font family name was retrieved
        if (familyName != null){
                // Get the font family with the name
            FontFamily family = FontFamilies.getInstance().get(familyName);
                // If there is actually a font family with that name
            if (family != null){
                    // Get the logical name for the font, defaulting to null
                String fontName = getMaskFontName(null);
                    // Get an iterator to go through the fonts in the font 
                    // family to find a matching font
                Iterator<Font> fontItr = family.iterator();
                    // This gets the first font in the iterator, used as a 
                    // fall-back in case the font name does not match any of the 
                Font firstFont = null;      // fonts
                    // While the iterator still has elements and the font with 
                    // the matching name has not been found
                while (fontItr.hasNext() && font == null){
                        // Get the current font
                    Font temp = fontItr.next();
                        // If the stored font name is null (use the first font 
                        // in the iterator) or the current font's name matches 
                        // the stored font name
                    if (fontName == null || fontName.equals(temp.getName()))
                            // Use this font
                        font = temp;
                        // If the first font is null (this will be false for all 
                        // fonts after this)
                    if (firstFont == null)
                        firstFont = temp;
                }   // If the font is still null (no font with a matching name 
                if (font == null)       // was found)
                    font = firstFont;
            }
        }   // If the font is null (the selected font was not found)
        if (font == null)
                // Use the given font
            font = defaultValue;
            // This will get the size of the font
        float size;
            // This will get the style of the font
        int style;
            // If the given default font is null
        if (defaultValue == null){
                // Get the size and style, defaulting to their respective values
            size = getMaskFontSize();
            style = getMaskFontStyle();
                // If the font is still null at this point (the selected font 
                // was not found and the given default font is null)
            if (font == null)
                    // Use Sans Serif as the font with the style and size
                return new Font(Font.SANS_SERIF,style,0).deriveFont(size);
        } else {
                // Get the size, defaulting to the size of the given font
            size = getMaskFontSize(defaultValue.getSize2D());
                // Get the style, defaulting to the style of the given font
            style = getMaskFontStyle(defaultValue.getStyle());
        }
        return font.deriveFont(style, size);
    }
    
    public void setMaskFont(Font value){
        setMaskFontSize(value);
        setMaskFontStyle(value);
        setMaskFontFamily(value);
        setMaskFontName(value);
    }
    
    public String getMaskText(String defaultValue){
        return getMaskPreferences().get(MASK_TEXT_KEY, defaultValue);
    }
    
    public String getMaskText(){
        return getMaskText("");
    }
    
    public void setMaskText(String value){
        if (value == null)
            getMaskPreferences().remove(MASK_TEXT_KEY);
        else
            getMaskPreferences().put(MASK_TEXT_KEY, value);
    }
    /**
     * 
     * @return 
     */
    public Dimension getMaskFontSelectorSize(){
        return getDimension(MASK_FONT_SELECTOR_NAME+COMPONENT_SIZE_KEY);
    }
    /**
     * 
     * @param comp
     * @return 
     */
    public Dimension loadMaskFontSelectorSize(Component comp){
        Dimension dim = getMaskFontSelectorSize();
        SwingExtendedUtilities.setComponentSize(comp,dim);
        return dim;
    }
    /**
     * 
     * @param width
     * @param height
     */
    public void setMaskFontSelectorSize(int width, int height){
        putDimension(MASK_FONT_SELECTOR_NAME+COMPONENT_SIZE_KEY,width,height);
    }
    /**
     * 
     * @param dim 
     */
    public void setMaskFontSelectorSize(Dimension dim){
        putDimension(MASK_FONT_SELECTOR_NAME+COMPONENT_SIZE_KEY,dim);
    }
    /**
     * 
     * @param comp 
     */
    public void setMaskFontSelectorSize(Component comp){
        putDimension(MASK_FONT_SELECTOR_NAME+COMPONENT_SIZE_KEY,comp);
    }
    
    public double getMaskScale(double defaultValue){
        return getMaskPreferences().getDouble(MASK_SCALE_KEY, defaultValue);
    }
    
    public double getMaskScale(){
        return getMaskScale(1.0);
    }
    
    public void setMaskScale(double value){
        getMaskPreferences().putDouble(MASK_SCALE_KEY, value);
    }
    
    public boolean isMaskImageAntialiased(boolean defaultValue){
        return getMaskPreferences().getBoolean(MASK_IMAGE_ANTIALIASING_KEY, defaultValue);
    }
    
    public boolean isMaskImageAntialiased(){
        return isMaskImageAntialiased(true);
    }
    
    public void setMaskImageAntialiased(boolean value){
        getMaskPreferences().putBoolean(MASK_IMAGE_ANTIALIASING_KEY, value);
    }
    
    public int getMaskType(int defaultValue){
        return getMaskPreferences().getInt(MASK_TYPE_KEY, defaultValue);
    }
    
    public int getMaskType(){
        return getMaskType(0);
    }
    
    public void setMaskType(int value){
        getMaskPreferences().putInt(MASK_TYPE_KEY, value);
    }
    
    public int getMaskAlphaIndex(int defaultValue){
        return getMaskPreferences().getInt(MASK_ALPHA_CHANNEL_INDEX_KEY, defaultValue);
    }
    
    public int getMaskAlphaIndex(){
        return getMaskAlphaIndex(0);
    }
    
    public void loadMaskAlphaIndex(ButtonGroup group){
        int index = getMaskAlphaIndex(-1);
        if (index < 0 || index >= group.getButtonCount())
            return;
        AbstractButton button = SwingExtendedUtilities.getButton(group, index);
        if (button != null)
            button.setSelected(true);
    }
    
    public void setMaskAlphaIndex(int value){
        getMaskPreferences().putInt(MASK_ALPHA_CHANNEL_INDEX_KEY, value);
    }
    
    public void setMaskAlphaIndex(ButtonGroup group){
        setMaskAlphaIndex(SwingExtendedUtilities.indexOfSelected(group));
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public boolean isMaskImageInverted(boolean defaultValue){
        return getMaskPreferences().getBoolean(MASK_IMAGE_INVERT_KEY, defaultValue);
    }
    /**
     * 
     * @return 
     */
    public boolean isMaskImageInverted(){
        return isMaskImageInverted(false);
    }
    /**
     * 
     * @param value 
     */
    public void setMaskImageInverted(boolean value){
        getMaskPreferences().putBoolean(MASK_IMAGE_INVERT_KEY, value);
    }
    
    public int getMaskDesaturateMode(int defaultValue){
        return getMaskPreferences().getInt(MASK_DESATURATE_MODE_KEY, defaultValue);
    }
    
    public int getMaskDesaturateMode(){
        return getMaskDesaturateMode(0);
    }
    
    public void setMaskDesaturateMode(int value){
        getMaskPreferences().putInt(MASK_DESATURATE_MODE_KEY, value);
    }
    
    public File getMaskImageFile(File defaultValue){
        return getFile(getMaskPreferences(),MASK_IMAGE_FILE_KEY,defaultValue);
    }
    
    public File getMaskImageFile(){
        return getMaskImageFile(null);
    }
    
    public void setMaskImageFile(File value){
        putFile(getMaskPreferences(),MASK_IMAGE_FILE_KEY,value);
    }
    
    public double getMaskShapeWidth(double defaultValue){
        return getMaskPreferences().getDouble(MASK_SHAPE_WIDTH_KEY, defaultValue);
    }
    
    public double getMaskShapeWidth(){
        return getMaskShapeWidth(0.1);
    }
    
    public void setMaskShapeWidth(double value){
        getMaskPreferences().putDouble(MASK_SHAPE_WIDTH_KEY, value);
    }
    
    public double getMaskShapeHeight(double defaultValue){
        return getMaskPreferences().getDouble(MASK_SHAPE_HEIGHT_KEY, defaultValue);
    }
    
    public double getMaskShapeHeight(){
        return getMaskShapeHeight(0.1);
    }
    
    public void setMaskShapeHeight(double value){
        getMaskPreferences().putDouble(MASK_SHAPE_HEIGHT_KEY, value);
    }
    
    public boolean isMaskShapeSizeLinked(boolean defaultValue){
        return getMaskPreferences().getBoolean(MASK_SHAPE_LINK_SIZE_KEY, defaultValue);
    }
    
    public boolean isMaskShapeSizeLinked(){
        return isMaskShapeSizeLinked(true);
    }
    
    public void setMaskShapeSizeLinked(boolean value){
        getMaskPreferences().putBoolean(MASK_SHAPE_LINK_SIZE_KEY, value);
    }
    
    public int getImageWidth(int defaultValue){
        return getPreferences().getInt(IMAGE_WIDTH_KEY, defaultValue);
    }
    
    public int getImageWidth(){
        return getImageWidth(SpiralGenerator.DEFAULT_SPIRAL_WIDTH);
    }
    
    public void setImageWidth(int value){
        getPreferences().putInt(IMAGE_WIDTH_KEY, value);
    }
    
    public int getImageHeight(int defaultValue){
        return getPreferences().getInt(IMAGE_HEIGHT_KEY, defaultValue);
    }
    
    public int getImageHeight(){
        return getImageHeight(SpiralGenerator.DEFAULT_SPIRAL_HEIGHT);
    }
    
    public void setImageHeight(int value){
        getPreferences().putInt(IMAGE_HEIGHT_KEY, value);
    }
    
    public Preferences getDebugTestNode(){
        if (testDebugNode == null)
            testDebugNode = getPreferences().node(TEST_SPIRAL_NODE_NAME);
        return testDebugNode;
    }
    
    public int getDebugTestImage(int size){
        return Math.max(Math.min(getDebugTestNode().getInt(TEST_SPIRAL_IMAGE_KEY, 0), size-1), -1);
    }
    
    public void setDebugTestImage(int value){
        getDebugTestNode().putInt(TEST_SPIRAL_IMAGE_KEY, value);
    }
    
    public double getDebugTestRotation(){
        return Math.max(Math.min(
                getDebugTestNode().getDouble(TEST_SPIRAL_ROTATION_KEY, 0), 
                GeometryMath.FULL_CIRCLE_DEGREES), 0);
    }
    
    public void setDebugTestRotation(double value){
        getDebugTestNode().putDouble(TEST_SPIRAL_ROTATION_KEY, value);
    }
    
    public double getDebugTestScale(){
        return Math.max(getDebugTestNode().getDouble(TEST_SPIRAL_SCALE_KEY, 1), 0);
    }
    
    public void setDebugTestScale(double value){
        getDebugTestNode().putDouble(TEST_SPIRAL_SCALE_KEY, value);
    }
    
    public double getDebugTestDouble(int index){
        return getDebugTestNode().getDouble("testDouble"+index, 0);
    }
    
    public void setDebugTestDouble(int index, double value){
        getDebugTestNode().putDouble("testDouble"+index, value);
    }
    
    public int getDebugTestInteger(int index){
        return getDebugTestNode().getInt("testInteger"+index, 0);
    }
    
    public void setDebugTestInteger(int index, int value){
        getDebugTestNode().putInt("testInteger"+index, value);
    }
    
    public String getDebugTestString(int index){
        return getDebugTestNode().get("testString"+index, null);
    }
    
    public void setDebugTestString(int index, String value){
        getDebugTestNode().put("testString"+index, value);
    }
    
    public boolean getDebugTestBoolean(int index, boolean defaultValue){
        return getDebugTestNode().getBoolean("testBoolean"+index, defaultValue);
    }
    
    public boolean getDebugTestBoolean(int index){
        return getDebugTestBoolean(index,false);
    }
    
    public void setDebugTestBoolean(int index, boolean value){
        getDebugTestNode().putBoolean("testBoolean"+index, value);
    }
}
