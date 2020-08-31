package net.coru.mloadgen.extractor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Stream;
import net.coru.mloadgen.extractor.parser.util.ResourceAsFile;
import net.coru.mloadgen.model.FieldValueMapping;
import net.coru.mloadgen.model.json.Schema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static java.util.Collections.singletonList;
import static net.coru.mloadgen.extractor.data.Fixtures.EXPECTED_MEDIUM_ARRAY_FIELD_VALUE_MAPPING;
import static net.coru.mloadgen.extractor.data.Fixtures.EXPECTED_MEDIUM_ENUM_FIELD_VALUE_MAPPING;
import static net.coru.mloadgen.extractor.data.Fixtures.EXPECTED_MEDIUM_FIELD_VALUE_MAPPING;
import static net.coru.mloadgen.extractor.data.Fixtures.EXPECTED_MEDIUM_MAP_FIELD_VALUE_MAPPING;
import static net.coru.mloadgen.extractor.data.Fixtures.EXPECTED_MEDIUM_SIMPLE_ARRAY_FIELD_VALUE_MAPPING;
import static net.coru.mloadgen.extractor.data.Fixtures.EXPECTED_SIMPLE_FIELD_VALUE_MAPPING;
import static net.coru.mloadgen.extractor.data.Fixtures.MEDIUM_ARRAY_SCHEMA;
import static net.coru.mloadgen.extractor.data.Fixtures.MEDIUM_ENUM_SCHEMA;
import static net.coru.mloadgen.extractor.data.Fixtures.MEDIUM_MAP_SCHEMA;
import static net.coru.mloadgen.extractor.data.Fixtures.MEDIUM_SCHEMA;
import static net.coru.mloadgen.extractor.data.Fixtures.MEDIUM_SIMPLE_ARRAY_SCHEMA;
import static net.coru.mloadgen.extractor.data.Fixtures.SIMPLE_SCHEMA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Arrays.asList;

class SchemaExtractorTest {

  private static ResourceAsFile resourceAsFile = new ResourceAsFile();

  private SchemaExtractor schemaExtractor = new SchemaExtractorImpl();

  private static Stream<Arguments> parametersForFlatPropertiesList() {
    return Stream.of(
      Arguments.of(SIMPLE_SCHEMA, EXPECTED_SIMPLE_FIELD_VALUE_MAPPING),
      Arguments.of(MEDIUM_SCHEMA, EXPECTED_MEDIUM_FIELD_VALUE_MAPPING),
      Arguments.of(MEDIUM_ARRAY_SCHEMA, EXPECTED_MEDIUM_ARRAY_FIELD_VALUE_MAPPING),
      Arguments.of(MEDIUM_SIMPLE_ARRAY_SCHEMA, EXPECTED_MEDIUM_SIMPLE_ARRAY_FIELD_VALUE_MAPPING),
      Arguments.of(MEDIUM_ENUM_SCHEMA, EXPECTED_MEDIUM_ENUM_FIELD_VALUE_MAPPING),
      Arguments.of(MEDIUM_MAP_SCHEMA, EXPECTED_MEDIUM_MAP_FIELD_VALUE_MAPPING)
    );
  }

  @MethodSource("parametersForFlatPropertiesList")
  @ParameterizedTest
  void should_flatPropertiesList_ReturnListFieldValueMapping(Schema schema, List<FieldValueMapping> expected) {
    List<FieldValueMapping> result = schemaExtractor.flatPropertiesList(schema);
    assertThat(result).hasSameElementsAs(expected);
  }

  private static Stream<Arguments> parametersForSchemaTypesList() throws URISyntaxException {
    return Stream.of(
        Arguments.of("JSchema", resourceAsFile.getFile("/jsonschema/simpleSchema.jcs"), singletonList(SIMPLE_SCHEMA)),
        Arguments.of("JSchema", resourceAsFile.getFile("/jsonschema/mediumSchema.jcs"), singletonList(MEDIUM_SCHEMA)),
        Arguments.of("JSchema", resourceAsFile.getFile("/jsonschema/mediumArraySchema.jcs"), singletonList(MEDIUM_ARRAY_SCHEMA)),
        Arguments.of("JSchema", resourceAsFile.getFile("/jsonschema/mediumEnumSchema.jcs"), singletonList(MEDIUM_ENUM_SCHEMA)),
        Arguments.of("JSchema", resourceAsFile.getFile("/jsonschema/mediumMapSchema.jcs"), singletonList(MEDIUM_MAP_SCHEMA))
    );
  }

  @MethodSource("parametersForSchemaTypesList")
  @ParameterizedTest
  void should_schemaTypesList_ReturnAListOfSchemas(String schemaType, File schemaFile, List<Schema> expected) throws IOException {

    List<Schema> result = schemaExtractor.schemaTypesList(schemaType, schemaFile);
    assertThat(result).hasSameElementsAs(expected);
  }

  private static Stream<Arguments> parametersForProcessSchema() {
    return Stream.of(
        Arguments.of(SIMPLE_SCHEMA, EXPECTED_SIMPLE_FIELD_VALUE_MAPPING),
        Arguments.of(MEDIUM_SCHEMA, EXPECTED_MEDIUM_FIELD_VALUE_MAPPING),
        Arguments.of(MEDIUM_ARRAY_SCHEMA, EXPECTED_MEDIUM_ARRAY_FIELD_VALUE_MAPPING),
        Arguments.of(MEDIUM_SIMPLE_ARRAY_SCHEMA, EXPECTED_MEDIUM_SIMPLE_ARRAY_FIELD_VALUE_MAPPING),
        Arguments.of(MEDIUM_ENUM_SCHEMA, EXPECTED_MEDIUM_ENUM_FIELD_VALUE_MAPPING),
        Arguments.of(MEDIUM_MAP_SCHEMA, EXPECTED_MEDIUM_MAP_FIELD_VALUE_MAPPING)
    );
  }

  @MethodSource("parametersForProcessSchema")
  @ParameterizedTest
  void should_processSchema(Schema schema, List<FieldValueMapping> expected) {
    List<FieldValueMapping> result = schemaExtractor.processSchema(schema);
    assertThat(result).hasSameElementsAs(expected);

  }
}