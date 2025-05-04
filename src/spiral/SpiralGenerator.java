/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package spiral;

import anim.*;
import com.madgag.gif.fmsware.AnimatedGifEncoder;
import components.JColorSelector;
import components.text.CompoundUndoManager;
import components.text.action.commands.TextComponentCommands;
import components.text.action.commands.UndoManagerCommands;
import files.extensions.ImageExtensions;
import icons.Icon2D;
import icons.box.ColorBoxIcon;
import icons.box.DisabledBoxIcon;
import io.github.dheid.fontchooser.FontDialog;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.coobird.thumbnailator.Thumbnailator;

/**
 *
 * @author Mosblinker
 */
public class SpiralGenerator extends javax.swing.JFrame {
    /**
     * This is an array containing the widths and heights for the icon images 
     * for this program. 
     */
    private static final int[] ICON_SIZES = {16, 24, 32, 48, 64, 96, 128, 256, 512};
    
    private static final String ICON_FILE_IMAGE = "/images/icon.png";
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
     * This is the amount that the spiral will rotate per frame.
     */
    private static final double SPIRAL_FRAME_ROTATION = 
            SpiralPainter.MAXIMUM_ANGLE / SPIRAL_FRAME_COUNT;
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
     */
    public SpiralGenerator() {
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
            colorIcons[i] = new ColorBoxIcon(16,16,config.getSpiralColor(i, DEFAULT_SPIRAL_COLORS[i]));
        }
        
        spiralPainter.setRadius(config.getSpiralRadius(spiralPainter.getRadius()));
        spiralPainter.setBase(config.getSpiralBase(spiralPainter.getBase()));
        spiralPainter.setBalance(config.getSpiralBalance(spiralPainter.getBalance()));
        spiralPainter.setClockwise(config.isSpiralClockwise(spiralPainter.isClockwise()));
        
        maskPainter.setAntialiasingEnabled(config.isMaskTextAntialiased(maskPainter.isAntialiasingEnabled()));
        maskPainter.setLineSpacing(config.getMaskLineSpacing(maskPainter.getLineSpacing()));
        
        colorButtons = new HashMap<>();
        colorIndexes = new HashMap<>();
        
        spiralIcon = new SpiralIcon();
        initComponents();
        
        BufferedImage iconImg = null;
        try {
            iconImg = ImageIO.read(this.getClass().getResource(ICON_FILE_IMAGE));
        } catch (IOException ex) {
            Logger.getLogger(SpiralGenerator.class.getName()).log(Level.WARNING, null, ex);
        }
        SpiralPainter iconPainter = new SpiralPainter();
        ArrayList<BufferedImage> iconImages = new ArrayList<>();
        for (int size : ICON_SIZES){
            if (iconImg != null){
                iconImages.add(Thumbnailator.createThumbnail(iconImg, size, size));
            } else {
                iconPainter.setRadius(size/3.0);
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
        
        config.getProgramBounds(SpiralGenerator.this);
        radiusSpinner.setValue(spiralPainter.getRadius());
        baseSpinner.setValue(spiralPainter.getBase());
        balanceSpinner.setValue(spiralPainter.getBalance());
        dirCombo.setSelectedIndex((spiralPainter.isClockwise())?0:1);
        angleSpinner.setValue(config.getSpiralAngle());
        spinDirCombo.setSelectedIndex((config.isSpinClockwise())?0:1);
        fontAntialiasingToggle.setSelected(maskPainter.isAntialiasingEnabled());
        lineSpacingSpinner.setValue(maskPainter.getLineSpacing());
        maskScaleSpinner.setValue(config.getMaskScale());
        
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
        
        spiralPainter.addPropertyChangeListener(handler);
        maskPainter.addPropertyChangeListener(handler);
        maskTextArea.getDocument().addDocumentListener(handler);
    }
    
    private BufferedImage createSpiralFrame(int frameIndex, int width, int height){
        BufferedImage img = new BufferedImage(width, height, 
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        paintSpiralDesign(g,frameIndex,width,height);
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
        loadMaskButton = new javax.swing.JButton();
        maskScaleLabel = new javax.swing.JLabel();
        maskScaleSpinner = new javax.swing.JSpinner();
        maskPopup = new javax.swing.JPopupMenu();
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
        spiralColorPanel = new javax.swing.JPanel();
        color1Button = new javax.swing.JButton();
        color2Button = new javax.swing.JButton();
        color3Button = new javax.swing.JButton();
        color4Button = new javax.swing.JButton();
        spinLabel = new javax.swing.JLabel();
        spinDirCombo = new javax.swing.JComboBox<>();
        maskEditButton = new javax.swing.JButton();
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
                .addComponent(maskTextScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 156, Short.MAX_VALUE)
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

        loadMaskButton.setText("Load Mask");
        loadMaskButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadMaskButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(104, 136, 115, 146);
        maskImageCtrlPanel.add(loadMaskButton, gridBagConstraints);

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
                .addComponent(maskTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 244, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(maskDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(maskScaleLabel)
                    .addComponent(maskScaleSpinner, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Hypno Gif Generator");
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

        previewSpiralPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Spiral Preview"));
        previewSpiralPanel.setLayout(new java.awt.BorderLayout());

        previewLabel.setComponentPopupMenu(debugPopup);
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
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 6);
        spiralCtrlPanel.add(radiusLabel, gridBagConstraints);

        radiusSpinner.setModel(new javax.swing.SpinnerNumberModel(1.0d, 1.0d, 100000.0d, 1.0d));
        radiusSpinner.setEditor(new javax.swing.JSpinner.NumberEditor(radiusSpinner, "0.00######"));
        radiusSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                radiusSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(radiusSpinner, gridBagConstraints);

        baseLabel.setLabelFor(baseSpinner);
        baseLabel.setText("Base:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(baseSpinner, gridBagConstraints);

        balanceLabel.setLabelFor(balanceSpinner);
        balanceLabel.setText("Balance:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
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
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(balanceSpinner, gridBagConstraints);

        dirLabel.setLabelFor(dirCombo);
        dirLabel.setText("Direction:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
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
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(dirCombo, gridBagConstraints);

        angleLabel.setLabelFor(angleSpinner);
        angleLabel.setText("Rotation:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
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
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(angleSpinner, gridBagConstraints);

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
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(spiralColorPanel, gridBagConstraints);

        spinLabel.setLabelFor(spinDirCombo);
        spinLabel.setText("Spin:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
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
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 7, 0);
        spiralCtrlPanel.add(spinDirCombo, gridBagConstraints);

        maskEditButton.setText("Edit Message Mask");
        maskEditButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maskEditButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        spiralCtrlPanel.add(maskEditButton, gridBagConstraints);

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(framesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 413, Short.MAX_VALUE)
                    .addComponent(previewPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(imageSizePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(spiralCtrlPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(alwaysScaleToggle)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ctrlButtonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(alwaysScaleToggle, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(ctrlButtonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
        File file = showSaveFileChooser(saveFC);
        if (file == null)
            return;
        try(FileOutputStream fileOut = new FileOutputStream(file);
                BufferedOutputStream buffOut = new BufferedOutputStream(fileOut)){
            AnimatedGifEncoder encoder = new AnimatedGifEncoder();
            encoder.start(buffOut);
            encoder.setRepeat(0);
            encoder.setDelay(SPIRAL_FRAME_DURATION);
            encoder.setSize(getImageWidth(), getImageHeight());
            Color bg = colorIcons[0].getColor();
            boolean transparency = bg.getAlpha() < 255;
            bg = new Color(bg.getRGB());
            encoder.setBackground(bg);
            if (transparency)
                encoder.setTransparent(bg);
            for (int i = 0; i < SPIRAL_FRAME_COUNT; i++){
                encoder.addFrame(createSpiralFrame(i,getImageWidth(), getImageHeight()));
            }
            encoder.finish();
        } catch (IOException ex){
            System.out.println("Error: "+ ex);
        }
    }//GEN-LAST:event_saveButtonActionPerformed

    private void loadMaskButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadMaskButtonActionPerformed
        File file = showOpenFileChooser(maskFC);
        if (file != null){
            overlayImageMask = null;
            try{
                overlayImage = ImageIO.read(file);
                if (overlayImage.getWidth() != overlayImage.getHeight()){
                    BufferedImage img = overlayImage;
                    int size = Math.max(img.getWidth(), img.getHeight());
                    overlayImage = new BufferedImage(size, size, 
                            BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g = overlayImage.createGraphics();
                    g.drawImage(img, 
                            Math.max(0, Math.floorDiv(size-img.getWidth(), 2)), 
                            Math.max(0, Math.floorDiv(size-img.getHeight(), 2)), 
                            null);
                    g.dispose();
                }
            } catch (IOException ex){
                System.out.println("Error: "+ ex);
                overlayImage = null;
            }
            maskPreviewLabel.repaint();
            refreshPreview(false);
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
        try{
            updateFrameNavigation();
        } catch (NullPointerException ex){
            System.out.println("Null? Nav "+evt);
        }
        try{
            previewLabel.repaint();
        } catch (NullPointerException ex){
            System.out.println("Null? Repaint "+evt);
        }
        try{
            updateFrameNumberDisplayed();
        } catch (NullPointerException ex){
            System.out.println("Null? Num "+evt);
        }
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
        // TODO add your handling code here:
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
        double value = (double) radiusSpinner.getValue();
        if (value != spiralPainter.getRadius())
            spiralPainter.setRadius(value);
    }//GEN-LAST:event_radiusSpinnerStateChanged

    private void baseSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_baseSpinnerStateChanged
        double value = (double) baseSpinner.getValue();
        if (value != spiralPainter.getBase())
            spiralPainter.setBase(value);
    }//GEN-LAST:event_baseSpinnerStateChanged

    private void balanceSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_balanceSpinnerStateChanged
        double value = (double) balanceSpinner.getValue();
        if (value != spiralPainter.getBalance())
            spiralPainter.setBalance(value);
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
        spiralPainter.setClockwise(dirCombo.getSelectedIndex() == 0);
    }//GEN-LAST:event_dirComboActionPerformed

    private void spinDirComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_spinDirComboActionPerformed
        config.setSpinClockwise(isSpinClockwise());
        refreshPreview(false);
    }//GEN-LAST:event_spinDirComboActionPerformed

    private void angleSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_angleSpinnerStateChanged
        config.setSpiralAngle((double)angleSpinner.getValue());
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
        maskPainter.setAntialiasingEnabled(fontAntialiasingToggle.isSelected());
    }//GEN-LAST:event_fontAntialiasingToggleActionPerformed

    private void lineSpacingSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_lineSpacingSpinnerStateChanged
        maskPainter.setLineSpacing((double)lineSpacingSpinner.getValue());
    }//GEN-LAST:event_lineSpacingSpinnerStateChanged

    private void maskEditButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maskEditButtonActionPerformed
        maskDialog.setLocationRelativeTo(this);
        maskDialog.setVisible(true);
    }//GEN-LAST:event_maskEditButtonActionPerformed

    private void maskTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_maskTabbedPaneStateChanged
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
        overlayMask = null;
        overlayImage = null;
        overlayImageMask = null;
        maskTextArea.setText("");
        maskScaleSpinner.setValue(1.0);
        for (int i = 0; i < colorIcons.length; i++){
            colorIcons[i].setColor(DEFAULT_SPIRAL_COLORS[i]);
            config.setSpiralColor(i, null);
            colorButtons.get(colorIcons[i]).repaint();
        }
        widthSpinner.setValue(DEFAULT_SPIRAL_WIDTH);
        heightSpinner.setValue(DEFAULT_SPIRAL_HEIGHT);
        radiusSpinner.setValue(100.0);
        baseSpinner.setValue(2.0);
        balanceSpinner.setValue(0.0);
        spinDirCombo.setSelectedIndex(0);
        config.setSpiralClockwise(true);
        dirCombo.setSelectedIndex(0);
        spiralPainter.setClockwise(true);
        angleSpinner.setValue(0.0);
    }//GEN-LAST:event_resetButtonActionPerformed
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
        if (isSpinClockwise() == spiralPainter.isClockwise())
                // Invert the angle, so as to make it spin in the right direction
            angle = SpiralPainter.MAXIMUM_ANGLE - angle;
            // Add the angle spinner's value and bound it by 360
        return (angle + (double) angleSpinner.getValue()) % 
                SpiralPainter.MAXIMUM_ANGLE;
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
            new SpiralGenerator().setVisible(true);
        });
    }
    
    private void refreshMaskText(){
        config.setMaskText(maskTextArea.getText());
        refreshPreview(true);
    }
    
    private void refreshPreview(boolean maskChanged){
        if (maskChanged){
            overlayMask = null;
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
        radiusSpinner.setEnabled(enabled);
        baseSpinner.setEnabled(enabled);
        balanceSpinner.setEnabled(enabled);
        dirCombo.setEnabled(enabled);
        angleSpinner.setEnabled(enabled);
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
                    diff, frameTimeTotal/((double)frameTotal), SPIRAL_FRAME_DURATION);
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
     * This is the painter used to paint the spiral.
     */
    private SpiralPainter spiralPainter = new SpiralPainter();
    /**
     * This is the painter used to paint the text used as the mask for the 
     * message when text is being used for the mask.
     */
    private CenteredTextPainter maskPainter = new CenteredTextPainter();
    /**
     * This is the image used as a mask for the overlay when text is being used 
     * as a mask. When this is null, then the mask will be generated the next 
     * time it is used.
     */
    private BufferedImage overlayMask = null;
    /**
     * This is the image used to create the mask for the overlay when a loaded 
     * image is used for the mask. This is the raw image, and is null when no 
     * image has been loaded for the mask.
     */
    private BufferedImage overlayImage = null;
    /**
     * This is the image used as as a mask for the overlay when a loaded image 
     * is used for the mask. This is null when the mask needs to be recreated 
     * from {@code overlayImage}, either due to another image being loaded in or 
     * the resulting image's size being changed.
     */
    private BufferedImage overlayImageMask = null;
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
    private javax.swing.JCheckBox italicToggle;
    private javax.swing.JLabel lineSpacingLabel;
    private javax.swing.JSpinner lineSpacingSpinner;
    private javax.swing.JButton loadMaskButton;
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
    private javax.swing.JLabel radiusLabel;
    private javax.swing.JSpinner radiusSpinner;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton saveButton;
    private javax.swing.JFileChooser saveFC;
    private components.JFileDisplayPanel saveFCPreview;
    private javax.swing.JComboBox<String> spinDirCombo;
    private javax.swing.JLabel spinLabel;
    private javax.swing.JPanel spiralColorPanel;
    private javax.swing.JPanel spiralCtrlPanel;
    private javax.swing.JPanel textMaskCtrlPanel;
    private javax.swing.JLabel widthLabel;
    private javax.swing.JSpinner widthSpinner;
    // End of variables declaration//GEN-END:variables
    
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
    
    private void paintOverlayMask(Graphics2D g, int frameIndex, Color color1, 
            Color color2, int width, int height){
        if (width <= 0 || height <= 0)
            return;
        boolean solidColor = frameIndex < 0 || Objects.equals(color1, color2);
        if (solidColor){
            if (hasNoColor(color1))
                return;
        } 
        else if (hasNoColor(color1,color2))
            return;
        String text = maskTextArea.getText();
        double scale = getMaskScale();
        double centerX = width/2.0;
        double centerY = height/2.0;
        boolean useImage = isOverlayMaskImage();
        if (!useImage && (solidColor || overlayMask == null || 
                overlayMask.getWidth() != width || overlayMask.getHeight() != height)
                && text != null && !text.isBlank()){
            Graphics2D gTemp;
            if (solidColor){
                gTemp = (Graphics2D)g.create();
                gTemp.setColor(color1);
                gTemp.translate(centerX, centerY);
                gTemp.scale(scale, scale);
                gTemp.translate(-centerX, -centerY);
            }
            else {
                overlayMask = new BufferedImage(width, height, 
                        BufferedImage.TYPE_INT_ARGB);
                gTemp = overlayMask.createGraphics();
                gTemp.setColor(Color.WHITE);
            }
            gTemp.setFont(maskTextArea.getFont());
            maskPainter.paint(gTemp, text, width, height);
            gTemp.dispose();
        }
        BufferedImage mask;
        if (useImage){
            if (overlayImageMask == null || overlayImageMask.getWidth() != width || 
                    overlayImageMask.getHeight() != height){
                overlayImageMask = overlayImage;
                if (overlayImageMask != null && 
                        (overlayImageMask.getWidth() != width || 
                        overlayImageMask.getHeight() != height)){
                    overlayImageMask = Thumbnailator.createThumbnail(
                            overlayImageMask, width, height);
                }
            }
            mask = overlayImageMask;
        }
        else if (!solidColor)
            mask = overlayMask;
        else
            return;
        if (mask != null){
            BufferedImage overlay = new BufferedImage(width, height, 
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D imgG = overlay.createGraphics();
            if (solidColor){
                imgG.setColor(color1);
                imgG.fillRect(0, 0, width, height);
            } else {
                paintSpiral(imgG,frameIndex,color1,color2,width,height);
            }
            imgG.translate(centerX, centerY);
            imgG.scale(scale, scale);
            imgG.translate(-centerX, -centerY);
            imgG.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                    ((useImage)?scale!=1.0:fontAntialiasingToggle.isSelected())? 
                            RenderingHints.VALUE_ANTIALIAS_ON : 
                            RenderingHints.VALUE_ANTIALIAS_OFF);
            maskImage(imgG,mask);
            imgG.dispose();
            g.drawImage(overlay, 0, 0, null);
        }
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
    
    private void paintSpiral(Graphics2D g, int frameIndex, Color color1, Color color2,
            int width, int height){
        paintSpiral(g,frameIndex,color1,color2,width,height,spiralPainter);
    }
    
    private void paintSpiralDesign(Graphics2D g, int frameIndex, int width, int height, Color color1){
        paintSpiral(g,frameIndex,color1,colorIcons[1].getColor(),width,height);
        paintOverlayMask(g,frameIndex,colorIcons[2].getColor(),colorIcons[3].getColor(),width,height);
    }
    
    private void paintSpiralDesign(Graphics2D g, int frameIndex, int width, int height){
        paintSpiralDesign(g,frameIndex,width,height,colorIcons[0].getColor());
    }
    
    private void paintMaskPreview(Graphics2D g, int width, int height){
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        paintOverlayMask(g,-1,Color.WHITE,Color.WHITE, width, height);
    }
    
    private class SpiralIcon implements Icon2D{

        @Override
        public void paintIcon2D(Component c, Graphics2D g, int x, int y) {
            g.translate(x, y);
            paintSpiralDesign(g,frameSlider.getValue(), getIconWidth(), getIconHeight());
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
            paintMaskPreview(g, getIconWidth(), getIconHeight());
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
    
    private class SpiralHandler implements PropertyChangeListener, ActionListener, DocumentListener{

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            boolean maskChanged = false;
            switch(evt.getPropertyName()){
                case(SpiralPainter.RADIUS_PROPERTY_CHANGED):
                    config.setSpiralRadius(spiralPainter.getRadius());
                    break;
                case(SpiralPainter.BASE_PROPERTY_CHANGED):
                    config.setSpiralBase(spiralPainter.getBase());
                    break;
                case(SpiralPainter.BALANCE_PROPERTY_CHANGED):
                    config.setSpiralBalance(spiralPainter.getBalance());
                    break;
                case(SpiralPainter.CLOCKWISE_PROPERTY_CHANGED):
                    config.setSpiralClockwise(spiralPainter.isClockwise());
                    break;
                case(CenteredTextPainter.ANTIALIASING_PROPERTY_CHANGED):
                    config.setMaskTextAntialiased(maskPainter.isAntialiasingEnabled());
                    maskChanged = true;
                    break;
                case(CenteredTextPainter.LINE_SPACING_PROPERTY_CHANGED):
                    config.setMaskLineSpacing(maskPainter.getLineSpacing());
                    maskChanged = true;
                    break;
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
}
