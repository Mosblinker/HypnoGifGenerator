/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package spiral.config;

/**
 *
 * @author Mosblinker
 */
public interface OverlayMaskMessagesSettings extends TextOverlayMaskSettings{
    /**
     * 
     */
    public static final String ADD_BLANK_FRAMES_KEY = "AddBlankFrames";
    /**
     * 
     */
    public static final String MESSAGE_COUNT_KEY = "MessageCount";
    /**
     * 
     */
    public static final String MESSAGE_KEY_PREFIX = "Message";
    /**
     * 
     */
    public static final String PROMPT_KEY = "Prompt";
    /**
     * 
     */
    public static final String ALWAYS_SHOW_PROMPT_KEY = "AlwaysShowPrompt";
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public boolean getAddBlankFrames(boolean defaultValue);
    /**
     * 
     * @return 
     */
    public default boolean getAddBlankFrames(){
        return getAddBlankFrames(false);
    }
    /**
     * 
     * @param value 
     */
    public void setAddBlankFrames(boolean value);
    /**
     * 
     * @return 
     */
    public int getMessageCount();
    /**
     * 
     * @param value 
     */
    public void setMessageCount(int value);
    /**
     * 
     * @param index
     * @return 
     */
    public String getMessage(int index);
    /**
     * 
     * @param index
     * @param value 
     */
    public void setMessage(int index, String value);
    /**
     * 
     * @return 
     */
    public String getPrompt();
    /**
     * 
     * @param value 
     */
    public void setPrompt(String value);
    /**
     * 
     * @param defaultValue
     * @return 
     */
    public boolean getAlwaysShowPrompt(boolean defaultValue);
    /**
     * 
     * @return 
     */
    public default boolean getAlwaysShowPrompt(){
        return getAlwaysShowPrompt(true);
    }
    /**
     * 
     * @param value 
     */
    public void setAlwaysShowPrompt(boolean value);
}
