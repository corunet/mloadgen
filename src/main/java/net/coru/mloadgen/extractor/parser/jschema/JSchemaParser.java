package net.coru.mloadgen.extractor.parser.jschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.coru.mloadgen.extractor.parser.SchemaParser;
import net.coru.mloadgen.model.json.*;
import org.apache.commons.collections4.CollectionUtils;

public class JSchemaParser implements SchemaParser {

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public Schema parse(String jsonSchema) {
		List<Field> fields = new ArrayList<>();
		try {
			JsonNode jsonNode = mapper.readTree(jsonSchema);
			CollectionUtils.collect(jsonNode.fieldNames(),
			                        fieldName -> buildProperty(fieldName, jsonNode.get(fieldName)),
			                        fields);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Schema.builder().properties(fields).build();
	}

	private Field buildProperty(String fieldName, JsonNode jsonNode) {
		Field result = null;
		if (jsonNode.getNodeType().compareTo(JsonNodeType.STRING) == 0) {
			result = buildSimpleField(fieldName, jsonNode.textValue());
		} else if (jsonNode.getNodeType().compareTo(JsonNodeType.ARRAY) == 0) {
			if ("enum".equalsIgnoreCase(fieldName)) {
				List<String> valuesList = new ArrayList<>();
				CollectionUtils.collect(jsonNode.elements(), JsonNode::textValue, valuesList);
				result = EnumField.builder().enumValues(valuesList).defaultValue(valuesList.get(0)).build();
			} else {
				result = buildArray(fieldName, jsonNode);
			}
		} else if (Objects.nonNull(jsonNode.get("map_of"))) {
			result = MapField.builder().name(fieldName).mapType(buildObject(jsonNode.get("map_of"))).build();
		} else if (jsonNode.getNodeType().compareTo(JsonNodeType.OBJECT) == 0) {
			result = buildObject(fieldName, jsonNode);
		}
		return result;
	}

	private Field buildSimpleField(String fieldName, String textValue) {
		Field field;
		switch(textValue) {
			case "int": field = IntegerField.builder().name(fieldName).build(); break;
			case "number": field = NumberField.builder().name(fieldName).build(); break;
			case "boolean": field = BooleanField.builder().name(fieldName).build(); break;
			case "date": field = DateField.builder().name(fieldName).format(textValue).build(); break;
			default: field = StringField.builder().name(fieldName).build(); break;
		}
		return field;
	}

	private Field buildObject(String fieldName, JsonNode objectNode) {
		List<Field> propertyList = new ArrayList<>();
		CollectionUtils.collect(objectNode.fields(),
		                        property -> buildProperty(property.getKey(), property.getValue()),
		                        propertyList);
		return ObjectField.builder().name(fieldName).properties(propertyList).build();
	}

	private Field buildObject(JsonNode objectNode) {
		List<Field> propertyList = new ArrayList<>();
		CollectionUtils.collect(objectNode.fields(),
				property -> buildProperty(property.getKey(), property.getValue()),
				propertyList);
		return ObjectField.builder().properties(propertyList).build();
	}

	private Field buildArray(String fieldName, JsonNode objectNode) {
		List<Field> propertyList = new ArrayList<>();
		CollectionUtils.collect(objectNode.elements(),
				this::buildObject,
				propertyList);
		return ArrayField.builder().name(fieldName).values(propertyList).build();
	}
}
