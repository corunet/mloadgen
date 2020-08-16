package net.coru.mloadgen.extractor.parser.jschema.data;

import net.coru.mloadgen.model.json.ObjectField;
import net.coru.mloadgen.model.json.Schema;
import net.coru.mloadgen.model.json.StringField;


import static java.util.Arrays.asList;

public class Fixtures {
  public static Schema SIMPLE_SCHEMA =
    Schema
      .builder()
      .field(
        ObjectField
          .builder()
          .properties(
            asList(
              StringField.builder().name("name").build(),
              StringField.builder().name("surname").build()
          ))
      )
      .build();
}
