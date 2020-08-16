package net.coru.mloadgen.util;

import javax.swing.ComboBoxModel;
import net.coru.mloadgen.exception.MLoadGenException;

/* This work is hereby released into the Public Domain.
 * To view a copy of the public domain dedication, visit
 * http://creativecommons.org/licenses/publicdomain/
 */
public class AutoCompletion extends javax.swing.text.PlainDocument {

  private final javax.swing.JComboBox<String> comboBox;

  private transient javax.swing.ComboBoxModel<String> model;

  private javax.swing.text.JTextComponent editor;

  // flag to indicate if setSelectedItem has been called
  // subsequent calls to remove/insertString should be ignored
  private boolean selecting = false;

  private final boolean hidePopupOnFocusLoss;

  private boolean hitBackspace = false;

  private boolean hitBackspaceOnSelection;

  private final transient java.awt.event.KeyListener editorKeyListener;

  private final transient java.awt.event.FocusListener editorFocusListener;

  public AutoCompletion(final javax.swing.JComboBox<String> comboBox) {
    this.comboBox = comboBox;
    model = comboBox.getModel();
    comboBox.addActionListener(e -> {
      if (!selecting) {
        highlightCompletedText(0);
      }
    });
    comboBox.addPropertyChangeListener(e -> {
      if (e.getPropertyName().equals("editor")) {
        configureEditor((javax.swing.ComboBoxEditor) e.getNewValue());
      }
      if (e.getPropertyName().equals("model")) {
        model = (ComboBoxModel) e.getNewValue();
      }
    });
    editorKeyListener = new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent e) {
        if (comboBox.isDisplayable()) {
          comboBox.setPopupVisible(true);
        }
        hitBackspace = false;
        switch (e.getKeyCode()) {
          // determine if the pressed key is backspace (needed by the remove method)
          case java.awt.event.KeyEvent.VK_BACK_SPACE:
            hitBackspace = true;
            hitBackspaceOnSelection = editor.getSelectionStart() != editor.getSelectionEnd();
            break;
          // ignore delete key
          case java.awt.event.KeyEvent.VK_DELETE:
            e.consume();
            comboBox.getToolkit().beep();
            break;
          default:
            break;
        }
      }
    };
    // Bug 5100422 on Java 1.5: Editable JComboBox won't hide popup when tabbing out
    hidePopupOnFocusLoss = System.getProperty("java.version").startsWith("1.5");
    // Highlight whole text when gaining focus
    editorFocusListener = new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent e) {
        highlightCompletedText(0);
      }

      public void focusLost(java.awt.event.FocusEvent e) {
        // Workaround for Bug 5100422 - Hide Popup on focus loss
        if (hidePopupOnFocusLoss) {
          comboBox.setPopupVisible(false);
        }
      }
    };
    configureEditor(comboBox.getEditor());
    // Handle initially selected object
    Object selected = comboBox.getSelectedItem();
    if (selected != null) {
      setText(selected.toString());
    }
    highlightCompletedText(0);
  }

  public static void enable(javax.swing.JComboBox<String> comboBox) {
    // has to be editable
    comboBox.setEditable(true);
    // change the editor's document
    new net.coru.mloadgen.util.AutoCompletion(comboBox);
  }

  private void configureEditor(javax.swing.ComboBoxEditor newEditor) {
    if (editor != null) {
      editor.removeKeyListener(editorKeyListener);
      editor.removeFocusListener(editorFocusListener);
    }

    if (newEditor != null) {
      editor = (javax.swing.text.JTextComponent) newEditor.getEditorComponent();
      editor.addKeyListener(editorKeyListener);
      editor.addFocusListener(editorFocusListener);
      editor.setDocument(this);
    }
  }

  public void remove(int offs, int len) throws javax.swing.text.BadLocationException {
    // return immediately when selecting an item
    int editOffs = offs;
    if (selecting) {
      return;
    }
    if (hitBackspace) {
      // user hit backspace => move the selection backwards
      // old item keeps being selected
      if (editOffs > 0) {
        if (hitBackspaceOnSelection) {
          editOffs--;
        }
      } else {
        // User hit backspace with the cursor positioned on the start => beep
        comboBox.getToolkit().beep(); // when available use: UIManager.getLookAndFeel().provideErrorFeedback(comboBox);
      }
      highlightCompletedText(editOffs);
    } else {
      super.remove(editOffs, len);
    }
  }

  public void insertString(int offs, String str, javax.swing.text.AttributeSet a) throws javax.swing.text.BadLocationException {
    // return immediately when selecting an item
    int editOffs = offs;
    if (selecting) {
      return;
    }
    // insert the string into the document
    super.insertString(editOffs, str, a);
    // lookup and select a matching item
    Object item = lookupItem(getText(0, getLength()));
    if (item != null) {
      setSelectedItem(item);
    } else {
      // keep old item selected if there is no match
      item = comboBox.getSelectedItem();
      // imitate no insert (later on offs will be incremented by str.length(): selection won't move forward)
      editOffs = editOffs - str.length();
      // provide feedback to the user that his input has been received but can not be accepted
      comboBox.getToolkit().beep(); // when available use: UIManager.getLookAndFeel().provideErrorFeedback(comboBox);
    }
    setText(item.toString());
    // select the completed part
    highlightCompletedText(editOffs + str.length());
  }

  private void setText(String text) {
    try {
      // remove all text and insert the completed string
      super.remove(0, getLength());
      super.insertString(0, text, null);
    } catch (javax.swing.text.BadLocationException e) {
      throw new MLoadGenException(e.toString());
    }
  }

  private void highlightCompletedText(int start) {
    editor.setCaretPosition(getLength());
    editor.moveCaretPosition(start);
  }

  private void setSelectedItem(Object item) {
    selecting = true;
    model.setSelectedItem(item);
    selecting = false;
  }

  private Object lookupItem(String pattern) {
    Object selectedItem = model.getSelectedItem();
    // only search for a different item if the currently selected does not match
    if (selectedItem != null && startsWithIgnoreCase(selectedItem.toString(), pattern)) {
      return selectedItem;
    } else {
      // iterate over all items
      for (int i = 0, n = model.getSize(); i < n; i++) {
        Object currentItem = model.getElementAt(i);
        // current item starts with the pattern?
        if (currentItem != null && startsWithIgnoreCase(currentItem.toString(), pattern)) {
          return currentItem;
        }
      }
    }
    // no item starts with the pattern => return null
    return null;
  }

  // checks if str1 starts with str2 - ignores case
  private boolean startsWithIgnoreCase(String str1, String str2) {
    return str1.toUpperCase().startsWith(str2.toUpperCase());
  }

  private static void createAndShowGUI() {
    // the combo box (add/modify items if you like to)
    final javax.swing.JComboBox<String> comboBox = new javax.swing.JComboBox<>(new String[]{"Ester", "Jordi", "Jordina", "Jorge", "Sergi"});
    enable(comboBox);

    // create and show a window containing the combo box
    final javax.swing.JFrame frame = new javax.swing.JFrame();
    frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    frame.getContentPane().add(comboBox);
    frame.pack();
    frame.setVisible(true);
  }

  public static void main(String[] args) {
    javax.swing.SwingUtilities.invokeLater(() -> createAndShowGUI());
  }
}
