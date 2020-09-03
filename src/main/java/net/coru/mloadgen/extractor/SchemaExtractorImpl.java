/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.coru.mloadgen.extractor;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.coru.mloadgen.extractor.parser.SchemaParser;
import net.coru.mloadgen.extractor.parser.jschema.JSONSchemaParser;
import net.coru.mloadgen.extractor.parser.jschema.JSchemaParser;
import net.coru.mloadgen.model.FieldValueMapping;
import net.coru.mloadgen.model.json.ArrayField;
import net.coru.mloadgen.model.json.EnumField;
import net.coru.mloadgen.model.json.Field;
import net.coru.mloadgen.model.json.MapField;
import net.coru.mloadgen.model.json.ObjectField;
import net.coru.mloadgen.model.json.Schema;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;

import static java.lang.String.join;
import static java.util.Collections.singletonList;

public class SchemaExtractorImpl implements SchemaExtractor {

  @Override
  public List<FieldValueMapping> flatPropertiesList(Schema parserSchema) {
    return processSchema(parserSchema);
  }

  @Override
  public List<Schema> schemaTypesList(String schemaType, File schemaFile) throws IOException {
    String readLine = readLineByLine(schemaFile.getPath());
    SchemaParser parser;

    if ("JSchema".equalsIgnoreCase(schemaType)) {
      parser = new JSchemaParser();
    } else {
      parser = new JSONSchemaParser();
    }

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

    schema.getProperties().forEach(field -> attributeList.addAll(processField(field)));
    return attributeList;
  }

  private List<FieldValueMapping> extractInternalFields(ObjectField field) {
    return processFieldList(field.getProperties());
  }

  private List<FieldValueMapping> processFieldList(List<Field> fieldList) {
    List<FieldValueMapping> completeFieldList = new ArrayList<>();
    for(Field innerField : fieldList) {
      completeFieldList.addAll(processField(innerField));
    }
    return completeFieldList;
  }


  private Transformer<FieldValueMapping, FieldValueMapping> fixName(String fieldName, String splitter) {
    return fieldValue-> {
      fieldValue.setFieldName(fieldName + splitter + fieldValue.getFieldName());
      return fieldValue;
    };
  }

  private List<FieldValueMapping> processField(Field innerField) {
    List<FieldValueMapping> completeFieldList = new ArrayList<>();
    if (innerField instanceof ObjectField) {
      processRecordFieldList(innerField.getName(), ".", extractInternalFields((ObjectField)innerField), completeFieldList);
    } else if (innerField instanceof ArrayField) {
      completeFieldList.addAll(extractArrayInternalFields((ArrayField) innerField));
    } else if (innerField instanceof EnumField) {
      completeFieldList.add(new FieldValueMapping(innerField.getName(), innerField.getType(), 0,
              join(",", ((EnumField) innerField).getEnumValues())));
    } else if (innerField instanceof MapField) {
      completeFieldList.addAll(extractMapInternalFields((MapField) innerField));
    } else {
      completeFieldList.add(new FieldValueMapping(innerField.getName(), innerField.getType()));
    }
    return completeFieldList;
  }

  private List<FieldValueMapping> extractArrayInternalFields(ArrayField innerField) {
    List<FieldValueMapping> completeFieldList = new ArrayList<>();
    for (Field value : innerField.getValue()) {
      if (value instanceof ObjectField) {
        for (Field arrayElementField : ((ObjectField) value).getProperties()) {
          CollectionUtils.collect(
            processField(arrayElementField),
            fixName(innerField.getName(), "[]."),
            completeFieldList);
        }
      } else {
        completeFieldList.add(new FieldValueMapping(innerField.getName() + "[]", value.getType() + "-array"));
      }
    }
    return completeFieldList;
  }

  private List<FieldValueMapping> extractMapInternalFields(MapField innerField) {
    List<FieldValueMapping> completeFieldList = new ArrayList<>();
    Field value = innerField.getMapType();
    if (value instanceof ObjectField) {
      for (Field arrayElementField : ((ObjectField) value).getProperties()) {
        CollectionUtils.collect(
                processField(arrayElementField),
                fixName(innerField.getName(), "[][]."),
                completeFieldList);
      }
    } else {
      completeFieldList.add(new FieldValueMapping(innerField.getName() + "[]", value.getType() + "-map"));
    }
    return completeFieldList;
  }

  private void processRecordFieldList(String fieldName, String splitter, List<FieldValueMapping> internalFields, List<FieldValueMapping> completeFieldList) {
    internalFields.forEach(internalField -> {
      if (Objects.nonNull(internalField.getFieldName())) {
        internalField.setFieldName(fieldName + splitter + internalField.getFieldName());
      } else {
        internalField.setFieldName(fieldName);
      }
      completeFieldList.add(internalField);
    });
  }

}
