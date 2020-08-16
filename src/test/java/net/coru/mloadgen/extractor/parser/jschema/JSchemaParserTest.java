package net.coru.mloadgen.extractor.parser.jschema;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import net.coru.mloadgen.extractor.parser.util.ResourceAsFile;
import net.coru.mloadgen.model.json.Schema;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


import static net.coru.mloadgen.extractor.parser.jschema.data.Fixtures.SIMPLE_SCHEMA;

class JSchemaParserTest {

	private static ResourceAsFile resourceAsFile = new ResourceAsFile();

	private JSchemaParser jSchemaParser = new JSchemaParser();

	private static Stream<Arguments> parametersForShouldParseJSchemaDocument() throws Exception {
		return Stream.of(
						Arguments.of(resourceAsFile.getContent("simpleSchema.jcs"), SIMPLE_SCHEMA)
		);
	}

	@ParameterizedTest
	@MethodSource("parametersForShouldParseJSchemaDocument")
	public void shouldParseJSchemaDocument(String schemaAsJson, Schema expected) {

		Schema result = jSchemaParser.parse(schemaAsJson);

		assertThat(result).isEqualTo(expected);
	}
}