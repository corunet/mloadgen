/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.coru.mloadgen.processor;

import static net.coru.mloadgen.extractor.data.FixturesConstants.DEFINED_MEDIUM_ARRAY_FIELD_VALUE_MAPPING;
import static net.coru.mloadgen.extractor.data.FixturesConstants.DEFINED_MEDIUM_ENUM_FIELD_VALUE_MAPPING;
import static net.coru.mloadgen.extractor.data.FixturesConstants.DEFINED_MEDIUM_FIELD_VALUE_MAPPING;
import static net.coru.mloadgen.extractor.data.FixturesConstants.DEFINED_MEDIUM_MAP_FIELD_VALUE_MAPPING;
import static net.coru.mloadgen.extractor.data.FixturesConstants.DEFINED_MEDIUM_SIMPLE_ARRAY_FIELD_VALUE_MAPPING;
import static net.coru.mloadgen.extractor.data.FixturesConstants.DEFINED_SIMPLE_FIELD_VALUE_MAPPING;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;

import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import net.coru.mloadgen.extractor.parser.util.ResourceAsFile;
import net.coru.mloadgen.model.FieldValueMapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Slf4j
class JsonSchemaProcessorTest {

  private static ResourceAsFile resourceAsFile = new ResourceAsFile();

  private JsonSchemaProcessor processor;

  @BeforeEach
  public void setUp() {
    processor = new JsonSchemaProcessor();
  }

  private static Stream<Arguments> parametersForFlatPropertiesList() throws Exception {
    return Stream.of(
            Arguments.of(DEFINED_SIMPLE_FIELD_VALUE_MAPPING, resourceAsFile.getContent("/jsonexpected/simpleJson.json")),
            Arguments.of(DEFINED_MEDIUM_FIELD_VALUE_MAPPING, resourceAsFile.getContent("/jsonexpected/mediumJson.json")),
            Arguments.of(DEFINED_MEDIUM_ARRAY_FIELD_VALUE_MAPPING, resourceAsFile.getContent("/jsonexpected/mediumArrayFieldJson.json")),
            Arguments.of(DEFINED_MEDIUM_SIMPLE_ARRAY_FIELD_VALUE_MAPPING, resourceAsFile.getContent("/jsonexpected/simpleArrayFieldJson.json")),
            Arguments.of(DEFINED_MEDIUM_ENUM_FIELD_VALUE_MAPPING, resourceAsFile.getContent("/jsonexpected/mediumEnumJson.json")),
            Arguments.of(DEFINED_MEDIUM_MAP_FIELD_VALUE_MAPPING, resourceAsFile.getContent("/jsonexpected/mediumMapJson.json"))
    );
  }

  @MethodSource("parametersForFlatPropertiesList")
  @ParameterizedTest
  void processSchema(List<FieldValueMapping> fieldValueMapping, String expected) {
    processor.processSchema(fieldValueMapping);
    String result = processor.next().toString();
    log.debug(result);
    assertThatJson(result).isEqualTo(expected);
  }
}