package net.coru.mloadgen.extractor.parser.jschema.data;

import net.coru.mloadgen.model.json.ArrayField;
import net.coru.mloadgen.model.json.BooleanField;
import net.coru.mloadgen.model.json.IntegerField;
import net.coru.mloadgen.model.json.NumberField;
import net.coru.mloadgen.model.json.ObjectField;
import net.coru.mloadgen.model.json.Schema;
import net.coru.mloadgen.model.json.StringField;

public class JsonSchemaFixtures {

  public static final Schema SIMPLE_SCHEMA =
          Schema.builder()
                .id("https://example.com/person.schema.json")
                .name("http://json-schema.org/draft-07/schema#")
                .type("object")
                .property(StringField.builder().name("firstName").build())
                .property(StringField.builder().name("lastName").build())
                .property(IntegerField.builder().name("age").build())
                .build();

  public static final Schema SIMPLE_SCHEMA_NUMBER =
          Schema.builder()
                .id("https://example.com/geographical-location.schema.json")
                .name("http://json-schema.org/draft-07/schema#")
                .type("object")
                .property(NumberField
                    .builder()
                    .name("latitude")
                    .minimum(-90)
                    .maximum(90)
                    .exclusiveMaximum(0)
                    .exclusiveMinimum(0)
                    .multipleOf(0)
                    .build())
                .property(NumberField
                    .builder()
                    .name("longitude")
                    .minimum(-180)
                    .maximum(180)
                    .exclusiveMinimum(0)
                    .exclusiveMaximum(0)
                    .multipleOf(0)
                    .build())
          .build();

  public static final Schema SIMPLE_SCHEMA_ARRAY =
          Schema.builder()
                .id("https://example.com/arrays.schema.json")
                .name("http://json-schema.org/draft-07/schema#")
                .type("object")
                .property(ArrayField.builder().name("fruits").value(StringField.builder().build()).build())
                .property(ArrayField
                      .builder()
                      .name("vegetables")
                      .value(ObjectField
                             .builder()
                             .property(StringField.builder().name("veggieName").build())
                             .property(BooleanField.builder().name("veggieLike").build())
                             .build())
                      .build())
                .description(ObjectField
                    .builder()
                    .name("veggie")
                    .property(StringField.builder().name("veggieName").build())
                    .property(BooleanField.builder().name("veggieLike").build())
                    .build())
                .build();
}
