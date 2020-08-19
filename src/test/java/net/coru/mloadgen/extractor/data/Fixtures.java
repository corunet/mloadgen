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
      .field(StringField.builder().name("name").build())
      .field(StringField.builder().name("surname").build())
      .build();

  public static final List<FieldValueMapping> EXPECTED_SIMPLE_FIELD_VALUE_MAPPING =
          asList(new FieldValueMapping("name", "STRING"),
                 new FieldValueMapping("surname", "STRING"));

  public static final Schema MEDIUM_SCHEMA =
      Schema
          .builder()
          .field(StringField.builder().name("name").build())
          .field(StringField.builder().name("surname").build())
          .field(ObjectField
              .builder()
              .name("address")
              .property(StringField.builder().name("street").build())
              .property(NumberField.builder().name("number").build())
              .build())
          .build();

  public static final List<FieldValueMapping> EXPECTED_MEDIUM_FIELD_VALUE_MAPPING =
          asList(new FieldValueMapping("name", "STRING"),
                 new FieldValueMapping("surname", "STRING"),
                 new FieldValueMapping("address.street", "STRING"),
                 new FieldValueMapping("address.number", "NUMBER")
                 );

  public static final Schema MEDIUM_ARRAY_SCHEMA =
      Schema
          .builder()
          .field(StringField.builder().name("name").build())
          .field(StringField.builder().name("surname").build())
          .field(ObjectField
              .builder()
              .name("address")
              .property(StringField.builder().name("street").build())
              .property(NumberField.builder().name("number").build())
              .build())
          .field(ArrayField
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
          asList(new FieldValueMapping("name", "STRING"),
                 new FieldValueMapping("surname", "STRING"),
                 new FieldValueMapping("address.street", "STRING"),
                 new FieldValueMapping("address.number", "NUMBER"),
                 new FieldValueMapping("contactData[].mobile", "BOOLEAN"),
                 new FieldValueMapping("contactData[].email", "STRING")
          );

  public static final Schema MEDIUM_SIMPLE_ARRAY_SCHEMA =
          Schema
                  .builder()
                  .field(StringField.builder().name("name").build())
                  .field(StringField.builder().name("surname").build())
                  .field(ObjectField
                                 .builder()
                                 .name("address")
                                 .property(StringField.builder().name("street").build())
                                 .property(NumberField.builder().name("number").build())
                                 .build())
                  .field(ArrayField
                                 .builder()
                                 .name("contactData")
                                 .value(StringField.builder().build())
                                 .build())
                  .build();

  public static final List<FieldValueMapping> EXPECTED_MEDIUM_SIMPLE_ARRAY_FIELD_VALUE_MAPPING =
          asList(new FieldValueMapping("name", "STRING"),
                 new FieldValueMapping("surname", "STRING"),
                 new FieldValueMapping("address.street", "STRING"),
                 new FieldValueMapping("address.number", "NUMBER"),
                 new FieldValueMapping("contactData[]", "STRING-array")
          );

  public static final Schema MEDIUM_ENUM_SCHEMA =
      Schema
          .builder()
          .field(StringField.builder().name("name").build())
          .field(StringField.builder().name("surname").build())
          .field(ObjectField
              .builder()
              .name("address")
              .property(StringField.builder().name("street").build())
              .property(NumberField.builder().name("number").build())
              .build())
          .field(ObjectField
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
          asList(new FieldValueMapping("name", "STRING"),
                 new FieldValueMapping("surname", "STRING"),
                 new FieldValueMapping("address.street", "STRING"),
                 new FieldValueMapping("address.number", "NUMBER"),
                 new FieldValueMapping("eye_color", "ENUM")
          );

  public static final Schema MEDIUM_MAP_SCHEMA =
          Schema
                  .builder()
                  .field(StringField.builder().name("name").build())
                  .field(StringField.builder().name("surname").build())
                  .field(ObjectField
                                 .builder()
                                 .name("address")
                                 .property(StringField.builder().name("street").build())
                                 .property(NumberField.builder().name("number").build())
                                 .build())
                  .field(MapField
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
    asList(new FieldValueMapping("name", "STRING"),
           new FieldValueMapping("surname", "STRING"),
           new FieldValueMapping("address.street", "STRING"),
           new FieldValueMapping("address.number", "NUMBER"),
           new FieldValueMapping("contactData[][].mobile", "BOOLEAN"),
           new FieldValueMapping("contactData[][].email", "STRING")
    );
}
