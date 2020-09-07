package net.coru.mloadgen.extractor.parser.jschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import net.coru.mloadgen.exception.MLoadGenException;
import net.coru.mloadgen.extractor.parser.SchemaParser;
import net.coru.mloadgen.model.json.ArrayField;
import net.coru.mloadgen.model.json.Field;
import net.coru.mloadgen.model.json.IntegerField;
import net.coru.mloadgen.model.json.NumberField;
import net.coru.mloadgen.model.json.ObjectField;
import net.coru.mloadgen.model.json.Schema;
import net.coru.mloadgen.model.json.StringField;
import org.apache.commons.collections4.CollectionUtils;

public class JSONSchemaParser implements SchemaParser {

  private final ObjectMapper mapper = new ObjectMapper();

  private Map<String, Field> definitionsMap = new HashMap<>();

  @Override
  public Schema parse(String jsonSchema) {
    List<Field> fields = new ArrayList<>();

    try {
      JsonNode jsonNode = mapper.readTree(jsonSchema);

      JsonNode definitions = jsonNode.path("definitions");
      definitionsMap.putAll(processDefinitions(definitions));

      JsonNode schemaId = jsonNode.path("$id");
      JsonNode schema = jsonNode.path("$schema");
      JsonNode requiredList = jsonNode.path("required");
      JsonNode type = jsonNode.path("type");

      CollectionUtils.collect(jsonNode.fieldNames(),
          fieldName -> buildProperty(fieldName, jsonNode.get(fieldName)),
          fields);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return Schema.builder().properties(fields).build();
  }

  private Map<String, Field> processDefinitions(JsonNode definitions) {
    Map<String, Field> definitionsMap = new HashMap<>();
    for (Iterator<Entry<String, JsonNode>> it = definitions.fields(); it.hasNext(); ) {
      Entry<String, JsonNode> definitionNode = it.next();
      definitionsMap.put(definitionNode.getKey(), buildProperty(definitionNode.getKey(), definitionNode.getValue()));
    }
    return definitionsMap;
  }

  private Field buildProperty(String fieldName, JsonNode jsonNode) {
    Field result;
    if (Objects.nonNull(jsonNode.findValue("$ref"))) {
      String reference = jsonNode.findValue("$ref").asText();
      if (!reference.isEmpty() && reference.startsWith("#")) {
        result = definitionsMap.get(reference.substring(reference.lastIndexOf("/"))).cloneField(fieldName);
      } else {
        throw new MLoadGenException(String.format("Reference not Supported: %s", reference));
      }
    } else {
      // TODO support for anyOf, allOf, oneOf
      switch (jsonNode.findPath("type").textValue().toLowerCase()) {
        case "integer":
          result = IntegerField.builder().name(fieldName).build();
          break;
        case "number":
          result = buildNumberField(fieldName, jsonNode);
          break;
        case "array":
          result = buildArrayField(fieldName, jsonNode);
          break;
        case "object":
          result = buildObjectField(fieldName, jsonNode);
          break;
        default:
          result = StringField.builder().name(fieldName).build();
          break;
      }
    }
    return result;
  }

  private Field buildNumberField(String fieldName, JsonNode jsonNode) {
    String maximum = jsonNode.path("maximum").asText("0");
    String minimum = jsonNode.path("minimum").asText("0");
    String exclusiveMaximum = jsonNode.path("exclusiveMaximum").asText("0");
    String exclusiveMinimum = jsonNode.path("exclusiveMinimum").asText("0");
    String multipleOf = jsonNode.path("multipleOf").asText("0");

    return NumberField
        .builder()
        .name(fieldName)
        .maximum(safeGetNumber(maximum))
        .minimum(safeGetNumber(minimum))
        .exclusiveMaximum(safeGetNumber(exclusiveMaximum))
        .exclusiveMinimum(safeGetNumber(exclusiveMinimum))
        .multipleOf(safeGetNumber(multipleOf))
        .build();
  }

  private Number safeGetNumber(String numberStr) {
    Number number;
    if (numberStr.contains(".")) {
      number = Float.parseFloat(numberStr);
    } else {
      number = Long.parseLong(numberStr);
    }
    return number;
  }

  private Field buildArrayField(String fieldName, JsonNode jsonNode) {
    String minItems = jsonNode.path("minItems").asText("0");
    String uniqueItems = jsonNode.path("uniqueItems").asText("false");
    return ArrayField
        .builder()
        .name(fieldName)
        .value(buildProperty("", jsonNode))
        .minItems(Integer.parseInt(minItems))
        .uniqueItems(Boolean.parseBoolean(uniqueItems))
        .build();
  }

  private Field buildObjectField(String fieldName, JsonNode jsonNode) {
    List<Field> properties = new ArrayList<>();
    CollectionUtils.collect(jsonNode.path("properties").fields(), field -> buildProperty(field.getKey(), field.getValue()), properties);
    List<String> strRequired = jsonNode.findValuesAsText("required");
    return ObjectField.builder().name(fieldName).properties(properties).required(strRequired).build();
  }

}
