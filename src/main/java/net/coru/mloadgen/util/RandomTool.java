/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.coru.mloadgen.util;

import com.github.curiousoddman.rgxgen.RgxGen;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.coru.mloadgen.exception.MLoadGenException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

public final class RandomTool {

  private RandomTool() {}

  protected static Object generateRandomMap(String fieldType, Integer mapSize, List<String> fieldValueList, Integer arraySize,
      Map<ConstraintTypeEnum, String> constrains) {
    Object value;
    switch (fieldType) {
      case "int-map":
        value = generateIntMap(mapSize, fieldValueList, constrains);
        break;
      case "long-map":
        value = generateLongMap(mapSize, fieldValueList, constrains);
        break;
      case "double-map":
        value = generateDoubleMap(mapSize, fieldValueList, constrains);
        break;
      case "short-map":
        value = generateShortMap(mapSize, fieldValueList, constrains);
        break;
      case "string-map":
        value = generateStringMap(mapSize, fieldValueList, constrains);
        break;
      case "uuid-map":
        value = generateUuidMap(mapSize, fieldValueList);
        break;
      case "boolean-map":
        value = generateBooleanMap(mapSize, fieldValueList);
        break;
      default:
        value = fieldType;
        break;
    }
    if (fieldType.endsWith("array")) {
      value = generateMapArray(fieldType, mapSize, fieldValueList, arraySize, constrains);
    }
    return value;
  }

  protected static Object generateMapArray(String type, Integer valueLength, List<String> fieldValueList, Integer arraySize,
      Map<ConstraintTypeEnum, String> constrains) {
    List<Map<String, Object>> generatedMapArray = new ArrayList<>(valueLength);
    for (int i = 0; i < arraySize; i++) {
      generatedMapArray.add((Map<String, Object>)generateRandomMap(type.substring(0, type.length() - 6), valueLength, fieldValueList, arraySize,
          constrains));
    }
    return generatedMapArray;
  }

  protected static Object generateArray(String fieldType, Integer valueLength, List<String> fieldValueList, Integer arraySize,
      Map<ConstraintTypeEnum, String> constrains) {
    Object value;
    switch (fieldType) {
      case "int-array":
        value = generateIntArray(arraySize, valueLength, fieldValueList, constrains);
        break;
      case "number-array":
        value = generateNumberArray(arraySize, valueLength, fieldValueList, constrains);
        break;
      case "long-array":
        value = generateLongArray(arraySize, valueLength, fieldValueList, constrains);
        break;
      case "double-array":
        value = generateDoubleArray(arraySize, valueLength, fieldValueList, constrains);
        break;
      case "short-array":
        value = generateShortArray(arraySize, valueLength, fieldValueList, constrains);
        break;
      case "string-array":
        value = generateStringArray(arraySize, valueLength, fieldValueList, constrains);
        break;
      case "uuid-array":
        value = generateUuidArray(arraySize, fieldValueList);
        break;
      case "boolean-array":
        value = generateBooleanArray(arraySize, fieldValueList);
        break;
      default:
        value = new ArrayList<>();
        break;
    }
    return value;
  }

  protected static Object generateRandom(String fieldType, Integer valueLength, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    Object value;
    switch (fieldType) {
      case "string":
        value = getStringValueOrRandom(valueLength, fieldValueList, constrains);
        break;
      case "number":
        value = getNumberValueOrRandom(valueLength, fieldValueList, constrains);
        break;
      case "int":
        value = getIntValueOrRandom(valueLength, fieldValueList, constrains);
        break;
      case "long":
        value = getLongValueOrRandom(valueLength, fieldValueList, constrains);
        break;
      case "short":
        value = getShortValueOrRandom(valueLength, fieldValueList, constrains);
        break;
      case "double":
        value = getDoubleValueOrRandom(valueLength, fieldValueList, constrains);
        break;
      case "bytes":
		    value = getByteRandom(valueLength);
		break;
      case "timestamp":
      case "longTimestamp":
      case "stringTimestamp":
        value = getTimestampValueOrRandom(fieldType, fieldValueList);
        break;
      case "uuid":
        value = getUUIDValueOrRandom(fieldValueList);
        break;
      case "boolean":
        value = getBooleanValueOrRandom(fieldValueList);
        break;
      case "enum":
        value = getEnumValueOrRandom(fieldValueList);
        break;
      default:
        value = fieldType;
        break;
    }
    return value;
  }

  private static String getEnumValueOrRandom(List<String> fieldValueList) {
    String value;
    if (!fieldValueList.isEmpty()) {
      value = fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim();
    } else {
      throw new MLoadGenException("Wrong enums values, problem in the parsing process");
    }
    return value;
  }

  protected static Object castValue(Object value, String type) {
    Object castValue;
    switch(type) {
      case "int":
        castValue = Integer.valueOf(value.toString());
        break;
      case "double":
        castValue = Double.valueOf(value.toString());
        break;
      case "long":
        castValue = Long.valueOf(value.toString());
        break;
      case "boolean":
        castValue = Boolean.valueOf(value.toString());
        break;
      default:
        castValue = value.toString();
        break;
    }
    return castValue;
  }

  private static List<Integer> generateIntArray(Integer arraySize, Integer valueLength, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    int size = arraySize == 0 ? RandomUtils.nextInt(1,5) : arraySize;
    List<Integer> intArray = new ArrayList<>();
    for (int i=0; i<size; i++) {
      intArray.add(getIntValueOrRandom(valueLength, fieldValueList, constrains));
    }
    return intArray;
  }

  private static List<Number> generateNumberArray(Integer arraySize, Integer valueLength, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    int size = arraySize == 0 ? RandomUtils.nextInt(1,5) : arraySize;
    List<Number> intArray = new ArrayList<>();
    for (int i=0; i<size; i++) {
      intArray.add(getIntValueOrRandom(valueLength, fieldValueList, constrains));
    }
    return intArray;
  }

  private static List<Long> generateLongArray(Integer arraySize, Integer valueLength, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    int size = arraySize == 0 ? RandomUtils.nextInt(1,5) : arraySize;
    List<Long> longArray = new ArrayList<>();
    for (int i=0; i<size; i++) {
      longArray.add(getLongValueOrRandom(valueLength, fieldValueList, constrains));
    }
    return longArray;
  }

  private static List<Double> generateDoubleArray(Integer arraySize, Integer valueLength, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    int size = arraySize == 0 ? RandomUtils.nextInt(1,5) : arraySize;
    List<Double> doubleArray = new ArrayList<>();
    for (int i=0; i<size; i++) {
      doubleArray.add(getDoubleValueOrRandom(valueLength, fieldValueList, constrains));
    }
    return doubleArray;
  }

  private static List<Short> generateShortArray(Integer arraySize, Integer valueLength, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    int size = arraySize == 0 ? RandomUtils.nextInt(1,5) : arraySize;
    List<Short> shortArray = new ArrayList<>();
    for (int i=0; i<size; i++) {
      shortArray.add(getShortValueOrRandom(valueLength, fieldValueList, constrains));
    }
    return shortArray;
  }

  private static List<String> generateStringArray(Integer arraySize, Integer valueLength, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    int size = arraySize == 0 ? RandomUtils.nextInt(1,5) : arraySize;
    List<String> stringArray = new ArrayList<>();
    for (int i=0; i<size; i++) {
      stringArray.add(getStringValueOrRandom(valueLength, fieldValueList, constrains));
    }
    return stringArray;
  }

  private static List<UUID> generateUuidArray(Integer arraySize, List<String> fieldValueList) {
    int size = arraySize == 0 ? RandomUtils.nextInt(1,5) : arraySize;
    List<UUID> uuidArray = new ArrayList<>();
    for (int i=0; i<size; i++) {
      uuidArray.add(getUUIDValueOrRandom(fieldValueList));
    }
    return uuidArray;
  }

  private static List<Boolean> generateBooleanArray(Integer arraySize, List<String> fieldValueList) {
    int size = arraySize == 0 ? RandomUtils.nextInt(1,5) : arraySize;
    List<Boolean> booleanArray = new ArrayList<>();
    for (int i=0; i<size; i++) {
      booleanArray.add(getBooleanValueOrRandom(fieldValueList));
    }
    return booleanArray;
  }

  private static Map<String, Integer> generateIntMap(Integer mapSize, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    int size = mapSize>0?mapSize:RandomUtils.nextInt(1,5);
    Map<String, Integer> intMap = new HashMap<>();
    while (intMap.size() < size) {
      Map.Entry<String, Integer> mapValue;
      if (!fieldValueList.isEmpty()) {
        String[] tempValue = getMapEntryValue(fieldValueList);
        if (tempValue.length > 1) {
          mapValue = new SimpleEntry<>(tempValue[0], Integer.parseInt(tempValue[1]));
        } else {
          mapValue = new SimpleEntry<>(tempValue[0], getIntValueOrRandom(0, Collections.emptyList(), constrains));
        }
      } else {
        mapValue = new SimpleEntry<>(getStringValueOrRandom(0, Collections.emptyList(), constrains),
            getIntValueOrRandom(0, Collections.emptyList(), constrains));
      }
      intMap.put(mapValue.getKey(), mapValue.getValue());
    }
    return intMap;
  }

  private static Map<String, Long> generateLongMap(Integer mapSize, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    int size = mapSize>0?mapSize:RandomUtils.nextInt(1,5);
    Map<String, Long> longMap = new HashMap<>();
    while (longMap.size() < size) {
      Map.Entry<String, Long> mapValue;
      if (!fieldValueList.isEmpty()) {
        String[] tempValue = getMapEntryValue(fieldValueList);
        if (tempValue.length > 1) {
          mapValue = new SimpleEntry<>(tempValue[0], Long.parseLong(tempValue[1]));
        } else {
          mapValue = new SimpleEntry<>(tempValue[0], getLongValueOrRandom(0, Collections.emptyList(), constrains));
        }
      } else {
        mapValue = new SimpleEntry<>(getStringValueOrRandom(0, Collections.emptyList(), constrains),
            getLongValueOrRandom(0, Collections.emptyList(), constrains));
      }
      longMap.put(mapValue.getKey(), mapValue.getValue());
    }
    return longMap;
  }

  private static Map<String, Double> generateDoubleMap(Integer mapSize, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    int size = mapSize>0?mapSize:RandomUtils.nextInt(1,5);
    Map<String, Double> doubleMap = new HashMap<>();
    while (doubleMap.size() < size) {
      Map.Entry<String, Double> mapValue;
      if (!fieldValueList.isEmpty()) {
        String[] tempValue = getMapEntryValue(fieldValueList);
        if (tempValue.length > 1) {
          mapValue = new SimpleEntry<>(tempValue[0], Double.parseDouble(tempValue[1]));
        } else {
          mapValue = new SimpleEntry<>(tempValue[0], getDoubleValueOrRandom(0, Collections.emptyList(), constrains));
        }
      } else {
        mapValue = new SimpleEntry<>(getStringValueOrRandom(0, Collections.emptyList(), constrains),
            getDoubleValueOrRandom(0, Collections.emptyList(), constrains));
      }
      doubleMap.put(mapValue.getKey(), mapValue.getValue());
    }
    return doubleMap;
  }

  private static Map<String, Short> generateShortMap(Integer mapSize, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    int size = mapSize>0?mapSize:RandomUtils.nextInt(1,5);
    Map<String, Short> shortMap = new HashMap<>();
    while (shortMap.size() < size) {
      Map.Entry<String, Short> mapValue;
      if (!fieldValueList.isEmpty()) {
        String[] tempValue = getMapEntryValue(fieldValueList);
        if (tempValue.length > 1) {
          mapValue = new SimpleEntry<>(tempValue[0], Short.parseShort(tempValue[1]));
        } else {
          mapValue = new SimpleEntry<>(tempValue[0], getShortValueOrRandom(0, Collections.emptyList(), constrains));
        }
      } else {
        mapValue = new SimpleEntry<>(getStringValueOrRandom(0, Collections.emptyList(), constrains),
            getShortValueOrRandom(0, Collections.emptyList(), constrains));
      }
      shortMap.put(mapValue.getKey(), mapValue.getValue());
    }
    return shortMap;
  }

  private static Map<String, String> generateStringMap(Integer mapSize, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    int size = mapSize > 0 ? mapSize : RandomUtils.nextInt(1,5);
    Map<String, String> stringMap = new HashMap<>();
    while (stringMap.size() < size) {
      Map.Entry<String, String> mapValue;
      if (!fieldValueList.isEmpty()) {
        String[] tempValue = getMapEntryValue(fieldValueList);
        if (tempValue.length > 1) {
          mapValue = new SimpleEntry<>(tempValue[0], tempValue[1]);
        } else {
          mapValue = new SimpleEntry<>(tempValue[0], getStringValueOrRandom(0, Collections.emptyList(), constrains));
        }
      } else {
        mapValue = new SimpleEntry<>(getStringValueOrRandom(0, Collections.emptyList(), constrains),
            getStringValueOrRandom(0, Collections.emptyList(), constrains));
      }
      stringMap.put(mapValue.getKey(), mapValue.getValue());
    }
    return stringMap;
  }

  private static Map<String, UUID> generateUuidMap(Integer mapSize, List<String> fieldValueList) {
    int size = mapSize > 0 ? mapSize : RandomUtils.nextInt(1,5);
    Map<String, UUID> uuidMap = new HashMap<>();
    while (uuidMap.size() < size) {
      Map.Entry<String, UUID> mapValue;
      if (!fieldValueList.isEmpty()) {
        String[] tempValue = getMapEntryValue(fieldValueList);
        if (tempValue.length > 1) {
          mapValue = new SimpleEntry<>(tempValue[0], UUID.fromString(tempValue[1]));
        } else {
          mapValue = new SimpleEntry<>(tempValue[0], getUUIDValueOrRandom(Collections.emptyList()));
        }
      } else {
        mapValue = new SimpleEntry<>(getStringValueOrRandom(0, Collections.emptyList(), Collections.emptyMap()),
            getUUIDValueOrRandom(Collections.emptyList()));
      }
      uuidMap.put(mapValue.getKey(), mapValue.getValue());
    }
    return uuidMap;
  }

  private static Map<String, Boolean> generateBooleanMap(Integer mapSize, List<String> fieldValueList) {
    int size = mapSize>0?mapSize:RandomUtils.nextInt(1,5);
    Map<String, Boolean> booleanMap = new HashMap<>();
    for (int i=0; i<size; i++) {
      Map.Entry<String, Boolean> mapValue;
      if (!fieldValueList.isEmpty()) {
        String[] tempValue = getMapEntryValue(fieldValueList);
        if (tempValue.length > 1) {
          mapValue = new SimpleEntry<>(tempValue[0], Boolean.parseBoolean(tempValue[1]));
        } else {
          mapValue = new SimpleEntry<>(tempValue[0], getBooleanValueOrRandom(Collections.emptyList()));
        }
      } else {
        mapValue = new SimpleEntry<>(getStringValueOrRandom(0, Collections.emptyList(), Collections.emptyMap()),
            getBooleanValueOrRandom(Collections.emptyList()));
      }
      booleanMap.put(mapValue.getKey(), mapValue.getValue());
    }
    return booleanMap;
  }

  private static String[] getMapEntryValue(List<String> fieldValueList) {
    return fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim().split(":");
  }

  private static Integer getIntValueOrRandom(Integer valueLength, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    int value;
    if (!fieldValueList.isEmpty()) {
      value = Integer.parseInt(fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim());
    } else {
      int minimum = calculateMinimum(1, constrains);
      int maximum = calculateMaximum(valueLength, constrains);
      if (constrains.containsKey(ConstraintTypeEnum.MULTIPLE_OF)) {
        int multipleOf = Integer.parseInt(constrains.get(ConstraintTypeEnum.MULTIPLE_OF));
        maximum = maximum > multipleOf ? maximum / multipleOf : maximum;
        value = RandomUtils.nextInt(minimum, maximum) * multipleOf;
      } else {
        value = RandomUtils.nextInt(minimum, maximum);
      }
    }
    return value;
  }

  private static Number getNumberValueOrRandom(Integer valueLength, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    Number value;
    if (!fieldValueList.isEmpty()) {
      String chosenValue = fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim();
      if (chosenValue.contains(".")) {
        value = Float.parseFloat(chosenValue);
      } else {
        value = Integer.parseInt(chosenValue);
      }
    } else {
      int minimum = calculateMinimum(1, constrains);
      int maximum = calculateMaximum(valueLength, constrains);
      if (constrains.containsKey(ConstraintTypeEnum.MULTIPLE_OF)) {
        int multipleOf = Integer.parseInt(constrains.get(ConstraintTypeEnum.MULTIPLE_OF));
        maximum = maximum > multipleOf ? maximum / multipleOf : maximum;
        value = RandomUtils.nextFloat(minimum, maximum) * multipleOf;
      } else {
        value = RandomUtils.nextFloat(minimum, maximum);
      }
    }
    return value;
  }

  private static Long getLongValueOrRandom(Integer valueLength, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    long value;
    if (!fieldValueList.isEmpty()) {
      value = Long.parseLong(fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim());
    } else {
      int minimum = calculateMinimum(1, constrains);
      int maximum = calculateMaximum(valueLength, constrains);
      if (constrains.containsKey(ConstraintTypeEnum.MULTIPLE_OF)) {
        int multipleOf = Integer.parseInt(constrains.get(ConstraintTypeEnum.MULTIPLE_OF));
        maximum = maximum > multipleOf ? maximum / multipleOf : maximum;
        value = RandomUtils.nextLong(minimum, maximum) * multipleOf;
      } else {
        value = RandomUtils.nextLong(minimum, maximum);
      }
    }
    return value;
  }

  private static Double getDoubleValueOrRandom(Integer valueLength, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    double value;
    if (!fieldValueList.isEmpty()) {
      value = Double.parseDouble(fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim());
    } else {
      int minimum = calculateMinimum(1, constrains);
      int maximum = calculateMaximum(valueLength, constrains);
      if (constrains.containsKey(ConstraintTypeEnum.MULTIPLE_OF)) {
        int multipleOf = Integer.parseInt(constrains.get(ConstraintTypeEnum.MULTIPLE_OF));
        maximum = maximum > multipleOf ? maximum / multipleOf : maximum;
        value = RandomUtils.nextDouble(minimum, maximum) * multipleOf;
      } else {
        value = RandomUtils.nextDouble(minimum, maximum);
      }
    }
    return value;
  }

  private static int calculateMaximum(Integer valueLength, Map<ConstraintTypeEnum, String> constrains) {
    int maximum;
    if (constrains.containsKey(ConstraintTypeEnum.MAXIMUM_VALUE)) {
      if (constrains.containsKey(ConstraintTypeEnum.EXCLUDED_MAXIMUM_VALUE)) {
        maximum = Integer.parseInt(constrains.get(ConstraintTypeEnum.EXCLUDED_MAXIMUM_VALUE)) - 1;
      } else {
        maximum = Integer.parseInt(constrains.get(ConstraintTypeEnum.MAXIMUM_VALUE));
      }
    } else {
      maximum = 9 * (int) Math.pow(10, calculateSize(valueLength));
    }
    return maximum;
  }

  private static int calculateMinimum(int minimum, Map<ConstraintTypeEnum, String> constrains) {
    if (constrains.containsKey(ConstraintTypeEnum.MINIMUM_VALUE)) {
      if (constrains.containsKey(ConstraintTypeEnum.EXCLUDED_MINIMUM_VALUE)) {
        minimum = Integer.parseInt(constrains.get(ConstraintTypeEnum.EXCLUDED_MINIMUM_VALUE)) - 1;
      } else {
        minimum = Integer.parseInt(constrains.get(ConstraintTypeEnum.MINIMUM_VALUE));
      }
    }
    return minimum;
  }

  private static ByteBuffer getByteRandom(Integer valueLength) {
    ByteBuffer value;
    if (valueLength == 0) {
      value =  ByteBuffer.wrap(RandomUtils.nextBytes(4));
    } else {
      value =  ByteBuffer.wrap(RandomUtils.nextBytes(valueLength));
    }
    return value;
  }

  private static String getStringValueOrRandom(Integer valueLength, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    String value;
    if (!fieldValueList.isEmpty()) {
      value = fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim();
    } else {
      if (constrains.containsKey(ConstraintTypeEnum.REGEX)) {
        RgxGen rxGenerator = new RgxGen(constrains.get(ConstraintTypeEnum.REGEX));
        value = rxGenerator.generate();
        if (valueLength > 0 || constrains.containsKey(ConstraintTypeEnum.MAXIMUM_VALUE)) {
          value = value.substring(0, getMaxLength(valueLength, constrains.get(ConstraintTypeEnum.MAXIMUM_VALUE)));
        }
      } else {
        value = RandomStringUtils.randomAlphabetic(valueLength == 0 ? RandomUtils.nextInt(1, 20) : valueLength);
      }
    }
    return value;
  }

  private static int getMaxLength(Integer valueLength, String maxValueStr) {
    int maxValue = Integer.parseInt(StringUtils.defaultIfEmpty(maxValueStr, "0"));
    if (valueLength > 0 && maxValue == 0 ) {
      maxValue = valueLength;
    }
    return maxValue;
  }

  private static Short getShortValueOrRandom(Integer valueLength, List<String> fieldValueList,
      Map<ConstraintTypeEnum, String> constrains) {
    short value;
    if (!fieldValueList.isEmpty()) {
      value = Short.parseShort(fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim());
    } else {
      if (valueLength < 5 ) {
        int minimum = calculateMinimum(1, constrains);
        int maximum = calculateMaximum(valueLength, constrains);
        if (constrains.containsKey(ConstraintTypeEnum.MULTIPLE_OF)) {
          int multipleOf = Integer.parseInt(constrains.get(ConstraintTypeEnum.MULTIPLE_OF));
          maximum = maximum > multipleOf ? maximum / multipleOf : maximum;
          value = (short) (RandomUtils.nextInt(minimum, maximum) * multipleOf);
        } else {
          value = (short) RandomUtils.nextInt(minimum, maximum);
        }
      } else {
        value = (short) RandomUtils.nextInt(1, 32767);
      }
    }
    return value;
  }

  private static int calculateSize(int valueLength) {
    return valueLength > 0 ? valueLength -1 : 0;
  }

  private static Object getTimestampValueOrRandom(String type, List<String> fieldValueList) {
    LocalDateTime value;
    if (!fieldValueList.isEmpty()) {
      value = LocalDateTime.parse(fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim());
    } else {
      value = LocalDateTime.now();
    }
    if ("longTimestamp".equalsIgnoreCase(type)) {
      return value.toInstant(ZoneOffset.UTC).toEpochMilli();
    } else if ("stringTimestamp".equalsIgnoreCase(type)) {
      return value.toString();
    }
    return value;
  }

  private static UUID getUUIDValueOrRandom(List<String> fieldValueList) {
    UUID value = UUID.randomUUID();
    if (!fieldValueList.isEmpty()) {
      value = UUID.fromString(fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim());
    }
    return value;
  }

  private static Boolean getBooleanValueOrRandom(List<String> fieldValueList) {
    boolean value = RandomUtils.nextBoolean();
    if (!fieldValueList.isEmpty()) {
      value = Boolean.parseBoolean(fieldValueList.get(RandomUtils.nextInt(0, fieldValueList.size())).trim());
    }
    return value;
  }

  public static Object generateSeq(String fieldName, String fieldType, List<String> fieldValueList, Map<String, Object> context) {

    return RandomTool.castValue(
        context.compute(fieldName, (fieldNameMap,
            seqObject) -> seqObject == null ? getSafeValue(fieldValueList) : ((Long) seqObject) + 1),
        fieldType);
  }

  private static Long getSafeValue(List<String> fieldValueList) {
    return fieldValueList.isEmpty() ? 1L : Long.parseLong(fieldValueList.get(0));
  }
}
