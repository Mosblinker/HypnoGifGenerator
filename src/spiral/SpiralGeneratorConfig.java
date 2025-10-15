/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral;

import config.ConfigUtilities;
import geom.GeometryMath;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.*;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import utils.SwingExtendedUtilities;

/**
 *
 * @author Mosblinker
 */
public class SpiralGeneratorConfig implements SpiralGeneratorSettings{
    /**
     * This is the configuration key for the bounds of the program.
     */
    public static final String PROGRAM_BOUNDS_KEY = "ProgramBounds";
    /**
     * 
     */
    public static final String FILE_CHOOSER_SIZE_KEY = "FileChooserSize";
    /**
     * 
     */
    public static final String FILE_CHOOSER_CURRENT_DIRECTORY_KEY = 
            "CurrentDirectory";
    /**
     * 
     */
    public static final String FILE_CHOOSER_SELECTED_FILE_KEY = 
            "SelectedFile";
    /**
     * 
     */
    public static final String FILE_CHOOSER_FILE_FILTER_KEY = 
            "FileFilter";
    /**
     * 
     */
    public static final String COMPONENT_SIZE_KEY = "Size";
    
    public static final String SPIRAL_NODE_NAME = "Spiral";
    
    public static final String MASK_NODE_NAME = "Mask";
    
    public static final String TEST_SPIRAL_NODE_NAME = "DebugTest";
    
    @Deprecated
    public static final String SPIRAL_RADIUS_KEY = "Radius";
    
    @Deprecated
    public static final String SPIRAL_BASE_KEY = "Base";
    
    @Deprecated
    public static final String SPIRAL_THICKNESS_KEY = "Thickness";
    
    @Deprecated
    public static final String SPIRAL_CLOCKWISE_KEY = "Clockwise";
    
    @Deprecated
    public static final String SPIN_CLOCKWISE_KEY = "SpinClockwise";
    /**
     * 
     */
    public static final String ALWAYS_SCALE_KEY = "AlwaysScale";
    
    public static final String OPTIMIZE_FOR_DIFFERENCE_KEY = "OptimizeForDifference";
    
    public static final String MASK_FONT_SELECTOR_NAME = "MaskFontSelector";
    
    public static final String CHECK_FOR_UPDATES_AT_START_KEY = "CheckForUpdatesAtStartup";

    
    
    public static final String TEST_SPIRAL_IMAGE_KEY = "TestImage";
    
    public static final String TEST_SPIRAL_ROTATION_KEY = "TestRotation";
    
    public static final String TEST_SPIRAL_SCALE_KEY = "TestScale";
    
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
     */
    private final Map<JFileChooser, Preferences> fcNodes;
    /**
     * 
     * @param node 
     */
    public SpiralGeneratorConfig(Preferences node) {
        this.node = Objects.requireNonNull(node);
        spiralNode = node.node(SPIRAL_NODE_NAME);
        maskNode = node.node(MASK_NODE_NAME);
        compNames = new HashMap<>();
        fcNodes = new HashMap<>();
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
     * @return 
     */
    public Map<JFileChooser, Preferences> getFileChooserPreferenceMap(){
        return fcNodes;
    }
    /**
     * 
     * @param fc
     * @param name
     */
    public void addFileChooser(JFileChooser fc, String name){
        if (!fcNodes.containsKey(fc))
            fcNodes.put(fc, getPreferences().node(name));
    }
    /**
     * 
     * @param fc
     * @return 
     */
    public Preferences getFileChooserPreferences(JFileChooser fc){
        return fcNodes.get(fc);
    }
    /**
     * 
     * @param key
     * @param defaultValue
     * @return 
     */
    public Dimension getDimension(String key, Dimension defaultValue){
        return ConfigUtilities.getDimension(node, key, defaultValue);
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
        ConfigUtilities.putDimension(node, key, width, height);
    }
    /**
     * 
     * @param key
     * @param dim 
     */
    public void putDimension(String key, Dimension dim){
        ConfigUtilities.putDimension(node, key, dim);
    }
    /**
     * 
     * @param key
     * @param comp 
     */
    public void putDimension(String key, Component comp){
        ConfigUtilities.putDimension(node, key, comp);
    }
    /**
     * 
     * @param key
     * @param defaultValue
     * @return 
     */
    public Rectangle getRectangle(String key, Rectangle defaultValue){
        return ConfigUtilities.getRectangle(node, key, defaultValue);
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
        ConfigUtilities.putRectangle(node, key, x, y, width, height);
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
        ConfigUtilities.putRectangle(node, key, value);
    }
    /**
     * 
     * @param key
     * @param comp 
     */
    public void putRectangle(String key, Component comp){
        ConfigUtilities.putRectangle(node, key, comp);
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
        return getFile(getFileChooserPreferences(fc),
                FILE_CHOOSER_SELECTED_FILE_KEY,defaultValue);
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
        putFile(getFileChooserPreferences(fc),FILE_CHOOSER_SELECTED_FILE_KEY,file);
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
        return ConfigUtilities.getDimension(getFileChooserPreferences(fc),
                FILE_CHOOSER_SIZE_KEY,null);
    }
    /**
     * 
     * @param fc
     */
    public void setFileChooserSize(JFileChooser fc){
        ConfigUtilities.putDimension(getFileChooserPreferences(fc),
                FILE_CHOOSER_SIZE_KEY,fc);
    }
    /**
     * 
     * @param fc
     * @return 
     */
    public File getCurrentDirectory(JFileChooser fc){
        return getFile(getFileChooserPreferences(fc),
                FILE_CHOOSER_CURRENT_DIRECTORY_KEY,null);
    }
    /**
     * 
     * @param fc 
     */
    public void setCurrentDirectory(JFileChooser fc){
        putFile(getFileChooserPreferences(fc),FILE_CHOOSER_CURRENT_DIRECTORY_KEY,
                fc.getCurrentDirectory());
    }
    /**
     * 
     * @param fc
     * @return 
     */
    public FileFilter getFileFilter(JFileChooser fc){
        return ConfigUtilities.getFileFilter(getFileChooserPreferences(fc), fc, 
                FILE_CHOOSER_FILE_FILTER_KEY, null);
    }
    /**
     * 
     * @param fc
     * @param filter 
     */
    public void setFileFilter(JFileChooser fc, FileFilter filter){
        ConfigUtilities.putFileFilter(getFileChooserPreferences(fc), fc, 
                FILE_CHOOSER_FILE_FILTER_KEY, filter);
    }
    /**
     * 
     * @param fc 
     */
    public void setFileFilter(JFileChooser fc){
        setFileFilter(fc,fc.getFileFilter());
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
            // Get the file filter for the file chooser
        FileFilter filter = getFileFilter(fc);
            // If there is a file filter selected
        if (filter != null)
            fc.setFileFilter(filter);
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
    
    @Override
    public byte[] getSpiralData(String key){
        return getSpiralPreferences().getByteArray(key, null);
    }
    @Override
    public void setSpiralData(String key, byte[] data){
        getSpiralPreferences().putByteArray(key, data);
    }
    /**
     * 
     * @param defaultValue
     * @return 
     * @deprecated 
     */
    @Deprecated
    public boolean isSpinClockwise(boolean defaultValue){
        return getPreferences().getBoolean(SPIN_CLOCKWISE_KEY, defaultValue);
    }
    /**
     * 
     * @return 
     * @deprecated 
     */
    @Deprecated
    public boolean isSpinClockwise(){
        return isSpinClockwise(true);
    }
    /**
     * 
     * @param value 
     * @deprecated 
     */
    @Deprecated
    public void setSpinClockwise(boolean value){
        getPreferences().putBoolean(SPIN_CLOCKWISE_KEY, value);
    }
    @Override
    public int getSpiralType(int defaultValue){
        return getPreferences().getInt(SPIRAL_TYPE_KEY, defaultValue);
    }
    @Override
    public void setSpiralType(int value){
        getPreferences().putInt(SPIRAL_TYPE_KEY, value);
    }
    @Override
    public int getFrameDuration(int defaultValue){
        return getPreferences().getInt(FRAME_DURATION_KEY, defaultValue);
    }
    @Override
    public void setFrameDuration(int value){
        getPreferences().putInt(FRAME_DURATION_KEY, value);
    }
    @Override
    public Color getSpiralColor(int index, Color defaultValue){
        return getColor(SPIRAL_COLOR_KEY_PREFIX+index,defaultValue);
    }
    @Override
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
    /**
     * 
     * @return 
     */
    public boolean isOptimizedForDifference(){
        return node.getBoolean(OPTIMIZE_FOR_DIFFERENCE_KEY, false);
    }
    /**
     * 
     * @param value 
     */
    public void setOptimizedForDifference(boolean value){
        node.putBoolean(OPTIMIZE_FOR_DIFFERENCE_KEY, value);
    }
    
    public Preferences getMaskPreferences(){
        return maskNode;
    }
    @Override
    public boolean isMaskTextAntialiased(boolean defaultValue){
        return getMaskPreferences().getBoolean(MASK_TEXT_ANTIALIASING_KEY, defaultValue);
    }
    @Override
    public void setMaskTextAntialiased(boolean value){
        getMaskPreferences().putBoolean(MASK_TEXT_ANTIALIASING_KEY, value);
    }
    @Override
    public double getMaskLineSpacing(double defaultValue){
        return getMaskPreferences().getDouble(MASK_LINE_SPACING_KEY, defaultValue);
    }
    @Override
    public void setMaskLineSpacing(double value){
        getMaskPreferences().putDouble(MASK_LINE_SPACING_KEY, value);
    }
    @Override
    public float getMaskFontSize(float defaultValue){
        return getMaskPreferences().getFloat(MASK_FONT_SIZE_KEY, defaultValue);
    }
    @Override
    public void setMaskFontSize(Float value){
        if (value == null)
            getMaskPreferences().remove(MASK_FONT_SIZE_KEY);
        else
            getMaskPreferences().putFloat(MASK_FONT_SIZE_KEY, value);
    }
    @Override
    public int getMaskFontStyle(int defaultValue){
        return getMaskPreferences().getInt(MASK_FONT_STYLE_KEY, defaultValue);
    }
    @Override
    public void setMaskFontStyle(Integer value){
        if (value == null)
            getMaskPreferences().remove(MASK_FONT_STYLE_KEY);
        else
            getMaskPreferences().putInt(MASK_FONT_STYLE_KEY, value);
    }
    @Override
    public String getMaskFontFamily(String defaultValue){
        return getMaskPreferences().get(MASK_FONT_FAMILY_KEY, defaultValue);
    }
    @Override
    public void setMaskFontFamily(String value){
        if (value == null)
            getMaskPreferences().remove(MASK_FONT_FAMILY_KEY);
        else
            getMaskPreferences().put(MASK_FONT_FAMILY_KEY, value);
    }
    @Override
    public String getMaskFontName(String defaultValue){
        return getMaskPreferences().get(MASK_FONT_NAME_KEY, defaultValue);
    }
    @Override
    public void setMaskFontName(String value){
        if (value == null)
            getMaskPreferences().remove(MASK_FONT_NAME_KEY);
        else
            getMaskPreferences().put(MASK_FONT_NAME_KEY, value);
    }
    @Override
    public String getMaskText(String defaultValue){
        return getMaskPreferences().get(MASK_TEXT_KEY, defaultValue);
    }
    @Override
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
    @Override
    public double getMaskScale(double defaultValue){
        return getMaskPreferences().getDouble(MASK_SCALE_KEY, defaultValue);
    }
    @Override
    public void setMaskScale(double value){
        getMaskPreferences().putDouble(MASK_SCALE_KEY, value);
    }
    @Override
    public boolean isMaskImageAntialiased(boolean defaultValue){
        return getMaskPreferences().getBoolean(MASK_IMAGE_ANTIALIASING_KEY, defaultValue);
    }
    @Override
    public void setMaskImageAntialiased(boolean value){
        getMaskPreferences().putBoolean(MASK_IMAGE_ANTIALIASING_KEY, value);
    }
    @Override
    public int getMaskType(int defaultValue){
        return getMaskPreferences().getInt(MASK_TYPE_KEY, defaultValue);
    }
    @Override
    public void setMaskType(int value){
        getMaskPreferences().putInt(MASK_TYPE_KEY, value);
    }
    @Override
    public int getMaskAlphaIndex(int defaultValue){
        return getMaskPreferences().getInt(MASK_ALPHA_CHANNEL_INDEX_KEY, defaultValue);
    }
    @Override
    public void setMaskAlphaIndex(int value){
        getMaskPreferences().putInt(MASK_ALPHA_CHANNEL_INDEX_KEY, value);
    }
    @Override
    public boolean isMaskImageInverted(boolean defaultValue){
        return getMaskPreferences().getBoolean(MASK_IMAGE_INVERT_KEY, defaultValue);
    }
    @Override
    public void setMaskImageInverted(boolean value){
        getMaskPreferences().putBoolean(MASK_IMAGE_INVERT_KEY, value);
    }
    @Override
    public int getMaskDesaturateMode(int defaultValue){
        return getMaskPreferences().getInt(MASK_DESATURATE_MODE_KEY, defaultValue);
    }
    @Override
    public void setMaskDesaturateMode(int value){
        getMaskPreferences().putInt(MASK_DESATURATE_MODE_KEY, value);
    }
    @Override
    public File getMaskImageFile(File defaultValue){
        return getFile(getMaskPreferences(),MASK_IMAGE_FILE_KEY,defaultValue);
    }
    @Override
    public void setMaskImageFile(File value){
        putFile(getMaskPreferences(),MASK_IMAGE_FILE_KEY,value);
    }
    @Override
    public int getMaskImageFrameIndex(){
        return getMaskPreferences().getInt(MASK_IMAGE_FRAME_INDEX_KEY, 0);
    }
    @Override
    public void setMaskImageFrameIndex(int value){
        getMaskPreferences().putInt(MASK_IMAGE_FRAME_INDEX_KEY, value);
    }
    @Override
    public int getMaskImageInterpolation(int defaultValue){
        return getMaskPreferences().getInt(MASK_IMAGE_INTERPOLATION_KEY, defaultValue);
    }
    @Override
    public void setMaskImageInterpolation(int value){
        getMaskPreferences().putInt(MASK_IMAGE_INTERPOLATION_KEY, value);
    }
    @Override
    public double getMaskRotation(double defaultValue){
        return getMaskPreferences().getDouble(MASK_ROTATION_KEY, defaultValue);
    }
    @Override
    public void setMaskRotation(double value){
        getMaskPreferences().putDouble(MASK_ROTATION_KEY, value);
    }
    @Override
    public int getMaskFlags(){
        return getMaskPreferences().getInt(MASK_FLAGS_KEY, 0);
    }
    @Override
    public void setMaskFlags(int value){
        getMaskPreferences().putInt(MASK_FLAGS_KEY, value);
    }
    @Override
    public double getMaskShapeWidth(double defaultValue){
        return getMaskPreferences().getDouble(MASK_SHAPE_WIDTH_KEY, defaultValue);
    }
    @Override
    public void setMaskShapeWidth(double value){
        getMaskPreferences().putDouble(MASK_SHAPE_WIDTH_KEY, value);
    }
    @Override
    public double getMaskShapeHeight(double defaultValue){
        return getMaskPreferences().getDouble(MASK_SHAPE_HEIGHT_KEY, defaultValue);
    }
    @Override
    public void setMaskShapeHeight(double value){
        getMaskPreferences().putDouble(MASK_SHAPE_HEIGHT_KEY, value);
    }
    @Override
    public boolean isMaskShapeSizeLinked(boolean defaultValue){
        return getMaskPreferences().getBoolean(MASK_SHAPE_LINK_SIZE_KEY, defaultValue);
    }
    @Override
    public void setMaskShapeSizeLinked(boolean value){
        getMaskPreferences().putBoolean(MASK_SHAPE_LINK_SIZE_KEY, value);
    }
    @Override
    public int getMaskShapeType(int defaultValue){
        return getMaskPreferences().getInt(MASK_SHAPE_TYPE_KEY, defaultValue);
    }
    @Override
    public void setMaskShapeType(int value){
        getMaskPreferences().putInt(MASK_SHAPE_TYPE_KEY, value);
    }
    
    public boolean getCheckForUpdateAtStartup(boolean defaultValue){
        return getPreferences().getBoolean(CHECK_FOR_UPDATES_AT_START_KEY, defaultValue);
    }
    
    public boolean getCheckForUpdateAtStartup(){
        return getCheckForUpdateAtStartup(true);
    }
    
    public void setCheckForUpdateAtStartup(boolean value){
        getPreferences().putBoolean(CHECK_FOR_UPDATES_AT_START_KEY, value);
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
    @Override
    public Dimension getImageSize(Dimension defaultValue) {
        return getDimension(IMAGE_SIZE_KEY,defaultValue);
    }
    @Override
    public void setImageSize(int width, int height) {
        putDimension(IMAGE_SIZE_KEY,width,height);
    }
    @Override
    public void setImageSize(Dimension value) {
        putDimension(IMAGE_SIZE_KEY,value);
    }
    @Override
    public boolean isMaskWordAntialiased(boolean defaultValue) {
        return getMaskPreferences().getBoolean(MASK_WORD_ANTIALIASING_KEY, defaultValue);
    }
    @Override
    public void setMaskWordAntialiased(boolean value) {
        getMaskPreferences().putBoolean(MASK_WORD_ANTIALIASING_KEY, value);
    }
    @Override
    public boolean getMaskWordAddBlankFrames(boolean defaultValue) {
        return getMaskPreferences().getBoolean(MASK_WORD_BLANK_FRAMES_KEY, defaultValue);
    }
    @Override
    public void setMaskWordAddBlankFrames(boolean value) {
        getMaskPreferences().putBoolean(MASK_WORD_BLANK_FRAMES_KEY, value);
    }
    @Override
    public int getMaskWordMessageCount() {
        return getMaskPreferences().getInt(MASK_WORD_MESSAGE_COUNT_KEY, 0);
    }
    @Override
    public void setMaskWordMessageCount(int value) {
        getMaskPreferences().putInt(MASK_WORD_MESSAGE_COUNT_KEY, value);
    }
    @Override
    public String getMaskWordMessage(int index) {
        return getMaskPreferences().get(MASK_WORD_MESSAGE_KEY_PREFIX+index, null);
    }
    @Override
    public void setMaskWordMessage(int index, String value) {
        String key = MASK_WORD_MESSAGE_KEY_PREFIX+index;
        if (value == null)
            getMaskPreferences().remove(key);
        else
            getMaskPreferences().put(key, value);
    }
    /**
     * 
     */
    public class FileChooserPropertyChangeListener implements PropertyChangeListener{
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
                // If the property name is null or the source is not a file 
                // chooser
            if (evt.getPropertyName() == null || !(evt.getSource() instanceof JFileChooser))
                return;
                // Determine the property that was just changed
            switch(evt.getPropertyName()){
                    // If the file filter has changed
                case(JFileChooser.FILE_FILTER_CHANGED_PROPERTY):
                        // Store the file filter in the preference node
                    setFileFilter((JFileChooser)evt.getSource());
            }
        }
    }
}
