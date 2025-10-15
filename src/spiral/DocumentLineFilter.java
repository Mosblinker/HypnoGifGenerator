/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author Mosblinker
 */
public class DocumentLineFilter extends DocumentFilter {

    private int maxLineCount;
    
    public DocumentLineFilter(int maxLineCount){
        if (maxLineCount <= 0)
            throw new IllegalArgumentException();
        this.maxLineCount = maxLineCount;
    }
    
    public int getMaximumLineCount(){
        return maxLineCount;
    }

    @Override
    public void insertString(FilterBypass fb, int offs,
            String str, AttributeSet a) throws BadLocationException {
        System.out.println("insertString ");
        super.insertString(fb, offs, str, a);
    }
    @Override
    public void replace(FilterBypass fb, int offs, int length, String str, 
            AttributeSet a) throws BadLocationException {
        System.out.println("replace");
        super.replace(fb, offs, length, str, a);
    }
}
