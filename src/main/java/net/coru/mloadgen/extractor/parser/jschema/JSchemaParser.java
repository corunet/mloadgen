package net.coru.mloadgen.extractor.parser.jschema;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.coru.mloadgen.extractor.parser.SchemaParser;
import net.coru.mloadgen.model.json.BooleanField;
import net.coru.mloadgen.model.json.DateField;
import net.coru.mloadgen.model.json.Field;
import net.coru.mloadgen.model.json.NumberField;
import net.coru.mloadgen.model.json.ObjectField;
import net.coru.mloadgen.model.json.Schema;
import net.coru.mloadgen.model.json.StringField;
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
		return Schema.builder().fields(fields).build();
	}

	private Field buildProperty(String fieldName, JsonNode jsonNode) {
		Field result = null;
		if (jsonNode.getNodeType().compareTo(JsonNodeType.STRING) == 0) {
			result = buildSimpleField(fieldName, jsonNode.textValue());
		} else if (jsonNode.getNodeType().compareTo(JsonNodeType.ARRAY) == 0) {
			result = buildObject(fieldName, jsonNode.elements().next());
		} else  if (jsonNode.getNodeType().compareTo(JsonNodeType.OBJECT) == 0) {
			result = buildObject(fieldName, jsonNode.elements().next());
		}
		return result;
	}

	private Field buildSimpleField(String fieldName, String textValue) {
		Field field = null;
		switch(textValue) {
			case "number": field = NumberField.builder().name(fieldName).build(); break;
			case "boolean": field = BooleanField.builder().name(fieldName).build(); break;
			case "date": field = DateField.builder().name(fieldName).build(); break;
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
}
