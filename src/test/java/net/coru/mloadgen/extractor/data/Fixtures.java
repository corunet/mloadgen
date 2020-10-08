package net.coru.mloadgen.extractor.data;

import static java.util.Arrays.asList;
import static net.coru.mloadgen.util.ConstraintTypeEnum.MINIMUM_VALUE;

import java.util.List;
import java.util.Map;
import net.coru.mloadgen.model.FieldValueMapping;
import net.coru.mloadgen.model.json.ArrayField;
import net.coru.mloadgen.model.json.BooleanField;
import net.coru.mloadgen.model.json.EnumField;
import net.coru.mloadgen.model.json.MapField;
import net.coru.mloadgen.model.json.NumberField;
import net.coru.mloadgen.model.json.ObjectField;
import net.coru.mloadgen.model.json.Schema;
import net.coru.mloadgen.model.json.StringField;
import net.coru.mloadgen.util.ConstraintTypeEnum;

public class Fixtures {

  private final static Map<ConstraintTypeEnum, String> STRING_DEFAULTS = Map.of(
      ConstraintTypeEnum.MAXIMUM_VALUE, "0",
      MINIMUM_VALUE, "0");

  public static final Schema SIMPLE_SCHEMA =
    Schema
      .builder()
      .property(StringField.builder().name("name").build())
      .property(StringField.builder().name("surname").build())
      .build();

  public static final List<FieldValueMapping> EXPECTED_SIMPLE_FIELD_VALUE_MAPPING =
          asList(FieldValueMapping.builder().fieldName("name").fieldType("string").constrains(STRING_DEFAULTS).build(),
                 FieldValueMapping.builder().fieldName("surname").fieldType("string").constrains(STRING_DEFAULTS).build());

  public static final List<FieldValueMapping> DEFINED_SIMPLE_FIELD_VALUE_MAPPING =
          asList(FieldValueMapping.builder().fieldName("name").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("name").build(),
                 FieldValueMapping.builder().fieldName("surname").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("surname").build());

  public static final Schema MEDIUM_SCHEMA =
      Schema
          .builder()
          .property(StringField.builder().name("name").build())
          .property(StringField.builder().name("surname").build())
          .property(ObjectField
              .builder()
              .name("address")
              .property(StringField.builder().name("street").build())
              .property(NumberField.builder().name("number").build())
              .build())
          .build();

  public static final List<FieldValueMapping> EXPECTED_MEDIUM_FIELD_VALUE_MAPPING =
          asList(FieldValueMapping.builder().fieldName("name").fieldType("string").constrains(STRING_DEFAULTS).build(),
                 FieldValueMapping.builder().fieldName("surname").fieldType("string").constrains(STRING_DEFAULTS).build(),
                 FieldValueMapping.builder().fieldName("address.street").fieldType("string").constrains(STRING_DEFAULTS).build(),
                 FieldValueMapping.builder().fieldName("address.number").fieldType("number").build());

  public static final List<FieldValueMapping> DEFINED_MEDIUM_FIELD_VALUE_MAPPING =
          asList(FieldValueMapping.builder().fieldName("name").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("name").build(),
                 FieldValueMapping.builder().fieldName("surname").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("surname").build(),
                 FieldValueMapping.builder().fieldName("address.street").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("address").build(),
                 FieldValueMapping.builder().fieldName("address.number").fieldType("number").fieldValueList("0").build()
          );

  public static final Schema MEDIUM_ARRAY_SCHEMA =
      Schema
          .builder()
          .property(StringField.builder().name("name").build())
          .property(StringField.builder().name("surname").build())
          .property(ObjectField
              .builder()
              .name("address")
              .property(StringField.builder().name("street").build())
              .property(NumberField.builder().name("number").build())
              .build())
          .property(ArrayField
              .builder()
              .name("contactData")
              .value(ObjectField
                  .builder()
                  .property(BooleanField.builder().name("mobile").build())
                  .property(StringField.builder().name("email").build())
                  .build())
              .build())
          .build();

  public static final List<FieldValueMapping> EXPECTED_MEDIUM_ARRAY_FIELD_VALUE_MAPPING =
          asList(FieldValueMapping.builder().fieldName("name").fieldType("string").constrains(STRING_DEFAULTS).build(),
                 FieldValueMapping.builder().fieldName("surname").fieldType("string").constrains(STRING_DEFAULTS).build(),
                 FieldValueMapping.builder().fieldName("address.street").fieldType("string").constrains(STRING_DEFAULTS).build(),
                 FieldValueMapping.builder().fieldName("address.number").fieldType("number").build(),
                 FieldValueMapping.builder().fieldName("contactData[].mobile").fieldType("boolean").build(),
                 FieldValueMapping.builder().fieldName("contactData[].email").fieldType("string").constrains(STRING_DEFAULTS).build()
          );

  public static final List<FieldValueMapping> DEFINED_MEDIUM_ARRAY_FIELD_VALUE_MAPPING =
          asList(FieldValueMapping.builder().fieldName("name").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("name").build(),
                 FieldValueMapping.builder().fieldName("surname").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("surname").build(),
                 FieldValueMapping.builder().fieldName("address.street").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("street").build(),
                 FieldValueMapping.builder().fieldName("address.number").fieldType("number").fieldValueList("0").build(),
                 FieldValueMapping.builder().fieldName("contactData[1].mobile").fieldType("boolean").fieldValueList("true").build(),
                 FieldValueMapping.builder().fieldName("contactData[].email").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("email").build()
          );

  public static final Schema MEDIUM_SIMPLE_ARRAY_SCHEMA =
          Schema
                  .builder()
                  .property(StringField.builder().name("name").build())
                  .property(StringField.builder().name("surname").build())
                  .property(ObjectField
                                 .builder()
                                 .name("address")
                                 .property(StringField.builder().name("street").build())
                                 .property(NumberField.builder().name("number").build())
                                 .build())
                  .property(ArrayField
                                 .builder()
                                 .name("contactData")
                                 .value(StringField.builder().build())
                                 .build())
                  .build();

  public static final List<FieldValueMapping> EXPECTED_MEDIUM_SIMPLE_ARRAY_FIELD_VALUE_MAPPING =
          asList(FieldValueMapping.builder().fieldName("name").fieldType("string").constrains(STRING_DEFAULTS).build(),
                 FieldValueMapping.builder().fieldName("surname").fieldType("string").constrains(STRING_DEFAULTS).build(),
                 FieldValueMapping.builder().fieldName("address.street").fieldType("string").constrains(STRING_DEFAULTS).build(),
                 FieldValueMapping.builder().fieldName("address.number").fieldType("number").build(),
                 FieldValueMapping.builder().fieldName("contactData[]").fieldType("string-array").build()
          );

  public static final List<FieldValueMapping> DEFINED_MEDIUM_SIMPLE_ARRAY_FIELD_VALUE_MAPPING =
          asList(FieldValueMapping.builder().fieldName("name").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("name").build(),
                 FieldValueMapping.builder().fieldName("surname").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("surname").build(),
                 FieldValueMapping.builder().fieldName("address.street").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("street").build(),
                 FieldValueMapping.builder().fieldName("address.number").fieldType("number").fieldValueList("0").build(),
                 FieldValueMapping.builder().fieldName("contactData[1]").fieldType("string-array").fieldValueList("contactData").build()
          );

  public static final Schema MEDIUM_ENUM_SCHEMA =
      Schema
          .builder()
          .property(StringField.builder().name("name").build())
          .property(StringField.builder().name("surname").build())
          .property(ObjectField
              .builder()
              .name("address")
              .property(StringField.builder().name("street").build())
              .property(NumberField.builder().name("number").build())
              .build())
          .property(ObjectField
              .builder()
              .name("eye_color")
              .property(EnumField
              .builder()
              .enumValues(asList("brown", "blue", "green"))
              .defaultValue("brown")
              .build())
              .build())
          .build();

  public static final List<FieldValueMapping> EXPECTED_MEDIUM_ENUM_FIELD_VALUE_MAPPING =
          asList(FieldValueMapping.builder().fieldName("name").fieldType("string").constrains(STRING_DEFAULTS).build(),
                 FieldValueMapping.builder().fieldName("surname").fieldType("string").constrains(STRING_DEFAULTS).build(),
                 FieldValueMapping.builder().fieldName("address.street").fieldType("string").constrains(STRING_DEFAULTS).build(),
                 FieldValueMapping.builder().fieldName("address.number").fieldType("number").build(),
                 FieldValueMapping.builder().fieldName("eye_color").fieldType("enum").fieldValueList("brown,blue,green").build()
          );

  public static final List<FieldValueMapping> DEFINED_MEDIUM_ENUM_FIELD_VALUE_MAPPING =
          asList(FieldValueMapping.builder().fieldName("name").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("name").build(),
                 FieldValueMapping.builder().fieldName("surname").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("surname").build(),
                 FieldValueMapping.builder().fieldName("address.street").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("street").build(),
                 FieldValueMapping.builder().fieldName("address.number").fieldType("number").fieldValueList("0").build(),
                 FieldValueMapping.builder().fieldName("eye_color").fieldType("enum").fieldValueList("brown").build()
          );

  public static final Schema MEDIUM_MAP_SCHEMA =
          Schema
                  .builder()
                  .property(StringField.builder().name("name").build())
                  .property(StringField.builder().name("surname").build())
                  .property(ObjectField
                                 .builder()
                                 .name("address")
                                 .property(StringField.builder().name("street").build())
                                 .property(NumberField.builder().name("number").build())
                                 .build())
                  .property(MapField
                                 .builder()
                                 .name("contactData")
                                 .mapType(ObjectField
                                                .builder()
                                                .property(BooleanField.builder().name("mobile").build())
                                                .property(StringField.builder().name("email").build())
                                                .build())
                                 .build())
                  .build();

  public static final List<FieldValueMapping> EXPECTED_MEDIUM_MAP_FIELD_VALUE_MAPPING =
    asList(FieldValueMapping.builder().fieldName("name").fieldType("string").constrains(STRING_DEFAULTS).build(),
           FieldValueMapping.builder().fieldName("surname").fieldType("string").constrains(STRING_DEFAULTS).build(),
           FieldValueMapping.builder().fieldName("address.street").fieldType("string").constrains(STRING_DEFAULTS).build(),
           FieldValueMapping.builder().fieldName("address.number").fieldType("number").build(),
           FieldValueMapping.builder().fieldName("contactData[][].mobile").fieldType("boolean").build(),
           FieldValueMapping.builder().fieldName("contactData[][].email").fieldType("string").constrains(STRING_DEFAULTS).build()
    );

  public static final List<FieldValueMapping> DEFINED_MEDIUM_MAP_FIELD_VALUE_MAPPING =
    asList(FieldValueMapping.builder().fieldName("name").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("name").build(),
           FieldValueMapping.builder().fieldName("surname").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("surname").build(),
           FieldValueMapping.builder().fieldName("address.street").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("street").build(),
           FieldValueMapping.builder().fieldName("address.number").fieldType("number").fieldValueList("0").build(),
           FieldValueMapping.builder().fieldName("contactData[1][].mobile").fieldType("boolean").fieldValueList("true").build(),
           FieldValueMapping.builder().fieldName("contactData[1][].email").fieldType("string").constrains(STRING_DEFAULTS).fieldValueList("email").build()
    );
}
