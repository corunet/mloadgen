/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.coru.mloadgen.config.fileserialized;

import net.coru.mloadgen.input.json.FileSubjectPropertyEditor;
import net.coru.mloadgen.input.json.SchemaConverterPropertyEditor;
import net.coru.mloadgen.model.FieldValueMapping;
import org.apache.jmeter.testbeans.BeanInfoSupport;
import org.apache.jmeter.testbeans.gui.TableEditor;
import org.apache.jmeter.testbeans.gui.TypeEditor;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;

public class FileSerializedConfigElementBeanInfo extends BeanInfoSupport {

  private static final String JSON_COLLECTION = "jsonCollection";

  private static final String SCHEMA_PROPERTIES = "schemaProperties";

  private static final String JSON_SCHEMA = "jsonSchema";

  public FileSerializedConfigElementBeanInfo() {

    super(FileSerializedConfigElement.class);

    createPropertyGroup("file_serialized_load_generator", new String[]{
            JSON_COLLECTION, JSON_SCHEMA, SCHEMA_PROPERTIES
    });

    PropertyDescriptor subjectNameProps = property(JSON_COLLECTION);
    subjectNameProps.setValue(NOT_UNDEFINED, Boolean.TRUE);
    subjectNameProps.setValue(DEFAULT, "<jsonCollection>");
    subjectNameProps.setValue(NOT_EXPRESSION, Boolean.FALSE);

    PropertyDescriptor avroSchemaProps = property(JSON_SCHEMA);
    avroSchemaProps.setPropertyEditorClass(FileSubjectPropertyEditor.class);
    avroSchemaProps.setValue(NOT_UNDEFINED, Boolean.TRUE);
    avroSchemaProps.setValue(DEFAULT, "");
    avroSchemaProps.setValue(NOT_EXPRESSION, Boolean.FALSE);

    TypeEditor tableEditor = TypeEditor.TableEditor;
    PropertyDescriptor tableProperties = property(SCHEMA_PROPERTIES, tableEditor);
    tableProperties.setValue(TableEditor.CLASSNAME, FieldValueMapping.class.getName());
    tableProperties.setValue(TableEditor.HEADERS,
        new String[]{
            "Field Name",
            "Field Type",
            "Field Length",
            "Field Values List"
        });
    tableProperties.setValue(TableEditor.OBJECT_PROPERTIES,
        new String[]{
            FieldValueMapping.FIELD_NAME,
            FieldValueMapping.FIELD_TYPE,
            FieldValueMapping.VALUE_LENGTH,
            FieldValueMapping.FIELD_VALUES_LIST
        });
    tableProperties.setValue(DEFAULT, new ArrayList<>());
    tableProperties.setValue(NOT_UNDEFINED, Boolean.TRUE);
  }
}
