package net.coru.mloadgen.extractor;

import java.util.List;
import java.util.stream.Stream;
import net.coru.mloadgen.model.FieldValueMapping;
import net.coru.mloadgen.model.json.Schema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


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

class SchemaExtractorTest {

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
  void flatPropertiesList(Schema schema, List<FieldValueMapping> expected) {
    List<FieldValueMapping> result = schemaExtractor.flatPropertiesList(schema);
   // assertThat(result).hasSameSizeAs(expected);
    assertThat(result).hasSameElementsAs(expected);
  }

  @Test
  void schemaTypesList() {
  }

  @Test
  void processSchema() {
  }
}