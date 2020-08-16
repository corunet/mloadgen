/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package net.coru.mloadgen.util;

import org.apache.jmeter.threads.JMeterContextService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatelessRandomTool {

  private final Map<String, Object> context = new HashMap<>();

  public Object generateRandom(String fieldName, String fieldType, Integer valueLength, List<String> fieldValuesList) {
    List<String> parameterList = new ArrayList<>(fieldValuesList);
    parameterList.replaceAll(fieldValue ->
        fieldValue.matches("\\$\\{\\w*}") ?
            JMeterContextService.getContext().getVariables().get(fieldValue.substring(2, fieldValue.length() - 1)) :
            fieldValue
    );

    Object value = net.coru.kloadgen.util.RandomTool.generateRandom(fieldType, valueLength, parameterList);
    if ("seq".equals(fieldType)) {
      value = net.coru.kloadgen.util.RandomTool.generateSeq(fieldName, fieldType, parameterList, context);
    }

    return value;
  }
}