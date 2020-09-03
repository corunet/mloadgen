package net.coru.mloadgen.extractor.data;

import java.util.List;
import net.coru.mloadgen.model.FieldValueMapping;
import net.coru.mloadgen.model.json.ArrayField;
import net.coru.mloadgen.model.json.BooleanField;
import net.coru.mloadgen.model.json.EnumField;
import net.coru.mloadgen.model.json.MapField;
import net.coru.mloadgen.model.json.NumberField;
import net.coru.mloadgen.model.json.ObjectField;
import net.coru.mloadgen.model.json.Schema;
import net.coru.mloadgen.model.json.StringField;


import static java.util.Arrays.asList;

public class Fixtures {

  public static final Schema SIMPLE_SCHEMA =
    Schema
      .builder()
      .property(StringField.builder().name("name").build())
      .property(StringField.builder().name("surname").build())
      .build();

  public static final List<FieldValueMapping> EXPECTED_SIMPLE_FIELD_VALUE_MAPPING =
          asList(new FieldValueMapping("name", "string"),
                 new FieldValueMapping("surname", "string"));

  public static final List<FieldValueMapping> DEFINED_SIMPLE_FIELD_VALUE_MAPPING =
          asList(new FieldValueMapping("name", "string", 0, "name"),
                 new FieldValueMapping("surname", "string", 0, "surname"));

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
          asList(new FieldValueMapping("name", "string"),
                 new FieldValueMapping("surname", "string"),
                 new FieldValueMapping("address.street", "string"),
                 new FieldValueMapping("address.number", "number")
                 );

  public static final List<FieldValueMapping> DEFINED_MEDIUM_FIELD_VALUE_MAPPING =
          asList(new FieldValueMapping("name", "string", 0, "name"),
                 new FieldValueMapping("surname", "string", 0, "surname"),
                 new FieldValueMapping("address.street", "string", 0, "address"),
                 new FieldValueMapping("address.number", "number", 0, "0")
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
          asList(new FieldValueMapping("name", "string"),
                 new FieldValueMapping("surname", "string"),
                 new FieldValueMapping("address.street", "string"),
                 new FieldValueMapping("address.number", "number"),
                 new FieldValueMapping("contactData[].mobile", "boolean"),
                 new FieldValueMapping("contactData[].email", "string")
          );

  public static final List<FieldValueMapping> DEFINED_MEDIUM_ARRAY_FIELD_VALUE_MAPPING =
          asList(new FieldValueMapping("name", "string", 0, "name"),
                 new FieldValueMapping("surname", "string", 0, "surname"),
                 new FieldValueMapping("address.street", "string", 0, "street"),
                 new FieldValueMapping("address.number", "number", 0, "0"),
                 new FieldValueMapping("contactData[1].mobile", "boolean", 0, "true"),
                 new FieldValueMapping("contactData[].email", "string", 0, "email")
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
          asList(new FieldValueMapping("name", "string"),
                 new FieldValueMapping("surname", "string"),
                 new FieldValueMapping("address.street", "string"),
                 new FieldValueMapping("address.number", "number"),
                 new FieldValueMapping("contactData[]", "string-array")
          );

  public static final List<FieldValueMapping> DEFINED_MEDIUM_SIMPLE_ARRAY_FIELD_VALUE_MAPPING =
          asList(new FieldValueMapping("name", "string", 0, "name"),
                 new FieldValueMapping("surname", "string", 0, "surname"),
                 new FieldValueMapping("address.street", "string", 0, "street"),
                 new FieldValueMapping("address.number", "number", 0, "0"),
                 new FieldValueMapping("contactData[1]", "string-array", 0, "contactData")
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
          asList(new FieldValueMapping("name", "string"),
                 new FieldValueMapping("surname", "string"),
                 new FieldValueMapping("address.street", "string"),
                 new FieldValueMapping("address.number", "number"),
                 new FieldValueMapping("eye_color", "enum")
          );

  public static final List<FieldValueMapping> DEFINED_MEDIUM_ENUM_FIELD_VALUE_MAPPING =
          asList(new FieldValueMapping("name", "string", 0, "name"),
                 new FieldValueMapping("surname", "string", 0, "surname"),
                 new FieldValueMapping("address.street", "string", 0, "street"),
                 new FieldValueMapping("address.number", "number", 0, "0"),
                 new FieldValueMapping("eye_color", "enum", 0, "brown")
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
    asList(new FieldValueMapping("name", "string"),
           new FieldValueMapping("surname", "string"),
           new FieldValueMapping("address.street", "string"),
           new FieldValueMapping("address.number", "number"),
           new FieldValueMapping("contactData[][].mobile", "boolean"),
           new FieldValueMapping("contactData[][].email", "string")
    );

  public static final List<FieldValueMapping> DEFINED_MEDIUM_MAP_FIELD_VALUE_MAPPING =
    asList(new FieldValueMapping("name", "string", 0, "name"),
           new FieldValueMapping("surname", "string", 0, "surname"),
           new FieldValueMapping("address.street", "string", 0, "street"),
           new FieldValueMapping("address.number", "number", 0, "0"),
           new FieldValueMapping("contactData[1][].mobile", "boolean", 0, "true"),
           new FieldValueMapping("contactData[1][].email", "string", 0, "email")
    );
}
