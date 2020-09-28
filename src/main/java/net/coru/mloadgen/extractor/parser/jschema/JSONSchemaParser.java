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
import net.coru.mloadgen.model.json.BooleanField;
import net.coru.mloadgen.model.json.Field;
import net.coru.mloadgen.model.json.IntegerField;
import net.coru.mloadgen.model.json.NumberField;
import net.coru.mloadgen.model.json.ObjectField;
import net.coru.mloadgen.model.json.Schema;
import net.coru.mloadgen.model.json.StringField;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

public class JSONSchemaParser implements SchemaParser {

  private final ObjectMapper mapper = new ObjectMapper();

  private final Map<String, Field> definitionsMap = new HashMap<>();

  @Override
  public Schema parse(String jsonSchema) {
    definitionsMap.clear();
    List<Field> fields = new ArrayList<>();
    Schema schema;
    try {
      JsonNode jsonNode = mapper.readTree(jsonSchema);

      JsonNode definitions = jsonNode.path("definitions");
      processDefinitions(definitions);

      JsonNode schemaId = jsonNode.path("$id");
      JsonNode schemaName = jsonNode.path("$schema");
      JsonNode requiredList = jsonNode.path("required");
      JsonNode type = jsonNode.path("type");

      CollectionUtils.collect(jsonNode.path("properties").fieldNames(),
          fieldName -> buildProperty(fieldName, jsonNode.path("properties").get(fieldName)),
          fields);
      schema = Schema.builder()
              .id(schemaId.asText())
              .name(schemaName.asText())
              .requiredFields(requiredList.asText().split(","))
              .type(type.asText())
              .properties(fields)
              .descriptions(definitionsMap.values())
              .build();
    } catch (IOException e) {
      throw new MLoadGenException("Wrong Json Schema", e);
    }

    return schema;
  }

  private void processDefinitions(JsonNode definitions) {
    Iterator<JsonNode> refNodeIterator = definitions.findValues("$ref").listIterator();
    while(refNodeIterator.hasNext()) {
      JsonNode refNode = refNodeIterator.next();
      String referenceName = extractRefName(refNode);
      JsonNode refResolveNode = definitions.findValue(referenceName);
      if (!isRefNode(refResolveNode)) {
        definitionsMap.put(referenceName, buildProperty(referenceName, refResolveNode));
        refNodeIterator.remove();
      }
    }



    for (Iterator<Entry<String, JsonNode>> it = definitions.fields(); it.hasNext(); ) {
      Entry<String, JsonNode> definitionNode = it.next();
      if (!isRefNode(definitionNode.getValue())) {
        definitionsMap.putIfAbsent(definitionNode.getKey(), buildProperty(definitionNode.getKey(), definitionNode.getValue()));
      } else if (isRefNodeSupported(definitionNode.getValue())) {
        String referenceName = extractRefName(definitionNode.getValue());
        if (definitionsMap.containsKey(referenceName)) {
          definitionsMap.put(definitionNode.getKey(), buildProperty(definitionNode.getKey(), definitionNode.getValue()));
        } else {
          if (!isRefNode(definitions.path(referenceName))) {
            definitionsMap.put(definitionNode.getKey(), buildProperty(definitionNode.getKey(), definitions.path(referenceName)));
          } else {
            throw new MLoadGenException("Wrong Json Schema, Missing definition");
          }
        }
      }
    }
  }

  private boolean isRefNodeSupported(JsonNode jsonNode) {
    String reference = jsonNode.findValue("$ref").asText();
    return !reference.isEmpty() && reference.startsWith("#");
  }

  private boolean isRefNode(JsonNode jsonNode) {
    return Objects.nonNull(jsonNode.findValue("$ref"));
  }

  private String extractRefName(JsonNode jsonNode) {
    String reference = jsonNode.findValue("$ref").asText();
    return reference.substring(reference.lastIndexOf("/") + 1);
  }

  private Field buildProperty(String fieldName, JsonNode jsonNode) {
    Field result;
    if (isRefNode(jsonNode)) {
      if (isRefNodeSupported(jsonNode)) {
        String referenceName = extractRefName(jsonNode);
        if ("array".equalsIgnoreCase(jsonNode.findPath("type").textValue())) {
          result = buildArrayField(fieldName, jsonNode, definitionsMap.get(referenceName).cloneField(null));
        } else {
          result = definitionsMap.get(referenceName).cloneField(fieldName);
        }
      } else {
        throw new MLoadGenException(String.format("Reference not Supported: %s", extractRefName(jsonNode)));
      }
    } else {
      String nodeType = jsonNode.findPath("type").textValue().toLowerCase();
      switch (nodeType) {
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
        case "boolean":
          result = buildBooleanField(fieldName);
          break;
        case "anyOf":
        case "allOf":
        case "oneOf":
          result = chooseAnyOf(fieldName, jsonNode, nodeType);
          break;
        default:
          result = StringField.builder().name(fieldName).build();
          break;
      }
    }
    return result;
  }

  private Field chooseAnyOf(String fieldName, JsonNode jsonNode, String type) {
    List<String> options = IteratorUtils.toList(jsonNode.get(type).fieldNames());
    int optionsNumber = options.size();
    Field resultObject;
    switch (type) {
      case "anyOf":
      case "oneOf":
        resultObject = buildObjectField(fieldName, jsonNode.path(type).get(RandomUtils.nextInt(0, optionsNumber)));
      break;
      default:
        resultObject = buildObjectField(fieldName, jsonNode.path(type));
        break;
    }
    return resultObject;
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
    return buildArrayField(fieldName, jsonNode, buildProperty(null, jsonNode.path("items")));
  }

  private Field buildArrayField(String fieldName, JsonNode jsonNode, Field value) {
    String minItems = jsonNode.path("minItems").asText("0");
    String uniqueItems = jsonNode.path("uniqueItems").asText("false");
    return ArrayField
        .builder()
        .name(fieldName)
        .value(value)
        .minItems(Integer.parseInt(minItems))
        .uniqueItems(Boolean.parseBoolean(uniqueItems))
        .build();
  }

  private Field buildObjectField(String fieldName, JsonNode jsonNode) {
    List<Field> properties = new ArrayList<>();
    CollectionUtils.collect(jsonNode.path("properties").fields(), field -> buildProperty(field.getKey(), field.getValue()), properties);
    List<String> strRequired = jsonNode.findValuesAsText("required");
    CollectionUtils.filter(strRequired, StringUtils::isNotEmpty);
    return ObjectField.builder().name(fieldName).properties(properties).required(strRequired).build();
  }

  private Field buildBooleanField(String fieldName) {
    return BooleanField.builder().name(fieldName).build();
  }
}
