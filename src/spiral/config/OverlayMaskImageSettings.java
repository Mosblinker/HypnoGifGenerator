/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package spiral.config;

import java.io.File;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import utils.SwingExtendedUtilities;

/**
 *
 * @author Mosblinker
 */
public interface OverlayMaskImageSettings extends AntialiasedOverlayMaskSettings{
    /**
     * 
     */
    public static final String ALPHA_CHANNEL_INDEX_KEY = "AlphaChannelIndex";
    /**
     * 
     */
    public static final String IMAGE_INVERT_KEY = "ImageInvert";
    /**
     * 
     */
    public static final String DESATURATE_MODE_KEY = "DesaturateMode";
    /**
     * 
     */
    public static final String IMAGE_FILE_KEY = "ImageFile";
    /**
     * 
     */
    public static final String IMAGE_FRAME_INDEX_KEY = "ImageFrameIndex";
    /**
     * 
     */
    public static final String IMAGE_INTERPOLATION_KEY = "ImageInterpolation";
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public int getAlphaIndex(int defaultValue);
    /**
     * 
     * @return 
     */
    public default int getAlphaIndex(){
        return getAlphaIndex(0);
    }
    /**
     * 
     * @param group 
     */
    public default void loadAlphaIndex(ButtonGroup group){
        int index = getAlphaIndex(-1);
        if (index < 0 || index >= group.getButtonCount())
            return;
        AbstractButton button = SwingExtendedUtilities.getButton(group, index);
        if (button != null)
            button.setSelected(true);
    }
    /**
     * 
     * @param value 
     */
    public void setAlphaIndex(int value);
    /**
     * 
     * @param group 
     */
    public default void setAlphaIndex(ButtonGroup group){
        setAlphaIndex(SwingExtendedUtilities.indexOfSelected(group));
    }
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public boolean isImageInverted(boolean defaultValue);
    /**
     * 
     * @return 
     */
    public default boolean isImageInverted(){
        return isImageInverted(false);
    }
    /**
     * 
     * @param value 
     */
    public void setImageInverted(boolean value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public int getDesaturateMode(int defaultValue);
    /**
     * 
     * @return 
     */
    public default int getDesaturateMode(){
        return getDesaturateMode(0);
    }
    /**
     * 
     * @param value 
     */
    public void setDesaturateMode(int value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public File getImageFile(File defaultValue);
    /**
     * 
     * @return 
     */
    public default File getImageFile(){
        return getImageFile(null);
    }
    /**
     * 
     * @param value 
     */
    public void setImageFile(File value);
    /**
     * 
     * @return 
     */
    public int getImageFrameIndex();
    /**
     * 
     * @param value 
     */
    public void setImageFrameIndex(int value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public int getImageInterpolation(int defaultValue);
    /**
     * 
     * @return 
     */
    public default int getImageInterpolation(){
        return getImageInterpolation(0);
    }
    /**
     * 
     * @param value 
     */
    public void setImageInterpolation(int value);
}
