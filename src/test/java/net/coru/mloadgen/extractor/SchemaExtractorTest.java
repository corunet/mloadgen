package net.coru.mloadgen.extractor;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Stream;
import net.coru.mloadgen.extractor.impl.SchemaExtractorImpl;
import net.coru.mloadgen.extractor.parser.util.ResourceAsFile;
import net.coru.mloadgen.model.FieldValueMapping;
import net.coru.mloadgen.model.json.Schema;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static java.util.Collections.singletonList;
import static net.coru.mloadgen.extractor.data.FixturesConstants.EXPECTED_MEDIUM_ARRAY_FIELD_VALUE_MAPPING;
import static net.coru.mloadgen.extractor.data.FixturesConstants.EXPECTED_MEDIUM_ENUM_FIELD_VALUE_MAPPING;
import static net.coru.mloadgen.extractor.data.FixturesConstants.EXPECTED_MEDIUM_FIELD_VALUE_MAPPING;
import static net.coru.mloadgen.extractor.data.FixturesConstants.EXPECTED_MEDIUM_MAP_FIELD_VALUE_MAPPING;
import static net.coru.mloadgen.extractor.data.FixturesConstants.EXPECTED_MEDIUM_SIMPLE_ARRAY_FIELD_VALUE_MAPPING;
import static net.coru.mloadgen.extractor.data.FixturesConstants.EXPECTED_SIMPLE_FIELD_VALUE_MAPPING;
import static net.coru.mloadgen.extractor.data.FixturesConstants.MEDIUM_ARRAY_SCHEMA;
import static net.coru.mloadgen.extractor.data.FixturesConstants.MEDIUM_ENUM_SCHEMA;
import static net.coru.mloadgen.extractor.data.FixturesConstants.MEDIUM_MAP_SCHEMA;
import static net.coru.mloadgen.extractor.data.FixturesConstants.MEDIUM_SCHEMA;
import static net.coru.mloadgen.extractor.data.FixturesConstants.MEDIUM_SIMPLE_ARRAY_SCHEMA;
import static net.coru.mloadgen.extractor.data.FixturesConstants.SIMPLE_SCHEMA;
import static org.assertj.core.api.Assertions.assertThat;

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
  void shouldFlatPropertiesListReturnListFieldValueMapping(Schema schema, List<FieldValueMapping> expected) {
    List<FieldValueMapping> result = schemaExtractor.flatPropertiesList(schema);
    assertThat(result).hasSameElementsAs(expected);
  }

  private static Stream<Arguments> parametersForSchemaTypesList() throws URISyntaxException {
    return Stream.of(
        Arguments.of("JSchema", resourceAsFile.getFile("/jschema/simpleSchema.jcs"), singletonList(SIMPLE_SCHEMA)),
        Arguments.of("JSchema", resourceAsFile.getFile("/jschema/mediumSchema.jcs"), singletonList(MEDIUM_SCHEMA)),
        Arguments.of("JSchema", resourceAsFile.getFile("/jschema/mediumArraySchema.jcs"), singletonList(MEDIUM_ARRAY_SCHEMA)),
        Arguments.of("JSchema", resourceAsFile.getFile("/jschema/mediumEnumSchema.jcs"), singletonList(MEDIUM_ENUM_SCHEMA)),
        Arguments.of("JSchema", resourceAsFile.getFile("/jschema/mediumMapSchema.jcs"), singletonList(MEDIUM_MAP_SCHEMA))
    );
  }

  @MethodSource("parametersForSchemaTypesList")
  @ParameterizedTest
  void shouldSchemaTypesListReturnAListOfSchemas(String schemaType, File schemaFile, List<Schema> expected) throws IOException {

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
  void shouldProcessSchema(Schema schema, List<FieldValueMapping> expected) {
    List<FieldValueMapping> result = schemaExtractor.processSchema(schema);
    assertThat(result).hasSameElementsAs(expected);

  }
}