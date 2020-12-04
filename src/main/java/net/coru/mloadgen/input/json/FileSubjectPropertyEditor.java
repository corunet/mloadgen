/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.coru.mloadgen.input.json;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;
import lombok.extern.slf4j.Slf4j;
import net.coru.mloadgen.extractor.SchemaExtractor;
import net.coru.mloadgen.extractor.impl.SchemaExtractorImpl;
import net.coru.mloadgen.model.FieldValueMapping;
import net.coru.mloadgen.model.json.Schema;
import net.coru.mloadgen.util.AutoCompletion;
import net.coru.mloadgen.util.PropsKeysHelper;
import org.apache.jmeter.gui.ClearGui;
import org.apache.jmeter.gui.GuiPackage;
import org.apache.jmeter.testbeans.gui.GenericTestBeanCustomizer;
import org.apache.jmeter.testbeans.gui.TableEditor;
import org.apache.jmeter.testbeans.gui.TestBeanGUI;
import org.apache.jmeter.testbeans.gui.TestBeanPropertyEditor;
import org.apache.jmeter.util.JMeterUtils;

@Slf4j
public class FileSubjectPropertyEditor extends PropertyEditorSupport implements ActionListener, TestBeanPropertyEditor, ClearGui {

  private JComboBox<String> schemaTypeComboBox;

  private final JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

  private final JPanel panel = new JPanel();

  private PropertyDescriptor propertyDescriptor;

  private final SchemaExtractor schemaExtractor = new SchemaExtractorImpl();

  private List<Schema> parserSchemaList;

  private final JButton openFileDialogButton = new JButton(JMeterUtils.getResString("file_visualizer_open"));

  public FileSubjectPropertyEditor() {
    this.init();
  }

  public FileSubjectPropertyEditor(Object source) {
    super(source);
    this.init();
    this.setValue(source);
  }

  public FileSubjectPropertyEditor(PropertyDescriptor propertyDescriptor) {
    super(propertyDescriptor);
    this.propertyDescriptor = propertyDescriptor;
    this.init();
  }

  private void init() {
    schemaTypeComboBox = new JComboBox<>();
    schemaTypeComboBox.setEditable(false);
    schemaTypeComboBox.insertItemAt("JSchema", 0);
    schemaTypeComboBox.insertItemAt("JSON-Schema", 1);
    schemaTypeComboBox.setSelectedIndex(0);
    panel.setLayout(new BorderLayout());
    openFileDialogButton.addActionListener(this);
    panel.add(openFileDialogButton, BorderLayout.LINE_END);
    panel.add(schemaTypeComboBox);
    AutoCompletion.enable(schemaTypeComboBox);
  }

  @Override
  public void actionPerformed(ActionEvent event) {
    int returnValue = fileChooser.showDialog(panel, JMeterUtils.getResString("file_visualizer_open"));

    if (JFileChooser.APPROVE_OPTION == returnValue) {
      File schemaFile = Objects.requireNonNull(fileChooser.getSelectedFile());
      try {
        String schemaType = schemaTypeComboBox.getSelectedItem().toString();
        parserSchemaList = schemaExtractor.schemaTypesList(schemaType, schemaFile);
        List<FieldValueMapping> attributeList = schemaExtractor.flatPropertiesList(parserSchemaList.get(0));
        //Get current test GUI component
        TestBeanGUI testBeanGUI = (TestBeanGUI) GuiPackage.getInstance().getCurrentGui();
        Field customizer = TestBeanGUI.class.getDeclaredField(PropsKeysHelper.CUSTOMIZER);
        customizer.setAccessible(true);

        //From TestBeanGUI retrieve Bean Customizer as it includes all editors like ClassPropertyEditor, TableEditor
        GenericTestBeanCustomizer testBeanCustomizer = (GenericTestBeanCustomizer) customizer.get(testBeanGUI);
        Field editors = GenericTestBeanCustomizer.class.getDeclaredField(PropsKeysHelper.EDITORS);
        editors.setAccessible(true);

        //Retrieve TableEditor and set all fields with default values to it
        PropertyEditor[] propertyEditors = (PropertyEditor[]) editors.get(testBeanCustomizer);
        for (PropertyEditor propertyEditor : propertyEditors) {
          if (propertyEditor instanceof TableEditor) {
            propertyEditor.setValue(attributeList);
          } else if (propertyEditor instanceof SchemaConverterPropertyEditor) {
            propertyEditor.setValue(parserSchemaList.get(0));
          }
        }
      } catch (NoSuchFieldException | IllegalAccessException e) {
        JOptionPane
                .showMessageDialog(panel, "Failed retrieve schema properties : " + e.getMessage(), "ERROR: Failed to retrieve properties!",
                        JOptionPane.ERROR_MESSAGE);
        log.error(e.getMessage(), e);
      } catch (IOException e) {
        JOptionPane.showMessageDialog(panel, "Can't read a file : " + e.getMessage(), "ERROR: Failed to retrieve properties!",
            JOptionPane.ERROR_MESSAGE);
        log.error(e.getMessage(), e);
      }
    }
  }

  @Override
  public void clearGui() {
    // Not implementation required
  }

  @Override
  public void setDescriptor(PropertyDescriptor descriptor) {
    propertyDescriptor = descriptor;
  }

  @Override
  public String getAsText() {
    return Objects.requireNonNull(this.schemaTypeComboBox.getSelectedItem()).toString();
  }

  @Override
  public Component getCustomEditor() {
    return this.panel;
  }

  @Override
  public Object getValue() {
    return this.schemaTypeComboBox.getSelectedItem();
  }

  @Override
  public boolean supportsCustomEditor() {
    return true;
  }

}
