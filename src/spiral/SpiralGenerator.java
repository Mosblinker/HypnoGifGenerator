/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package spiral;

import anim.*;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import components.JColorSelector;
import components.debug.DebugCapable;
import components.text.CompoundUndoManager;
import components.text.action.commands.TextComponentCommands;
import components.text.action.commands.UndoManagerCommands;
import files.FilesExtended;
import files.extensions.ImageExtensions;
import icons.Icon2D;
import icons.box.ColorBoxIcon;
import icons.box.DisabledBoxIcon;
import io.github.dheid.fontchooser.FontDialog;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.coobird.thumbnailator.Thumbnailator;
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
    public static final String PROGRAM_VERSION = "0.6.1";
    /**
     * This is the name of the program.
     */
    public static final String PROGRAM_NAME = "Hypno Gif Generator";
    /**
     * This is an array containing the widths and heights for the icon images 
     * for this program. 
     */
    private static final int[] ICON_SIZES = {16, 24, 32, 48, 64, 96, 128, 256, 512};
    
    private static final String ICON_FILE_IMAGE = "/images/icon.png";
    
    private static final String TEST_IMAGE_FILE_FOLDER = "DevStuff/images";
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
    private static final int SPIRAL_FRAME_DURATION = 20;
    /**
     * This is the name of the preference node used to store the settings for 
     * this program.
     */
    private static final String PREFERENCE_NODE_NAME = 
            "milo/spiral/jack/HypnoGifGenerator";
    /**
     * This is an array that contains the default colors used for the spiral.
     */
    private static final Color[] DEFAULT_SPIRAL_COLORS = {
        Color.WHITE,
        Color.BLACK,
        new Color(0x0084D7),
        new Color(0xA184B2)
    };
    
    private static final String OVERLAY_MASK_FILE_CHOOSER_NAME = "OverlayFC";
    
    private static final String SAVE_FILE_CHOOSER_NAME = "SaveFC";
    
    private static final String COLOR_SELECTOR_NAME = "ColorSelector";
    
//    private static final String FONT_SELECTOR_NAME = "FontSelector";
    
    private static final String MASK_DIALOG_NAME = "MaskDialog";
    
    /**
     * Creates new form SpiralGenerator
     * @param debugMode
     */
    public SpiralGenerator(boolean debugMode) {
        this.debugMode = debugMode;
            // This will get the preference node for the program
        Preferences node = null;
        try{    // Try to get the preference node used for the program
            node = Preferences.userRoot().node(PREFERENCE_NODE_NAME);
        } catch (SecurityException | IllegalStateException ex){
            System.out.println("Unable to load preference node: " +ex);
            // TODO: Error message window
        }
        config = new SpiralGeneratorConfig(node);
        colorIcons = new ColorBoxIcon[DEFAULT_SPIRAL_COLORS.length];
            // A for loop to create the color icons with their respective colors
        for (int i = 0; i < colorIcons.length; i++){
            colorIcons[i] = new ColorBoxIcon(16,16,config.getSpiralColor(i, 
                    DEFAULT_SPIRAL_COLORS[i]));
        }
        
        spiralPainters = new SpiralPainter[]{
            new LogarithmicSpiralPainter(),
            new ArithmeticSpiralPainter(),
            new ConcentricSpiralPainter(),
            new RippleSpiralPainter()
        };
        for (SpiralPainter painter : spiralPainters){
            try{
                painter.fromByteArray(config.getSpiralData(painter));
            } catch (IllegalArgumentException ex) {}
        }
        
        overlayMask.textPainter.setAntialiasingEnabled(
                config.isMaskTextAntialiased(
                        overlayMask.textPainter.isAntialiasingEnabled()));
        overlayMask.textPainter.setLineSpacing(config.getMaskLineSpacing(
                overlayMask.textPainter.getLineSpacing()));
        
        colorButtons = new HashMap<>();
        colorIndexes = new HashMap<>();
        spiralCompLabels = new HashMap<>();
        
        spiralIcon = new SpiralIcon();
        
        int spiralType = config.getSpiralType();
        int maskType = config.getMaskType();
        initComponents();
        for (JLabel label : new JLabel[]{
            radiusLabel,baseLabel,balanceLabel,dirLabel,angleLabel
        }){
            spiralCompLabels.put(label.getLabelFor(), label);
        }
        
        BufferedImage iconImg = null;
        try {
            iconImg = ImageIO.read(this.getClass().getResource(ICON_FILE_IMAGE));
        } catch (IOException ex) {
            Logger.getLogger(SpiralGenerator.class.getName()).log(Level.WARNING, null, ex);
        }
        LogarithmicSpiralPainter iconPainter = new LogarithmicSpiralPainter();
        ArrayList<BufferedImage> iconImages = new ArrayList<>();
        for (int size : ICON_SIZES){
            if (iconImg != null){
                iconImages.add(Thumbnailator.createThumbnail(iconImg, size, size));
            } else {
                iconPainter.setSpiralRadius(size/3.0);
                BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                int offset = (int)Math.floorDiv(size, 128);
                g.translate(offset, offset);
                int imgSize = size-offset-offset;
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, imgSize, imgSize);
                g.setColor(Color.WHITE);
                iconPainter.paint(g, 0.0, imgSize, imgSize);
                g.dispose();
                iconImages.add(img);
            }
        }
        setIconImages(iconImages);
        
        editCommands = new TextComponentCommands(maskTextArea);
        undoCommands = new UndoManagerCommands(new CompoundUndoManager());
        maskPopup.add(undoCommands.getUndoAction());
        maskPopup.add(undoCommands.getRedoAction());
        maskPopup.addSeparator();
        maskPopup.add(editCommands.getCopyAction());
        maskPopup.add(editCommands.getCutAction());
        maskPopup.add(editCommands.getPasteAction());
        maskPopup.add(editCommands.getDeleteAction());
        maskPopup.addSeparator();
        maskPopup.add(editCommands.getSelectAllAction());
        
        editCommands.addToTextComponent();
        undoCommands.addToTextComponent(maskTextArea);
        
        SpiralHandler handler = new SpiralHandler();
        colorIndexes.put(color1Button, 0);
        colorIndexes.put(color2Button, 1);
        colorIndexes.put(color3Button, 2);
        colorIndexes.put(color4Button, 3);
        for (Map.Entry<JButton,Integer> entry : colorIndexes.entrySet()){
            JButton button = entry.getKey();
            ColorBoxIcon icon = colorIcons[entry.getValue()];
            colorButtons.put(icon, button);
            button.addActionListener(handler);
            button.setIcon(icon);
            button.setDisabledIcon(new DisabledBoxIcon(icon));
        }
        
        frameSlider.setMaximum(SPIRAL_FRAME_COUNT-1);
        progressBar.setMaximum(SPIRAL_FRAME_COUNT);
        updateFrameNumberDisplayed();
        animationTimer = new javax.swing.Timer(SPIRAL_FRAME_DURATION, (ActionEvent e) -> {
            progressAnimation(e);
        });
        previewLabel.setIcon(spiralIcon);
        maskPreviewLabel.setIcon(new MaskPreviewIcon());
        config.setComponentName(maskFC, OVERLAY_MASK_FILE_CHOOSER_NAME);
        config.setComponentName(saveFC, SAVE_FILE_CHOOSER_NAME);
        config.setComponentName(colorSelector, COLOR_SELECTOR_NAME);
//        config.setComponentName(fontSelector, FONT_SELECTOR_NAME);
        config.setComponentName(maskDialog, MASK_DIALOG_NAME);
        config.loadFileChooser(maskFC);
        config.loadFileChooser(saveFC);
        config.loadComponentSize(colorSelector);
//        config.loadComponentSize(fontSelector);
        
        SwingExtendedUtilities.setComponentSize(SpiralGenerator.this, 960, 575);
        config.getProgramBounds(SpiralGenerator.this);
        
        spiralTypeCombo.setSelectedIndex(Math.max(Math.min(spiralType, 
                        spiralPainters.length-1), 0));
        maskTabbedPane.setSelectedIndex(Math.max(Math.min(maskType, 
                maskTabbedPane.getTabCount()-1), 0));
        config.loadMaskAlphaIndex(maskAlphaButtons);
        loadSpiralPainter();
        angleSpinner.setValue(config.getSpiralRotation());
        spinDirCombo.setSelectedIndex((config.isSpinClockwise())?0:1);
        fontAntialiasingToggle.setSelected(overlayMask.textPainter.isAntialiasingEnabled());
        imgMaskAntialiasingToggle.setSelected(config.isMaskImageAntialiased());
        lineSpacingSpinner.setValue(overlayMask.textPainter.getLineSpacing());
        maskScaleSpinner.setValue(config.getMaskScale());
        delaySpinner.setValue(config.getFrameDuration(SPIRAL_FRAME_DURATION));
        
        alwaysScaleToggle.setSelected(config.isImageAlwaysScaled());
        previewLabel.setImageAlwaysScaled(alwaysScaleToggle.isSelected());
        maskPreviewLabel.setImageAlwaysScaled(alwaysScaleToggle.isSelected());
        
        widthSpinner.setValue(config.getImageWidth());
        heightSpinner.setValue(config.getImageHeight());
        
        Font font = config.getMaskFont(maskTextArea.getFont());
        maskTextArea.setFont(font);
        boldToggle.setSelected(font.isBold());
        italicToggle.setSelected(font.isItalic());
        
        maskTextArea.setText(config.getMaskText());
        
        for (SpiralPainter painter : spiralPainters)
            painter.addPropertyChangeListener(handler);
        overlayMask.textPainter.addPropertyChangeListener(handler);
        maskTextArea.getDocument().addDocumentListener(handler);
        
        if (debugMode){
            testSpiralPainter = spiralPainters[spiralPainters.length-1];
            testComponents = new HashMap<>();
            previewLabel.setComponentPopupMenu(debugPopup);
            testImages = new ArrayList<>();
            File prgDir = null;
            try{
                java.net.URL url = SpiralPainter.class.getProtectionDomain().getCodeSource().getLocation();
                if (url != null){
                    prgDir = new File(url.toURI()).getParentFile();
                    if (prgDir.getParentFile() != null)
                        prgDir = prgDir.getParentFile();
                }
            } catch (URISyntaxException ex) {}
            if (prgDir == null)
                prgDir = new File(System.getProperty("user.dir"));
            File imgDir = new File(prgDir,TEST_IMAGE_FILE_FOLDER);
            if (imgDir.exists()){
                List<File> files = FilesExtended.getFilesFromFolder(imgDir, (File pathname) -> {
                    if (pathname == null)
                        return false;
                    if (imgDir.equals(pathname))
                        return true;
                    String name = pathname.getName();
                    if (name == null) 
                        return false;
                    if (name.startsWith("test") && name.endsWith(".png")) {
                        try {
                            Integer.valueOf(name.substring(4, name.length()-4));
                            return true;
                        }catch (NumberFormatException ex){}
                    }
                    return false;
                }, 1);
                files.remove(imgDir);
                files.sort((File o1, File o2) -> {
                    String name1 = o1.getName();
                    String name2 = o2.getName();
                    return Integer.compare(
                            Integer.parseInt(name1.substring(4,name1.length()-4)),
                            Integer.parseInt(name2.substring(4,name2.length()-4)));
                });
                for (File file : files){
                    try {
                        testImages.add(ImageIO.read(file));
                    } catch (IOException ex) {
                        Logger.getLogger(SpiralGenerator.class.getName()).log(
                                Level.INFO, null, ex);
                    }
                }
            }
            if (testImages.isEmpty())
                testSpiralImageSpinner.setEnabled(false);
            else
                testSpiralImageSpinner.setModel(new SpinnerNumberModel(
                        config.getDebugTestImage(testImages.size()), -1, 
                        testImages.size()-1, 1));
            testRotateSpinner.setValue(config.getDebugTestRotation());
            testScaleSpinner.setValue(config.getDebugTestScale());
            testComponents.put(Double.class, Arrays.asList());
            testComponents.put(Boolean.class, Arrays.asList());
            testComponents.put(Integer.class, Arrays.asList());
            DebugTestComponentHandler debugHandler = new DebugTestComponentHandler();
            for (Map.Entry<Class, List<Component>> entry : testComponents.entrySet()){
                Class type = entry.getKey();
                List<Component> list = entry.getValue();
                for (int i = 0; i < list.size(); i++){
                    Object value = null;
                    Component c = list.get(i);
                    if (Double.class.equals(type))
                        value = config.getDebugTestDouble(i);
                    else if (Integer.class.equals(type))
                        value = config.getDebugTestInteger(i);
                    else if (Boolean.class.equals(type)){
                        if (c instanceof JToggleButton){
                            JToggleButton b = (JToggleButton)c;
                            b.setSelected(config.getDebugTestBoolean(i,b.isSelected()));
                        } else 
                            value = config.getDebugTestBoolean(i);
                    }
                    else if (String.class.equals(type))
                        value = config.getDebugTestString(i);
                    if (c instanceof JToggleButton)
                        ((JToggleButton)c).addActionListener(debugHandler);
                    else if (c instanceof JSpinner){
                        if (value != null)
                            ((JSpinner)c).setValue(value);
                        ((JSpinner)c).addChangeListener(debugHandler);
                    }
                }
            }
        }
    }
    
    public SpiralGenerator() {
        this(false);
    }
    
    private SpiralPainter getSpiralPainter(int index){
        if (index >= 0 && index < spiralPainters.length)
            return spiralPainters[index];
        return null;
    }
    
    private SpiralPainter getSpiralPainter(){
        return getSpiralPainter(spiralTypeCombo.getSelectedIndex());
    }
    
    private void loadSpiralPainter(SpiralPainter painter){
        if (painter == null)
            return;
        dirCombo.setSelectedIndex((painter.isClockwise())?0:1);
        radiusSpinner.setValue(painter.getSpiralRadius());
        balanceSpinner.setValue(painter.getBalance());
        boolean isLog = painter instanceof LogarithmicSpiral;
        if (isLog)
            baseSpinner.setValue(((LogarithmicSpiral)painter).getBase());
        baseSpinner.setVisible(isLog);
        for (Map.Entry<Component, JLabel> entry : spiralCompLabels.entrySet()){
            entry.getValue().setVisible(entry.getKey().isVisible());
        }
    }
    
    private void loadSpiralPainter(){
        loadSpiralPainter(getSpiralPainter());
    }
    
    private BufferedImage createSpiralFrame(int frameIndex,int width,int height, 
            SpiralPainter spiralPainter,OverlayMask mask){
        BufferedImage img = new BufferedImage(width, height, 
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        paintSpiralDesign(g,frameIndex,width,height,spiralPainter,mask);
        g.dispose();
        return img;
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
        maskTextArea = new javax.swing.JTextArea();
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
        javax.swing.Box.Filler filler14 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        javax.swing.Box.Filler filler15 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        maskAlphaCtrlPanel = new javax.swing.JPanel();
        maskAlphaToggle = new javax.swing.JRadioButton();
        maskAlphaColorCtrlPanel = new javax.swing.JPanel();
        maskAlphaBlackToggle = new javax.swing.JRadioButton();
        maskAlphaWhiteToggle = new javax.swing.JRadioButton();
        javax.swing.Box.Filler filler17 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 32767));
        maskAlphaRedToggle = new javax.swing.JRadioButton();
        maskAlphaGreenToggle = new javax.swing.JRadioButton();
        maskAlphaBlueToggle = new javax.swing.JRadioButton();
        maskScaleLabel = new javax.swing.JLabel();
        maskScaleSpinner = new javax.swing.JSpinner();
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
        maskAlphaButtons = new javax.swing.ButtonGroup();
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
        color1Button = new javax.swing.JButton();
        color2Button = new javax.swing.JButton();
        color3Button = new javax.swing.JButton();
        color4Button = new javax.swing.JButton();
        maskEditButton = new javax.swing.JButton();
        spiralTypeLabel = new javax.swing.JLabel();
        spiralTypeCombo = new javax.swing.JComboBox<>();
        imageSizePanel = new javax.swing.JPanel();
        widthLabel = new javax.swing.JLabel();
        javax.swing.Box.Filler filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 32767));
        widthSpinner = new javax.swing.JSpinner();
        javax.swing.Box.Filler filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 32767));
        heightLabel = new javax.swing.JLabel();
        javax.swing.Box.Filler filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 0), new java.awt.Dimension(6, 32767));
        heightSpinner = new javax.swing.JSpinner();
        ctrlButtonPanel = new javax.swing.JPanel();
        saveButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        alwaysScaleToggle = new javax.swing.JCheckBox();
        progressBar = new javax.swing.JProgressBar();
        delaySpinner = new javax.swing.JSpinner();
        delayLabel = new javax.swing.JLabel();

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
        maskFC.setFileFilter(ImageExtensions.PNG_FILTER);

        saveFC.setAccessory(saveFCPreview);
        saveFC.setDialogType(javax.swing.JFileChooser.SAVE_DIALOG);
        saveFC.setFileFilter(ImageExtensions.GIF_FILTER);

        maskDialog.setTitle("Edit Message Mask");
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

        maskTextArea.setColumns(20);
        maskTextArea.setRows(5);
        maskTextArea.setComponentPopupMenu(maskPopup);
        maskTextScrollPane.setViewportView(maskTextArea);

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
                    .addComponent(maskTextScrollPane)
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
                        .addGap(0, 30, Short.MAX_VALUE)))
                .addContainerGap())
        );
        textMaskCtrlPanelLayout.setVerticalGroup(
            textMaskCtrlPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(textMaskCtrlPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(maskTextScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 181, Short.MAX_VALUE)
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
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 6);
        maskImageCtrlPanel.add(loadMaskButton, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
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
        maskImageCtrlPanel.add(imgMaskAntialiasingToggle, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        maskImageCtrlPanel.add(filler14, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        maskImageCtrlPanel.add(filler15, gridBagConstraints);

        maskAlphaCtrlPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Mask Alpha Channel"));
        maskAlphaCtrlPanel.setLayout(new java.awt.BorderLayout(0, 7));

        maskAlphaButtons.add(maskAlphaToggle);
        maskAlphaToggle.setSelected(true);
        maskAlphaToggle.setText("Alpha Component");
        maskAlphaToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskAlphaToggleActionPerformed(evt);
            }
        });
        maskAlphaCtrlPanel.add(maskAlphaToggle, java.awt.BorderLayout.PAGE_START);

        maskAlphaColorCtrlPanel.setLayout(new java.awt.GridLayout(2, 0, 6, 7));

        maskAlphaButtons.add(maskAlphaBlackToggle);
        maskAlphaBlackToggle.setText("Black");
        maskAlphaBlackToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskAlphaToggleActionPerformed(evt);
            }
        });
        maskAlphaColorCtrlPanel.add(maskAlphaBlackToggle);

        maskAlphaButtons.add(maskAlphaWhiteToggle);
        maskAlphaWhiteToggle.setText("White");
        maskAlphaWhiteToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskAlphaToggleActionPerformed(evt);
            }
        });
        maskAlphaColorCtrlPanel.add(maskAlphaWhiteToggle);
        maskAlphaColorCtrlPanel.add(filler17);

        maskAlphaButtons.add(maskAlphaRedToggle);
        maskAlphaRedToggle.setText("Red");
        maskAlphaRedToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskAlphaToggleActionPerformed(evt);
            }
        });
        maskAlphaColorCtrlPanel.add(maskAlphaRedToggle);

        maskAlphaButtons.add(maskAlphaGreenToggle);
        maskAlphaGreenToggle.setText("Green");
        maskAlphaGreenToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskAlphaToggleActionPerformed(evt);
            }
        });
        maskAlphaColorCtrlPanel.add(maskAlphaGreenToggle);

        maskAlphaButtons.add(maskAlphaBlueToggle);
        maskAlphaBlueToggle.setText("Blue");
        maskAlphaBlueToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskAlphaToggleActionPerformed(evt);
            }
        });
        maskAlphaColorCtrlPanel.add(maskAlphaBlueToggle);

        maskAlphaCtrlPanel.add(maskAlphaColorCtrlPanel, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        maskImageCtrlPanel.add(maskAlphaCtrlPanel, gridBagConstraints);

        maskTabbedPane.addTab("Image", maskImageCtrlPanel);

        maskScaleLabel.setLabelFor(maskScaleSpinner);
        maskScaleLabel.setText("Mask Scale:");

        maskScaleSpinner.setModel(new javax.swing.SpinnerNumberModel(1.0d, 0.0001d, 10.0d, 0.01d));
        maskScaleSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(maskScaleSpinner, "0.##%"));
        maskScaleSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                maskScaleSpinnerStateChanged(evt);
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
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, maskDialogLayout.createSequentialGroup()
                        .addComponent(maskScaleLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(maskScaleSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                    .addComponent(maskScaleSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.5;
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        testCtrlPanel.add(showTestSpiralToggle, gridBagConstraints);

        testCtrlPanel2.setLayout(new java.awt.GridLayout(0, 4, 6, 7));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(7, 0, 0, 0);
        testCtrlPanel.add(testCtrlPanel2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 0.9;
        testCtrlPanel.add(filler16, gridBagConstraints);

        javax.swing.GroupLayout testDialogLayout = new javax.swing.GroupLayout(testDialog.getContentPane());
        testDialog.getContentPane().setLayout(testDialogLayout);
        testDialogLayout.setHorizontalGroup(
            testDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(testDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(testCtrlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        testDialogLayout.setVerticalGroup(
            testDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, testDialogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(testCtrlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(PROGRAM_NAME + " - Version "+ PROGRAM_VERSION);
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
        gridBagConstraints.gridy = 3;
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
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(balanceSpinner, gridBagConstraints);

        dirLabel.setLabelFor(dirCombo);
        dirLabel.setText("Direction:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(dirCombo, gridBagConstraints);

        angleLabel.setLabelFor(angleSpinner);
        angleLabel.setText("Rotation:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
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
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(angleSpinner, gridBagConstraints);

        spinLabel.setLabelFor(spinDirCombo);
        spinLabel.setText("Spin:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
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
        gridBagConstraints.gridy = 6;
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
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(spiralColorPanel, gridBagConstraints);

        maskEditButton.setText("Edit Message Mask");
        maskEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskEditButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        spiralCtrlPanel.add(maskEditButton, gridBagConstraints);

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

        imageSizePanel.setLayout(new javax.swing.BoxLayout(imageSizePanel, javax.swing.BoxLayout.X_AXIS));

        widthLabel.setLabelFor(widthSpinner);
        widthLabel.setText("Width:");
        imageSizePanel.add(widthLabel);
        imageSizePanel.add(filler1);

        widthSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 9999, 1));
        widthSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                widthSpinnerStateChanged(evt);
            }
        });
        imageSizePanel.add(widthSpinner);
        imageSizePanel.add(filler2);

        heightLabel.setLabelFor(heightSpinner);
        heightLabel.setText("Height:");
        imageSizePanel.add(heightLabel);
        imageSizePanel.add(filler3);

        heightSpinner.setModel(new javax.swing.SpinnerNumberModel(1, 1, 9999, 1));
        heightSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                heightSpinnerStateChanged(evt);
            }
        });
        imageSizePanel.add(heightSpinner);

        ctrlButtonPanel.setLayout(new java.awt.GridLayout(1, 0, 6, 0));

        saveButton.setText("Save");
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        ctrlButtonPanel.add(saveButton);

        resetButton.setText("Reset");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        ctrlButtonPanel.add(resetButton);

        alwaysScaleToggle.setText("Scale Preview");
        alwaysScaleToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alwaysScaleToggleActionPerformed(evt);
            }
        });

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

        delaySpinner.setModel(new javax.swing.SpinnerNumberModel(10, 10, 100, 10));
        delaySpinner.setToolTipText("This is the duration for each frame of animation, in milliseconds.");
        delaySpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                delaySpinnerStateChanged(evt);
            }
        });

        delayLabel.setLabelFor(delaySpinner);
        delayLabel.setText("Duration:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(framesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
                    .addComponent(previewPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(imageSizePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spiralCtrlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(progressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(alwaysScaleToggle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ctrlButtonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(delayLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(delaySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
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
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(spiralCtrlPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(imageSizePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(ctrlButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(alwaysScaleToggle))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(delaySpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(delayLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        File file = showSaveFileChooser(saveFC);
        if (file != null){
            fileWorker = new AnimationSaver(file);
            fileWorker.execute();
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void loadMaskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMaskButtonActionPerformed
        File file = showOpenFileChooser(maskFC);
        if (file != null){
            fileWorker = new ImageLoader(file);
            fileWorker.execute();
        }
    }//GEN-LAST:event_loadMaskButtonActionPerformed

    private void frameFirstButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frameFirstButtonActionPerformed
        frameSlider.setValue(0);
    }//GEN-LAST:event_frameFirstButtonActionPerformed

    private void framePrevButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_framePrevButtonActionPerformed
        frameSlider.setValue((SPIRAL_FRAME_COUNT+frameSlider.getValue()-1)%SPIRAL_FRAME_COUNT);
    }//GEN-LAST:event_framePrevButtonActionPerformed

    private void framePlayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_framePlayButtonActionPerformed
        updateFrameControls();
        updateControlsEnabled();
        frameTime = System.currentTimeMillis();
        frameTimeTotal = 0;
        frameTotal = 0;
        if (framePlayButton.isSelected()){
            animationTimer.restart();
        } else{
            animationTimer.stop();
        }
    }//GEN-LAST:event_framePlayButtonActionPerformed

    private void frameSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_frameSliderStateChanged
        updateFrameNavigation();
        try{
            previewLabel.repaint();
        } catch (NullPointerException ex){
            Logger.getLogger(SpiralGenerator.class.getName()).log(
                    Level.WARNING,"Null encountered in frameSliderStateChanged", 
                    ex);
        }
        updateFrameNumberDisplayed();
    }//GEN-LAST:event_frameSliderStateChanged

    private void frameStopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frameStopButtonActionPerformed
        framePlayButton.setSelected(false);
        updateFrameControls();
        updateControlsEnabled();
        animationTimer.stop();
        frameSlider.setValue(0);
    }//GEN-LAST:event_frameStopButtonActionPerformed

    private void frameNextButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frameNextButtonActionPerformed
        frameSlider.setValue((frameSlider.getValue()+1)%SPIRAL_FRAME_COUNT);
    }//GEN-LAST:event_frameNextButtonActionPerformed

    private void frameLastButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_frameLastButtonActionPerformed
        frameSlider.setValue(SPIRAL_FRAME_COUNT-1);
    }//GEN-LAST:event_frameLastButtonActionPerformed

    private void printTestButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printTestButtonActionPerformed
        System.out.println("Bounds: " + getBounds());
        System.out.println("Rotation: " + getFrameRotation(frameSlider.getValue()));
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
        SpiralPainter painter = getSpiralPainter();
        double value = (double) radiusSpinner.getValue();
        if (value != painter.getSpiralRadius())
            painter.setSpiralRadius(value);
    }//GEN-LAST:event_radiusSpinnerStateChanged

    private void baseSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_baseSpinnerStateChanged
        SpiralPainter temp = getSpiralPainter();
        if (temp instanceof LogarithmicSpiral){
            LogarithmicSpiral painter = (LogarithmicSpiral) temp;
            double value = (double) baseSpinner.getValue();
            if (value != painter.getBase())
                painter.setBase(value);
        }
    }//GEN-LAST:event_baseSpinnerStateChanged

    private void balanceSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_balanceSpinnerStateChanged
        SpiralPainter painter = getSpiralPainter();
        double value = (double) balanceSpinner.getValue();
        if (value != painter.getBalance())
            painter.setBalance(value);
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
        getSpiralPainter().setClockwise(dirCombo.getSelectedIndex() == 0);
    }//GEN-LAST:event_dirComboActionPerformed

    private void spinDirComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spinDirComboActionPerformed
        config.setSpinClockwise(isSpinClockwise());
        refreshPreview(false);
    }//GEN-LAST:event_spinDirComboActionPerformed

    private void angleSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_angleSpinnerStateChanged
        config.setSpiralRotation((double)angleSpinner.getValue());
        refreshPreview(false);
    }//GEN-LAST:event_angleSpinnerStateChanged

    private void fontButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontButtonActionPerformed
        FontDialog fontSelector = new FontDialog(this,"Select Font",true);
        config.loadMaskFontSelectorSize(fontSelector);
        fontSelector.setLocationRelativeTo(this);
        fontSelector.setSelectedFont(maskTextArea.getFont().deriveFont(Font.PLAIN));
        fontSelector.setVisible(true);
        if (!fontSelector.isCancelSelected()){
            Font font = fontSelector.getSelectedFont().deriveFont(getFontStyle());
            maskTextArea.setFont(font);
            config.setMaskFont(font);
            refreshPreview(true);
        }
        fontDim = fontSelector.getSize(fontDim);
        config.setMaskFontSelectorSize(fontDim);
    }//GEN-LAST:event_fontButtonActionPerformed

    private void maskDialogComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_maskDialogComponentResized
        config.setComponentSize(maskDialog);
    }//GEN-LAST:event_maskDialogComponentResized

    private void styleToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_styleToggleActionPerformed
        int style = getFontStyle();
        config.setMaskFontStyle(style);
        maskTextArea.setFont(maskTextArea.getFont().deriveFont(style));
        refreshPreview(true);
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
        refreshPreview(false);
    }//GEN-LAST:event_maskTabbedPaneStateChanged

    private void maskScaleSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_maskScaleSpinnerStateChanged
        config.setMaskScale(getMaskScale());
        maskPreviewLabel.repaint();
        refreshPreview(false);
    }//GEN-LAST:event_maskScaleSpinnerStateChanged

    private void widthSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_widthSpinnerStateChanged
        config.setImageWidth(getImageWidth());
        refreshPreview(true);
    }//GEN-LAST:event_widthSpinnerStateChanged

    private void heightSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_heightSpinnerStateChanged
        config.setImageHeight(getImageHeight());
        refreshPreview(true);
    }//GEN-LAST:event_heightSpinnerStateChanged

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        overlayImage = null;
        overlayMask.reset();
        maskTextArea.setText("");
        maskTabbedPane.setSelectedIndex(0);
        maskAlphaToggle.setSelected(true);
        config.setMaskAlphaIndex(maskAlphaButtons);
        maskScaleSpinner.setValue(1.0);
        for (int i = 0; i < colorIcons.length; i++){
            colorIcons[i].setColor(DEFAULT_SPIRAL_COLORS[i]);
            config.setSpiralColor(i, null);
            colorButtons.get(colorIcons[i]).repaint();
        }
        widthSpinner.setValue(DEFAULT_SPIRAL_WIDTH);
        heightSpinner.setValue(DEFAULT_SPIRAL_HEIGHT);
        spinDirCombo.setSelectedIndex(0);
        for (SpiralPainter painter : spiralPainters){
            painter.reset();
        }
        loadSpiralPainter();
        angleSpinner.setValue(0.0);
    }//GEN-LAST:event_resetButtonActionPerformed

    private void imgMaskAntialiasingToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imgMaskAntialiasingToggleActionPerformed
        config.setMaskImageAntialiased(imgMaskAntialiasingToggle.isSelected());
        maskPreviewLabel.repaint();
        refreshPreview(false);
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
        previewLabel.setIcon((showTestSpiralToggle.isSelected()) ? new TestSpiralIcon() : spiralIcon);
    }//GEN-LAST:event_showTestSpiralToggleActionPerformed

    private void showTestDialogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showTestDialogButtonActionPerformed
        testDialog.setVisible(true);
    }//GEN-LAST:event_showTestDialogButtonActionPerformed

    private void testSpiralImageSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_testSpiralImageSpinnerStateChanged
        if (showTestSpiralToggle.isSelected())
            previewLabel.repaint();
        config.setDebugTestImage((int)testSpiralImageSpinner.getValue());
    }//GEN-LAST:event_testSpiralImageSpinnerStateChanged

    private void testRotateSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_testRotateSpinnerStateChanged
        if (showTestSpiralToggle.isSelected())
            previewLabel.repaint();
        config.setDebugTestRotation((double)testRotateSpinner.getValue());
    }//GEN-LAST:event_testRotateSpinnerStateChanged

    private void testScaleSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_testScaleSpinnerStateChanged
        if (showTestSpiralToggle.isSelected())
            previewLabel.repaint();
        config.setDebugTestScale((double)testScaleSpinner.getValue());
    }//GEN-LAST:event_testScaleSpinnerStateChanged

    private void spiralTypeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spiralTypeComboActionPerformed
        config.setSpiralType(spiralTypeCombo.getSelectedIndex());
        loadSpiralPainter();
        refreshPreview(false);
    }//GEN-LAST:event_spiralTypeComboActionPerformed

    private void delaySpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_delaySpinnerStateChanged
        int value = ((Integer) delaySpinner.getValue());
        if (value % 10 != 0){
            UIManager.getLookAndFeel().provideErrorFeedback(delaySpinner);
            delaySpinner.setValue(value - (value % 10));
            return;
        }
        config.setFrameDuration(value);
        animationTimer.setDelay(value);
        animationTimer.setInitialDelay(value);
    }//GEN-LAST:event_delaySpinnerStateChanged

    private void maskAlphaToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskAlphaToggleActionPerformed
        config.setMaskAlphaIndex(maskAlphaButtons);
        refreshPreview(true);
    }//GEN-LAST:event_maskAlphaToggleActionPerformed
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
     * This returns whether the mask for the overlay message is created using an 
     * image.
     * @return Whether the message mask is created using an image.
     */
    private boolean isOverlayMaskImage(){
        return maskTabbedPane != null && maskTabbedPane.getSelectedIndex() == 1;
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
     * @return The rotation to apply to the spiral for the frame.
     */
    private double getFrameRotation(int frameIndex){
            // Get the angle to use for the rotation
        double angle = SPIRAL_FRAME_ROTATION*frameIndex;
            // If the spin direction is the same as the spiral's direction
        if (isSpinClockwise() == getSpiralPainter().isClockwise())
                // Invert the angle, so as to make it spin in the right direction
            angle = SpiralPainter.FULL_CIRCLE_DEGREES - angle;
            // Add the angle spinner's value and bound it by 360
        return (angle + (double) angleSpinner.getValue()) % 
                SpiralPainter.FULL_CIRCLE_DEGREES;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | 
                IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SpiralGenerator.class.getName()).
                    log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new SpiralGenerator(DebugCapable.checkForDebugArgument(args)).setVisible(true);
        });
    }
    
    private void refreshMaskText(){
        config.setMaskText(maskTextArea.getText());
        refreshPreview(true);
    }
    
    private void refreshPreview(boolean maskChanged){
        if (maskChanged){
            overlayMask.textMask = null;
            if (!isOverlayMaskImage())
                maskPreviewLabel.repaint();
        }
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
                frameSlider.getValue() > frameSlider.getMinimum());
        frameNextButton.setEnabled(frameSlider.isEnabled());
        frameLastButton.setEnabled(frameNextButton.isEnabled() && 
                frameSlider.getValue() < frameSlider.getMaximum());
    }
    /**
     * 
     * @param enabled 
     */
    private void setValueControlsEnabled(boolean enabled){
        for (Component comp : spiralCompLabels.keySet())
            comp.setEnabled(enabled);
        spinDirCombo.setEnabled(enabled);
        maskTextArea.setEnabled(enabled);
        fontButton.setEnabled(enabled);
        lineSpacingSpinner.setEnabled(enabled);
        boldToggle.setEnabled(enabled);
        italicToggle.setEnabled(enabled);
        fontAntialiasingToggle.setEnabled(enabled);
        for (JButton button : colorButtons.values()){
            button.setEnabled(enabled);
        }
        widthSpinner.setEnabled(enabled);
        heightSpinner.setEnabled(enabled);
        maskScaleSpinner.setEnabled(enabled);
        imgMaskAntialiasingToggle.setEnabled(enabled);
        resetButton.setEnabled(enabled);
        spiralTypeCombo.setEnabled(enabled);
        delaySpinner.setEnabled(enabled);
    }
    /**
     * 
     */
    private void updateControlsEnabled(){
        boolean enabled = frameSlider.isEnabled();
        saveButton.setEnabled(enabled);
        loadMaskButton.setEnabled(enabled);
        maskEditButton.setEnabled(enabled);
        maskTabbedPane.setEnabled(enabled);
        setValueControlsEnabled(enabled);
    }
    /**
     * 
     */
    private void updateFrameNumberDisplayed(){
        String text = String.format("Frame: %d / %d", frameSlider.getValue()+1,
                SPIRAL_FRAME_COUNT);
        frameNumberLabel.setText(text);
    }
    /**
     * 
     * @param evt 
     */
    private void progressAnimation(java.awt.event.ActionEvent evt){
        long temp = System.currentTimeMillis();
        long diff = temp - frameTime;
        frameTimeTotal += diff;
        frameTotal++;
        frameTime = temp;
        if (printFPSToggle.isSelected()){
            System.out.printf("Last Frame: %5d ms, Avg: %10.5f, Target: %5d%n", 
                    diff, frameTimeTotal/((double)frameTotal), animationTimer.getDelay());
        }
        try{
            frameSlider.setValue((frameSlider.getValue()+1)%SPIRAL_FRAME_COUNT);
        } catch (NullPointerException ex){
            System.out.println("Null? "+evt);
        }
    }
    /**
     * 
     * @param index 
     */
    private void setColor(int index){
        int option = colorSelector.showDialog(this, colorIcons[index].getColor());
        if(option == JColorSelector.ACCEPT_OPTION || option == JColorSelector.CLEAR_COLOR_OPTION){
            Color color = colorSelector.getColor();
            config.setSpiralColor(index, color);
            if (color == null)
                color = DEFAULT_SPIRAL_COLORS[index];
            colorIcons[index].setColor(color);
            refreshPreview(false);
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
     * This is a scratch dimension object used to store the dimensions of the 
     * font chooser dialog.
     */
    private Dimension fontDim = null;
    /**
     * This is the icon used to display the sprial.
     */
    private Icon spiralIcon;
    /**
     * This is an array containing the test images to display while testing.
     */
    private ArrayList<BufferedImage> testImages = null;
    /**
     * These are the painters used to paint the spiral.
     */
    private SpiralPainter[] spiralPainters;
    /**
     * 
     */
    private SpiralPainter testSpiralPainter;
    /**
     * 
     */
    private Map<Component, JLabel> spiralCompLabels;
    /**
     * This is the image used to create the mask for the overlay when a loaded 
     * image is used for the mask. This is the raw image, and is null when no 
     * image has been loaded for the mask.
     */
    private BufferedImage overlayImage = null;
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
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox alwaysScaleToggle;
    private javax.swing.JLabel angleLabel;
    private javax.swing.JSpinner angleSpinner;
    private javax.swing.JLabel balanceLabel;
    private javax.swing.JSpinner balanceSpinner;
    private javax.swing.JLabel baseLabel;
    private javax.swing.JSpinner baseSpinner;
    private javax.swing.JCheckBox boldToggle;
    private javax.swing.JButton color1Button;
    private javax.swing.JButton color2Button;
    private javax.swing.JButton color3Button;
    private javax.swing.JButton color4Button;
    private components.JColorSelector colorSelector;
    private javax.swing.JPanel ctrlButtonPanel;
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
    private javax.swing.JPanel imageSizePanel;
    private javax.swing.JCheckBox imgMaskAntialiasingToggle;
    private javax.swing.JCheckBoxMenuItem inputEnableToggle;
    private javax.swing.JCheckBox italicToggle;
    private javax.swing.JLabel lineSpacingLabel;
    private javax.swing.JSpinner lineSpacingSpinner;
    private javax.swing.JButton loadMaskButton;
    private javax.swing.JRadioButton maskAlphaBlackToggle;
    private javax.swing.JRadioButton maskAlphaBlueToggle;
    private javax.swing.ButtonGroup maskAlphaButtons;
    private javax.swing.JPanel maskAlphaColorCtrlPanel;
    private javax.swing.JPanel maskAlphaCtrlPanel;
    private javax.swing.JRadioButton maskAlphaGreenToggle;
    private javax.swing.JRadioButton maskAlphaRedToggle;
    private javax.swing.JRadioButton maskAlphaToggle;
    private javax.swing.JRadioButton maskAlphaWhiteToggle;
    private javax.swing.JDialog maskDialog;
    private javax.swing.JButton maskEditButton;
    private javax.swing.JFileChooser maskFC;
    private components.JFileDisplayPanel maskFCPreview;
    private javax.swing.JPanel maskImageCtrlPanel;
    private javax.swing.JPopupMenu maskPopup;
    private components.JThumbnailLabel maskPreviewLabel;
    private javax.swing.JLabel maskScaleLabel;
    private javax.swing.JSpinner maskScaleSpinner;
    private javax.swing.JTabbedPane maskTabbedPane;
    private javax.swing.JTextArea maskTextArea;
    private javax.swing.JScrollPane maskTextScrollPane;
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
    private javax.swing.JButton saveButton;
    private javax.swing.JFileChooser saveFC;
    private components.JFileDisplayPanel saveFCPreview;
    private javax.swing.JMenuItem showTestDialogButton;
    private javax.swing.JCheckBox showTestSpiralToggle;
    private javax.swing.JComboBox<String> spinDirCombo;
    private javax.swing.JLabel spinLabel;
    private javax.swing.JPanel spiralColorPanel;
    private javax.swing.JPanel spiralCtrlPanel;
    private javax.swing.JComboBox<SpiralPainter> spiralTypeCombo;
    private javax.swing.JLabel spiralTypeLabel;
    private javax.swing.JDialog testDialog;
    private javax.swing.JSpinner testRotateSpinner;
    private javax.swing.JSpinner testScaleSpinner;
    private javax.swing.JSpinner testSpiralImageSpinner;
    private javax.swing.JPanel textMaskCtrlPanel;
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
        updateFrameControls();
        updateControlsEnabled();
    }
    
    private void scaleMaintainLocation(Graphics2D g, double x, double y, 
            double scaleX, double scaleY){
            // Translate the graphics context to the given point
        g.translate(x, y);
            // Scale the graphics context
        g.scale(scaleX, scaleY);
            // Translate the graphics context back to where would be before 
            // scaling it
        g.translate(-x, -y);
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
    
    private void maskImage(BufferedImage img, BufferedImage mask){
        int width = img.getWidth();
        int height = img.getHeight();
        if (mask.getWidth() != width && mask.getHeight() != height){
            mask = Thumbnailator.createThumbnail(mask, width, height);
        }
        int[] imgData = new int[width];
        int[] maskData = new int[width];
        
        for (int y = 0; y < height; y++){
            img.getRGB(0, y, width, 1, imgData, 0, 1);
            mask.getRGB(0, y, width, 1, maskData, 0, 1);
            
            for (int x = 0; x < width; x++){
                //Normalize (0 - 1)
                float maskAlpha = (maskData[x] & 0x000000FF)/ 255f;
                float imageAlpha = ((imgData[x] >> 24) & 0x000000FF) / 255f;

                //Image without alpha channel
                int rgb = imgData[x] & 0x00FFFFFF;

                //Multiplied alpha
                int alpha = ((int) ((maskAlpha * imageAlpha) * 255)) << 24;

                //Add alpha to image
                imgData[x] = rgb | alpha;
            }
            img.setRGB(0, y, width, 1, imgData, 0, 1);
        }
    }
    
    private void maskImage(Graphics2D g, Image mask){
        g.setComposite(AlphaComposite.DstIn);
        g.drawImage(mask, 0, 0, null);
    }
    
    private boolean hasNoColor(Color color){
        return color == null || color.getAlpha() == 0;
    }
    
    private boolean hasNoColor(Color color1, Color color2){
        return hasNoColor(color1) && hasNoColor(color2);
    }
    
    private void paintTextMask(Graphics2D g, int width, int height, String text, 
            CenteredTextPainter painter){
            // Set the graphics context font to the mask font
        g.setFont(maskTextArea.getFont());
            // Paint the mask's text to the graphics context
        painter.paint(g, text, width, height);
    }
    
    private BufferedImage getImageMaskImage(int width, int height, 
            BufferedImage image, BufferedImage mask){
            // If the source image is null
        if (image == null)
            return null;
            // If the mask version of the overlay image is null
        if (mask == null){
            mask = image;
        }
            // If the mask version of the overlay image doesn't match the 
            // size of the area being rendered
            // TODO: Work on implementing user control over the overlay 
            // image's size and stuff
        if (mask.getWidth() != width || mask.getHeight() != height)
                // Scale the overlay image
            return Thumbnailator.createThumbnail(image,width,height);
        return mask;
    }
    
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
        mask = new BufferedImage(width, height, 
                BufferedImage.TYPE_INT_ARGB);
            // Create the graphics context for the image
        Graphics2D g = mask.createGraphics();
            // Paint the mask's text
        paintTextMask(g,width,height,text,painter);
            // Dispose of the graphics context
        g.dispose();
        return mask;
    }
    
    private boolean getOverlayAntialiased(){
            // If the mask is an image, use whether the image  antialiasing 
            // toggle is selected.
        if (isOverlayMaskImage())
            return imgMaskAntialiasingToggle.isSelected();
        return overlayMask.textPainter.isAntialiasingEnabled();
    }
    
    private void paintOverlay(Graphics2D g, int frameIndex, Color color1, 
            Color color2, int width, int height, BufferedImage mask, 
            SpiralPainter spiralPainter, CenteredTextPainter painter){
            // If the message should be a solid color
        boolean solidColor = Objects.equals(color1, color2);
            // This gets the scale for the mask
        double scale = getMaskScale();
            // If the overlay is a solid color and using the mask
        if (solidColor && !isOverlayMaskImage()){
                // Get the text for the mask 
            String text = maskTextArea.getText();
                // If the text is null or blank
            if (text == null || text.isBlank())
                return;
                // Create a copy of the given graphics context
            g = (Graphics2D) g.create();
                // Set the color to the first color
            g.setColor(color1);
                // Scale the graphics, maintaining the center
            scaleMaintainLocation(g,width/2.0,height/2.0,scale,scale);
               // Paint the mask's text
            paintTextMask(g,width,height,text,painter);
                // Dispose of the graphics context
            g.dispose();
            return;
        }
            // If the mask is somehow null at this point
        if (mask == null)
            return;
            // Create an image to render the overlay to
        BufferedImage overlay = new BufferedImage(width, height, 
                BufferedImage.TYPE_INT_ARGB);
            // Create a graphics context for the image
        Graphics2D imgG = overlay.createGraphics();
            // Configure the image graphics
        imgG = configureGraphics(imgG);
            // If the overlay is being rendered in a solid color
        if (solidColor){
                // Fill the image with the first color
            imgG.setColor(color1);
            imgG.fillRect(0, 0, width, height);
        } else {
                // Paint a spiral with the two colors
            paintSpiral(imgG,frameIndex,color1,color2,width,height,spiralPainter);
        }   // Scale the image, maintaining its center
        scaleMaintainLocation(imgG,width/2.0,height/2.0,scale,scale);
            // Enable or disable the antialiasing, depending on whether the mask 
            // should be antialiased
        imgG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                (getOverlayAntialiased())? RenderingHints.VALUE_ANTIALIAS_ON : 
                        RenderingHints.VALUE_ANTIALIAS_OFF);
            // Mask the overlay image pixels with the mask image
        maskImage(imgG,mask);
            // Dispose of the image graphics
        imgG.dispose();
            // Create a copy of the given graphics context and configure it
        g = configureGraphics((Graphics2D) g.create());
            // Draw the overlay image
        g.drawImage(overlay, 0, 0, null);
            // Dispose of the copy of the graphics context
        g.dispose();
    }
    
    private void paintOverlay(Graphics2D g, int frameIndex, Color color1, 
            Color color2, int width, int height,SpiralPainter spiralPainter, 
            OverlayMask mask){
            // If the width or height are less than or equal to zero (nothing 
            // would be drawn)
        if (width <= 0 || height <= 0)
            return;
            // Determine if the overlay is being rendered in a solid color
        boolean solidColor = frameIndex < 0 || Objects.equals(color1, color2);
            // If the overlay is a solid color
        if (solidColor){
                // If the first color is non-existant
            if (hasNoColor(color1))
                return;
        }   // If both colors are non-existant
        else if (hasNoColor(color1,color2))
            return;
            // Paint the overlay
        paintOverlay(g,frameIndex,color1,(solidColor)?color1:color2,width,
                height,mask.getMask(width, height),spiralPainter,
                mask.textPainter);
    }
    
    private void paintSpiral(Graphics2D g, int frameIndex, Color color1, Color color2,
            int width, int height, SpiralPainter spiralPainter){
        if (width <= 0 || height <= 0)
            return;
        if (hasNoColor(color1,color2))
            return;
        else if (Objects.equals(color1, color2)){
            g.setColor(color1);
            g.fillRect(0, 0, width, height);
            return;
        }
        double angle = getFrameRotation(frameIndex);
        if (color2 != null && color2.getAlpha() != 0){
            if (color1 != null && color1.getAlpha() != 0){
                g.setColor(color1);
                g.fillRect(0, 0, width, height);
            }
            g.setColor(color2);
            spiralPainter.paint(g, angle, width, height);
        } else {
            BufferedImage img = new BufferedImage(width, height, 
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D imgG = img.createGraphics();
            imgG.setColor(color1);
            imgG.fillRect(0, 0, width, height);
            imgG.setComposite(AlphaComposite.DstOut);
            spiralPainter.paint(imgG, angle, width, height);
            imgG.dispose();
            g.drawImage(img, 0, 0, null);
        }
    }
    
    private void paintSpiralDesign(Graphics2D g, int frameIndex, int width, 
            int height, Color color1, SpiralPainter spiralPainter,
            OverlayMask mask){
        paintSpiral(g,frameIndex,color1,colorIcons[1].getColor(),width,height,
                spiralPainter);
        paintOverlay(g,frameIndex,
                colorIcons[2].getColor(),colorIcons[3].getColor(),width,height,
                spiralPainter,mask);
    }
    
    private void paintSpiralDesign(Graphics2D g, int frameIndex, int width, 
            int height, SpiralPainter spiralPainter,OverlayMask mask){
        paintSpiralDesign(g,frameIndex,width,height,colorIcons[0].getColor(),
                spiralPainter,mask);
    }
    
    private class SpiralIcon implements Icon2D{
        @Override
        public void paintIcon2D(Component c, Graphics2D g, int x, int y) {
            g.translate(x, y);
            paintSpiralDesign(g,frameSlider.getValue(), getIconWidth(), 
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
    
    private class TestSpiralIcon implements Icon2D{
        @Override
        public void paintIcon2D(Component c, Graphics2D g, int x, int y) {
            g.translate(x, y);
            int index = (int) testSpiralImageSpinner.getValue();
                // Get the width of the icon
            int width = getIconWidth();
                // Get the height of the icon
            int height = getIconHeight();
            double scale = (double)testScaleSpinner.getValue();
            if (scale == 0)
                scale = 1;
            scale = 1.0/scale;
            scaleMaintainLocation(g,width/2.0,height/2.0,scale,scale);
            if (index >= 0 && index < testImages.size()){
                BufferedImage img = testImages.get(index);
                if (img.getWidth() != width || img.getHeight() != height)
                    img = Thumbnailator.createThumbnail(img, width, height);
                g.drawImage(img, 0, 0, null);
                g.setColor(new Color(0x8000FF00,true));
            } else {
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, width, height);
                g.setColor(Color.BLACK);
            }
            testSpiralPainter.paint(g, (double)testRotateSpinner.getValue(), width, height);
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
    
    private class MaskPreviewIcon implements Icon2D{
        @Override
        public void paintIcon2D(Component c, Graphics2D g, int x, int y) {
            g.translate(x, y);
            g.setColor(Color.BLACK);
                // Get the width of the icon
            int width = getIconWidth();
                // Get the height of the icon
            int height = getIconHeight();
            g.fillRect(0, 0, width, height);
            paintOverlay(g,-1,Color.WHITE,Color.WHITE, width, height,
                    getSpiralPainter(),overlayMask);
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
            SpiralPainter painter = null;
            if (evt.getSource() instanceof SpiralPainter)
                painter = (SpiralPainter) evt.getSource();
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
                case(GEGLSpiralPainter.SPIRAL_RADIUS_PROPERTY_CHANGED):
                case(LogarithmicSpiralPainter.BASE_PROPERTY_CHANGED):
                case(GEGLSpiralPainter.THICKNESS_PROPERTY_CHANGED):
                case(SpiralPainter.CLOCKWISE_PROPERTY_CHANGED):
                    if (painter != null)
                        config.setSpiralData(painter);
            }
            refreshPreview(maskChanged);
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
    
    private class OverlayMask{
        /**
         * This is the image used as a mask for the overlay when text is being 
         * used as a mask. When this is null, then the mask will be generated 
         * the next time it is used.
         */
        public BufferedImage textMask = null;
        /**
         * This is the image used as as a mask for the overlay when a loaded 
         * image is used for the mask. This is null when the mask needs to be 
         * recreated from {@code overlayImage}, either due to another image 
         * being loaded in or the resulting image's size being changed.
         */
        public BufferedImage imgMask = null;
        /**
         * This is the painter used to paint the text used as the mask for the 
         * message when text is being used for the mask.
         */
        public CenteredTextPainter textPainter;
        
        protected OverlayMask(){
            textPainter = new CenteredTextPainter();
        }
        
        protected OverlayMask(CenteredTextPainter painter){
            textPainter = new CenteredTextPainter(painter);
        }
        
        protected OverlayMask(OverlayMask mask){
            this(mask.textPainter);
        }
        
        public void reset(){
            textMask = imgMask = null;
        }
        
        public BufferedImage getMask(int width, int height){
                // This will get the mask to return
            BufferedImage mask;
                // If a loaded image is being used as the overlay mask
            if (isOverlayMaskImage())
                    // Use the mask version of the overlay image as the mask
                mask = imgMask = getImageMaskImage(width,height,overlayImage,
                        imgMask);
            else 
                    // Use the text mask, creating it if it needs to be made
                mask = textMask = getTextMaskImage(width,height,
                        maskTextArea.getText(),textMask,textPainter);
            return mask;
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
                    System.out.println("Error: " + ex);
                    success = false;
                    useWaitCursor(false);
                        // Show the failure prompt and get if the user wants to 
                    retry = showFailurePrompt(file, ex);    // try again
                }
            }   // While the file failed to be processed and the user wants to 
            while(!success && retry);   // try again
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
                Color bg = colorIcons[0].getColor();
                    // Get if the background has transparency
                boolean transparency = bg.getAlpha() < 255;
                    // Get the background without an alpha
                bg = new Color(bg.getRGB());
                    // Set the background for the gif
                encoder.setBackground(bg);
                    // If the background is transparent
                if (transparency)
                    encoder.setTransparent(bg);
                    // A for loop to go through and add all the frames to the 
                    // gif
                for (int i = 0; i < SPIRAL_FRAME_COUNT; i++){
                        // This gets the current frame
                    BufferedImage frame;
                        // If the frame is in the frames list
                    if (i < frames.size())
                        frame = frames.get(i);
                    else {
                            // Create the frame
                        frame = createSpiralFrame(i,width,height,painter,mask);
                        frames.add(frame);
                        progressBar.setValue(progressBar.getValue()+1);
                    }   // Set the preview to the current frame
                    previewLabel.setImage(frame);
                        // Add the frame to the gif
                    encoder.addFrame(frame);
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
        private BufferedImage img = null;
        /**
         * 
         * @param file 
         */
        ImageLoader(File file) {
            super(file);
        }
        @Override
        protected boolean loadFile(File file) throws IOException {
            img = ImageIO.read(file);
            if (img != null && img.getWidth() != img.getHeight()){
                BufferedImage temp = img;
                int size = Math.max(img.getWidth(), img.getHeight());
                img = new BufferedImage(size, size,BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = img.createGraphics();
                g.drawImage(temp, 
                        Math.max(0, Math.floorDiv(size-temp.getWidth(), 2)), 
                        Math.max(0, Math.floorDiv(size-temp.getHeight(), 2)), 
                        null);
                g.dispose();
            }
            return img != null;
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
            if (success){
                overlayMask.imgMask = null;
                overlayImage = img;
            }
            maskPreviewLabel.repaint();
            refreshPreview(false);
            super.done();
        }
    }
}
