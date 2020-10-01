package net.coru.mloadgen.extractor.parser.jschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import net.coru.mloadgen.exception.MLoadGenException;
import net.coru.mloadgen.extractor.parser.SchemaParser;
import net.coru.mloadgen.model.json.ArrayField;
import net.coru.mloadgen.model.json.BooleanField;
import net.coru.mloadgen.model.json.EnumField;
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

  private static final Set<String> cyclingSet = new HashSet<>();

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
    for (Iterator<Entry<String, JsonNode>> it = definitions.fields(); it.hasNext(); ) {
      Entry<String, JsonNode> definitionNode = it.next();
      if (!isRefNode(definitionNode.getValue())) {
        definitionsMap.putIfAbsent(definitionNode.getKey(), buildDefinition(definitionNode.getKey(), definitionNode.getValue(), definitions));
      } else if (isRefNodeSupported(definitionNode.getValue())) {
        String referenceName = extractRefName(definitionNode.getValue());
        if (definitionsMap.containsKey(referenceName)) {
          definitionsMap.put(definitionNode.getKey(), buildDefinition(definitionNode.getKey(), definitionNode.getValue(), definitions));
        } else {
          if (!isRefNode(definitions.path(referenceName))) {
            if (cyclingSet.add(referenceName)) {
              definitionsMap.put(definitionNode.getKey(), buildDefinition(definitionNode.getKey(), definitions.path(referenceName), definitions));
              cyclingSet.remove(referenceName);
            } else {
              throw new MLoadGenException("Wrong Json Schema, Missing definition");
            }
          } else {
            throw new MLoadGenException("Wrong Json Schema, Missing definition");
          }
        }
      }
    }
  }

  private Field buildDefinition(String fieldName, JsonNode jsonNode, JsonNode definitions) {
    Field result;
    if (isAnyType(jsonNode)) {
      String nodeType = getSafeType(jsonNode);
      switch (nodeType) {
        case "integer":
          result = IntegerField.builder().name(fieldName).build();
          break;
        case "number":
          result = buildNumberField(fieldName, jsonNode);
          break;
        case "array":
          result = buildDefinitionArrayField(fieldName, jsonNode, definitions);
          break;
        case "object":
          result = buildDefinitionObjectField(fieldName, jsonNode, definitions);
          break;
        case "boolean":
          result = buildBooleanField(fieldName);
          break;
        default:
          result = StringField.builder().name(fieldName).build();
          break;
      }
    } else if (isRefNode(jsonNode)) {
        String referenceName = extractRefName(jsonNode);
        if (definitionsMap.containsKey(referenceName)) {
          result = definitionsMap.get(referenceName);
        } else {
          if (cyclingSet.add(referenceName)) {
            result = extractDefinition(referenceName, definitions);
            cyclingSet.remove(referenceName);
          } else {
            result = null;
          }
        }
      } else {
      if (Objects.nonNull(jsonNode.get("anyOf"))) {
        result = chooseAnyOfDefinition(fieldName, jsonNode, "anyOf", definitions);
      } else if (Objects.nonNull(jsonNode.get("allOf"))) {
        result = chooseAnyOfDefinition(fieldName, jsonNode, "allOf", definitions);
      } else {
        result = chooseAnyOfDefinition(fieldName, jsonNode, "oneOf", definitions);
      }
    }
    return result;
  }

  private String getSafeType(JsonNode jsonNode) {
    String nodeType;
    if (jsonNode.findPath("type").isArray()) {
      nodeType = getNonNUll(jsonNode.findPath("type").elements());
    } else {
      nodeType = jsonNode.findPath("type").textValue().toLowerCase();
    }
    return nodeType;
  }

  private String getNonNUll(Iterator<JsonNode> typeIt) {
    String type = null;
    while (typeIt.hasNext() && Objects.isNull(type)) {
      type = typeIt.next().asText();
      if ("null".equalsIgnoreCase(type)) {
        type = null;
      }
    }
    return type;
  }

  private Field extractDefinition(String referenceName, JsonNode definitions) {
    JsonNode field = definitions.path(referenceName);
    if (Objects.nonNull(field)) {
      Field definition = buildDefinition(referenceName, field, definitions);
      definitionsMap.put(referenceName, definition);
      return definition;
    }
    return null;
  }

  private Field chooseAnyOfDefinition(String fieldName, JsonNode jsonNode, String type, JsonNode definitions) {
    List<JsonNode> options = IteratorUtils.toList(jsonNode.get(type).elements());
    int optionsNumber = options.size();
    Field resultObject;
    switch (type) {
      case "anyOf":
      case "oneOf":
        resultObject = buildDefinition(fieldName, jsonNode.path(type).get(RandomUtils.nextInt(0, optionsNumber)), definitions);
        break;
      default:
        resultObject = buildDefinition(fieldName, jsonNode.path(type), definitions);
        break;
    }
    return resultObject;
  }

  private Field buildDefinitionArrayField(String fieldName, JsonNode jsonNode, JsonNode definitions) {
    return buildArrayField(fieldName, jsonNode, buildDefinition(null, jsonNode.path("items"), definitions));
  }

  private Field buildDefinitionObjectField(String fieldName, JsonNode jsonNode, JsonNode definitions) {
    List<Field> properties = new ArrayList<>();
    if (Objects.nonNull(jsonNode.get("properties"))) {
      CollectionUtils.collect(jsonNode.path("properties").fields(), field -> buildDefinition(field.getKey(), field.getValue(), definitions), properties);
      List<String> strRequired = jsonNode.findValuesAsText("required");
      CollectionUtils.filter(strRequired, StringUtils::isNotEmpty);
      return ObjectField.builder().name(fieldName).properties(properties).required(strRequired).build();
    } else if (Objects.nonNull(jsonNode.get("$ref"))) {
      String referenceName = extractRefName(jsonNode);
      if (definitionsMap.containsKey(referenceName)) {
        return definitionsMap.get(referenceName).cloneField(fieldName);
      } else if (cyclingSet.add(referenceName)){
        return extractDefinition(referenceName, definitions);
      } else {
        return null;
      }
    } else {
      return ObjectField.builder().build();
    }
  }

  private boolean isRefNodeSupported(JsonNode jsonNode) {
    String reference = jsonNode.get("$ref").asText();
    return !reference.isEmpty() && reference.startsWith("#");
  }

  private boolean isRefNode(JsonNode jsonNode) {
    return Objects.nonNull(jsonNode.get("$ref"));
  }

  private String extractRefName(JsonNode jsonNode) {
    String reference = jsonNode.get("$ref").asText();
    return extractRefName(reference);
  }

  private String extractRefName(String jsonNodeName) {
    return jsonNodeName.substring(jsonNodeName.lastIndexOf("/") + 1);
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
    } else if (isAnyType(jsonNode)) {
      String nodeType = getSafeType(jsonNode).toLowerCase();
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
        default:
          result = buildStringField(fieldName, jsonNode);
          break;
      }
    } else {
      if (Objects.nonNull(jsonNode.get("anyOf"))) {
        result = buildArrayField(fieldName, chooseAnyOf(fieldName, jsonNode, "anyOf"));
      } else if (Objects.nonNull(jsonNode.get("allOf"))) {
        result = buildArrayField(fieldName, chooseAnyOf(fieldName, jsonNode, "allOf"));
      } else {
        result = buildArrayField(fieldName, chooseAnyOf(fieldName, jsonNode, "oneOf"));
      }
    }
    return result;
  }

  private Field buildStringField(String fieldName, JsonNode jsonNode) {
    Field result;
    if (Objects.isNull(jsonNode.get("enum"))) {
      result = StringField.builder().name(fieldName).build();
    } else {
      result = buildEnumField(fieldName, jsonNode);
    }
    return result;
  }

  private Field buildEnumField(String fieldName, JsonNode jsonNode) {
    List<String> valueList = new ArrayList<>();
    if (jsonNode.get("enum").isArray()) {
      valueList = extractValues(jsonNode.get("enum").elements());
    }
    return EnumField.builder().name(fieldName).defaultValue(valueList.get(0)).enumValues(valueList).build();
  }

  private List<String> extractValues(Iterator<JsonNode> enumValueList) {
    List<String> valueList = new ArrayList<>();
    while (enumValueList.hasNext()) {
      valueList.add(enumValueList.next().asText());
    }
    return valueList;
  }

  private boolean isAnyType(JsonNode node) {
    return Objects.nonNull(node.get("type"));
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

  private Field buildArrayField(String fieldName, Field value) {

    return ArrayField
        .builder()
        .name(fieldName)
        .value(value)
        .minItems(1)
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
