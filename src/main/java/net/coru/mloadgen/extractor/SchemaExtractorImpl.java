/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.coru.mloadgen.extractor;

import static java.lang.String.join;
import static java.util.Collections.singletonList;
import static net.coru.mloadgen.util.ConstraintTypeEnum.EXCLUDED_MAXIMUM_VALUE;
import static net.coru.mloadgen.util.ConstraintTypeEnum.EXCLUDED_MINIMUM_VALUE;
import static net.coru.mloadgen.util.ConstraintTypeEnum.FORMAT;
import static net.coru.mloadgen.util.ConstraintTypeEnum.MAXIMUM_VALUE;
import static net.coru.mloadgen.util.ConstraintTypeEnum.MINIMUM_VALUE;
import static net.coru.mloadgen.util.ConstraintTypeEnum.MULTIPLE_OF;
import static net.coru.mloadgen.util.ConstraintTypeEnum.REGEX;

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
import net.coru.mloadgen.util.ConstraintTypeEnum;
import net.coru.mloadgen.model.FieldValueMapping;
import net.coru.mloadgen.model.json.ArrayField;
import net.coru.mloadgen.model.json.EnumField;
import net.coru.mloadgen.model.json.Field;
import net.coru.mloadgen.model.json.MapField;
import net.coru.mloadgen.model.json.NumberField;
import net.coru.mloadgen.model.json.ObjectField;
import net.coru.mloadgen.model.json.Schema;
import net.coru.mloadgen.model.json.StringField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

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
      completeFieldList.add(FieldValueMapping
          .builder()
          .fieldName(innerField.getName())
          .fieldType(innerField.getType())
          .valueLength(0)
          .fieldValueList(join(",", ((EnumField) innerField).getEnumValues()))
          .build());
    } else if (innerField instanceof MapField) {
      completeFieldList.addAll(extractMapInternalFields((MapField) innerField));
    } else if (innerField instanceof NumberField) {
      FieldValueMapping.FieldValueMappingBuilder builder = FieldValueMapping
          .builder()
          .fieldName(innerField.getName())
          .fieldType(innerField.getType());

      addConstraint(builder, EXCLUDED_MAXIMUM_VALUE, getSafeNumberAsString(((NumberField) innerField).getExclusiveMaximum()));
      addConstraint(builder, EXCLUDED_MINIMUM_VALUE, getSafeNumberAsString(((NumberField) innerField).getExclusiveMinimum()));
      addConstraint(builder, MAXIMUM_VALUE, getSafeNumberAsString(((NumberField) innerField).getMaximum()));
      addConstraint(builder, MINIMUM_VALUE, getSafeNumberAsString(((NumberField) innerField).getMinimum()));
      addConstraint(builder, MULTIPLE_OF, getSafeNumberAsString(((NumberField) innerField).getMultipleOf()));

      completeFieldList.add(builder.build());
    } else if (innerField instanceof StringField) {
      FieldValueMapping.FieldValueMappingBuilder builder = FieldValueMapping
          .builder()
          .fieldName(innerField.getName())
          .fieldType(innerField.getType());

      addConstraint(builder, REGEX, ((StringField) innerField).getRegex());
      addConstraint(builder, MAXIMUM_VALUE, getSafeNumberAsString(((StringField) innerField).getMaxlength()));
      addConstraint(builder, MINIMUM_VALUE, getSafeNumberAsString(((StringField) innerField).getMinLength()));
      addConstraint(builder, FORMAT, ((StringField) innerField).getFormat());

      completeFieldList.add(builder.build());
    } else {
      completeFieldList.add(FieldValueMapping.builder().fieldName(innerField.getName()).fieldType(innerField.getType()).build());
    }
    return completeFieldList;
  }

  private void addConstraint(FieldValueMapping.FieldValueMappingBuilder builder, ConstraintTypeEnum constrain, String constrainValue) {
    if (StringUtils.isNotBlank(constrainValue)) {
      builder.constrain(constrain, constrainValue);
    }
  }

  private String getSafeNumberAsString(Number exclusiveMaximum) {
    String result = null;
    if (Objects.nonNull(exclusiveMaximum)) {
      result = exclusiveMaximum.toString();
    }
    return result;
  }

  private List<FieldValueMapping> extractArrayInternalFields(ArrayField innerField) {
    List<FieldValueMapping> completeFieldList = new ArrayList<>();
    for (Field value : innerField.getValues()) {
      if (value instanceof ObjectField) {
        for (Field arrayElementField : value.getProperties()) {
          CollectionUtils.collect(
            processField(arrayElementField),
            fixName(innerField.getName(), "[]."),
            completeFieldList);
        }
      } else {
        completeFieldList.add(FieldValueMapping.builder().fieldName(innerField.getName() + "[]").fieldType( value.getType() + "-array").build());
      }
    }
    return completeFieldList;
  }

  private List<FieldValueMapping> extractMapInternalFields(MapField innerField) {
    List<FieldValueMapping> completeFieldList = new ArrayList<>();
    Field value = innerField.getMapType();
    if (value instanceof ObjectField) {
      for (Field arrayElementField : value.getProperties()) {
        CollectionUtils.collect(
                processField(arrayElementField),
                fixName(innerField.getName(), "[][]."),
                completeFieldList);
      }
    } else {
      completeFieldList.add(FieldValueMapping.builder().fieldName(innerField.getName() + "[]").fieldType(value.getType() + "-map").build());
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
