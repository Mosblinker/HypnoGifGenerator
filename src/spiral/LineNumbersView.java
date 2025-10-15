/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package spiral;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;

/**
 * Left hand side RowHeaderView for a JEditorPane in a JScrollPane. Highlights
 * the currently selected line. Handles line wrapping, frame resizing.
 * <p>
 * <a href="https://github.com/hblok/rememberjava/blob/master/_includes/src/com/rememberjava/ui/RowHeaderViewLineNumbers.java">RowHeaderVeuwLineNumbers.java</a>
 * @author Rob Camick
 * @author hblok
 * @author Mosblinker
 */
public class LineNumbersView extends JComponent implements DocumentListener, CaretListener, ComponentListener {
    
    private final int MARGIN_INSET_WIDTH = 10;
    
    private final int MINIMUM_MARGIN_WIDTH = 28;
    
    public final int MARGIN_WIDTH_PX = 28;
    
    private static final long serialVersionUID = 1L;

    private JTextComponent editor;

    private Font font;

    public LineNumbersView(JTextComponent editor) {
        this.editor = editor;

        editor.getDocument().addDocumentListener(this);
        editor.addComponentListener(this);
        editor.addCaretListener(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Rectangle clip = g.getClipBounds();
        int startOffset = editor.viewToModel2D(new Point(0, clip.y));
        int endOffset = editor.viewToModel2D(new Point(0, clip.y + clip.height));
        
        while (startOffset <= endOffset) {
            try {
                String lineNumber = getLineNumber(startOffset);
                if (lineNumber != null) {
                    int x = getInsets().left + 2;
                    int y = getOffsetY(startOffset);

                    font = font != null ? font : new Font(Font.MONOSPACED, Font.BOLD, editor.getFont().getSize());
                    g.setFont(font);

                    g.setColor(isCurrentLine(startOffset) ? Color.RED : Color.BLACK);

                    g.drawString(lineNumber, x, y);
                }

              startOffset = Utilities.getRowEnd(editor, startOffset) + 1;
            } catch (BadLocationException e) {
                SpiralGenerator.getLogger().log(Level.INFO, 
                        "Bad location found while painting line numbers", e);
                // ignore and continue
            }
        }
    }

    /**
     * Returns the line number of the element based on the given (start) offset
     * in the editor model. Returns null if no line number should or could be
     * provided (e.g. for wrapped lines).
     */
    private String getLineNumber(int offset) {
        Element root = editor.getDocument().getDefaultRootElement();
        int index = root.getElementIndex(offset);
        Element line = root.getElement(index);

        return line.getStartOffset() == offset ? String.format("%3d", index + 1) : null;
    }

    /**
     * Returns the y axis position for the line number belonging to the element
     * at the given (start) offset in the model.
     */
    private int getOffsetY(int offset) throws BadLocationException {
        FontMetrics fontMetrics = editor.getFontMetrics(editor.getFont());
        int descent = fontMetrics.getDescent();

        Rectangle2D r = editor.modelToView2D(offset);
        double y = r.getY() + r.getHeight() - descent;

        return (int)Math.floor(y);
    }

    /**
     * Returns true if the given start offset in the model is the selected (by
     * cursor position) element.
     */
    private boolean isCurrentLine(int offset) {
        int caretPosition = editor.getCaretPosition();
        Element root = editor.getDocument().getDefaultRootElement();
        return root.getElementIndex(offset) == root.getElementIndex(caretPosition);
    }

    /**
     * Schedules a refresh of the line number margin on a separate thread.
     */
    private void documentChanged() {
        SwingUtilities.invokeLater(() -> {
            repaint();
        });
    }

    /**
     * Updates the size of the line number margin based on the editor height.
     */
    private void updateSize() {
        
        Dimension size = new Dimension(MARGIN_WIDTH_PX, editor.getHeight());
        setPreferredSize(size);
        setSize(size);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        documentChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        documentChanged();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        documentChanged();
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        documentChanged();
    }

    @Override
    public void componentResized(ComponentEvent e) {
        updateSize();
        documentChanged();
    }

    @Override
    public void componentMoved(ComponentEvent e) {
    }

    @Override
    public void componentShown(ComponentEvent e) {
        updateSize();
        documentChanged();
    }

    @Override
    public void componentHidden(ComponentEvent e) {
    }
            System.out.println("Font changed");
}
