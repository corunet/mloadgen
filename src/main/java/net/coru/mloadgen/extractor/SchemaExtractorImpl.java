/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.coru.mloadgen.extractor;

import net.coru.mloadgen.extractor.parser.jschema.JSchemaParser;
import net.coru.mloadgen.model.FieldValueMapping;
import net.coru.mloadgen.model.json.ArrayField;
import net.coru.mloadgen.model.json.Field;
import net.coru.mloadgen.model.json.ObjectField;
import net.coru.mloadgen.model.json.Schema;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;


import static freemarker.template.utility.Collections12.singletonList;

public class SchemaExtractorImpl implements SchemaExtractor {

  @Override
  public List<FieldValueMapping> flatPropertiesList(Schema parserSchema) {
    return processSchema(parserSchema);
  }

  @Override
  public List<Schema> schemaTypesList(File schemaFile) throws IOException {
    String readLine = readLineByLine(schemaFile.getPath());
    JSchemaParser parser = new JSchemaParser();
    return singletonList(parser.parse(readLine));
  }

  private static String readLineByLine(String filePath) throws IOException {
    StringBuilder contentBuilder = new StringBuilder();

    try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
    {
      stream.forEach(s -> contentBuilder.append(s).append("\n"));
    }

    return contentBuilder.toString();
  }

  @Override
  public List<FieldValueMapping> processSchema(Schema schema) {
    List<FieldValueMapping> attributeList = new ArrayList<>();

//    schema.getFields().forEach(field -> processField(field, attributeList));
    return attributeList;
  }

  private List<FieldValueMapping> extractInternalFields(ObjectField field) {
    return Collections.emptyList(); // processFieldList(field.getValue());
  }

  private List<FieldValueMapping> processFieldList(List<Field> fieldList) {
    List<FieldValueMapping> completeFieldList = new ArrayList<>();
    for(Field innerField : fieldList) {
      processField(innerField, completeFieldList);
    }
    return completeFieldList;
  }

  private List<FieldValueMapping> extractArrayInternalFields(Field innerField) {
    return extractArrayInternalFields(innerField.getName(), innerField);
  }

  private List<FieldValueMapping> extractArrayInternalFields(String fieldName, Field innerField) {
    List<FieldValueMapping> completeFieldList = new ArrayList<>();
    if (innerField instanceof ObjectField) {
     /* for (Field arrayElementField : (List<Field>)innerField.getValue()) {
        processField(arrayElementField, completeFieldList);
      }*/
    }/* else if (typesSet.contains(innerField.getType())) {
      completeFieldList.add( new FieldValueMapping(fieldName,innerField.getType()+"-array"));
    }*/
    return completeFieldList;
  }

  private void processField(Field innerField, List<FieldValueMapping> completeFieldList) {
    if (innerField instanceof ObjectField) {
      processRecordFieldList(innerField.getName(), ".", extractInternalFields((ObjectField)innerField), completeFieldList);
    } else if (innerField instanceof ArrayField) {
      List<FieldValueMapping> internalFields = extractArrayInternalFields(innerField);
      if (checkIfRecord(innerField)) {
        processRecordFieldList(innerField.getName(), "[].", internalFields, completeFieldList);
      } else {
        createArrayType(completeFieldList, internalFields, innerField.getName());
      }
    } else {
      completeFieldList.add(new FieldValueMapping(innerField.getName(), innerField.getType()));
    }
  }

  private void createArrayType(List<FieldValueMapping> completeFieldList, List<FieldValueMapping> internalFields, String fieldName) {
    internalFields.get(0).setFieldName(fieldName);
    completeFieldList.add(internalFields.get(0));
  }

  private boolean checkIfRecord(Field innerField) {
    return innerField instanceof ObjectField;
  }

  private void processRecordFieldList(String fieldName, String splitter, List<FieldValueMapping> internalFields, List<FieldValueMapping> completeFieldList) {
    internalFields.forEach(internalField -> {
      internalField.setFieldName(fieldName + splitter + internalField.getFieldName());
      completeFieldList.add(internalField);
    });
  }

}
