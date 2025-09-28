/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package spiral;

import anim.*;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import com.madgag.gif.fmsware.GifDecoder;
import com.technicjelle.UpdateChecker;
import components.JAboutPanel;
import components.JColorSelector;
import components.debug.DebugCapable;
import components.text.CompoundUndoManager;
import components.text.action.commands.TextComponentCommands;
import components.text.action.commands.UndoManagerCommands;
import files.FilesExtended;
import files.extensions.ConfigExtensions;
import files.extensions.ImageExtensions;
import files.extensions.TextDocumentExtensions;
import geom.GeometryMath;
import icons.Icon2D;
import icons.box.ColorBoxIcon;
import icons.box.DisabledBoxIcon;
import io.github.dheid.fontchooser.FontDialog;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import net.coobird.thumbnailator.Thumbnailator;
import static spiral.SpiralGeneratorUtilities.*;
import spiral.painter.*;
import swing.CenteredTextPainter;
import utils.SwingExtendedUtilities;

/**
 *
 * @author Mosblinker
 */
public class SpiralGenerator extends javax.swing.JFrame implements DebugCapable{
    /**
     * This is the current version of the program.
     */
    public static final String PROGRAM_VERSION = "0.28.0";
    /**
     * This is the name of the program.
     */
    public static final String PROGRAM_NAME = "Hypno Gif Generator";
    /**
     * The name of the author and main developer.
     */
    protected static final String AUTHOR_NAME = "Mosblinker";
    /**
     * This is the name by which the program internally references itself as.
     */
    protected static final String INTERNAL_PROGRAM_NAME = "HypnoGifGenerator";
    /**
     * This is the credits for the program. This is currently private as I plan 
     * to rework it.
     * @todo Rework this and then make it public. Also add any additional 
     * credits necessary
     */
    private static final String[][] CREDITS = {{
            "Developers",
            "Mosblinker - Main developer and artist."
        },{
//            "Testers",
//            "*Insert Testers Here*"
//        },{
            "Special Thanks",
            "Special thanks to JWolf for the inspiration for this program and for being my friend."
        },{
            "Libraries",
            "Thumbnailator - coobird - https://github.com/coobird/thumbnailator",
            "SwingExtended - Mosblinker - https://github.com/Mosblinker/SwingExtended",
            "FilesExtended - Mosblinker - https://github.com/Mosblinker/FilesExtended",
            "GeomArt4J - Mosblinker - https://github.com/Mosblinker/GeomArt4J",
            "SwingFilesExtended - Mosblinker",
            "Measure - Mosblinker",
            "GUIComponents - Mosblinker",
            "ConfigUtilities - Mosblinker - https://github.com/Mosblinker/ConfigUtilities",
            "animated-gif-lib - rtyley and Kevin Weiner - https://github.com/rtyley/animated-gif-lib-for-java",
            "webp-imageio - https://github.com/darkxanter/webp-imageio",
            "FontChooser - Daniel Heid - https://gitlab.com/dheid/fontchooser",
            "UpdateChecker - TechnicJelle - https://github.com/TechnicJelle/UpdateCheckerJava"
    }};
    /**
     * This is the pattern for the file handler to use for the log files of this 
     * program.
     */
    private static final String PROGRAM_LOG_PATTERN = 
            "%h/.mosblinker/logs/"+INTERNAL_PROGRAM_NAME+"-%u.%g.log";
    /**
     * This is an array containing the widths and heights for the icon images 
     * for this program. 
     */
    private static final int[] ICON_SIZES = {16, 24, 32, 48, 64, 96, 128, 256, 512};
    /**
     * This is the path to the image used as the mask for the text on the icon 
     * for the program.
     */
    private static final String ICON_MASK_FILE_IMAGE = "/images/icon_mask.png";
    /**
     * These are the fractions to use to control the fade out of the icon at the 
     * edges of the icon.
     */
    private static final float[] ICON_FADE_FRACTIONS = {0.85f, 1.0f};
    /**
     * These are the colors to use for the gradient used to create the fade out 
     * of the icon.
     */
    private static final Color[] ICON_FADE_COLORS = {Color.WHITE,
        TRANSPARENT_COLOR};
    /**
     * This is the path to a local folder containing test images to use to 
     * compare the output of the program to an existing image.
     */
    private static final String TEST_IMAGE_FILE_FOLDER = "DevStuff"+File.separator+"images";
    /**
     * This is the default width for the spiral image.
     */
    protected static final int DEFAULT_SPIRAL_WIDTH = 450;
    /**
     * This is the default height for the spiral image.
     */
    protected static final int DEFAULT_SPIRAL_HEIGHT = 450;
    /**
     * This is the number of frames in the animation.
     */
    private static final int SPIRAL_FRAME_COUNT = 64;
    /**
     * This is the amount that the spiral will rotate per frame, in degrees.
     */
    private static final double SPIRAL_FRAME_ROTATION = 
            SpiralPainter.FULL_CIRCLE_DEGREES / SPIRAL_FRAME_COUNT;
    /**
     * This is the duration for each frame of animation.
     */
    protected static final int SPIRAL_FRAME_DURATION = 20;
    /**
     * This is the name of the preference node used to store the settings for 
     * this program.
     */
    private static final String PREFERENCE_NODE_NAME = 
            "mosblinker/spiral/jwolf/"+INTERNAL_PROGRAM_NAME;
    /**
     * This is an array that contains the default colors used for the spiral.
     */
    private static final Color[] DEFAULT_SPIRAL_COLORS = {
        Color.WHITE,
        Color.BLACK,
        new Color(0x0084D7),
        new Color(0xA184B2)
    };
    /**
     * This is an array of colors to use to try for the background of the GIF 
     * when combining frames. If none of these work, then the program will 
     * derive the background color from the frame instead.
     */
    private static final Color[] GIF_TRANSPARENCY_COLORS = {
        Color.BLACK,
        Color.WHITE,
        Color.PINK,
        Color.RED,
        Color.ORANGE,
        Color.YELLOW,
        Color.GREEN,
        Color.CYAN,
        Color.BLUE,
        Color.MAGENTA
    };
    
    private static final String OVERLAY_MASK_FILE_CHOOSER_NAME = "OverlayFC";
    
    private static final String SAVE_FILE_CHOOSER_NAME = "SaveFC";
    
    private static final String CONFIG_FILE_CHOOSER_NAME = "ConfigFC";
    
    private static final String COLOR_SELECTOR_NAME = "ColorSelector";
    
//    private static final String FONT_SELECTOR_NAME = "FontSelector";
    
    private static final String MASK_DIALOG_NAME = "MaskDialog";
    /**
     * This is a logger to log events in the program.
     */
    private static final Logger LOGGER = Logger.getLogger(INTERNAL_PROGRAM_NAME+
            "-"+PROGRAM_VERSION);
    /**
     * This returns the logger used by this program to log issues and debugging 
     * statements.
     * @return The logger used by the program to log stuff.
     */
    public static Logger getLogger(){
        return LOGGER;
    }
    /**
     * 
     */
    private void loadFromSettings(SpiralGeneratorSettings settings){
        getLogger().entering(this.getClass().getName(), "loadFromSettings",
                settings);
        for (int i = 0; i < colorIcons.length; i++){
            colorIcons[i].setColor(settings.getSpiralColor(i,DEFAULT_SPIRAL_COLORS[i]));
        }
        
        loadFromSettings(settings,settings.getSpiralType(),settings.getMaskType());
        getLogger().exiting(this.getClass().getName(), "loadFromSettings");
    }
    /**
     * 
     * @param settings 
     */
    private void loadFromSettings(SpiralGeneratorSettings settings, int spiralType, 
            int maskType){
        getLogger().entering(this.getClass().getName(), "loadFromSettings",
                new Object[]{settings,spiralType,maskType});
        Dimension imgSize = settings.getImageSize();
        widthSpinner.setValue(imgSize.width);
        heightSpinner.setValue(imgSize.height);
        
        getLogger().log(Level.FINER, "Loading SpiralPainters");
            // Go through the spiral painters
        for (SpiralPainter painter : spiralPainters){
                // Get the byte array for the painter from the preferences
            byte[] arr = settings.getSpiralData(painter);
            try{    // Load the current spiral painter from the byte array
                painter.fromByteArray(arr);
            } catch (IllegalArgumentException | BufferOverflowException | 
                    BufferUnderflowException ex) {
                getLogger().log(Level.WARNING, String.format(
                        "Failed to load %s from preferences using %s", 
                        painter.getClass(),toByteString(arr)), ex);
            }
        }
        getLogger().log(Level.FINER, "Finished loading SpiralPainters");
        
            // Configure the overlay mask's text painter's settings from the 
            // preferences
        overlayMask.textPainter.setAntialiasingEnabled(
                settings.isMaskTextAntialiased(true));
        fontAntialiasingToggle.setSelected(overlayMask.textPainter.isAntialiasingEnabled());
        overlayMask.textPainter.setLineSpacing(settings.getMaskLineSpacing(0));
        lineSpacingSpinner.setValue(overlayMask.textPainter.getLineSpacing());
        
        spiralTypeCombo.setSelectedIndex(Math.max(Math.min(spiralType, 
                spiralPainters.length-1), 0));
        maskTabbedPane.setSelectedIndex(Math.max(Math.min(maskType, 
                maskTabbedPane.getTabCount()-1), 0));
        maskAlphaToggle.setSelected(true);
        settings.loadMaskAlphaIndex(maskAlphaButtons);
        maskAlphaInvertToggle.setSelected(settings.isMaskImageInverted());
        maskDesaturateCombo.setSelectedIndex(Math.max(Math.min(
                settings.getMaskDesaturateMode(), 
                maskDesaturateCombo.getItemCount()-1), 0));
        updateMaskAlphaControlsEnabled();
        maskShapeLinkSizeToggle.setSelected(settings.isMaskShapeSizeLinked());
        maskShapeWidthSpinner.setValue(settings.getMaskShapeWidth());
        maskShapeHeightSpinner.setValue(settings.getMaskShapeHeight());
        updateMaskShapeControlsEnabled();
        
        imgMaskAntialiasingToggle.setSelected(settings.isMaskImageAntialiased());
        maskScaleSpinner.setValue(settings.getMaskScale());
        delaySpinner.setValue(settings.getFrameDuration());
            // Get the mask's rotation
        double imgRotation = settings.getMaskRotation();
            // If the mask rotation is greater than 360
        if (imgRotation > GeometryMath.FULL_CIRCLE_DEGREES)
            imgRotation = GeometryMath.boundDegrees(imgRotation);
        try{
            maskRotateSpinner.setValue(imgRotation);
        } catch (IllegalArgumentException ex){
            getLogger().log(Level.WARNING, "Mask rotation is invalid", ex);
        }
        maskFlipHorizToggle.setSelected(settings.isMaskFlippedHorizontally());
        maskFlipVertToggle.setSelected(settings.isMaskFlippedVertically());
        maskImgScaleMethodCombo.setSelectedIndex(settings.getMaskImageInterpolation(4));
        maskShapeCombo.setSelectedIndex(Math.max(Math.min(
                settings.getMaskShapeType(),maskShapeCombo.getItemCount()-1), 0));
        
            // Load the values for the components for controlling the spiral 
            // from the current spiral painter
        loadSpiralPainter();
        
            // Get the font for the text mask from the preferences
        Font font = settings.getMaskFont(maskTextPane.getFont());
        maskTextPane.setFont(font);
        boldToggle.setSelected(font.isBold());
        italicToggle.setSelected(font.isItalic());
            // Load the text for the mask from the preferences
        maskTextPane.setText(settings.getMaskText());
        getLogger().exiting(this.getClass().getName(), "loadFromSettings");
    }
    /**
     * Creates new form SpiralGenerator
     * @param debugMode
     */
    public SpiralGenerator(boolean debugMode) {
        getLogger().entering(this.getClass().getName(), "<init>");
        this.debugMode = debugMode;
        try{    // Try to get the preference node used for the program
            config = new SpiralGeneratorConfig(Preferences.userRoot()
                    .node(PREFERENCE_NODE_NAME));
        } catch (SecurityException | IllegalStateException ex){
            getLogger().log(Level.SEVERE, "Unable to load preference node", ex);
            // TODO: Error message window
        }
            // This is the handler to listen to the painters and color buttons
        SpiralHandler handler = new SpiralHandler();
        
        colorIcons = new ColorBoxIcon[DEFAULT_SPIRAL_COLORS.length];
        colorButtons = new HashMap<>();
        colorIndexes = new HashMap<>();
        
            // A for loop to create the color icons with their respective colors
        for (int i = 0; i < colorIcons.length; i++){
                // Load the color from the preferences and use it for the color 
                // of the icon
            colorIcons[i] = new ColorBoxIcon(16,16,config.getSpiralColor(i, 
                    DEFAULT_SPIRAL_COLORS[i]),false);
                // Create a button for this icon
            JButton button = new JButton(colorIcons[i]);
                // Set its disabled icon to a disabled version of the icon
            button.setDisabledIcon(new DisabledBoxIcon(colorIcons[i]));
            button.addActionListener(handler);
            colorButtons.put(colorIcons[i], button);
            colorIndexes.put(button, i);
        }
            // Create the spiral models using the color icons
        models = new SpiralModel[]{
                // Base spiral model
            new ColorIconSpiralModel(0,1),
                // Overlay spiral model
            new ColorIconSpiralModel(2,3)
        };
            // Create an array with the spiral painters that are available
        spiralPainters = new SpiralPainter[]{
            new LogarithmicSpiralPainter(),
            new ArithmeticSpiralPainter(),
            new ConcentricSpiralPainter(),
            new RippleSpiralPainter(),
            new OscillatingCirclesSpiralPainter()
        };
        
        spiralCompLabels = new HashMap<>();
        
        try{
            updateChecker = new UpdateChecker(AUTHOR_NAME,INTERNAL_PROGRAM_NAME,
                    PROGRAM_VERSION);
        } catch (RuntimeException ex){
            getLogger().log(Level.WARNING, "UpdateChecker could not be constructed", 
                    ex);
        }
        spiralIcon = new SpiralIcon();
            // Get the spiral type from the configuration
        int spiralType = config.getSpiralType();
            // Get the mask type from the configuration
        int maskType = config.getMaskType();
            // Initialize the components
        initComponents();
        
            // Configure the mask text pane to have centered text
            
            // Get the document for the mask text pane
        StyledDocument doc = maskTextPane.getStyledDocument();
            // Create a style to use to center the text on the text pane
        SimpleAttributeSet centeredText = new SimpleAttributeSet();
            // Make the style center the text
        StyleConstants.setAlignment(centeredText, StyleConstants.ALIGN_CENTER);
            // Apply the centered text style to the entire pane
        doc.setParagraphAttributes(0, doc.getLength(), centeredText, false);
        
            // Add all the image file filters to the mask image file chooser
        for (FileFilter filter : ImageExtensions.IMAGE_FILTERS){
            maskFC.addChoosableFileFilter(filter);
        }   // Set the current file filter to the image filter
        maskFC.setFileFilter(ImageExtensions.IMAGE_FILTER);
        
            // Add file filters to config file choosers
        configFC.addChoosableFileFilter(TextDocumentExtensions.TEXT_FILTER);
        
            // Go through the labels for the components used to set the 
            // parameters for the spirals
        for (JLabel label : new JLabel[]{
            radiusLabel,baseLabel,balanceLabel,dirLabel,angleLabel,spiralShapeLabel
        }){
            spiralCompLabels.put(label.getLabelFor(), label);
        }
        
        maskFrameCtrlPanel.setVisible(false);
        
            // Create the icon for the program
        
            // This is the image to use as a mask for the icon
        BufferedImage iconImg = null;
        try {   // Load the mask for the program icon
            iconImg = readImageResource(ICON_MASK_FILE_IMAGE);
        } catch (IOException ex) {
            getLogger().log(Level.WARNING,
                    "Failed to load icon mask \""+ICON_MASK_FILE_IMAGE+"\"",
                    ex);
        }   // This is the painter to use to paint the icons for the program
        LogarithmicSpiralPainter iconPainter = new LogarithmicSpiralPainter();
            // This is an array to get the images to use for the icon of the 
            // program
        ArrayList<BufferedImage> iconImages = new ArrayList<>();
            // This is the model for the main part of the spiral for the program 
            // icon
        SpiralModel iconModel = new ImmutableSpiralModel(
                DEFAULT_SPIRAL_COLORS[0],DEFAULT_SPIRAL_COLORS[1],0.0);
            // This is the model for the message part of the spiral for the 
            // program icon
        SpiralModel iconMsgModel = new ImmutableSpiralModel(
                DEFAULT_SPIRAL_COLORS[2],DEFAULT_SPIRAL_COLORS[3],0.0);
            // Go through the icon sizes
        for (int size : ICON_SIZES){
                // Create and add an image of the given size to the icons
            iconImages.add(getProgramIcon(size,size,iconModel,iconMsgModel,
                    iconPainter,iconImg));
        }
        setIconImages(iconImages);
        
            // Create the icon for the about window
        SpiralProgramIcon aboutIcon = new SpiralProgramIcon(128,iconPainter,
                iconImg,iconModel,iconMsgModel);
        aboutPanel.setProgramIcon(aboutIcon);
        updateIconLabel.setIcon(new SpiralProgramIcon(64,aboutIcon));
            // Get the document for the credits text pane
        StyledDocument creditsDoc = aboutPanel.getCreditsDocument();
            // This is a String to get the credits text
        String credits = "";
            // Go through the credits arrays
        for (int i = 0; i < CREDITS.length; i++){
                // If this is not the first array
            if (i > 0)
                credits += System.lineSeparator()+System.lineSeparator();
                // Add the header for this section
            credits += "---- "+CREDITS[i][0]+" ----";
                // Go through the credits in this section
            for (int j = 1; j < CREDITS[i].length; j++){
                credits += System.lineSeparator()+CREDITS[i][j];
            }
        }
        aboutPanel.setCreditsText(credits);
        creditsDoc.setParagraphAttributes(0, creditsDoc.getLength(), 
                centeredText, false);
        
            // Set the maximum for the progress bar. The only thing that uses it 
            // in this program is saving the animation
        progressBar.setMaximum(SPIRAL_FRAME_COUNT);
            // Configure the frame slider
        frameSlider.setMaximum(SPIRAL_FRAME_COUNT-1);
            // Update the number for the frame being displayed
        updateFrameNumberDisplayed();
        animationTimer = new javax.swing.Timer(SPIRAL_FRAME_DURATION, (ActionEvent e) -> {
            progressAnimation(e);
        });
        previewLabel.setIcon(spiralIcon);
        maskPreviewLabel.setIcon(overlayMask);
        
            // Add the components and their names to the preferences
        config.setComponentName(colorSelector, COLOR_SELECTOR_NAME);
//        config.setComponentName(fontSelector, FONT_SELECTOR_NAME);
        config.setComponentName(maskDialog, MASK_DIALOG_NAME);
            // Go through and load the components from the preferences
        for (Component c : config.getComponentNames().keySet()){
                 // Load the component's size from the preferences
            config.loadComponentSize(c);
        }
            // Get a listener for the file choosers
        PropertyChangeListener fcL = config.new FileChooserPropertyChangeListener();
            // Add the file choosers and their names to the preferences
        config.addFileChooser(maskFC, OVERLAY_MASK_FILE_CHOOSER_NAME);
        config.addFileChooser(saveFC, SAVE_FILE_CHOOSER_NAME);
        config.addFileChooser(configFC, CONFIG_FILE_CHOOSER_NAME);
        for (JFileChooser fc : config.getFileChooserPreferenceMap().keySet()){
                // Load the file chooser from the preferences
            config.loadFileChooser(fc);
            fc.addPropertyChangeListener(fcL);
        }
        
            // Ensure the program's size is at least 960x575
        SwingExtendedUtilities.setComponentSize(SpiralGenerator.this, 960, 575);
            // Load the program's bounds from the preferences
        config.getProgramBounds(SpiralGenerator.this);
        
            // Load the settings for the program from the preferences
        loadFromSettings(config,spiralType,maskType);
        alwaysScaleToggle.setSelected(config.isImageAlwaysScaled());
        previewLabel.setImageAlwaysScaled(alwaysScaleToggle.isSelected());
        maskPreviewLabel.setImageAlwaysScaled(alwaysScaleToggle.isSelected());
        checkUpdatesAtStartToggle.setSelected(config.getCheckForUpdateAtStartup());
        optimizeDifferenceToggle.setSelected(config.isOptimizedForDifference());
            // Load the size of the font selector from the preferences
        fontDim = config.getMaskFontSelectorSize();
        
            // Create and configure the actions for the mask text pane
        editCommands = new TextComponentCommands(maskTextPane);
        undoCommands = new UndoManagerCommands(new CompoundUndoManager());
            // Add the actions to the popup menu for the mask text pane
        maskPopup.add(undoCommands.getUndoAction());
        maskPopup.add(undoCommands.getRedoAction());
        maskPopup.addSeparator();
        maskPopup.add(editCommands.getCopyAction());
        maskPopup.add(editCommands.getCutAction());
        maskPopup.add(editCommands.getPasteAction());
        maskPopup.add(editCommands.getDeleteAction());
        maskPopup.addSeparator();
        maskPopup.add(editCommands.getSelectAllAction());
            // Add the listeners to the mask text pane
        editCommands.addToTextComponent();
        undoCommands.addToTextComponent(maskTextPane);
        
            // Go through the spiral painters
        for (SpiralPainter painter : spiralPainters)
            painter.addPropertyChangeListener(handler);
        overlayMask.textPainter.addPropertyChangeListener(handler);
        doc.addDocumentListener(handler);
        
            // If the mask is an image
        if (maskTabbedPane.getSelectedIndex() == 1){
                // Get the overlay mask image file from the preferences
            File file = config.getMaskImageFile();
                // If the overlay mask image file is not null and does exist
            if (file != null && file.exists()){
                    // Load the image file from the preferences
                fileWorker = new ImageLoader(file, true, 
                        config.getMaskImageFrameIndex());
                fileWorker.execute();
            }
        } else {
            config.setMaskImageFile(null);
            config.setMaskImageFrameIndex(0);
        }
        
            // If the program is in debug mode
        if (debugMode){
            testSpiralIcon = new TestSpiralIcon(spiralPainters[spiralPainters.length-1]);
            testComponents = new HashMap<>();
                // Set the popup menu for the preview label to be the debug menu
            previewLabel.setComponentPopupMenu(debugPopup);
                // Get the program's directory
            File prgDir = getProgramDirectory();
                // If the program's directory is null
            if (prgDir == null)
                prgDir = new File(System.getProperty("user.dir"));
                // If the program's directory's parent is not null
            else if (prgDir.getParentFile() != null)
                prgDir = prgDir.getParentFile();
                // Get the folder for the test images
            File imgDir = new File(prgDir,TEST_IMAGE_FILE_FOLDER);
                // If the test image folder exists
            if (imgDir.exists()){
                    // Get a list of the files in that folder that match the 
                    // pattern for the test images
                List<File> files = FilesExtended.getFilesFromFolder(imgDir, (File pathname) -> {
                        // If the file is null
                    if (pathname == null)
                        return false;
                        // If the file is the test image folder
                    if (imgDir.equals(pathname))
                        return true;
                        // Get the name of the file
                    String name = pathname.getName();
                        // If the file somehow doesn't have a name
                    if (name == null) 
                        return false;
                        // If the name matches the pattern
                    if (name.startsWith("test") && name.endsWith(".png")) {
                        try {   // Try to check if the rest of the name is a 
                                // number
                            Integer.valueOf(name.substring(4, name.length()-4));
                            return true;
                        }catch (NumberFormatException ex){}
                    }
                    return false;
                }, 1);
                    // Remove the test image folder from the list
                files.remove(imgDir);
                    // Sort the file names
                files.sort((File o1, File o2) -> {
                        // Get the name of the first file
                    String name1 = o1.getName();
                        // Get the name of the second file
                    String name2 = o2.getName();
                        // Compare the numbers in the file names
                    return Integer.compare(
                            Integer.parseInt(name1.substring(4,name1.length()-4)),
                            Integer.parseInt(name2.substring(4,name2.length()-4)));
                }); // Go through the files for the test images
                for (File file : files){
                    try {   // Try to load the image
                        testSpiralIcon.images.add(ImageIO.read(file));
                    } catch (IOException ex) {
                        getLogger().log(Level.INFO, 
                                "Failed to load test image \""+file.getName()+"\"", 
                                ex);
                    }
                }
            }   // If the test images list is empty
            if (testSpiralIcon.images.isEmpty())
                testSpiralImageSpinner.setEnabled(false);
            else
                testSpiralImageSpinner.setModel(new SpinnerNumberModel(
                        config.getDebugTestImage(testSpiralIcon.images.size()), 
                        -1, testSpiralIcon.images.size()-1, 1));
            testRotateSpinner.setValue(config.getDebugTestRotation());
            testScaleSpinner.setValue(config.getDebugTestScale());
                // Add the components for the test double values
            testComponents.put(Double.class, Arrays.asList());
                // Add the components for the test boolean values
            testComponents.put(Boolean.class, Arrays.asList());
                // Add the components for the test integer values
            testComponents.put(Integer.class, Arrays.asList());
                // This is a handler to listen to the test value components
            DebugTestComponentHandler debugHandler = new DebugTestComponentHandler();
                // Go through the list of components and their types
            for (Map.Entry<Class, List<Component>> entry : testComponents.entrySet()){
                    // Get the type of value set for these components
                Class type = entry.getKey();
                    // Get the list of components
                List<Component> list = entry.getValue();
                    // Go through the components
                for (int i = 0; i < list.size(); i++){
                        // This will get the value for the component
                    Object value = null;
                        // Get the current component
                    Component c = list.get(i);
                        // If the component is for test doubles
                    if (Double.class.equals(type))
                        value = config.getDebugTestDouble(i);
                        // If the component is for test integers
                    else if (Integer.class.equals(type))
                        value = config.getDebugTestInteger(i);
                        // If the component is for test booleans
                    else if (Boolean.class.equals(type)){
                            // If the component is a toggle button
                        if (c instanceof JToggleButton){
                                // Get the component as a toggle button
                            JToggleButton b = (JToggleButton)c;
                            b.setSelected(config.getDebugTestBoolean(i,b.isSelected()));
                        } else 
                            value = config.getDebugTestBoolean(i);
                    }   // If the component is for test strings
                    else if (String.class.equals(type))
                        value = config.getDebugTestString(i);
                        // If the component is a toggle button
                    if (c instanceof JToggleButton)
                        ((JToggleButton)c).addActionListener(debugHandler);
                        // If the test component is a spinner
                    else if (c instanceof JSpinner){
                            // If the test value for this spinner is not null
                        if (value != null)
                            ((JSpinner)c).setValue(value);
                        ((JSpinner)c).addChangeListener(debugHandler);
                    }
                }
            }
        }
            // If the program should check for updates at startup and the 
            // program is not in debug mode
        if (!debugMode && checkUpdatesAtStartToggle.isSelected()){
            updateWorker = new UpdateCheckWorker(true);
            updateWorker.execute();
        }
        getLogger().exiting(this.getClass().getName(), "<init>");
    }
    
    public SpiralGenerator() {
        this(false);
    }
    
    protected static BufferedImage getProgramIcon(int width, int height, 
            SpiralModel model, SpiralModel maskModel, SpiralPainter painter, 
            BufferedImage mask){
            // This is the image for the current size
        BufferedImage img = new BufferedImage(width, height, 
                BufferedImage.TYPE_INT_ARGB);
            // Get a graphics context for the image
        Graphics2D g = img.createGraphics();
            // Draw the spiral for the icon
        painter.paint(g, model, width, height);
            // If there is a mask for the overlay for the icon
        if (mask != null){
                // This is the image for the overlay
            BufferedImage imgOverlay = new BufferedImage(width, height, 
                    BufferedImage.TYPE_INT_ARGB);
                // Get a graphics context for the overlay
            Graphics2D g2 = imgOverlay.createGraphics();
                // Draw the spiral for the overlay
            painter.paint(g2, maskModel, width, height);
                // Mask the overlay using the mask
            maskImage(g2,Thumbnailator.createThumbnail(mask,width, height));
            g2.dispose();
                // Draw the overlay
            g.drawImage(imgOverlay, 0, 0, null);
        }   // Set the composite mode to only include pixels that are drawn
        g.setComposite(AlphaComposite.DstIn);
            // Set the paint to a radial gradient that will make the icon 
            // circular while also fading out near the edge
        g.setPaint(new RadialGradientPaint(width/2.0f,height/2.0f, 
                (width+height)/4.0f, ICON_FADE_FRACTIONS, ICON_FADE_COLORS));
            // Fill the area to mask the icon
        g.fillRect(0, 0, width, height);
        g.dispose();
        return img;
    }
    /**
     * 
     * @param index
     * @return 
     */
    private SpiralPainter getSpiralPainter(int index){
            // If the index is a valid index in the spiral painter array
        if (index >= 0 && index < spiralPainters.length)
            return spiralPainters[index];
        return null;
    }
    /**
     * 
     * @return 
     */
    private SpiralPainter getSpiralPainter(){
        return getSpiralPainter(spiralTypeCombo.getSelectedIndex());
    }
    
    private void loadSpiralPainter(SpiralPainter painter){
        getLogger().entering(this.getClass().getName(), "loadSpiralPainter",
                painter);
            // If the painter is null
        if (painter == null){
            getLogger().exiting(this.getClass().getName(), "loadSpiralPainter");
            return;
        }
        dirCombo.setSelectedIndex((painter.isClockwise())?0:1);
        radiusSpinner.setValue(painter.getSpiralRadius());
        balanceSpinner.setValue(painter.getBalance());
        angleSpinner.setValue(painter.getRotation());
        spinDirCombo.setSelectedIndex((painter.isSpinClockwise())?0:1);
            // Show the base spinner if the spiral is logarithmic
        baseSpinner.setVisible(painter instanceof LogarithmicSpiral);
            // If the spiral is logaritmic
        if (baseSpinner.isVisible())
            baseSpinner.setValue(((LogarithmicSpiral)painter).getBase());
            // Show the shape combo box if the spiral has a shape
        spiralShapeCombo.setVisible(painter instanceof ShapedSpiral);
            // If the spiral has a shape
        if (spiralShapeCombo.isVisible())
            spiralShapeCombo.setSelectedItem(((ShapedSpiral)painter).getShape());
            // If the spiral is not logarithmic and doesn't have a shape
        spiralCtrlFiller.setVisible(!baseSpinner.isVisible() && !spiralShapeCombo.isVisible());
            // Go through the components for the spirals and their labels
        for (Map.Entry<Component, JLabel> entry : spiralCompLabels.entrySet()){
            entry.getValue().setVisible(entry.getKey().isVisible());
        }
        getLogger().exiting(this.getClass().getName(), "loadSpiralPainter");
    }
    /**
     * 
     */
    private void loadSpiralPainter(){
        loadSpiralPainter(getSpiralPainter());
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        debugPopup = new javax.swing.JPopupMenu();
        printTestButton = new javax.swing.JMenuItem();
        printFPSToggle = new javax.swing.JCheckBoxMenuItem();
        inputEnableToggle = new javax.swing.JCheckBoxMenuItem();
        showTestDialogButton = new javax.swing.JMenuItem();
        colorSelector = new components.JColorSelector();
        maskFCPreview = new components.JFileDisplayPanel();
        saveFCPreview = new components.JFileDisplayPanel();
        maskFC = new javax.swing.JFileChooser();
        saveFC = new javax.swing.JFileChooser();
        maskDialog = new javax.swing.JDialog(this);
        maskTabbedPane = new javax.swing.JTabbedPane();
        textMaskCtrlPanel = new javax.swing.JPanel();
        maskTextScrollPane = new javax.swing.JScrollPane();
        javax.swing.JPanel maskTextPanel = new javax.swing.JPanel();
        maskTextPane = new javax.swing.JTextPane();
        fontButton = new javax.swing.JButton();
        boldToggle = new javax.swing.JCheckBox();
        italicToggle = new javax.swing.JCheckBox();
        fontAntialiasingToggle = new javax.swing.JCheckBox();
        lineSpacingLabel = new javax.swing.JLabel();
        lineSpacingSpinner = new javax.swing.JSpinner();
        maskImageCtrlPanel = new javax.swing.JPanel();
        javax.swing.Box.Filler filler11 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        loadMaskButton = new javax.swing.JButton();
        javax.swing.Box.Filler filler10 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        imgMaskAntialiasingToggle = new javax.swing.JCheckBox();
        maskAlphaCtrlPanel = new javax.swing.JPanel();
        maskAlphaToggle = new javax.swing.JRadioButton();
        maskAlphaGrayToggle = new javax.swing.JRadioButton();
        maskAlphaColorCtrlPanel = new javax.swing.JPanel();
        maskAlphaRedToggle = new javax.swing.JRadioButton();
        maskAlphaGreenToggle = new javax.swing.JRadioButton();
        maskAlphaBlueToggle = new javax.swing.JRadioButton();
        maskAlphaInvertToggle = new javax.swing.JCheckBox();
        maskDesaturateLabel = new javax.swing.JLabel();
        maskDesaturateCombo = new javax.swing.JComboBox<>();
        imgAspectRatioButton = new javax.swing.JButton();
        imgMaskPreview = new components.JThumbnailLabel();
        maskFrameCtrlPanel = new javax.swing.JPanel();
        maskFramePrevButton = new javax.swing.JButton();
        maskFrameLabel = new javax.swing.JLabel();
        maskFrameNextButton = new javax.swing.JButton();
        maskImgScalePanel = new javax.swing.JPanel();
        maskImgScaleMethodLabel = new javax.swing.JLabel();
        javax.swing.Box.Filler filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 32767));
        maskImgScaleMethodCombo = new javax.swing.JComboBox<>();
        shapeMaskCtrlPanel = new javax.swing.JPanel();
        shapeMaskSizePanel = new javax.swing.JPanel();
        maskShapeWidthLabel = new javax.swing.JLabel();
        javax.swing.Box.Filler filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 32767));
        maskShapeWidthSpinner = new javax.swing.JSpinner();
        javax.swing.Box.Filler filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 32767));
        maskShapeHeightLabel = new javax.swing.JLabel();
        javax.swing.Box.Filler filler17 = new javax.swing.Box.Filler(new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 32767));
        maskShapeHeightSpinner = new javax.swing.JSpinner();
        javax.swing.Box.Filler filler18 = new javax.swing.Box.Filler(new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 32767));
        maskShapeLinkSizeToggle = new javax.swing.JCheckBox();
        maskShapeLabel = new javax.swing.JLabel();
        maskShapeCombo = new javax.swing.JComboBox<>();
        maskScaleLabel = new javax.swing.JLabel();
        maskScaleSpinner = new javax.swing.JSpinner();
        resetMaskButton = new javax.swing.JButton();
        maskRotateLabel = new javax.swing.JLabel();
        maskRotateSpinner = new javax.swing.JSpinner();
        maskFlipHorizToggle = new javax.swing.JCheckBox();
        maskFlipVertToggle = new javax.swing.JCheckBox();
        maskPopup = new javax.swing.JPopupMenu();
        testDialog = new javax.swing.JDialog(this);
        javax.swing.JPanel testCtrlPanel = new javax.swing.JPanel();
        javax.swing.JLabel testSpiralImageLabel = new javax.swing.JLabel();
        testSpiralImageSpinner = new javax.swing.JSpinner();
        javax.swing.JLabel testRotateLabel = new javax.swing.JLabel();
        testRotateSpinner = new javax.swing.JSpinner();
        javax.swing.JLabel testScaleLabel = new javax.swing.JLabel();
        testScaleSpinner = new javax.swing.JSpinner();
        showTestSpiralToggle = new javax.swing.JCheckBox();
        javax.swing.JPanel testCtrlPanel2 = new javax.swing.JPanel();
        javax.swing.Box.Filler filler16 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 32767));
        testShowRadiusToggle = new javax.swing.JCheckBox();
        javax.swing.Box.Filler filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        maskAlphaButtons = new javax.swing.ButtonGroup();
        aboutDialog = new javax.swing.JDialog(this);
        aboutPanel = new components.JAboutPanel();
        updateCheckDialog = new javax.swing.JDialog(this);
        updatePanel = new javax.swing.JPanel();
        updateIconLabel = new javax.swing.JLabel();
        updateTextLabel = new javax.swing.JLabel();
        currentVersTextLabel = new javax.swing.JLabel();
        latestVersTextLabel = new javax.swing.JLabel();
        checkUpdatesAtStartToggle = new javax.swing.JCheckBox();
        currentVersLabel = new javax.swing.JLabel();
        latestVersLabel = new javax.swing.JLabel();
        updateContinueButton = new javax.swing.JButton();
        updateOpenButton = new javax.swing.JButton();
        configFC = new javax.swing.JFileChooser();
        framesPanel = new javax.swing.JPanel();
        frameNumberLabel = new javax.swing.JLabel();
        frameNavPanel = new javax.swing.JPanel();
        frameFirstButton = new javax.swing.JButton();
        javax.swing.Box.Filler filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 32767));
        framePrevButton = new javax.swing.JButton();
        javax.swing.Box.Filler filler5 = new javax.swing.Box.Filler(new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 32767));
        framePlayButton = new javax.swing.JToggleButton();
        javax.swing.Box.Filler filler8 = new javax.swing.Box.Filler(new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 32767));
        frameSlider = new javax.swing.JSlider();
        javax.swing.Box.Filler filler9 = new javax.swing.Box.Filler(new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 32767));
        frameStopButton = new javax.swing.JButton();
        javax.swing.Box.Filler filler6 = new javax.swing.Box.Filler(new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 32767));
        frameNextButton = new javax.swing.JButton();
        javax.swing.Box.Filler filler7 = new javax.swing.Box.Filler(new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 0), new java.awt.Dimension(7, 32767));
        frameLastButton = new javax.swing.JButton();
        javax.swing.Box.Filler filler12 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        javax.swing.Box.Filler filler13 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        previewPanel = new javax.swing.JPanel();
        previewSpiralPanel = new javax.swing.JPanel();
        previewLabel = new components.JThumbnailLabel();
        previewMaskPanel = new javax.swing.JPanel();
        maskPreviewLabel = new components.JThumbnailLabel();
        spiralCtrlPanel = new javax.swing.JPanel();
        radiusLabel = new javax.swing.JLabel();
        radiusSpinner = new javax.swing.JSpinner();
        baseLabel = new javax.swing.JLabel();
        baseSpinner = new javax.swing.JSpinner();
        balanceLabel = new javax.swing.JLabel();
        balanceSpinner = new javax.swing.JSpinner();
        dirLabel = new javax.swing.JLabel();
        dirCombo = new javax.swing.JComboBox<>();
        angleLabel = new javax.swing.JLabel();
        angleSpinner = new javax.swing.JSpinner();
        spinLabel = new javax.swing.JLabel();
        spinDirCombo = new javax.swing.JComboBox<>();
        spiralColorPanel = new javax.swing.JPanel();
        javax.swing.JButton color1Button = colorButtons.get(colorIcons[0]);
        javax.swing.JButton color2Button = colorButtons.get(colorIcons[1]);
        javax.swing.JButton color3Button = colorButtons.get(colorIcons[2]);
        javax.swing.JButton color4Button = colorButtons.get(colorIcons[3]);
        spiralTypeLabel = new javax.swing.JLabel();
        spiralTypeCombo = new javax.swing.JComboBox<>();
        spiralShapeLabel = new javax.swing.JLabel();
        spiralShapeCombo = new javax.swing.JComboBox<>();
        resetButton = new javax.swing.JButton();
        spiralCtrlFiller = new javax.swing.Box.Filler(new java.awt.Dimension(0, 28), new java.awt.Dimension(0, 28), new java.awt.Dimension(32767, 28));
        imageCtrlPanel = new javax.swing.JPanel();
        widthLabel = new javax.swing.JLabel();
        widthSpinner = new javax.swing.JSpinner();
        heightLabel = new javax.swing.JLabel();
        heightSpinner = new javax.swing.JSpinner();
        delayLabel = new javax.swing.JLabel();
        delaySpinner = new javax.swing.JSpinner();
        alwaysScaleToggle = new javax.swing.JCheckBox();
        optimizeDifferenceToggle = new javax.swing.JCheckBox();
        progressBar = new javax.swing.JProgressBar();
        maskEditButton = new javax.swing.JButton();
        ctrlButtonPanel = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        aboutButton = new javax.swing.JButton();
        configButtonPanel = new javax.swing.JPanel();
        saveConfigButton = new javax.swing.JButton();
        loadConfigButton = new javax.swing.JButton();

        printTestButton.setText("Print Data");
        printTestButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printTestButtonActionPerformed(evt);
            }
        });
        debugPopup.add(printTestButton);

        printFPSToggle.setText("Print Frame Rate");
        printFPSToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printFPSToggleActionPerformed(evt);
            }
        });
        debugPopup.add(printFPSToggle);

        inputEnableToggle.setSelected(true);
        inputEnableToggle.setText("Input Enabled");
        inputEnableToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inputEnableToggleActionPerformed(evt);
            }
        });
        debugPopup.add(inputEnableToggle);

        showTestDialogButton.setText("Show Test Dialog");
        showTestDialogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showTestDialogButtonActionPerformed(evt);
            }
        });
        debugPopup.add(showTestDialogButton);

        colorSelector.setClearButtonShown(true);

        maskFCPreview.setFileChooser(maskFC);

        saveFCPreview.setFileChooser(saveFC);

        maskFC.setAccessory(maskFCPreview);
        maskFC.setFileHidingEnabled(false);

        saveFC.setAccessory(saveFCPreview);
        saveFC.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        saveFC.setFileFilter(ImageExtensions.GIF_FILTER);
        saveFC.setFileHidingEnabled(false);

        maskDialog.setTitle("Edit Overlay");
        maskDialog.setMinimumSize(new java.awt.Dimension(640, 480));
        maskDialog.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                maskDialogComponentResized(evt);
            }
        });

        maskTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                maskTabbedPaneStateChanged(evt);
            }
        });

        maskTextPanel.setLayout(new java.awt.BorderLayout());

        maskTextPane.setComponentPopupMenu(maskPopup);
        maskTextPanel.add(maskTextPane, java.awt.BorderLayout.CENTER);

        maskTextScrollPane.setViewportView(maskTextPanel);

        fontButton.setText("Select Font");
        fontButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontButtonActionPerformed(evt);
            }
        });

        boldToggle.setText("Bold");
        boldToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styleToggleActionPerformed(evt);
            }
        });

        italicToggle.setText("Italic");
        italicToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styleToggleActionPerformed(evt);
            }
        });

        fontAntialiasingToggle.setSelected(true);
        fontAntialiasingToggle.setText("Antialiasing");
        fontAntialiasingToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontAntialiasingToggleActionPerformed(evt);
            }
        });

        lineSpacingLabel.setLabelFor(lineSpacingSpinner);
        lineSpacingLabel.setText("Line Spacing:");

        lineSpacingSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, null, null, 1.0d));
        lineSpacingSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                lineSpacingSpinnerStateChanged(evt);
            }
        });

        javax.swing.GroupLayout textMaskCtrlPanelLayout = new javax.swing.GroupLayout(textMaskCtrlPanel);
        textMaskCtrlPanel.setLayout(textMaskCtrlPanelLayout);
        textMaskCtrlPanelLayout.setHorizontalGroup(
            textMaskCtrlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(textMaskCtrlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(textMaskCtrlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(textMaskCtrlPanelLayout.createSequentialGroup()
                        .addComponent(fontButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(boldToggle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(italicToggle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(fontAntialiasingToggle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(lineSpacingLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lineSpacingSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 112, Short.MAX_VALUE))
                    .addComponent(maskTextScrollPane))
                .addContainerGap())
        );
        textMaskCtrlPanelLayout.setVerticalGroup(
            textMaskCtrlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(textMaskCtrlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(maskTextScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 224, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(textMaskCtrlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fontButton)
                    .addComponent(boldToggle)
                    .addComponent(italicToggle)
                    .addComponent(fontAntialiasingToggle)
                    .addComponent(lineSpacingLabel)
                    .addComponent(lineSpacingSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        maskTabbedPane.addTab("Text", textMaskCtrlPanel);

        maskImageCtrlPanel.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 0.5;
        maskImageCtrlPanel.add(filler11, gridBagConstraints);

        loadMaskButton.setText("Load Mask Image");
        loadMaskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMaskButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        maskImageCtrlPanel.add(loadMaskButton, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 0.5;
        maskImageCtrlPanel.add(filler10, gridBagConstraints);

        imgMaskAntialiasingToggle.setSelected(true);
        imgMaskAntialiasingToggle.setText("Antialiasing");
        imgMaskAntialiasingToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imgMaskAntialiasingToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        maskImageCtrlPanel.add(imgMaskAntialiasingToggle, gridBagConstraints);

        maskAlphaCtrlPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Mask Alpha Channel"));
        maskAlphaCtrlPanel.setLayout(new java.awt.GridBagLayout());

        maskAlphaButtons.add(maskAlphaToggle);
        maskAlphaToggle.setSelected(true);
        maskAlphaToggle.setText("Alpha Component Only");
        maskAlphaToggle.setToolTipText("Use only the alpha component of the image for the mask. The alpha component of a given pixel will be the determining factor of whether that pixel will show in the mask.");
        maskAlphaToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskAlphaToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        maskAlphaCtrlPanel.add(maskAlphaToggle, gridBagConstraints);

        maskAlphaButtons.add(maskAlphaGrayToggle);
        maskAlphaGrayToggle.setText("Grayscale");
        maskAlphaGrayToggle.setToolTipText("Treat the image like a grayscale image and derive the mask from that. Black pixels will become fully transparent and white pixels will become fully opaque, with all other shades of grey being varying levels of transparency.");
        maskAlphaGrayToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskAlphaToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        maskAlphaCtrlPanel.add(maskAlphaGrayToggle, gridBagConstraints);

        maskAlphaColorCtrlPanel.setAlignmentX(0.0F);
        maskAlphaColorCtrlPanel.setLayout(new java.awt.GridLayout(1, 0, 6, 7));

        maskAlphaButtons.add(maskAlphaRedToggle);
        maskAlphaRedToggle.setText("Red");
        maskAlphaRedToggle.setToolTipText("Use the red component of the image for the mask. This treats the red component of the image as an alpha channel.");
        maskAlphaRedToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskAlphaToggleActionPerformed(evt);
            }
        });
        maskAlphaColorCtrlPanel.add(maskAlphaRedToggle);

        maskAlphaButtons.add(maskAlphaGreenToggle);
        maskAlphaGreenToggle.setText("Green");
        maskAlphaGreenToggle.setToolTipText("Use the green component of the image for the mask. This treats the green component of the image as an alpha channel.");
        maskAlphaGreenToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskAlphaToggleActionPerformed(evt);
            }
        });
        maskAlphaColorCtrlPanel.add(maskAlphaGreenToggle);

        maskAlphaButtons.add(maskAlphaBlueToggle);
        maskAlphaBlueToggle.setText("Blue");
        maskAlphaBlueToggle.setToolTipText("Use the blue component of the image for the mask. This treats the blue component of the image as an alpha channel.");
        maskAlphaBlueToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskAlphaToggleActionPerformed(evt);
            }
        });
        maskAlphaColorCtrlPanel.add(maskAlphaBlueToggle);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        maskAlphaCtrlPanel.add(maskAlphaColorCtrlPanel, gridBagConstraints);

        maskAlphaInvertToggle.setText("Invert Colors");
        maskAlphaInvertToggle.setToolTipText("This inverts the colors for the image being used as a mask.");
        maskAlphaInvertToggle.setEnabled(false);
        maskAlphaInvertToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskAlphaInvertToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        maskAlphaCtrlPanel.add(maskAlphaInvertToggle, gridBagConstraints);

        maskDesaturateLabel.setLabelFor(maskDesaturateCombo);
        maskDesaturateLabel.setText("Desaturate:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 6);
        maskAlphaCtrlPanel.add(maskDesaturateLabel, gridBagConstraints);

        maskDesaturateCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Luminance", "Luma", "Lightness (HSL)", "Average", "Value (HSV)" }));
        maskDesaturateCombo.setEnabled(false);
        maskDesaturateCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskDesaturateComboActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        maskAlphaCtrlPanel.add(maskDesaturateCombo, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        maskImageCtrlPanel.add(maskAlphaCtrlPanel, gridBagConstraints);

        imgAspectRatioButton.setText("Apply Aspect Ratio");
        imgAspectRatioButton.setEnabled(false);
        imgAspectRatioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imgAspectRatioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        maskImageCtrlPanel.add(imgAspectRatioButton, gridBagConstraints);

        imgMaskPreview.setBorder(javax.swing.BorderFactory.createTitledBorder("Mask Image Preview"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        maskImageCtrlPanel.add(imgMaskPreview, gridBagConstraints);

        maskFrameCtrlPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Frame"));
        maskFrameCtrlPanel.setLayout(new java.awt.BorderLayout(6, 0));

        maskFramePrevButton.setIcon(new FrameNavigationIcon(FrameNavigation.PREVIOUS));
        maskFramePrevButton.setToolTipText("Previous");
        maskFramePrevButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        maskFramePrevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskFramePrevButtonActionPerformed(evt);
            }
        });
        maskFrameCtrlPanel.add(maskFramePrevButton, java.awt.BorderLayout.LINE_START);

        maskFrameLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        maskFrameLabel.setText("1");
        maskFrameCtrlPanel.add(maskFrameLabel, java.awt.BorderLayout.CENTER);

        maskFrameNextButton.setIcon(new FrameNavigationIcon(FrameNavigation.NEXT));
        maskFrameNextButton.setToolTipText("Next");
        maskFrameNextButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        maskFrameNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskFrameNextButtonActionPerformed(evt);
            }
        });
        maskFrameCtrlPanel.add(maskFrameNextButton, java.awt.BorderLayout.LINE_END);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        maskImageCtrlPanel.add(maskFrameCtrlPanel, gridBagConstraints);

        maskImgScalePanel.setLayout(new javax.swing.BoxLayout(maskImgScalePanel, javax.swing.BoxLayout.LINE_AXIS));

        maskImgScaleMethodLabel.setLabelFor(maskImgScaleMethodCombo);
        maskImgScaleMethodLabel.setText("Scale Method:");
        maskImgScalePanel.add(maskImgScaleMethodLabel);
        maskImgScalePanel.add(filler14);

        maskImgScaleMethodCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nearest Neighbor", "Bilinear", "Bicubic", "Smooth", "Thumbnailator" }));
        maskImgScaleMethodCombo.setSelectedIndex(4);
        maskImgScaleMethodCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskImgScaleMethodComboActionPerformed(evt);
            }
        });
        maskImgScalePanel.add(maskImgScaleMethodCombo);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        maskImageCtrlPanel.add(maskImgScalePanel, gridBagConstraints);

        maskTabbedPane.addTab("Image", maskImageCtrlPanel);

        shapeMaskCtrlPanel.setLayout(new java.awt.GridBagLayout());

        shapeMaskSizePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Size"));
        shapeMaskSizePanel.setLayout(new javax.swing.BoxLayout(shapeMaskSizePanel, javax.swing.BoxLayout.LINE_AXIS));

        maskShapeWidthLabel.setLabelFor(maskShapeWidthSpinner);
        maskShapeWidthLabel.setText("Width:");
        shapeMaskSizePanel.add(maskShapeWidthLabel);
        shapeMaskSizePanel.add(filler2);

        maskShapeWidthSpinner.setModel(new javax.swing.SpinnerNumberModel(0.1d, 0.001d, 1.0d, 0.01d));
        maskShapeWidthSpinner.setToolTipText("The percentage of the image width taken up by the heart.");
        maskShapeWidthSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(maskShapeWidthSpinner, "0.##%"));
        maskShapeWidthSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                maskShapeWidthSpinnerStateChanged(evt);
            }
        });
        shapeMaskSizePanel.add(maskShapeWidthSpinner);
        shapeMaskSizePanel.add(filler3);

        maskShapeHeightLabel.setLabelFor(maskShapeHeightSpinner);
        maskShapeHeightLabel.setText("Height:");
        shapeMaskSizePanel.add(maskShapeHeightLabel);
        shapeMaskSizePanel.add(filler17);

        maskShapeHeightSpinner.setModel(new javax.swing.SpinnerNumberModel(0.1d, 0.001d, 1.0d, 0.01d));
        maskShapeHeightSpinner.setToolTipText("The percentage of the image height taken up by the heart.");
        maskShapeHeightSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(maskShapeHeightSpinner, "0.##%"));
        maskShapeHeightSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                maskShapeHeightSpinnerStateChanged(evt);
            }
        });
        shapeMaskSizePanel.add(maskShapeHeightSpinner);
        shapeMaskSizePanel.add(filler18);

        maskShapeLinkSizeToggle.setSelected(true);
        maskShapeLinkSizeToggle.setText("Link Size");
        maskShapeLinkSizeToggle.setToolTipText("Whether the width and height are linked");
        maskShapeLinkSizeToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskShapeLinkSizeToggleActionPerformed(evt);
            }
        });
        shapeMaskSizePanel.add(maskShapeLinkSizeToggle);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 25;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        shapeMaskCtrlPanel.add(shapeMaskSizePanel, gridBagConstraints);

        maskShapeLabel.setLabelFor(maskShapeCombo);
        maskShapeLabel.setText("Shape:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        shapeMaskCtrlPanel.add(maskShapeLabel, gridBagConstraints);

        maskShapeCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Heart", "Star" }));
        maskShapeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskShapeComboActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        shapeMaskCtrlPanel.add(maskShapeCombo, gridBagConstraints);

        maskTabbedPane.addTab("Shape", shapeMaskCtrlPanel);

        maskScaleLabel.setLabelFor(maskScaleSpinner);
        maskScaleLabel.setText("Overlay Scale:");

        maskScaleSpinner.setModel(new javax.swing.SpinnerNumberModel(1.0d, 0.0001d, 10.0d, 0.01d));
        maskScaleSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(maskScaleSpinner, "0.##%"));
        maskScaleSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                maskScaleSpinnerStateChanged(evt);
            }
        });

        resetMaskButton.setText("Reset");
        resetMaskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetMaskButtonActionPerformed(evt);
            }
        });

        maskRotateLabel.setLabelFor(maskRotateSpinner);
        maskRotateLabel.setText("Rotation:");

        maskRotateSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 360.0d, 1.0d));
        maskRotateSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(maskRotateSpinner, "0.00###"));
        maskRotateSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                maskRotateSpinnerStateChanged(evt);
            }
        });

        maskFlipHorizToggle.setText("Flip Horizontally");
        maskFlipHorizToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskFlipHorizToggleActionPerformed(evt);
            }
        });

        maskFlipVertToggle.setText("Flip Vertically");
        maskFlipVertToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskFlipVertToggleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout maskDialogLayout = new javax.swing.GroupLayout(maskDialog.getContentPane());
        maskDialog.getContentPane().setLayout(maskDialogLayout);
        maskDialogLayout.setHorizontalGroup(
            maskDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(maskDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(maskDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(maskTabbedPane)
                    .addGroup(maskDialogLayout.createSequentialGroup()
                        .addGroup(maskDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(maskScaleLabel)
                            .addGroup(maskDialogLayout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(maskRotateLabel)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(maskDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(maskScaleSpinner, javax.swing.GroupLayout.DEFAULT_SIZE, 90, Short.MAX_VALUE)
                            .addComponent(maskRotateSpinner))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(maskDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(maskDialogLayout.createSequentialGroup()
                                .addComponent(maskFlipHorizToggle)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(maskDialogLayout.createSequentialGroup()
                                .addComponent(maskFlipVertToggle)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(resetMaskButton)))))
                .addContainerGap())
        );
        maskDialogLayout.setVerticalGroup(
            maskDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(maskDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(maskTabbedPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(maskDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maskScaleLabel)
                    .addComponent(maskScaleSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(maskFlipHorizToggle))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(maskDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(maskDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(maskRotateSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(maskRotateLabel)
                        .addComponent(maskFlipVertToggle))
                    .addComponent(resetMaskButton))
                .addContainerGap())
        );

        testDialog.setMinimumSize(new java.awt.Dimension(640, 480));

        testCtrlPanel.setLayout(new java.awt.GridBagLayout());

        testSpiralImageLabel.setLabelFor(testSpiralImageSpinner);
        testSpiralImageLabel.setText("Test Image:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 6);
        testCtrlPanel.add(testSpiralImageLabel, gridBagConstraints);

        testSpiralImageSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                testSpiralImageSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 6);
        testCtrlPanel.add(testSpiralImageSpinner, gridBagConstraints);

        testRotateLabel.setLabelFor(testRotateSpinner);
        testRotateLabel.setText("Rotation:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 6);
        testCtrlPanel.add(testRotateLabel, gridBagConstraints);

        testRotateSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, null, 360.0d, 1.0d));
        testRotateSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                testRotateSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        testCtrlPanel.add(testRotateSpinner, gridBagConstraints);

        testScaleLabel.setLabelFor(testScaleSpinner);
        testScaleLabel.setText("1/Scale:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        testCtrlPanel.add(testScaleLabel, gridBagConstraints);

        testScaleSpinner.setModel(new javax.swing.SpinnerNumberModel(1.0d, 0.0d, null, 1.0d));
        testScaleSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                testScaleSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 7);
        testCtrlPanel.add(testScaleSpinner, gridBagConstraints);

        showTestSpiralToggle.setText("Show Test Spiral");
        showTestSpiralToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showTestSpiralToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        testCtrlPanel.add(showTestSpiralToggle, gridBagConstraints);

        testCtrlPanel2.setLayout(new java.awt.GridLayout(0, 4, 6, 7));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        testCtrlPanel.add(testCtrlPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 0.9;
        testCtrlPanel.add(filler16, gridBagConstraints);

        testShowRadiusToggle.setText("Show Radius");
        testShowRadiusToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testShowRadiusToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        testCtrlPanel.add(testShowRadiusToggle, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        testCtrlPanel.add(filler1, gridBagConstraints);

        javax.swing.GroupLayout testDialogLayout = new javax.swing.GroupLayout(testDialog.getContentPane());
        testDialog.getContentPane().setLayout(testDialogLayout);
        testDialogLayout.setHorizontalGroup(
            testDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(testCtrlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
                .addContainerGap())
        );
        testDialogLayout.setVerticalGroup(
            testDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, testDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(testCtrlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                .addContainerGap())
        );

        aboutDialog.setTitle("About "+PROGRAM_NAME);
        aboutDialog.setMinimumSize(new java.awt.Dimension(640, 400));
        aboutDialog.setResizable(false);

        aboutPanel.setProgramName(PROGRAM_NAME);
        aboutPanel.setProgramVersion(PROGRAM_VERSION);
        aboutPanel.setProgramWebsiteText("Visit the Github Repository");
        aboutPanel.setProgramWebsiteURI(java.net.URI.create("https://github.com/Mosblinker/HypnoGifGenerator"));
        aboutPanel.setUpdateButtonIsShown(true);
        aboutPanel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutPanelActionPerformed(evt);
            }
        });
        aboutDialog.getContentPane().add(aboutPanel, java.awt.BorderLayout.CENTER);

        updateCheckDialog.setTitle(PROGRAM_NAME+" Update Checker");
        updateCheckDialog.setMinimumSize(new java.awt.Dimension(400, 196));
        updateCheckDialog.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        updateCheckDialog.setResizable(false);

        updatePanel.setLayout(new java.awt.GridBagLayout());

        updateIconLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 18);
        updatePanel.add(updateIconLabel, gridBagConstraints);

        updateTextLabel.setText("A new version of "+PROGRAM_NAME+" is available.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 18, 0);
        updatePanel.add(updateTextLabel, gridBagConstraints);

        currentVersTextLabel.setLabelFor(currentVersLabel);
        currentVersTextLabel.setText("Current Version:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 12);
        updatePanel.add(currentVersTextLabel, gridBagConstraints);

        latestVersTextLabel.setLabelFor(latestVersLabel);
        latestVersTextLabel.setText("Latest Version:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 12);
        updatePanel.add(latestVersTextLabel, gridBagConstraints);

        checkUpdatesAtStartToggle.setSelected(true);
        checkUpdatesAtStartToggle.setText("Check for Updates at startup");
        checkUpdatesAtStartToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkUpdatesAtStartToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(13, 0, 0, 0);
        updatePanel.add(checkUpdatesAtStartToggle, gridBagConstraints);

        currentVersLabel.setText(PROGRAM_VERSION);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        updatePanel.add(currentVersLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHWEST;
        updatePanel.add(latestVersLabel, gridBagConstraints);

        updateContinueButton.setText("Continue");
        updateContinueButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateContinueButtonActionPerformed(evt);
            }
        });

        updateOpenButton.setText("Go to web page");
        updateOpenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateOpenButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout updateCheckDialogLayout = new javax.swing.GroupLayout(updateCheckDialog.getContentPane());
        updateCheckDialog.getContentPane().setLayout(updateCheckDialogLayout);
        updateCheckDialogLayout.setHorizontalGroup(
            updateCheckDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updateCheckDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(updateCheckDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(updatePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, updateCheckDialogLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(updateOpenButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(updateContinueButton)))
                .addContainerGap())
        );
        updateCheckDialogLayout.setVerticalGroup(
            updateCheckDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(updateCheckDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(updatePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(13, 13, 13)
                .addGroup(updateCheckDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(updateContinueButton)
                    .addComponent(updateOpenButton))
                .addContainerGap())
        );

        configFC.setFileFilter(ConfigExtensions.CONFIG_FILTER);
        configFC.setFileHidingEnabled(false);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(PROGRAM_NAME);
        setLocationByPlatform(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                formComponentMoved(evt);
            }
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        framesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Frames"));
        framesPanel.setLayout(new java.awt.GridBagLayout());

        frameNumberLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        frameNumberLabel.setAlignmentX(0.5F);
        frameNumberLabel.setMaximumSize(new java.awt.Dimension(65535, 64));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        framesPanel.add(frameNumberLabel, gridBagConstraints);

        frameNavPanel.setLayout(new javax.swing.BoxLayout(frameNavPanel, javax.swing.BoxLayout.LINE_AXIS));

        frameFirstButton.setIcon(new FrameNavigationIcon(FrameNavigation.FIRST));
        frameFirstButton.setToolTipText("First");
        frameFirstButton.setEnabled(false);
        frameFirstButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        frameFirstButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frameFirstButtonActionPerformed(evt);
            }
        });
        frameNavPanel.add(frameFirstButton);
        frameNavPanel.add(filler4);

        framePrevButton.setIcon(new FrameNavigationIcon(FrameNavigation.PREVIOUS));
        framePrevButton.setToolTipText("Previous");
        framePrevButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        framePrevButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                framePrevButtonActionPerformed(evt);
            }
        });
        frameNavPanel.add(framePrevButton);
        frameNavPanel.add(filler5);

        framePlayButton.setIcon(new FrameNavigationIcon(FrameNavigation.PLAY));
        framePlayButton.setToolTipText("Play/Pause");
        framePlayButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        framePlayButton.setRolloverIcon(new FrameNavigationIcon(FrameNavigation.PLAY));
        framePlayButton.setSelectedIcon(new FrameNavigationIcon(FrameNavigation.PAUSE));
        framePlayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                framePlayButtonActionPerformed(evt);
            }
        });
        frameNavPanel.add(framePlayButton);
        frameNavPanel.add(filler8);

        frameSlider.setMajorTickSpacing(8);
        frameSlider.setMaximum(55);
        frameSlider.setMinorTickSpacing(1);
        frameSlider.setPaintTicks(true);
        frameSlider.setValue(0);
        frameSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                frameSliderStateChanged(evt);
            }
        });
        frameNavPanel.add(frameSlider);
        frameNavPanel.add(filler9);

        frameStopButton.setIcon(new FrameNavigationIcon(FrameNavigation.STOP));
        frameStopButton.setToolTipText("Stop");
        frameStopButton.setEnabled(false);
        frameStopButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        frameStopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frameStopButtonActionPerformed(evt);
            }
        });
        frameNavPanel.add(frameStopButton);
        frameNavPanel.add(filler6);

        frameNextButton.setIcon(new FrameNavigationIcon(FrameNavigation.NEXT));
        frameNextButton.setToolTipText("Next");
        frameNextButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        frameNextButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frameNextButtonActionPerformed(evt);
            }
        });
        frameNavPanel.add(frameNextButton);
        frameNavPanel.add(filler7);

        frameLastButton.setIcon(new FrameNavigationIcon(FrameNavigation.LAST));
        frameLastButton.setToolTipText("Last");
        frameLastButton.setEnabled(false);
        frameLastButton.setMargin(new java.awt.Insets(2, 0, 2, 0));
        frameLastButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frameLastButtonActionPerformed(evt);
            }
        });
        frameNavPanel.add(frameLastButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        framesPanel.add(frameNavPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        framesPanel.add(filler12, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.3;
        framesPanel.add(filler13, gridBagConstraints);

        previewPanel.setLayout(new java.awt.GridLayout(1, 2, 6, 0));

        previewSpiralPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Hypno Gif Preview"));
        previewSpiralPanel.setLayout(new java.awt.BorderLayout());
        previewSpiralPanel.add(previewLabel, java.awt.BorderLayout.CENTER);

        previewPanel.add(previewSpiralPanel);

        previewMaskPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Message Mask Preview"));
        previewMaskPanel.setLayout(new java.awt.BorderLayout());
        previewMaskPanel.add(maskPreviewLabel, java.awt.BorderLayout.CENTER);

        previewPanel.add(previewMaskPanel);

        spiralCtrlPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Spiral Controls"));
        spiralCtrlPanel.setLayout(new java.awt.GridBagLayout());

        radiusLabel.setLabelFor(radiusSpinner);
        radiusLabel.setText("Radius:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 6);
        spiralCtrlPanel.add(radiusLabel, gridBagConstraints);

        radiusSpinner.setModel(new javax.swing.SpinnerNumberModel(1.0d, 0.001d, 100000.0d, 1.0d));
        radiusSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(radiusSpinner, "0.00######"));
        radiusSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                radiusSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(radiusSpinner, gridBagConstraints);

        baseLabel.setLabelFor(baseSpinner);
        baseLabel.setText("Base:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 6);
        spiralCtrlPanel.add(baseLabel, gridBagConstraints);

        baseSpinner.setModel(new javax.swing.SpinnerNumberModel(1.0d, 1.0d, 1000.0d, 0.1d));
        baseSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(baseSpinner, "0.000#####"));
        baseSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                baseSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(baseSpinner, gridBagConstraints);

        balanceLabel.setLabelFor(balanceSpinner);
        balanceLabel.setText("Balance:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 6);
        spiralCtrlPanel.add(balanceLabel, gridBagConstraints);

        balanceSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, -1.0d, 1.0d, 0.01d));
        balanceSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(balanceSpinner, "0.000#########"));
        balanceSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                balanceSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(balanceSpinner, gridBagConstraints);

        dirLabel.setLabelFor(dirCombo);
        dirLabel.setText("Direction:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 6);
        spiralCtrlPanel.add(dirLabel, gridBagConstraints);

        dirCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Clockwise", "Counter-Clockwise" }));
        dirCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dirComboActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(dirCombo, gridBagConstraints);

        angleLabel.setLabelFor(angleSpinner);
        angleLabel.setText("Rotation:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 6);
        spiralCtrlPanel.add(angleLabel, gridBagConstraints);

        angleSpinner.setModel(new javax.swing.SpinnerNumberModel(0.0d, 0.0d, 360.0d, 1.0d));
        angleSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(angleSpinner, "0.00###"));
        angleSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                angleSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(angleSpinner, gridBagConstraints);

        spinLabel.setLabelFor(spinDirCombo);
        spinLabel.setText("Spin:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 6);
        spiralCtrlPanel.add(spinLabel, gridBagConstraints);

        spinDirCombo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Clockwise", "Counter-Clockwise" }));
        spinDirCombo.setToolTipText("This controls the direction in which the spiral will spin in the animation.");
        spinDirCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spinDirComboActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(spinDirCombo, gridBagConstraints);

        spiralColorPanel.setLayout(new java.awt.GridLayout(2, 0, 6, 7));

        color1Button.setText("Color 1");
        color1Button.setToolTipText("This is the first color of the main spiral");
        color1Button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        color1Button.setIconTextGap(8);
        color1Button.setMargin(new java.awt.Insets(5, 0, 5, 0));
        color1Button.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        spiralColorPanel.add(color1Button);

        color2Button.setText("Color 2");
        color2Button.setToolTipText("This is the second color of the main spiral");
        color2Button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        color2Button.setIconTextGap(8);
        color2Button.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        spiralColorPanel.add(color2Button);

        color3Button.setText("Color 3");
        color3Button.setToolTipText("This is the first color of the message over the spiral");
        color3Button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        color3Button.setIconTextGap(8);
        color3Button.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        spiralColorPanel.add(color3Button);

        color4Button.setText("Color 4");
        color4Button.setToolTipText("This is the second color of the message over the spiral");
        color4Button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        color4Button.setIconTextGap(8);
        color4Button.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        spiralColorPanel.add(color4Button);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(spiralColorPanel, gridBagConstraints);

        spiralTypeLabel.setLabelFor(spiralTypeCombo);
        spiralTypeLabel.setText("Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 6);
        spiralCtrlPanel.add(spiralTypeLabel, gridBagConstraints);

        spiralTypeCombo.setModel(new DefaultComboBoxModel<>(spiralPainters));
        spiralTypeCombo.setRenderer(new SpiralPainterListCellRenderer());
        spiralTypeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spiralTypeComboActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(spiralTypeCombo, gridBagConstraints);

        spiralShapeLabel.setLabelFor(spiralShapeCombo);
        spiralShapeLabel.setText("Shape:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 6);
        spiralCtrlPanel.add(spiralShapeLabel, gridBagConstraints);

        spiralShapeCombo.setModel(new DefaultComboBoxModel<>(SpiralShape.values()));
        spiralShapeCombo.setRenderer(new SpiralShapeListCellRenderer());
        spiralShapeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                spiralShapeComboActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(spiralShapeCombo, gridBagConstraints);

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.ipadx = 10;
        spiralCtrlPanel.add(resetButton, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(spiralCtrlFiller, gridBagConstraints);

        imageCtrlPanel.setLayout(new java.awt.GridBagLayout());

        widthLabel.setLabelFor(widthSpinner);
        widthLabel.setText("Width:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 6);
        imageCtrlPanel.add(widthLabel, gridBagConstraints);

        widthSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 9999, 1));
        widthSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                imageSizeSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = -9;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 6);
        imageCtrlPanel.add(widthSpinner, gridBagConstraints);

        heightLabel.setLabelFor(heightSpinner);
        heightLabel.setText("Height:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 6);
        imageCtrlPanel.add(heightLabel, gridBagConstraints);

        heightSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 9999, 1));
        heightSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                imageSizeSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        imageCtrlPanel.add(heightSpinner, gridBagConstraints);

        delayLabel.setLabelFor(delaySpinner);
        delayLabel.setText("Duration:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        imageCtrlPanel.add(delayLabel, gridBagConstraints);

        delaySpinner.setModel(new javax.swing.SpinnerNumberModel(10, 10, 100, 10));
        delaySpinner.setToolTipText("This is the duration for each frame of animation, in milliseconds.");
        delaySpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                delaySpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 6, 0, 6);
        imageCtrlPanel.add(delaySpinner, gridBagConstraints);

        alwaysScaleToggle.setText("Scale Preview");
        alwaysScaleToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alwaysScaleToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        imageCtrlPanel.add(alwaysScaleToggle, gridBagConstraints);

        optimizeDifferenceToggle.setText("Optimize for Difference");
        optimizeDifferenceToggle.setToolTipText("This indicates whether the animation will be optimized for the difference between frames. This may reduce the file size at the cost of quality.");
        optimizeDifferenceToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                optimizeDifferenceToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        imageCtrlPanel.add(optimizeDifferenceToggle, gridBagConstraints);

        progressBar.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                progressBarStateChanged(evt);
            }
        });
        progressBar.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                progressBarPropertyChange(evt);
            }
        });

        maskEditButton.setText("Edit Overlay");
        maskEditButton.setToolTipText("Edit the overlay that appears over the spiral.");
        maskEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskEditButtonActionPerformed(evt);
            }
        });

        ctrlButtonPanel.setLayout(new java.awt.GridLayout(1, 0, 6, 0));

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        ctrlButtonPanel.add(saveButton);

        aboutButton.setText("About");
        aboutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutButtonActionPerformed(evt);
            }
        });
        ctrlButtonPanel.add(aboutButton);

        configButtonPanel.setLayout(new java.awt.GridLayout(1, 0, 6, 0));

        saveConfigButton.setText("Save Config");
        saveConfigButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveConfigButtonActionPerformed(evt);
            }
        });
        configButtonPanel.add(saveConfigButton);

        loadConfigButton.setText("Load Config");
        loadConfigButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadConfigButtonActionPerformed(evt);
            }
        });
        configButtonPanel.add(loadConfigButton);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(framesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                    .addComponent(previewPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spiralCtrlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(maskEditButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ctrlButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(imageCtrlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(configButtonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(previewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(framesPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(spiralCtrlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(imageCtrlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(maskEditButton)
                            .addComponent(ctrlButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(configButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
            // Get the file to save to
        File file = showSaveFileChooser(saveFC);
        config.setSelectedFile(saveFC);
            // If a file was selected
        if (file != null){
            fileWorker = new AnimationSaver(file);
            fileWorker.execute();
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void loadMaskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMaskButtonActionPerformed
            // Get the image file to load
        File file = showOpenFileChooser(maskFC);
        config.setSelectedFile(maskFC);
            // If a file was selected
        if (file != null){
            fileWorker = new ImageLoader(file);
            fileWorker.execute();
        }
    }//GEN-LAST:event_loadMaskButtonActionPerformed

    private void frameFirstButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frameFirstButtonActionPerformed
        setFrameIndex(0);
    }//GEN-LAST:event_frameFirstButtonActionPerformed

    private void framePrevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_framePrevButtonActionPerformed
        setFrameIndex((SPIRAL_FRAME_COUNT+frameIndex-1)%SPIRAL_FRAME_COUNT);
    }//GEN-LAST:event_framePrevButtonActionPerformed

    private void framePlayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_framePlayButtonActionPerformed
        updateFrameControls();
        updateControlsEnabled();
        frameTime = System.currentTimeMillis();
        frameTimeTotal = 0;
        frameTotal = 0;
            // If the animation is now playing
        if (framePlayButton.isSelected()){
            animationTimer.restart();
        } else{
            animationTimer.stop();
        }
    }//GEN-LAST:event_framePlayButtonActionPerformed

    private void frameSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_frameSliderStateChanged
        getLogger().entering(this.getClass().getName(), "frameSliderStateChanged");
        setFrameIndex(frameSlider.getValue());
        getLogger().exiting(this.getClass().getName(), "frameSliderStateChanged");
    }//GEN-LAST:event_frameSliderStateChanged

    private void frameStopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frameStopButtonActionPerformed
        framePlayButton.setSelected(false);
        updateFrameControls();
        updateControlsEnabled();
        animationTimer.stop();
        setFrameIndex(0);
    }//GEN-LAST:event_frameStopButtonActionPerformed

    private void frameNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frameNextButtonActionPerformed
        setFrameIndex((frameIndex+1)%SPIRAL_FRAME_COUNT);
    }//GEN-LAST:event_frameNextButtonActionPerformed

    private void frameLastButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frameLastButtonActionPerformed
        setFrameIndex(SPIRAL_FRAME_COUNT-1);
    }//GEN-LAST:event_frameLastButtonActionPerformed

    private void printTestButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printTestButtonActionPerformed
            // Get the index of the selected spiral type
        int selected = spiralTypeCombo.getSelectedIndex();
            // Go through the spiral painters
        for (int i = 0; i < spiralPainters.length; i++){
            System.out.printf("%1s Painter %2d: %s%n",(i == selected) ? "*":"",
                    i,spiralPainters[i]);
        }
        System.out.println("Bounds: " + getBounds());
        System.out.println("Rotation: " + getFrameRotation(frameIndex,getSpiralPainter()));
    }//GEN-LAST:event_printTestButtonActionPerformed
    /**
     * 
     * @param evt
     */
    private void updateProgramBoundsInConfig(java.awt.event.ComponentEvent evt){
            // If the program is not maximized
        if ((getExtendedState() & JFrame.MAXIMIZED_BOTH) == 0)
                // Put the size of the program into the preference node
            config.setProgramBounds(this);
    }
    
    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        updateProgramBoundsInConfig(evt);
    }//GEN-LAST:event_formComponentResized

    private void formComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentMoved
        updateProgramBoundsInConfig(evt);
    }//GEN-LAST:event_formComponentMoved

    private void radiusSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_radiusSpinnerStateChanged
            // Set the radius for the currently selected spiral painter
        getSpiralPainter().setSpiralRadius((double) radiusSpinner.getValue());
    }//GEN-LAST:event_radiusSpinnerStateChanged

    private void baseSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_baseSpinnerStateChanged
            // Get the currently selected spiral painter
        SpiralPainter painter = getSpiralPainter();
            // If the spiral painter is logarithmic in nature
        if (painter instanceof LogarithmicSpiral){
                // Set the spiral's base
            ((LogarithmicSpiral) painter).setBase((double) baseSpinner.getValue());
        }
    }//GEN-LAST:event_baseSpinnerStateChanged

    private void balanceSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_balanceSpinnerStateChanged
            // Set the balance for the currently selected spiral painter
        getSpiralPainter().setBalance((double) balanceSpinner.getValue());
    }//GEN-LAST:event_balanceSpinnerStateChanged

    private void alwaysScaleToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alwaysScaleToggleActionPerformed
        previewLabel.setImageAlwaysScaled(alwaysScaleToggle.isSelected());
        maskPreviewLabel.setImageAlwaysScaled(alwaysScaleToggle.isSelected());
        config.setImageAlwaysScaled(alwaysScaleToggle.isSelected());
    }//GEN-LAST:event_alwaysScaleToggleActionPerformed

    private void printFPSToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printFPSToggleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_printFPSToggleActionPerformed

    private void dirComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dirComboActionPerformed
            // Set the direction for the currently selected spiral
        getSpiralPainter().setClockwise(dirCombo.getSelectedIndex() == 0);
    }//GEN-LAST:event_dirComboActionPerformed

    private void spinDirComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spinDirComboActionPerformed
            // Set the spin direction for the currently selected spiral
        getSpiralPainter().setSpinClockwise(spinDirCombo.getSelectedIndex() == 0);
    }//GEN-LAST:event_spinDirComboActionPerformed

    private void angleSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_angleSpinnerStateChanged
            // Set the base rotation for the currently selected spiral painter
        getSpiralPainter().setRotation((double)angleSpinner.getValue());
        refreshPreview();
    }//GEN-LAST:event_angleSpinnerStateChanged

    private void fontButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontButtonActionPerformed
            // Create a font dialog to select the font to use
        FontDialog fontSelector = new FontDialog(this,"Select Font For Overlay",true);
            // If the font selector's size is not null
        if (fontDim != null){
            fontDim.width = Math.max(fontDim.width, 540);
            fontDim.height = Math.max(fontDim.height, 400);
        } else
            fontDim = new Dimension(540, 400);
            // Set the size for the font dialog
        SwingExtendedUtilities.setComponentSize(fontSelector, fontDim);
            // Set the font dialog's location to be relative to this program
        fontSelector.setLocationRelativeTo(this);
            // Set the currently selected font to the current font
        fontSelector.setSelectedFont(maskTextPane.getFont().deriveFont(Font.PLAIN));
            // Show the font dialog
        fontSelector.setVisible(true);
            // If the user did not cancel the font selection
        if (!fontSelector.isCancelSelected()){
                // Get the selected font and set its style
            Font font = fontSelector.getSelectedFont().deriveFont(getFontStyle());
            maskTextPane.setFont(font);
            config.setMaskFont(font);
                // Refresh the text mask and preview
            refreshPreview(0);
        }
        fontDim = fontSelector.getSize(fontDim);
        config.setMaskFontSelectorSize(fontDim);
    }//GEN-LAST:event_fontButtonActionPerformed

    private void maskDialogComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_maskDialogComponentResized
        config.setComponentSize(maskDialog);
    }//GEN-LAST:event_maskDialogComponentResized

    private void styleToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_styleToggleActionPerformed
            // Get the font's style
        int style = getFontStyle();
        config.setMaskFontStyle(style);
        maskTextPane.setFont(maskTextPane.getFont().deriveFont(style));
            // Refresh the text mask and preview
        refreshPreview(0);
    }//GEN-LAST:event_styleToggleActionPerformed

    private void fontAntialiasingToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontAntialiasingToggleActionPerformed
        overlayMask.textPainter.setAntialiasingEnabled(fontAntialiasingToggle.isSelected());
    }//GEN-LAST:event_fontAntialiasingToggleActionPerformed

    private void lineSpacingSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_lineSpacingSpinnerStateChanged
        overlayMask.textPainter.setLineSpacing((double)lineSpacingSpinner.getValue());
    }//GEN-LAST:event_lineSpacingSpinnerStateChanged

    private void maskEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskEditButtonActionPerformed
        maskDialog.setLocationRelativeTo(this);
        maskDialog.setVisible(true);
    }//GEN-LAST:event_maskEditButtonActionPerformed

    private void maskTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_maskTabbedPaneStateChanged
        config.setMaskType(maskTabbedPane.getSelectedIndex());
        maskPreviewLabel.repaint();
        refreshPreview();
    }//GEN-LAST:event_maskTabbedPaneStateChanged

    private void maskScaleSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_maskScaleSpinnerStateChanged
        config.setMaskScale(getMaskScale());
        maskPreviewLabel.repaint();
        refreshPreview();
    }//GEN-LAST:event_maskScaleSpinnerStateChanged

    private void imageSizeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_imageSizeSpinnerStateChanged
        config.setImageSize(getImageWidth(), getImageHeight());
            // Refresh the mask and preview
        refreshPreview(-1);
    }//GEN-LAST:event_imageSizeSpinnerStateChanged

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
            // Go through the color icons
        for (int i = 0; i < colorIcons.length; i++){
            colorIcons[i].setColor(DEFAULT_SPIRAL_COLORS[i]);
            config.setSpiralColor(i, null);
            colorButtons.get(colorIcons[i]).repaint();
        }
        widthSpinner.setValue(DEFAULT_SPIRAL_WIDTH);
        heightSpinner.setValue(DEFAULT_SPIRAL_HEIGHT);
            // Go through the spiral painters
        for (SpiralPainter painter : spiralPainters){
            painter.reset();
        }
        loadSpiralPainter();
    }//GEN-LAST:event_resetButtonActionPerformed

    private void imgMaskAntialiasingToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imgMaskAntialiasingToggleActionPerformed
        config.setMaskImageAntialiased(imgMaskAntialiasingToggle.isSelected());
            // Refresh the image mask and preview
        refreshPreview(1);
    }//GEN-LAST:event_imgMaskAntialiasingToggleActionPerformed
    /**
     * 
     * @param evt 
     */
    private void progressBarStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_progressBarStateChanged
        updateProgressString();
    }//GEN-LAST:event_progressBarStateChanged
    /**
     * 
     * @param evt 
     */
    private void progressBarPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_progressBarPropertyChange
            // If the property name is not null
        if (evt.getPropertyName() != null){
                // Determine what to do with the property name
            switch(evt.getPropertyName()){
                    // If the progress bar's indeterminate state or the string 
                    // painted get changed
                case("indeterminate"):
                case("stringPainted"):
                    updateProgressString();
            }
        }
    }//GEN-LAST:event_progressBarPropertyChange

    private void inputEnableToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inputEnableToggleActionPerformed
        setInputEnabled(inputEnableToggle.isSelected());
    }//GEN-LAST:event_inputEnableToggleActionPerformed

    private void showTestSpiralToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showTestSpiralToggleActionPerformed
        previewLabel.setIcon((showTestSpiralToggle.isSelected()) ? testSpiralIcon : spiralIcon);
    }//GEN-LAST:event_showTestSpiralToggleActionPerformed

    private void showTestDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showTestDialogButtonActionPerformed
        testDialog.setVisible(true);
    }//GEN-LAST:event_showTestDialogButtonActionPerformed

    private void testSpiralImageSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_testSpiralImageSpinnerStateChanged
            // If the test spiral is being shown
        if (showTestSpiralToggle.isSelected())
            previewLabel.repaint();
            // Get the index for the test image
        int index = (int) testSpiralImageSpinner.getValue();
        config.setDebugTestImage(index);
           // If the index for the test image is within range of the test images
        if (index >= 0 && index < testSpiralIcon.images.size()){
                // Set the first color of the spiral to transparent
            testSpiralIcon.model.setColor1(TRANSPARENT_COLOR);
                // Set the second color to a transparent green
            testSpiralIcon.model.setColor2(new Color(0x8000FF00,true));
        } else {
                // Set the first color of the spiral to white
            testSpiralIcon.model.setColor1(Color.WHITE);
                // Set the second color of the spiral to black
            testSpiralIcon.model.setColor2(Color.BLACK);
        }
    }//GEN-LAST:event_testSpiralImageSpinnerStateChanged

    private void testRotateSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_testRotateSpinnerStateChanged
            // If the test spiral is being shown
        if (showTestSpiralToggle.isSelected())
            previewLabel.repaint();
            // Get the rotation for the test spiral
        double rotation = (double)testRotateSpinner.getValue();
        config.setDebugTestRotation(rotation);
        testSpiralIcon.model.setRotation(rotation);
    }//GEN-LAST:event_testRotateSpinnerStateChanged

    private void testScaleSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_testScaleSpinnerStateChanged
            // If the test spiral is being shown
        if (showTestSpiralToggle.isSelected())
            previewLabel.repaint();
        config.setDebugTestScale((double)testScaleSpinner.getValue());
    }//GEN-LAST:event_testScaleSpinnerStateChanged

    private void spiralTypeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spiralTypeComboActionPerformed
        getLogger().log(Level.FINE, "Setting spiral type to type {0}", 
                spiralTypeCombo.getSelectedIndex());
        config.setSpiralType(spiralTypeCombo.getSelectedIndex());
        loadSpiralPainter();
        refreshPreview();
    }//GEN-LAST:event_spiralTypeComboActionPerformed

    private void delaySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_delaySpinnerStateChanged
            // Get the value for the duration from the duration spinner
        int value = ((Integer) delaySpinner.getValue());
            // If the duration is not a multiple of 10
        if (value % 10 != 0){
                // Provide error feedback
            UIManager.getLookAndFeel().provideErrorFeedback(delaySpinner);
                // Make it to be a multiple of 10
            delaySpinner.setValue(value - (value % 10));
            return;
        }
        config.setFrameDuration(value);
        animationTimer.setDelay(value);
        animationTimer.setInitialDelay(value);
    }//GEN-LAST:event_delaySpinnerStateChanged

    private void spiralShapeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spiralShapeComboActionPerformed
            // Get the currently selected spiral painter
        SpiralPainter painter = getSpiralPainter();
            // If the spiral is shaped
        if (painter instanceof ShapedSpiral){
                // Set the shape of the spiral for the currently selected spiral
            ((ShapedSpiral) painter).setShape(spiralShapeCombo.getItemAt(
                    spiralShapeCombo.getSelectedIndex()));
        }
    }//GEN-LAST:event_spiralShapeComboActionPerformed

    private void maskAlphaToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskAlphaToggleActionPerformed
        config.setMaskAlphaIndex(maskAlphaButtons);
        updateMaskAlphaControlsEnabled();
            // Refresh the image mask and preview
        refreshPreview(1);
    }//GEN-LAST:event_maskAlphaToggleActionPerformed

    private void maskAlphaInvertToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskAlphaInvertToggleActionPerformed
        config.setMaskImageInverted(maskAlphaInvertToggle.isSelected());
            // Refresh the image mask and preview
        refreshPreview(1);
    }//GEN-LAST:event_maskAlphaInvertToggleActionPerformed

    private void maskDesaturateComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskDesaturateComboActionPerformed
        config.setMaskDesaturateMode(maskDesaturateCombo.getSelectedIndex());
            // Refresh the image mask and preview
        refreshPreview(1);
    }//GEN-LAST:event_maskDesaturateComboActionPerformed

    private void testShowRadiusToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testShowRadiusToggleActionPerformed
            // If the test spiral is being shown
        if (showTestSpiralToggle.isSelected())
            previewLabel.repaint();
    }//GEN-LAST:event_testShowRadiusToggleActionPerformed

    private void resetMaskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetMaskButtonActionPerformed
            // Determine which mask to reset based off the current mask
        switch (maskTabbedPane.getSelectedIndex()){
                // If the mask is text
            case(0):
                maskTextPane.setText("");
                break;
                // If the mask is an image
            case(1):
                overlayImages.clear();
                imgMaskPreview.setIcon(null);
                maskAlphaToggle.setSelected(true);
                maskAlphaInvertToggle.setSelected(false);
                maskDesaturateCombo.setSelectedIndex(0);
                updateMaskAlphaControlsEnabled();
                config.setMaskAlphaIndex(maskAlphaButtons);
                config.setMaskImageInverted(maskAlphaInvertToggle.isSelected());
                config.setMaskDesaturateMode(maskDesaturateCombo.getSelectedIndex());
                config.setMaskImageFile(null);
                imgAspectRatioButton.setEnabled(false);
                maskImgScaleMethodCombo.setSelectedIndex(SpiralGeneratorUtilities.IMAGE_SCALING_METHOD_THUMBNAILATOR);
                config.setMaskImageInterpolation(maskImgScaleMethodCombo.getSelectedIndex());
                break;
                // If the mask is a shape
            case(2):
                maskShapeLinkSizeToggle.setSelected(true);
                maskShapeWidthSpinner.setValue(0.1);
                updateMaskShapeControlsEnabled();
                config.setMaskShapeSizeLinked(maskShapeLinkSizeToggle.isSelected());
                maskShapeCombo.setSelectedIndex(0);
                config.setMaskShapeType(0);
        }
        overlayMask.reset(maskTabbedPane.getSelectedIndex());
        maskScaleSpinner.setValue(1.0);
        maskRotateSpinner.setValue(0.0);
        maskFlipHorizToggle.setSelected(false);
        maskFlipVertToggle.setSelected(false);
        config.setMaskFlag(SpiralGeneratorConfig.MASK_FLIP_HORIZONTAL_FLAG | 
                SpiralGeneratorConfig.MASK_FLIP_VERTICAL_FLAG, false);
    }//GEN-LAST:event_resetMaskButtonActionPerformed

    private void maskShapeWidthSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_maskShapeWidthSpinnerStateChanged
            // Get the value for the width of the mask shape
        double value = (double) maskShapeWidthSpinner.getValue();
        config.setMaskShapeWidth(value);
            // If the width and height are linked
        if (maskShapeLinkSizeToggle.isSelected())
                // Set the height of the mask shape to the width
            maskShapeHeightSpinner.setValue(value);
            // Refresh the shape mask and preview
        refreshPreview(2);
    }//GEN-LAST:event_maskShapeWidthSpinnerStateChanged

    private void maskShapeHeightSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_maskShapeHeightSpinnerStateChanged
        config.setMaskShapeHeight((double) maskShapeHeightSpinner.getValue());
            // If the width and height are linked
        if (maskShapeLinkSizeToggle.isSelected())
            return;
            // Refresh the shape mask and preview
        refreshPreview(2);
    }//GEN-LAST:event_maskShapeHeightSpinnerStateChanged

    private void maskShapeLinkSizeToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskShapeLinkSizeToggleActionPerformed
        updateMaskShapeControlsEnabled();
        config.setMaskShapeSizeLinked(maskShapeLinkSizeToggle.isSelected());
            // If the width and height are linked
        if (maskShapeLinkSizeToggle.isSelected()){
            maskShapeHeightSpinner.setValue((double) maskShapeWidthSpinner.getValue());
                // Refresh the shape mask and preview
            refreshPreview(2);
        }
    }//GEN-LAST:event_maskShapeLinkSizeToggleActionPerformed

    private void aboutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutButtonActionPerformed
            // Make the about dialog location relative to the program
        aboutDialog.setLocationRelativeTo(this);
            // Show the about dialog
        aboutDialog.setVisible(true);
    }//GEN-LAST:event_aboutButtonActionPerformed

    private void updateContinueButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateContinueButtonActionPerformed
        updateCheckDialog.setVisible(false);
    }//GEN-LAST:event_updateContinueButtonActionPerformed

    private void updateOpenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateOpenButtonActionPerformed
            // Get the update URL
        String url = updateChecker.getUpdateUrl();
        try {   // Try to open the update URL in the user's web browser
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (URISyntaxException | IOException ex) {
            getLogger().log(Level.WARNING,"Could not open update URL "+url,ex);
        }
    }//GEN-LAST:event_updateOpenButtonActionPerformed

    private void checkUpdatesAtStartToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkUpdatesAtStartToggleActionPerformed
        config.setCheckForUpdateAtStartup(checkUpdatesAtStartToggle.isSelected());
    }//GEN-LAST:event_checkUpdatesAtStartToggleActionPerformed

    private void maskRotateSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_maskRotateSpinnerStateChanged
        config.setMaskRotation(getMaskRotation());
            // Refresh the mask and preview
        refreshPreview(-1);
            // If the overlay image list is not empty
        if (!overlayImages.isEmpty())
            imgMaskPreview.repaint();
    }//GEN-LAST:event_maskRotateSpinnerStateChanged

    private void maskFlipHorizToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskFlipHorizToggleActionPerformed
        config.setMaskFlippedHorizontally(maskFlipHorizToggle.isSelected());
        maskPreviewLabel.repaint();
            // Refresh the preview
        refreshPreview();
            // If the overlay image list is not empty
        if (!overlayImages.isEmpty())
            imgMaskPreview.repaint();
    }//GEN-LAST:event_maskFlipHorizToggleActionPerformed

    private void maskFlipVertToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskFlipVertToggleActionPerformed
        config.setMaskFlippedVertically(maskFlipVertToggle.isSelected());
        maskPreviewLabel.repaint();
            // Refresh the preview
        refreshPreview();
            // If the overlay image list is not empty
        if (!overlayImages.isEmpty())
            imgMaskPreview.repaint();
    }//GEN-LAST:event_maskFlipVertToggleActionPerformed

    private void imgAspectRatioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imgAspectRatioButtonActionPerformed
            // Get the overlay image
        BufferedImage image = getOverlayImage();
            // If the overlay image is null
        if (image == null)
            return;
            // Get the new target size for the image
        Dimension dim = SpiralGeneratorUtilities.getTargetSize(image, 
                getImageWidth(), getImageHeight(), false);
            // Set the width of the spiral
        widthSpinner.setValue(dim.width);
            // Set the height of the spiral
        heightSpinner.setValue(dim.height);
    }//GEN-LAST:event_imgAspectRatioButtonActionPerformed

    private void maskFramePrevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskFramePrevButtonActionPerformed
        setOverlayImage(overlayImageIndex-1);
    }//GEN-LAST:event_maskFramePrevButtonActionPerformed

    private void maskFrameNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskFrameNextButtonActionPerformed
        setOverlayImage(overlayImageIndex+1);
    }//GEN-LAST:event_maskFrameNextButtonActionPerformed

    private void maskImgScaleMethodComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskImgScaleMethodComboActionPerformed
        config.setMaskImageInterpolation(maskImgScaleMethodCombo.getSelectedIndex());
            // Refresh the image mask and preview
        refreshPreview(1);
    }//GEN-LAST:event_maskImgScaleMethodComboActionPerformed

    private void aboutPanelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutPanelActionPerformed
        switch(evt.getActionCommand()){
            case(JAboutPanel.CLOSE_SELECTED):
                aboutDialog.setVisible(false);
                break;
            case(JAboutPanel.UPDATE_SELECTED):
                updateWorker = new UpdateCheckWorker(false);
                updateWorker.execute();
        }
    }//GEN-LAST:event_aboutPanelActionPerformed

    private void maskShapeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskShapeComboActionPerformed
        config.setMaskShapeType(maskShapeCombo.getSelectedIndex());
        refreshPreview(2);
    }//GEN-LAST:event_maskShapeComboActionPerformed

    private void optimizeDifferenceToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_optimizeDifferenceToggleActionPerformed
        config.setOptimizedForDifference(optimizeDifferenceToggle.isSelected());
    }//GEN-LAST:event_optimizeDifferenceToggleActionPerformed

    private void saveConfigButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveConfigButtonActionPerformed
        configFC.setDialogTitle("Save Configuration...");
            // Get the image file to save the frame data
        File file = showSaveFileChooser(configFC);
            // Set the selected file for the file chooser
        config.setSelectedFile(configFC, file);
            // If the user selected a file to save to
        if (file != null){
            fileWorker = new ConfigDataSaver(file);
            fileWorker.execute();
        }
    }//GEN-LAST:event_saveConfigButtonActionPerformed

    private void loadConfigButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadConfigButtonActionPerformed
        configFC.setDialogTitle("Load Configuration From...");
            // Get the image file to save the config data
        File file = showOpenFileChooser(configFC);
            // Set the selected file for the file chooser
        config.setSelectedFile(configFC, file);
            // If the user selected a file to save to
        if (file != null){
            fileWorker = new ConfigDataLoader(file);
            fileWorker.execute();
        }
    }//GEN-LAST:event_loadConfigButtonActionPerformed
    /**
     * This returns the width for the image.
     * @return The width for the image.
     */
    private int getImageWidth(){
        return (int)widthSpinner.getValue();
    }
    /**
     * This returns the height for the image.
     * @return The height for the image.
     */
    private int getImageHeight(){
        return (int)heightSpinner.getValue();
    }
    /**
     * This returns the scale used for the mask for the message.
     * @return The scale factor for the message mask.
     */
    private double getMaskScale(){
        return (double)maskScaleSpinner.getValue();
    }
    /**
     * 
     * @return 
     */
    private double getMaskRotation(){
        return (double)maskRotateSpinner.getValue();
    }
    /**
     * 
     * @param index
     * @return 
     */
    private BufferedImage getOverlayImage(int index){
            // If there are no overlay images
        if (overlayImages.isEmpty())
            return null;
        return overlayImages.get(index);
    }
    /**
     * 
     * @return 
     */
    private BufferedImage getOverlayImage(){
        try{
            return getOverlayImage(overlayImageIndex);
        } catch (IndexOutOfBoundsException ex){
            getLogger().log(Level.WARNING, "Index out of bounds for overlay image", ex);
            throw ex;
        }
    }
    /**
     * 
     */
    private void setOverlayImage(int index){
        try{    // Get the current overlay image
            BufferedImage img = getOverlayImage(index);
            overlayImageIndex = index;
                // If the image is not null, use it.
            imgMaskPreview.setIcon((img!=null)?new ImageIcon(img):null);
            maskFrameLabel.setText((index+1)+"/"+overlayImages.size());
            config.setMaskImageFrameIndex(index);
            updateMaskFrameControlsEnabled();
                // Refresh the image mask and preview
            refreshPreview(1);
        } catch (IndexOutOfBoundsException ex){
            getLogger().log(Level.WARNING, "Index out of bounds for overlay image", ex);
        }
    }
    /**
     * This returns the style set for the font.
     * @return The style for the font.
     */
    private int getFontStyle(){
        return ((boldToggle.isSelected())?Font.BOLD:0) | 
                ((italicToggle.isSelected())?Font.ITALIC:0);
    }
    /**
     * This returns whether the spiral will spin clockwise.
     * @return Whether the spiral will spin clockwise.
     */
    private boolean isSpinClockwise(){
        return spinDirCombo.getSelectedIndex() == 0;
    }
    /**
     * This returns the rotation for the frame with the given index.
     * @param frameIndex The index of the frame.
     * @param painter The spiral painter being used.
     * @return The rotation to apply to the spiral for the frame.
     */
    private double getFrameRotation(int frameIndex, SpiralPainter painter){
            // Get the angle to use for the rotation
        double angle = SPIRAL_FRAME_ROTATION*frameIndex;
            // If the spin direction is not the same as the spiral's direction
        if (painter.isSpinClockwise() != painter.isClockwise())
                // Invert the angle, so as to make it spin in the right direction
            angle = SpiralPainter.FULL_CIRCLE_DEGREES - angle;
            // Bound the angle by 360
        return angle  % SpiralPainter.FULL_CIRCLE_DEGREES;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
            // Get whether the program is in debug mode
        boolean debug = DebugCapable.checkForDebugArgument(args);
            // Set the logger's level to the lowest level in order to log all
        getLogger().setLevel(Level.FINEST);
        try {   // Get the parent file for the log file
            File file = new File(PROGRAM_LOG_PATTERN.replace("%h", 
                    System.getProperty("user.home"))
                    .replace('/', File.separatorChar)).getParentFile();
                // If the parent of the log file doesn't exist
            if (!file.exists()){
                try{    // Try to create the directories for the log file
                    Files.createDirectories(file.toPath());
                } catch (IOException ex){
                    getLogger().log(Level.WARNING, 
                            "Failed to create directories for log file", ex);
                }
            }   // Add a file handler to log messages to a log file
            getLogger().addHandler(new java.util.logging.FileHandler(
                    PROGRAM_LOG_PATTERN,0,8));
        } catch (IOException | SecurityException ex) {
            getLogger().log(Level.SEVERE, "Failed to get log file", ex);
        }   // Log the user's OS name
        getLogger().log(Level.CONFIG, "OS: {0}, version: {1}, arch: {2}", new Object[]{
                System.getProperty("os.name"),
                System.getProperty("os.version"),
                System.getProperty("os.arch")});
            // Log the Java vendor name and url
        getLogger().log(Level.CONFIG, "Java vendor: {0}, URL: {1}", new Object[]{
                System.getProperty("java.vendor"),
                System.getProperty("java.vendor.url")});
            // Log the Java version
        getLogger().log(Level.CONFIG, "Java version: {0}", 
                System.getProperty("java.version"));
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | 
                IllegalAccessException | UnsupportedLookAndFeelException ex) {
            getLogger().log(Level.SEVERE, "Failed to load Nimbus LnF", ex);
        }
        //</editor-fold>
            // If there is no look and feel set
        if (UIManager.getLookAndFeel() == null)
            getLogger().log(Level.CONFIG, "Look and Feel: null");
        else    // Log the current Look and Feel
            getLogger().log(Level.CONFIG, "Look and Feel: {0}",
                    UIManager.getLookAndFeel().getName());
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new SpiralGenerator(debug).setVisible(true);
        });
    }
    
    private void refreshMaskText(){
        config.setMaskText(maskTextPane.getText());
            // Refresh the text mask and preview
        refreshPreview(0);
    }
    
    private void refreshPreview(int index){
            // If the index for the mask that changed is -1 (reset all masks and 
            // refresh the mask preview)
        if (index < 0){
            overlayMask.reset();
            maskPreviewLabel.repaint();
        } else {
                // If the mask at the given index is the one being reset
            if (overlayMask.reset(index))
                maskPreviewLabel.repaint();
        }   // Refresh the preview
        refreshPreview();
    }
    
    private void refreshPreview(){
        previewLabel.repaint();
    }
    /**
     * 
     */
    private void updateFrameControls(){
        frameStopButton.setEnabled(framePlayButton.isEnabled() && 
                framePlayButton.isSelected());
        frameSlider.setEnabled(framePlayButton.isEnabled() && 
                !framePlayButton.isSelected());
        updateFrameNavigation();
    }
    /**
     * 
     */
    private void updateFrameNavigation(){
        framePrevButton.setEnabled(frameSlider.isEnabled());
        frameFirstButton.setEnabled(framePrevButton.isEnabled() && 
                frameIndex > frameSlider.getMinimum());
        frameNextButton.setEnabled(frameSlider.isEnabled());
        frameLastButton.setEnabled(frameNextButton.isEnabled() && 
                frameIndex < frameSlider.getMaximum());
    }
    /**
     * 
     * @param enabled 
     */
    private void setValueControlsEnabled(boolean enabled){
        angleSpinner.setEnabled(enabled);
    }
    /**
     * 
     */
    private void updateControlsEnabled(){
            // Get whether the controls should be enabled
        boolean enabled = frameSlider.isEnabled();
        saveButton.setEnabled(enabled);
        setValueControlsEnabled(enabled);
    }
    /**
     * 
     */
    private void updateMaskAlphaControlsEnabled(){
        maskAlphaInvertToggle.setEnabled(maskAlphaToggle.isEnabled() && 
                !maskAlphaToggle.isSelected());
        maskDesaturateCombo.setEnabled(maskAlphaGrayToggle.isEnabled() && 
                maskAlphaGrayToggle.isSelected());
    }
    /**
     * 
     */
    private void updateMaskShapeControlsEnabled(){
        maskShapeHeightSpinner.setEnabled(maskShapeWidthSpinner.isEnabled() && 
                !maskShapeLinkSizeToggle.isSelected());
    }
    /**
     * 
     */
    private void updateMaskFrameControlsEnabled(){
        boolean enabled = loadMaskButton.isEnabled() && !overlayImages.isEmpty();
        maskFramePrevButton.setEnabled(enabled && overlayImageIndex > 0);
        maskFrameNextButton.setEnabled(enabled && overlayImageIndex < overlayImages.size()-1);
    }
    /**
     * 
     * @param index 
     */
    private void setFrameIndex(int index){
        if (index == frameIndex)
            return;
        getLogger().entering(this.getClass().getName(), "setFrameIndex", index);
        int old = frameIndex;
        frameIndex = index;
        updateFrameNavigation();
        try{
            refreshPreview();
        } catch (NullPointerException ex){
            getLogger().log(Level.WARNING, 
                    "Null encountered while repainting preview label for frame " 
                            + frameIndex, ex);
        }
        updateFrameNumberDisplayed();
        try{
            frameSlider.setValue(frameIndex);
        } catch (NullPointerException ex){
            getLogger().log(Level.WARNING, 
                    "Null encountered while incrementing frame ("+old+" -> "+
                            frameIndex + ")", ex);
        }
        getLogger().exiting(this.getClass().getName(), "setFrameIndex");
    }
    /**
     * 
     */
    private void updateFrameNumberDisplayed(){
            // Get the text to display on the frame number label
        String text = String.format("Frame: %d / %d", frameIndex+1,
                SPIRAL_FRAME_COUNT);
        frameNumberLabel.setText(text);
    }
    /**
     * 
     * @param evt 
     */
    private void progressAnimation(java.awt.event.ActionEvent evt){
        getLogger().entering(this.getClass().getName(), "progressAnimation", evt);
            // Get the current time
        long temp = System.currentTimeMillis();
            // Get the difference in the time to get how long it took before the 
            // frame updated
        long diff = temp - frameTime;
        frameTimeTotal += diff;
        frameTotal++;
        frameTime = temp;
            // If the FPS should be printed to the console
        if (printFPSToggle.isSelected()){
            System.out.printf("Last Frame: %5d ms, Avg: %10.5f, Target: %5d%n", 
                    diff, frameTimeTotal/((double)frameTotal), animationTimer.getDelay());
        }   // Increment the frame index, wrapping around at the end
        setFrameIndex((frameIndex+1)%SPIRAL_FRAME_COUNT);
        getLogger().exiting(this.getClass().getName(), "progressAnimation");
    }
    /**
     * 
     * @param index 
     */
    private void setColor(int index){
            // Show the color selector dialog and get the option selected by the 
            // user
        int option = colorSelector.showDialog(this, colorIcons[index].getColor());
            // If the user chose to accept or clear the color
        if (option == JColorSelector.ACCEPT_OPTION || 
                option == JColorSelector.CLEAR_COLOR_OPTION){
                // Get the selected color
            Color color = colorSelector.getColor();
            config.setSpiralColor(index, color);
                // If the selected color is null (reset the color to its default)
            if (color == null)
                color = DEFAULT_SPIRAL_COLORS[index];
            colorIcons[index].setColor(color);
            refreshPreview();
            colorButtons.get(colorIcons[index]).repaint();
        }
    }
    /**
     * 
     * @param fc
     * @return 
     */
    private File showOpenFileChooser(JFileChooser fc){
        int option;     // This is used to store which button the user pressed
        File file = null;           // This gets the file to open
        do{     // If the file chooser has text set for its approve button
            if (fc.getApproveButtonText() != null)
                    // Show the file chooser dialog with the set approve button 
                    // text
                option = fc.showDialog(this, fc.getApproveButtonText());
            else    // Show an open file chooser dialog
                option = fc.showOpenDialog(this);
                // Set the preferred size of the file chooser to its current size
            fc.setPreferredSize(fc.getSize());
                // Store the file chooser's settins in the config
            config.storeFileChooser(fc);
                // If the user approved the selection
            if (option == JFileChooser.APPROVE_OPTION){
                    // Get the selected file
                file = fc.getSelectedFile();
                    // If the file doesn't exist
                if (!file.exists()){
                        // Beep at the user
                    getToolkit().beep();
                        // Tell the user the file doesn't exist
                    JOptionPane.showMessageDialog(this, 
                        "\""+file.getName()+"\"\nFile not found.\nCheck the "
                                + "file name and try again.", 
                        "File Not Found", JOptionPane.WARNING_MESSAGE);
                        // Set the file to null
                    file = null;
                }
            }
            else
                file = null;
        }   // While the user appropved the selection and the selected file does
            // not exist
        while (option == JFileChooser.APPROVE_OPTION && file == null);
        return file;
    }
    /**
     * 
     * @param filter
     * @param fc
     * @return 
     */
    private File getFileWithExtension(File file, JFileChooser fc){
            // Get the file filter for the file chooser
        FileFilter filter = fc.getFileFilter();
            // If the selected file filter doesn't accept the given file
        if (!filter.accept(file)){
                // This is the file extension to add to the end
            String ext = null;
                // If the file filter is the all image file filter
            if (filter.equals(ImageExtensions.IMAGE_FILTER))
                ext = ImageExtensions.PNG;
                // If the file filter is a file extension filter
            else if (filter instanceof FileNameExtensionFilter)
                    // Get the first file extension in the filter
                ext = ((FileNameExtensionFilter)filter).getExtensions()[0];
                // If there is a file extension for the file
            if (ext != null){
                    // Add the file extension to the file
                file = new File(file.toString()+"."+ext);
                    // Set the file in the file chooser
                fc.setSelectedFile(file);
            }
        }
        return file;
    }
    /**
     * 
     * @param fc
     * @return 
     */
    private File showSaveFileChooser(JFileChooser fc){
        int option;     // This is used to store which button the user pressed
        File file = null;           // This gets the file to save
        do{     // If the file chooser has text set for its approve button
            if (fc.getApproveButtonText() != null)
                    // Show the file chooser dialog with the set approve button 
                    // text
                option = fc.showDialog(this, fc.getApproveButtonText());
            else    // Show an open file chooser dialog
                option = fc.showSaveDialog(this);
                // Set the preferred size of the file chooser to its current size
            fc.setPreferredSize(fc.getSize());
                // Store the file chooser's settins in the config
            config.storeFileChooser(fc);
                // If the user wants to save the file
            if (option == JFileChooser.APPROVE_OPTION){
                    // Get the selected file
                file = fc.getSelectedFile();
                    // If the file chooser doesn't have multi-selection enabled 
                    // and the file chooser is set to only select files
                if (!fc.isMultiSelectionEnabled() && 
                        fc.getFileSelectionMode() == JFileChooser.FILES_ONLY){
                        // Make sure the file extension is on the file
                    file = getFileWithExtension(file, fc);
                        // If the file already exists
                    if (file.exists()){
                            // Beep at the user
                        getToolkit().beep();
                            // Show the user a confirmation dialog asking if the 
                            // user wants to overwrite the file
                        int option2 = JOptionPane.showConfirmDialog(this, 
                                "There is already a file with that name.\n"+
                                "Should the file be overwritten?\n"+
                                "File: \""+file+"\"", "File Already Exists", 
                                JOptionPane.YES_NO_CANCEL_OPTION, 
                                JOptionPane.WARNING_MESSAGE);
                            // Determine the action to perform based off the 
                        switch(option2){ // user's choice
                                // If the user selected No
                            case(JOptionPane.NO_OPTION):
                                    // Set the file to null to run the loop 
                                file = null;    // again
                                // If the user selected Yes
                            case(JOptionPane.YES_OPTION):
                                break;
                                // If the user selected Cancel or exited the 
                            default:    // dialog
                                    // Cancel the operation, and show a prompt 
                                    // notifying that nothing was saved.
                                JOptionPane.showMessageDialog(this,
                                        "No file was saved.", 
                                        "File Already Exists", 
                                        JOptionPane.INFORMATION_MESSAGE);
                                return null;
                        }
                    }
                }
            } else
                return null;
        }   // While the user appropved the selection and the selected file 
            // already exists and the user doesn't want to overwrite it
        while (option == JFileChooser.APPROVE_OPTION && file == null);
        return file;
    }
    @Override
    public boolean isInDebug() {
        return debugMode;
    }
    /**
     * This is the time it was when the most recent frame was displayed when the 
     * animation is playing.
     */
    private long frameTime = 0;
    /**
     * This is a running total of the time it takes between each frame in order 
     * to get the average duration of a frame when the animation is playing.
     */
    private long frameTimeTotal = 0;
    /**
     * This is the total number of frames that have been played since the 
     * animation started playing.
     */
    private int frameTotal = 0;
    /**
     * This is the index of the current frame.
     */
    private int frameIndex = 0;
    /**
     * This is a scratch dimension object used to store the dimensions of the 
     * font chooser dialog.
     */
    private Dimension fontDim = null;
    /**
     * This is the icon used to display the sprial.
     */
    private Icon spiralIcon;
    /**
     * This is the icon used to display the spiral when testing the program.
     */
    private TestSpiralIcon testSpiralIcon = null;
    /**
     * These are the painters used to paint the spiral.
     */
    private SpiralPainter[] spiralPainters;
    /**
     * 
     */
    private Map<Component, JLabel> spiralCompLabels;
    /**
     * This is a list of images used to create the mask for the overlay when a 
     * loaded image is used for the mask. This contains the raw images, and is 
     * empty when no image has been loaded for the mask.
     */
    private ArrayList<BufferedImage> overlayImages = new ArrayList<>();
    /**
     * This is the index of the overlay image.
     */
    private int overlayImageIndex = 0;
    /**
     * This is the file that was loaded for the overlay image.
     */
    private File overlayFile = null;
    /**
     * This contains the masks and painter used for the overlay.
     */
    private OverlayMask overlayMask = new OverlayMask();
    /**
     * This is a timer used to animate the animation.
     */
    private javax.swing.Timer animationTimer;
    /**
     * An array that contains the icons used to display the colors for the 
     * spiral.
     */
    private ColorBoxIcon[] colorIcons;
    /**
     * A map that maps the color box icons to the buttons they are displayed on 
     * and used to set the colors with.
     */
    private Map<ColorBoxIcon, JButton> colorButtons;
    /**
     * A map that maps the buttons for set the colors to the index for their 
     * respective color box icons.
     */
    private Map<JButton, Integer> colorIndexes;
    /**
     * These are the models to use to render the spiral preview.
     */
    private SpiralModel[] models;
    /**
     * A configuration object to store the configuration for the program.
     */
    private SpiralGeneratorConfig config;
    /**
     * The text edit commands for the message mask field.
     */
    private TextComponentCommands editCommands;
    /**
     * The undo commands for the message mask field.
     */
    private UndoManagerCommands undoCommands;
    /**
     * This is the String to display on the progress bar before the progress 
     * amount.
     */
    private String progressString = null;
    /**
     * This is the file worker currently being used.
     */
    private FileWorker fileWorker = null;
    /**
     * This is whether the program is in debug mode.
     */
    private boolean debugMode;
    /**
     * This is a map to map the test components to their corresponding classes.
     */
    private Map<Class, List<Component>> testComponents;
    /**
     * This is the checker to use to check for updates for the program.
     */
    private UpdateChecker updateChecker = null;
    /**
     * This is the swing worker used to check for updates.
     */
    private UpdateCheckWorker updateWorker = null;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton aboutButton;
    private javax.swing.JDialog aboutDialog;
    private components.JAboutPanel aboutPanel;
    private javax.swing.JCheckBox alwaysScaleToggle;
    private javax.swing.JLabel angleLabel;
    private javax.swing.JSpinner angleSpinner;
    private javax.swing.JLabel balanceLabel;
    private javax.swing.JSpinner balanceSpinner;
    private javax.swing.JLabel baseLabel;
    private javax.swing.JSpinner baseSpinner;
    private javax.swing.JCheckBox boldToggle;
    private javax.swing.JCheckBox checkUpdatesAtStartToggle;
    private components.JColorSelector colorSelector;
    private javax.swing.JPanel configButtonPanel;
    private javax.swing.JFileChooser configFC;
    private javax.swing.JPanel ctrlButtonPanel;
    private javax.swing.JLabel currentVersLabel;
    private javax.swing.JLabel currentVersTextLabel;
    private javax.swing.JPopupMenu debugPopup;
    private javax.swing.JLabel delayLabel;
    private javax.swing.JSpinner delaySpinner;
    private javax.swing.JComboBox<String> dirCombo;
    private javax.swing.JLabel dirLabel;
    private javax.swing.JCheckBox fontAntialiasingToggle;
    private javax.swing.JButton fontButton;
    private javax.swing.JButton frameFirstButton;
    private javax.swing.JButton frameLastButton;
    private javax.swing.JPanel frameNavPanel;
    private javax.swing.JButton frameNextButton;
    private javax.swing.JLabel frameNumberLabel;
    private javax.swing.JToggleButton framePlayButton;
    private javax.swing.JButton framePrevButton;
    private javax.swing.JSlider frameSlider;
    private javax.swing.JButton frameStopButton;
    private javax.swing.JPanel framesPanel;
    private javax.swing.JLabel heightLabel;
    private javax.swing.JSpinner heightSpinner;
    private javax.swing.JPanel imageCtrlPanel;
    private javax.swing.JButton imgAspectRatioButton;
    private javax.swing.JCheckBox imgMaskAntialiasingToggle;
    private components.JThumbnailLabel imgMaskPreview;
    private javax.swing.JCheckBoxMenuItem inputEnableToggle;
    private javax.swing.JCheckBox italicToggle;
    private javax.swing.JLabel latestVersLabel;
    private javax.swing.JLabel latestVersTextLabel;
    private javax.swing.JLabel lineSpacingLabel;
    private javax.swing.JSpinner lineSpacingSpinner;
    private javax.swing.JButton loadConfigButton;
    private javax.swing.JButton loadMaskButton;
    private javax.swing.JRadioButton maskAlphaBlueToggle;
    private javax.swing.ButtonGroup maskAlphaButtons;
    private javax.swing.JPanel maskAlphaColorCtrlPanel;
    private javax.swing.JPanel maskAlphaCtrlPanel;
    private javax.swing.JRadioButton maskAlphaGrayToggle;
    private javax.swing.JRadioButton maskAlphaGreenToggle;
    private javax.swing.JCheckBox maskAlphaInvertToggle;
    private javax.swing.JRadioButton maskAlphaRedToggle;
    private javax.swing.JRadioButton maskAlphaToggle;
    private javax.swing.JComboBox<String> maskDesaturateCombo;
    private javax.swing.JLabel maskDesaturateLabel;
    private javax.swing.JDialog maskDialog;
    private javax.swing.JButton maskEditButton;
    private javax.swing.JFileChooser maskFC;
    private components.JFileDisplayPanel maskFCPreview;
    private javax.swing.JCheckBox maskFlipHorizToggle;
    private javax.swing.JCheckBox maskFlipVertToggle;
    private javax.swing.JPanel maskFrameCtrlPanel;
    private javax.swing.JLabel maskFrameLabel;
    private javax.swing.JButton maskFrameNextButton;
    private javax.swing.JButton maskFramePrevButton;
    private javax.swing.JPanel maskImageCtrlPanel;
    private javax.swing.JComboBox<String> maskImgScaleMethodCombo;
    private javax.swing.JLabel maskImgScaleMethodLabel;
    private javax.swing.JPanel maskImgScalePanel;
    private javax.swing.JPopupMenu maskPopup;
    private components.JThumbnailLabel maskPreviewLabel;
    private javax.swing.JLabel maskRotateLabel;
    private javax.swing.JSpinner maskRotateSpinner;
    private javax.swing.JLabel maskScaleLabel;
    private javax.swing.JSpinner maskScaleSpinner;
    private javax.swing.JComboBox<String> maskShapeCombo;
    private javax.swing.JLabel maskShapeHeightLabel;
    private javax.swing.JSpinner maskShapeHeightSpinner;
    private javax.swing.JLabel maskShapeLabel;
    private javax.swing.JCheckBox maskShapeLinkSizeToggle;
    private javax.swing.JLabel maskShapeWidthLabel;
    private javax.swing.JSpinner maskShapeWidthSpinner;
    private javax.swing.JTabbedPane maskTabbedPane;
    private javax.swing.JTextPane maskTextPane;
    private javax.swing.JScrollPane maskTextScrollPane;
    private javax.swing.JCheckBox optimizeDifferenceToggle;
    private components.JThumbnailLabel previewLabel;
    private javax.swing.JPanel previewMaskPanel;
    private javax.swing.JPanel previewPanel;
    private javax.swing.JPanel previewSpiralPanel;
    private javax.swing.JCheckBoxMenuItem printFPSToggle;
    private javax.swing.JMenuItem printTestButton;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel radiusLabel;
    private javax.swing.JSpinner radiusSpinner;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton resetMaskButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JButton saveConfigButton;
    private javax.swing.JFileChooser saveFC;
    private components.JFileDisplayPanel saveFCPreview;
    private javax.swing.JPanel shapeMaskCtrlPanel;
    private javax.swing.JPanel shapeMaskSizePanel;
    private javax.swing.JMenuItem showTestDialogButton;
    private javax.swing.JCheckBox showTestSpiralToggle;
    private javax.swing.JComboBox<String> spinDirCombo;
    private javax.swing.JLabel spinLabel;
    private javax.swing.JPanel spiralColorPanel;
    private javax.swing.Box.Filler spiralCtrlFiller;
    private javax.swing.JPanel spiralCtrlPanel;
    private javax.swing.JComboBox<SpiralShape> spiralShapeCombo;
    private javax.swing.JLabel spiralShapeLabel;
    private javax.swing.JComboBox<SpiralPainter> spiralTypeCombo;
    private javax.swing.JLabel spiralTypeLabel;
    private javax.swing.JDialog testDialog;
    private javax.swing.JSpinner testRotateSpinner;
    private javax.swing.JSpinner testScaleSpinner;
    private javax.swing.JCheckBox testShowRadiusToggle;
    private javax.swing.JSpinner testSpiralImageSpinner;
    private javax.swing.JPanel textMaskCtrlPanel;
    private javax.swing.JDialog updateCheckDialog;
    private javax.swing.JButton updateContinueButton;
    private javax.swing.JLabel updateIconLabel;
    private javax.swing.JButton updateOpenButton;
    private javax.swing.JPanel updatePanel;
    private javax.swing.JLabel updateTextLabel;
    private javax.swing.JLabel widthLabel;
    private javax.swing.JSpinner widthSpinner;
    // End of variables declaration//GEN-END:variables
    /**
     * 
     */
    private void updateProgressString(){
            // If the progress bar's string is painted
        if (progressBar.isStringPainted()){
                // If the progress string is not null, then use it. Otherwise, 
                // use an empty string
            String str = (progressString != null) ? progressString : "";
                // Get the percentage as a string
            String percent = String.format("%.1f%%", progressBar.getPercentComplete()*100.0);
                // If the string is not empty
            if (!str.isEmpty()){
                    // If the progress bar is indeterminate
                if (progressBar.isIndeterminate())
                    progressBar.setString(str + "...");
                else
                    progressBar.setString(str+": "+percent);
                // If the progress bar is not indeterminate
            } else if (!progressBar.isIndeterminate())
                progressBar.setString(percent);
        }
    }
    /**
     * 
     * @param text 
     */
    private void setProgressString(String text){
        progressString = text;
        progressBar.setStringPainted(text != null);
    }
    /**
     * 
     * @param isWaiting 
     */
    private void useWaitCursor(boolean isWaiting) {
        setCursor((isWaiting)?Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR):null);
    }
    /**
     * 
     * @param enabled 
     */
    private void setInputEnabled(boolean enabled){
        inputEnableToggle.setSelected(enabled);
        framePlayButton.setEnabled(enabled);
        widthSpinner.setEnabled(enabled);
        heightSpinner.setEnabled(enabled);
        delaySpinner.setEnabled(enabled);
        for (Component comp : spiralCompLabels.keySet())
            if (angleSpinner != comp)
                comp.setEnabled(enabled);
        spiralTypeCombo.setEnabled(enabled);
        spinDirCombo.setEnabled(enabled);
        for (JButton button : colorButtons.values()){
            button.setEnabled(enabled);
        }
        resetButton.setEnabled(enabled);
        maskEditButton.setEnabled(enabled);
        maskTabbedPane.setEnabled(enabled);
        maskTextPane.setEnabled(enabled);
        fontButton.setEnabled(enabled);
        lineSpacingSpinner.setEnabled(enabled);
        boldToggle.setEnabled(enabled);
        italicToggle.setEnabled(enabled);
        fontAntialiasingToggle.setEnabled(enabled);
        maskScaleSpinner.setEnabled(enabled);
        loadMaskButton.setEnabled(enabled);
        for (AbstractButton button : SwingExtendedUtilities.toArray(maskAlphaButtons)){
            button.setEnabled(enabled);
        }
        updateMaskAlphaControlsEnabled();
        imgMaskAntialiasingToggle.setEnabled(enabled);
        maskShapeWidthSpinner.setEnabled(enabled);
        maskShapeLinkSizeToggle.setEnabled(enabled);
        updateMaskShapeControlsEnabled();
        resetMaskButton.setEnabled(enabled);
        aboutPanel.setEnabled(enabled);
        updateOpenButton.setEnabled(enabled);
        maskRotateSpinner.setEnabled(enabled);
        maskFlipHorizToggle.setEnabled(enabled);
        maskFlipVertToggle.setEnabled(enabled);
        imgAspectRatioButton.setEnabled(enabled && !overlayImages.isEmpty());
        updateMaskFrameControlsEnabled();
        maskImgScaleMethodCombo.setEnabled(enabled);
        updateFrameControls();
        updateControlsEnabled();
        optimizeDifferenceToggle.setEnabled(enabled);
        saveConfigButton.setEnabled(enabled);
        loadConfigButton.setEnabled(enabled);
    }
    /**
     * 
     * @param file
     * @return 
     */
    private SpiralGeneratorProperties toProperties(File file){
        SpiralGeneratorProperties prop = new SpiralGeneratorProperties();
        prop.setImageSize(getImageWidth(),getImageHeight());
        prop.setFrameDuration(animationTimer.getDelay());
        for (int i = 0; i < colorIcons.length; i++){
            prop.setSpiralColor(i, config.getSpiralColor(i));
        }
        prop.setSpiralType(spiralTypeCombo.getSelectedIndex());
        for (SpiralPainter painter : spiralPainters){
            prop.setSpiralData(painter);
        }
        prop.setMaskType(maskTabbedPane.getSelectedIndex());
        prop.setMaskScale((double)maskScaleSpinner.getValue());
        prop.setMaskRotation((double)maskRotateSpinner.getValue());
        prop.setMaskFlippedHorizontally(maskFlipHorizToggle.isSelected());
        prop.setMaskFlippedVertically((maskFlipVertToggle.isSelected()));
        prop.setMaskText(maskTextPane.getText());
        prop.setMaskFont(maskTextPane.getFont());
        prop.setMaskTextAntialiased(fontAntialiasingToggle.isSelected());
        prop.setMaskLineSpacing((double)lineSpacingSpinner.getValue());
        prop.setMaskImageAntialiased(imgMaskAntialiasingToggle.isSelected());
        prop.setMaskAlphaIndex(maskAlphaButtons);
        prop.setMaskDesaturateMode(maskDesaturateCombo.getSelectedIndex());
        prop.setMaskImageInverted(maskAlphaInvertToggle.isSelected());
        prop.setMaskImageInterpolation(maskImgScaleMethodCombo.getSelectedIndex());
        prop.setMaskImageFrameIndex(overlayImageIndex);
        File imgFile = overlayFile;
        if (imgFile != null && file != null){
            imgFile = FilesExtended.relativize(imgFile, file);
        }
        prop.setMaskImageFile(imgFile);
        prop.setMaskShapeType(maskShapeCombo.getSelectedIndex());
        prop.setMaskShapeWidth((double)maskShapeWidthSpinner.getValue());
        prop.setMaskShapeHeight((double)maskShapeHeightSpinner.getValue());
        prop.setMaskShapeSizeLinked(maskShapeLinkSizeToggle.isSelected());
        
        return prop;
    }
    /**
     * 
     * @return 
     */
    private SpiralGeneratorProperties toProperties(){
        return toProperties(null);
    }
    /**
     * 
     * @param file
     * @param images
     * @return
     * @throws IOException 
     */
    private List<BufferedImage> loadImage(File file, List<BufferedImage> images) 
            throws IOException{ 
        getLogger().entering(this.getClass().getName(), "loadImage", file.getName());
        if (images == null)
            images = new ArrayList<>();
        else
            images.clear();
            // Get a GIF decoder to decode the image if it's a GIF
        GifDecoder decoder = new GifDecoder();
            // Try to decode the image and get the status of the decoder
        int status = decoder.read(file.toString());
        getLogger().log(Level.FINER, "GifDecoder Status: {0}", status);
            // If the image is a GIF that decoded just fine and there are 
            // any frames in the image
        if (status == 0 && decoder.getFrameCount() > 0){
            getLogger().log(Level.FINER, "Decoded {0} frames.", decoder.getFrameCount());
                // Go through the decoded frames
            for (int i = 0; i < decoder.getFrameCount(); i++){
                    // Get the current frame
                BufferedImage img = decoder.getFrame(i);
                    // If that frame is not null
                if (img != null)
                    images.add(img);
            }
        } else{
            getLogger().finer("Using ImageIO to read file.");
                // Read the image from the file
            BufferedImage img = ImageIO.read(file);
                // If the image is not null
            if (img != null)
                images.add(img);
        }
        getLogger().log(Level.FINER, "Loaded {0} images from file.", images.size());
        getLogger().exiting(this.getClass().getName(), "loadImage");
        return images;
    }
    /**
     * 
     * @param images
     * @param index
     * @param file
     * @param initLoad 
     */
    private void setOverlayImages(List<BufferedImage> images, int index, 
            File file, boolean initLoad){
        overlayFile = file;
        overlayImages.clear();
        overlayImages.addAll(images);
        maskFrameCtrlPanel.setVisible(overlayImages.size() > 1);
            // If the program is not loading this image at the start of the 
            // program
        if (!initLoad)
            config.setMaskImageFile(file);
        setOverlayImage(Math.max(Math.min(index, overlayImages.size()-1), 0));
    }
    
    private Graphics2D configureGraphics(Graphics2D g){
            // Prioritize rendering quality over speed
        g.setRenderingHint(RenderingHints.KEY_RENDERING, 
                RenderingHints.VALUE_RENDER_QUALITY);
            // Prioritize quality over speed for alpha interpolation
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, 
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        return g;
    }
    /**
     * 
     * @param g
     * @param width
     * @param height 
     * @param bounds
     */
    private void transformMaskGraphics(Graphics2D g, int width, int height, 
            Rectangle2D bounds){
            // This is the center x-coordinate of the mask
        double centerX = width/2.0;
            // This is the center y-coordinate of the mask
        double centerY = height/2.0;
            // Scale the graphics for the mask, maintaining the center
        scale(g,centerX,centerY,getMaskScale());
            // If the mask is horizontally or vertically flipped
        if (maskFlipHorizToggle.isSelected() || maskFlipVertToggle.isSelected())
                // Flip the image horizontally and/or vertically, maintaining 
                // the center
            scale(g,centerX,centerY,maskFlipHorizToggle.isSelected()?-1:1,
                    maskFlipVertToggle.isSelected()?-1:1);
            // Rotate the graphics context around the center
        g.rotate(Math.toRadians(getMaskRotation()), centerX, centerY);
            // If the bounds have been provided
        if (bounds != null){
                // Translate to get x and y back at (0, 0)
            g.translate(bounds.getMinX(), bounds.getMinY());
        }
    }
    /**
     * 
     * @param g
     * @param width
     * @param height
     * @param text
     * @param painter 
     */
    private void paintTextMask(Graphics2D g, int width, int height, String text, 
            CenteredTextPainter painter){
            // Set the graphics context font to the mask font
        g.setFont(maskTextPane.getFont());
            // Paint the mask's text to the graphics context
        painter.paint(g, text, width, height);
    }
    /**
     * 
     * @param width
     * @param height
     * @param text
     * @param mask
     * @param painter
     * @return 
     */
    private BufferedImage getTextMaskImage(int width, int height, String text, 
            BufferedImage mask, CenteredTextPainter painter){
            // If the text is null or blank
        if (text == null || text.isBlank())
            return null;
            // If the overlay mask is not null and is the same width and height 
            // as the given width and height
        if (mask != null && mask.getWidth() == width && mask.getHeight() == height)
            return mask;
        
            // Overlay mask needs to be refreshed
            
            // Create a new image for the overlay mask
        mask = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            // Create the graphics context for the image
        Graphics2D g = mask.createGraphics();
            // Paint the mask's text
        paintTextMask(g,width,height,text,painter);
            // Dispose of the graphics context
        g.dispose();
        return mask;
    }
    /**
     * 
     * @param image
     * @param mask
     * @return 
     */
    private BufferedImage getImageAlphaMask(BufferedImage image, 
            BufferedImage mask, int width, int height){
            // If the source image is null
        if (image == null)
            return null;
            // Get the width of the image
        int w = image.getWidth();
            // Get the height of the image
        int h = image.getHeight();
            // If the mask is currently not null
        if (mask != null)
            return mask;
            // If the alpha channel should be used as-is
        if (maskAlphaToggle.isSelected())
            return image;
            // This will get a version of the image with the background filled
        mask = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            // Get the graphics context for the image
        Graphics2D g = mask.createGraphics();
            // This is the amount by which to shift the RGB values to get the 
            // byte desired for the color
        int colorShift = 0;
            // If the red channel should be used as the alpha channel
        if (maskAlphaRedToggle.isSelected())
            colorShift = 16;
            // If the green channel should be used as the alpha channel
        else if (maskAlphaGreenToggle.isSelected())
            colorShift = 8;
            // Get the desaturation mode
        int mode = maskDesaturateCombo.getSelectedIndex();
            // This sets the color to fill the background of the image. If the 
            // alpha channel is inverted, then use black. Otherwise, use white. 
            // This way, the transparent area remains transparent.
        g.setColor((maskAlphaInvertToggle.isSelected()) ? Color.WHITE : Color.BLACK);
            // Fill the background area with the fill color
        g.fillRect(0, 0, w, h);
            // If the image should be treated as greyscale and using luminance 
            // to desaturate the image
        if (maskAlphaGrayToggle.isSelected() && mode == 0)
                // Draw a grayscale version of the image
            g.drawImage(image, new ColorConvertOp(
                    ColorSpace.getInstance(ColorSpace.CS_GRAY),null), 0, 0);
        else  // Draw the image
            g.drawImage(image, 0, 0, null);
        g.dispose();
            // Transfer the temporary image to the image variable
        image = mask;
            // This will be the image to use as a mask
        mask = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
            // This is an array to hold the RGB data of each row of pixels
        int[] imgData = new int[w];
            // Go through each row of pixels
        for (int y = 0; y < h; y++){
                // Get the RGB values of the pixels in the current row
            image.getRGB(0, y, w, 1, imgData, 0, 1);
                // Go through each pixel in the current row
            for (int x = 0; x < w; x++){
                    // Get the RGB value of the current pixel
                int rgb = imgData[x];
                    // If the colors are inverted
                if (maskAlphaInvertToggle.isSelected())
                    rgb = ~rgb;
                    // Shift the bits over to get the byte to use into position
                rgb >>= colorShift;
                    // This is the alpha component for the current pixel
                int alpha;
                    // If the image should be treated as a grayscale image and 
                    // this is not using luminance to desaturate the image
                if (maskAlphaGrayToggle.isSelected() && mode > 0)
                    alpha = (int)(0xFF * toGrayscale(rgb,mode));
                else 
                    alpha = rgb & 0x000000FF;
                    // Remove the old alpha component of the pixel
                imgData[x] &= 0x00FFFFFF;
                    // Add the new alpha component to the pixel
                imgData[x] |= alpha << 24;
            }
            mask.setRGB(0, y, w, 1, imgData, 0, 1);
        }
        return mask;
    }
    /**
     * 
     * @param width
     * @param height
     * @param image
     * @param mask
     * @return 
     */
    private BufferedImage getImageMaskImage(int width, int height, 
            BufferedImage image, BufferedImage mask){
            // If the source image is null
        if (image == null)
            return null;
            // If the mask version of the overlay image is null
        if (mask == null)
            mask = image;
            // If the mask version of the overlay image doesn't match the 
            // size of the area being rendered
            // TODO: Work on implementing user control over the overlay 
            // image's size and stuff
        if (mask.getWidth() != width || mask.getHeight() != height){
                // Scale and center the overlay image
            return getCenteredImage(
                    SpiralGeneratorUtilities.scaleImage(image, width, height, 
                            maskImgScaleMethodCombo.getSelectedIndex()),
                    width,height);
        }
        return mask;
    }
    /**
     * 
     * @param g
     * @param width
     * @param height
     * @param path
     * @return 
     */
    private Path2D paintShapeMask(Graphics2D g, int width, int height, double w,
            double h, Path2D path){
        w *= width;
        h *= height;
            // Get the x-coordinate for the shape
        double x = (width-w)/2.0;
            // Get the y-coordinate for the shape
        double y = (height-h)/2.0;
            // Determine which shape to use for the mask
        switch(maskShapeCombo.getSelectedIndex()){
                // If the shape is a heart
            case(0):
                    // Get the heart shape
                path = SpiralGeneratorUtilities.getHeartShape(x, y, w, h, path);
                break;
                // If the shape is a star
            case(1):
                path = SpiralGeneratorUtilities.getStarShape(x, y, w, h, path);
        }
        g.fill(path);
        return path;
    }
    /**
     * 
     * @param width
     * @param height
     * @param mask
     * @param path
     * @return 
     */
    private BufferedImage getShapeMaskImage(int width, int height, 
            BufferedImage mask, Path2D path){
            // Get the width for the shape, as a percentage of the image width
        double w = (double) maskShapeWidthSpinner.getValue();
            // Get the height for the shape, as a percentage of the image height
        double h = (double) maskShapeHeightSpinner.getValue();
            // If the width or height of the shape are 0% or less
        if (w <= 0 || h <= 0)
            return null;
            // If the overlay mask is not null and is the same width and height 
            // as the given width and height
        if (mask != null && mask.getWidth() == width && mask.getHeight() == height)
            return mask;
        
            // Overlay mask needs to be refreshed
            
            // Create a new image for the overlay mask
        mask = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            // Create the graphics context for the image
        Graphics2D g = mask.createGraphics();
            // Enable antialiasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                RenderingHints.VALUE_ANTIALIAS_ON);
            // Paint the mask's shape
        paintShapeMask(g,width,height,w,h,path);
            // Dispose of the graphics context
        g.dispose();
        return mask;
    }
    /**
     * 
     * @param g
     * @param model
     * @param width
     * @param height
     * @param spiralPainter
     * @param mask 
     */
    private void paintOverlay(Graphics2D g, SpiralModel model, int width, 
            int height,SpiralPainter spiralPainter, OverlayMask mask){
            // If the width or height are less than or equal to zero or there is 
            // nothing visible for the overlay (nothing  would be drawn)
        if (width <= 0 || height <= 0 || !mask.isOverlayRendered())
            return;
            // Create an image to render the overlay to
        BufferedImage overlay = new BufferedImage(width, height, 
                BufferedImage.TYPE_INT_ARGB);
            // Create and configure a graphics context for the image
        Graphics2D imgG = configureGraphics(overlay.createGraphics());
            // Paint a spiral
        spiralPainter.paint(imgG, model, width, height);
            // Apply the mask for the overlay
        mask.maskOverlay(imgG, width, height);
            // Dispose of the image graphics
        imgG.dispose();
            // Create a copy of the given graphics context and configure it
        g = configureGraphics((Graphics2D) g.create());
            // Enable or disable the antialiasing, depending on whether the 
            // mask should be antialiased
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                (mask.isAntialiased())? RenderingHints.VALUE_ANTIALIAS_ON : 
                        RenderingHints.VALUE_ANTIALIAS_OFF);
        // TODO: Speed up the drawing of the overlay image somehow
//        long time = System.currentTimeMillis();
            // Draw the overlay image
        g.drawImage(overlay, 0, 0, width, height, null);
//        time = System.currentTimeMillis() - time;
//        System.out.println("Time to draw overlay: " + time + " ms");
            // Dispose of the copy of the graphics context
        g.dispose();
    }
    /**
     * 
     * @param g
     * @param frameIndex
     * @param width
     * @param height
     * @param spiralPainter
     * @param mask 
     */
    private void paintSpiralDesign(Graphics2D g, int frameIndex, int width, 
            int height, SpiralPainter spiralPainter,OverlayMask mask){
            // If the width or height is less than or equal to zero
        if (width <= 0 || height <= 0)
            return;
            // Get the angle of rotation for the spiral
        double angle = getFrameRotation(frameIndex,spiralPainter);
            // Go through the spiral models
        for (SpiralModel model : models)
            model.setRotation(angle);
            // Paint the spiral
        spiralPainter.paint(g, models[0], width, height);
        paintOverlay(g,models[1],width,height,spiralPainter,mask);
    }
    
    private class ColorIconSpiralModel extends AbstractSpiralModel{
        /**
         * The index of the color icon for the first color in the model.
         */
        private int index1;
        /**
         * The index of the color icon for the second color in the model.
         */
        private int index2;
        /**
         * The rotation of the spiral.
         */
        private double rotation = 0.0;
        
        public ColorIconSpiralModel(int index1, int index2){
            this.index1 = index1;
            this.index2 = index2;
        }
        @Override
        public Color getColor1() {
            return colorIcons[index1].getColor();
        }
        /**
         * 
         * @param index
         * @param color 
         */
        private void setColor(int index, Color color){
            config.setSpiralColor(index, color);
                // If the color is null and the index is within range of the 
                // default colors
            if (color == null && index >= 0 && index < DEFAULT_SPIRAL_COLORS.length)
                color = DEFAULT_SPIRAL_COLORS[index];
            colorIcons[index].setColor(color);
            refreshPreview();
            colorButtons.get(colorIcons[index]).repaint();
        }
        @Override
        public void setColor1(Color color) {
            setColor(index1,color);
        }
        @Override
        public Color getColor2() {
            return colorIcons[index2].getColor();
        }
        @Override
        public void setColor2(Color color) {
            setColor(index2,color);
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
    
    private class SpiralIcon implements Icon2D{
        @Override
        public void paintIcon2D(Component c, Graphics2D g, int x, int y) {
            g.translate(x, y);
            paintSpiralDesign(g,frameIndex, getIconWidth(), 
                    getIconHeight(),getSpiralPainter(),overlayMask);
        }
        @Override
        public int getIconWidth() {
            return getImageWidth();
        }
        @Override
        public int getIconHeight() {
            return getImageHeight();
        }
    }
    /**
     * 
     */
    private class TestSpiralIcon implements Icon2D{
        /**
         * The Spiral model used for drawing the test spiral.
         */
        SpiralModel model = new DefaultSpiralModel();
        /**
         * This is an array containing the test images to display while testing.
         */
        ArrayList<BufferedImage> images = new ArrayList<>();
        /**
         * This is the spiral painter being tested.
         */
        SpiralPainter painter;
        /**
         * 
         * @param painter 
         */
        TestSpiralIcon(SpiralPainter painter){
            this.painter = painter;
        }
        @Override
        public void paintIcon2D(Component c, Graphics2D g, int x, int y) {
            g.translate(x, y);
                // Get the width of the icon
            int width = getIconWidth();
                // Get the height of the icon
            int height = getIconHeight();
                // Get the scale for the icon
            double scale = (double)testScaleSpinner.getValue();
                // If the scale is 0
            if (scale == 0)
                scale = 1;
                // Invert the scale
            scale = 1.0/scale;
                // Get the center x-coordinate
            double centerX = width/2.0;
                // Get the center y-coordinate
            double centerY = height/2.0;
                // Scale the graphics context
            scale(g,centerX,centerY,scale);
                // Get the index for the test image
            int index = (int) testSpiralImageSpinner.getValue();
                // If the index for the test image is within range of the test 
                // images
            if (index >= 0 && index < images.size()){
                    // Get the test image at that index
                BufferedImage img = images.get(index);
                    // If the image's size is not the icon's size
                if (img.getWidth() != width || img.getHeight() != height)
                        // Scale the test image
                    img = Thumbnailator.createThumbnail(img, width, height);
                    // Draw the test image
                g.drawImage(img, 0, 0, null);
            } else {
                g.setColor(model.getColor1());
                g.fillRect(0, 0, width, height);
            }   // Draw the test spiral
            painter.paint(g, model, width, height);
                // If the radius should be shown
            if (testShowRadiusToggle.isSelected()){
                g.setColor(Color.CYAN);
                    // Get the spiral's radius
                double radius = painter.getSpiralRadius();
                    // Get an ellipse to draw the circle for the radius
                Ellipse2D e = new Ellipse2D.Double();
                    // Set the ellipse to be a circle with the spiral's radius
                e.setFrameFromCenter(centerX, centerY, centerX+radius, centerY+radius);
                    // Draw the circle
                g.draw(e);
                g.setColor(Color.RED);
                    // Get the cartesian point with the radius and rotation of 
                    // the spiral
                Point2D p = GeometryMath.polarToCartesianDegrees(radius, 
                        model.getRotation()+painter.getRotation(), centerX,
                        centerY,null);
                    // Draw a line to represent the radius of the spiral
                g.draw(new Line2D.Double(centerX, centerY,p.getX(),p.getY()));
            }
        }
        @Override
        public int getIconWidth() {
            return getImageWidth();
        }
        @Override
        public int getIconHeight() {
            return getImageHeight();
        }
    }
    
    private class SpiralHandler implements PropertyChangeListener, 
            ActionListener, DocumentListener{
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
                // This gets the SpiralPainter that's the source of the change
            SpiralPainter painter = null;
                // If the source of the change is a SpiralPainter
            if (evt.getSource() instanceof SpiralPainter)
                painter = (SpiralPainter) evt.getSource();
                // This gets if the text mask should also be repainted
            boolean maskChanged = false;
            switch(evt.getPropertyName()){
                case(CenteredTextPainter.ANTIALIASING_PROPERTY_CHANGED):
                    config.setMaskTextAntialiased(overlayMask.textPainter.isAntialiasingEnabled());
                    maskChanged = true;
                    break;
                case(CenteredTextPainter.LINE_SPACING_PROPERTY_CHANGED):
                    config.setMaskLineSpacing(overlayMask.textPainter.getLineSpacing());
                    maskChanged = true;
                    break;
                case(SpiralPainter.SPIRAL_RADIUS_PROPERTY_CHANGED):
                case(LogarithmicSpiral.BASE_PROPERTY_CHANGED):
                case(SpiralPainter.THICKNESS_PROPERTY_CHANGED):
                case(SpiralPainter.CLOCKWISE_PROPERTY_CHANGED):
                case(ShapedSpiral.SHAPE_PROPERTY_CHANGED):
                case(SpiralPainter.ROTATION_PROPERTY_CHANGED):
                case(SpiralPainter.SPIN_CLOCKWISE_PROPERTY_CHANGED):
                        // If there is a spiral painter
                    if (painter != null)
                        config.setSpiralData(painter);
            }   // If the text mask has changed in any way
            if (maskChanged)
                    // Refresh the text mask and preview
                refreshPreview(0);
            else    // Refresh the preview
                refreshPreview();
        }
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() instanceof JButton){
                Integer index = colorIndexes.get((JButton)evt.getSource());
                if (index != null)
                    setColor(index);
            }
        }
        @Override
        public void insertUpdate(DocumentEvent evt) {
            refreshMaskText();
        }
        @Override
        public void removeUpdate(DocumentEvent evt) {
            refreshMaskText();
        }
        @Override
        public void changedUpdate(DocumentEvent evt) {
            refreshMaskText();
        }
    }
    
    private void setDebugValueInConfig(Component source, Object value){
        if (value == null || source == null)
            return;
        Class type = value.getClass();
        List<Component> list = testComponents.get(type);
        if (list != null){
            int index = list.indexOf(source);
            if (index < 0)
                return;
            if (Double.class.equals(type))
                config.setDebugTestDouble(index, (double)value);
            else if (Integer.class.equals(type))
                config.setDebugTestInteger(index, (int)value);
            else if (Boolean.class.equals(type))
                config.setDebugTestBoolean(index, (boolean)value);
            else
                config.setDebugTestString(index, value.toString());
        }
    }
    
    private class DebugTestComponentHandler implements ChangeListener, 
            ActionListener{
        @Override
        public void stateChanged(ChangeEvent evt) {
            if (evt.getSource() instanceof JSpinner){
                JSpinner spinner = (JSpinner) evt.getSource();
                if (spinner.getModel() instanceof SpinnerNumberModel)
                    setDebugValueInConfig(spinner,spinner.getValue());
            }
            if (showTestSpiralToggle.isSelected())
                previewLabel.repaint();
        }
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (evt.getSource() instanceof JToggleButton){
                JToggleButton button = (JToggleButton) evt.getSource();
                setDebugValueInConfig(button,button.isSelected());
            }
            if (showTestSpiralToggle.isSelected())
                previewLabel.repaint();
        }
    }
    
    private class OverlayMask implements Painter<Color>, Icon2D{
        /**
         * This is the image used as a mask for the overlay when text is being 
         * used as a mask. When this is null, then the mask will be generated 
         * the next time it is used.
         */
        public BufferedImage textMask = null;
        /**
         * This is the unscaled image used  a mask for the overlay when a loaded 
         * image is used for the mask. This is null when the mask needs to be 
         * recreated from {@code overlayImage}, either due to another image 
         * being loaded in or the image alpha channel is changed.
         */
        public BufferedImage alphaMask = null;
        /**
         * This is the image used as a mask for the overlay when a loaded 
         * image is used for the mask. This is null when the mask needs to be 
         * recreated from {@code alphaMask}, either due to another image being 
         * loaded in or the resulting image's size being changed.
         */
        public BufferedImage imgMask = null;
        /**
         * This is the image used as a mask for the overlay when a shape is 
         * being used for the mask. When this is null, then the mask will be 
         * generated the next time it is used.
         */
        public BufferedImage shapeMask = null;
        /**
         * This is the painter used to paint the text used as the mask for the 
         * message when text is being used for the mask.
         */
        public CenteredTextPainter textPainter;
        /**
         * This is a scratch Path2D object used to draw the shapes if need be. 
         * This is initially null and is initialized the first time it's used.
         */
        public Path2D path = null;
        /**
         * This is a rectangle shape to use to get the actual size of the area
         * to be drawn with the overlay mask.
         */
        private Rectangle2D rect = new Rectangle2D.Double();
        /**
         * An AffineTransform object to use to transform the rectangle to get 
         * the actual size of the area to be drawn.
         */
        private AffineTransform tx = null;
        
        protected OverlayMask(){
            textPainter = new CenteredTextPainter();
        }
        
        protected OverlayMask(CenteredTextPainter painter){
            textPainter = new CenteredTextPainter(painter);
        }
        
        protected OverlayMask(OverlayMask mask){
            this(mask.textPainter);
            this.alphaMask = mask.alphaMask;
        }
        /**
         * 
         */
        public void reset(){
            textMask = alphaMask = imgMask = shapeMask = null;
        }
        /**
         * 
         * @param index 
         */
        public boolean reset(int index){
            switch(index){
                case(0):
                    textMask = null;
                    break;
                case(1):
                    alphaMask = imgMask = null;
                    break;
                case(2):
                    shapeMask = null;
            }
            return index == maskTabbedPane.getSelectedIndex();
        }
        /**
         * 
         * @return 
         */
        public boolean isOverlayRendered(){
                // Determine what to return based off the index
            switch (maskTabbedPane.getSelectedIndex()){
                    // The mask is using text
                case (0):
                        // Get the text for the mask 
                    String text = maskTextPane.getText();
                        // Return if the text is neither null nor blank
                    return text != null && !text.isBlank();
                    // The mask is an image
                case(1):
                        // Return if there is an overlay image
                    return !overlayImages.isEmpty();
                    // The mask is using a shape
                case(2):
                        // Return if both of the size spinners are set to values 
                        // greater than zero
                    return (double)maskShapeWidthSpinner.getValue() > 0 && 
                            (double)maskShapeHeightSpinner.getValue() > 0 && 
                            maskShapeCombo.getSelectedIndex() >= 0;
            }
            return false;
        }
        /**
         * 
         * @param width
         * @param height
         * @return 
         */
        public BufferedImage getMask(int width, int height){
            if (!isOverlayRendered())
                return null;
                // Determine what to return based off the index
            switch (maskTabbedPane.getSelectedIndex()){
                    // The mask is using text
                case (0):
                        // Use the text mask, creating it if need be
                    return textMask = getTextMaskImage(width,height,
                            maskTextPane.getText(),textMask,textPainter);
                    // The mask is an image
                case(1):
                        // Get the alpha channel for the overlay image, creating 
                        // it if need be
                    alphaMask = getImageAlphaMask(getOverlayImage(),alphaMask,
                            width,height);
                        // Use the mask version of the alpha image as the mask
                    return imgMask = getImageMaskImage(width,height,alphaMask,
                            imgMask);
                case(2):
                    if (path == null)
                        path = new Path2D.Double();
                    return shapeMask = getShapeMaskImage(width,height,shapeMask,path);
            }
            return null;
        }
        /**
         * 
         * @return 
         */
        public boolean isAntialiased(){
                // Determine what to return based off the index
            switch (maskTabbedPane.getSelectedIndex()){
                    // The mask is using text
                case (0):
                    return textPainter.isAntialiasingEnabled();
                    // The mask is an image
                case(1):
                    return imgMaskAntialiasingToggle.isSelected();
            }
            return true;
        }
        /**
         * 
         * @return 
         */
        protected Object getAntialiasingHint(){
            return (isAntialiased()) ? RenderingHints.VALUE_ANTIALIAS_ON : 
                    RenderingHints.VALUE_ANTIALIAS_OFF;
        }
        /**
         * 
         * @param width
         * @param height
         * @return 
         */
        private Rectangle2D getRotatedBounds(double width, double height){
                // Set the frame of the rectangle
            rect.setFrame(0, 0, width, height);
                // Get the rotation of the mask
            double r = Math.toRadians(getMaskRotation());
                // If the scratch transform is null
            if (tx == null)
                tx = AffineTransform.getRotateInstance(r, rect.getCenterX(), rect.getCenterY());
            else
                tx.setToRotation(r, rect.getCenterX(), rect.getCenterY());
                // Get the bounds of the transformed area
            return tx.createTransformedShape(rect).getBounds2D();
        }
        /**
         * 
         * @param g
         * @param width
         * @param height 
         */
        public void maskOverlay(Graphics2D g, int width, int height){
                // This will get the mask for the overlay
            BufferedImage mask;
                // Get the antialiasing rendering hint
            Object antialiasing = getAntialiasingHint();
                // If the mask is an image
            if (maskTabbedPane.getSelectedIndex() == 1){
                    // This is the bounds of the area to be drawn
                Rectangle2D bounds = getRotatedBounds(width,height);
                    // Get the mask to use
                mask = getMask((int)Math.ceil(bounds.getWidth()),
                        (int)Math.ceil(bounds.getHeight()));
                    // If the mask is null
                if (mask == null)
                    return;
                    // Transform the graphics for the mask
                transformMaskGraphics(g,width,height,bounds);
            } else {    // Get the mask to use
                BufferedImage img = getMask(width,height);
                    // If the mask is null
                if (img == null)
                    return;
                    // This will be the mask to draw
                mask = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
                Graphics2D maskG = configureGraphics(mask.createGraphics());
                   // Enable or disable the antialiasing, depending on whether 
                    // the mask should be antialiased
                maskG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                        antialiasing);
                transformMaskGraphics(maskG,width,height,null);
                maskG.drawImage(img, 0, 0, null);
                maskG.dispose();
            }   // Enable or disable the antialiasing, depending on whether the 
                // mask should be antialiased
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasing);
                // Mask the overlay pixels with the mask image
            maskImage(g,mask);
        }
        @Override
        public void paint(Graphics2D g, Color color, int width, int height) {
                // If the width or height are less than or equal to zero 
                // (nothing would be drawn)
            if (width <= 0 || height <= 0 || !isOverlayRendered())
                return;
                // Create a copy of the given graphics context and configure it
            g = configureGraphics((Graphics2D) g.create());
                // This is a buffered image to draw to if the mask needs to be 
                // drawn to a buffer image
            BufferedImage img = null;
                // This is the graphics context to draw to
            Graphics2D imgG = g;
                // Get the antialiasing rendering hint
            Object antialiasing = getAntialiasingHint();
                // This is the bounds of the area to be drawn
            Rectangle2D bounds = null;
                // If the mask is an image
            if (maskTabbedPane.getSelectedIndex() == 1){
                img = new BufferedImage(width,height,
                        BufferedImage.TYPE_INT_ARGB);
                imgG = configureGraphics(img.createGraphics());
                    // Enable or disable the antialiasing, depending on 
                    // whether the mask should be antialiased
                imgG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                        antialiasing);
                    // Get the bounds for the area to be drawn
                bounds = getRotatedBounds(width,height);
            }   // Enable or disable the antialiasing, depending on whether the 
                // mask should be antialiased
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialiasing);
                // Transform the graphics for the mask
            transformMaskGraphics(imgG,width,height,bounds);
                // If the bounds should be used for this mask
            if (bounds != null){
                    // Transfer the width and height over from the bounds
                width = (int)Math.ceil(bounds.getWidth());
                height = (int)Math.ceil(bounds.getHeight());
            }   // If the given color is not null
            if (color != null)
                    // Set the color for the overlay
                imgG.setColor(color);
                // Determine what to return based off the index
            switch (maskTabbedPane.getSelectedIndex()){
                    // The mask is using text
                case(0):
                        // Paint the text mask
                    paintTextMask(imgG,width,height,maskTextPane.getText(),
                            textPainter);
                    break;
                    // The mask is an image
                case(1):
                        // Fill the area
                    imgG.fillRect(0, 0, width, height);
                        // Mask the area to be filled with the image
                    maskImage(imgG,getMask(width,height));
                    break;
                    // The mask is using a shape
                case(2):
                        // Paint the shape
                    path = paintShapeMask(imgG,width,height,
                            (double)maskShapeWidthSpinner.getValue(),
                            (double)maskShapeHeightSpinner.getValue(),path);
            }   // If this rendered to an image as a buffer 
            if (img != null){
                imgG.dispose();
                    // Draw the image
                g.drawImage(img, 0, 0, null);
            }
            g.dispose();
        }
        @Override
        public void paintIcon2D(Component c, Graphics2D g, int x, int y) {
            g.translate(x, y);
            g.setColor(Color.BLACK);
                // Get the width of the icon
            int width = getIconWidth();
                // Get the height of the icon
            int height = getIconHeight();
            g.fillRect(0, 0, width, height);
            paint(g, Color.WHITE, width, height);
        }
        @Override
        public int getIconWidth() {
            return getImageWidth();
        }
        @Override
        public int getIconHeight() {
            return getImageHeight();
        }
    }
    /**
     * 
     */
    private abstract class FileWorker extends SwingWorker<Void, Void>{
        /**
         * The file to process.
         */
        protected File file;
        /**
         * Whether this was successful at processing the file.
         */
        protected boolean success = false;
        /**
         * This constructs a FileWorker that will process the given file.
         * @param file The file to process.
         */
        FileWorker(File file){
            this.file = file;
        }
        /**
         * This returns whether this was successful at processing the file. 
         * This will be inaccurate up until the file is finished being 
         * processed.
         * @return Whether this has successfully processed the file.
         */
        public boolean isSuccessful(){
            return success;
        }
        /**
         * This returns the file being processed by this FileWorker.
         * @return The file that will be processed.
         */
        public File getFile(){
            return file;
        }
        /**
         * This returns the String that is displayed for the progress bar.
         * @return The String to display on the progress bar.
         */
        public abstract String getProgressString();
        /**
         * This is used to display a success prompt to the user when the file is 
         * successfully processed.
         * @param file The file that was successfully processed.
         */
        protected void showSuccessPrompt(File file){}
        /**
         * This is used to display a failure prompt to the user when the file 
         * fails to be processed. If the failure prompt is a retry prompt, then 
         * this method should return whether to try processing the file again. 
         * Otherwise, this method should return {@code false}.
         * @param file The file that failed to be processed.
         * @param ex The exception that was thrown, or null if there was no 
         * exception thrown.
         * @return {@code true} if this should attempt to process the file 
         * again, {@code false} otherwise.
         */
        protected boolean showFailurePrompt(File file, IOException ex){
            return false;
        }
        @Override
        protected Void doInBackground() throws Exception {
            getLogger().entering(this.getClass().getName(), "doInBackground");
            setInputEnabled(false);
            progressBar.setValue(0);
            progressBar.setIndeterminate(true);
            setProgressString(getProgressString());
                // Whether the user wants this to try processing the file again 
            boolean retry = false;  // if unsuccessful
            do{
                useWaitCursor(true);
                try{
                    success = processFile(file);    // Try to process the file
                    useWaitCursor(false);
                    if (success)    // If the file was successfully processed
                        showSuccessPrompt(file);    // Show the success prompt
                    else            // If the file failed to be processed
                            // Show the failure prompt and get if the user wants 
                        retry = showFailurePrompt(file, null);  // to try again
                } catch (IOException ex){
                    getLogger().log(Level.WARNING, 
                            "Error processing file \""+file+"\"", ex);
                    success = false;
                    useWaitCursor(false);
                        // Show the failure prompt and get if the user wants to 
                    retry = showFailurePrompt(file, ex);    // try again
                }
            }   // While the file failed to be processed and the user wants to 
            while(!success && retry);   // try again
            getLogger().exiting(this.getClass().getName(), "doInBackground");
            return null;
        }
        /**
         * 
         * @param file
         * @return
         * @throws IOException 
         */
        protected abstract boolean processFile(File file) throws IOException;
        @Override
        protected void done(){
            System.gc();        // Run the garbage collector
            progressBar.setValue(0);
            progressBar.setIndeterminate(false);
            setProgressString(null);
            setInputEnabled(true);
            useWaitCursor(false);
        }
    }
    /**
     * This is an abstract class that provides the framework for loading from 
     * files.
     */
    private abstract class FileLoader extends FileWorker{
        /**
         * Whether this is currently loading a file.
         */
        protected volatile boolean loading = false;
        /**
         * Whether file not found errors should be shown.
         */
        protected boolean showFileNotFound;
        /**
         * This constructs a FileLoader that will load the data from the given 
         * file.
         * @param file The file to load the data from.
         * @param showFileNotFound Whether a file not found error should result 
         * in a popup being shown to the user.
         */
        FileLoader(File file, boolean showFileNotFound){
            super(file);
            this.showFileNotFound = showFileNotFound;
        }
        /**
         * This constructs a FileLoader that will load the data from the given 
         * file.
         * @param file The file to load the data from.
         */
        FileLoader(File file){
            this(file,true);
        }
        @Override
        public String getProgressString(){
            return "Loading";
        }
        /**
         * This returns whether this is currently loading from a file.
         * @return Whether a file is currently being loaded.
         */
        public boolean isLoading(){
            return loading;
        }
        /**
         * This returns whether this was successful at loading from the file. 
         * This will be inaccurate up until the file is loaded.
         * @return Whether this has successfully loaded the file.
         */
        @Override
        public boolean isSuccessful(){
            return super.isSuccessful();
        }
        /**
         * This returns whether this shows a failure prompt when the file is not 
         * found.
         * @return Whether the file not found failure prompt is shown.
         */
        public boolean getShowsFileNotFoundPrompts(){
            return showFileNotFound;
        }
        /**
         * This returns the file being loaded by this FileLoader.
         * @return The file that will be loaded.
         */
        @Override
        public File getFile(){
            return super.getFile();
        }
        /**
         * This loads the data from the given file. This is called by {@link 
         * #processFile(File) processFile} in order to load the file.
         * @param file The file to load the data from.
         * @return Whether the file was successfully loaded.
         * @throws IOException 
         * @see #processFile(File) 
         */
        protected abstract boolean loadFile(File file) throws IOException;
        /**
         * {@inheritDoc } This delegates to {@link #loadFile(File) loadFile}.
         * @see #loadFile(File) 
         */
        @Override
        protected boolean processFile(File file) throws IOException{
            loading = true;
            return loadFile(file);
        }
        /**
         * This is used to display a success prompt to the user when the file is 
         * successfully loaded.
         * @param file The file that was successfully loaded.
         */
        @Override
        protected void showSuccessPrompt(File file){}
        /**
         * This is used to display a failure prompt to the user when the file 
         * fails to be loaded. 
         * @param file The file that failed to load.
         * @param ex
         * @return {@inheritDoc}
         */
        @Override
        protected boolean showFailurePrompt(File file, IOException ex){
                // If the file doesn't exist
            if (!file.exists() || ex instanceof FileNotFoundException){
                    // If this should show file not found prompts
                if (showFileNotFound){
                    JOptionPane.showMessageDialog(SpiralGenerator.this, 
                            getFileNotFoundMessage(file,ex), 
                            getFailureTitle(file,ex), 
                            JOptionPane.ERROR_MESSAGE);
                }
                return false;
            }
            else{   // Ask the user if they would like to try loading the file
                    // again
                return JOptionPane.showConfirmDialog(SpiralGenerator.this,
                        getFailureMessage(file,ex)+"\nWould you like to try again?",
                        getFailureTitle(file,ex),JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE) == JOptionPane.YES_OPTION;
            }
        }
        /**
         * This returns the title for the dialog to display if the file fails to 
         * be saved.
         * @param file The file that failed to load.
         * @return The title for the dialog to display if the file fails to
         * save.
         */
        protected String getFailureTitle(File file, IOException ex){
            return "ERROR - File Failed To Load";
        }
        /**
         * This returns the message to display if the file fails to load.
         * @param file The file that failed to load.
         * @return The message to display if the file fails to load.
         */
        protected String getFailureMessage(File file, IOException ex){
            return "The file failed to load.";
        }
        /**
         * This returns the message to display if the file does not exist.
         * @param file The file that did not exist.
         * @return The message to display if the file does not exist.
         */
        protected String getFileNotFoundMessage(File file, IOException ex){
            return "The file does not exist.";
        }
        @Override
        protected void done(){
            loading = false;
            super.done();
        }
    }
    /**
     * This is an abstract class that provides the framework for saving to a 
     * file.
     */
    private abstract class FileSaver extends FileWorker{
        /**
         * Whether this is currently saving a file.
         */
        protected volatile boolean saving = false;
        /**
         * This stores whether this should exit the program after saving.
         */
        protected volatile boolean exitAfterSaving;
        /**
         * This constructs a FileSaver that will save data to the given file 
         * and, if {@code exit} is {@code true}, will exit the program after 
         * saving the file.
         * @param file The file to save the data to.
         * @param exit Whether the program will exit after saving the file.
         */
        FileSaver(File file, boolean exit){
            super(file);
            exitAfterSaving = exit;
        }
        /**
         * This constructs a FileSaver that will save data to the given file.
         * @param file The file to save the data to.
         */
        FileSaver(File file){
            this(file,false);
        }
        @Override
        public String getProgressString(){
            return "Saving";
        }
        /**
         * This returns whether this is currently saving to a file.
         * @return Whether a file is currently being saved to.
         */
        public boolean isSaving(){
            return saving;
        }
        /**
         * This returns whether this was successful at saving to the file. This 
         * will be inaccurate up until the file is saved.
         * @return Whether this has successfully saved the file.
         */
        @Override
        public boolean isSuccessful(){
            return super.isSuccessful();
        }
        /**
         * This returns whether the program will exit after this finishes saving 
         * the file.
         * @return Whether the program will exit once the file is saved.
         */
        public boolean getExitAfterSaving(){
            return exitAfterSaving;
        }
        /**
         * This returns the file being saved to by this FileSaver.
         * @return The file that will be saved.
         */
        @Override
        public File getFile(){
            return super.getFile();
        }
        /**
         * This returns whether this should consider the file returned by {@link 
         * #getFile() getFile} as a directory in which to save files into.
         * @return Whether the file given to this FileSaver is actually a 
         * directory.
         */
        protected boolean isFileDirectory(){
            return false;
        }
        /**
         * This attempts to save to the given file. This is called by {@link 
         * #processFile(File) processFile} in order to save the file.
         * @param file The file to save.
         * @return Whether the file was successfully saved to.
         * @throws IOException
         * @see #processFile(File) 
         */
        protected abstract boolean saveFile(File file) throws IOException;
        /**
         * {@inheritDoc } This delegates the saving of the file to {@link 
         * #saveFile(File) saveFile}.
         * @see #saveFile(File) 
         */
        @Override
        protected boolean processFile(File file) throws IOException{
            saving = true;
                // Try to create the directories and if that fails, then give up 
                // on saving the file. (If the file is the directory, include it 
                // as a directory to be created. Otherwise, create the parent 
                // file of the file to be saved)
            if (!FilesExtended.createDirectories(SpiralGenerator.this, 
                    (isFileDirectory())?file:file.getParentFile()))
                return false;
            return saveFile(file);
        }
        /**
         * This returns the title for the dialog to display if the file is 
         * successfully saved.
         * @param file The file that was successfully saved.
         * @return The title for the dialog to display if the file is 
         * successfully saved.
         */
        protected String getSuccessTitle(File file){
            return "File Saved Successfully";
        }
        /**
         * This returns the message to display if the file is successfully 
         * saved.
         * @param file The file that was successfully saved.
         * @return The message to display if the file is successfully saved.
         */
        protected String getSuccessMessage(File file){
            return "The file was successfully saved.";
        }
        /**
         * This returns the title for the dialog to display if the file fails to 
         * be saved.
         * @param file The file that failed to be saved.
         * @return The title for the dialog to display if the file fails to
         * save.
         */
        protected String getFailureTitle(File file, IOException ex){
            return "ERROR - File Failed To Save";
        }
        /**
         * This returns the message to display if the file fails to be saved.
         * @param file The file that failed to be saved.
         * @return The message to display if the file fails to save.
         */
        protected String getFailureMessage(File file, IOException ex){
            return "The file failed to save.";
        }
        /**
         * This is used to display a success prompt to the user when the file is 
         * successfully saved. The success prompt will display the message 
         * returned by {@link #getSuccessMessage()}. If the program is to exit 
         * after saving the file, then this will show nothing.
         * @param file The file that was successfully saved.
         */
        @Override
        protected void showSuccessPrompt(File file){
                // If the program is not to exit after saving the file
            if (!exitAfterSaving)   
                JOptionPane.showMessageDialog(SpiralGenerator.this, 
                        getSuccessMessage(file), getSuccessTitle(file), 
                        JOptionPane.INFORMATION_MESSAGE);
        }
        /**
         * This is used to display a failure and retry prompt to the user when 
         * the file fails to be saved.
         * @param file The file that failed to be saved.
         * @param ex
         * @return {@inheritDoc }
         */
        @Override
        protected boolean showFailurePrompt(File file, IOException ex){
                // Get the message to be displayed. If the file failed to be 
                // backed up, show the backup failed message. Otherwise show the 
                // normal failure message.
            String message = getFailureMessage(file,ex);
                // Show a dialog prompt asking the user if they would like to 
                // try and save the file again and get their input. 
            int option = JOptionPane.showConfirmDialog(
                    SpiralGenerator.this, 
                    message+"\nWould you like to try again?",
                    getFailureTitle(file,ex),
                        // If the program is to exit after saving the file, show 
                        // a third "cancel" option to allow the user to cancel 
                        // exiting the program
                    (exitAfterSaving)?JOptionPane.YES_NO_CANCEL_OPTION:
                            JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE);
                // If the program was going to exit after saving the file
            if (exitAfterSaving){   
                    // If the option selected was the cancel option or the user 
                    // closed the dialog without selecting anything, then don't 
                    // exit the program
                exitAfterSaving = option != JOptionPane.CLOSED_OPTION && 
                        option != JOptionPane.CANCEL_OPTION;
            }   // Return whether the user selected yes
            return option == JOptionPane.YES_OPTION;    
        }
        /**
         * This is used to exit the program after this finishes saving the file.
         */
        protected void exitProgram(){
            System.exit(0);         // Exit the program
        }
        @Override
        protected void done(){
            if (exitAfterSaving)    // If the program is to exit after saving
                exitProgram();      // Exit the program
            saving = false;
            super.done();
        }
    }
    /**
     * 
     */
    private class AnimationSaver extends FileSaver{
        /**
         * 
         */
        private List<BufferedImage> frames = null;
        /**
         * 
         */
        private SpiralPainter painter = null;
        /**
         * 
         */
        private OverlayMask mask = null;
        /**
         * This is the current icon for the preview label.
         */
        private Icon currentIcon = null;
        /**
         * 
         * @param file 
         */
        AnimationSaver(File file) {
            super(file);
        }
        @Override
        protected boolean saveFile(File file) throws IOException {
            progressBar.setIndeterminate(true);
                // If the current icon has not been set yet
            if (currentIcon == null)
                currentIcon = previewLabel.getIcon();
            showTestSpiralToggle.setEnabled(false);
                // Create the necessary file output streams for writing to the 
                // file, and a buffered output stream to write to the file stream
            try(FileOutputStream fileOut = new FileOutputStream(file);
                    BufferedOutputStream buffOut = new BufferedOutputStream(fileOut)){
                    // If the frame list is null
                if (frames == null)
                    frames = new ArrayList<>();
                    // If the spiral painter copy is null
                if (painter == null)
                    painter = getSpiralPainter().clone();
                    // If the overlay mask copy is null
                if (mask == null)
                    mask = new OverlayMask(overlayMask);
                    // This is a set of RGB values for the colors in a given 
                    // frame
                Set<Integer> colors = new TreeSet<>();
                    // This is a set of possible RGB values to use for the 
                    // transparent backgrounds when combining frames
                TreeSet<Integer> possibleColors = new TreeSet<>();
                progressBar.setValue(0);
                progressBar.setIndeterminate(false);
                    // Create an encoder to encode the gif
                AnimatedGifEncoder encoder = new AnimatedGifEncoder();
                    // Get the image width
                int width = getImageWidth();
                    // Get the image height
                int height = getImageHeight();
                    // Start encoding to the buffered output stream
                encoder.start(buffOut);
                    // Repeat infinitely
                encoder.setRepeat(0);
                    // Use the spiral frame duration for the frames
                encoder.setDelay(animationTimer.getDelay());
                    // Set the size for the image
                encoder.setSize(width, height);
                    // Get the background color for the spiral
                Color bg = models[0].getColor1();
                    // Get if the background has transparency
                boolean transparency = bg.getAlpha() < 255;
                    // Get the background without an alpha
                bg = new Color(bg.getRGB());
                    // Set the background for the gif
                encoder.setBackground(bg);
                    // This is the disposal mode for each frame. 
                    // 0 - Don't care
                    // 1 - combine 
                    // 2 - replace
                int disposal = (optimizeDifferenceToggle.isSelected())?1:2;
                    // If the background is transparent
                if (transparency){
                    encoder.setTransparent(bg);
                        // Replace each frame
                    disposal = 2;
                }   // Set the disposal mode for the GIF
                encoder.setDispose(disposal);
                    // A for loop to go through and add all the frames to the 
                    // gif
                for (int i = 0; i < SPIRAL_FRAME_COUNT; i++){
                        // This gets the current frame
                    BufferedImage frame;
                        // If the frame is in the frames list
                    if (i < frames.size())
                        frame = frames.get(i);
                    else {  // Create the image for the frame
                        frame = new BufferedImage(width, height, 
                                BufferedImage.TYPE_INT_ARGB);
                            // Get a graphics context for the image
                        Graphics2D g = frame.createGraphics();
                            // Paint the spiral design on the image
                        paintSpiralDesign(g,i,width,height,painter,mask);
                        g.dispose();
                        frames.add(frame);
                    }   // Set the preview to the current frame
                    previewLabel.setImage(frame);
                        // If frames should be combined and this is not the 
                        // first frame of the animation
                    if (disposal == 1 && i > 0){
                            // Get the difference between the current and 
                            // previous frames
                        frame = SpiralGeneratorUtilities.getImageDifference(
                                frames.get(i-1), frame, colors);
                            // If there's no a background color or that color is 
                            // in the frame colors
                        if (bg == null || colors.contains(bg.getRGB() & 0x00FFFFFF)){
                                // Clear the set of possible colors
                            possibleColors.clear();
                                // Go through the spiral models
                            for (SpiralModel model : models){
                                    // Add the inverse of the first color
                                possibleColors.add((~model.getColor1().getRGB()) & 0x00FFFFFF);
                                    // Add the inverse of the second color
                                possibleColors.add((~model.getColor2().getRGB()) & 0x00FFFFFF);
                            }   // Go through the predefined colors
                            for (Color temp : GIF_TRANSPARENCY_COLORS){
                                    // Add the color as a possible color
                                possibleColors.add(temp.getRGB() & 0x00FFFFFF);
                            }   // Remove all the colors in the frame
                            possibleColors.removeAll(colors);
                                // If all of those colors are in the frame
                            if (possibleColors.isEmpty()){
                                    // Go through the colors in the frame
                                for (Integer temp : colors){
                                        // Add the inverse of the current color
                                    possibleColors.add((~temp) & 0x00FFFFFF);
                                }   // Remove all the colors in the frame
                                possibleColors.removeAll(colors);
                            }   // Reset the background color to null
                            bg = null;
                                // If there are no possible colors
                            if (possibleColors.isEmpty()){
                                getLogger().warning(
                                        "Failed to find color to use for transparency, bruteforcing color");
                                    // Go through the possible colors in the RGB 
                                    // range of colors
                                for (int c = 0; c <= 0x00FFFFFF && bg == null; c++){
                                        // If the current color is not used
                                    if (!colors.contains(c))
                                        bg = new Color(c);
                                }
                            } else 
                                bg = new Color(possibleColors.first());
                        }   // Set the background to the transparency color
                        encoder.setBackground(bg);
                            // Make the background color transparent.
                        encoder.setTransparent(bg, false);
                    }   // Add the frame to the gif
                    encoder.addFrame(frame);
                    progressBar.setValue(progressBar.getValue()+1);
                }
                progressBar.setIndeterminate(true);
                    // Finish encoding the gif
                encoder.finish();
            }
            return true;
        }
        @Override
        protected void done(){
            super.done();
            showTestSpiralToggle.setEnabled(true);
            previewLabel.setIcon(currentIcon);
        }
        @Override
        protected String getSuccessTitle(File file){
            return "Animation Saved Successfully";
        }
        @Override
        protected String getSuccessMessage(File file){
            return "The animation was successfully saved.";
        }
        @Override
        protected String getFailureTitle(File file, IOException ex){
            return "ERROR - Animation Failed To Save";
        }
        @Override
        protected String getFailureMessage(File file, IOException ex){
            String msg = "";
            if (ex != null)
                msg = "\nError: "+ex;
            return "There was an error saving the animation to file\n"+
                    "\""+file+"\"."+msg;
        }
        @Override
        public String getProgressString(){
            return "Saving Animation";
        }
    }
    /**
     * 
     */
    private class ImageLoader extends FileLoader{
        /**
         * 
         */
        private List<BufferedImage> imgs = null;
        /**
         * Whether this is being used during the initial loading of the program
         */
        private boolean initLoad;
        /**
         * This is the index of the overlay image to use if loaded successfully.
         */
        private int index;
        /**
         * 
         * @param file 
         */
        ImageLoader(File file, boolean initialLoad, int index) {
            super(file,!initialLoad);
            this.initLoad = initialLoad;
            this.index = index;
        }
        /**
         * 
         * @param file 
         */
        ImageLoader(File file, boolean initialLoad) {
            this(file,initialLoad,0);
        }
        /**
         * 
         * @param file 
         */
        ImageLoader(File file, int index){
            this(file,false,index);
        }
        /**
         * 
         * @param file 
         */
        ImageLoader(File file){
            this(file,0);
        }
        @Override
        protected boolean loadFile(File file) throws IOException {
            getLogger().entering(this.getClass().getName(), "loadFile", file.getName());
            imgs = loadImage(file,imgs);
            getLogger().exiting(this.getClass().getName(), "loadFile", !imgs.isEmpty());
            return !imgs.isEmpty();
        }
        @Override
        protected String getFailureTitle(File file, IOException ex){
            return "ERROR - Image Failed To Load";
        }
        @Override
        protected String getFailureMessage(File file, IOException ex){
            String msg = "";
            if (ex != null)
                msg = "\nError: "+ex;
            return "The image failed to load."+msg;
        }
        @Override
        public String getProgressString(){
            return "Loading Image Mask";
        }
        @Override
        protected void done(){
            overlayFile = null;
                // If this was successful in loading the image
            if (success){
                setOverlayImages(imgs,index,file,initLoad);
                // If the program failed to load the image mask at the start of 
                // the program
            } else if (initLoad){
                config.setMaskImageFile(null);
                config.setMaskImageFrameIndex(0);
            }
            super.done();
        }
    }
    /**
     * 
     */
    private class UpdateCheckWorker extends SwingWorker<Boolean, Void>{
        /**
         * This gets whether there is an update available for the program.
         */
        private boolean updateAvailable = false;
        
        private boolean success = false;
        /**
         * Whether this is being called at the start of the program.
         */
        private boolean isAtStart;
        /**
         * 
         * @param isAtStart 
         */
        UpdateCheckWorker(boolean isAtStart){
            this.isAtStart = isAtStart;
        }
        @Override
        protected Boolean doInBackground() throws Exception {
            getLogger().entering(this.getClass().getName(), "doInBackground");
            setInputEnabled(false);
            progressBar.setValue(0);
            progressBar.setIndeterminate(true);
            setProgressString("Checking For Updates");
                // Whether this should retry to check for an update
            boolean retry = false;
            do{
                useWaitCursor(true);
                try{    // Check for an update
                    updateChecker.check();
                    success = true;
                } catch (Exception ex){
                    getLogger().log(Level.WARNING, 
                            "An error occurred while checking the latest version",
                            ex);
                    useWaitCursor(false);
                        // Ask the user if they would like to try checking for 
                        // updates again
                    retry = JOptionPane.showConfirmDialog(getParentComponent(),
                        "Failed to check for updates.\nWould you like to try again?",
                        "Update Checker Failed",JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE) == JOptionPane.YES_OPTION;
                }
            }   // While this has not checked for an update and the user wants 
                // to try again
            while (!success && retry);
                // Get whether there is an update available
            updateAvailable = updateChecker.isUpdateAvailable();
            getLogger().exiting(this.getClass().getName(), "doInBackground", 
                    updateAvailable);
            return updateAvailable;
        }
        /**
         * 
         * @return 
         */
        protected Component getParentComponent(){
            return (isAtStart)?SpiralGenerator.this:aboutDialog;
        }
        @Override
        protected void done(){
                // If this was successful at checking for an update
            if (success){
                    // If there's an update available, then set the text for the 
                    // latest version label to be the latest version for the 
                    // program. Otherwise, just state the current version
                latestVersLabel.setText((updateAvailable) ? 
                        updateChecker.getLatestVersion() : 
                        updateChecker.getCurrentVersion());
            }
            System.gc();        // Run the garbage collector
            progressBar.setValue(0);
            progressBar.setIndeterminate(false);
            setProgressString(null);
            setInputEnabled(true);
            useWaitCursor(false);
                // If this was successful at checking for an update
            if (success){
                    // If there is an update available for the program
                if (updateAvailable){
                        // Log the update
                    updateChecker.logUpdateMessage(getLogger());
                        // Set the update check dialog's location relative to 
                        // this if at startup and relative to the about dialog 
                        // if the check was initialized from there.
                    updateCheckDialog.setLocationRelativeTo(getParentComponent());
                    updateCheckDialog.setVisible(true);
                } else if (!isAtStart){
                    JOptionPane.showMessageDialog(aboutDialog, 
                            "This program is already up to date,",
                            updateCheckDialog.getTitle(), 
                            JOptionPane.INFORMATION_MESSAGE, 
                            updateIconLabel.getIcon());
                }
            }
        }
    }
    /**
     * 
     */
    private class ConfigDataSaver extends FileSaver{
        /**
         * 
         */
        private SpiralGeneratorProperties prop = null;
        /**
         * 
         * @param file 
         */
        ConfigDataSaver(File file) {
            super(file);
        }
        @Override
        protected boolean saveFile(File file) throws IOException {
            if (prop == null)
                prop = toProperties(file);
            try (PrintWriter out = new PrintWriter(file)){
                prop.store(out);
            }
            return true;
        }
        @Override
        protected String getSuccessTitle(File file){
            return "Config Data Saved Successfully";
        }
        @Override
        protected String getSuccessMessage(File file){
            return "The configuration data was successfully saved.";
        }
        @Override
        protected String getFailureTitle(File file, IOException ex){
            return "ERROR - Config Data Failed To Save";
        }
        @Override
        protected String getFailureMessage(File file, IOException ex){
            String msg = "";
            if (ex != null)
                msg = "\nError: "+ex;
            return "There was an error saving the configuration frame data to file"+
                    "\n\""+file+"\"."+msg;
        }
        @Override
        public String getProgressString(){
            return "Saving Config Data";
        }
    }
    /**
     * 
     */
    private class ConfigDataLoader extends FileLoader{
        /**
         * 
         */
        private List<BufferedImage> imgs = null;
        /**
         * 
         */
        private File imgFile = null;
        /**
         * The index of the image
         */
        private int imgIndex = 0;
        /**
         * 
         */
        private SpiralGeneratorProperties prop = new SpiralGeneratorProperties();
        /**
         * 
         */
        private boolean propLoaded = false;
        /**
         * 
         * @param file
         * @param showFileNotFound 
         */
        ConfigDataLoader(File file, boolean showFileNotFound) {
            super(file, showFileNotFound);
        }
        /**
         * 
         * @param file 
         */
        ConfigDataLoader(File file){
            this(file,true);
        }
        @Override
        protected boolean loadFile(File file) throws IOException {
            getLogger().entering(this.getClass().getName(), "loadFile", file.getName());
            if (!propLoaded){
                try (FileReader in = new FileReader(file)){
                    prop.load(in);
                    imgFile = prop.getMaskImageFile();
                    if (imgFile != null)
                        imgFile = FilesExtended.resolve(file,imgFile);
                    imgIndex = prop.getMaskImageFrameIndex();
                    propLoaded = true;
                    setProgressString(getProgressString());
                    showFileNotFound = true;
                }
            }
            if (imgFile != null){
                if (!imgFile.exists())
                    throw new FileNotFoundException("Image file not found");
                imgs = loadImage(imgFile,imgs);
                getLogger().exiting(this.getClass().getName(), "loadFile", !imgs.isEmpty());
                return !imgs.isEmpty();
            }
            getLogger().exiting(this.getClass().getName(), "loadFile", true);
            return true;
        }
        @Override
        protected String getFailureTitle(File file, IOException ex){
            return String.format("ERROR - %s Failed To Load", (propLoaded)?
                    "Config Data":"Image");
        }
        @Override
        protected String getFailureMessage(File file, IOException ex){
            String msg = "";
            if (ex != null)
                msg = "\nError: "+ex;
            if (propLoaded && imgFile != null)
                return "The image file specified by the configuration data failed to load."
                        + "\n\""+imgFile+"\""+msg;
            return "The configuration frame data failed to load."+msg;
        }
        @Override
        protected String getFileNotFoundMessage(File file, IOException ex){
            if (propLoaded && imgFile != null)
                return "The image file specified by the configuration data does not exist."
                        + "\n\""+imgFile+"\"";
            return super.getFileNotFoundMessage(file, ex);
        }
        @Override
        public String getProgressString(){
            if (propLoaded)
                return "Loading Image";
            return "Loading Config Data";
        }
        @Override
        protected void done(){
            if (propLoaded){
                loadFromSettings(prop);
                if (imgFile != null && success){
                    setOverlayImages(imgs,imgIndex,imgFile,false);
                } else {
                    overlayFile = null;
                    config.setMaskImageFile(null);
                    config.setMaskImageFrameIndex(0);
                }
                config.setSpiralType(spiralTypeCombo.getSelectedIndex());
                for (int i = 0; i < colorIcons.length; i++){
                    config.setSpiralColor(i, prop.getSpiralColor(i));
                }
                config.setMaskType(prop.getMaskType());
                config.setMaskFlags(prop.getMaskFlags());
                config.setMaskDesaturateMode(prop.getMaskDesaturateMode());
                config.setMaskImageInverted(prop.isMaskImageInverted());
                config.setMaskAlphaIndex(prop.getMaskAlphaIndex());
                config.setMaskImageInterpolation(prop.getMaskImageInterpolation());
                config.setMaskImageAntialiased(prop.isMaskImageAntialiased());
                config.setMaskShapeType(prop.getMaskShapeType());
                config.setMaskShapeSizeLinked(prop.isMaskShapeSizeLinked());
            }
            super.done();
        }
    }
}
