package net.coru.mloadgen.extractor.parser.jschema;

import static net.coru.mloadgen.extractor.parser.jschema.data.JSchemaFixturesConstants.MEDIUM_ARRAY_SCHEMA;
import static net.coru.mloadgen.extractor.parser.jschema.data.JSchemaFixturesConstants.MEDIUM_ENUM_SCHEMA;
import static net.coru.mloadgen.extractor.parser.jschema.data.JSchemaFixturesConstants.MEDIUM_MAP_SCHEMA;
import static net.coru.mloadgen.extractor.parser.jschema.data.JSchemaFixturesConstants.MEDIUM_SCHEMA;
import static net.coru.mloadgen.extractor.parser.jschema.data.JSchemaFixturesConstants.SIMPLE_SCHEMA;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import net.coru.mloadgen.extractor.parser.util.ResourceAsFile;
import net.coru.mloadgen.model.json.Schema;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class JSchemaParserTest {

	private static ResourceAsFile resourceAsFile = new ResourceAsFile();

	private JSchemaParser jSchemaParser = new JSchemaParser();

	private static Stream<Arguments> parametersForShouldParseJSchemaDocument() throws Exception {
		return Stream.of(
						Arguments.of(resourceAsFile.getContent("/jschema/simpleSchema.jcs"), SIMPLE_SCHEMA),
						Arguments.of(resourceAsFile.getContent("/jschema/mediumSchema.jcs"), MEDIUM_SCHEMA),
						Arguments.of(resourceAsFile.getContent("/jschema/mediumArraySchema.jcs"), MEDIUM_ARRAY_SCHEMA),
						Arguments.of(resourceAsFile.getContent("/jschema/mediumEnumSchema.jcs"), MEDIUM_ENUM_SCHEMA),
						Arguments.of(resourceAsFile.getContent("/jschema/mediumMapSchema.jcs"), MEDIUM_MAP_SCHEMA)
				);
	}

	@ParameterizedTest
	@MethodSource("parametersForShouldParseJSchemaDocument")
	void shouldParseJSchemaDocument(String schemaAsJson, Schema expected) {

		Schema result = jSchemaParser.parse(schemaAsJson);

		assertThat(result).isEqualTo(expected);
	}
}