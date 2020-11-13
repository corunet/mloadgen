package net.coru.mloadgen.extractor.parser.jschema;

import static net.coru.mloadgen.extractor.parser.jschema.data.JsonSchemaFixtures.COMPLEX_SCHEMA;
import static net.coru.mloadgen.extractor.parser.jschema.data.JsonSchemaFixtures.MEDIUM_COMPLEX_SCHEMA;
import static net.coru.mloadgen.extractor.parser.jschema.data.JsonSchemaFixtures.SIMPLE_SCHEMA;
import static net.coru.mloadgen.extractor.parser.jschema.data.JsonSchemaFixtures.SIMPLE_SCHEMA_ARRAY;
import static net.coru.mloadgen.extractor.parser.jschema.data.JsonSchemaFixtures.SIMPLE_SCHEMA_NUMBER;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.coru.mloadgen.extractor.parser.util.ResourceAsFile;
import net.coru.mloadgen.model.json.Field;
import net.coru.mloadgen.model.json.NumberField;
import net.coru.mloadgen.model.json.Schema;
import net.coru.mloadgen.model.json.StringField;
import net.coru.mloadgen.model.json.UUIDField;
import org.apache.commons.collections4.IterableUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class JSONSchemaParserTest {

  private static ResourceAsFile resourceAsFile = new ResourceAsFile();

  private static JSONSchemaParser jsonSchemaParser = new JSONSchemaParser();

  private static Stream<Arguments> parametersForShouldParseJSONSchemaDocument() throws Exception {
    return Stream.of(
        Arguments.of(resourceAsFile.getContent("/jsonschema/basic.jcs"), SIMPLE_SCHEMA),
        Arguments.of(resourceAsFile.getContent("/jsonschema/basic-number.jcs"), SIMPLE_SCHEMA_NUMBER),
        Arguments.of(resourceAsFile.getContent("/jsonschema/basic-array.jcs"), SIMPLE_SCHEMA_ARRAY),
        Arguments.of(resourceAsFile.getContent("/jsonschema/complex-document.jcs"), COMPLEX_SCHEMA),
        Arguments.of(resourceAsFile.getContent("/jsonschema/medium-document.jcs"), MEDIUM_COMPLEX_SCHEMA)
    );
  }

  @ParameterizedTest
  @MethodSource("parametersForShouldParseJSONSchemaDocument")
  void shouldParseJSONSchemaDocument(String schemaAsJson, Schema expected) {

    Schema result = jsonSchemaParser.parse(schemaAsJson);

    assertThat(result).isEqualTo(expected);
  }


  @Test
  void shouldParseJSONSchemaDocument() throws Exception {

    Schema result = jsonSchemaParser.parse(resourceAsFile.getContent("/jsonschema/multiple-type.jcs"));

    assertThat(result).extracting("properties").satisfies(this::multiTypeTestStringOrNumber)
        .satisfies(propertyNamesAre("id", "version", "dtype", "timestamp", "event_type"));
  }

  private Consumer<List<?>> propertyNamesAre(String... propertiesNames) {
    Set<String> propertyNames = Set.of(propertiesNames);
    return fields -> IterableUtils.matchesAll((List)fields.get(0), field-> propertyNames.contains(((Field)field).getName()));
  }

  private boolean multiTypeTestStringOrNumber(Object field) {
    return field instanceof StringField || field instanceof NumberField || field instanceof UUIDField;
  }

}