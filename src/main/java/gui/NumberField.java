package gui;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class NumberField extends JTextField {
    static class NumberFilter extends DocumentFilter {
        private static boolean isValidInt(String text) {
            try {
                return Integer.parseInt(text) > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (isValidInt(string)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (isValidInt(text)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    public void setNumber(int number) {
        setText(Integer.toString(number));
    }

    NumberField(int initialNumber) {
        super();
        PlainDocument doc = (PlainDocument) getDocument();
        doc.setDocumentFilter(new NumberFilter());
        setNumber(initialNumber);
    }

    int getNumber() {
        return getText().length() == 0 ? -1 : Integer.parseInt(getText());
    }
}
