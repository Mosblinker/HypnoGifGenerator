/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral.config;

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
import spiral.SpiralGenerator;
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
    
    public static final String MASK_TEXT_NODE_NAME = "Text";
    
    public static final String MASK_IMAGE_NODE_NAME = "Image";
    
    public static final String MASK_MESSAGE_NODE_NAME = "Messages";
    
    public static final String MASK_SHAPE_NODE_NAME = "Shape";
    
    public static final String TEST_SPIRAL_NODE_NAME = "DebugTest";
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
    
    private final Preferences maskTextNode;
    
    private final OverlayMaskTextSettings maskTextConfig;
    
    private final Preferences maskImageNode;
    
    private final OverlayMaskImageSettings maskImageConfig;
    
    private final Preferences maskMessagesNode;
    
    private final OverlayMaskMessagesSettings maskMessagesConfig;
    
    private final Preferences maskShapeNode;
    
    private final OverlayMaskShapeSettings maskShapeConfig;
    
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
        maskTextNode = maskNode.node(MASK_TEXT_NODE_NAME);
        maskImageNode = maskNode.node(MASK_IMAGE_NODE_NAME);
        maskMessagesNode = maskNode.node(MASK_MESSAGE_NODE_NAME);
        maskShapeNode = maskNode.node(MASK_SHAPE_NODE_NAME);
        compNames = new HashMap<>();
        fcNodes = new HashMap<>();
        maskTextConfig = new OverlayMaskTextSettingsImpl();
        maskImageConfig = new OverlayMaskImageSettingsImpl();
        maskMessagesConfig = new OverlayMaskMessagesSettingsImpl();
        maskShapeConfig = new OverlayMaskShapeSettingsImpl();
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
    /**
     * 
     * @param value 
     */
    public void setOptimizedForDifference(boolean value){
        node.putBoolean(OPTIMIZE_FOR_DIFFERENCE_KEY, value);
    }
    /**
     * 
     * @return 
     */
    public Preferences getMaskPreferences(){
        return maskNode;
    }
    /**
     * 
     * @return 
     */
    public Preferences getMaskTextPreferences(){
        return maskTextNode;
    }
    /**
     * 
     * @return 
     */
    public Preferences getMaskImagePreferences(){
        return maskImageNode;
    }
    /**
     * 
     * @return 
     */
    public Preferences getMaskMessagesPreferences(){
        return maskMessagesNode;
    }
    /**
     * 
     * @return 
     */
    public Preferences getMaskShapePreferences(){
        return maskShapeNode;
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
    public int getMaskType(int defaultValue){
        return getMaskPreferences().getInt(MASK_TYPE_KEY, defaultValue);
    }
    @Override
    public void setMaskType(int value){
        getMaskPreferences().putInt(MASK_TYPE_KEY, value);
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
    public OverlayMaskTextSettings getMaskTextSettings() {
        return maskTextConfig;
    }
    @Override
    public OverlayMaskImageSettings getMaskImageSettings() {
        return maskImageConfig;
    }
    @Override
    public OverlayMaskMessagesSettings getMaskMessageSettings() {
        return maskMessagesConfig;
    }
    @Override
    public OverlayMaskShapeSettings getMaskShapeSettings(){
        return maskShapeConfig;
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
    /**
     * 
     */
    private abstract class AntialiasedOverlayMaskSettingsImpl implements 
            AntialiasedOverlayMaskSettings{
        
        protected abstract Preferences getNode();
        @Override
        public boolean isAntialiased(boolean defaultValue) {
            return getNode().getBoolean(ANTIALIASING_KEY, defaultValue);
        }
        @Override
        public void setAntialiased(boolean value) {
            getNode().putBoolean(ANTIALIASING_KEY, value);
        }
    }
    /**
     * 
     */
    private abstract class TextOverlayMaskSettingsImpl extends 
            AntialiasedOverlayMaskSettingsImpl implements TextOverlayMaskSettings{
        @Override
        public float getFontSize(float defaultValue) {
            return getNode().getFloat(FONT_SIZE_KEY, defaultValue);
        }
        @Override
        public void setFontSize(Float value) {
            if (value == null)
                getNode().remove(FONT_SIZE_KEY);
            else
                getNode().putFloat(FONT_SIZE_KEY, value);
        }
        @Override
        public int getFontStyle(int defaultValue) {
            return getNode().getInt(FONT_STYLE_KEY, defaultValue);
        }
        @Override
        public void setFontStyle(Integer value) {
            if (value == null)
                getNode().remove(FONT_STYLE_KEY);
            else
                getNode().putInt(FONT_STYLE_KEY, value);
        }
        @Override
        public String getFontFamily(String defaultValue) {
            return getNode().get(FONT_FAMILY_KEY, defaultValue);
        }
        @Override
        public void setFontFamily(String value) {
            if (value == null)
                getNode().remove(FONT_FAMILY_KEY);
            else
                getNode().put(FONT_FAMILY_KEY, value);
        }
        @Override
        public String getFontName(String defaultValue) {
            return getNode().get(FONT_NAME_KEY, defaultValue);
        }
        @Override
        public void setFontName(String value) {
            if (value == null)
                getNode().remove(FONT_NAME_KEY);
            else
                getNode().put(FONT_NAME_KEY, value);
        }
        @Override
        public double getLineSpacing(double defaultValue) {
            return getNode().getDouble(LINE_SPACING_KEY, defaultValue);
        }
        @Override
        public void setLineSpacing(double value) {
            getNode().putDouble(LINE_SPACING_KEY, value);
        }
    }
    /**
     * 
     */
    private class OverlayMaskTextSettingsImpl extends TextOverlayMaskSettingsImpl 
            implements OverlayMaskTextSettings{
        @Override
        protected Preferences getNode() {
            return getMaskTextPreferences();
        }
        @Override
        public String getText(String defaultValue) {
            return getNode().get(TEXT_KEY, defaultValue);
        }
        @Override
        public void setText(String value) {
            if (value == null)
                getNode().remove(TEXT_KEY);
            else
                getNode().put(TEXT_KEY, value);
        }
    }
    /**
     * 
     */
    private class OverlayMaskImageSettingsImpl 
            extends AntialiasedOverlayMaskSettingsImpl 
            implements OverlayMaskImageSettings{
        @Override
        protected Preferences getNode() {
            return getMaskImagePreferences();
        }
        @Override
        public int getAlphaIndex(int defaultValue) {
            return getNode().getInt(ALPHA_CHANNEL_INDEX_KEY, defaultValue);
        }
        @Override
        public void setAlphaIndex(int value) {
            getNode().putInt(ALPHA_CHANNEL_INDEX_KEY, value);
        }
        @Override
        public boolean isImageInverted(boolean defaultValue) {
            return getNode().getBoolean(IMAGE_INVERT_KEY, defaultValue);
        }
        @Override
        public void setImageInverted(boolean value) {
            getNode().putBoolean(IMAGE_INVERT_KEY, value);
        }
        @Override
        public int getDesaturateMode(int defaultValue) {
            return getNode().getInt(DESATURATE_MODE_KEY, defaultValue);
        }
        @Override
        public void setDesaturateMode(int value) {
            getNode().putInt(DESATURATE_MODE_KEY, value);
        }
        @Override
        public File getImageFile(File defaultValue) {
            return getFile(getNode(),IMAGE_FILE_KEY,defaultValue);
        }
        @Override
        public void setImageFile(File value) {
            putFile(getNode(),IMAGE_FILE_KEY,value);
        }
        @Override
        public int getImageFrameIndex() {
            return getNode().getInt(IMAGE_FRAME_INDEX_KEY, 0);
        }
        @Override
        public void setImageFrameIndex(int value) {
            getNode().putInt(IMAGE_FRAME_INDEX_KEY, value);
        }
        @Override
        public int getImageInterpolation(int defaultValue) {
            return getNode().getInt(IMAGE_INTERPOLATION_KEY, defaultValue);
        }
        @Override
        public void setImageInterpolation(int value) {
            getNode().putInt(IMAGE_INTERPOLATION_KEY, value);
        }
    }
    /**
     * 
     */
    private class OverlayMaskMessagesSettingsImpl 
            extends TextOverlayMaskSettingsImpl
            implements OverlayMaskMessagesSettings{
        @Override
        protected Preferences getNode() {
            return getMaskMessagesPreferences();
        }
        @Override
        public boolean getAddBlankFrames(boolean defaultValue) {
            return getNode().getBoolean(ADD_BLANK_FRAMES_KEY, defaultValue);
        }
        @Override
        public void setAddBlankFrames(boolean value) {
            getNode().putBoolean(ADD_BLANK_FRAMES_KEY, value);
        }
        @Override
        public int getMessageCount() {
            return getNode().getInt(MESSAGE_COUNT_KEY, 0);
        }
        @Override
        public void setMessageCount(int value) {
            getNode().putInt(MESSAGE_COUNT_KEY, value);
        }
        @Override
        public String getMessage(int index) {
            return getNode().get(MESSAGE_KEY_PREFIX+index, null);
        }
        @Override
        public void setMessage(int index, String value) {
            String key = MESSAGE_KEY_PREFIX+index;
            if (value == null)
                getNode().remove(key);
            else
                getNode().put(key, value);
        }
        @Override
        public String getPrompt() {
            return getNode().get(PROMPT_KEY, null);
        }
        @Override
        public void setPrompt(String value) {
            if (value == null)
                getNode().remove(PROMPT_KEY);
            else
                getNode().put(PROMPT_KEY, value);
        }
        @Override
        public boolean getAlwaysShowPrompt(boolean defaultValue) {
            return getNode().getBoolean(ALWAYS_SHOW_PROMPT_KEY, defaultValue);
        }
        @Override
        public void setAlwaysShowPrompt(boolean value) {
            getNode().putBoolean(ALWAYS_SHOW_PROMPT_KEY, value);
        }
    }
    /**
     * 
     */
    private class OverlayMaskShapeSettingsImpl extends 
            AntialiasedOverlayMaskSettingsImpl implements OverlayMaskShapeSettings{
        @Override
        protected Preferences getNode() {
            return getMaskShapePreferences();
        }
        @Override
        public double getShapeWidth(double defaultValue){
            return getNode().getDouble(SHAPE_WIDTH_KEY, defaultValue);
        }
        @Override
        public void setShapeWidth(double value){
            getNode().putDouble(SHAPE_WIDTH_KEY, value);
        }
        @Override
        public double getShapeHeight(double defaultValue){
            return getNode().getDouble(SHAPE_HEIGHT_KEY, defaultValue);
        }
        @Override
        public void setShapeHeight(double value){
            getNode().putDouble(SHAPE_HEIGHT_KEY, value);
        }
        @Override
        public boolean isShapeSizeLinked(boolean defaultValue){
            return getNode().getBoolean(SHAPE_LINK_SIZE_KEY, defaultValue);
        }
        @Override
        public void setShapeSizeLinked(boolean value){
            getNode().putBoolean(SHAPE_LINK_SIZE_KEY, value);
        }
        @Override
        public int getShapeType(int defaultValue){
            return getNode().getInt(SHAPE_TYPE_KEY, defaultValue);
        }
        @Override
        public void setShapeType(int value){
            getNode().putInt(SHAPE_TYPE_KEY, value);
        }
    }
}
