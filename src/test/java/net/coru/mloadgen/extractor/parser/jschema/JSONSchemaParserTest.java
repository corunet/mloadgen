package net.coru.mloadgen.extractor.parser.jschema;

import java.util.stream.Stream;
import net.coru.mloadgen.extractor.parser.util.ResourceAsFile;
import net.coru.mloadgen.model.json.Schema;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


import static net.coru.mloadgen.extractor.parser.jschema.data.JsonSchemaFixtures.SIMPLE_SCHEMA;
import static net.coru.mloadgen.extractor.parser.jschema.data.JsonSchemaFixtures.SIMPLE_SCHEMA_ARRAY;
import static net.coru.mloadgen.extractor.parser.jschema.data.JsonSchemaFixtures.SIMPLE_SCHEMA_NUMBER;
import static org.assertj.core.api.Assertions.assertThat;

class JSONSchemaParserTest {

  private static ResourceAsFile resourceAsFile = new ResourceAsFile();

  private static JSONSchemaParser jsonSchemaParser = new JSONSchemaParser();

  private static Stream<Arguments> parametersForShouldParseJSONSchemaDocument() throws Exception {
    return Stream.of(
            Arguments.of(resourceAsFile.getContent("/jsonschema/basic.jcs"), SIMPLE_SCHEMA),
            Arguments.of(resourceAsFile.getContent("/jsonschema/basic-number.jcs"), SIMPLE_SCHEMA_NUMBER),
            Arguments.of(resourceAsFile.getContent("/jsonschema/basic-array.jcs"), SIMPLE_SCHEMA_ARRAY)
    );
  }

  @ParameterizedTest
  @MethodSource("parametersForShouldParseJSONSchemaDocument")
  void shouldParseJSONSchemaDocument(String schemaAsJson, Schema expected) {

    Schema result = jsonSchemaParser.parse(schemaAsJson);

    assertThat(result).isEqualTo(expected);
  }
}