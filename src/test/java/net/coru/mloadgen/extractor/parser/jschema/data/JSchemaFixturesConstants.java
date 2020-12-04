package net.coru.mloadgen.extractor.parser.jschema.data;

import net.coru.mloadgen.model.json.ArrayField;
import net.coru.mloadgen.model.json.BooleanField;
import net.coru.mloadgen.model.json.EnumField;
import net.coru.mloadgen.model.json.MapField;
import net.coru.mloadgen.model.json.NumberField;
import net.coru.mloadgen.model.json.ObjectField;
import net.coru.mloadgen.model.json.Schema;
import net.coru.mloadgen.model.json.StringField;


import static java.util.Arrays.asList;

public class JSchemaFixturesConstants {

  public static final Schema SIMPLE_SCHEMA =
    Schema
      .builder()
      .property(StringField.builder().name("name").build())
      .property(StringField.builder().name("surname").build())
      .build();

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
}
